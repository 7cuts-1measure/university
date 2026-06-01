#include <assert.h>
#include <bits/types/siginfo_t.h>
#include <bits/types/sigset_t.h>
#include <signal.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/mman.h>
#include <err.h>
#include <fcntl.h>

// theese vars should be inited before send/recv jobs
void *buf;
size_t buf_size;
sigset_t old;
pid_t partner_pid;

// vars for signals 
bool allow_read = false;
bool allow_write = true;


void check_order(unsigned int next, unsigned int prev) {
    //printf("%d->%d\n", prev, next);
    if (next != prev + 1)
        printf("Wrong next number: %d -> %d. Diff = %d\n", prev, next, next - prev);
}


void allow_read_handler(int signo, siginfo_t *siginfo, void *context) {   
    if (signo == SIGUSR1 && siginfo->si_pid == partner_pid) {
        allow_read = true;
    }
}


void allow_write_handler(int signo, siginfo_t *siginfo, void *context) {
    if (signo == SIGUSR1 && siginfo->si_pid == partner_pid ) {
        allow_write = true;
    }
}


unsigned int child_recv_num(const unsigned int *ptr) {
    while (!allow_read) {
        sigsuspend(&old);   // unblock SIGUSR1
    }
    
    unsigned int num = *ptr;
    allow_read = false;

    if (kill(partner_pid, SIGUSR1) == -1) {
        err(1, "SIGUSR1 to parent (%d)", partner_pid);
    }
    return num;
}


void parent_send_num(unsigned int *ptr, unsigned int num) {
    *ptr = num;
    allow_write = false;

    if (kill(partner_pid, SIGUSR1) == -1) {
        err(1, "SIGUSR1 to %d", partner_pid);
    }
    while (!allow_write) {
        sigsuspend(&old);   // unblock SIGUSR1
    }
}


void child_work() {
    pid_t ppid = getppid();
    printf("child: %d\n" "parent: %d\n", getpid(), ppid);

    struct sigaction sa =  {
        .sa_mask = 0,
        .sa_flags = SA_SIGINFO,
        .sa_sigaction = allow_read_handler
    };
    sigaction(SIGUSR1, &sa, NULL);
         
    unsigned int prev_num = -1;
    unsigned int num;
    unsigned int *ptr = buf;

    const size_t elems = buf_size / sizeof(num);
    for (int i = 0; ; i++) {
        num = child_recv_num(&ptr[i % elems]);
        check_order(num, prev_num);
        prev_num = num;
    }
}


void parent_work() {
    struct sigaction sa_usr1 =  {
        .sa_mask = 0,
        .sa_flags = SA_SIGINFO,
        .sa_sigaction = allow_write_handler
    };

    sigaction(SIGUSR1, &sa_usr1, NULL);

    unsigned int *ptr = buf;
    const size_t elems = buf_size / sizeof(unsigned int);
    
    for (unsigned int i = 0; ; i++) {
        parent_send_num(&ptr[i % elems], i);
    }
}


void terminate_handler(int signo, siginfo_t *info, void *context) {
    
    if (info->si_pid == partner_pid) {
        bool is_child = getppid() == partner_pid;
        if (is_child) {
            char msg[] = "Reader: Terminating because Writer was interrupted\n";
            write(1, msg, sizeof(msg));
        } else {
            char msg[] = "Writer: Terminating because Reader was interrupted\n";
            write(1, msg, sizeof(msg));
        }
    } else {
        // got SIGTERM but not from the partner
        char msg[] = "Caught SIGTERM not from partner. Still terminating\n";
        kill(partner_pid, SIGTERM); 
        write(1, msg, sizeof(msg));
    }
    munmap(buf, buf_size);
    exit(0);
}

void interrupt_handler(int signo, siginfo_t *info, void *context) {
    bool is_parent = getppid() != partner_pid;
    if (is_parent) {
        char msg[] = "\nWriter: interrupting...\n";
        write(1, msg, sizeof(msg));
    } else {
        char msg[] = "\nReader: interrupting...\n";
        write(1, msg, sizeof(msg));
    }
    kill(partner_pid, SIGTERM);
    munmap(buf, buf_size);
    exit(0);
}

int main() {
    sigset_t mask;
    sigemptyset(&mask);
    sigaddset(&mask, SIGUSR1);  // block SIGUSR1
    sigprocmask(SIG_BLOCK, &mask, &old);
    
    struct sigaction sa_int = {
        .sa_mask = 0,
        .sa_flags = SA_SIGINFO,
        .sa_sigaction = interrupt_handler 
    };

    sigaddset(&mask, SIGINT);
    struct sigaction sa_term = {
        .sa_mask = mask,
        .sa_flags = SA_SIGINFO,
        .sa_sigaction = terminate_handler
    };

    sigaction(SIGTERM, &sa_term, NULL);
    sigaction(SIGINT, &sa_int, NULL);

    buf_size = sysconf(_SC_PAGESIZE);
    
    buf = mmap(NULL, buf_size, PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANONYMOUS, -1, 0);
    if (buf == MAP_FAILED) {
        err(1, "Failed to map");
    }
      
    pid_t pid = fork();
    switch (pid) {
        case -1:
            err(1, "fork");
        case 0:
            partner_pid = getppid();
            child_work();
        default:
            partner_pid = pid;
            parent_work();
    } 
    assert(0);
}