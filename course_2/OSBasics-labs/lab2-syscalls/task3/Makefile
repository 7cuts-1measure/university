
all: my_strace

my_strace: my_strace.c hello_static.c
	gcc -g my_strace.c -o my_strace
	gcc -g -nolibc -nostdlib hello_static.c -o hello_static

clean:
	rm -f my_strace hello_static

.PHONY: all clean
