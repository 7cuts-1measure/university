#include <stdbool.h>
#include <linux/if_ether.h>
#include <linux/in.h>
#include <linux/ip.h>
#include <linux/tcp.h>
#include <linux/bpf.h>
#include <bpf/bpf_helpers.h>
#include <bpf/bpf_endian.h>

#define MAX_PAYLOAD_LEN 1600

// Helper function to check if the packet is TCP
static bool is_tcp(struct ethhdr *eth, void *data_end) {
    if ((void *)(eth + 1) > data_end)
        return false;

    // Only handle IPv4 packets
    if (bpf_ntohs(eth->h_proto) != ETH_P_IP)
        return false;

    struct iphdr *ip = (struct iphdr *)(eth + 1);

    if ((void *)(ip + 1) > data_end)
        return false;

    if (ip->protocol != IPPROTO_TCP)
        return false;

    return true;
}

#define HAS_SPAM(payload) ((payload)[i] == 'S' && (payload)[i+1] == 'P' && (payload)[i+2] == 'A' && (payload)[i+3] == 'M')

SEC("xdp")
int xdp_pass(struct xdp_md *ctx) {
    
    // Pointers to packet data
    void *data = (void *)(long)ctx->data;
    void *data_end = (void *)(long)ctx->data_end;

    struct ethhdr *eth = data;

    if (!is_tcp(eth, data_end))                             return XDP_PASS;

    struct iphdr *ip = (struct iphdr *)(eth + 1);
    int ip_hdr_len = ip->ihl * 4;

    struct tcphdr *tcp = (struct tcphdr *)((unsigned char *)ip + ip_hdr_len);
    if ((void *)(tcp + 1) > data_end)                       return XDP_PASS;
    
    unsigned int tcp_hdr_len = tcp->doff * 4;
    if (tcp_hdr_len < sizeof(struct tcphdr))                return XDP_PASS;
    
    unsigned char *payload = (unsigned char *)tcp + tcp_hdr_len;

    for (int i = 0; i <= MAX_PAYLOAD_LEN-4; i++) {
        if ((void *)(payload + i + 4) > data_end) break;
        if (HAS_SPAM(payload))                              return XDP_DROP;
    }

    return XDP_PASS;
}

char __license[] SEC("license") = "GPL";