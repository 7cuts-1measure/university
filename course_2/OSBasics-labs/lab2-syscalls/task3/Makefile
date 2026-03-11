
all: main

main: main.c hello_static.c
	gcc main.c -o main
	gcc -nolibc -nostdlib hello_static.c -o hello_static

clean:
	rm -f main hello_static

.PHONY: all clean
