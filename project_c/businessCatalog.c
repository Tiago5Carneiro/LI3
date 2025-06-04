#include "businessCatalog.h"
#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>

/** CompBusiness **/

struct compBusiness{
	Business business;  //Business
	float stars; 		//Estrelas deste business
	GPtrArray *reviews; //Array dinamico com as reviews
	//GArray tem a componente 'len' que serve de review_count
};

CompBusiness init_compBusiness(Business b){
	CompBusiness cpB = (CompBusiness) malloc(sizeof(struct compBusiness));
	cpB->business    = b;
	cpB->stars       = 0.0;
	cpB->reviews     = g_ptr_array_new_with_free_func((GDestroyNotify) free);
	return cpB;
}

void free_compBusiness(CompBusiness cpB){
	if(cpB == NULL) return;
	free_business(cpB->business);
	g_ptr_array_free(cpB->reviews,TRUE);
	free(cpB);
}

Business get_compBusiness_business(CompBusiness cpB){
	return clone_business(cpB->business);
}

char* get_compBusiness_id(CompBusiness cpB){
	return get_business_id(cpB->business);
}

char* get_compBusiness_name(CompBusiness cpB){
	return get_business_name(cpB->business);
}

char* get_compBusiness_city(CompBusiness cpB){
	return get_business_city(cpB->business);
}

char* get_compBusiness_state(CompBusiness cpB){
	return get_business_state(cpB->business);
}

float get_compBusiness_stars(CompBusiness cpB){
	return cpB->stars;
}

guint get_compBusiness_nr_of_categories(CompBusiness cpB){
	return get_business_nr_of_categories(cpB->business);
}

char *get_compBusiness_category_by_index(CompBusiness cpB, guint index){
	return get_business_category_by_index(cpB->business, index);
}

char *get_compBusiness_reviewID_by_index (CompBusiness cpB, guint index){
    if(index < cpB->reviews->len)
        return strdup(g_ptr_array_index(cpB->reviews, index));
    return NULL;
}

unsigned int get_compBusiness_reviewCount(CompBusiness cpB){
	return cpB->reviews->len;
}

//Refresh no número de estrelas do negócio
void compBusiness_refresh_stars(CompBusiness cpB, float new_review_stars){
	int review_count = cpB->reviews->len;
	float bus_stars  = cpB->stars;
	if (review_count != 0) cpB->stars = (bus_stars * review_count + new_review_stars) / (review_count + 1) ;
	else cpB->stars = new_review_stars;
}

/** BusinessByLetter **/

typedef struct structBusinessByLetter{
    GPtrArray *lista[26];
} *BusinessByLetter;

BusinessByLetter BusinessByLetter_init (){
	BusinessByLetter bl = (BusinessByLetter) malloc(sizeof(struct structBusinessByLetter));
	if(bl == NULL) 
		return NULL;
		
	for(int i = 0; i < 26; i++)
		bl->lista[i] = g_ptr_array_new();

	return bl;
}

void businessByLetter_free (BusinessByLetter bl){
	for(int i = 0; i < 26; i++)
		g_ptr_array_free(bl->lista[i], TRUE);
	free(bl);
}

//Dado o nome de um negócio, retorna o index da lista ligada onde se encontrará 
//Admite nome começado por letra
int businessByLetter_get_list_index (char *name){
	int r = (int) name[0];
	if(r >= 65 && r <= 90) return r - 65;
	else return r - 97;
}

//Insere um negócio no array correspondente tendo em conta a primeira letra do nome
void businessByLetter_insert(BusinessByLetter bl, Business b){
	char *name = get_business_name(b);
	int index  = businessByLetter_get_list_index(name);
	g_ptr_array_add(bl->lista[index], b);
	free(name);
}

//Função auxiliar de 'businessByLetter_get_business_names_list_by_letter' que segue o tipo GFunc para que possa ser usada na funcao g_ptr_array_foreach.
//O objetivo desta função é obter o nome do business e colocá-lo num array
void clone_business_name_to_array(Business b, GPtrArray *bus_names_array){
	g_ptr_array_add(bus_names_array, get_business_name(b));
}

