import java.io.Serializable;
import java.util.*;

public interface CatalogoAvaliacoes extends Serializable {
    /* Adiciona Review ao catalogo
     * Retorna :
     *      1 se for a primeira review do utilizador
     *      2 se for a primeira review do negocio
     *      3 se ambas as anteriores forem verdade
     *      0 se nenhuma das anteriores forem verdade
     */
    int addAvaliacao(Avaliacao avaliacao);
    //int addAvaliacao(String nonParsedAvaliacao, String delim);
    //void addAvaliacao(String id, String userId, String businessId, float stars, int useful, int funny, int cool, LocalDateTime date, String text);

    /** User Related **/
    boolean isReviewer(String userId); //Retorna true se o utilizador com o id fornecido já fez alguma review
    boolean isUserFirstReviewOfTheMonth(Avaliacao avaliacao); //Retorna true se for a primeira review do mês para esse utilizador

    public TreeSet<PairUserIdBusNum> getBusinesses_ReviewCount_perUser();

    /** Business Related **/
    boolean businessReviewed(String businessId); //Retorna true se o negócio com o id fornecido já foi avaliado por alguém

    /*
    //Retorna um TreeSet com as reviews de um dado utilizador, organizadas (ordem crescente) por data (mes e ano)
    // e para valores iguais da data, organiza por ordem alfabetica dos business ids
    TreeSet<Avaliacao> getUserReviewsOrderedPerDateAndBusinessIdInTreeSet(String userId);
    */
    //Retorna um TreeSet com as reviews de um dado utilizador, organizadas (ordem crescente) por data
    TreeSet<Avaliacao> getUserReviewsOrderedPerDateInTreeSet(String userId);

    //Retorna um TreeSet com as reviews de um dado negocio, organizadas (ordem crescente) por data
    TreeSet<Avaliacao> getBusinessReviewsOrderedPerDateInTreeSet(String businessId);

    //Retorna um Set com pares de business ids e número de vezes que este foi avaliado pelo utilizador fornecido
    Set<Map.Entry<String,Integer>> getReviewedBusinessesPlusNumberOfReviews(String userId);

    //Retorna uma lista com pares de ids e o número de reviews de todos os businesses
    public List<PairBusIdNum> getAllBusinessesReviewCount();

    //Retorna um map de treesets. Estes treeset contêm triplos (business id, reviewCount, reviewersCount) ordenados por ordem decrescente do número de reviews
    public Map<Integer,TreeSet<Triple_BusinessId_ReviewCount_ReviewersCount>> getBusinesses_ReviewCount_ReviewersCount_perYear();

    public TreeSet<TripleUserIdRevNumRating> getUserReviewAvgStarsPerBusiness(String businessID);

    List<QuintuploMesAnoReviewcountBusinesscountRating> getUserMonthlyStats(String userId);

    List<QuintuploMesAnoReviewcountUsercountRating> getBusinessMonthlyStats(String businessId);

    //Comparador de ordem crescente do número de vezes que um negócio for avaliado
    Comparator<Map.Entry<String,Integer>> comparatorPairBusinessId_ReviewCount = Comparator.comparingInt(Map.Entry::getValue);

    //Comparador de ordem decrescente do número de vezes que um negócio for avaliado, e em caso de serem iguais por ordem alfabetica da key (crescente)
    Comparator<Map.Entry<String,Integer>> comparatorPairString_ReviewCount = (e1,e2) -> {
        int compareValue = e1.getValue().compareTo(e2.getValue());
        if(compareValue == 0)
            return e1.getKey().compareTo(e2.getKey());
        else
            return -compareValue;
    };
    /*
    //Retorna um TreeSet com pares de business ids e número de vezes que este foi avaliado pelo utilizador fornecido
    //Está organizado por ordem decrescente do número de reviews
    default List<Map.Entry<String,Integer>> getReviewedBusinessesOrderedByNumberOfReviews(String userId){
        return this.getReviewedBusinessesPlusNumberOfReviews(userId).stream()
                                                                    .sorted(comparatorPairBusinessId_ReviewCount.reversed())
                                                                    .collect(Collectors.toList());
    }*/

    public List<ParBusIDRating> getBusIDsAndRating ();
}
