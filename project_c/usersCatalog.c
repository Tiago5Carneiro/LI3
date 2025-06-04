#include "usersCatalog.h"
#include <stdio.h>
#include <stdlib.h>

/** CompUser **/

typedef struct compUser{
	User u; //User
	bool international; //Visitou 1 ou mais estados
	GPtrArray *reviews;	//Array dinamico com review ids
} *CompUser;

CompUser init_compUser(User u){
	CompUser cpU = (CompUser) malloc(sizeof(struct compUser));
	cpU->u = u;
	cpU->international = false;
	cpU->reviews = g_ptr_array_new_with_free_func((GDestroyNotify) free);
	return cpU;
}

void free_compUser(CompUser cpU){
	free_user(cpU->u);
	g_ptr_array_free(cpU->reviews,TRUE);
	free(cpU);
}

User get_compUser_user(CompUser cpU){
	return clone_user(cpU->u);
}

char* get_compUser_id(CompUser cpU){
	return get_user_id(cpU->u);
}

bool get_compUser_international(CompUser cpU){
	return cpU->international;
}

char *strdup_gcopyfunc(char *str, __attribute__((unused)) void *lixo){
	return strdup(str);
}

GPtrArray *get_compUser_reviews(CompUser cpU){
	GPtrArray *clone = g_ptr_array_copy(cpU->reviews,(GCopyFunc) strdup_gcopyfunc, NULL);
	g_ptr_array_set_free_func(clone, (GDestroyNotify) free);
	return clone;
}

unsigned int get_compUser_reviewCount(CompUser cpU){
	return cpU->reviews->len;
}
/*
char *get_compUser_review_businessid_by_index(CompUser cpU, guint index){
	if(index < cpU->reviews->len)
		return get_review_businessid(g_ptr_array_index(cpU->reviews, index));
	return NULL;
}*/

char *get_compUser_review_id_by_index(CompUser cpU, guint index){
	if(index < cpU->reviews->len)
		return strdup(g_ptr_array_index(cpU->reviews, index));
	return NULL;
}

void insert_compUser_international(CompUser cpU, bool b){
	cpU->international = b;
}


/** UserCatalog **/

struct _UserCatalog{
	GHashTable *ht;
};

UserCatalog userCatalog_init(){
	UserCatalog uc = (UserCatalog) malloc(sizeof(struct _UserCatalog));
	uc->ht = g_hash_table_new_full(g_str_hash,g_str_equal,(GDestroyNotify) free, (GDestroyNotify)free_compUser);
	return uc;
}

void userCatalog_Free(UserCatalog uc){
	if(uc != NULL) {
		g_hash_table_destroy(uc->ht);
		free(uc);
	}
}

void userCatalog_Insrt(UserCatalog uc, CompUser cpU){
	g_hash_table_insert(uc->ht,get_compUser_id(cpU),cpU);
}

int userCatalog_contains(UserCatalog uc, char *user_id){
	if(g_hash_table_contains(uc->ht, user_id) == TRUE) return 1;
	return 0;
}

// Dado um user id e um index, procura no usercatalog o compUser, e retorna o review id com indice 'index'
char* userCatalog_get_compUser_review_id_by_index(UserCatalog uc,char* user_id, unsigned int index){
	CompUser cpU = g_hash_table_lookup(uc->ht, user_id);
	return get_compUser_review_id_by_index(cpU, index);
}

// Dado um user id, retorna a flag 'international'
bool userCatalog_get_compUser_international(UserCatalog uc,char* user_id){
	CompUser cpU = g_hash_table_lookup(uc->ht, user_id);
	return get_compUser_international(cpU);
}

// Dado um user id e um index, procura no usercatalog o compUser, e altera a flag 'international'
void userCatalog_insert_compUser_international(UserCatalog uc, char* user_id, bool international){
	CompUser cpU = g_hash_table_lookup(uc->ht, user_id);
	return insert_compUser_international(cpU, international);
}

// Adiciona o review_id á lista de review ids de um compUser
void userCatalog_insert_reviewID(UserCatalog uc, char *user_id, char *review_id){
	CompUser cpU = g_hash_table_lookup(uc->ht, user_id);	
	g_ptr_array_add(cpU->reviews, strdup(review_id));
}

//Retorna um GPtrArray* com os review ids presentes numa estrutura CompBusiness 
GPtrArray *userCatalog_get_user_list_of_review_ids(UserCatalog uc, char *user_id){

	//Gets compUser's list of review ids
	CompUser cpU = g_hash_table_lookup(uc->ht, user_id);

	if(cpU == NULL) return NULL;

	//Clones the list of review ids
	GPtrArray *clone = get_compUser_reviews(cpU); 

	return clone;
}

//Retorna lista de ids de utilizadores que tenham feito reviews de negócios de vários estados diferentes
GPtrArray *hash__international_user(UserCatalog uc){
	GPtrArray *array_users = g_ptr_array_new_with_free_func((GDestroyNotify) free);
	GList *list_users      = g_hash_table_get_values(uc->ht);

	for(GList *l = list_users; l != NULL ; l = l->next){
        bool international = get_compUser_international(l->data);
		if(international == TRUE) g_ptr_array_add(array_users, get_compUser_id(l->data));
	}

	g_list_free(list_users);
	return array_users;
}


/** Load User File **/

int load_user_file (char *path, UserCatalog uc){
	//Declaracoes iniciais para usar getline
    char   *buffer    = NULL;
    size_t tam_buffer = 0;

    int n; //Usado para guardar quantos caracteres foram lidos pelo getline
    FILE *fp;
    User u; 
    CompUser cpU;
    
    if((fp = fopen(path,"r")) == NULL) {
        perror("Erro ao abrir ficheiro dos utilizadores");
        return -1;
    }
    
    while((n = getline(&buffer,&tam_buffer,fp)) > 0){
        if(buffer[n-1] == '\n') buffer[n-1] = '\0';
        if((u = str_to_user(buffer)) != NULL) {
            cpU = init_compUser(u);
            userCatalog_Insrt(uc,cpU);
        }
    }

    fclose(fp); free(buffer);
    return 0;
}