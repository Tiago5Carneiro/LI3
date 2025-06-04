import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe onde sao catalogadas as reviews, utilizando um map com os Ids dos users como chave e outro utilizando os Ids do Negocios como chave
 *
 */

public class ReviewCatalog implements CatalogoAvaliacoes, Serializable {
    private final Map<String, Set<Avaliacao>> mapUserRev;
    private final Map<String, Set<Avaliacao>> mapBusRev;

    public ReviewCatalog() {
        this.mapUserRev = new HashMap<>();
        this.mapBusRev  = new HashMap<>();
    }

    @Override
    public int addAvaliacao(Avaliacao avaliacao) {
        int r = 0;
        Avaliacao avClone = avaliacao.Clone();

        //Insercao na HashTable cuja key é o user id
        String userId = avClone.getUserId();
        Set<Avaliacao> userSet = this.mapUserRev.get(userId);

        //Caso ainda não tenham sido adicionadas reviews deste user
        if(userSet == null){
            r++; // 1 é o valor a adicionar se for a primeira review adicionada do user fornecido
            userSet = new TreeSet<>(Avaliacao.dateComparator);
            this.mapUserRev.put(userId, userSet);
        }
        userSet.add(avClone);

        //Insercao na HashTable cuja key é o business id
        String busId = avClone.getBusinessId();
        Set<Avaliacao> busSet = this.mapBusRev.get(busId);

        //Caso ainda não tenham sido adicionadas reviews deste user
        if(busSet == null){
            r += 2; // 2 é o valor a adicionar se for a primeira review adicionada ao business fornecido
            busSet = new TreeSet<>(Avaliacao.dateComparator);
            this.mapBusRev.put(busId, busSet);
        }
        busSet.add(avClone);

        return r;
    }

    public boolean isReviewer(String userId) { return this.mapUserRev.containsKey(userId); }

    /**
     * Serve para verificar se e a primeira review do mes para o utilizador que a escreveu.
     * Esta funcao e usada antes da avaliacao ser adicionada ao catalogo.
     * @param avaliacao
     * @return booleano que indica se e a primeira review do mes para o utilizador que a escreveu.
     */
    public boolean isUserFirstReviewOfTheMonth(Avaliacao avaliacao) {
        TreeSet<Avaliacao> avaliacoes = (TreeSet<Avaliacao>) this.mapUserRev.get(avaliacao.getUserId());
        if(avaliacoes == null) return true;
        else{
            Avaliacao avLower = avaliacoes.lower(avaliacao);
            Avaliacao avUpper = avaliacoes.higher(avaliacao);
            return !( (avLower != null && Avaliacao.hasSameMonthAndYear(avLower,avaliacao)) || (avUpper != null && Avaliacao.hasSameMonthAndYear(avUpper,avaliacao)) );
        }
    }

    public TreeSet<Avaliacao> getUserReviewsOrderedPerDateInTreeSet(String userId) {
        TreeSet<Avaliacao> newTreeSet = new TreeSet<>(Avaliacao.dateComparator);

        for (Avaliacao av : this.mapUserRev.get(userId)) newTreeSet.add(av.Clone());

        return newTreeSet;
    }

    public TreeSet<Avaliacao> getBusinessReviewsOrderedPerDateInTreeSet(String businessId) {
        TreeSet<Avaliacao> newTreeSet = new TreeSet<>(Avaliacao.dateComparator);

        for (Avaliacao av : this.mapBusRev.get(businessId)) newTreeSet.add(av.Clone());

        return newTreeSet;
    }

    public Set<Map.Entry<String, Integer>> getReviewedBusinessesPlusNumberOfReviews(String userId) {
        Set<Avaliacao> avaliacoes = this.mapUserRev.get(userId);
        if(avaliacoes == null) return null;

        Map<String,Integer> pares = new HashMap<>(); //Map onde serao guardados os pares de business id e review count
        String busId;
        Integer reviewCount;

        //Insercao dos busId e do número de vezes que foram avaliados
        for(Avaliacao a : avaliacoes){
            busId = a.getBusinessId();
            reviewCount = pares.get(busId);
            if(reviewCount != null)
                pares.put(busId, ++reviewCount);
            else
                pares.put(busId, 1);
        }

        return pares.entrySet();
    }

    public boolean businessReviewed(String businessId) { return this.mapBusRev.containsKey(businessId); }

    public List<PairBusIdNum> getAllBusinessesReviewCount(){
        List<PairBusIdNum> ls = new ArrayList<>();

        for(Map.Entry<String,Set<Avaliacao>> entry : this.mapBusRev.entrySet())
            ls.add(new PairBusIdNum(entry.getKey(),entry.getValue().size()));

        return ls;
    }

