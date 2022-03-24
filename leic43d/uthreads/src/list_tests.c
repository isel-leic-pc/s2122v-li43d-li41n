#include <stdio.h>
#include <stdlib.h>
#include "list.h"

typedef struct int_list_node {
    int value;
    list_entry_t links;    
} int_list_node_t;


int_list_node_t* create_int_list_node(int value) {
    int_list_node_t* pnode = malloc(sizeof(int_list_node_t));
    pnode->value = value;
    return pnode;
}

void print_list(list_entry_t * plist) {
    for (list_entry_t* pcurrent = plist->next; pcurrent != plist; pcurrent = pcurrent->next) {
        int_list_node_t * pint_list_node = container_of(pcurrent, int_list_node_t, links);
		printf("value = %d; ", pint_list_node->value);
    }
    putchar('\n');
}

int compare_int_list_nodes(list_entry_t *first, list_entry_t *second) {
    return container_of(first, int_list_node_t, links)->value - 
        container_of(second, int_list_node_t, links)->value;
}

void test_sorted() {
    list_entry_t sorted_list;
    init_list(&sorted_list);

    insert_at_list_sorted_by(&sorted_list, &create_int_list_node(3)->links, compare_int_list_nodes);
    insert_at_list_sorted_by(&sorted_list, &create_int_list_node(1)->links, compare_int_list_nodes);
    insert_at_list_sorted_by(&sorted_list, &create_int_list_node(4)->links, compare_int_list_nodes);
    insert_at_list_sorted_by(&sorted_list, &create_int_list_node(2)->links, compare_int_list_nodes);
    insert_at_list_sorted_by(&sorted_list, &create_int_list_node(5)->links, compare_int_list_nodes);

    print_list(&sorted_list);
}

int main() {

    test_sorted();

    list_entry_t list;
    init_list(&list);

    insert_at_list_tail(&list, &create_int_list_node(1)->links);
    insert_at_list_tail(&list, &create_int_list_node(2)->links);
    insert_at_list_tail(&list, &create_int_list_node(3)->links);
    print_list(&list);

    remove_from_list_head(&list);
    print_list(&list);

    insert_at_list_head(&list, &create_int_list_node(1)->links);
    insert_at_list_head(&list, &create_int_list_node(2)->links);
    insert_at_list_head(&list, &create_int_list_node(3)->links);
    print_list(&list);

    remove_from_list_head(&list);
    print_list(&list);
}