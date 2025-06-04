#include "interpretador.h"
#include <glib.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include "table.h"
#include "sgr.h"
#include "ui.h"

//Caso seja uma espaço, aumenta o index do buffer
#define maybe_skip_char(c1, c2, index) if(c1 == c2) index++; 

/***** Variable *****/

enum OPERATOR{LT=-1, EQ=0, GT=1, ERR=2};

typedef struct variable{
    char *name;
    TABLE table;
} *VAR;

//Inicia a estrutura 'variable' que será usada para guardar o nome desta, assim como a table correspondente
VAR init_variable(char *name, TABLE table){
    VAR v    = (VAR) malloc(sizeof(struct variable));
    v->name  = strdup(name);
    v->table = table;
    return v;
}

char *getVariableName(VAR v){
    return v->name;
}

void free_variable(VAR v){
    free(v->name);
    free_table(v->table);
    free(v);
}

//Verifica se já existe a variável na lista. Retorna NULL caso nao exista, e o apontador para a struct GSList caso exista.
GSList *search_variable(GSList *variable_list, char *name){
    GSList *tmp;
    for(tmp = variable_list; tmp != NULL && strcmp(getVariableName(tmp->data),name) != 0; tmp = tmp->next);
    return tmp;
}

//Insere a variavel numa lista
GSList *insert_variable(GSList *variable_list, char *name, TABLE table){
    //Caso a tabela seja nula, não faz nada
    if(table == NULL) {printf("Tabela inválida. Variável não adicionada.\n"); return variable_list;}

    VAR v = init_variable(name, table);
    GSList *node;

    if((node = search_variable(variable_list, name)) != NULL){ 

        //Caso uma variável com o mesmo nome já exista, esta será substituída

        printVariavelSubstituida(getVariableName(v));
        free_variable(node->data);
        node->data = v;
    }
    else{
        variable_list = g_slist_prepend(variable_list, v);
        printVariavelAdicionada(getVariableName(v));
    }
    return variable_list;
}

void free_variable_list(GSList *variables_list){
    g_slist_free_full(variables_list, (GDestroyNotify) free_variable);
}

/***** Auxiliares *****/

//Função usada para remover espaços á esquerda e á direita de uma string
//https://stackoverflow.com/questions/122616/how-do-i-trim-leading-trailing-whitespace-in-a-standard-way
char *trimwhitespace(char *str)
{
    if(str == NULL) return NULL;
    char *end;

    // Trim leading space
    while(isspace((unsigned char)*str)) str++;

    if(*str == 0)  // All spaces?
    return str;

    // Trim trailing space
    end = str + strlen(str) - 1;
    while(end > str && isspace((unsigned char)*end)) end--;

    // Write new null terminator character
    end[1] = '\0';

    return str;
}

//Função auxiliar que retorna 1 caso encontre um '=', ou um '(', ou um '['. Retorna 0, caso contrário.
int myIsPunc(char c){
    if(c == '=' || c == '(' || c == '[') return 1;
    else return 0;
}

//Retorna indice da primeira pontuação que encontrar. -1 se não encontrar
int find_next_punc(char *str, int index){
    for(;str[index] != '\0' && myIsPunc(str[index]) == 0; index++);
    if(str[index] == '\0') return -1;
    return index;
}

/***** Handle Command *****/
/* Functions that will process a string containing the arguments, and send them to the main function.
 * Eg.: 'handleFromCSV' will receive a string, extract the arguments from it, and send them to the function 'fromCSV'. */

void handleLoadSGR(SGR *sgr, char *args){
    //No caso de já ter dado load a uma sgr, dá free a essa, para poder ler a próxima
    if(*sgr != NULL) {
        printFreeingLastSgr();
        free_sgr(*sgr);
    }

    //Verifica se é suposto ler os ficheiros da pasta onde o executável se encontra, i.e., quando não recebe argumentos.
    int i;
    for(i = 0; args[i] == ' '; i++);
    if(args[i] == ')') {
        printReadingFromDefaultFolder();
        *sgr = load_sgr("users.csv","business.csv","reviews.csv");
    }
    else{
        //Se o primeiro char diferente de um espaço não for um ')', tenta ler os filepaths
        char *usersfilepath    = trimwhitespace(strsep(&args,","));
        char *businessfilepath = trimwhitespace(strsep(&args,","));
        char *reviewsfilepath  = trimwhitespace(strsep(&args,")"));
        if(usersfilepath == NULL || businessfilepath == NULL || reviewsfilepath == NULL) {bad_syntax_err();}
        else {printReadingFiles(); *sgr = load_sgr(usersfilepath, businessfilepath, reviewsfilepath);}
    }

    if(*sgr == NULL) { printFailReadSgr(); printCheckArgs(); }
}

