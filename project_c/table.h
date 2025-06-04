#ifndef TABLE_H
#define TABLE_H

#include <stdio.h>
#include <glib.h>

typedef struct Table *TABLE;

void free_table(TABLE t);

int get_table_length(TABLE t);

TABLE directAcess(TABLE t,int coluna, int linha);

FILE* toCSV(TABLE t,char delim,char *path);

TABLE fromCSV(char *path,char delim);

TABLE filter(TABLE t,char* col,char* value,int op);

TABLE proj(TABLE t, char** cols);

void print_table(TABLE t,int page);

TABLE construct_table(GPtrArray **colunas, char *headers[], int nr_colunas);

#endif