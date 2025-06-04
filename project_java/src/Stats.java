import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe utilizada para guardar os caminhos para os ficheiros, assim como as estatisticas globais do programa
 */
public class Stats implements Estatisticas, Serializable {
    //Files
    private final List<String> filePaths;

    //Reviews
    private int globalReviewCount;
    private int wrongReviewCount;
    private int nonImpactfulReviewCount;
    private float globalAverageRating;

    //Businesses
    private int businessCount;
    private int businessesReviewedCount;
    private int businessesNotReviewedCount;

    //Users
    private int userCount;
    private int reviewersCount;
    private int nonReviewersCount;

    //Monthly
    private final Map<pairMesAno,MonthInfo> monthInfos;

    //Constructors

    public Stats() {
        this.filePaths = new ArrayList<>();
        this.globalReviewCount          = 0;
        this.wrongReviewCount           = 0;
        this.nonImpactfulReviewCount    = 0;
        this.globalAverageRating        = 0;
        this.businessCount              = 0;
        this.businessesReviewedCount    = 0;
        this.businessesNotReviewedCount = 0;
        this.userCount                  = 0;
        this.reviewersCount             = 0;
        this.nonReviewersCount          = 0;
        this.monthInfos = new HashMap<>();
    }


    //Getters & Setters

    public List<String> getFilePaths() { return new ArrayList<>(filePaths); }

    public int getGlobalReviewCount() { return this.globalReviewCount; }

    public void setGlobalReviewCount(int reviewCount) {
        this.globalReviewCount = reviewCount;
    }

    public int getWrongReviewCount() { return this.wrongReviewCount; }

    public void setWrongReviewCount(int wrongReviewCount) { this.wrongReviewCount = wrongReviewCount; }

    public int getNonImpactfulReviewCount() { return this.nonImpactfulReviewCount; }

    public void setNonImpactfulReviewsCount(int nonImpactfulReviewsCount) { this.nonImpactfulReviewCount = nonImpactfulReviewsCount; }

    public float getGlobalAverageRating() { return this.globalAverageRating; }

    public void setGlobalAverageRating(float globalAverageRating) { this.globalAverageRating = globalAverageRating; }

    public int getBusinessCount() { return this.businessCount; }

    public void setBusinessCount(int businessCount) { this.businessCount = businessCount; }

    public int getBusinessesReviewedCount() { return this.businessesReviewedCount; }

    public void setBusinessesReviewedCount(int businessesReviewedCount) { this.businessesReviewedCount = businessesReviewedCount; }

    public int getBusinessesNotReviewedCount() { return this.businessesNotReviewedCount; }

    public void setBusinessesNotReviewedCount(int businessesNotReviewedCount) { this.businessesNotReviewedCount = businessesNotReviewedCount; }

    public int getUserCount() { return this.userCount; }

    public void setUserCount(int userCount) { this.userCount = userCount; }

    public int getReviewersCount() { return this.reviewersCount; }

    public void setReviewersCount(int reviewersCount) { this.reviewersCount = reviewersCount; }

    public int getNonReviewersCount() { return this.nonReviewersCount; }

    public void setNonReviewersCount(int nonReviewersCount) { this.nonReviewersCount = nonReviewersCount; }

    //As funções getMonthlyNumberOfReviews, getMonthlyAverageRating e getMonthlyActiveUsers retornam '0' em caso de o mês ser inválido
    public int getMonthlyNumberOfReviews(int mes, int ano){
        pairMesAno key = new pairMesAno(mes, ano);
        MonthInfo mi = this.monthInfos.get(key);
        if(mi != null) return mi.getReviewCount();
        else return 0;
    }

    public float getMonthlyAverageRating(int mes, int ano){
        pairMesAno key = new pairMesAno(mes, ano);
        MonthInfo mi = this.monthInfos.get(key);
        if(mi != null) return mi.getAverageRating();
        else return 0;
    }

    public int getMonthlyActiveUsers(int mes, int ano){
        pairMesAno key = new pairMesAno(mes, ano);
        MonthInfo mi = this.monthInfos.get(key);
        if(mi != null) return mi.getActiveUsers();
        else return 0;
    }

    //Incrementadores e Adds

    public void addFilePath(String filepath){ this.filePaths.add(filepath); }

    public void incWrongReviewCount() { this.wrongReviewCount++; }

    public void incNonImpactfulReviewCount() { this.nonImpactfulReviewCount++; }

    public void incBusinessCount() { this.businessCount++; }

    public void incBusinessReviewedCount() { this.businessesReviewedCount++; }

    public void incBusinessNotReviewedCount() { this.businessesNotReviewedCount++; }

    public void incUserCount() { this.userCount++; }

    public void incReviewersCount() { this.reviewersCount++; }

    public void incNonReviewersCount() { this.nonReviewersCount++; }

    /**
     * Adiciona informacao de uma review
     * @param mes o mes
     * @param ano o ano
     * @param stars parte da informacao a ser adicionada
     * @param isFirstReviewOfTheMonth true se for o primeira avaliacao que foi feita no determinado mes de um ano
     */

    public void addReviewStats(int mes, int ano, float stars, boolean isFirstReviewOfTheMonth) {
        pairMesAno key = new pairMesAno(mes, ano);
        MonthInfo mi   = this.monthInfos.get(key);

        if(mi != null){
            mi.incReviewCount(stars);
            if(isFirstReviewOfTheMonth) mi.incActiveUsers();
        }
        else{
            //Se nao existir month info entao e o definitivamente a primeira review do mes para esse utilizador
            this.monthInfos.put(key, new MonthInfo(1, stars, 1));
        }
    }

    //toString

    public String toString() {
        return  "filePaths = " + filePaths +
                "\nglobalReviewCount = " + globalReviewCount +
                "\nwrongReviewCount = " + wrongReviewCount +
                "\nnonImpactfulReviewCount = " + nonImpactfulReviewCount +
                "\nglobalAverageRating = " + globalAverageRating +
                "\nbusinessCount = " + businessCount +
                "\nbusinessesReviewedCount = " + businessesReviewedCount +
                "\nbusinessesNotReviewedCount = " + businessesNotReviewedCount +
                "\nuserCount = " + userCount +
                "\nreviewersCount = " + reviewersCount +
                "\nnonReviewersCount = " + nonReviewersCount;
    }
}
