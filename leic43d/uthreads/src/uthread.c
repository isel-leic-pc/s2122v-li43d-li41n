#include <stdlib.h>
#include "uthread.h"

extern void context_switch(uthread_t * pthread1, uthread_t * pthread2);

/**
 * @brief The uthread used to represent the special case of termination. When this uthread
 * finally runs it means that all uthreads have terminated, and therefore the execution will end.
 */
uthread_t main_thread;

list_entry_t ready_queue;

uthread_t * running_uthread;

/**
 * @brief Gets the next uthread to be executed, removing it from the ready queue.
 */
uthread_t* remove_next_ready_thread() {
    return is_empty(&ready_queue) ? &main_thread : 
        container_of(remove_from_list_head(&ready_queue), uthread_t, links);
}

uthread_t* ut_create(void (*thread_code)()) {
    uthread_t * uthread = malloc(STACK_SIZE);
    uthread_context_t* pctx = (uthread_context_t*)
        (((uint8_t*) uthread + STACK_SIZE) - sizeof(uthread_context_t));
    pctx->rbp = 0;
    pctx->ret_address = thread_code;
    uthread->rsp = (uint64_t) pctx;
    
    insert_at_list_tail(&ready_queue, &(uthread->links));
    return uthread;
}

void ut_exit() {
    context_switch(running_uthread, remove_next_ready_thread());
}

void ut_yield() {
    if (!is_empty(&ready_queue)) {
        insert_at_list_tail(&ready_queue, &(running_uthread->links));
        context_switch(running_uthread, remove_next_ready_thread());
    }
}

void ut_init() {
    init_list(&ready_queue);
}

void ut_run() {
    running_uthread = &main_thread;
    context_switch(running_uthread, remove_next_ready_thread());
}

void ut_end() {
    // TODO
}