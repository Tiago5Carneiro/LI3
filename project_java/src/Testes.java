import java.io.IOException;
import java.util.*;

/**
 * Classe de testes
 */

public class Testes {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);

        //Read time test
        Crono.start();
        GestReviews gestReviews = new GestReviews();
        gestReviews.readFiles("users_full.csv", ";", false, null, "business_full.csv",";",",","reviews_1M.csv", ";", true);
        Crono.printElapsedTime();

        //Query 1 time test
        Crono.start();
        Collection<String> ls = gestReviews.getIdOfNonReviewedBusinessesAlphabeticallyOrdered();
        CommandLineTable clt = new CommandLineTable();
        clt.setHeaders("Ids dos Negocios sem avaliacoes");
        for (String s : ls){
            clt.addRow(s);
        }
        System.out.println("Tempo (Query 1): " + Crono.getTimeAsString());

        ls.clear();

        //Query 2 time test
        Crono.start();
        AbstractMap.SimpleEntry<Integer,Integer> entry = gestReviews.getMonthGlobalReviewCountAndDistinctUsersCount(7, 2014);
        System.out.println("Tempo (Query 2): " + Crono.getTimeAsString());

        //Query 3 time test
        Crono.start();
        List<QuintuploMesAnoReviewcountBusinesscountRating> lqu = gestReviews.getUserMonthlyStats("RtGqdDBvvBCjcu5dUqwfzA");

        CommandLineTable clt3 = new CommandLineTable();
        clt3.setHeaders("Mes","Ano","Nº de Avaliacoes","Nº de Negocios","Rating");
        for (QuintuploMesAnoReviewcountBusinesscountRating q : lqu){
            clt3.addRow(Integer.toString(q.getMes()),
                    Integer.toString(q.getAno()),
                    Integer.toString(q.getReviewCount()),
                    Integer.toString(q.getBusinessCount()),
                    Float.toString(q.getRating()));
        }
        System.out.println("Tempo (Query 3): " + Crono.getTimeAsString());

        //Query 4 time test
        Crono.start();
        List<QuintuploMesAnoReviewcountUsercountRating> lqb = gestReviews.getBusinessMonthlyStats("bZiIIUcpgxh8mpKMDhdqbA");

        CommandLineTable clt4 = new CommandLineTable();
        clt4.setHeaders("Mes","Ano","Nº de Avaliacoes","Nº de Utilizadores","Rating");
        for (QuintuploMesAnoReviewcountUsercountRating q : lqb){
            clt4.addRow(Integer.toString(q.getMes()),
                    Integer.toString(q.getAno()),
                    Integer.toString(q.getReviewCount()),
                    Integer.toString(q.getUserCount()),
                    Float.toString(q.getRating()));
        }
        System.out.println("Tempo (Query 4): " + Crono.getTimeAsString());

        //Query 5 time test
        Crono.start();
        List<PairBusNameNum> lesi = gestReviews.getReviewedBusinessesOrderedByNumberOfReviews("RtGqdDBvvBCjcu5dUqwfzA");

        CommandLineTable clt5 = new CommandLineTable();
        clt5.setHeaders("Nomes dos Negócios","Nº de Negócios");
        for(PairBusNameNum p : lesi){
            clt5.addRow(p.getBusName(),Integer.toString(p.getBusRevCount()));
        }
        System.out.println("Tempo (Query 5): " + Crono.getTimeAsString());

        //Query 6 time test
        Crono.start();
        TreeMap<Integer,List<Triple_BusinessId_ReviewCount_ReviewersCount>> treeMap = gestReviews.get_N_mostReviewedBusinessesPerYear(10);

        CommandLineTable clt6 = new CommandLineTable();
        clt6.setHeaders("Ano", "Top Negócios", "Nº de Reviews", "Nº de Utilizadores");
        for (int i :treeMap.keySet()){
            for(Triple_BusinessId_ReviewCount_ReviewersCount t : treeMap.get(i)){
                clt6.addRow(Integer.toString(i),t.getBusinessId(),Integer.toString(t.getReviewCount()),Integer.toString(t.getReviewersCount()));
            }
        }
        System.out.println("Tempo (Query 6): " + Crono.getTimeAsString());

        //Query 7 time test
        Crono.start();
        Map<String,List<PairBusIdNum>> mapa = gestReviews.get_N_MostFamousBusinessesPerCity(3);

        CommandLineTable clt7 = new CommandLineTable();
        clt7.setHeaders("Cidade","Id do Negócio", "Nº de Avaliações");
        for (String key : mapa.keySet()){
            for (PairBusIdNum p : mapa.get(key)){
                clt7.addRow(key,p.getBusId(),Integer.toString(p.getBusRevCount()));
            }
        }
        System.out.println("Tempo (Query 7): " + Crono.getTimeAsString());

        //Query 8 time test
        Crono.start();
        List<PairUserIdBusNum> lpbnn = gestReviews.topN_Reviewers(10);

        CommandLineTable clt8 = new CommandLineTable();
        clt8.setHeaders("Id do Utilizador","Nº de Negocios");
        for(PairUserIdBusNum p : lpbnn){
            clt8.addRow(p.getUserId(),Integer.toString(p.getNumBusReviewed()));
        }
        System.out.println("Tempo (Query 8): " + Crono.getTimeAsString());

        //Query 9 time test
        Crono.start();
        List<TripleUserIdRevNumRating> ltuirvr = gestReviews.topN_SpecificBusinessReviewers(10, "bZiIIUcpgxh8mpKMDhdqbA");

        CommandLineTable clt9 = new CommandLineTable();
        clt9.setHeaders("Id Utilizadores","Nº de Avalicaoes","Classificação");
        for (TripleUserIdRevNumRating t : ltuirvr){
            clt9.addRow(t.getUserId(),Integer.toString(t.getRevCount()),Float.toString(t.getRating()));
        }
        System.out.println("Tempo (Query 9): " + Crono.getTimeAsString());

        //Query 10 time test
        Crono.start();
        Map<String,Map<String, List<ParBusIDRating>>> mapa10 = gestReviews.organizedBusinessAndAvgStars();

        CommandLineTable clt10 = new CommandLineTable();
        clt10.setHeaders("Estados","Cidades","Id de Negócios","Classificação");
        for (String estado : mapa10.keySet()){
            for (String cidade : mapa10.get(estado).keySet()){
                for (ParBusIDRating p : mapa10.get(estado).get(cidade)){
                    clt10.addRow(estado,cidade,p.getBusId(),Float.toString(p.getRating()));
                }
            }
        }
        System.out.println("Tempo (Query 10): " + Crono.getTimeAsString());


        //Estatisticas
        Crono.start();
        String str = gestReviews.estatisticasString();
        System.out.println("Tempo (Estatisticas): " + Crono.getTimeAsString());
    }
}
