#ifndef BUSINESS
#define BUSINESS

typedef struct business *Business;

Business str_to_business(char *buffer);

/* Deep clone de um negócio 
   void *lixo é usado apenas para seguir tipo GFunc
*/
Business clone_business(Business b);

Business clone_business_GCopyFunc(Business b, void *lixo);

char *get_business_id(Business b);

char *get_business_name(Business b);

char *get_business_city(Business b);

char *get_business_state(Business b);

char *get_business_category_by_index(Business b, unsigned int index);

unsigned int get_business_nr_of_categories(Business b);

/* void *lixo para obedecer ao tipo GFunc */
void print_business(Business b, void *lixo);

void free_business(Business b);

/* void *lixo é usado apenas para seguir tipo GFunc */
void free_business_gfunc(Business b, void *lixo);

#endif