TABLE handleFromCSV(char *args){
    char *filepath  = trimwhitespace(strsep(&args,","));
    char *delim_str = trimwhitespace(strsep(&args,")"));
    char delim;

    if(filepath == NULL || delim_str == NULL)   return NULL;
    if(sscanf(delim_str,"\"%c\"", &delim) == 0) return NULL;
    
    TABLE t = fromCSV(filepath, delim);
    if(t == NULL) {printFailReadingToTable(); printCheckArgs(); }
    return t;
}

int handleToCSV(char *args, GSList *variables_list){
    char *var       = trimwhitespace(strsep(&args,","));
    char *delim_str = trimwhitespace(strsep(&args,","));    
    char *filepath  = trimwhitespace(strsep(&args,")"));
    GSList *aux;
    VAR  v;
    char delim;

    if(var       == NULL 
    || filepath  == NULL 
    || delim_str == NULL 
    || (aux = search_variable(variables_list, var)) == NULL 
    || sscanf(delim_str,"\"%c\"", &delim) == 0) {bad_syntax_err(); printCheckArgs(); return -1;}

    v = aux->data;
    toCSV(v->table, delim, filepath);
    return 0;
}


int handleShow(char *args, GSList *variables_list){
    char   *var = trimwhitespace(strsep(&args,")"));
    GSList *aux;

     char   *input_buffer   = NULL,
           *tmp             = NULL; 
    size_t tam_input_buffer = 0;
    ssize_t n; 

    if(var == NULL || (aux = search_variable(variables_list, var)) == NULL) { printInvalidVariable(); return -1;}

    TABLE t = ((VAR) aux->data)->table;

    //if(t == NULL) {printf("Não é possível ler uma tabela vazia!\n"); return -1;}
    printTableLength(t);
    print_table(t,0);

    int totalpages, length = get_table_length(t) ;

    while(1){
        printString("Introduza a página que quer visualizar ou saia(q): ");
        if((n = getline(&input_buffer,&tam_input_buffer,stdin)) > 0){
            //Elimina '\n' do final 
            if(input_buffer[n - 1] == '\n') input_buffer[n - 1] = '\0';

            //Funçoes que seguem podem alterar a string, para que seja possivel dar free posteriormente, usamos uma variavel tmp para percorrer a string
            tmp = input_buffer;

            if(!strncmp(tmp,"q",1)) { free(input_buffer); return 0;}
            else{
                int page;
                if(sscanf(input_buffer, "%d", &page) != 1) printf("Só é permitido dígitos ou q! Tente novamente!\n"); 
                else{
                    if(length%50==0) totalpages = (length/50);
                    else totalpages = (length/50) + 1;

                    if(page > 0 && totalpages >= page) print_table(t,page - 1);
                    else printf("Não existe essa página! Tente novamente!\n");
                }
            }
        }
        else printf("Só é permitido dígitos ou q! Tente novamente!\n");
    }

    return 0;
}


enum OPERATOR getOperator(char* op){
    if(!strcmp("LT",op)) return LT;
    else if(!strcmp("EQ",op)) return EQ;
    else if(!strcmp("GT",op)) return GT;
    else return ERR;
}

TABLE handleFilter(char *args, GSList *variables_list){ 
    char   *var      = trimwhitespace(strsep(&args,","));
    char   *col      = trimwhitespace(strsep(&args,","));
    char   *value    = trimwhitespace(strsep(&args,","));
    enum OPERATOR op = getOperator(trimwhitespace(strsep(&args,")")));

    int  operator = op;
    
    GSList *aux;
    
    if(var == NULL || col == NULL || value == NULL || operator == 2 || (aux = search_variable(variables_list, var)) == NULL ) {
        bad_syntax_err();
        printCheckArgs();
        return NULL;
    }

    return filter(((VAR) aux->data)->table, col, value, operator);
}


