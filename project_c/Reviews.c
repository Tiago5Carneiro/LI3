#include <stdlib.h>
#include <stdio.h>
#include <glib.h>
#include "Reviews.h"

struct review {
    char *review_id;
    char *user_id;
    char *business_id;
    float stars;
    int useful;
    int funny;
    int cool;
    char *date;
    char *text;
};

Review str_to_review(char *buffer){
    Review r       = (Review) malloc(sizeof(struct review));
    r->review_id   = strsep(&buffer,";");
    r->user_id     = strsep(&buffer,";");
    r->business_id = strsep(&buffer,";");
    if(strlen(r->user_id)!=22 || strlen(r->review_id)!=22 || strlen(r->business_id)!=22 || !buffer){
        free(r);
        return NULL;}
    r->stars       =   atof(strsep(&buffer,";"));
    if(!buffer || (r->stars) < 0 || (r->stars) > 5){
        free(r);
        return NULL;}
    r->useful      =   atoi(strsep(&buffer,";"));
    if(!buffer || (r->useful) < 0){
        free(r);
        return NULL;}
    r->funny       =   atoi(strsep(&buffer,";"));
    if(!buffer || (r->funny) < 0){
        free(r);
        return NULL;}
    r->cool        =   atoi(strsep(&buffer,";"));
    if(!buffer || (r->cool) < 0){
        free(r);
        return NULL;}
    r->date        = strsep(&buffer,";");
    r->text        = buffer;

    if(strlen(r->date) != 19 || strlen(r->text) == 0){
        free(r);
        return NULL;}

    /*int h,min,s,a,mes,d;
    if (!r->date || 0<(r->cool) || sscanf(r->date, "%d-%d-%d %d:%d:%d",&h,&min,&s,&a,&mes,&d)!= 6 ||
       0>h>=24 || 0>min>=60 || 0>s>=60 || 0>=a || 0>=mes>12 || d<=0 || mes<=7 && mes%2==1 && d>31||
       mes<=7 && mes%2==0  && d>30 ||mes%2==0 && d>31|| mes%2==1 && d>30|| 
       a%4==0 && mes==2 && d>29 || a%4!=0 && mes==2 && d>28 || !r->text){
        free(r);
        return NULL;
    }*/
    r->review_id   = strdup(r->review_id);
    r->user_id     = strdup(r->user_id);
    r->business_id = strdup(r->business_id);
    r->date        = strdup(r->date);
    r->text        = strdup(r->text);
    return r;
}

Review clone_review(Review r){
    Review clone = (Review) malloc(sizeof(struct review));
    clone->review_id   = strdup(r->review_id);
    clone->user_id     = strdup(r->user_id);
    clone->business_id = strdup(r->business_id);
    clone->date        = strdup(r->date);
    clone->text        = strdup(r->text);
    clone->stars       = r->stars;
    clone->funny       = r->funny;
    clone->cool        = r->cool;
    clone->useful      = r->useful;
    return clone;
}

char *get_review_id (Review r){
    return strdup(r->review_id);
}

char *get_review_userid (Review r){
    return strdup(r->user_id);
}

char *get_review_businessid (Review r){
    return strdup(r->business_id);
}

float get_review_stars(Review r){
    return r->stars;
}

int get_review_useful(Review r){
    return r->useful;
}

int get_review_funny(Review r){
    return r->funny;
}

int get_review_cool(Review r){
    return r->cool;
}

char *get_review_date(Review r){
    return strdup(r->date);
}

char *get_review_text(Review r){
    return strdup(r->text);
}

void print_review(Review r){
    if(r) fprintf(stdout,"Review id: %s    User id: %s    Business id: %s    Stars: %.1f    Funny: %d    useful: %d    Cool: %d\n", r->review_id, r->user_id, r->business_id, r->stars, r->funny, r->useful, r->cool); 
}

void free_review(Review r){
    free(r->review_id);
    free(r->user_id);
    free(r->business_id);
    free(r->date);
    free(r->text);
    free(r);
}
