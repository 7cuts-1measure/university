#include <stdbool.h>
#include <linux/if_ether.h>
#include <linux/in.h>
#include <linux/ip.h>
#include <linux/tcp.h>
#include <linux/bpf.h>
#include <bpf/bpf_helpers.h>
#include <bpf/bpf_endian.h>

#define MAX_BLACKLIST_ENTRIES 1000


// wtf is this declaration ??????????
struct {
    __uint(type, BPF_MAP_TYPE_HASH);
    __uint(max_entries, MAX_BLACKLIST_ENTRIES);
    __type(key, __u32);
    __type(value, __u8);
} blacklist SEC(".maps");


bool parse_tcp_packet(struct xdp_md *ctx, __u32 *src_ip, __u32 *dst_ip, __u8 *tcp_header_len) {
    void *data = (void *)(long)ctx->data;
    void *data_end = (void *)(long)ctx->data_end;
    
    struct ethhdr *eth  = data;
    if ((void *)(eth + 1) > data_end)        return false;

    if (eth->h_proto != bpf_htons(ETH_P_IP)) return false;

    struct iphdr *ip = (void *)(eth + 1);
    if ((void *)(ip + 1) > data_end)         return false;
    if (ip->protocol != IPPROTO_TCP)         return false;

    *src_ip = ip->saddr;
    *dst_ip = ip->daddr;

    __u32 ip_hdr_len = ip->ihl * 4;
    if ((void *)ip + ip_hdr_len > data_end) return false;

    struct tcphdr *tcp = (void *)ip + ip_hdr_len;
    if ((void *)(tcp + 1) > data_end)       return false;

    *tcp_header_len = tcp->doff * 4;

    return true;
}

// Объявляем секцию "xdp", под которую ядро будет загружать нашу программу
SEC("xdp")
int xdp_tcp_blacklist_func(struct xdp_md *ctx) {
    __u32 src_ip = 0, dst_ip = 0;
    __u8 tcp_hdr_len = 0;
    
    if (!parse_tcp_packet(ctx, &src_ip, &dst_ip, &tcp_hdr_len)) {
        return XDP_PASS;
    }
    // Проверяем src_ip в чёрном списке
    bool is_src_ip_in_blacklist = bpf_map_lookup_elem(&blacklist, &src_ip) != NULL;
    if (is_src_ip_in_blacklist) {
        bpf_printk("DROP src_ip: %d\n", src_ip);
        return XDP_DROP;
    }
    return XDP_PASS;
}
char _license[] SEC("license") = "GPL";
