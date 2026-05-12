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

            puts("child: call exit(5)");
            exit(5);
        }
        default: {
            sleep(35);
            // no wait() 
        }
   }
    return 0;
}
