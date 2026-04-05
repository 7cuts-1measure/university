#!/usr/bin/env python3
import sys
import socket
import subprocess

def block_ip(ip_address):
    # 1. Конвертируем IP-адрес в 4-байтовую строку в сетевом порядке (big-endian)
    ip_bytes = socket.inet_aton(ip_address)
    # 2. Преобразуем байты в формат, который ожидает bpftool (шестнадцатеричные числа)
    hex_key = ' '.join(f'{b:02x}' for b in ip_bytes)
    print(hex_key)
    # 3. Формируем и выполняем команду bpftool
    cmd = ['sudo', 'bpftool', 'map', 'update', 'name', 'blacklist',
           'key', 'hex'] + hex_key.split() + ['value', 'hex', '00', 'any']
    subprocess.run(cmd)
    print(f"IP {ip_address} добавлен в чёрный список.")

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print(f"Использование: {sys.argv[0]} <IP-ADDRESS>")
        sys.exit(1)
    block_ip(sys.argv[1])
