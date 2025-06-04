import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Classe que se preocupa com transmitir informacao ao utilizador desta aplicacao
 */

public class VistaTestesApp {
    private Controlador controlador;
    private Menu MenuPrincipal;
    private Menu MenuEstado; //Menu usado para guardar e ler o estado.
    private Scanner sc;

    public VistaTestesApp (Controlador controlador){
        this.controlador   = controlador;
        this.MenuPrincipal = this.defineMenuPrincipal();
        this.MenuEstado    = this.defineMenuEstado();
        this.sc            = new Scanner(System.in);
    }

    private Menu defineMenuPrincipal(){
        //Definicao das opcoes do Menu
        Menu menuPrincipal = new Menu("Menu Queries", new String[] {"Query 1", "Query 2", "Query 3", "Query 4", "Query 5", "Query 6", "Query 7", "Query 8", "Query 9", "Query 10", "Estatisticas", "Ler ou guardar estado"});

        //Definicao das pre-condicoes para executar as diferentes opcoes
        for(int op = 1; op <= 11 ; op++)
            menuPrincipal.setPreCondition(op, this.controlador::gestReviewsInitialized);

        //Definicao dos handlers para cada Opcao
        menuPrincipal.setHandler(1, () -> handleQuery1());
        menuPrincipal.setHandler(2, () -> handleQuery2());
        menuPrincipal.setHandler(3, () -> handleQuery3());
        menuPrincipal.setHandler(4, () -> handleQuery4());
        menuPrincipal.setHandler(5, () -> handleQuery5());
        menuPrincipal.setHandler(6, () -> handleQuery6());
        menuPrincipal.setHandler(7, () -> handleQuery7());
        menuPrincipal.setHandler(8, () -> handleQuery8());
        menuPrincipal.setHandler(9, () -> handleQuery9());
        menuPrincipal.setHandler(10, () -> handleQuery10());
        menuPrincipal.setHandler(11, () -> handleEstatisticas());
        menuPrincipal.setHandler(12, () -> handleEstado());

        return menuPrincipal;
    }

    private Menu defineMenuEstado(){
        //Definicao das opcoes do Menu
        Menu menuEstado = new Menu("Menu do Estado", new String[] {"Ler ficheiros default","Ler ficheiros (modo texto)","Ler ficheiro objeto","Guardar em ficheiro objeto"});

        menuEstado.setHandler(1, () -> handleReadFromTextFilesDefault());
        menuEstado.setHandler(2, () -> handleReadFromTextFiles());
        menuEstado.setHandler(3, () -> handleReadFromObjectFile());
        menuEstado.setHandler(4, () -> handleSaveToObjectFile());

        menuEstado.setPreCondition(4, () -> !this.controlador.isGestReviewsNull());
        return menuEstado;
    }

    private void handleQuery1() {
        Crono.start();
        Collection<String> collection = this.controlador.query1();
        CommandLineTable clt = new CommandLineTable();
        clt.setHeaders("Ids dos Negocios sem avaliacoes");
        for (String s : collection){
            clt.addRow(s);
        }
        System.out.println("Tempo (Query 1): " + Crono.getTimeAsString());
        System.out.println("Numero de negocios: " + collection.size());
        clt.printTableByPage();
    }

    private void handleQuery2() {

        int mes, ano;


        System.out.print("Insira um mês válido (1 a 12): ");

        do {
            try {
                mes = sc.nextInt();
            } catch (java.util.NoSuchElementException e) {
                mes = -1;
            }
        }while(mes < 1 || mes > 12);

        System.out.print("Insira um ano válido (Não pode ser negativo) :");

        do {
            try {
                ano = sc.nextInt();
            } catch (java.util.NoSuchElementException e) {
                ano = -1;
            }
        }while(ano < 0);

        Crono.start();
        AbstractMap.SimpleEntry<Integer,Integer> entry = this.controlador.query2(mes,ano);
        System.out.println("Tempo (Query 2): " + Crono.getTimeAsString());

        System.out.println("Para o mês " + mes + "/" + ano + ", o número total de reviews e o número total de utilizadores distintos que as realizaram foram, respetivamente, " + entry.getKey() + " e " + entry.getValue() + ".");
    }

