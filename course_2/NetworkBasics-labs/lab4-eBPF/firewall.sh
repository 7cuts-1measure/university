#!/usr/bin/env bash

# ========== НАСТРОЙКИ ==========
BPF_OBJ="filter.o"             # объектный файл eBPF
BPF_SRC="filter.c"             # исходный код eBPF
MAP_NAME="blacklist"           # имя BPF-карты

# Цвета для вывода (опционально)
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# ========== ПРОВЕРКИ ==========
check_deps() {
    local deps=("clang" "bpftool" "ip")
    for dep in "${deps[@]}"; do
        if ! command -v "$dep" &> /dev/null; then
            echo -e "${RED}Ошибка: $dep не установлен.${NC}"
            exit 1
        fi
    done
}

# ========== ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ ==========
# Преобразование IP (a.b.c.d) в шестнадцатеричный формат для bpftool (big-endian)
ip_to_hex() {
    local ip="$1"
    local hex=""
    IFS='.' read -r o1 o2 o3 o4 <<< "$ip"
    printf "%02x %02x %02x %02x" "$o1" "$o2" "$o3" "$o4"
}

# Преобразование шестнадцатеричного вывода bpftool в читаемый IP
# На вход: строка вида "c0 a8 01 01" (без кавычек)
hex_to_ip() {
    local hex_str="$1"
    # Удаляем все пробелы, переводим в верхний регистр для единообразия
    hex_str=$(echo "$hex_str" | tr -d ' ' | tr '[:lower:]' '[:upper:]')
    # Разбиваем по два символа
    local oct1=$((16#${hex_str:0:2}))
    local oct2=$((16#${hex_str:2:2}))
    local oct3=$((16#${hex_str:4:2}))
    local oct4=$((16#${hex_str:6:2}))
    echo "$oct1.$oct2.$oct3.$oct4"
}

# Получить ID карты по имени (через bpftool)
get_map_id() {
    id_with_column=$(sudo bpftool map list | grep  "name blacklist" | grep -oP '\d+:')
    echo ${id_with_column::-1}
}

# Проверить, загружена ли XDP-программа на интерфейс
is_xdp_loaded() {
    ip link show "$INTERFACE" | grep -q "xdp"
}

# ========== ОСНОВНЫЕ КОМАНДЫ ==========
block_ip() {
    local ip="$1"
    local hex_key=$(ip_to_hex "$ip")
    local map_id=$(get_map_id)
    if [[ -z "$map_id" ]]; then
        echo -e "${RED}Ошибка: карта $MAP_NAME не найдена. Убедитесь, что XDP загружен.${NC}"
        return 1
    fi
    if sudo bpftool map update id "$map_id" key hex $hex_key value hex 00 any; then
        echo -e "${GREEN}$ip (0x${hex_key// /}) was added in blacklist.${NC}"
    else
        echo -e "${RED}Cannot add $ip. Maybe, it is already in blacklist?.${NC}"
    fi
}

unblock_ip() {
    local ip="$1"
    local hex_key=$(ip_to_hex "$ip")
    local map_id=$(get_map_id)
    if [[ -z "$map_id" ]]; then
        echo -e "${RED}ERROR: map $MAP_NAME is not found.${NC}"
        return 1
    fi
    if sudo bpftool map delete id "$map_id" key hex $hex_key; then
        echo -e "${GREEN}$ip was deleted from blacklist.${NC}"
    else
        echo -e "${YELLOW}Cannot delete $ip (may be, it is not in the blacklist).${NC}"
    fi
}

list_blacklist() {
    local map_id=$(get_map_id)
    echo "map_id = $map_id"
    if [[ -z "$map_id" ]]; then
        echo -e "${RED}Map $MAP_NAME was not found.${NC}"
        return 1
    fi
    local dump=$(sudo bpftool map dump id "$map_id")
    if [[ -z "$dump" ]] || [[ "$dump" == "[]" ]]; then
        echo -e "${YELLOW}Blacklist is empty.${NC}"
        return
    fi
    echo -e "${GREEN}Blacklist:${NC}"
    # Парсим строки вида "key: c0 a8 01 01 value: 00"
    
    echo "$dump" | grep -oP '\"key\": \d+' | awk -F ': ' '{print $2}' | while read -r num; do
        # преобразуем десятичное число в IP (host order, little-endian)
        local oct1=$((num & 0xFF))
        local oct2=$(((num >> 8) & 0xFF))
        local oct3=$(((num >> 16) & 0xFF))
        local oct4=$(((num >> 24) & 0xFF))
        echo "  $oct1.$oct2.$oct3.$oct4"
    done
}

rebuild_xdp() {
    echo "Compiling XDP program $BPF_SRC to bpf target..."
    clang -O2 -g -target bpf -c "$BPF_SRC" -o "$BPF_OBJ"
    if [[ $? -ne 0 ]]; then
        echo -e "${RED}ERROR: failed compilation.${NC}"
        exit 1
    fi
}

load_xdp() {
    if ! ip link show "$INTERFACE" &>/dev/null; then
        echo -e "${RED}ERROR: Interface "$INTERFACE" does not exist.${NC}"
        exit 1
    fi
    
    echo "Load XDP on "$INTERFACE" ..."
    # Открепляем старую программу, если была
    sudo ip link set dev "$INTERFACE" xdp off
    sudo ip link set dev "$INTERFACE" xdp obj "$BPF_OBJ" sec xdp
    
    if [[ $? -eq 0 ]]; then
        echo -e "${GREEN}XDP loaded on "$INTERFACE".${NC}"
    else
        echo -e "${RED}ERROR: Can't load XDP on "$INTERFACE"."
        exit 1
    fi
}

unload_xdp() {
    echo "Unloading XDP from "$INTERFACE" ..."
    sudo ip link set dev "$INTERFACE" xdp off
    echo -e "${GREEN}XDP unloaded.${NC}"
    exit
}

interactive_loop() {
    echo -e "${GREEN}XDP Firewall on "$INTERFACE" is active.${NC}"
    echo "Enter help for get list of commands."
    while true; do
        read -r -p "> " cmd arg
        case "$cmd" in
            block)
                if [[ -z "$arg" ]]; then
                    echo "Usage: block <IP>"
                else
                    block_ip "$arg"
                fi
                ;;
            unblock)
                if [[ -z "$arg" ]]; then
                    echo "Usage: unblock <IP>"
                else
                    unblock_ip "$arg"
                fi
                ;;
            list)
                list_blacklist
                ;;
            exit|quit)
                break
                ;;
            help)
                echo -e "${GREEN}Commands:${NC}"
                echo "  block <IP>     - add IP in blacklsit"
                echo "  unblock <IP>   - delete IP from blacklist"
                echo "  list           - show all blocked IP"
                echo "  exit или quit  - unload XDP and exit"
                echo "  help           - print this message"
                ;;
            "")
                continue
                ;;
            *)
                echo "Unknown command. Type help."
                ;;
        esac
    done
}

main() {
    # Проверка прав (скрипт должен запускаться с sudo)
    if [[ $EUID -eq 0 ]]; then
        echo -e "${RED}Do not run this script with root permissions (do not use sudo). ${NC}"
        exit 1
    fi
    # Проверяем, что передан аргумент с интерфейсом
    if [[ $# -ne 1 ]]; then
        echo -e "${RED}Usage: $0 <interface>${NC}"
        echo "Example: $0 eth0"
        exit 1
    fi
    INTERFACE="$1"   # глобальная переменная, будет доступна всем функциям

    check_deps
    # Пересобираем и загружаем XDP
    rebuild_xdp
    load_xdp

    # Устанавливаем trap для корректного выхода
    trap unload_xdp INT TERM

    # Запускаем интерактивный режим
    interactive_loop
    unload_xdp
}

main "$@"