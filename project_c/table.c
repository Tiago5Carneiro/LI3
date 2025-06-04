#include "table.h"
#include <stdio.h>
#include <stdlib.h>
#include <glib.h>
#include <ctype.h>

#define MAX_INFOTYPES 5

struct Table {
    char infoTypes[MAX_INFOTYPES][20];
    int size[MAX_INFOTYPES];
    int nr_colunas;
    GPtrArray *table;
};

TABLE init_table(){
    TABLE t = (TABLE) malloc(sizeof(struct Table));
    t->table=g_ptr_array_new(); //_with_free_func((GDestroyNotify) free_table);

	if(t == NULL) {
		perror("init table"); 
		return NULL;
	}

    for(int i=0;i<MAX_INFOTYPES;i++) t->size[i]=0;

    t->nr_colunas=0;

    return t;
}

void free_table(TABLE t){
    unsigned int len = t->table->len;
    char** tline;

    for(unsigned int i = 0; i < len; i++){
        tline = g_ptr_array_index(t->table, i);
        for(int auxi = 0 ; auxi < t->nr_colunas ; auxi++) free(tline[auxi]);
        free(tline);
    }

    g_ptr_array_free(t->table,TRUE);
    free(t);
}

int get_table_length(TABLE t){
    return t->table->len;
}

char* get_table_index(TABLE t, int i, int auxi){
    char** buffer = g_ptr_array_index(t->table,i);
    return buffer[auxi];
}

void set_infoType(TABLE t, char* buffer, char* delim){
    int i=0, size;

    for(i=0; buffer!=NULL && i<MAX_INFOTYPES;i++){

        char* header = strsep(&buffer,delim);
        size = strlen(header);

        strcpy(t->infoTypes[i],header);

        if(t->size[i] < size) t->size[i] = size;

        t->nr_colunas++;
    }
}

void set_table_index(TABLE t, char* buffer,char* delim){
    int i=0,size;

    char** tl=(char**)malloc((t->nr_colunas)*sizeof(char*));

    while(buffer!=NULL && i<t->nr_colunas){

        char* auxBuffer = strsep(&buffer,delim);
        size = strlen(auxBuffer);
        
        if(t->size[i] < size) t->size[i] = size;

        tl[i] = (char*)malloc((size+1)*sizeof(char));
        strcpy(tl[i],auxBuffer);

        i++;
    }

    g_ptr_array_add(t->table, tl);
}

void cloneNAddTableLine(TABLE newt, TABLE oldt, int i){
    int len = oldt->nr_colunas;

    char** auxt = g_ptr_array_index(oldt->table,i);
    char** newAuxt = (char**)malloc(len*sizeof(char));

    for(int auxI=0;auxI<len;auxI++){
        int size = strlen(auxt[auxI]);

        if(newt->size[auxI]<size) newt->size[auxI] = size;

        newAuxt[auxI] = (char*)malloc((size+1)*sizeof(char));
        strcpy(newAuxt[auxI],auxt[auxI]);
    }

    g_ptr_array_add(newt->table,newAuxt);
}

TABLE directAcess(TABLE t,int coluna, int linha){
    TABLE newt = init_table();

    if(coluna > t->nr_colunas) return NULL;
    if(linha > (int) t->table->len) return NULL;

    strcpy(newt->infoTypes[0],t->infoTypes[coluna-1]);
    newt->nr_colunas=1;    

    int sizeIT, sizeInfo;

    char* auxInfo;
    auxInfo  = get_table_index(t, linha-1, coluna-1);
    sizeInfo = strlen(auxInfo);

    char** info = (char**) malloc(sizeof(char*));
    info[0]     = (char*) malloc((sizeInfo+1)*sizeof(char));
    strcpy(info[0], auxInfo);

    sizeIT = strlen(newt->infoTypes[0]);

    if(sizeIT < sizeInfo) newt->size[0]= sizeInfo;
    else newt->size[0]= sizeIT;

    g_ptr_array_add(newt->table,info);
    

    return newt;
}

