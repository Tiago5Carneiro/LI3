#include <stdio.h>
#include <stdlib.h>
#include <glib.h>
#include "reviewsCatalog.h"
#include "Reviews.h"
#include <stdbool.h>
#include <ctype.h>
typedef struct book{
	GHashTable * ht;
}*Book;


struct _ReviewCatalog {
	GHashTable *ht;// hashtable de reviews
	Book bk;
};


/** Book **/

Book book_init(){
	Book bk = malloc(sizeof(struct book));
	bk->ht = g_hash_table_new(g_str_hash,g_str_equal);
	return bk;
}

void free_GPTR(char* key ,GPtrArray * r,__attribute__((unused)) void* lixo){
	free(key);
	g_ptr_array_free(r,TRUE);
}

void book_free(Book bk){
	if(bk->ht != NULL){
		g_hash_table_foreach(bk->ht,(GHFunc)free_GPTR,NULL);
		g_hash_table_destroy(bk->ht);
	}
	free(bk);
}

void insrt_word_in_book(GHashTable * ht, Review r, char * w){
	GPtrArray * revs;

	if ((revs = g_hash_table_lookup(ht,w))) {
		int i = (int)revs->len-1;
		Review aux_r = g_ptr_array_index(revs,i);
		if (aux_r != r) g_ptr_array_add(revs,r);
		free(w);
	}
	else{
		GPtrArray * revs = g_ptr_array_new();
		g_ptr_array_add(revs,r);
		g_hash_table_insert(ht,w,revs);
	}
}

void text_to_Hash(Book bk, Review r){
	char * aux_text = get_review_text(r);
	char * word;
	int word_start = 0;
	for (int i = 0;aux_text[i]!= '\0';i++){
		if (aux_text[i] == ' '  || ispunct(aux_text[i])){
			word = strndup(&aux_text[word_start],i-word_start);
			if (strlen(word)>0) insrt_word_in_book(bk->ht,r,word);
			else free(word);

			word_start = i+1;		
		}
	}
	free(aux_text);
}

GPtrArray ** word_in_hash(ReviewCatalog rc, char * word){
	GPtrArray * revs  = g_hash_table_lookup(rc->bk->ht,word);
	GPtrArray ** output = (GPtrArray **) malloc(sizeof(GPtrArray *));
	if (revs){
		GPtrArray * names = g_ptr_array_new_with_free_func((GDestroyNotify) free);
		for (guint i = 0;i<revs->len;i++){
			char * id = get_review_id(g_ptr_array_index(revs,i));
			g_ptr_array_add(names,id);
		}
		output[0] = names;
	 	return output;
	}
	else return NULL;
}

ReviewCatalog reviewCatalog_init(){
	ReviewCatalog rc = (ReviewCatalog) malloc(sizeof(struct _ReviewCatalog));
	rc->ht = g_hash_table_new_full(g_str_hash,g_str_equal,(GDestroyNotify) free, (GDestroyNotify)free_review);
	rc->bk = book_init();
	return rc;
}

void reviewCatalog_free(ReviewCatalog rc){
	if(rc != NULL) {
		g_hash_table_destroy(rc->ht);
		book_free(rc->bk);
		free(rc);
	}
}

void reviewCatalog_insert(ReviewCatalog rc, Review r){
	g_hash_table_insert(rc->ht,get_review_id(r),r);
}

char *reviewCatalog_get_business_id(ReviewCatalog rc, char *review_id){
	Review r = g_hash_table_lookup(rc->ht, review_id);
	if(r == NULL) return NULL;
	return get_review_businessid(r);
}

void compUser_international_check(UserCatalog uc, char* user_id, BusinessCatalog bc, ReviewCatalog rc, Review r){
	//Se um utilizador ainda não for internacional significa que todas as suas reviews pertencem ao mesmo estado. Basta entao comparar o estado da nova review com a primeira que exista no compUser.
	char *review_id_aux;
	if(userCatalog_get_compUser_international(uc, user_id) == true || (review_id_aux = userCatalog_get_compUser_review_id_by_index(uc, user_id, 0)) == NULL) return;

	//Encontra o estado do negócio ao qual foi feita a review
	char *bus_id     = get_review_businessid(r),
		 *bus_state  = get_business_state_by_id(bc, bus_id);

	char *bus_id_aux 	  = reviewCatalog_get_business_id(rc, review_id_aux),
		 *bus_state_aux   = get_business_state_by_id(bc, bus_id_aux); 

	//Se os estados forem diferentes, altera a flag 'international' presente no compUser
	if(strcmp(bus_state, bus_state_aux))
		userCatalog_insert_compUser_international(uc, user_id, true);

	free(bus_id); free(bus_state); free(review_id_aux); free(bus_id_aux); free(bus_state_aux);
}

//Adiciona uma review no catalogo caso o user id e o business id existam. Para alem disso tambem insere o review id no compUser (atualizando a flag 'international') e compBusiness respetivo.
int insert_review(UserCatalog uc, BusinessCatalog bc, ReviewCatalog rc, Review r){
	char *rev_bus_id  = get_review_businessid(r),
		 *rev_user_id = get_review_userid(r);

	//Verifica se o business id e o user id mencionados na review sao validos
	if(userCatalog_contains(uc, rev_user_id) == 0 || BusinessCatalog_contains(bc, rev_bus_id) == 0) {free(rev_bus_id); free(rev_user_id); free_review(r); return -1;}

	//Adiciona a review no catálogo
	reviewCatalog_insert(rc, r);

	//Insercao do review id e atualizacao do número de estrelas de um compBusiness
	char *review_id = get_review_id(r);
	BusinessCatalog_insert_reviewID(bc, rev_bus_id, review_id, get_review_stars(r));

	//Atualizacao da flag 'international' em compUser
	compUser_international_check(uc, rev_user_id, bc, rc, r);

	//Inserção da review na CompUser correspondente
	userCatalog_insert_reviewID(uc, rev_user_id, review_id);

	free(review_id);free(rev_bus_id);free(rev_user_id);
	return 0;
}

//Dado um review id, retorna o business id associado a essa review
char *reviewCatalog_get_business_id_of_review(ReviewCatalog rc, char *review_id){
	Review r = g_hash_table_lookup(rc->ht, review_id);
	return get_review_businessid(r);
}



//Load 
int load_review_file(char *path, UserCatalog uc, BusinessCatalog bc, ReviewCatalog rc){
	char *buffer = NULL;
	size_t tam_buffer = 0;
	int n;
	FILE *fp;

	if ((fp = fopen(path, "r")) == NULL)
	{
		perror("Erro ao abrir ficheiro das reviews");
		return -1;
	}

	Review r;
	int insert_sucess;
	while ((n = getline(&buffer, &tam_buffer, fp)) > 0)
	{	
		if (buffer[n - 1] == '\n')
			buffer[n - 1] = '\0';
		if ((r = str_to_review(buffer)) != NULL){
			insert_sucess = insert_review(uc, bc, rc, r); //As funções que inserem em estruturas têm de estar no ficheiro catalog respetivo
			if(insert_sucess != -1) text_to_Hash(rc->bk,r);
		}
	}
	fclose(fp);
	free(buffer);
	return 1;
}