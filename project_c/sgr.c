#include "sgr.h"
#include <stdio.h>
#include <ctype.h>
#include "usersCatalog.h"
#include "businessCatalog.h"
#include "reviewsCatalog.h"


struct sgr{
    BusinessCatalog bc;
    UserCatalog uc;
	ReviewCatalog rc;
};

SGR init_sgr(){
	SGR sgr = (SGR) malloc(sizeof(struct sgr));
	sgr->bc = BusinessCatalog_init();
	sgr->uc = userCatalog_init();
	sgr->rc = reviewCatalog_init();
    return sgr;
}

void free_sgr(SGR sgr){
	if (sgr != NULL){
		BusinessCatalog_free(sgr->bc);
		userCatalog_Free(sgr->uc);
		reviewCatalog_free(sgr->rc);
	    free(sgr);
	}
}

/* query 1 */

SGR load_sgr(char *users, char *businesses, char *reviews){
	SGR sgr = init_sgr();

	if(load_user_file(users,sgr->uc) == -1) { free_sgr(sgr); return NULL; }

	if(load_business_file(businesses,sgr->bc) == -1) { free_sgr(sgr); return NULL; }

	if(load_review_file(reviews,sgr->uc,sgr->bc,sgr->rc) == -1) { free_sgr(sgr); return NULL; }

    return sgr;
}

/* query 2 */
TABLE businesses_started_by_letter(SGR sgr, char letter){
    GPtrArray *arr = BusinessCatalog_get_business_names_list_by_letter(sgr->bc, &letter);
	if(arr == NULL) return NULL;

	//Controi a tabela
	char *header[] = {"Nome"};	
	TABLE table = construct_table(&arr, header, 1);

	//Liberta a memória usada para o GPtrArray
    g_ptr_array_free(arr,TRUE);
	
    return table;
}

/* query 3 (PRECISA DE RECEBER UM ARRAY DE ARRAYS PARA OBEDECER ENCAPSULAMENTO)*/
TABLE business_info(SGR sgr, char *business_id){
	GPtrArray ** array = BusinessCatalog_get_business_info(sgr->bc, business_id);
	if(array == NULL) return NULL;

	//Cria a tabela
	char *headers[] = {"Nome", "Cidade", "Estado", "Estrelas", "Nr de Reviews"};	
	TABLE table = construct_table(array, headers, 5);

	//Liberta a memória usada pelo 'array'
	for(int i = 0; i < 5; i++) g_ptr_array_free(array[i],TRUE);
	free(array);

	return table;
}

/* query 4 */
TABLE businesses_reviewed(SGR sgr, char *user_id){
	//GPtrArray* array vai servir para inicialmente colocar os ids das reviews feitas pelo utilizador.
	//Posteriormente serão substituidas pelos ids dos negocios de cada review

	//Gets user's list of review ids
	GPtrArray* review_ids_array = userCatalog_get_user_list_of_review_ids(sgr->uc, user_id);
	
	//Verifica se o user existe. Caso seja NULL pode significar que o user não existe ou que não tem qualquer review
	if(review_ids_array == NULL) return NULL;

	//Dado que não se consegue substituir pointers nos GPtrArray*, é necessário criar um novo para inserir os businesses ids 
	GPtrArray *business_ids_array = g_ptr_array_new_with_free_func((GDestroyNotify) free);
	char *review_id, *business_id;
	for(unsigned int i = 0; i < review_ids_array->len ; i++){
		review_id   = g_ptr_array_index(review_ids_array, i);
		business_id = reviewCatalog_get_business_id_of_review(sgr->rc, review_id); 
		g_ptr_array_add(business_ids_array, business_id); 
	}

	//Gets array de GPtrArray* com as informações pretendidas(id e nome dos negócios)
	GPtrArray **clone_array;
	clone_array    = (GPtrArray **) malloc(sizeof(GPtrArray *) * 2);
	clone_array[0] = business_ids_array;
	clone_array[1] = g_ptr_array_new_with_free_func((GDestroyNotify) free); //Array dos names
	for(guint i = 0; i < review_ids_array->len; i++) 
		g_ptr_array_add(clone_array[1], BusinessCatalog_get_business_name(sgr->bc, g_ptr_array_index(clone_array[0], i)));

	//Cria a tabela
	char *headers[] = {"ID", "Nome"};
	TABLE table = construct_table(clone_array, headers, 2);

	//Libertação da memória usada pelos GPtrArrays
	g_ptr_array_free(review_ids_array,TRUE);
	for(int i = 0; i < 2; i++) g_ptr_array_free(clone_array[i],TRUE);
	free(clone_array);

	return table;
}

/* query 5 */
TABLE businesses_with_stars_and_city(SGR sgr, float stars, char *city){
	GPtrArray **array = get_businesses_above_n_stars_by_city (sgr->bc, city, stars);
	if(array == NULL) return NULL;

	//Cria a tabela
	char *headers[] = {"ID", "Nome"};
	TABLE table = construct_table(array, headers, 2);

	//Liberta a memória usada pelo 'array'
	for(int i = 0; i < 2; i++) g_ptr_array_free(array[i],TRUE);
	free(array);

	return table;
}

/* query 6 */
TABLE top_businesses_by_city(SGR sgr, int top){
	GPtrArray **array = get_top_businesses_from_all_cities (sgr->bc, (guint) top);
	if(array == NULL) return NULL;

	//Cria a tabela
	char *headers[] = {"ID", "Nome", "Estrelas"};
	TABLE table = construct_table(array, headers, 3);

	//Liberta a memória usada pelo 'array'
	for(int i = 0; i < 3; i++) g_ptr_array_free(array[i],TRUE);
	free(array);

	return table;
}

/* query 7 */
TABLE international_users(SGR sgr){
	GPtrArray *array = hash__international_user(sgr->uc);
	if(array == NULL) return NULL;

	//Cria a tabela
	char *headers[] = {"ID", "Nome", "Estrelas"};
	TABLE table = construct_table(&array, headers, 1);

	//Liberta a memória usada pelo 'array'
	g_ptr_array_free(array, TRUE);

	return table;
}

/* query 8 */
TABLE top_businesses_with_category(SGR sgr, int top, char *category){
	GPtrArray **array = get_top_businesses_by_category (sgr->bc, category, (guint) top);
	if(array == NULL) return NULL;

	//Cria a tabela
	char *headers[] = {"ID", "Nome", "Estrelas"};
	TABLE table = construct_table(array, headers, 3);

	//Liberta a memória usada pelo 'array'
	for(int i = 0; i < 3; i++) g_ptr_array_free(array[i],TRUE);
	free(array);

	return table;
}

/* query 9 */
TABLE reviews_with_word(SGR sgr, char *word){
	GPtrArray ** revs_ids = word_in_hash(sgr->rc,word);

	if (revs_ids){
		char *headers[] = {"Reviews"};
		TABLE t = construct_table(revs_ids,headers,1);

		g_ptr_array_free(revs_ids[0],TRUE);
		free(revs_ids);
		
		return t;	
	}
	else return NULL;
}