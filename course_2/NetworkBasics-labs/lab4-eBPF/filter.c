#include <sys/socket.h>
#include <stdlib.h>
#include <netinet/in.h>
#include <netinet/ip.h>         // struct iphdr 
#include <stdint.h>
#include <stdio.h>
#include <sys/syscall.h>
#include <syscall.h>
#include <sys/socket.h>
#include <unistd.h>
#include <net/ethernet.h>
#include <arpa/inet.h>
#include <linux/bpf.h>
#include <linux/filter.h>       // for sock_prog
#include <linux/if_packet.h>    // for struct sockaddr_ll
#include <linux/if_ether.h>
#include <net/if.h>
#include <stdbool.h>
#include <err.h>

#define ARRAY_SIZE(arr) (sizeof(arr) / sizeof((arr)[0]))
#define BUF_SIZE 65536
bool IS_LOGGING = true;

// =================== typedefs ========================= //
typedef struct sock_filter sock_filter;
typedef struct sock_fprog sock_fprog;
typedef struct sockaddr_ll sockaddr_ll;
typedef struct sockaddr sockaddr;
typedef struct ifreq ifreq;
typedef struct ether_header ether_header;
typedef struct iphdr iphdr; // internet address (ipv4)


// ====================================================== //


// ===============vars declaration======================= //
// Программа-фильтр
// код получается с помощью `tcpdump -dd ip`
// фильтр читает пакеты и если это ipv4, то копирует 256 КБ пакета
sock_filter ip_filter_code[] = {
    { 0x28, 0, 0, 0x0000000c },
    { 0x15, 0, 1, 0x00000800 },
    { 0x6, 0, 0, 0x00040000 },
    { 0x6, 0, 0, 0x00000000 },
};


// Программа фильтр. 
// Код получен с помощью `tcpdump -dd ip and tcp`
// читаем только tcp/ip пакеты и получаем 256 КБ
sock_filter tcp_ip_filter_code[] = {
    { 0x28, 0, 0, 0x0000000c },
    { 0x15, 0, 3, 0x00000800 },
    { 0x30, 0, 0, 0x00000017 },
    { 0x15, 0, 1, 0x00000006 },
    { 0x6, 0, 0, 0x00040000 },
    { 0x6, 0, 0, 0x00000000 },
};
// ====================================================== //


void log_ethernet_packet(size_t len, ether_header *eth) 
{  
    static size_t cnt = 0;
    if (!IS_LOGGING) return;
    printf("[%lu] Packet: %ld bytes\n"
            "\tEtherType: 0x%04x\n"
            "\tSRC MAC: %02x:%02x:%02x:%02x:%02x:%02x\n"
            "\tDST MAC: %02x:%02x:%02x:%02x:%02x:%02x\n",
            cnt, len,
            ntohs(eth->ether_type),
            eth->ether_shost[0], eth->ether_shost[1], eth->ether_shost[2],
            eth->ether_shost[3], eth->ether_shost[4], eth->ether_shost[5],
            eth->ether_dhost[0], eth->ether_dhost[1], eth->ether_dhost[2],
            eth->ether_dhost[3], eth->ether_dhost[4], eth->ether_dhost[5]
    );
    cnt++;
}
void log_ip_header(iphdr *ip) 
{
    if (!IS_LOGGING) return;
    char src_ip[INET_ADDRSTRLEN];
    char dst_ip[INET_ADDRSTRLEN];
    inet_ntop(AF_INET, &ip->saddr, src_ip, INET_ADDRSTRLEN);
    inet_ntop(AF_INET, &ip->daddr, dst_ip, INET_ADDRSTRLEN);
    printf("\t\tSource ip: %s\n", src_ip);
    printf("\t\tDestination ip: %s\n", dst_ip);
}
void log_ip_protocol(iphdr *ip) 
{
    if (!IS_LOGGING) return;
    printf("\t\tIP Protocol: ");
    switch (ip->protocol) {
        case IPPROTO_TCP: printf("TCP\n"); break;
        case IPPROTO_ICMP: printf("ICMP\n"); break;
        case IPPROTO_UDP: printf("UDP\n"); break;
        default: printf("%d\n", ip->protocol); break;
    }
}

void log_packet(size_t packet_len, ether_header *eth) {
    iphdr *ip = (iphdr *) (eth + 1);
    log_ethernet_packet(packet_len, eth);
    log_ip_header(ip);
    log_ip_protocol(ip);
}

int setup_filter(sock_fprog *prog) {
    if (prog == NULL) {
        errx(EXIT_FAILURE, "Error: Program for eBPF is NULL");
    }
    // для bind(2). привязка сокета к интерфейсу eth0
    sockaddr_ll addr = {
        .sll_family = AF_PACKET,
        .sll_protocol = htons(ETH_P_ALL),
        .sll_ifindex = if_nametoindex("wlan0"),
        .sll_pkttype = PACKET_HOST  // только пакеты для этого хоста
    };
    // создаём сокет
    // AF_PACKET - работа на канальном уровне
    int socket_fd = socket(AF_PACKET, SOCK_RAW, htons(ETH_P_ALL));
    if (socket_fd == -1) {
        perror("Cannot open socket");
        exit(EXIT_FAILURE);
    }
    // связываем интерфес c сокетом
    if (bind(socket_fd, (sockaddr *)&addr, sizeof(addr)) == -1) {
        perror("Connot bind interface wlan0 to socket");
        exit(EXIT_FAILURE);
    }
    // применяем BPF фильтр
    if (setsockopt(socket_fd, SOL_SOCKET, SO_ATTACH_FILTER, prog, sizeof(*prog)) == -1) {
        perror("Cannot set bpf-filter to socket");
        exit(EXIT_FAILURE);
    }
    return socket_fd;
}


// процедура главная
int main() 
{   
    sock_fprog prog = {
        .len = ARRAY_SIZE(tcp_ip_filter_code),
        .filter = tcp_ip_filter_code
    };

    int socket_fd = setup_filter(&prog);
    char packet_buf[BUF_SIZE];
    while (1) {
        ssize_t packet_len = recv(socket_fd, packet_buf, sizeof(packet_buf), 0);
        if (packet_len == -1) {
            perror("Error while reading data from socket");
            exit(EXIT_FAILURE);
        }
        ether_header *eth = (ether_header *) packet_buf;
        iphdr *ip = (iphdr *) (eth + 1);  // ip header goes after the end of ethernet header       
        log_packet(packet_len, eth);
    }
    
    printf("done!\n");
    close(socket_fd);
    return 0;
}