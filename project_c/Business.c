#include <stdlib.h>
#include <stdio.h>
#include "Business.h"
#include <glib.h>
#include <ctype.h>

struct business {
    char *business_id;
    char *name;
    char *city;
    char *state;
    GPtrArray *categories;
};

Business str_to_business(char *buffer){
    Business b       = (Business) malloc(sizeof(struct business)); if(b == NULL) return NULL;
    b->business_id   = strsep(&buffer,";");
    b->name          = strsep(&buffer,";");
    b->city          = strsep(&buffer,";");
    b->state         = strsep(&buffer,";");

    if(!b->business_id || !b->name || !b->city || !b->state || strlen(b->business_id)!=22 || !isalpha(b->name[0])) {
        free(b);
        return NULL;
    }
    b->business_id   = strdup(b->business_id) ;
    b->name          = strdup(b->name);
    b->city          = strdup(b->city);
    b->state         = strdup(b->state);

    char *categorie;
    b->categories    = g_ptr_array_new_with_free_func((GDestroyNotify) free);
    while((categorie = strsep(&buffer,",")) != NULL){
        categorie = strdup(categorie);
        g_ptr_array_add(b->categories, categorie);
    }
    
    if(b->categories->len <= 0){
        free_business(b);
        return NULL;
    }

    return b;
}

void free_business(Business b){
    free(b->business_id);
    free(b->name);
    free(b->city);
    free(b->state);
    if(b->categories != NULL)
        g_ptr_array_free(b->categories, TRUE);
    free(b);
}

void free_business_gfunc(Business b,__attribute__((unused)) void *lixo){
    free_business(b);
}

Business clone_business(Business b){
    char *categorie;
    Business new     = (Business) malloc(sizeof(struct business));
    new->business_id = strdup(b->business_id);
    new->name        = strdup(b->name);
    new->city        = strdup(b->city);
    new->state       = strdup(b->state);
    new->categories  = g_ptr_array_new_with_free_func((GDestroyNotify) free);
    for(guint i = 0; i < b->categories->len; i++){
        categorie = strdup(g_ptr_array_index(b->categories, i));
        g_ptr_array_add(new->categories, categorie);
    }
    return new;
}

Business clone_business_GCopyFunc(Business b,__attribute__((unused)) void *lixo){
    return clone_business(b);
}

char *get_business_id(Business b){
    return strdup(b->business_id);
}

char *get_business_name(Business b){
    return strdup(b->name);
}

char *get_business_city(Business b){
    return strdup(b->city);
}

char *get_business_state(Business b){
    return strdup(b->state);
}

char *get_business_category_by_index(Business b, unsigned int index){
    if(index < b->categories->len) return strdup(g_ptr_array_index(b->categories, index));
    return NULL;
}

guint get_business_nr_of_categories(Business b){
    return b->categories->len;
}

void print_business(Business b, __attribute__((unused)) void *lixo){
    if(b) {
        printf("Business id: %s    Name: %s    City: %s    State: %s    ", b->business_id, b->name, b->city, b->state);
        if(b->categories != NULL)
            for(guint i = 0; i < b->categories->len; i++)
                printf("Categories[%d]: %s    ",i + 1,(char*)g_ptr_array_index(b->categories,i));
        printf("\n\n");
    }
}