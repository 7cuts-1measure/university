#include <stdio.h>
#include <sys/syscall.h>  // for SYS_* constants
#include <unistd.h>       // for syscall func
#include <errno.h>

ssize_t my_write(int fd, const void* buf, size_t count) {
    long long result; 
    
    /* 
     * 1. Кладём номер сискола в rax. fd -> rdi, buf -> rsi, count -> rdx
     * 2. Проц выполняет инструкцию syscall
     * 3. Результат сискола из rax помещается в result
    */
    asm volatile(
     "\tsyscall\n"
        : "=a" (result)
        : "a" (SYS_write), "D" (1), "S" (buf), "d" (count) // a - rax, D - rdi, S - rsi, d - rdx
        : "rcx", "r8", "r9", "r10", "r11", "memory", "cc" 
    );
    if (-4095 <= result && result < 0) {
        errno = -result;
        return -1;
    }
    return result;


}

int main() {
    ssize_t r;    
    // ======== TASK 1.1.1-1.1.2 ============
    r = puts("puts: hello, world!");
    if (r == -1) {
        perror("puts");
    }
    // ======== TASK 1.1.3 ============
    const int STDOUT_FD = 1;
    const char msg_syscall[] = "syscall: hello, world!\n";
    r = syscall(SYS_write, STDOUT_FD, msg_syscall, sizeof(msg_syscall)); 
    if (r == -1) {
        perror("syscall");
    }

    
    // ========= TASK 1.2 ==============
    const char msg_my_write[] = "my_write: hello, world!\n";
    r = my_write(STDOUT_FD, msg_my_write, sizeof(msg_my_write));
    if (r == 1) {
        perror("my_write");
    }
    return 0;
}
