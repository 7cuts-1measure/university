#include <assert.h>
#include <stdio.h>
#include <stdbool.h>
#include <unistd.h>
#include <sys/mman.h>
#include <err.h>
#include <fcntl.h>


void check_order(unsigned num, unsigned prev_num) {
    if (num != prev_num + 1)
        printf("Wrong next number: %d -> %d. Diff = %d\n", prev_num, num, num - prev_num);
}

void child_work(unsigned *buf, size_t size) {
    assert(size % sizeof(unsigned int) == 0);
    
    unsigned prev_num = -1;
    unsigned num;

    const size_t elems = size / sizeof(num);
    for (int i = 0; ; i++) {
        num = buf[i % elems];
        check_order(num, prev_num);
        prev_num = num;
    }
}

void parent_work(unsigned *buf, size_t size, pid_t cpid) {
    unsigned int *ptr = buf; 
    assert(size % sizeof(unsigned int) == 0);
    
    const size_t elems = size / sizeof(unsigned);
    for (unsigned i = 0; ; i++) {
        ptr[i % elems] = i;

    }

}

int main() {
   
    const int PAGE_SIZE = sysconf(_SC_PAGESIZE);

    void *buf = mmap(NULL, PAGE_SIZE, PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANONYMOUS, -1, 0);
    if (buf == MAP_FAILED) {
        err(1, "Failed to map");
    }
      
    pid_t pid = fork();
    switch (pid) {
        case -1:
            err(1, "fork");
        case 0:
            child_work(buf, PAGE_SIZE);
        default:
            parent_work(buf, PAGE_SIZE, pid);
    }
    puts("why it here ???");


}