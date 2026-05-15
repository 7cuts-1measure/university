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
#define READER_PIPE_FD 0
#define WRITER_PIPE_FD 1

int fields[2];
size_t buf_size;
sigset_t old;
volatile sig_atomic_t allow_read = false;
volatile sig_atomic_t allow_write = true;
volatile sig_atomic_t partner_pid;

void check_order(unsigned next, unsigned prev) {
    if (next != prev + 1)
        printf("Wrong next number: %d -> %d. Diff = %d\n", prev, next, next - prev);
}

unsigned child_recv_num(const unsigned *ptr) {
    while (!allow_read) {
        sigsuspend(&old);   // unblock SIGUSR1
    }
    
    unsigned num = *ptr;
    allow_read = false;

    if (kill(partner_pid, SIGUSR1) == -1) {
        err(1, "SIGUSR1 to parent (%d)", partner_pid);
    }
    return num;
}


void parent_send_num(unsigned *ptr, unsigned num) {
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

    close(fields[1]);

    unsigned prev_num = -1;
    unsigned num;
    
    for (;;) {
        ssize_t nread = (read(fields[0], &num, sizeof(num)));
        if (nread == -1) {
            err(1, "read pipe");
        }
        if (nread == 0) {
            // EOF (Writer closed the pipe)
            puts("child: pipe closed");
            close(fields[0]);
        }
        check_order(num, prev_num);
        prev_num = num;
    }
}


void parent_work() {
    close(fields[0]);  // do not need it
    
    for (unsigned i = 0; ; i++) {
        write(fields[1], &i, sizeof(i));
    }
}


void terminate_handler(int signo, siginfo_t *info, void *context) {
    bool is_child = getppid() == partner_pid;
    if (info->si_pid == partner_pid) {
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

    close(fields[is_child ? 0 : 1]);
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
    close(fields[is_parent ? 1 : 0]);
    exit(0);
}

int main() {
    sigset_t mask;
    sigemptyset(&mask);
    sigaddset(&mask, SIGUSR1); 
    sigprocmask(SIG_BLOCK, &mask, &old);
    
    sigset_t mask_int;
    sigemptyset(&mask_int);
    sigaddset(&mask_int, SIGINT);
    
    struct sigaction sa_int = {
        .sa_mask = mask_int,
        .sa_flags = SA_SIGINFO,
        .sa_sigaction = interrupt_handler 
    };

    struct sigaction sa_term = {
        .sa_mask = mask_int,
        .sa_flags = SA_SIGINFO,
        .sa_sigaction = terminate_handler
    };

    sigaction(SIGTERM, &sa_term, NULL);
    sigaction(SIGINT, &sa_int, NULL);

    buf_size = sysconf(_SC_PAGESIZE);

    if (pipe(fields) == -1) {
        err(1, "pipe");
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