//Função que retorna lista de nomes de negócios começados por determinada letra
GPtrArray* businessByLetter_get_business_names_list_by_letter (BusinessByLetter bl, char *letter){
	//Caso não seja NULL, não pode executar a query
	if(isalpha(*letter) == 0) return NULL;

	//Encontra o index do GPtrArray* onde se encontrarão os negócios começados pela letra fornecida
	int index = businessByLetter_get_list_index(letter);

	//Inicia o GPtrArray* onde serão armazenados os nomes dos negócios
	GPtrArray *bus_names_array = g_ptr_array_new_with_free_func((GDestroyNotify) free);

	//Clonagem dos nomes dos negócios para o GPtrArray* iniciado anteriormente
	g_ptr_array_foreach(bl->lista[index],(GFunc) clone_business_name_to_array, bus_names_array);
	
	return bus_names_array;
}

/********** CompBusiness HashTable **********/
GHashTable *hash_business_init(){
	GHashTable* hash = g_hash_table_new_full(g_str_hash,g_str_equal,(GDestroyNotify) free, (GDestroyNotify)free_compBusiness);
	return hash;
}

void hash_business_Free(GHashTable* hash){
	if(hash != NULL)
		g_hash_table_destroy(hash);
}

void hash_business_Insrt(GHashTable* hash, CompBusiness cpB){
	if(cpB != NULL)
		g_hash_table_insert(hash, get_business_id(cpB->business), cpB);
}

Business hash_business_get_business(GHashTable *ht, char *business_id){
	CompBusiness cpB = g_hash_table_lookup(ht,business_id);
	if(cpB == NULL) return NULL;
	return clone_business(cpB->business);
}

/******** HashTable (Key: Categoria (ou City)) de Dynamic Arrays (CompBusiness) *********/
//Free dos compBusiness são feitos pela HashTable

void hash_parameter_array_free (GPtrArray *CompBusiness_arr){
	g_ptr_array_free(CompBusiness_arr,TRUE);
}

GHashTable *hash_parameter_init(){
	GHashTable *ht = g_hash_table_new_full(g_str_hash, g_str_equal, (GDestroyNotify) free, (GDestroyNotify) hash_parameter_array_free);
	return ht;
}

//Free da HashTable de Parametros
void hash_parameter_free (GHashTable* ht){
	if(ht != NULL) g_hash_table_destroy(ht);
}

/* Dado um parametro(Categoria ou Cidade) e um CompBusiness, verifica se é necessário criar uma HashTable para esse parametro e insere o CompBusiness
   No caso de ser necessário criar a HashTable, retorna o apontador para ela */ 
void hash_parameter_insert (GHashTable* ht, char* parameter, CompBusiness cpB){
	GPtrArray *CompBusiness_arr;
	if((CompBusiness_arr = g_hash_table_lookup (ht,parameter)) == NULL){
		CompBusiness_arr = g_ptr_array_new();
		g_hash_table_insert(ht, strdup(parameter), CompBusiness_arr);
	}
	g_ptr_array_add(CompBusiness_arr, cpB);
}

//Insere o CompBusiness em todas as categorias presentes na HashTable
void hash_category_CompBusiness_insert (GHashTable* ht, CompBusiness cpB){
	char *category;
	for(guint i = 0; i < get_compBusiness_nr_of_categories(cpB); i++){
		category = get_compBusiness_category_by_index(cpB,i);
        hash_parameter_insert(ht, category, cpB);
		free(category);
	}
}

//Insere o CompBusiness em todas as categorias presentes na HashTable
void hash_city_CompBusiness_insert (GHashTable* ht, CompBusiness cpB){
	char *city = get_compBusiness_city(cpB);
    hash_parameter_insert (ht, city, cpB);
	free(city);
}

/****** Auxiliares das Queries 5,6 e 8 *******/

//Função auxiliar para ordenar um array de CompBusiness por ordem descrescente do número de estrelas
int CompBusiness_sort_per_star(gconstpointer a, gconstpointer b){	
	float stars1 = get_compBusiness_stars(*(CompBusiness *) a),
		  stars2 = get_compBusiness_stars(*(CompBusiness *) b);
	if(stars1 == stars2) return 0;
	else if(stars1 > stars2) return -1;
	else return 1;
}

