import java.util.Comparator;

/**
 * Classe utilizada na realisacao da query 5
 *
 */

public class PairBusNameNum {
    private final String busName;
    private final int busRevCount;

    public PairBusNameNum(String busName, int busRevCount) {
        this.busName = busName;
        this.busRevCount = busRevCount;
    }

    public String getBusName() {
        return busName;
    }

    public int getBusRevCount() {
        return busRevCount;
    }

    @Override
    public String toString() {
        return "pairBusNameNum{" +
                "busName='" + busName + '\'' +
                ", busRevCount=" + busRevCount +
                '}';
    }

    //Ordem decrescente de número de reviews, e ordem alfabética para número de reviews iguais
    public static Comparator<PairBusNameNum> comparatorBusRevCount = (p1, p2) -> {
        int compareValue = Integer.compare(p1.getBusRevCount(),p2.getBusRevCount());
        if(compareValue == 0)
            return p1.getBusName().compareTo(p2.getBusName());
        else
            return -compareValue;
    };

}
