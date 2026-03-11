void __stack_chk_fail() {

}

void exit(int code) {
    long long result;
    asm volatile(
     "\tsyscall\n"
        : "=a" (result)
        : "a" (60), "D" (code) 
        : "rcx", "r8", "r9", "r10", "r11", "memory", "cc" 
    );
   
}

long long my_write(int fd, const void* buf, unsigned long count) {
    long long result; 
    
    /* 
     * 1. Кладём номер сискола в rax. fd -> rdi, buf -> rsi, count -> rdx
     * 2. Проц выполняет инструкцию syscall
     * 3. Результат сискола из rax помещается в result
    */
    asm volatile(
     "\tsyscall\n"
        : "=a" (result)
        : "a" (1), "D" (1), "S" (buf), "d" (count) // a - rax, D - rdi, S - rsi, d - rdx
        : "rcx", "r8", "r9", "r10", "r11", "memory", "cc" 
    );
    if (-4095 <= result && result < 0) {
        return -1;
    }
    return result;
}

void _start() {

    long long r;    
   
    // ========= TASK 1.2 ==============
    const char msg_my_write[] = "my_write: hello, world!\n";
    r = my_write(1, msg_my_write, sizeof(msg_my_write));
    exit(0);
}