    private void handleQuery3() {

        String userID;

        System.out.print("Insira um utilizador válido: ");
        do {
            try {
                userID = sc.nextLine();
            } catch (java.util.NoSuchElementException e) {
                userID = "";
            }
        }while(userID.equals(""));

        //Verifica se o utilizador existe
        if(!this.controlador.userExist(userID)){
            System.out.println("Não existe qualquer utilizador com este id!");
            return;
        }


        Crono.start();
        List<QuintuploMesAnoReviewcountBusinesscountRating> lista = this.controlador.query3(userID);

        CommandLineTable clt = new CommandLineTable();
        clt.setHeaders("Mes","Ano","Nº de Avaliacoes","Nº de Negocios","Rating");
        for (QuintuploMesAnoReviewcountBusinesscountRating q : lista){
            clt.addRow(Integer.toString(q.getMes()),
                    Integer.toString(q.getAno()),
                    Integer.toString(q.getReviewCount()),
                    Integer.toString(q.getBusinessCount()),
                    Float.toString(q.getRating()));
        }
        System.out.println("Tempo (Query 3): " + Crono.getTimeAsString());

        clt.printTableByPage();
    }

    private void handleQuery4() {

        String businessID;

        System.out.print("Insira um negócio válido: ");
        do {
            try {
                businessID = sc.nextLine();
            } catch (java.util.NoSuchElementException e) {
                businessID = "";
            }
        }while(businessID.equals(""));

        //Verifica se o negocio existe
        if(!this.controlador.businessExist(businessID)){
            System.out.println("Não existe qualquer negócio com este id!");
            return;
        }

        Crono.start();
        List<QuintuploMesAnoReviewcountUsercountRating> lista = this.controlador.query4(businessID);

        CommandLineTable clt = new CommandLineTable();
        clt.setHeaders("Mes","Ano","Nº de Avaliacoes","Nº de Utilizadores","Rating");
        for (QuintuploMesAnoReviewcountUsercountRating q : lista){
            clt.addRow(Integer.toString(q.getMes()),
                    Integer.toString(q.getAno()),
                    Integer.toString(q.getReviewCount()),
                    Integer.toString(q.getUserCount()),
                    Float.toString(q.getRating()));
        }
        System.out.println("Tempo (Query 4): " + Crono.getTimeAsString());

        clt.printTableByPage();
    }

    private void handleQuery5() {

        String userID;

        System.out.print("Insira um utilizador válido: ");
        do {
            try {
                userID = sc.nextLine();
            } catch (java.util.NoSuchElementException e) {
                userID = "";
            }
        }while(userID.equals(""));

        //Verifica se o utilizador existe
        if(!this.controlador.userExist(userID)){
            System.out.println("Não existe qualquer utilizador com este id!");
            return;
        }

        Crono.start();
        List<PairBusNameNum> lista = this.controlador.query5(userID);

        CommandLineTable clt = new CommandLineTable();
        clt.setHeaders("Nomes dos Negócios","Nº de Negócios");
        for(PairBusNameNum p : lista){
            clt.addRow(p.getBusName(),Integer.toString(p.getBusRevCount()));
        }
        System.out.println("Tempo (Query 5): " + Crono.getTimeAsString());

        clt.printTableByPage();
    }

    private void handleQuery6() {

        int n;

        System.out.print("Insira um número positivo: ");

        do {
            try {
                n = sc.nextInt();
            } catch (java.util.NoSuchElementException e) {
                n = -1;
            }
        }while(n<0);

        Crono.start();
        TreeMap<Integer,List<Triple_BusinessId_ReviewCount_ReviewersCount>> tree = this.controlador.query6(n);

        CommandLineTable clt = new CommandLineTable();
        clt.setHeaders("Ano", "Top Negócios", "Nº de Reviews", "Nº de Utilizadores");
        for (int i :tree.keySet()){
            for(Triple_BusinessId_ReviewCount_ReviewersCount t : tree.get(i)){
                clt.addRow(Integer.toString(i),t.getBusinessId(),Integer.toString(t.getReviewCount()),Integer.toString(t.getReviewersCount()));
            }
        }
        System.out.println("Tempo (Query 6): " + Crono.getTimeAsString());

        clt.printTableByPage();
    }

    private void handleQuery7() {
        Crono.start();
        Map<String,List<PairBusIdNum>> map = this.controlador.query7();

        CommandLineTable clt = new CommandLineTable();
        clt.setHeaders("Cidade","Id do Negócio", "Nº de Avaliações");
        for (String key : map.keySet()){
            for (PairBusIdNum p : map.get(key)){
                clt.addRow(key,p.getBusId(),Integer.toString(p.getBusRevCount()));
            }
        }
        System.out.println("Tempo (Query 7): " + Crono.getTimeAsString());
        clt.printTableByPage();
    }

    private void handleQuery8() {

        int n;

        System.out.print("Insira um número positivo: ");

        do {
            try {
                n = sc.nextInt();
            } catch (java.util.NoSuchElementException e) {
                n = -1;
            }
        }while(n<=0);

        Crono.start();
        List<PairUserIdBusNum> lista = this.controlador.query8(n);

        CommandLineTable clt = new CommandLineTable();
        clt.setHeaders("Id do Utilizador","Nº de Negocios");
        for(PairUserIdBusNum p : lista){
            clt.addRow(p.getUserId(),Integer.toString(p.getNumBusReviewed()));
        }
        System.out.println("Tempo (Query 8): " + Crono.getTimeAsString());

        clt.printTableByPage();
    }