void parameter_array_sort_by_stars(GPtrArray *arr){
	g_ptr_array_sort(arr, CompBusiness_sort_per_star);
}

//Dada uma cidade ou categoria, ordena por ordem decrescente o array que lhe corresponde
GPtrArray *hash_parameter_array_sort_by_stars(GHashTable *ht, char *parameter){
	GPtrArray *arr = g_hash_table_lookup(ht, parameter);
	if(arr == NULL) return NULL;

	parameter_array_sort_by_stars(arr);
	return arr;
}

//Dado um GPtrArray de CompBusiness, copia para array de GPtrArrays o id, o nome e o numero de estrelas de n CompBusinesses
//Esta função admite que o clone array já iniciou os três arrays com g_ptr_array_new
void clone_id_name_stars_from_compBusiness_array(GPtrArray *compBusiness_array, GPtrArray **clone_array,guint nr_elements){
	//Caso nr_elements seja maior que o número de elementos existentes em arr, copia todos os elementos desse
	if(nr_elements > compBusiness_array->len) nr_elements = compBusiness_array->len;

	CompBusiness cpB; char *stars;
	for(guint i = 0; i < nr_elements ; i++){
		cpB = g_ptr_array_index(compBusiness_array, i);
		g_ptr_array_add(clone_array[0],get_compBusiness_id(cpB));
		g_ptr_array_add(clone_array[1],get_compBusiness_name(cpB));

		//Para as stars, embora não seja o mais eficiente a nivel de memoria e tempo, foi necessário criar um apontador para obedecer ao tipo pedido pela estrutura table
		 stars = (char *) malloc(4); //Só são necessários 4 bytes dado que só pretendemos ter 1 casa decimal
		sprintf(stars, "%.1f", get_compBusiness_stars(cpB));
		g_ptr_array_add(clone_array[2], stars);
	}
}

//Retorna um GPtrArray** (basicamente um GPtrArray*[2] dado que os dados pretendidos sao o nome e o id) com os nomes e ids dos negócios
//com um número de estrelas igual ou superior ao fornecido, que é o tipo pedido pela table.
GPtrArray **city_array_above_n_stars (GHashTable *ht, char *city, float stars){
	GPtrArray *compBusiness_array;
	CompBusiness cpB;

	//Verifica se os argumentos são válidos. Nr de estrelas entre 0 e 5, e se a cidade tem algum negócio.
	if(stars < 0 || stars > 5 || (compBusiness_array = g_hash_table_lookup(ht, city)) == NULL) return NULL;
	
	//Inicializacao dos arrays onde se vao guardar as informacoes
	GPtrArray **clone_array;
	clone_array    = (GPtrArray **) malloc(sizeof(GPtrArray *) * 2);
	clone_array[0] = g_ptr_array_new_with_free_func((GDestroyNotify) free); //Array dos ids
	clone_array[1] = g_ptr_array_new_with_free_func((GDestroyNotify) free); //Array dos nomes

	//Ordenação do array que contem os negócios da cidade especificada
	parameter_array_sort_by_stars(compBusiness_array);

	//Insercao do nome e do id no respetivo array
	for(guint i = 0; i < compBusiness_array->len && get_compBusiness_stars(cpB = g_ptr_array_index(compBusiness_array, i)) >= stars; i++){
		g_ptr_array_add(clone_array[0],get_compBusiness_id(cpB));
		g_ptr_array_add(clone_array[1],get_compBusiness_name(cpB));
	}

	return clone_array;
}