    //Query 3
    //Dado um código de utilizador, determinar, para cada mês, quantas reviews fez,
    //quantos negócios distintos avaliou e que nota média atribuiu
    public List<QuintuploMesAnoReviewcountBusinesscountRating> getUserMonthlyStats(String userId){
        TreeSet<Avaliacao> set = (TreeSet<Avaliacao>) this.mapUserRev.get(userId);
        if(set == null || set.size() == 0) return null;

        List<QuintuploMesAnoReviewcountBusinesscountRating> ls = new ArrayList<>(); //Lista a retornar com os quintuplos
        int mes = 0, ano = 0, /* Mes e ano de referencia */
                mesAux, anoAux;
        int reviewCount = 0; //Review Count do mes e ano de referencia
        float sumRating = 0; //Acumulacao do rating do mes e ano de referencia
        HashSet<String> businesses = new HashSet<>(); //Usado para guardar os diferentes businessesIds, servindo desta forma de contador de businesses distintos
        Avaliacao av;

        Iterator<Avaliacao> it = set.iterator();
        if(it.hasNext()){
            av  = it.next();
            mes = av.getDate().getMonthValue();
            ano = av.getDate().getYear();
            sumRating = av.getStars();
            reviewCount = 1;
            businesses.add(av.getBusinessId());


            while(it.hasNext()){
                av = it.next();
                mesAux = av.getDate().getMonthValue();
                anoAux = av.getDate().getYear();

                if(mes == mesAux && ano == anoAux){
                    reviewCount++;
                    sumRating += av.getStars();
                }
                else{
                    float rating = sumRating / reviewCount;
                    ls.add(new QuintuploMesAnoReviewcountBusinesscountRating(mes, ano, reviewCount, businesses.size(), rating));
                    mes = mesAux;
                    ano = anoAux;
                    reviewCount = 1;
                    sumRating   = av.getStars();
                    businesses.clear();
                    //System.gc(); //To clean the previous hashsets memory allocations(Gasta muito tempo, usar dps de executar a query)
                }
                businesses.add(av.getBusinessId());
            }

            //Adiciona os ultimos dados
            ls.add(new QuintuploMesAnoReviewcountBusinesscountRating(mes, ano, reviewCount, businesses.size(), sumRating / businesses.size()));
        }

        return ls;
    }

    //Query 4
    //Dado um código de negocio, determinar, para cada mês, quantas reviews dela é feita,
    //quantos utilizadores distintos o avaliaram e que nota média foi lhe atribuida
    public List<QuintuploMesAnoReviewcountUsercountRating> getBusinessMonthlyStats(String businessId){
        TreeSet<Avaliacao> set = (TreeSet<Avaliacao>) this.mapBusRev.get(businessId);
        if(set == null || set.size() == 0) return null;

        List<QuintuploMesAnoReviewcountUsercountRating> ls = new ArrayList<>(); //Lista a retornar com os quintuplos
        int mes, ano, mesAux, anoAux;
        int reviewCount = 0; //Review Count do mes e ano de referencia
        float sumRating = 0; //Acumulacao do rating do mes e ano de referencia
        HashSet<String> users = new HashSet<>(); //Usado para guardar os diferentes userIds, servindo desta forma de contador de users distintos
        Avaliacao av;

        Iterator<Avaliacao> it = set.iterator();
        if(it.hasNext()){
            av  = it.next();
            mes = av.getDate().getMonthValue();
            ano = av.getDate().getYear();
            sumRating = av.getStars();
            reviewCount = 1;
            users.add(av.getUserId());

            while(it.hasNext()){
                av = it.next();
                mesAux = av.getDate().getMonthValue();
                anoAux = av.getDate().getYear();

                if(mes == mesAux && ano == anoAux){
                    reviewCount++;
                    sumRating += av.getStars();
                }
                else{
                    float rating = sumRating / reviewCount;
                    ls.add(new QuintuploMesAnoReviewcountUsercountRating(mes, ano, reviewCount, users.size(), rating));
                    mes = mesAux;
                    ano = anoAux;
                    reviewCount = 1;
                    sumRating   = av.getStars();
                    users.clear();
                }
                users.add(av.getUserId());
            }

            //Adiciona os ultimos dados
            ls.add(new QuintuploMesAnoReviewcountUsercountRating(mes, ano, reviewCount, users.size(), sumRating / users.size()));
        }

        return ls;
    }