    private void handleQuery9() {

        int n;
        String businessID;

        System.out.print("Insira um número positivo: ");

        do {
            try {
                n = sc.nextInt();
            } catch (java.util.NoSuchElementException e) {
                n = -1;
            }
        }while(n<0);


        System.out.print("Insira o id de um negócio: ");

        do {
            try {
                businessID = sc.nextLine();
            } catch (java.util.NoSuchElementException e) {
                businessID = "";
            }
        }while(businessID.equals(""));

        //Verifica se o negocio existe
        if(!this.controlador.businessExist(businessID)){
            System.out.println("Não existe qualquer negócio com este id!");
            return;
        }

        Crono.start();
        List<TripleUserIdRevNumRating> lista = this.controlador.query9(businessID, n);

        CommandLineTable clt = new CommandLineTable();
        clt.setHeaders("Id Utilizadores","Nº de Avalicaoes","Classificação");
        for (TripleUserIdRevNumRating t : lista){
            clt.addRow(t.getUserId(),Integer.toString(t.getRevCount()),Float.toString(t.getRating()));
        }
        System.out.println("Tempo (Query 9): " + Crono.getTimeAsString());

        clt.printTableByPage();
    }

    private void handleQuery10() {
        Crono.start();
        Map<String,Map<String, List<ParBusIDRating>>> MapStates = this.controlador.query10();

        CommandLineTable clt = new CommandLineTable();
        clt.setHeaders("Estados","Cidades","Id de Negócios","Classificação");
        for (String estado : MapStates.keySet()){
            for (String cidade : MapStates.get(estado).keySet()){
                for (ParBusIDRating p : MapStates.get(estado).get(cidade)){
                    clt.addRow(estado,cidade,p.getBusId(),Float.toString(p.getRating()));
                }
            }
        }
        System.out.println("Tempo (Query 10): " + Crono.getTimeAsString());
        clt.printTableByPage();
    }

    /**
     * Imprime as estatisticas do GestReviews
     */
    public void handleEstatisticas(){
        Crono.start();
        String str = this.controlador.estatisticasString();
        System.out.println("Tempo (Estatisticas): " + Crono.getTimeAsString());
        System.out.println(str);
    }

    /**
     * Invoca o menu que permite guardar ou carregar o estado.
     */
    private void handleEstado(){
        this.MenuEstado.runOneTime();
    }


    /**
     * Handle de uma opcao do menuEstado. Consiste em ler os ficheiros por predefinição.
     */
    private void handleReadFromTextFilesDefault() {
        String delimFriends = null, aux;
        boolean readFriends;

        //Pergunta se pretende ler os amigos
        do {
            System.out.println("Pretende ler os amigos? (S/N)");
            aux = sc.nextLine();
        }while(!aux.equals("S") && !aux.equals("N"));

        readFriends = aux.equals("S");

        if(readFriends) {
            //Pergunta se é suposto dar parse aos friends
            do {
                System.out.println("Pretende dar parse aos amigos? (S/N)");
                aux = sc.nextLine();
            } while (!aux.equals("S") && !aux.equals("N"));

            if (aux.equals("S")) delimFriends = ",";
        }

        try {
            System.out.println("A carregar ...");
            Crono.start();
            this.controlador.readFromTextFiles("users_full.csv", ";" , readFriends, delimFriends, "business_full.csv", ";", ",", "reviews_1M.csv", ";");
            System.out.println("Tempo (Carregar Ficheiros): " + Crono.getTimeAsString());
        }
        catch (FileNotFoundException fnfe){
            System.out.println("\n\n Ficheiro não encontrado!");
        }
        catch (IOException ioe){
            System.out.println("\n\n Não foi possivel ler o fiheiro!");
        }
    }