FILE* toCSV(TABLE t,char delim,char *path){
    FILE *fp;
    fp = fopen(path, "a" );
    int auxl, len = t->table->len;

    for(auxl=0; auxl < t->nr_colunas; auxl++){
        fprintf(fp,"%s",t->infoTypes[auxl]);

        if(auxl+1 < t->nr_colunas) fprintf(fp,"%c",delim);
        else if(len != 0) fprintf(fp,"\n");

    }

    for(int i=0;i<len;i++){

        for(int auxi=0;auxi<auxl;auxi++){
            fprintf(fp,"%s",get_table_index(t,i,auxi));

            if(auxi+1<auxl)  fprintf(fp,"%c",delim);
            else if(i+1<len) fprintf(fp,"\n");
        }
    }

    fclose(fp);
    return fp;
}

TABLE fromCSV(char *path,char delim){
    FILE *fp;
    if((fp = fopen(path,"r")) == NULL) {
        perror("Erro ao abrir ficheiro table");
        return NULL;
    }

    int n;
    char* buffer=NULL;
    size_t tam_buffer = 0;
    TABLE t = init_table();

    if((n=getline(&buffer,&tam_buffer,fp))<=0) return NULL;
    if(buffer[n-1] == '\n') buffer[n-1] = '\0';

    char* del=(char*)malloc(2*sizeof(char));
    del[0]= delim; del[1]='\0';

    set_infoType(t,buffer,del);

    while((n = getline(&buffer,&tam_buffer,fp)) > 0){

        if(buffer[n-1] == '\n') buffer[n-1] = '\0';

        set_table_index(t,buffer,del);
    }

    fclose(fp);free(buffer);free(del);
    return t;
}

int check_LwrOrGtrOrEq(char* arr,char* value){
    int i=0,arrl=strlen(arr),valuel=strlen(value),a,v;

    while(i<valuel && (isdigit(value[i]) || ispunct(value[i]))) i++;

    if(i==valuel){
        double fv= atof(value), fa= atof(arr);
        if(fa<fv) return -1;
        if(fa>fv) return 1;
        if(fa==fv) return 0;
    } 
    else i=0;
    
    for(;i<arrl && i<valuel;i++){
        a=tolower(arr[i]); v=tolower(value[i]);
        if(a<v) return -1;
        if(a>v) return 1;
    }

    if(arrl<valuel) return 1;
    if(arrl>valuel) return -1;

    return 0;
}

TABLE filter(TABLE t,char* col,char* value,int op){
    TABLE newt = init_table();
    int auxi=0,len = t->table->len;

    newt->nr_colunas = t->nr_colunas;

    for(int n=0; n < t-> nr_colunas; n++) {
        strcpy(newt->infoTypes[n],t->infoTypes[n]);
        newt->size[n] = t->size[n];
    }

    while(strcmp(t->infoTypes[auxi],col)!=0){

        if(auxi== t->nr_colunas){
            printf("Coluna %s é inválida",col);
            return NULL;
        }
        else auxi++;
    }

    if(op==-1){//table where value is greater than everything in column col
        for(int i=0;i<len;i++){
            if(check_LwrOrGtrOrEq(get_table_index(t,i,auxi),value)==-1) cloneNAddTableLine(newt, t, i);
        }
    }
    if(op==0){
        for(int i=0;i<len;i++){
            if(check_LwrOrGtrOrEq(get_table_index(t,i,auxi),value)==0) cloneNAddTableLine(newt, t, i);
        }
    }
    if(op==1){//table where value is lower than everything in column col
        for(int i=0;i<len;i++){
            if(check_LwrOrGtrOrEq(get_table_index(t,i,auxi),value)==1) cloneNAddTableLine(newt, t, i);
        }
    }

    return newt;
}

