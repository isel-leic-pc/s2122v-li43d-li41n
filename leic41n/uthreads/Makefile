main: src/main.c src/add_asm.s
	mkdir -p bin
	gcc -Wall -pedantic -g -o bin/main src/main.c src/add_asm.s

list: src/list_tests.c src/list.h
	mkdir -p bin
	gcc -Wall -pedantic -g -o bin/list_tests src/list_tests.c src/list.h

clean:
	rm -rf bin

