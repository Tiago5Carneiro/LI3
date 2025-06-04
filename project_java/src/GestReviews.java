import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class GestReviews implements Serializable {
    private final CatalogoUtilizadores uc;
    private final CatalogoNegocios bc;
    private final CatalogoAvaliacoes rc;
    private final Estatisticas stats;


    /**
     * Contrutor do GestReviews
     */
    public GestReviews() {
        this.uc = new UserCatalog();
        this.bc = new BusinessCatalog();
        this.rc = new ReviewCatalog();
        this.stats = new Stats();
    }

    /**
     * Preenche as estruturas de dados recorrendo a ficheiros de texto com utilizadores, negocios e avaliacoes.
     * @param userFilePath Caminho para o ficheiro de texto que contem os utilizadores.
     * @param delimUsersFields Delimitador dos campos de utilizador. Os utilizadores devem ter os seguintes campos, id, nome e amigos, pela ordem respetiva.
     * @param delimFriends Delimitador dos amigos.
     * @param businessFilePath Caminho para o ficheiro de texto que contem os negocios.
     * @param delimBusFields Delimitador dos campos de negocio. Os negocios devem ter os seguintes campos, id, nome, cidade, estado e categorias, pela ordem respetiva.
     * @param delimCategories Delimitador das categorias.
     * @param reviewFilePath Caminho para o ficheiro de texto que contem os avaliacoes.
     * @param delimRevsFields Delimitador dos campos da avaliacao. As avaliacoes devem ter os seguintes campos, id, id do avaliador, id do negocio avaliado,
     *                        classificacao, numero de vezes que foi util a outros utilizadores, número de vezes que outros utilizadores a acharam engraçada,
     *                        numero de vezes que outros utilizadores a acharam fixe, a data em que foi redigida e por fim o texto, pela ordem respetiva.
     * @param ignoreFirstLine Booleano, cujo valor verdadeiro, implica ignorar a primeira linha de todos os ficheiros (cabeçalho).
     * @throws FileNotFoundException Caso algum dos nomes de ficheiro seja invalido.
     * @throws IOException Caso exista qualquer erro, diferente do anterior.
     */
    public void readFiles(String userFilePath, String delimUsersFields, boolean readFriends, String delimFriends, String businessFilePath, String delimBusFields, String delimCategories, String reviewFilePath, String delimRevsFields, boolean ignoreFirstLine) throws FileNotFoundException, IOException {
        //Inicializacao de variaveis
        String line = null;
        int reviewCount = 0, wrongReviewCount = 0, businessCount = 0, businessesReviewedCount = 0
                ,userCount   = 0, reviewersCount   = 0, nonImpactfulReviewsCount = 0;
        float sumAllRatings = 0; //Somatório de todos os ratings das reviews

        //Parse Users
        BufferedReader userFile = new BufferedReader(new FileReader(userFilePath));
        if(ignoreFirstLine) userFile.readLine();
        while((line = userFile.readLine()) != null)
            if(this.uc.addUtilizador(line, delimUsersFields, readFriends, delimFriends))
                userCount++;

        //Parse Businesses
        BufferedReader busFile  = new BufferedReader(new FileReader(businessFilePath));
        if(ignoreFirstLine) busFile.readLine();
        while((line = busFile.readLine()) != null)
            if(this.bc.addNegocio(line, delimBusFields, delimCategories))
                businessCount++;

        //Parse Reviews
        BufferedReader revFile  = new BufferedReader(new FileReader(reviewFilePath));
        if(ignoreFirstLine) revFile.readLine();
        while((line = revFile.readLine()) != null){
            Avaliacao av = Review.newAvaliacao(line, delimRevsFields);

            if(av != null && this.uc.containsUser(av.getUserId()) && this.bc.containsBusiness(av.getBusinessId())) {
                //Atualiza Stats (+ Precisamente se é a primeira review do mês para esse utilizador)
                LocalDateTime dateTime = av.getDate();
                this.stats.addReviewStats(dateTime.getMonthValue(), dateTime.getYear(), av.getStars(), this.rc.isUserFirstReviewOfTheMonth(av));

                switch (this.rc.addAvaliacao(av)){
                    case 1:
                        reviewersCount++;
                        break;
                    case 2 :
                        businessesReviewedCount++;
                        break;
                    case 3 :
                        businessesReviewedCount++;
                        reviewersCount++;
                        break;
                    default :
                        break;
                }
                reviewCount++; sumAllRatings += av.getStars();
                if(!av.isImpactful()) nonImpactfulReviewsCount++;
            }
            else wrongReviewCount++;
        }

        //Stats
        this.stats.addFilePath(userFilePath);
        this.stats.setUserCount(userCount);
        this.stats.setReviewersCount(reviewersCount);
        this.stats.setNonReviewersCount(userCount - reviewersCount);

        this.stats.addFilePath(businessFilePath);
        this.stats.setBusinessCount(businessCount);
        this.stats.setBusinessesReviewedCount(businessesReviewedCount);
        this.stats.setBusinessesNotReviewedCount(businessCount - businessesReviewedCount);

        this.stats.addFilePath(reviewFilePath);
        this.stats.setGlobalReviewCount(reviewCount);
        this.stats.setWrongReviewCount(wrongReviewCount);
        this.stats.setNonImpactfulReviewsCount(nonImpactfulReviewsCount);
        this.stats.setGlobalAverageRating(sumAllRatings / (float) reviewCount);
    }

    /**
     * Le um ficheiro objeto.
     * @param fileName Nome do ficheiro objeto.
     * @return o objeto GestReviews lido do ficheiro.
     * @throws FileNotFoundException Caso nao exista um ficheiro com este nome.
     * @throws IOException Caso ocorra outro erro relativo ao input/output.
     * @throws ClassNotFoundException Quando nao e encontrada a classe que se esta a tentar ler do ficheiro.
     */
    public static GestReviews readObjectFile(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
        GestReviews gestReviews = (GestReviews) ois.readObject();
        ois.close();
        return gestReviews;
    }

    /**
     * Escreve para ficheiro objeto.
     * @param fileName Nome do ficheiro onde se pretende guardar o estado.
     * @throws IOException Caso ocorra algum erro relativo ao input/output.
     */
    public void writeToObjectFile(String fileName) throws IOException{
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
        oos.writeObject(this);
        oos.flush();
        oos.close();
    }

    /** Queries **/

    /**
     * Query 1
     * Ordena e retorna os ids dos negocios nao avaliados.
     * @return lista de negocios nao avaliados.
     */
    public Collection<String> getIdOfNonReviewedBusinessesAlphabeticallyOrdered() {
        List<String> ls = this.bc.getAllIdsAsList();
        return ls.stream().filter(id -> !this.rc.businessReviewed(id)).sorted().collect(Collectors.toList());
    }

    /**
     * Query 2 :
     * Dado um mes e um ano, retorna par com o numero total global de reviews
     * realizadas e o numero total de users distintos que as realizaram
     */
    public AbstractMap.SimpleEntry<Integer,Integer> getMonthGlobalReviewCountAndDistinctUsersCount (int mes, int ano){
        return new AbstractMap.SimpleEntry<>(this.stats.getMonthlyNumberOfReviews(mes, ano), this.stats.getMonthlyActiveUsers(mes, ano));
    }

    /**
     * Query 3 :
     * Dado um codigo de utilizador, determinar, para cada mes, quantas reviews fez,
     * quantos negocios distintos avaliou e que nota media atribuiu
     * @param userId
     * @return uma lista de quintuplos com as informacoes mensais do utilizador fornecido. Cada quintuplo corresponde a um mes diferente, e contem o mes, o ano, o numero de avaliacoes feitas, o numero de negocios diferentes avaliados e a classificacao media.
     */
    public List<QuintuploMesAnoReviewcountBusinesscountRating> getUserMonthlyStats(String userId){
        return this.rc.getUserMonthlyStats(userId);
    }

    /**
     * Query 4 :
     * Dado um codigo de negocio, determinar, para cada mes, quantas reviews dela e feita,
     * quantos utilizadores distintos o avaliaram e que nota media foi lhe atribuida
     * @param businessId
     * @return uma lista de quintuplos com as informacoes mensais do negocio fornecido. Cada quintuplo corresponde a um mes diferente, e contem o mes, o ano, o numero de avaliacoes feitas, o numero de avaliadores diferentes e a classificacao media.
     */
    public List<QuintuploMesAnoReviewcountUsercountRating> getBusinessMonthlyStats(String businessId){
        return this.rc.getBusinessMonthlyStats(businessId);
    }

    /**
     * Query 5 :
     * Dado o codigo de um utilizador determinar a lista de nomes de negocios que mais
     * avaliou (e quantos), ordenada por ordem decrescente de quantidade e, para
     * quantidades iguais, por ordem alfabetica dos negocios
     * @param userId
     * @return uma lista de pares. Os pares contem o nome do negocio e o numero de reviews feitas pelo utilizador a esse negocio.
     */
    public List<PairBusNameNum> getReviewedBusinessesOrderedByNumberOfReviews(String userId){
        Set<Map.Entry<String,Integer>> set = this.rc.getReviewedBusinessesPlusNumberOfReviews(userId);
        if(set == null) return null;

        List<PairBusNameNum> ls = new ArrayList<>();

        for(Map.Entry<String,Integer> entry : set)
            ls.add(new PairBusNameNum(this.bc.getBusinessName(entry.getKey()) + " (" + entry.getKey() + ")",entry.getValue()));

        ls.sort(PairBusNameNum.comparatorBusRevCount);

        return ls;
    }

    /**
     * Query 6 :
     * Determina o conjunto dos N negocios mais avaliados (com mais reviews) em cada
     * ano, indicando o numero total de distintos utilizadores que o avaliaram (N e um
     * inteiro dado pelo utilizador)
     * @param N Numero de negocios , pretendido, mais avaliados por ano
     * @return um TreeMap cuja key e o ano, e o value consiste em listas de triplos. Os triplos contem o id do negocio, o numero de reviews e o numero de avaliadores diferentes.
     */
    public TreeMap<Integer,List<Triple_BusinessId_ReviewCount_ReviewersCount>> get_N_mostReviewedBusinessesPerYear (int N){
        TreeMap<Integer,List<Triple_BusinessId_ReviewCount_ReviewersCount>> newMap = new TreeMap<>(Comparator.naturalOrder());

        //Percorre todas as entries e copia para um novo treeMap os N (ou menos) primeiros triplos de cada ano
        Map<Integer,TreeSet<Triple_BusinessId_ReviewCount_ReviewersCount>> map = this.rc.getBusinesses_ReviewCount_ReviewersCount_perYear();

        for(Map.Entry<Integer,TreeSet<Triple_BusinessId_ReviewCount_ReviewersCount>> entry : map.entrySet()){
            List<Triple_BusinessId_ReviewCount_ReviewersCount> ls = new ArrayList<>(); //lista onde serão guardados os negócios mais avaliados de cada ano

            TreeSet<Triple_BusinessId_ReviewCount_ReviewersCount> treeSet = entry.getValue(); //Set com as informacoes de todos os negocios avaliados no dado ano (key do TreeMap)

            //Copia dos N (ou menos) primeiros triplos do dado ano
            Triple_BusinessId_ReviewCount_ReviewersCount triple;
            for(int i = 0; i < N && (triple = entry.getValue().pollFirst()) != null ; i++)
                ls.add(triple);

            newMap.put(entry.getKey(),ls);

            //Clear do treeSet
            treeSet.clear();
        }

        //Clear do Map vindo de getBusinesses_ReviewCount_ReviewersCount_perYear
        map.clear();

        return newMap;
    }

    /**
     * Query 7 :
     * Determina, para cada cidade, a lista dos N mais famosos negocios em termos de numero de reviews
     * @param N Numero de negocios, pretendido, mais famosos em termos de numero de reviews
     * @return Map cuja key e o nome da cidade, e o value e uma lista de pares que contem o id do negocio e o numero de reviews feitas a esse.
     */
    public Map<String,List<PairBusIdNum>> get_N_MostFamousBusinessesPerCity(int N){
        List<PairBusIdNum> id_RevCount_pairs = this.rc.getAllBusinessesReviewCount();
        if(id_RevCount_pairs == null) return null;

        //Agrupados todos os pares pela respetiva cidade
        Map<String,TreeSet<PairBusIdNum>> pairs_Id_ReviewCount_perCity = new HashMap<>();

        String city; TreeSet<PairBusIdNum> set;

        for(PairBusIdNum pair : id_RevCount_pairs){
            city = this.bc.getBusinessCity(pair.getBusId());

            //Se ainda nao existir um set para a cidade
            if((set = pairs_Id_ReviewCount_perCity.get(city)) == null){
                set = new TreeSet<>(PairBusIdNum.comparatorRevCount);
                pairs_Id_ReviewCount_perCity.put(city,set);
            }

            set.add(pair);
        }

        //Passagem para um novo map os N mais famosos de cada cidade
        Map<String,List<PairBusIdNum>> N_famous_pairs_Id_ReviewCount_perCity = new HashMap<>();

        for(Map.Entry<String,TreeSet<PairBusIdNum>> entry : pairs_Id_ReviewCount_perCity.entrySet()){
            //Cópia dos N mais famosos da cidade para uma lista
            List<PairBusIdNum> ls = new ArrayList<>();
            PairBusIdNum pair;
            for(int i = 0; i < N && (pair = entry.getValue().pollFirst()) != null; i++)
                ls.add(pair);

            //Insercao da lista no novo map
            N_famous_pairs_Id_ReviewCount_perCity.put(entry.getKey(), ls);

            //Teste
            entry.getValue().clear();
        }

        return N_famous_pairs_Id_ReviewCount_perCity;
    }

    /**
     * Query 8
     * Determina os N utlizadores que mais avaliaram negocios distintos
     * @param N Numero de utilizadores pretendidos que mais avaliaram negocios distintos
     * @return
     */
    public List<PairUserIdBusNum> topN_Reviewers(int N) {
        TreeSet<PairUserIdBusNum> User_ReviewsCount = this.rc.getBusinesses_ReviewCount_perUser();

        List<PairUserIdBusNum> topN = new ArrayList<>();

        if(User_ReviewsCount.isEmpty()) return topN;

        for(PairUserIdBusNum pair : User_ReviewsCount){
            topN.add(pair);
            N--;
            if(N==0) return topN;
        }
        return  topN;
    }

    /**
     * Query 9
     * Determina os N utilizadores que mais avaliaram um negócio específico
     * @param N Numero de utlizadores, pretendido, que mais avaliaram um negocio especifico
     * @param businessID Id do negocio
     * @return lista de triplos que contem o id do utilizador, o numero de reviews feitas por este e a classificao media.
     */
    public List<TripleUserIdRevNumRating> topN_SpecificBusinessReviewers(int N, String businessID) {
        TreeSet<TripleUserIdRevNumRating> UserReviewsCount = this.rc.getUserReviewAvgStarsPerBusiness(businessID);

        List<TripleUserIdRevNumRating> topN = new ArrayList<>();

        if(UserReviewsCount.isEmpty()) return topN;

        for(TripleUserIdRevNumRating triple : UserReviewsCount){
            topN.add(triple);
            N--;
            if(N==0) return topN;
        }
        return  topN;
    }

    //Query10

    /**
     * Query 10 :
     * Determina para cada estado, cidade a cidade, a media de classificacao de cada negocio.
     * @return um map cuja key e o estado, e o value e outro map cuja key e a cidade e o value e uma lista de pares. Os pares contem o id do business e a sua classificao media.
     */
    public Map<String,Map<String, List<ParBusIDRating>>> organizedBusinessAndAvgStars(){

        Map<String,Map<String, List <ParBusIDRating>>> MapStates = new HashMap<>();

        for(ParBusIDRating pares : this.rc.getBusIDsAndRating()){

            Map<String, List<ParBusIDRating>> MapCities;

            String estado = this.bc.getBusinessState(pares.getBusId());
            String cidade = this.bc.getBusinessCity(pares.getBusId());

            if (!MapStates.containsKey(estado)) {

                MapCities = new HashMap<>();
                List<ParBusIDRating> lista = new ArrayList<>();

                lista.add(pares);
                MapStates.put(estado,MapCities);
                MapCities.put(cidade,lista);

            }
            else {
                MapCities = MapStates.get(estado);

                if (MapCities.containsKey(cidade)) MapCities.get(cidade).add(pares);

                else {

                    List <ParBusIDRating> lista = new ArrayList<>();
                    lista.add(pares);
                    MapStates.get(estado).put(cidade, lista);
                }
            }
        }
        return MapStates;
    };

    /**
     *
     */
    public String estatisticasString(){
        return this.stats.toString();
    }

    /**
     * Verifica se existe um negocio com o id fornecido.
     */
    public boolean containsBus(String businessID){ return this.bc.containsBusiness(businessID);}

    /**
     * Verifica se existe um utilizador com o id fornecido
     */
    public boolean containsUser(String userID){ return this.uc.containsUser(userID);}
}

