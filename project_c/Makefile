FLAGS= -std=c99 -Wextra -O2 -D_GNU_SOURCE
PKG = `pkg-config --cflags --libs glib-2.0`
COMPILER=gcc


all: intrepertador ui reviewsCatalog businessCatalog usersCatalog Reviews Business Users sgr table

	
intrepertador: intrepertador.c intrepertador.h
    $(COMPILER) $(FLAGS) -o intrepertador.o $(PKG) -c intrepertador.c

ui: ui.c ui.h
    $(COMPILER) $(FLAGS) -o ui.o $(PKG) -c ui.c


reviewsCatalog: reviewsCatalog.c reviewsCatalog.h
    $(COMPILER) $(FLAGS) -o reviewsCatalog.o $(PKG) -c reviewsCatalog.c

businessCatalog: businessCatalog.c businessCatalog.h
    $(COMPILER) $(FLAGS) -o businessCatalog.o $(PKG) -c businessCatalog.c

usersCatalog:  usersCatalog.c usersCatalog.h
    $(COMPILER) $(FLAGS) -o usersCatalog.o $(PKG) -c usersCatalog.c


Reviews: Reviews.c Reviews.h
    $(COMPILER) $(FLAGS) -o Reviews.o $(PKG) -c Reviews.c

Business: Business.c Business.h
    $(COMPILER) $(FLAGS) -o Business.o $(PKG) -c Business.c

Users: Users.c Users.h
    $(COMPILER) $(FLAGS) -o Users.o $(PKG) -c Users.c


sgr: sgr.c sgr.h
    $(COMPILER) $(FLAGS) -o sgr.o $(PKG) -c sgr.c

table: table.c table.h
    $(COMPILER) $(FLAGS) -o table.o $(PKG) -c table.c


memcheck:
	valgrind --leak-check=yes ./program

clean:
	rm -rf *.o program