char** getHeaders(char* headers){
    int i, size;
    char** trueHeaders =(char**) malloc(sizeof(char*));
    char* auxheader;

    for(i=0;headers!=NULL;i++){
        trueHeaders=(char**) realloc(trueHeaders , ((i+2) * sizeof(char*)));

        auxheader      = trimwhitespace(strsep(&headers,","));
        size           = strlen(auxheader);

        trueHeaders[i] = (char*) malloc((size+1)*sizeof(char));
        strcpy(trueHeaders[i],auxheader);
    }
    trueHeaders[i] = (char*) malloc(sizeof(char));
    trueHeaders[i][0] = '\0';

    return trueHeaders;
}

TABLE handleProj(char *args, GSList *variables_list){
    char   *var      = trimwhitespace(strsep(&args,","));
    char   **cols_str = getHeaders(strsep(&args,")"));
    GSList *aux;

    if( var == NULL      ||  cols_str == NULL || (aux = search_variable(variables_list, var)) == NULL ){
        bad_syntax_err();
        printCheckArgs();
        return NULL;
    }

    return proj(((VAR) aux->data)->table, cols_str);
}

// z = x[1][1]
TABLE handleDirectAcess(TABLE table, char *args){
    int linha, coluna;
    if(sscanf(args, "%d][%d]", &linha, &coluna) != 2) {bad_syntax_err(); printCheckArgs(); return NULL;}
    return directAcess(table, linha, coluna);
}

//Query2
//Receber apenas a letra
TABLE handleBusinesses_started_by_letter(SGR sgr, char *args){
    if(sgr == NULL) {check_load_sgr(); return NULL;}
    else{
        char *letter_str = trimwhitespace(strsep(&args,")"));
        char  letter;
        if(sscanf(letter_str, "\"%c\"", &letter) != 1) return NULL;

        TABLE table = businesses_started_by_letter(sgr, letter);
        if(table == NULL) printCheckArgs();
        return table;
    }
}

//Query3
TABLE handleBusiness_info(SGR sgr, char *args){
    if(sgr == NULL) {check_load_sgr(); return NULL;}
    else{
        char *id = trimwhitespace(strsep(&args,")"));
        if(id == NULL) {printCheckArgs(); return NULL;}

        TABLE table = business_info(sgr, id);
        if(table == NULL) printInvalidID();
        return table;
    }
}

//Query4
TABLE handleBusinesses_reviewed(SGR sgr, char *args){
    if(sgr == NULL) {check_load_sgr(); return NULL;}
    else{
        char *id = trimwhitespace(strsep(&args,")"));
        if(id == NULL) { bad_syntax_err(); return NULL;}

        TABLE table = businesses_reviewed(sgr, id);
        if(table == NULL) printInvalidID();
        return table;
    }
}

//Query5
TABLE handleBusinesses_with_stars_and_city(SGR sgr, char *args){
    if(sgr == NULL) {check_load_sgr(); return NULL;}
    else{
        char *stars_str = trimwhitespace(strsep(&args,",")); 
        char *city      = trimwhitespace(strsep(&args,")"));
        float stars;
        if(stars_str == NULL || city == NULL || sscanf(stars_str, "%f", &stars) != 1) return NULL;

        TABLE table = businesses_with_stars_and_city(sgr, stars, city);
        if(table == NULL) printCheckArgs();
        return table;
    }
}

//Query6
TABLE handleTop_businesses_by_city(SGR sgr, char *args){
    if(sgr == NULL) {check_load_sgr(); return NULL;}
    else{
        char *top_str = trimwhitespace(strsep(&args,")"));
        int   top;
        if(sscanf(top_str, "%d", &top) != 1) return NULL;

        TABLE table = top_businesses_by_city(sgr, top);
        if(table == NULL) printCheckArgs();
        return table;
    }
}

//Query8
TABLE handleTop_businesses_with_category(SGR sgr, char *args){
    if(sgr == NULL) {check_load_sgr(); return NULL;}
    else{
        char *top_str  = trimwhitespace(strsep(&args,",")); 
        char *category = trimwhitespace(strsep(&args,")"));
        int   top;
        if(top_str == NULL || category == NULL || sscanf(top_str, "%d", &top) != 1) return NULL;

        TABLE table = top_businesses_with_category(sgr, top, category);
        if(table == NULL) printCheckArgs();
        return table;
    }
}

