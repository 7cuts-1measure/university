#include "my_syscall.h"
#include <stdlib.h>

void __stack_chk_fail() {} /* idk what this function should do, but without them it does not compile... */

long errno = 0;

long my_syscall(long syscall_no, long a1, long a2, long a3, 
                                  long a4, long a5, long a6) {
    long result;
    register long r10 asm("r10") = a4;
    register long r8  asm("r8")  = a5;
    register long r9  asm("r9")  = a6;
    asm volatile (
        "syscall"
        : "=a" (result)
        : "a" (syscall_no), "D" (a1), "S" (a2), "d" (a3), "r" (r10), "r" (r8), "r" (r9)
        : "rcx", "r11", "memory" 
    );
    if (-4095 <= result && result < 0) {
        errno = -result;
        result = -1;
    }
    return result;
}

void exit(int code) {
    my_syscall1(MY_NR_exit, code); } /* ignore warning: there is syscall exit before this code */

long my_write(int fd, const void* buf, long count) {
    return my_syscall3(MY_NR_write, fd, buf, count);
}

void _start() {

    long r;    
   
    // ========= TASK 1.2 ==============
    const char msg_my_write[] = "my_write: hello, world!\n";
    r = my_write(1, msg_my_write, sizeof(msg_my_write));
    exit(0);
}
