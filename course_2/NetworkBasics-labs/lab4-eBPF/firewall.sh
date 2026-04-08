#!/usr/bin/env bash

# !!скрипт написан чатом гпт с небольшими ручными правками!!


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
    IFS='.' read -r o1 o2 o3 o4 <<< "$ip"
    printf "%02x %02x %02x %02x" "$o1" "$o2" "$o3" "$o4"
}

# Преобразование шестнадцатеричного вывода bpftool в читаемый IP


# Получить ID карты по имени (через bpftool)
get_map_id() {
    id_with_column=$(sudo bpftool map list | grep  "name blacklist" | grep -oP '\d+:')
    echo "${id_with_column::-1}"
}

# Проверить, загружена ли XDP-программа на интерфейс

# ========== ОСНОВНЫЕ КОМАНДЫ ==========
block_ip() {
    local ip="$1"
    local hex_key; hex_key=$(ip_to_hex "$ip")
    local map_id;  map_id=$(get_map_id)
   
    if [[ -z "$map_id" ]]; then
        echo -e "${RED}Ошибка: карта $MAP_NAME не найдена. Убедитесь, что XDP загружен.${NC}"
        return 1
    fi
    if sudo bpftool map update id "$map_id" key hex "$hex_key" value hex 00 any; then
        echo -e "${GREEN}$ip (0x${hex_key// /}) was added in blacklist.${NC}"
    else
        echo -e "${RED}Cannot add $ip. Maybe, it is already in blacklist?.${NC}"
    fi
}

unblock_ip() {
    local ip="$1"
    local hex_key; hex_key=$(ip_to_hex "$ip")
    local map_id;  map_id=$(get_map_id)
    if [[ -z "$map_id" ]]; then
        echo -e "${RED}ERROR: map $MAP_NAME is not found.${NC}"
        return 1
    fi
    if sudo bpftool map delete id "$map_id" key hex "$hex_key"; then
        echo -e "${GREEN}$ip was deleted from blacklist.${NC}"
    else
        echo -e "${YELLOW}Cannot delete $ip (may be, it is not in the blacklist).${NC}"
    fi
}

decimal_to_ip_with_dots() {
    num=$1
    # преобразуем десятичное число в IP (host order, little-endian)
    local oct1=$((num & 0xFF))
    local oct2=$(((num >> 8) & 0xFF))
    local oct3=$(((num >> 16) & 0xFF))
    local oct4=$(((num >> 24) & 0xFF))
    echo "$oct1.$oct2.$oct3.$oct4"
}

list_blacklist() {
    local map_id; map_id=$(get_map_id)
    echo "map_id = $map_id"
    if [[ -z "$map_id" ]]; then
        echo -e "${RED}Map $MAP_NAME was not found.${NC}"
        return 1
    fi
    local dump; dump=$(sudo bpftool map dump id "$map_id")
    if [[ -z "$dump" ]] || [[ "$dump" == "[]" ]]; then
        echo -e "${YELLOW}Blacklist is empty.${NC}"
        return
    fi
    echo -e "${GREEN}Blacklist:${NC}"
    
    echo "$dump" | grep -oP '\"key\": \d+' | awk -F ': ' '{print $2}' | while read -r num; do
        echo "   $(decimal_to_ip_with_dots "$num")"
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
        echo -e "${RED}ERROR: Interface $INTERFACE does not exist.${NC}"
        exit 1
    fi
    
    echo "Load XDP on $INTERFACE ..."
    sudo ip link set dev "$INTERFACE" xdp off
    sudo ip link set dev "$INTERFACE" xdp obj "$BPF_OBJ" sec xdp
    
    if [[ $? -eq 0 ]]; then
        echo -e "${GREEN}XDP loaded on $INTERFACE.${NC}"
    else
        echo -e "${RED}ERROR: Can't load XDP on $INTERFACE."
        exit 1
    fi
}

unload_xdp() {
    echo "Unloading XDP from $INTERFACE ..."
    sudo ip link set dev "$INTERFACE" xdp off
    echo -e "${GREEN}XDP unloaded.${NC}"Ё
    exit
}

interactive_loop() {
    echo -e "${GREEN}XDP Firewall on $INTERFACE is active.${NC}"
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
    if [[ $EUID -eq 0 ]]; then
        echo -e "${RED}Do not run this script with root permissions (do not use sudo). ${NC}"
        exit 1
    fi

    if [[ $# -ne 1 ]]; then
        echo -e "${RED}Usage: $0 <interface>${NC}"
        echo "Example: $0 eth0"
        exit 1
    fi
    INTERFACE="$1"   # global var

    check_deps

    rebuild_xdp
    load_xdp

    trap unload_xdp INT TERM

    interactive_loop
    unload_xdp
}

main "$@"