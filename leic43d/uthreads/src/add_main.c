#include <stdio.h>
#include <stdlib.h>

extern int add_asm(int, int);

// X86-64 calling conventions
// https://en.wikipedia.org/wiki/X86_calling_conventions

int main(int argc, char *argv[]) {
    // No args verification. This crashes big time ;)
    int arg1 = atoi(argv[1]), arg2 = atoi(argv[2]);
    int result = add_asm(arg1, arg2);
    printf("add_asm(%d, %d) = %d\n", arg1, arg2, result);
    printf("Result's address is %p", (void*) &result);
    puts("\nPress any key to finish");
    getchar();
    printf("Lets look at the address of result again: %p\n", (void*) &result);
    return 0;
}