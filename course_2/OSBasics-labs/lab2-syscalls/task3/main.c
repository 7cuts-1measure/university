#include <stdint.h> // for intmax_t
#include <stdio.h>
#include <stdlib.h> // for exit
#include <unistd.h> // for fork, execve
#include <sys/ptrace.h>
#include <sys/user.h>
#define IGNORED 0

int main() {
    const int ignored = 0;
    pid_t pid = fork();
    if (pid == -1) {
        perror("fork problem");
        exit(1);
    } else if (pid == 0) {
        ptrace(PTRACE_ME, 0, 0, 0);
        execve("./hello_static", IGNORED, IGNORED);
        perror("execve problem"); /* execve has no return. if we are here -- execve has problems */
        exit(2);
    }
    /* only parent process should be heere */

}
