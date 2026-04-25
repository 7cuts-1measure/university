#include <err.h>
#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#ifndef GLOBAL_ARR_SZ 
#define GLOBAL_ARR_SZ 4096
#endif

#define BAD_HELLO_BUFSIZE 100

int global_arr_small_inited[6] = {1, 2, 3, 4, 5, 6};
int global_arr_small[6];
int global_arr[GLOBAL_ARR_SZ];

const int global_const = 15;
int global_inited = 42;
int global_empty;

void foo() 
{
    const int  foo_const_var  = 20;
    static int foo_static_var = 15;
    int        foo_local_var  = 10;

    puts("----------------");
    printf("global_arr_small_inited:    %lx\n", (uintptr_t) global_arr_small_inited);
    printf("global_arr_small:           %lx\n", (uintptr_t) global_arr_small);
    
    printf("global_arr[0]:              %lx\n", (uintptr_t) &global_arr[0]);
    printf("global_arr[last]:           %lx\n", (uintptr_t) &global_arr[GLOBAL_ARR_SZ - 1]);
    
    printf("global_inited:              %lx\n", (uintptr_t) &global_inited);
    printf("global_empty:               %lx\n", (uintptr_t) &global_empty);
    printf("global_const:               %lx\n", (uintptr_t) &global_const);
    puts("");

    printf("foo_const_var:              %lx\n", (uintptr_t) &foo_const_var);
    printf("foo_stsatic_var:            %lx\n", (uintptr_t) &foo_static_var);
    printf("foo_local_var:              %lx\n", (uintptr_t) &foo_local_var);
    puts("----------------");
    
}


int *return_local() 
{
    int local = 69;
    return &local;
}

void bad_hello() 
{
    char *str = (char *) malloc(BAD_HELLO_BUFSIZE);
    
    if (!str)
        err(1, "cannot allocate buffer");
    
    sprintf(str, "hello world!");
    
    printf("before free: %s\n", str);
    free(str);
    printf("after free:  %s\n", str);

    char *str2 = (char *) malloc(BAD_HELLO_BUFSIZE);
    sprintf(str, "hello, str");
    sprintf(str2, "hello, str2");

    printf("str: %s\n", str);
    printf("str2: %s\n", str2);

    char *middle_str2 = str2 + BAD_HELLO_BUFSIZE / 2;
    free(middle_str2);

    printf("middle: %s\b", middle_str2);
    printf("start:  %s", str2);
}


void read_env(const char *name) 
{
    const char *value = getenv(name);
    if (!value) {
        warnx("var '%s' is undefined\n", name);
        return;
    }
    printf("%s is %s\n", name, value);
    printf("address of env_var: %p\n", value);

}

void write_and_read_env() 
{
    read_env("SAD");

    const char *new_value = "BIMBIM_BAMBAM";
    printf("setting to %s\n", new_value);

    setenv("SAD", new_value, true);
    read_env("SAD");

    
}

int main() 
{
    printf("My pid: %d\n", getpid());    
    //foo();

    //int *plocal = return_local();
    //printf("%d at %lx\n", *plocal, (uintptr_t)plocal);

    //bad_hello();
    
    write_and_read_env();
    
    while(1) {
        sleep(2000);
    }
    return 0;
}