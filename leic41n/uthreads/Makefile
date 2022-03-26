main: src/main.c src/uthread.c src/ctx_switch.s
	mkdir -p bin
	gcc -Wall -pedantic -g -o bin/main src/main.c src/uthread.c src/ctx_switch.s

list: src/list_tests.c src/list.h
	mkdir -p bin
	gcc -Wall -pedantic -g -o bin/list_tests src/list_tests.c

add: src/add_main.c src/add_asm.s
	mkdir -p bin
	gcc -Wall -pedantic -g -o bin/add src/add_main.c src/add_asm.s

clean:
	rm -rf bin 

