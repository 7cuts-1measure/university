#include <err.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/mman.h>
#include <unistd.h>


#define BUF_ON_STACK 4096
#define BUF_ON_HEAP 4096
#define PAGE_SIZE 4096

void recursive_stack() 
{
    static size_t curr_size = 0;
    curr_size += BUF_ON_STACK;
    printf("Allocated %.2lf KBytes\n", (double)curr_size / 1024);

    char buf[BUF_ON_STACK];
    // speed up x5 after 1 Kbyte
    usleep(curr_size <= 1000 * 1024 ? 50000 : 10000 );

    recursive_stack();
}

void loop_heap() 
{
    size_t curr_size = BUF_ON_HEAP;
    char *p = malloc(curr_size);
    
    if (!p) err(1, "cannot malloc p");
    
    for (int i = 0; i < 200; ++i) {
        curr_size += BUF_ON_HEAP;
        p = realloc(p, curr_size);
        if (!p) err(1, "cannot realloc p");
        sleep(1);
    }
    puts("Sleep 5...");
    sleep(5);
    puts("Free memory");
    free(p);
    sleep(5);    
}

void process_map() {
    puts("create 10-page anonymous mapping");
    
    char *pmap = mmap(NULL, PAGE_SIZE * 10, PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANONYMOUS, -1, 0);
    if (pmap == MAP_FAILED) {
        err(1, "cannot mmap");
    }
    printf("mapping start addr: %lx\n", (uintptr_t)pmap);

    puts("sleep 10...");
    sleep(10);

    puts("attempting to reed...");
    char c = *(pmap + 2 * PAGE_SIZE);
    
    puts("attempting to write...");
    *(pmap + 2 * PAGE_SIZE) = 'a';
    
    puts("ok!");
    sleep(5);

    
    
    //puts("change protection to READ_ONLY");
    // mprotect(pmap, PAGE_SIZE * 10, PROT_WRITE);
    //puts("try read");
    // c = *(pmap + 2 * PAGE_SIZE);
    
    
    // puts("change protection to READ_ONLY");
    // mprotect(pmap, PAGE_SIZE * 10, PROT_READ);
    // puts("try write");
    // *(pmap + 2 * PAGE_SIZE) = 'a';

    puts("munmap 4,5,6 pages");
    if (munmap(pmap + 3 * PAGE_SIZE, 3 * PAGE_SIZE) == -1) {
        err(1, "failed to munmap len=3*PAGE_SIZE at %p", pmap + 3 *PAGE_SIZE);
    }
    puts("sleep 10000...");
    sleep(10000);

}

int main() {
    printf("My pid: %d\n", getpid());
    sleep(10);
    puts("wake up");
    //recursive_stack();
    //loop_heap();
    process_map();
    
    return 0;
}