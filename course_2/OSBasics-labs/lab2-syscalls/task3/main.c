#include <stdint.h> // for intmax_t
#include <stdio.h>
#include <stdlib.h> // for exit
#include <unistd.h> // for fork, execve
#include <sys/wait.h>
#include <sys/ptrace.h>
#include <sys/user.h>
#define IGNORED 0

char buf_syscall_name[20];

const char* get_syscall_name(long nr) {
   switch(nr) {
        case 12: return "brk";
        case 21: return "accesss";
        case 9:  return "mmap";
        case 158:return "arch_prctl";
        case 218:return "set_tid_address";
        case 273:return "set_robust_list"; 
        case 334:return "rseq";
        case 10: return "protect";
        case 1:  return "write";
        case 60: return "exit";
        default: {
            sprintf(buf_syscall_name, "%d", nr);
            return buf_syscall_name;
        }
   }
}
//TODO: Передавать запускаемый экзешник через аргумент
int main() {
    const int ignored = 0;
    pid_t pid = fork();
    if (pid == -1) {
        perror("fork problem");
        exit(1);
    } else if (pid == 0) {
        ptrace(PTRACE_TRACEME, ignored, ignored, ignored);
        execl("./hello_static", "hello_static", NULL);
        perror("exec child problems");
        exit(2);
    } else {
         /* only parent process should be here */
        int status;
        waitpid(pid, &status, 0);

        ptrace(PTRACE_SETOPTIONS, pid, 0, PTRACE_O_T RACESYSGOOD);
        while (1) {
            ptrace(PTRACE_SYSCALL, pid, NULL, NULL);
            waitpid( pid, &status, 0);
            if (WIFEXITED(status)) break;
            if (WIFSTOPPED(status) && WSTOPSIG(status) & 0x80) {
                struct user_regs_struct regs;
                ptrace(PTRACE_GETREGS, pid, NULL, &regs);
                printf("> syscall: %s\n", get_syscall_name(regs.orig_rax));
            }
        }
     
    
    }
    return 0;
}
