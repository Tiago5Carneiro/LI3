#ifndef REVIEWS
#define REVIEWS

typedef struct review *Review;

Review str_to_review(char *buffer);

Review clone_review(Review r);

char *get_review_id (Review r);

char *get_review_userid (Review r);

char *get_review_businessid (Review r);

float get_review_stars(Review r);

int get_review_useful(Review r);

int get_review_funny(Review r);

int get_review_cool(Review r);

char *get_review_date(Review r);

char *get_review_text(Review r);

void free_review(Review r);

#endif