TABLE proj(TABLE t, char** cols){
    TABLE newt = init_table();
    int l=0, auxlen=0;
    int p[MAX_INFOTYPES];

    while(cols[auxlen][0]!='\0') auxlen++;

    for(int i = 0 ; i < auxlen && i < MAX_INFOTYPES ; i++){

        //variável ex verifica se existe a coluna ou não na table
        int ex=0;
        for(int m = 0; m < t->nr_colunas && m < MAX_INFOTYPES ; m++){

            if(strcmp(t->infoTypes[m],cols[i])==0){

                //fica guardado na lista p a posição do cols[i] no infoTypes
                p[l]=m;
                strcpy(newt->infoTypes[l],t->infoTypes[m]);

                int size = strlen(newt->infoTypes[l]);

                newt->size[l] = size;

                newt->nr_colunas++;


                ex=1;
                l++;
            }
        }
        if(ex!=1) printf("Coluna %s é inválida\n",cols[i] );
    }

    if(l==0){
        printf("Colunas inválidas\n");
        return NULL;
    }

    int length = t->table->len;

    for(int  i = 0; i<length; i++){
            char** auxt = g_ptr_array_index(t->table,i);
            char** newAuxt = (char**)malloc(l*sizeof(char));

            for(int auxl=0;auxl<l;auxl++){
                int size = strlen(auxt[p[auxl]]);

                if(newt->size[auxl]<size) newt->size[auxl] = size;

                newAuxt[auxl] = (char*)malloc((size+1)*sizeof(char));
                strcpy(newAuxt[auxl],auxt[p[auxl]]);
            }

            g_ptr_array_add(newt->table,newAuxt);
    }

    return newt;
}

void print_table(TABLE t,int page){
    int n=0, len=t->table->len, maxChar = 1; 

    for(;n < t->nr_colunas;n++){
        int auxsize = strlen(t->infoTypes[n]);
        if(auxsize>t->size[n]) t->size[n] = auxsize ;        
        maxChar += t->size[n] + 3;
    }

    //print the line separators
    n=0;
    while (n<maxChar) {
        printf("-");
        n++;
    }
    printf("\n");

    n=0;

    //print de headers
    while(n < t->nr_colunas){
        printf("| %s",t->infoTypes[n]);
        int auxsize = strlen(t->infoTypes[n]);
        while((t->size[n] + 1 - auxsize > 0)){
            printf(" ");
            auxsize++;

        }
        if((n +1) == t->nr_colunas) printf("|");
        n++;
    }
    printf("\n");
    n=0;
    //print da table
    int auxi, i=page*50, size=0;

    while(i<len && i<(page+1)*50){

        while (n<maxChar) {printf("-"); n++;} printf("\n");

        n=0;
        char **auxt = g_ptr_array_index(t->table,i);

        for(auxi=0; auxi< t->nr_colunas;auxi++){

            size=strlen(auxt[auxi]); 
            printf("| %s",auxt[auxi]);

            while(t->size[auxi]+1-size >0){
                printf(" ");
                size++;
            }
            if( (auxi +1) == t->nr_colunas) printf("|");
        }
        
        printf("\n");
        i++;
    }

    while (n<maxChar) {printf("-"); n++;} printf("\n");

    int totalpages;
    if(len%50==0) totalpages = (len/50);
    else totalpages = (len/50) + 1;

    printf("\nPage: %d / %d\n",page+1,totalpages);
}


TABLE construct_table(GPtrArray **colunas, char *headers[], int nr_colunas){
    //Verifica se argumentos são validos para construir table
    if(colunas == NULL || nr_colunas <= 0) { return NULL;}
    for(int i = 0; i < nr_colunas; i++)
        if(colunas[i]->len <= 0) return NULL;
        
    TABLE t = init_table();
    t->nr_colunas = nr_colunas;
    int i;

    //put headers into struct Table
    for(i = 0; i < nr_colunas; i++){
        strcpy(t->infoTypes[i], headers[i]);
    }

    int size, auxi, len = colunas[0]->len;
    char* info;

    //cria a table da struct Table
    for(i = 0; i < len ; i++){

        char **auxt;
        auxt = (char**) malloc(nr_colunas * sizeof(char*));

        for(auxi = 0; auxi < nr_colunas ; auxi++){
            info = g_ptr_array_index(colunas[auxi],i);

            size=strlen(info);

            if(t->size[auxi] < size) t->size[auxi]=size;

            auxt[auxi] = (char*)malloc((size+1) * sizeof(char));
            strcpy(auxt[auxi], info);
        }

        g_ptr_array_add(t->table,auxt);
    }
    return t;
}


