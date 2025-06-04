import java.util.AbstractMap;
import java.util.Comparator;

public class TripleUserIdRevNumRating {
    private final String userId;
    private final int revCount;
    private final float rating;

    public TripleUserIdRevNumRating(String userId, int revCount, float rating) {
        this.userId   = userId;
        this.revCount = revCount;
        this.rating   = rating;
    }

    public String getUserId() {
        return userId;
    }

    public float getRating() {
        return rating;
    }

    public int getRevCount() {
        return revCount;
    }

    //Ordena por ordem decrescente de número de reviews e ordem alfabética do id para número de reviews iguais
    public static Comparator<TripleUserIdRevNumRating> comparatorRevCount = (t1, t2) -> {
        int comparaRevCount = Integer.compare(t2.getRevCount(),t1.getRevCount());
        if(comparaRevCount == 0)
            return t1.getUserId().compareTo(t2.getUserId());
        else
            return comparaRevCount;
    };
}
