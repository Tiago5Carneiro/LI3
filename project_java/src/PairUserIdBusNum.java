import java.util.Comparator;

/**
 * Classe utilizada na query 8
 *
 */

public class PairUserIdBusNum {
    private final String userId;
    private final int numBusReviewed;

    public PairUserIdBusNum(String userId, int numBusReviewed) {
        this.userId = userId;
        this.numBusReviewed = numBusReviewed;
    }

    public String getUserId() {
        return userId;
    }

    public int getNumBusReviewed() {
        return numBusReviewed;
    }

    @Override
    public String toString() {
        return "pairUserIdBusNum{" +
                "userId='" + userId + '\'' +
                ", numBusReviewed=" + numBusReviewed +
                '}';
    }

    //Comparator para ordenar por ordem decrescente do número de negócios avaliados
    public static Comparator<PairUserIdBusNum> comparatorBusCount = (q1, q2) -> {
        int compareInt = Integer.compare(q2.getNumBusReviewed(),q1.getNumBusReviewed());
        if(compareInt == 0)
            return 1;
        else
            return compareInt;
    };
}

