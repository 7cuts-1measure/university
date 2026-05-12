#include <err.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <unistd.h>
#include <sys/wait.h>


int main() {
    pid_t pid = getpid();
    printf("parent pid:\t%d\n", pid);
    
    pid_t child_pid = fork();
    switch (child_pid) {
        case -1:
            err(1, "fork()");
        case 0: {
            pid_t pid = getpid();
            printf("child pid:\t%d\n", pid);

            while(true) {
                sleep(5);
                printf("process %d: ppid=%d\n", pid, getppid());
            }
        }
        default: {
            exit(0);
        }
   }
    return 0;
}
