all: fp_lib main update_links copy_lib

CFLAGS=-g

fp_lib: fp.c
	gcc $(CFLAGS) -fPIC -shared fp.c -o libfp.so
	

main: fp_lib main.c
	gcc $(CFLAGS) main.c -o main

update_links:
	./update_links.sh

copy_lib: fp_lib
	cp libfp.so ./programs

clean:
	rm -f main libfp.so fp.so ./programs/libfp.so ./programs/fp

.PHONY: all clean update_links copy_lib fp_lib