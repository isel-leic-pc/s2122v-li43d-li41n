/**
 * @brief The public interface of the uthread library
 */

#ifndef UTHREAD_H
#define UTHREAD_H

#include <stdint.h>
#include "list.h"

typedef struct uthread uthread_t;

/**
 * @brief Creates a uthread with the specified behaviour.
 * @param thread_code   The uthread's behaviour (its code)
 * @param args          The uthread's arguments
 * @return the descriptor of the created uthread
 */
uthread_t* ut_create(void (*thread_code)(), void *args);

/**
 * @brief Terminates the calling uthread.
 */
void ut_exit();

/**
 * @brief Hands over the "processor" (the right to execute) to another uthread.
 */
void ut_yield();

/**
 * @brief Suspends execution of the calling uthread for at least delay seconds.
 */
void ut_sleep(uint8_t delay);

/**
 * @brief Initializes the uthread runtime.
 */
void ut_init();

/**
 * @brief Called by the main thread of the process (an OS thread) so that its used as the "processor" 
 * for all uthreads. All uthreads will execute in the OS thread that calls this function. 
 * The function returns when all uthreads terminate.
 */
void ut_run();

/**
 * @brief Used to cleanup the uthreads runtime.
 */
void ut_end();

//////////////////////// The supported synchronizers

/**
 * @brief A couwnt down latch.
 */
typedef struct ut_latch {
    uint32_t units;
    list_entry_t wait_queue;
} ut_latch_t;

/**
 * @brief Initializes the count down latch with the given initial units.
 */
void ut_latch_init(ut_latch_t* latch, uint32_t initial_units);

/**
 * @brief Blocks the calling uthread until the count reaches 0.
 */
void ut_latch_await(ut_latch_t* latch);

/**
 * @brief Decrements the number of units held by the latch. If the count reaches 0, unblocks 
 * all waiting uthreads.
 */
void ut_latch_count_down(ut_latch_t* latch);

#endif