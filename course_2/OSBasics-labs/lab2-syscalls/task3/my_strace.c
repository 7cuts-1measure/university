#include <assert.h>
#include <string.h>
#include <stdint.h> // for intmax_t
#include <stdio.h>
#include <stdlib.h> // for exit
#include <unistd.h> // for fork, execve
#include <sys/wait.h> // WIFEXITED, W* defines. waitpid()
#include <sys/ptrace.h> // ptrace, ptrace PTRACE_* defines
#include <sys/user.h>   // for regs_struct
#include <stdbool.h>
#include <asm/unistd_64.h>
#include <errno.h>

#define IGNORED 0
#define SYSCALL_HEADER_PATH "/usr/include/asm/unistd_64.h"
#define BUFF_LEN 32
static char buf_syscall_name[BUFF_LEN];
static const char (*names)[BUFF_LEN] = NULL;

void clean() {
    if (names) free((void *)names);
}

int count_lines(FILE* f)
{
    if (!f) {
        fprintf(stderr, "Cannot count lines in NULL file\n");
        return -1;
    }
    // go to start of the file
    int offest_backup = ftell(f);
    rewind(f);

    int cnt = 0;
    char c;
    while ((c = fgetc(f)) != EOF) {
        if (c == '\n') cnt++;
    }  

    // restore offset 
    fseek(f, offest_backup, SEEK_SET);
    return cnt;
} 

bool cache_syscall_names() {
    FILE* f = fopen(SYSCALL_HEADER_PATH, "r");
    if (!f) {
        perror("Cannot open " SYSCALL_HEADER_PATH);
        puts("Printing only numbers");
        return false;
    }
    int lines = count_lines(f);
    names = (const char (*)[BUFF_LEN]) malloc(lines * sizeof(*names));
    
    ////////////////// PARSE HEADER FILE ////////////////
    // token_count:       0              1             2  
    // str in header:  #define   __NR_[syscall_name] [nr]
    char line[100];
    while (fgets(line, sizeof(line), f)) {
        int token_count = 0;
        for (char *str = line; ; str = NULL) {
            char *token = strtok(str, " ");
            if (token == NULL) 
                break;
            if (!strcmp(token, "#define")) {
                assert(token_count == 0 && "\"#define\" should be first token");
                token_count++;
                continue;
            } else if (!strncmp(token, "__NR_", 5)) {
                assert(token_count == 1 && "\"__NR_...\" should be second token");
                token_count++;
                strncpy(buf_syscall_name, token + 5, BUFF_LEN);
            } else if (token_count == 2) {
                char *endptr;
                int syscall_number = strtol(token, &endptr, 10);   // TODO: what if it is not an integer?
                if (*endptr != '\n' && *endptr != '\0' && *endptr != ' ') {
                    perror("Invalid define in " SYSCALL_HEADER_PATH);
                    exit(EXIT_FAILURE);
                }
                token_count = 0;
                strncpy((char*)names[syscall_number], buf_syscall_name, BUFF_LEN);
            } else {
                token_count = 0;
            }
        }
    }
    fclose(f);
    return true;
}

const char* get_syscall_name(long nr) 
{
    static bool cached = false; 

    // return numbers if have problems with parsing header with syscalls
    static bool return_numbers = false; 

    if (return_numbers) {   
        sprintf(buf_syscall_name, "%ld", nr);
        return buf_syscall_name;
    }
    if (!cached) {
        if (cache_syscall_names()) 
            cached = true;
        else 
            return_numbers = true;
    }
    return names[nr];
}

int main(int argc, char* argv[], char* env[]) 
{
    if (argc < 2) {
        printf("Usage: %s [prog to exec]\n", argv[0]);
        exit(EXIT_FAILURE);
    }

    pid_t pid = fork();
    if (pid == -1) {
        perror("fork problem");
        exit(EXIT_FAILURE);
    } else if (pid == 0) {
        ptrace(PTRACE_TRACEME, IGNORED, IGNORED, IGNORED);
        execve(argv[1], argv + 1, env);
        perror("exec child problems");
        exit(EXIT_FAILURE);
    } else {
        // only parent process should be here
        int status;
        waitpid(pid, &status, 0);
        ptrace(PTRACE_SETOPTIONS, pid, 0, PTRACE_O_TRACESYSGOOD);
        while (1) {
            ptrace(PTRACE_SYSCALL, pid, NULL, NULL);
            waitpid(pid, &status, 0);
            if (WIFEXITED(status)) 
                break;
            if (WIFSTOPPED(status)) {
                struct user_regs_struct regs;
                ptrace(PTRACE_GETREGS, pid, NULL, &regs);
                printf("%s\n", get_syscall_name(regs.orig_rax));
            }
        } 
    }
    clean();
    return 0;
}
