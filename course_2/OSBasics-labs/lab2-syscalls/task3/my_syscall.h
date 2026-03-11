#ifndef MY_SYSCALL_H
#define MY_SYSCALL_H 1

#define MY_NR_write 1
#define MY_NR_exit 60

#define my_syscall1(syscall_no, a1) \
    my_syscall((long)syscall_no, (long)(a1), 0, 0, 0, 0, 0)

#define my_syscall2(syscall_no, a1, a2) \
    my_syscall((long)syscall_no, (long)(a1), (long)(a2), 0, 0, 0, 0)

#define my_syscall3(syscall_no, a1, a2, a3) \
    my_syscall((long)syscall_no, (long)(a1), (long)(a2), (long)(a3), 0, 0, 0)

#define my_syscall4(syscall_no, a1, a2, a3, a4) \
    my_syscall((long)syscall_no, (long)(a1), (long)(a2), (long)(a3), (long)(a4), 0, 0)

#define my_syscall5(syscall_no, a1, a2, a3, a4, a5) \
    my_syscall((long)syscall_no, (long)(a1), (long)(a2), (long)(a3), (long)(a4), (long)(a5), 0)

#define my_syscall6(syscall_no, a1, a2, a3, a4, a5, a6) \
    my_syscall((long)syscall_no, (long)(a1), (long)(a2), (long)(a3), (long)(a4), (long)(a5), (long)(a6))

#endif
