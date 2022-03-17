#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>

#define STACK_SIZE (8 * 4096)

typedef struct uthread_context {
    uint64_t r15;
    uint64_t r14;
    uint64_t r13;
    uint64_t r12;
    uint64_t rbx;
    uint64_t rbp;
    void (*ret_address)();
} uthread_context_t;

typedef struct uthread {
    uint64_t rsp;
    uint8_t stack[STACK_SIZE];
} uthread_t;

extern void context_switch(uthread_t * pthread1, uthread_t * pthread2);

void ut_prepare_thread(uthread_t * pthread, void (*thread_code)()) {
    uthread_context_t* pctx = (uthread_context_t*)
        ((pthread->stack + STACK_SIZE) - sizeof(uthread_context_t));
    pctx->rbp = 0;
    pctx->ret_address = thread_code;
    pthread->rsp = (uint64_t) pctx;    
}

uthread_t thread1;
uthread_t thread2;
uthread_t thread3;

uthread_t main_thread;

void thread1_code() {
    puts("T1: step 1");
    context_switch(&thread1, &thread2);
    puts("T1: step 2");
    context_switch(&thread1, &thread2);
    puts("T1: step 3");
    context_switch(&thread1, &thread2);
}

void thread2_code() {
    puts("T2: step 1");
    context_switch(&thread2, &thread3);
    puts("T2: step 2");
    context_switch(&thread2, &thread1);
    puts("T2: step 3");
    context_switch(&thread2, &main_thread);
}

void thread3_code() {
    puts("T3: single step");
    context_switch(&thread3, &thread1);
}

int main(int argc, char *argv[]) {

    puts("main: It begins");

    ut_prepare_thread(&thread1, thread1_code);
    ut_prepare_thread(&thread2, thread2_code);
    ut_prepare_thread(&thread3, thread3_code);

    context_switch(&main_thread, &thread1);

    puts("main: It ends");

    return 0;
}