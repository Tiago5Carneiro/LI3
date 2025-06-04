#ifndef BUSINESS_CATALOG_H
#define BUSINESS_CATALOG_H

#include "Business.h"
#include <glib.h>

/** CompBusiness (Estrutura que guarda um negócio, o número de estrelas e os ids das reviews feitas a este) **/

typedef struct compBusiness *CompBusiness;

CompBusiness init_compBusiness(Business b);

void free_compBusiness(CompBusiness cpB);

Business get_compBusiness_business(CompBusiness cpB);

char* get_compBusiness_id(CompBusiness cpB);

char* get_compBusiness_name(CompBusiness cpB);

char* get_compBusiness_city(CompBusiness cpB);

char* get_compBusiness_state(CompBusiness cpB);

float get_compBusiness_stars(CompBusiness cpB);

unsigned int get_compBusiness_nr_of_categories(CompBusiness cpB);

char *get_compBusiness_category_by_index(CompBusiness cpB, unsigned int index);

char *get_compBusiness_reviewID_by_index (CompBusiness cpB, unsigned int index);

unsigned int get_compBusiness_reviewCount(CompBusiness cpB);


/** BusinessCatalog **/

typedef struct structBusinessCatalog *BusinessCatalog;

BusinessCatalog BusinessCatalog_init();

void BusinessCatalog_free(BusinessCatalog bc);

int BusinessCatalog_contains(BusinessCatalog bc, char *business_id);

char *get_business_state_by_id(BusinessCatalog bc, char* business_id);

void BusinessCatalog_insert_reviewID(BusinessCatalog bc, char* business_id, char* review_id, float stars);

char *BusinessCatalog_get_business_name(BusinessCatalog bc, char* business_id);

GPtrArray *BusinessCatalog_get_business_names_list_by_letter(BusinessCatalog bc, char *letter);

GPtrArray **BusinessCatalog_get_business_info(BusinessCatalog bc, char* business_id);

GPtrArray **get_businesses_above_n_stars_by_city (BusinessCatalog bc, char *city, float stars);

GPtrArray **get_top_businesses_from_all_cities(BusinessCatalog bc, guint top);

GPtrArray **get_top_businesses_by_category (BusinessCatalog bc, char *key, guint top);

/******** Load Ficheiro *********/

int load_business_file (char *path, BusinessCatalog bc);

#endif