//Retorna um GPtrArray** (GPtrArray*[3]) com os nomes, ids e número de stars dos negócios dos melhores n negócios
GPtrArray **hash_parameter_get_top_sorted_by_stars (GHashTable *ht, char *key, guint top){
	GPtrArray *array;
	//Retorna NULL caso o valor top não seja superior a 0, ou se não existir o parametro do qual se pretende obter o array
	if(top <= 0 || (array = hash_parameter_array_sort_by_stars(ht, key)) == NULL) return NULL;

	//Inicializacao dos arrays onde se vao guardar as informacoes
	GPtrArray **clone_array;
	clone_array    = (GPtrArray **) malloc(sizeof(GPtrArray *) * 3);
	clone_array[0] = g_ptr_array_new_with_free_func((GDestroyNotify) free); //Array dos ids
	clone_array[1] = g_ptr_array_new_with_free_func((GDestroyNotify) free); //Array dos nomes
	clone_array[2] = g_ptr_array_new_with_free_func((GDestroyNotify) free); //Array dos números de estrelas

	//Clonagem das informações. Inseridas no clone_array.
	clone_id_name_stars_from_compBusiness_array(array, clone_array, top);

	return clone_array;
}

//Retorna um GPtrArray** (basicamente um GPtrArray*[3]) com os nomes, ids e o número de estrelas dos melhores n negócios
GPtrArray **hash_city_get_top_from_all_cities(GHashTable *ht, guint top){
	if(top <= 0) return NULL;

	//Inicializacao dos arrays onde se vao guardar as informacoes
	GPtrArray **clone_array;
	clone_array    = (GPtrArray **) malloc(sizeof(GPtrArray *) * 3);
	clone_array[0] = g_ptr_array_new_with_free_func((GDestroyNotify) free); //Array dos ids
	clone_array[1] = g_ptr_array_new_with_free_func((GDestroyNotify) free); //Array dos nomes
	clone_array[2] = g_ptr_array_new_with_free_func((GDestroyNotify) free); //Array dos números de estrelas

	//Passagem dos valores da HashTable para uma lista para que a manipulacao de dados seja mais facil
	GList *list_cities = g_hash_table_get_values(ht);

	for(GList *l = list_cities; l != NULL ; l = l->next){
		//l->data corresponde ao GPtrArray* de cada cidade

		//Ordenacao do array da cidade por ordem decrescente do número de estrelas
		parameter_array_sort_by_stars(l->data);

		//Clonagem das informações mencionadas na descrição da função
		clone_id_name_stars_from_compBusiness_array(l->data, clone_array, top);
	}

	g_list_free(list_cities);
	return clone_array;
}

/******** BusinessCatalog *********/

//Todas as estruturas partilham o msm Business. E as ultimas 3 partilham o msm CompBusiness. Como o CompBusiness contem o Business, o free apenas é feito atraves da estrutura ht_businesses
struct structBusinessCatalog{
	BusinessByLetter bl;
	GHashTable* ht_businesses;
    GHashTable* ht_bus_categories;
    GHashTable* ht_bus_cities;
};

BusinessCatalog BusinessCatalog_init(){
	BusinessCatalog bc = (BusinessCatalog) malloc(sizeof(struct structBusinessCatalog));
	if(bc == NULL) return NULL;

	bc->bl 			  	  = BusinessByLetter_init();
	bc->ht_businesses 	  = hash_business_init();
	bc->ht_bus_categories = hash_parameter_init();
	bc->ht_bus_cities     = hash_parameter_init();

	return bc;
}

void BusinessCatalog_free(BusinessCatalog bc){
	if(bc != NULL){
		businessByLetter_free(bc->bl);
		hash_business_Free(bc->ht_businesses);
		hash_parameter_free(bc->ht_bus_categories);
		hash_parameter_free(bc->ht_bus_cities);
		free(bc);
	}	
}

int BusinessCatalog_contains(BusinessCatalog bc, char *business_id){
	if(g_hash_table_contains(bc->ht_businesses, business_id) == TRUE) return 1;
	return 0;
}

//Dado um business id, retorna o estado onde este negócio se encontra
char *get_business_state_by_id(BusinessCatalog bc, char* business_id){
	CompBusiness cpB = g_hash_table_lookup(bc->ht_businesses, business_id);
	if(cpB == NULL) return NULL;	
	return get_compBusiness_state(cpB);
}

/*
//Dado um business id e um index, retorna um review id
char *BusinessCatalog_get_compBusiness_review_by_index(BusinessCatalog bc, char *business_id, unsigned int index){
	CompBusiness cpB = g_hash_table_lookup(bc->ht_businesses, business_id);
	if(cpB == NULL) return NULL;
	return get_compBusiness_reviewID_by_index(cpB, index);
}
*/

