#include <asm-generic/errno-base.h>
#include <assert.h>
#include <errno.h>
#include <signal.h>
#include <stdio.h>
#include <stdbool.h>
#include <unistd.h>
#include <sys/mman.h>
#include <err.h>
#include <fcntl.h>

sig_atomic_t allow_read = false;
sig_atomic_t allow_write = true;

void check_order(unsigned num, unsigned prev_num) {
    if (num != prev_num + 1)
        printf("Wrong next number: %d -> %d. Diff = %d\n", prev_num, num, num - prev_num);
}


void allow_read_handler(int signo) {
    if (signo == SIGUSR1) {
        allow_read = true;
    }

}

void child_work(void *buf, size_t size, sigset_t old) {
    assert(size % sizeof(unsigned int) == 0);
   
    pid_t ppid = getppid();
    printf("child: %d\nparent: %d\n", getpid(), ppid);
   
    signal(SIGUSR1, allow_read_handler);
         
    unsigned prev_num = -1;
    unsigned num;
    unsigned *ptr = buf;

    const size_t elems = size / sizeof(num);
    for (int i = 0; ; i++) {
        while (!allow_read) {
            sigsuspend(&old);
        }
        
        num = ptr[i % elems];
        allow_read = false;

        if (kill(ppid, SIGUSR1) == -1) {
            err(1, "SIGUSR1 to parent (%d)", ppid);
        }
        
        check_order(num, prev_num);
        prev_num = num;
    }
}

void allow_write_handler(int signo) {
    if (signo == SIGUSR1) {
        allow_write = true;
    }
}

void parent_work(void *buf, size_t size, pid_t cpid, sigset_t old) {
    signal(SIGUSR1, allow_write_handler);
    
    assert(size % sizeof(unsigned int) == 0);
    unsigned *ptr = buf; 
    
    const size_t elems = size / sizeof(unsigned);
    for (unsigned i = 0; ; i++) {
        ptr[i % elems] = i;
        allow_write = false;

        if (kill(cpid, SIGUSR1) == -1) {
            err(1, "SIGUSR1 to %d", cpid);
        }
        
        while (!allow_write) {
            sigsuspend(&old);   // unblock SIGUSR1
        }
    }
}

int main() {
    sigset_t mask, old;
    sigemptyset(&mask);
    sigaddset(&mask, SIGUSR1);  // block SIGUSR1
    sigprocmask(SIG_BLOCK, &mask, &old);
    
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
            child_work(buf, PAGE_SIZE, old);
        default:
            parent_work(buf, PAGE_SIZE, pid, old);
    } 
    assert(0);
}