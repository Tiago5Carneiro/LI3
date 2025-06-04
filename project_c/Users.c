#include <stdio.h>
#include <stdlib.h>
#include <glib.h>
#include "Users.h"

struct user {
    char *user_id;
    char *name;
    GPtrArray *friends;
};

User str_to_user(char *buffer){
    User u = (User) malloc(sizeof(struct user));
    char *friend_id;

    u->user_id = strsep(&buffer,";");
    u->name    = strsep(&buffer,";");
    if(strlen(u->user_id)!=22 || !u->name){
        free(u);
        return NULL;
    }
    u->user_id = strdup(u->user_id);
    u->name    = strdup(u->name);
    u->friends = g_ptr_array_new_with_free_func((GDestroyNotify) free);

    while((friend_id = strsep(&buffer,",")) != NULL){
        friend_id = strdup(friend_id);
        g_ptr_array_add(u->friends, friend_id);
    }

    if(u->friends->len <= 0){
        free_user(u);
        return NULL;
    }
    return u;
}

User clone_user(User u){
    char *friend;
    User new     = (User) malloc(sizeof(struct user));
    new->user_id = strdup(u->user_id);
    new->name    = strdup(u->name);
    new->friends = g_ptr_array_new_with_free_func((GDestroyNotify) free);
    
    for(guint i = 0; i < u->friends->len; i++){
        friend = strdup(g_ptr_array_index(u->friends, i));
        g_ptr_array_add(new->friends, friend);
    }
    return new;
}

char *get_user_id(User u){
    return strdup(u->user_id);
}

char *get_user_name(User u){
    return strdup(u->name);
}

/* Não esquecer de dar free */
char *get_user_friend_id_by_index(User u, unsigned int index){
    if(index < u->friends->len)
        return strdup(g_ptr_array_index(u->friends, index));
    return NULL;
}

unsigned int get_user_number_of_friends(User u){
    return u->friends->len;
}

void print_user(User u){
    if(u) {
        printf("User id: %s    Name: %s    ",u->user_id,u->name);
        for(unsigned int i = 0; i < u->friends->len; i++)
            printf("Friend[%d]: %s    ",i + 1,(char*)g_ptr_array_index(u->friends,i));
        printf("\n");
    }
}

void free_user(User u){
    free(u->user_id);
    free(u->name);
    if(u->friends != NULL)
        g_ptr_array_free(u->friends, TRUE);
    free(u);
}