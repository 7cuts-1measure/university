#include <stdio.h>
#include <unistd.h>

int main(int argc, char *argv[]) 
{
    pid_t mypid = getpid();
    printf("my pid: %d\n", mypid);
    sleep(1);
    
    execv("./exec_self", argv);
    
    puts("hello world");
    return 0;
}