    //Percorre cada set de reviews dos businesses, e insere no TreeSet do respetivo ano,
    // as informacoes do número de reviews feitas e do número de users distintos que o avaliaram.
    //Os TreeSets estao organizados por ordem decrescente do número de reviews
    public Map<Integer,TreeSet<Triple_BusinessId_ReviewCount_ReviewersCount>> getBusinesses_ReviewCount_ReviewersCount_perYear(){
        HashMap<Integer,TreeSet<Triple_BusinessId_ReviewCount_ReviewersCount>> map = new HashMap<>();

        //Obtem o TreeSet de reviews de cada business
        for(Map.Entry<String, Set<Avaliacao>> par_Id_Review : this.mapBusRev.entrySet()){
            String businessId = par_Id_Review.getKey();

            //iterator do treeset do business
            Iterator<Avaliacao> it = par_Id_Review.getValue().iterator();

            //Variaveis auxiliares
            int ano, anoAux;
            int reviewCount; //Review Count do ano de referencia
            HashSet<String> distinctUsers = new HashSet<>(); //Usado para guardar os diferentes userIds, servindo desta forma de contador de users distintos
            Avaliacao av;
            TreeSet<Triple_BusinessId_ReviewCount_ReviewersCount> triples;

            if(it.hasNext()){
                av  = it.next();
                ano = av.getDate().getYear();
                reviewCount = 1;
                distinctUsers.add(av.getUserId());

                while(it.hasNext()){
                    av = it.next();
                    anoAux = av.getDate().getYear();

                    if(ano == anoAux)
                        reviewCount++;
                    else{
                        triples = map.get(ano);
                        if(triples == null) {
                            triples = new TreeSet<>(Triple_BusinessId_ReviewCount_ReviewersCount.reviewCountComparator.reversed());
                            map.put(ano, triples);
                        }
                        triples.add(new Triple_BusinessId_ReviewCount_ReviewersCount(businessId,reviewCount,distinctUsers.size()));
                        ano = anoAux;
                        reviewCount = 1;
                        distinctUsers.clear();
                    }
                    distinctUsers.add(av.getUserId());
                }

                triples = map.get(ano);
                if(triples == null) {
                    triples = new TreeSet<>(Triple_BusinessId_ReviewCount_ReviewersCount.reviewCountComparator.reversed());
                    map.put(ano, triples);
                }
                triples.add(new Triple_BusinessId_ReviewCount_ReviewersCount(businessId,reviewCount,distinctUsers.size()));
            }
        }

        return map;
    }

    public TreeSet<PairUserIdBusNum> getBusinesses_ReviewCount_perUser() {
        TreeSet<PairUserIdBusNum> map = new TreeSet<>(PairUserIdBusNum.comparatorBusCount);

        //Obtem o TreeSet de reviews de cada business
        for (Map.Entry<String, Set<Avaliacao>> par_Id_Review : this.mapUserRev.entrySet()) {
            String userId = par_Id_Review.getKey();

            //iterator do treeset do business
            Iterator<Avaliacao> it = par_Id_Review.getValue().iterator();

            //Variaveis auxiliares
            Avaliacao av;
            Set<String> distinctBusinesses = new HashSet<>(); //Usado para guardar os diferentes userIds, servindo desta forma de contador de negocios distintos

            while (it.hasNext()) {
                av = it.next();
                distinctBusinesses.add(av.getBusinessId());
            }

            map.add(new PairUserIdBusNum(userId, distinctBusinesses.size()));

            distinctBusinesses.clear();
        }

        return map;
    }

    public TreeSet<TripleUserIdRevNumRating> getUserReviewAvgStarsPerBusiness(String businessID) {
        TreeSet<TripleUserIdRevNumRating> set = new TreeSet<>(TripleUserIdRevNumRating.comparatorRevCount);

        Set<Avaliacao> BusinessReviews = this.mapBusRev.get(businessID);

        Map<String,Map.Entry<Integer,Float>> user_RevStarCount = new HashMap<>();

        for (Avaliacao av : BusinessReviews) {

            String user = av.getUserId();

            if (!user_RevStarCount.containsKey(user)) {
                Map.Entry<Integer,Float> info = new AbstractMap.SimpleEntry<>(1,av.getStars());
                user_RevStarCount.put(user, info);
            }
            else{
                int reviewCounts = 1 + user_RevStarCount.get(user).getKey();
                float totalStars = av.getStars() + user_RevStarCount.get(user).getValue();

                Map.Entry<Integer,Float> info = new AbstractMap.SimpleEntry<>(reviewCounts,totalStars);

                user_RevStarCount.replace(user, info);
            }
        }

        if(!user_RevStarCount.isEmpty()) {
            for(Map.Entry<String,Map.Entry<Integer,Float>> entry : user_RevStarCount.entrySet()){
                int revCount = entry.getValue().getKey();
                float sumRating = entry.getValue().getValue();
                set.add(new TripleUserIdRevNumRating(entry.getKey(), revCount, sumRating / revCount)); //adiciona ao set o triplo (user id, review count e clasificação média)
            }
        }

        user_RevStarCount.clear();
        return set;
    }

    public List<ParBusIDRating> getBusIDsAndRating (){
        List <ParBusIDRating> lista = new ArrayList<>();
        for ( Map.Entry<String, Set<Avaliacao>> entry: this.mapBusRev.entrySet()){
            float rating = 0;
            for (Avaliacao a : entry.getValue()) {
                rating += a.getStars();
            }
           lista.add(new ParBusIDRating(entry.getKey(), rating / entry.getValue().size()));
        }
        return lista;
    }
}
