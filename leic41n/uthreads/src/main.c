#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>

#include "uthread.h"

void do_step(void * args) {
    char * id = (char *) args;
    printf("%s: step 1\n", id);
    ut_yield();
    printf("%s: step 2\n", id);
    ut_yield();
    printf("%s: step 3\n", id);
}

void sayHi(void * args) {
    char * id = (char *) args;
    printf("%s: Hi all!\n", id);
}

int main() {

    ut_init();
    puts("main: And so it begins");

    ut_create(do_step, "T1");
    ut_create(do_step, "T2");
    ut_create(sayHi, "Greeter");

    ut_run();

    puts("main: the end is near");
    ut_end();

    return 0;
}