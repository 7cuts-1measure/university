#include <assert.h>
#include <bits/types/siginfo_t.h>
#include <signal.h>
#include <stdio.h>
#include <stdbool.h>
#include <unistd.h>
#include <sys/mman.h>
#include <err.h>
#include <fcntl.h>

volatile sig_atomic_t allow_read = false;
volatile sig_atomic_t pid_allow_read;
volatile sig_atomic_t allow_write = true;
volatile sig_atomic_t pid_allow_write;

void check_order(unsigned num, unsigned prev_num) {
    if (num != prev_num + 1)
        printf("Wrong next number: %d -> %d. Diff = %d\n", prev_num, num, num - prev_num);
}


void allow_read_handler(int signo, siginfo_t *siginfo, void *context) {
    
    if (signo == SIGUSR1) {
        pid_allow_read = siginfo->si_pid;
        allow_read = true;
    }
}

void allow_write_handler(int signo, siginfo_t *siginfo, void *context) {
    if (signo == SIGUSR1) {
        allow_write = true;
        pid_allow_write = siginfo->si_pid;
    }
}

unsigned child_recv_num(const unsigned *ptr, pid_t ppid, const sigset_t *old) {
    while (!allow_read || pid_allow_read != ppid) {
        sigsuspend(old);   // unblock SIGUSR1
    }
    
    unsigned num = *ptr;
    allow_read = false;

    if (kill(ppid, SIGUSR1) == -1) {
        err(1, "SIGUSR1 to parent (%d)", ppid);
    }
    return num;
}


void parent_send_num(unsigned *ptr, unsigned num,  pid_t cpid, sigset_t *old) {
    *ptr = num;
    allow_write = false;

    if (kill(cpid, SIGUSR1) == -1) {
        err(1, "SIGUSR1 to %d", cpid);
    }
    while (!allow_write || pid_allow_write != cpid) {
        sigsuspend(old);   // unblock SIGUSR1
    }
}

void child_work(void *buf, size_t size, sigset_t *old) {
    assert(size % sizeof(unsigned int) == 0);
    pid_t ppid = getppid();
    printf("child: %d\n" "parent: %d\n", getpid(), ppid);

    
    struct sigaction sa =  {
        .sa_handler = NULL,
        .sa_mask = 0,
        .sa_flags = SA_SIGINFO,
        .sa_sigaction = allow_read_handler
    };
    sigaction(SIGUSR1, &sa, NULL);
         
    unsigned prev_num = -1;
    unsigned num;
    unsigned *ptr = buf;

    const size_t elems = size / sizeof(num);
    for (int i = 0; ; i++) {
        num = child_recv_num(&ptr[i % elems], ppid, old);
        check_order(num, prev_num);
        prev_num = num;
    }
}


void parent_work(void *buf, size_t size, pid_t cpid, sigset_t *old) {
    struct sigaction sa =  {
        .sa_handler = NULL,
        .sa_mask = 0,
        .sa_flags = SA_SIGINFO,
        .sa_sigaction = allow_write_handler
    };
    sigaction(SIGUSR1, &sa, NULL);
    pid_allow_write = cpid;

    assert(size % sizeof(unsigned int) == 0);
    unsigned *ptr = buf; 
    
    const size_t elems = size / sizeof(unsigned);
    for (unsigned i = 0; ; i++) {
        parent_send_num(&ptr[i % elems], i, cpid, old);
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
            child_work(buf, PAGE_SIZE, &old);
        default:
            parent_work(buf, PAGE_SIZE, pid, &old);
    } 
    assert(0);
}