//Query9
TABLE handleReviews_with_word(SGR sgr, char *args){
    if(sgr == NULL) {check_load_sgr(); return NULL;}
    else{
        char *word = trimwhitespace(strsep(&args,")"));
        if(word == NULL) return NULL;

        TABLE table = reviews_with_word(sgr, word);
        if(table == NULL) printWordNotFound();
        return table;
    }
}


/***** Select Command *****/

TABLE select_toTable_command(SGR sgr, GSList *variables_list, char *str){
    char punc,
        *func_or_var,
        *args;
    int index;

    if((index = find_next_punc(str, 0)) > 0 ) {
    
    punc = str[index];
    str[index++]  = '\0';
    func_or_var   = trimwhitespace(str);
    args          = &(str[index]);
    }
    else{bad_syntax_err(); return NULL;}

    TABLE r = NULL;
    if(punc == '('){
        if(!strcmp("fromCSV",func_or_var))     r = handleFromCSV(args);
        else if(!strcmp("filter",func_or_var)) r = handleFilter(args, variables_list);
        else if(!strcmp("proj",func_or_var))   r = handleProj(args, variables_list);
        else 
        if(!strcmp("businesses_started_by_letter",func_or_var))  r = handleBusinesses_started_by_letter(sgr, args);
        else if(!strcmp("business_info",func_or_var))                  r = handleBusiness_info(sgr, args);
        else if(!strcmp("businesses_reviewed",func_or_var))            r = handleBusinesses_reviewed(sgr, args);
        else if(!strcmp("businesses_with_stars_and_city",func_or_var)) r = handleBusinesses_with_stars_and_city(sgr, args);
        else if(!strcmp("top_businesses_by_city",func_or_var))         r = handleTop_businesses_by_city(sgr, args);
        else if(!strcmp("top_businesses_with_category",func_or_var))   r = handleTop_businesses_with_category(sgr, args);
        else if(!strcmp("reviews_with_word",func_or_var))              r = handleReviews_with_word(sgr, args);
        else if(!strcmp("international_users",func_or_var)) {
            if(sgr != NULL)
                r = international_users(sgr);
        }           
        else bad_syntax_err();
    }
    else if(punc == '[')
    {   
        //z = x[1][1]
        GSList *aux = search_variable(variables_list, func_or_var);
        TABLE table = ((VAR) aux->data)->table;
        r = handleDirectAcess(table, args);
    }
    else {bad_syntax_err(); return NULL;}
    return r;
}

void select_other_commands(char *str, int index, GSList *variables_list, SGR *sgr){
    char *tmp  = str,
         *func = trimwhitespace(tmp),
         *args = &(str[index]);
    int r = 0;
    if(!strcmp("load_sgr",func))   handleLoadSGR(sgr, args);
    else if(!strcmp("show",func))  r = handleShow(args, variables_list);
    else if(!strcmp("toCSV",func)) r = handleToCSV(args, variables_list);
    else {bad_syntax_err(); return;}
    
    if(r == -1) {bad_syntax_err(); return;}
}

