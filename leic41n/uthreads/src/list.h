/**
 * @brief List implementation using a double-linked circular list with sentinel.
 */

#ifndef LIST_H
#define LIST_H

#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>

/**
 * Macro used to compute an offset for the field 'field' in an instance of type 'type' 
 * (defined by a struct) located at address 'address'
 */
#ifndef container_of
#define container_of(address, type, field) \
	((type *)((uint8_t *)(address) - (size_t)(&((type *)0)->field)))

#endif

/**
 * @brief The list entry, containing a forward link (next) and a backward link (prev).
 */
typedef struct list_entry {
    struct list_entry *next;
    struct list_entry *prev;
} list_entry_t;

/**
 * @brief Initializes the list, so that it's an empty list.
 * @param head  The list's head
 */
// The attribute always_inline forces inline even without compilation optimizations
__attribute__((always_inline))
inline void init_list(list_entry_t* head) {
    head->next = head->prev = head;
}

/**
 * @brief Checks whether the list is empty or not.
 * @param head   The list's head
 * @return A bool value indicating if the list starting at head is empty or not.
 */
// The attribute always_inline forces inline even without compilation optimizations
__attribute__((always_inline))
inline bool is_empty(list_entry_t* head) {
    return head->next == head;
}

/**
 * @brief Inserts the given entry to the head of the list.
 * @param head   The list's head
 * @param entry  The entry to be added
 */
// The attribute always_inline forces inline even without compilation optimizations
__attribute__((always_inline))
inline void insert_at_list_head(list_entry_t * head, list_entry_t * entry) {
    entry->next = head->next;
    entry->prev = head;
    entry->next->prev = entry;
    head->next = entry;
}

/**
 * @brief Inserts the given entry at the end of the list.
 * @param head   The list's head
 * @param entry  The entry to be added
 */
// The attribute always_inline force inline even without compiling with optimizations
__attribute__((always_inline))
inline void insert_at_list_tail(list_entry_t * head, list_entry_t * entry) {
    entry->next = head;
    entry->prev = head->prev;
    entry->prev->next = entry;
    head->prev = entry;
}

/**
 * @brief Inserts the given entry to the list according to the given sorting criteria.
 * @param head      The list's head
 * @param entry     The entry to be added
 * @param compare   The function used to compare two list entries.
 */
__attribute__((always_inline))
inline void insert_at_list_sorted_by(
    list_entry_t * head, 
    list_entry_t * entry, 
    int (*compare)(list_entry_t*, list_entry_t*)
) {
    list_entry_t *current = head->next;
    while (current != head && compare(current, entry) <= 0) {
        current = current->next;
    }
    entry->next = current;
    entry->prev = current->prev;
    entry->prev->next = entry;
    current->prev = entry;
}

/**
 * @brief Removes the entry at the head of the list.
 * @param head The list's head
 * @return The removed entry, or NULL if the list is empty
 */
// The attribute always_inline forces inline even without compilation optimizations
__attribute__((always_inline))
inline list_entry_t* remove_from_list_head(list_entry_t * head) {
    if (is_empty(head)) return NULL;
    list_entry_t* to_remove = head->next;
    head->next = head->next->next;
    head->next->prev = head;
    return to_remove;
}

/**
 * @brief Gets the first element from the list, without removing it (peek).
 * @param head The list's head
 * @return The first elment or NULL if the list is empty
 */
// The attribute always_inline forces inline even without compilation optimizations
__attribute__((always_inline))
inline list_entry_t* get_first_from_list(list_entry_t * head) {
    if (is_empty(head)) return NULL;
    return head->next;
}

#endif