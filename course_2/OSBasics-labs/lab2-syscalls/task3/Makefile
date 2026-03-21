
all: my_strace

my_strace: my_strace.c hello_static.c
	gcc my_strace.c -o my_strace
	gcc -nolibc -nostdlib hello_static.c -o hello_static

clean:
	rm -f my_strace hello_static

.PHONY: all clean
