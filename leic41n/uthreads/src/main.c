#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>

#include "uthread.h"
#include "list.h"

///////////////// SLEEPER DEMO

void sleeper(void *args) {
    char * id = (char*) args;
    printf("%s: starts\n", id);

    for(int i=0; i<5; ++i) {
        printf("%s: Going to sleep\n", id);
        ut_sleep(5);
        printf("%s: done\n", id);
    }
    printf("%s: ends\n", id);
}

void run_sleeper_demo() {
    puts("main: SLEEPER DEMO");
    ut_create(sleeper, "T0");
    ut_run();
}

///////////////// STEPPING DEMO

void do_steps(void * args) {
    char * id = (char*) args;
    printf("%s: step 1\n", id);
    ut_yield();
    printf("%s: step 2 (sleeping now)\n", id);
    ut_sleep(1);
    printf("%s: step 3\n", id);
}

void say_hi(void *args) {
    char * id = (char*) args;
    printf("%s: Hi all! (Going to sleep)\n", id);
    ut_sleep(10);
    printf("%s: Bye!\n", id);
}

void run_stepping_demo() {

    puts("main: STEPPING DEMO");
    ut_create(do_steps, "T1");
    ut_create(do_steps, "T2");
    ut_create(say_hi, "T3");
    ut_run();
}

///////////////// WORKERS DEMO

typedef struct participants_args {
    char * id;
    ut_latch_t *latch;
} participants_args_t;

void boss(void * args) {
    participants_args_t * boss_args = (participants_args_t *) args;
    printf("%s: Zzzzzzzz Zzzzzzzz\n", boss_args->id);
    ut_latch_await(boss_args->latch);
    printf("%s: All done ?!?! Great!\n", boss_args->id);
}

void worker(void * args) {
    participants_args_t * worker_args = (participants_args_t *) args;
    printf("%s: Grinding!\n", worker_args->id);
    ut_sleep(10);
    printf("%s: Done! And now, Elden Ring FTW\n", worker_args->id);
    ut_latch_count_down(worker_args->latch);
}


#define MAX_ID_LENGHT  10

void run_countdown_demo() {

    puts("main: COUNT_DOWN DEMO");

    ut_latch_t latch;
    int worker_count = 2;
    ut_latch_init(&latch, worker_count);

    participants_args_t boss_args;
    boss_args.id = "BOSS";
    boss_args.latch = &latch;
    ut_create(boss, &boss_args);

    participants_args_t worker_args[worker_count];
    for(int i = 0; i < worker_count; ++i) {
        char * id = malloc(MAX_ID_LENGHT + 1);
        sprintf(id, "W%d", i + 1);
        worker_args[i].id = id;
        worker_args[i].latch = &latch;
        ut_create(worker, &worker_args[i]);
    }

    ut_run();

    for(int i = 0; i < worker_count; ++i) {
        free(worker_args[i].id);
    }
}

int main() {

    puts("main: DEMO STARTS");
    ut_init();

    // run_stepping_demo();
    // run_sleeper_demo();
    run_countdown_demo();

    ut_end();
    puts("main: DEMO ENDS");
    return 0;
}