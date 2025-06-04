#ifndef REVIEWSCATALOG
#define REVIEWSCATALOG

typedef struct _ReviewCatalog *ReviewCatalog;

#include "usersCatalog.h"
#include "businessCatalog.h"
#include "Reviews.h"

ReviewCatalog reviewCatalog_init();

void reviewCatalog_free(ReviewCatalog rc);

void reviewCatalog_insert(ReviewCatalog rc, Review r);

char *reviewCatalog_get_business_id_of_review(ReviewCatalog rc, char *review_id);

GPtrArray ** word_in_hash(ReviewCatalog rc, char * word);

int load_review_file(char *path, UserCatalog uc, BusinessCatalog bc, ReviewCatalog rc);

#endif