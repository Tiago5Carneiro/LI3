#ifndef USERSCATALOG
#define USERSCATALOG

#include <glib.h>
#include <stdbool.h>
#include "Users.h"

/** User Catalog **/

typedef struct _UserCatalog *UserCatalog; 

//Inits the userCatalog
UserCatalog userCatalog_init();

//Frees the memory allocated for the catalog
void userCatalog_Free(UserCatalog uc);

//Verifica se existe um user com este id no catálogo
int userCatalog_contains(UserCatalog uc, char *user_id);

GPtrArray *userCatalog_get_user_list_of_review_ids(UserCatalog uc, char *user_id);

char* userCatalog_get_compUser_review_id_by_index(UserCatalog uc,char* user_id, unsigned int index);

bool userCatalog_get_compUser_international(UserCatalog uc,char* user_id);

void userCatalog_insert_compUser_international(UserCatalog uc, char* user_id, bool international);

void userCatalog_insert_reviewID(UserCatalog uc, char *user_id, char *review_id);

//Retorna lista de ids de utilizadores que tenham feito reviews de negócios de vários estados diferentes
GPtrArray *hash__international_user(UserCatalog uc);


/** Load User File **/

int load_user_file (char *path, UserCatalog uc);

#endif