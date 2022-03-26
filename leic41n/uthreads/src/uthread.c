/**
 * @brief The implementation of the uthread library
 */

#include <stdlib.h>
#include <unistd.h>
#include <time.h>
#include "uthread.h"
#include "list.h"

#define STACK_SIZE (8*4096)

typedef union wait_data_block {
    uint64_t value;
    void * block;
} wait_data_t;

/**
 * Defines the constitution of uthreads.
 */
typedef struct uthread {
    uint64_t rsp;
    void (*entry_point)(void*);
    void *args;
    wait_data_t wait_data;
    list_entry_t links;
} uthread_t;

/**
 * The uthreads context, stored at and restored from their stack each time they are switched.
 */
typedef struct uthread_context {
    uint64_t r15;
    uint64_t r14;
    uint64_t r13;
    uint64_t r12;
    uint64_t rbx;
    uint64_t rbp;
    void (*ret_address)();
} uthread_context_t;

/**
 * Switches execution from pthread1 to pthread2.
 */
extern void context_switch(uthread_t* pthread1, uthread_t* pthread2);

/**
 * Terminates the current uthread and switches execution to pthread.
 */
extern void internal_exit(uthread_t* pthread);

/**
 * @brief The list of uthreads that are READY to execute.
 */
list_entry_t ready_queue;

/**
 * @brief The running uthread.
 */
uthread_t * running_uthread;

/**
 * @brief The number of existing threads, regardless of their current state.
 */
uint32_t uthread_count;

/**
 * @brief The list of uthreads that are at SLEEP.
 */
list_entry_t sleep_queue;

/**
 * @brief The uthread used to represent the special case of termination. When this uthread
 * finally runs it means that all uthreads have terminated, and therefore the execution will end.
 */
uthread_t main_thread;

/**
 * @brief Gets the next uthread to be executed, removing it from the ready queue.
 */
uthread_t* remove_next_ready_thread() {
    return is_empty(&ready_queue) ? &main_thread : 
        container_of(remove_from_list_head(&ready_queue), uthread_t, links);
}

/**
 * @brief the actual uthread's entry point.
 */
void start_uthread() {
    running_uthread->entry_point(running_uthread->args);
    ut_exit();
}

/**
 * @brief Releases the uthread's allocated memory.
 */
void cleanup_uthread(uthread_t* puthread) {
    puts("Freeing up uthread's resources");
    free(puthread);
}

/**
 * @brief Helper function used to retrieve the current timestamp
 */
uint32_t get_time() {
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return ts.tv_sec;
}

/**
 * @brief Helper function used to compare two uthread_t nodes based on their sleep 
 * timestamp value. This presumes that the uthread is in the sleep queue.
 */
int compare_timestamps(list_entry_t *first, list_entry_t *second) {
    uint32_t firstTimeStamp = container_of(first, uthread_t, links)->wait_data.value;
    uint32_t secondTimeStamp = container_of(second, uthread_t, links)->wait_data.value;
    return firstTimeStamp - secondTimeStamp;
}

/**
 * @brief Move all uthreads that should awake from the sleep queue to the ready queue.
 */
void awake_sleepers() {
    while (!is_empty(&sleep_queue)) {
        list_entry_t * first = get_first_from_list(&sleep_queue);
        uint32_t now = get_time();
        uint32_t awake_at = container_of(first, uthread_t, links)->wait_data.value;
        if (now < awake_at) {
            break;
        } 
        insert_at_list_tail(&ready_queue, remove_from_list_head(&sleep_queue));
    }
}

/**
 * @brief Schedules the next READY uthread for execution.
 */
void schedule() {
    awake_sleepers();
    uthread_t * next_to_run = remove_next_ready_thread();
    context_switch(running_uthread, next_to_run);
}

//////////// Implementation of the public functions

uthread_t* ut_create(void (*thread_code)(), void * args) {
    uthread_t* pthread = malloc(STACK_SIZE);
    pthread->entry_point = thread_code;
    pthread->args = args;
    uthread_context_t* pctx = (uthread_context_t*)
        (((uint8_t*)pthread + STACK_SIZE) - sizeof(uthread_context_t));
    pctx->rbp = 0;
    pctx->ret_address = start_uthread;
    pthread->rsp = (uint64_t) pctx;

    insert_at_list_tail(&ready_queue, &pthread->links);
    uthread_count += 1;

    return pthread;
}

void ut_exit() {
    uthread_count -= 1;
    awake_sleepers();
    internal_exit(remove_next_ready_thread());
}

void ut_yield() {
    insert_at_list_tail(&ready_queue, &(running_uthread->links));
    schedule();
}

void ut_sleep(uint8_t delay) {
    uint32_t sleep_end = get_time() + delay;
    running_uthread->wait_data.value = sleep_end;
    insert_at_list_sorted_by(&sleep_queue, &running_uthread->links, compare_timestamps);
    schedule();
}

void ut_init() {
    init_list(&ready_queue);
    init_list(&sleep_queue);
    uthread_count = 0;
}

void ut_run() {
    running_uthread = &main_thread;
    while (uthread_count != 0) {
        schedule();
        sleep(1);
    }
}

void ut_end() {
    // No cleanup needed
}

//////////// Implementation of the supported synchronizers

/**
 * @brief Initializes the count down latch with the given initial units.
 */
void ut_latch_init(ut_latch_t* latch, uint32_t initial_units) {
    latch->units = initial_units;
    init_list(&latch->wait_queue);
}

/**
 * @brief Blocks the calling uthread until the count reaches 0.
 */
void ut_latch_await(ut_latch_t* latch) {
    if (latch->units != 0) {
        insert_at_list_tail(&latch->wait_queue, &running_uthread->links);
        schedule();
    }
}

/**
 * @brief Decrements the number of units held by the latch. If the count reaches 0, unblocks 
 * all waiting uthreads.
 */
void ut_latch_count_down(ut_latch_t* latch) {
    if (latch->units > 0) {
        latch->units -= 1;
        if (latch->units == 0) {
            while (!is_empty(&latch->wait_queue)) {
                list_entry_t *unblocked = remove_from_list_head(&latch->wait_queue);
                insert_at_list_tail(&ready_queue, unblocked);
            }
        }
    }    
}