/***** Help Command *****/
void help(){
    char *quit            = "-> quit : Termina o programa.\n\n",
         *load_str        = "-> load_sgr() : Carrega os dados na estrutura SGR. Os dados são lidos dos ficheiros(\"users.csv\", \"business.csv\" e \"reviews.csv\") presentes na pasta do executável.\n\n",     
         *load_str_args   = "-> load_sgr(<users file path>,<businesses file path>,<reviews file path>) : Carrega os dados na estrutura SGR. Dados lidos a partir dos filepaths fornecidos.\n\n",
         *toCSV_str       = "-> toCSV(<variavel>, <delim>, <path>) : Escreve o conteúdo de uma tabela num ficheiro. 'delim' é o caractér usado para separar cada campo de uma linha no ficheiro.\n\n",
         *show_str        = "-> show(<variavel>)  : Mostra o conteúdo presente numa variável, em forma de tabela.\n\n",
         *directAcess_str = "-> <variavel1> = <variavel2>[<linha da celula>][<coluna da celula>] : Aceder a registos/valores contidos numa determinada célula da tabela.\n\n",
         *fromCSV_str     = "-> <variavel>  = fromCSV(<file path>,\"<delim>\") : Lê para uma tabela o conteúdo de um ficheiro. 'delim' é o caractér usado para separar cada campo de uma linha no ficheiro.\n\n",
         *filter_str      = "-> <variavel>  = filter(<variavel>, <column name>, <value>, <oper>) : Filtra dados de uma tabela, dada uma coluna, um valor de comparação e um operador de comparação.\n\n",
         *proj_str        = "-> <variavel>  = proj(<variavel>, <column name 1> [, <column name 2>, ...]) : Obtem um subconjunto de colunas de uma tabela.\n\n",
         *bsbl_str        = "-> <variavel>  = businesses_started_by_letter(\"<letra>\")           : Determina a lista de nomes de negócios começados por uma determinada letra.\n\n",
         *bi_str          = "-> <variavel>  = business_info(<business id>)                      : Determina a informação de um negócio (nome, cidade, estado, stars, e número total reviews).\n\n",
         *br_str          = "-> <variavel>  = businesses_reviewed(<user id>)                    : Determina a lista de negócios aos quais um utilizador fez review.\n\n",
         *bwsac_str       = "-> <variavel>  = businesses_with_stars_and_city(<stars>, <city>)   : Determina a lista de negócios com um número igual ou superior de estrelas numa determinada cidade. O número de estrelas deve estar compreendido entre 0 e 5.\n\n",
         *tbbc_str        = "-> <variavel>  = top_businesses_by_city(<top>)                     : Determina a lista dos top n negócios em cada cidade.\n\n",
         *iu_str          = "-> <variavel>  = international_users()                             : Determina a lista de ids de utilizadores e o número total de utilizadores que tenham visitado mais de um estado.\n\n",
         *tbwc_str        = "-> <variavel>  = top_businesses_with_category(<top>, <category>)   : Determina a lista dos top n negócios que pertencem a uma determinada categoria.\n\n",
         *rww_str         = "-> <variavel>  = reviews_with_word(<word>)                         : Determina a lista de ids de reviews que referem uma palavra.\n\n";

    printString(quit); printString(load_str); printString(load_str_args); printString(toCSV_str); printString(show_str); printString(directAcess_str); printString(fromCSV_str); printString(filter_str); printString(proj_str); printString(bsbl_str); printString(bi_str); printString(br_str); printString(bwsac_str); printString(tbbc_str); printString(iu_str); printString(tbwc_str); printString(rww_str);
}


/***** Interpretador *****/

void interpretador(){
    SGR sgr = NULL;
    GSList *variables_list  = NULL;

    char   *input_buffer    = NULL,
           *tmp             = NULL; 
    size_t tam_input_buffer = 0;
    ssize_t n; 
    int index;

    char  *var_name;
    TABLE  var_table;
    while(1){
        printString("Introduza um comando: ");
        if((n = getline(&input_buffer,&tam_input_buffer,stdin)) > 0){
            //Elimina '\n' do final 
            if(input_buffer[n - 1] == '\n') input_buffer[n - 1] = '\0';

            //Funçoes que seguem podem alterar a string, para que seja possivel dar free posteriormente, usamos uma variavel tmp para percorrer a string
            tmp = input_buffer;

            if(!strncmp(tmp,"quit",4)) { free(input_buffer); free_variable_list(variables_list); free_sgr(sgr); return;}
            else if(!strncmp(tmp,"help",4)) help();
            else if((index = find_next_punc(tmp, 0)) > 0){
                if(tmp[index] == '='){
                    tmp[index++]   = '\0';
                    var_name       = trimwhitespace(tmp);
                    var_table      = select_toTable_command(sgr, variables_list, &(input_buffer[index]));
                    variables_list = insert_variable(variables_list, var_name, var_table);
                }
                else if(tmp[index] == '('){
                    tmp[index++] = '\0';
                    select_other_commands(tmp, index, variables_list, &sgr);
                }
                else {bad_syntax_err();}
            }
            else {bad_syntax_err();}

        }
        else perror("Ocorreu um erro! Tente novamente!\n");
    }
}