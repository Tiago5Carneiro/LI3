import java.io.Serializable;
import java.util.List;

public interface Estatisticas extends Serializable{

    /**
     * Usado para determinar os nomes dos ficheiros lidos
     * @return Retorna uma lista com os nomes dos ficheiros lidos
     */
    List<String> getFilePaths();

    /**
     * Usado para adicionar um file path para um ficheiro
     * @param filePath o file path de um ficheiro que se vai adicionar
     */
    void addFilePath(String filePath);

    /**
     * Usado para determinar o numero total de avaliacoes
     * @return o numero total de avaliacoes
     */
    int getGlobalReviewCount();

    /**
     * Usado para modificar o numero total de avaliacoes
     * @param reviewCount o novo numero total de avaliacoes
     */
    void setGlobalReviewCount(int reviewCount);

    /**
     * Usado para determinar o numero de avaliacoes erradas
     * @return o numero de avaliacoes erradas
     */
    int getWrongReviewCount();

    /**
     * Usado para modificar o numero de avaliacoes erradas
     * @param wrongReviewCount é o novo numero de avaliacoes erradas
     */
    void setWrongReviewCount(int wrongReviewCount);

    /**
     * Usado para determinar o numero de avaliacoes sem impacto, ou seja, com 0 valores no somatorio de cool, funny ou useful
     * @return o numero de avaliacoes sem impacto
     */
    int getNonImpactfulReviewCount();

    /**
     * Usado para modificar o numero de avaliacoes sem impacto
     * @param nonImpactfulReviewsCount o novo numero de avaliacoes sem impacto
     */
    void setNonImpactfulReviewsCount(int nonImpactfulReviewsCount);

    /**
     * Usado para determinar a media de todas de avaliacoes
     * @return a media de todas de avaliacoes
     */
    float getGlobalAverageRating();

    /**
     * Usado para modificar a edia de todas de avaliacoes
     * @param globalAverageRating a nova media de todas de avaliacoes
     */
    void setGlobalAverageRating(float globalAverageRating);

    /**
     * Usado para determinar o numero total de negocios
     * @return o numero total de negocios
     */
    int getBusinessCount();

    /**
     * Usado para modificar o numero total de negocios
     * @param businessCount o novo numero total de negocios
     */
    void setBusinessCount(int businessCount);

    /**
     * Usado para determinar o numero de negocios que foram avaliados
     * @return o numero de negocios que foram avaliados
     */
    int getBusinessesReviewedCount();

    /**
     * Usado para modificar o numero de negocios que foram avaliados
     * @param businessesReviewedCount o novo numero de negocios que foram avaliados
     */
    void setBusinessesReviewedCount(int businessesReviewedCount);

    /**
     * Usado para determinar o numero de negocios que nao foram avaliados
     * @return o numero de negocios que nao foram avaliados
     */
    int getBusinessesNotReviewedCount();

    /**
     * Usado para modificar o numero de negócios que nao foram avaliados
     * @param businessesNotReviewedCount o novo numero de negócios que nao foram avaliados
     */
    void setBusinessesNotReviewedCount(int businessesNotReviewedCount);

    /**
     * Usado para determinar o numero total de utilizadores
     * @return o numero total de utilizadores
     */
    int getUserCount();

    /**
     * Usado para modificar o numero total de utlizadores
     * @param userCount o novo numero total de utilizadores
     */
    void setUserCount(int userCount);

    /**
     * Usado para determinar o numero de utilizadores que avaliaram pelo menos um negocio
     * @return o numero de utilizadores que avaliaram um negocio
     */
    int getReviewersCount();

    /**
     * Usado para modificar o numero de utilizadores que avaliaram pelo menos um negocio
     * @param reviewersCount o novo numero de utilizadores que avaliaram um negocio
     */
    void setReviewersCount(int reviewersCount);

    /**
     * Usado para determinar o numero de utilizadores que nao avaliaram pelo menos um negocio
     * @return o numero de utilizadores que nao avaliaram um negocio
     */
    int getNonReviewersCount();

    /**
     * Usado para modificar o numero de utilizadores que nao avaliaram pelo menos um negocio
     * @param nonReviewersCount o novo numero de utilizadores que nao avaliaram um negocio
     */
    void setNonReviewersCount(int nonReviewersCount);

    /** Mothly Infos **/

    /**
     * Usado para determinar o numero de avaliacoes feitas num determinado mes de um ano
     * @param mes o mês
     * @param ano o ano
     * @return o numero de avaliacoes feitas num determinado mes de um ano
     */
    int   getMonthlyNumberOfReviews(int mes, int ano);

    /**
     * Usado para determinar a media das avaliacoes feitas num determinado mes de um ano
     * @param mes o mes
     * @param ano o ano
     * @return a media das avaliacoes feitas num determinado mes de um ano
     */
    float getMonthlyAverageRating(int mes, int ano);

    /**
     * Usado para determinar o numero de utilizadores que avaliaram um negocio num determinado mes de um ano
     * @param mes o mes
     * @param ano o ano
     * @return o numero de utilizadores que avaliaram um negocio num determinado mes de um ano
     */
    int   getMonthlyActiveUsers(int mes, int ano);

    /**
     * Usado para adiconar a informacao de um determinado mes e ano a lista
     * @param mes o mes
     * @param ano o ano
     * @param stars parte da informacao a ser adicionada
     * @param isFirstReviewOfTheMonth true se for o primeira avaliacao que foi feita no determinado mes de um ano
     */
    void  addReviewStats(int mes, int ano, float stars, boolean isFirstReviewOfTheMonth);

    String toString();
}
