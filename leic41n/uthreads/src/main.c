#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>

#define STACK_SIZE (8 * 4096)
typedef struct uthread {
    uint64_t rsp;
    uint8_t stack[STACK_SIZE];
} uthread_t;

typedef struct uthread_context {
    uint64_t r15;
    uint64_t r14;
    uint64_t r13;
    uint64_t r12;
    uint64_t rbx;
    uint64_t rbp;
    void (*ret_address)();
} uthread_context_t;

extern void context_switch(uthread_t* pthread_out, uthread_t* pthread_in);

uthread_t thread1;
uthread_t thread2;

uthread_t main_thread;

void ut_prepare(uthread_t* pthread, void (*thread_code)()) {
    uthread_context_t* pctx = (uthread_context_t*)
        ((pthread->stack + STACK_SIZE) - sizeof(uthread_context_t));
    pctx->rbp = 0;
    pctx->ret_address = thread_code;
    pthread->rsp = (uint64_t) pctx;
}

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
    context_switch(&thread2, &thread1);
    puts("T2: step 2");
    context_switch(&thread2, &thread1);
    puts("T3: step 3");
    context_switch(&thread1, &main_thread);
}

int main() {

    puts("main: And so it begins");
    ut_prepare(&thread1, thread1_code);
    ut_prepare(&thread2, thread2_code);

    context_switch(&main_thread, &thread1);
    puts("main: the end is near");

    return 0;
}