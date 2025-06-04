#ifndef UI_H
#define UI_H

//Dá print a uma string.
void printString(char *str);

void bad_syntax_err();

void printCheckArgs();

void printReadingFromDefaultFolder();

void printReadingFiles();

void check_load_sgr();

void printFreeingLastSgr();

void printFailReadSgr();

void printVariavelSubstituida(char *variable_name);

void printVariavelAdicionada(char *variable_name);

void printInvalidVariable();

void printFailReadingToTable();

void printTableLength(TABLE t);

void printInvalidID();

void printWordNotFound();

#endif