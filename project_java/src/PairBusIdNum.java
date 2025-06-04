import java.util.Comparator;

/**
 * Classe utilizada na realisacao da query 7
 *
 */

public class PairBusIdNum {
    private final String busId;
    private final int busRevCount;

    public PairBusIdNum(String busId, int busRevCount) {
        this.busId = busId;
        this.busRevCount = busRevCount;
    }

    public String getBusId() {
        return busId;
    }

    public int getBusRevCount() {
        return busRevCount;
    }

    @Override
    public String toString() {
        return "parBusIdNum{" +
                "busId='" + busId + '\'' +
                ", busRevCount=" + busRevCount +
                '}';
    }

    //Comparador de ordem decrescente do número de vezes que um negócio for avaliado
    public static Comparator<PairBusIdNum> comparatorRevCount = Comparator.comparingInt(PairBusIdNum::getBusRevCount).reversed();
}
