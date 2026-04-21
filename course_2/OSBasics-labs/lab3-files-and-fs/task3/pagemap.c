#include <sys/types.h>
#define _GNU_SOURCE

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <err.h>
#include <errno.h>
#include <unistd.h>

#define LINE_SZ 1024
#define PME_SIZE 8


#define GET_BIT(num, idx) (( (num) & (1UL << (idx)) ) >> (idx)) 
int PAGE_SIZE = 4096;

#define IS_IN_RAM(pme)             GET_BIT((pme), 63)
#define IS_IN_SWAP(pme)            GET_BIT((pme), 62)
#define IS_FILE_MAPPED(pme)        GET_BIT((pme), 61)
#define IS_WRITE_PROTECTED(pme)    GET_BIT((pme), 57)
#define IS_EXCLUSIVELY_MAPPED(pme) GET_BIT((pme), 62)
#define IS_SOFT_DIRTY(pme)         GET_BIT((pme), 55)


uint64_t read_pme(FILE *f, uint64_t virtual_addr) 
{    
    uint64_t vpn = virtual_addr / PAGE_SIZE;
    uint64_t offset = vpn * sizeof(uint64_t);

    if (fseek(f, offset, SEEK_SET) == -1) {
        err(EXIT_FAILURE, "cannot fseek /proc/self/pagemap");     
    }

    uint64_t pme = 0;
    
    fread(&pme, sizeof(pme), 1, f);
    if (ferror(f)) {
        err(EXIT_FAILURE, "cannot read /proc/self/pagemap");
    }
    return pme;
}

#define CNT_SHOW 15

void print_one(uint64_t start_addr, int count, FILE *pagemap_file) 
{
    uint64_t addr = start_addr + (uint64_t)count * PAGE_SIZE;
    uint64_t pme = read_pme(pagemap_file, addr);
    
    printf("%5d:   %s      %s        %s            %s          %s          %s       \n",
        count,
        IS_IN_RAM(pme)             ? "x" : ".",
        IS_IN_SWAP(pme)            ? "x" : ".",
        IS_FILE_MAPPED(pme)        ? "x" : ".",
        IS_WRITE_PROTECTED(pme)    ? "x" : ".",
        IS_EXCLUSIVELY_MAPPED(pme) ? "x" : ".",
        IS_SOFT_DIRTY(pme)         ? "x" : "."
    );
}

void print_pme_in_range(FILE *pagemap_file, uint64_t start_addr, uint64_t end_addr)
{
    printf(    "  cnt   ram    swap   file-mapped    wr-prot    excl    soft-dirty pte\n");
    int total_pages = (end_addr - start_addr) / PAGE_SIZE;
    int count = 1;

    
    for (int count = 0; count < CNT_SHOW; count++) {
        print_one(start_addr, count, pagemap_file);
    }

    if (total_pages > CNT_SHOW) {
        printf("   ... and more %d ...\n", total_pages - 2 * CNT_SHOW + 1);
        for (int count = total_pages - CNT_SHOW; count < total_pages; count++) {
            print_one(start_addr, count, pagemap_file);
        }
    }


}

void print_file(FILE *f) {
    char *line = NULL;
    while (!feof(f)) {
        size_t len = 0;
        getline(&line, &len, f);
        puts(line);
    }
    free(line);
    rewind(f);
}

int main() 
{
    volatile int *p = malloc(4096 * 4);
    
    FILE *pagemap_file  = fopen("/proc/self/pagemap", "rb");
    FILE *maps_file     = fopen("/proc/self/maps", "rb");
    print_file(maps_file);
    if (!pagemap_file || !maps_file) {
        warn("cannot open file");
        return 1;
    }

    PAGE_SIZE = getpagesize();
    printf("Page size: %d bytes\n", PAGE_SIZE);

    char *line = NULL;
    size_t len = 0;
    while (getline(&line, &len, maps_file) > 0) {        
        uint64_t start_addr, end_addr;
 
        int nscan = sscanf(line, "%lx-%lx", &start_addr, &end_addr);        
        if (nscan != 2)
            err(EXIT_FAILURE, "cannot parse /proc/self/maps");


        printf("%lx-%lx\n", start_addr, end_addr);
        
        print_pme_in_range(pagemap_file, start_addr, end_addr);
        
    }
    if (ferror(maps_file)) {
        perror("/proc/self/maps");
        return EXIT_FAILURE;
    }

    free(line);

    return 0;
}