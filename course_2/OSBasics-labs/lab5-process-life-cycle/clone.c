#include <assert.h>
#define _GNU_SOURCE
#include <err.h>
#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <sys/mman.h>
#include <sched.h>

#define STACK_SIZE 10 * 1024
#define MAX_DEPTH 10
#define SLEEP_SECS 0

// do not use static var for depth
// because it will be placed in .data section
// but we want to see it in stack
void recursive_hello(long long depth) {
    char array[] = "hello world";
    sleep(SLEEP_SECS);
    if (depth < MAX_DEPTH) {
        recursive_hello(depth + 1);
    }

}

int child_main(void *arg) {
    recursive_hello(0);
}

void *map_stack(const char *fname, size_t stack_size) {
    assert(stack_size > 0);
    
    int fd = open(fname, O_CREAT | O_RDWR  | O_TRUNC | O_DSYNC);
    if (fd == -1) {
        perror(fname);
        return NULL;
    }

    if (ftruncate(fd, stack_size) == -1) {
        perror("ftruncate");
        return NULL;
    } 
    
    void *map_addr = mmap(NULL, stack_size, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
    if (map_addr == MAP_FAILED) {
        perror("mmap");
    }
    close(fd);
    return map_addr;
}

int main() {
    void *const stack_top = map_stack("stack.bin", STACK_SIZE);
    if (!stack_top) {
        return 1;
    }  
    sleep(2);
    void *const stack_base = stack_top + STACK_SIZE;

    pid_t pid = clone(child_main, stack_base, 0, NULL);
    if (pid == -1) {
        perror("clone");
        return 1;
    }
    printf("parent pid = %d\n", getpid());
    printf("child pid= %d\n", pid);
    //munmap(stack, STACK_SIZE);
    return 0;
}