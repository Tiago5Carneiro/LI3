#include <stdio.h>
#include <stdlib.h>
#include "table.h"
void printString(char *str){
    printf("%s",str);
}

void bad_syntax_err(){ printf("Bad syntax! For more information use \"help\".\n\n"); }

void printCheckArgs(){
    printf("Verifique os argumento(s).\n");
}


//Files

void printReadingFromDefaultFolder(){
    printf("A ler os ficheiros da pasta atual...\n\n");
}

void printReadingFiles(){
    printf("A ler os ficheiros...\n\n");
}

//Sgr

void check_load_sgr(){ printf("Inicialize o SGR para executar esta função.\n"); }

void printFreeingLastSgr(){
    printf("Libertando memória da SGR anterior ...\n\n");
}

void printFailReadSgr(){
    printf("Não foi possível carregar a SGR.\n");
}


//Variavel Interpretador

void printVariavelSubstituida(char *variable_name){
    printf("Variavel \"%s\" substituida\n\n", variable_name);
}

void printVariavelAdicionada(char *variable_name){
    printf("Variavel \"%s\" adicionada\n\n", variable_name);
}

void printInvalidVariable(){
    printf("Variável inválida!\n");
}


//Table

void printFailReadingToTable(){
    printf("Não foi possível carregar o ficheiro para uma tabela.\n");
}

void printTableLength(TABLE t){
    printf("O número de linhas presentes na tabela são: %d.\n",get_table_length(t));
}
//Queries

void printInvalidID(){
    printf("ID inválido.\n");
}

void printWordNotFound(){
    printf("Não foram encontradas reviews com esta palavra.\n");
}