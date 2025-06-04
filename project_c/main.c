#include "businessCatalog.h"
#include "usersCatalog.h"
#include "sgr.h"
#include <stdio.h>
#include "interpretador.h"

//gcc *.c $GLIB -std=c99 -Wextra -D_GNU_SOURCE -O2 -ggdb -Wall

int main(){
    interpretador();
    return 0;
}