//Adiciona o review_id á lista de review ids e atualiza o numero de estrelas
void BusinessCatalog_insert_reviewID(BusinessCatalog bc, char* business_id, char* review_id, float stars){
	CompBusiness cpB = g_hash_table_lookup(bc->ht_businesses, business_id);
	compBusiness_refresh_stars(cpB, stars);
	g_ptr_array_add(cpB->reviews, strdup(review_id));
}

char *BusinessCatalog_get_business_name(BusinessCatalog bc, char* business_id){
	return get_compBusiness_name( g_hash_table_lookup(bc->ht_businesses, business_id) );
}

//Query 2
GPtrArray *BusinessCatalog_get_business_names_list_by_letter(BusinessCatalog bc, char *letter){
	return businessByLetter_get_business_names_list_by_letter(bc->bl, letter);
}

//Query 3
GPtrArray **BusinessCatalog_get_business_info(BusinessCatalog bc, char* business_id){
	CompBusiness cpB = g_hash_table_lookup(bc->ht_businesses, business_id);
	if(cpB == NULL) return NULL;

	//Inicializacao dos arrays onde se vao guardar as informacoes
	GPtrArray **clone_array = (GPtrArray **) malloc(sizeof(GPtrArray *) * 5);
	for(int i = 0; i < 5; i++) { clone_array[i] = g_ptr_array_new_with_free_func((GDestroyNotify) free); }

	//Clone & Store
	g_ptr_array_add(clone_array[0], get_compBusiness_name(cpB));
	g_ptr_array_add(clone_array[1], get_compBusiness_city(cpB));
	g_ptr_array_add(clone_array[2], get_compBusiness_state(cpB));

	char *stars = (char *) malloc(4); //Só são necessários 4 bytes dado que só pretendemos ter 1 casa decimal
	sprintf(stars, "%.1f", get_compBusiness_stars(cpB));
	g_ptr_array_add(clone_array[3], stars);

	char *reviewCount = (char *) malloc(11); //2^(4 * 8) = 2 ^ 32 = 4294967296. 10 digitos + 1 para o '\0' = 11 chars
	sprintf(reviewCount, "%d", get_compBusiness_reviewCount(cpB));
	g_ptr_array_add(clone_array[4], reviewCount);
	
	return clone_array;
}

//Query 5
GPtrArray **get_businesses_above_n_stars_by_city (BusinessCatalog bc, char *city, float stars){
	return city_array_above_n_stars(bc->ht_bus_cities, city, stars);
}

//Query 6
GPtrArray **get_top_businesses_from_all_cities(BusinessCatalog bc, guint top){
	return hash_city_get_top_from_all_cities(bc->ht_bus_cities, top);
}

//Query 8
GPtrArray **get_top_businesses_by_category (BusinessCatalog bc, char *key, guint top){
	return hash_parameter_get_top_sorted_by_stars(bc->ht_bus_categories, key, top);
}

/******** Load Ficheiro *********/

int load_business_file (char *path, BusinessCatalog bc){
	BusinessByLetter bl       = bc->bl;
	GHashTable* ht_business   = bc->ht_businesses; 
	GHashTable* ht_categories = bc->ht_bus_categories; 
	GHashTable* ht_cities     = bc->ht_bus_cities;


    char   *buffer    = NULL;
    size_t tam_buffer = 0;
    int n;
    FILE *fp;
    Business b; 
    CompBusiness cpB;
    
    if((fp = fopen(path,"r")) == NULL) {
        perror("Erro ao abrir ficheiro dos negócios");
        return -1;
    }
    
    while((n = getline(&buffer,&tam_buffer,fp)) > 0){
        if(buffer[n-1] == '\n') buffer[n-1] = '\0';
        if((b = str_to_business(buffer)) != NULL) {
            businessByLetter_insert(bl,b);
            cpB = init_compBusiness(b);
            hash_business_Insrt(ht_business,cpB);
			hash_city_CompBusiness_insert(ht_cities, cpB);
			hash_category_CompBusiness_insert(ht_categories, cpB);
        }
    }

    fclose(fp); free(buffer);	
    return 0;
}