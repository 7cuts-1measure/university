#!/usr/bin/env bash

# disable already attached xdp-program
sudo ip link set dev wlan0 xdp off

#build
clang -O2 -g -target bpf -c filter.c -o filter.o

# set interface
sudo ip link set dev wlan0 xdp obj filter.o sec xdp

./block_ip.py $1


