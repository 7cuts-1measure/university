#include <err.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <unistd.h>
#include <sys/wait.h>

int global = 0xAAAA;

void print_status(int status) {
    if (WIFEXITED(status))
        printf("Exit code: %d\n", WEXITSTATUS(status));
    if (WIFSIGNALED(status)) {
        printf("Terminated by signal %d", WTERMSIG(status));
    }
}

int main() {

    int local = 0xBBBB;

    pid_t pid = getpid();
    printf("vars: local=%x, global=%x\n", local, global);
    printf("pid = %d\n", pid);

    pid_t child_pid = fork();
    switch (child_pid) {
        case -1:
            err(1, "fork()");
        case 0: {
            pid_t pid = getpid();
            pid_t ppid = getppid();
            printf("parent:\t%d\n", ppid);
            printf("child:\t%d\n", pid);
            printf("vars child: local=%x, global=%x\n", local, global);
            sleep(10);
            
            local = 0xCCCC;
            global = 0xDDDD;
            
            printf("vars child: local=%x, global=%x\n", local, global);

            sleep(5);    
            puts("child: exit");
            exit(5);
        }
        default: {
            sleep(20);
            printf("vars parent: local=%x, global=%x\n", local, global);

            int status;
            wait(&status); 
            print_status(status); 
        }

   }
    
    return 0;
}
