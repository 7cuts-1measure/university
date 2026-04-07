#include <stdbool.h>
#include <linux/if_ether.h>
#include <linux/in.h>
#include <linux/ip.h>
#include <linux/tcp.h>
#include <linux/bpf.h>
#include <bpf/bpf_helpers.h>
#include <bpf/bpf_endian.h>

#define MAX_PAYLOAD_COPY_TO_STACK 511

// Helper function to check if the packet is TCP
static bool is_tcp(struct ethhdr *eth, void *data_end) {
    // Ensure Ethernet header is within bounds
    if ((void *)(eth + 1) > data_end)
        return false;

    // Only handle IPv4 packets
    if (bpf_ntohs(eth->h_proto) != ETH_P_IP)
        return false;

    struct iphdr *ip = (struct iphdr *)(eth + 1);

    // Ensure IP header is within bounds
    if ((void *)(ip + 1) > data_end)
        return false;

    // Check if the protocol is TCP
    if (ip->protocol != IPPROTO_TCP)
        return false;

    return true;
}

SEC("xdp")
int xdp_pass(struct xdp_md *ctx) {
    // Pointers to packet data
    void *data = (void *)(long)ctx->data;
    void *data_end = (void *)(long)ctx->data_end;

    // Parse Ethernet header
    struct ethhdr *eth = data;

    // Check if the packet is a TCP packet
    if (!is_tcp(eth, data_end))                             return XDP_PASS;

    // Cast to IP header
    struct iphdr *ip = (struct iphdr *)(eth + 1);

    // Calculate IP header length
    int ip_hdr_len = ip->ihl * 4;
    if (ip_hdr_len < sizeof(struct iphdr))                  return XDP_PASS;
    

    // Ensure IP header is within packet bounds
    if ((void *)ip + ip_hdr_len > data_end)                 return XDP_PASS;


    // Parse TCP header
    struct tcphdr *tcp = (struct tcphdr *)((unsigned char *)ip + ip_hdr_len);

    // Ensure TCP header is within packet bounds
    if ((void *)(tcp + 1) > data_end)                       return XDP_PASS;
    unsigned int tcp_hdr_len = tcp->doff * 4;
    if (tcp_hdr_len < sizeof(struct tcphdr))                return XDP_PASS;
    
    unsigned char *payload = (unsigned char *)tcp + tcp_hdr_len;
    long payload_len = (char *)data_end - (char *)payload;
    if (payload_len < 4)                                    return XDP_PASS;

    // Копируем payload в стековую переменную (безопасно)
    unsigned char copy[MAX_PAYLOAD_COPY_TO_STACK];
    long copy_len = payload_len;
    if (copy_len > MAX_PAYLOAD_COPY_TO_STACK) copy_len = MAX_PAYLOAD_COPY_TO_STACK;
    
    // bpf_probe_read_kernel копирует данные из пакета в стек
    if (bpf_probe_read_kernel(copy, copy_len, payload) < 0) return XDP_PASS;

    // Поиск "SPAM" в скопированных данных (теперь доступ безопасен)
    for (int i = 0; i <= copy_len - 4; i++) {
        if (copy[i] == 0x53 && copy[i+1] == 0x50 &&
            copy[i+2] == 0x41 && copy[i+3] == 0x4D) {
            bpf_printk("SPAM found, dropping packet\n");
            return XDP_DROP;
        }
    }

    return XDP_PASS;
}

char __license[] SEC("license") = "GPL";