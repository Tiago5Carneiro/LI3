import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.io.IOException;

public class Controlador {
    private GestReviews gestReviews;

    public Controlador(GestReviews gestReviews){
        this.gestReviews = gestReviews;
    }

    public Collection<String> query1(){
        return this.gestReviews.getIdOfNonReviewedBusinessesAlphabeticallyOrdered();
    }

    public AbstractMap.SimpleEntry<Integer,Integer> query2(int mes, int ano){
        return this.gestReviews.getMonthGlobalReviewCountAndDistinctUsersCount(mes,ano);
    }

    public List<QuintuploMesAnoReviewcountBusinesscountRating> query3(String user){
        return this.gestReviews.getUserMonthlyStats(user);
    }

    public List<QuintuploMesAnoReviewcountUsercountRating> query4(String business){
        return this.gestReviews.getBusinessMonthlyStats(business);
    }

    public List<PairBusNameNum> query5(String user){
        return this.gestReviews.getReviewedBusinessesOrderedByNumberOfReviews(user);
    }

    public TreeMap<Integer,List<Triple_BusinessId_ReviewCount_ReviewersCount>> query6(int n){
        return this.gestReviews.get_N_mostReviewedBusinessesPerYear(n);
    }

    public Map<String,List<PairBusIdNum>> query7(){
        return this.gestReviews.get_N_MostFamousBusinessesPerCity(3);
    }

    public List<PairUserIdBusNum> query8(int n){
        return this.gestReviews.topN_Reviewers(n);
    }

    public List<TripleUserIdRevNumRating> query9(String business, int n){
        return this.gestReviews.topN_SpecificBusinessReviewers(n, business);
    }

    public Map<String,Map<String, List<ParBusIDRating>>> query10(){
        return this.gestReviews.organizedBusinessAndAvgStars();
    }

    public void readFromTextFiles(String userFilePath, String delimUsersFields, boolean readFriends, String delimFriends, String businessFilePath, String delimBusFields, String delimCategories ,String reviewFilePath, String delimRevsFields) throws FileNotFoundException, IOException{
        this.gestReviews = new GestReviews();
        this.gestReviews.readFiles(userFilePath, delimUsersFields, readFriends, delimFriends, businessFilePath, delimBusFields, delimCategories, reviewFilePath, delimRevsFields, true);
    }

    public void readFromObjectFiles(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        this.gestReviews = GestReviews.readObjectFile(fileName);
    }

    public void saveToObjectFiles(String fileName) throws IOException{
        this.gestReviews.writeToObjectFile(fileName);
    }

    public String estatisticasString(){
        return this.gestReviews.estatisticasString();
    }

    public boolean isGestReviewsNull(){
        return this.gestReviews == null;
    }

    public boolean gestReviewsInitialized() {
        return gestReviews != null;
    }

    public boolean userExist(String userID) {
        return this.gestReviews.containsUser(userID);
    }

    public boolean businessExist(String businessID) {
        return this.gestReviews.containsBus(businessID);
    }
}