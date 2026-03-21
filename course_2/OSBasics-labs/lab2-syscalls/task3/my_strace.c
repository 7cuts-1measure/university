#include <stdint.h> // for intmax_t
#include <stdio.h>
#include <stdlib.h> // for exit
#include <unistd.h> // for fork, execve
#include <sys/wait.h> // WIFEXITED, W* defines. waitpid()
#include <sys/ptrace.h> // ptrace, ptrace PTRACE_* defines
#include <sys/user.h>   // 

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
            sprintf(buf_syscall_name, "%d\0", nr);
            return buf_syscall_name;
        }
   }
}

int main(int argc, char* argv[], char* env[]) {
    if (argc < 2) {
        printf("%s: ./my_strace [prog to exec]", argv[0]);
        exit(3);
    }

    pid_t pid = fork();
    if (pid == -1) {
        perror("fork problem");
        exit(1);
    } else if (pid == 0) {
        ptrace(PTRACE_TRACEME, IGNORED, IGNORED, IGNORED);
        execve(argv[1], argv + 1, env);
        perror("exec child problems");
        exit(2);
    } else {
         /* only parent process should be here */
        int status;
        waitpid(pid, &status, 0);

        ptrace(PTRACE_SETOPTIONS, pid, 0, PTRACE_O_TRACESYSGOOD);
        while (1) {
            ptrace(PTRACE_SYSCALL, pid, NULL, NULL);
            waitpid(pid, &status, 0);
           
            if (WIFEXITED(status)) break;
            
            if (WIFSTOPPED(status)) {
                struct user_regs_struct regs;
                ptrace(PTRACE_GETREGS, pid, NULL, &regs);
                printf("> syscall: %s\n", get_syscall_name(regs.orig_rax));
            }
        } 
    }
    return 0;
}
