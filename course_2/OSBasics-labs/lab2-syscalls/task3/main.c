#include <stdint.h> // for intmax_t
#include <stdio.h>
#include <stdlib.h> // for exit
#include <unistd.h> // for fork, execve

int main() {
    pid_t pid = fork();
    if (pid == -1) {
        perror("fork problem");
        exit(1)
    } else if (pid == 0) {
        execve("./hello_static", NULL, NULL);
        perror("execve problem"); /* execve has no return. if we are here -- execve has problems */
        exit(2);
    }

    /* only parent process should be heere */
    puts("ima parent");  
}