    private void handleReadFromTextFiles() {

        String userFilePath, delimUsersFields, delimFriends = null, businessFilePath, delimBusFields,delimCategories, reviewFilePath, delimRevsFields, aux;
        boolean readFriends;

        System.out.println("\n\nInsira o caminho para o ficheiro dos utilizadores : \n");
        do {
            try {
                userFilePath = sc.nextLine();
            } catch (java.util.NoSuchElementException e) {
                userFilePath = "";
            }
        }while(userFilePath.equals("")); // || this.controlador.userExist(userID));

        System.out.println("\n\nInsira o delimitador dos usuários no ficheiro: ");
        do {
            try {
                delimUsersFields = sc.nextLine();
            } catch (java.util.NoSuchElementException e) {
                delimUsersFields = "";
            }
        }while(delimUsersFields.equals(""));

        //Pergunta se pretende ler os amigos
        do {
            System.out.println("Pretende ler os amigos? (S/N)");
            aux = sc.nextLine();
        }while(!aux.equals("S") && !aux.equals("N"));

        readFriends = aux.equals("S");

        if(readFriends) {
            System.out.println("\n\nInsira o delimitador dos amigos no ficheiro (Se não pretender dar parse aos amigos, escreva \"null\"): ");
            do {
                try {
                    delimFriends = sc.nextLine();
                    if (delimFriends.equals("null")) delimFriends = null;
                } catch (java.util.NoSuchElementException e) {
                    delimFriends = "";
                }
            } while ("".equals(delimFriends));
        }

        System.out.println("\n\nInsira o caminho para o ficheiro dos negocios : \n");
        do {
            try {
                businessFilePath = sc.nextLine();
            } catch (java.util.NoSuchElementException e) {
                businessFilePath = "";
            }
        }while(businessFilePath.equals(""));

        System.out.println("\n\nInsira o delimitador dos negócios no ficheiro: ");
        do {
            try {
                delimBusFields = sc.nextLine();
            } catch (java.util.NoSuchElementException e) {
                delimBusFields = "";
            }
        }while(delimBusFields.equals(""));

        System.out.println("\n\nInsira o delimitador das categorias: \n");
        do {
            try {
                delimCategories = sc.nextLine();
            } catch (java.util.NoSuchElementException e) {
                delimCategories = "";
            }
        }while(delimCategories.equals(""));

        System.out.println("\n\nInsira o caminho para o ficheiro das avaliações : \n");
        do {
            try {
                reviewFilePath = sc.nextLine();
            } catch (java.util.NoSuchElementException e) {
                reviewFilePath = "";
            }
        }while(reviewFilePath.equals(""));

        System.out.println("\n\nInsira o delimitador das avaliações no ficheiro: ");
        do {
            try {
                delimRevsFields = sc.nextLine();
            } catch (java.util.NoSuchElementException e) {
                delimRevsFields = "";
            }
        }while(delimRevsFields.equals(""));

        try {
            System.out.println("A carregar ...");
            Crono.start();
            this.controlador.readFromTextFiles(userFilePath, delimUsersFields, readFriends, delimFriends, businessFilePath, delimBusFields,delimCategories,reviewFilePath, delimRevsFields);
            System.out.println("Tempo (Carregar ficheiros): " + Crono.getTimeAsString());
        }
        catch (FileNotFoundException fnfe){
            System.out.println("\n\n Ficheiro não encontrado!");
        }
        catch (IOException ioe){
            System.out.println("\n\n Não foi possivel ler o fiheiro!");
        }
    }

    private void handleReadFromObjectFile() {

        String filename;

        System.out.println("\n\nInsira o nome : ");
        do {
            try {
                filename = sc.nextLine();
            } catch (java.util.NoSuchElementException e) {
                filename = "";
            }
        }while(filename.equals(""));

        try {
            System.out.println("A carregar ...");
            Crono.start();
            this.controlador.readFromObjectFiles(filename);
            System.out.println("Tempo (Carregar de ficheiro objeto): " + Crono.getTimeAsString());
        }
        catch (FileNotFoundException fnfe){
            System.out.println("\n\n Ficheiro não encontrado!");
        }
        catch (ClassNotFoundException cnfe){
            System.out.println("\n\n Ficheiro não corresponde ao objeto pretendido!");
        }
        catch (IOException ioe){
            System.out.println("\n\n Não foi possivel ler o fiheiro!");
        }
    }

    private void handleSaveToObjectFile() {

        String filename;

        System.out.println("\n\nInsira o nome : \n");
        do {
            try {
                filename = sc.nextLine();
            } catch (java.util.NoSuchElementException e) {
                filename = "";
            }
        }while(filename.equals(""));

        try {
            System.out.println("A carregar ...");
            Crono.start();
            this.controlador.saveToObjectFiles(filename);
            System.out.println("Tempo (Guardar em ficheiro objeto): " + Crono.getTimeAsString());
        } catch (IOException ioe) {
            System.out.println("\n\n Não foi possivel ler o fiheiro!");
        }

    }

    /**
     * Inicia a Vista
     */
    public void run() {
        //Corre o menu estado uma vez
        this.MenuEstado.runOneTime();

        //Se a opcao for "0" então o utilizador escolheu sair
        if(this.MenuEstado.getLastOption() == 0)
            System.out.println("Até a uma próxima!");
        else {
            this.MenuPrincipal.run();
            System.out.println("Até a uma próxima!");
        }
    }

}
