#include <err.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <unistd.h>
#include <sys/wait.h>

int global = 0xAAAA;

void print_wstatus(int wstatus) {
    printf("Exit code: %d\n", WEXITSTATUS(wstatus));
}

int main() {

    int local = 0xBBBB;

    pid_t pid = getpid();
    printf("vars: local=%x, global=%x\n", local, global);
    printf("pid = %d\n", pid);

    pid_t child_pid = fork(); 
    if (child_pid == -1) {
        err(1, "Cannot create child");
    } else if (child_pid == 0) {
        pid_t pid = getpid();
        pid_t ppid = getppid();
        printf("parent:\t%d\n", ppid);
        printf("child:\t%d\n", pid);
        sleep(15);


        printf("vars child: local=%x, global=%x\n", local, global);
        
        local = 0xCCCC;
        global = 0xDDDD;
        printf("vars child: local=%x, global=%x\n", local, global);
        puts(">vars changed");
        sleep(20);
        
        puts("child: exit");
        exit(5);
    } else {
        sleep(15);
        printf("vars parent: local=%x, global=%x\n", local, global);
 
        int status;
        wait(&status);
 
        print_wstatus(status);
    }
    
    return 0;
}