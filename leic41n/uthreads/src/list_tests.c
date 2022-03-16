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

int main() {

    list_entry_t list;
    init_list(&list);

    insert_at_list_head(&list, &create_int_list_node(1)->links);
    insert_at_list_head(&list, &create_int_list_node(2)->links);
    insert_at_list_head(&list, &create_int_list_node(3)->links);

    for (list_entry_t* pcurrent = list.next; pcurrent != &list; pcurrent = pcurrent->next) {
        int_list_node_t * pint_list_node = container_of(pcurrent, int_list_node_t, links);
		printf("value = %d\n", pint_list_node->value);
    }
    
}