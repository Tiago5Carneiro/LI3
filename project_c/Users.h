#ifndef USERS
#define USERS

typedef struct user *User;

User str_to_user(char *buffer);

User clone_user(User b);

char *get_user_id(User u);

char *get_user_name(User u);

char *get_user_friend_id_by_index(User u, unsigned int index);

unsigned int get_user_number_of_friends(User u);

void print_user(User u);

void free_user (User u);

#endif