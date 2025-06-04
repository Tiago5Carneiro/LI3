import java.util.Comparator;
import java.util.Objects;

/**
 * Classe utilizada na realisacao da query 6
 */

public class Triple_BusinessId_ReviewCount_ReviewersCount {
    private String businessId;
    private int reviewCount;
    private int reviewersCount;

    public Triple_BusinessId_ReviewCount_ReviewersCount(String businessId, int reviewCount, int reviewersCount) {
        this.businessId = businessId;
        this.reviewCount = reviewCount;
        this.reviewersCount = reviewersCount;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public int getReviewersCount() {
        return reviewersCount;
    }

    public void setReviewersCount(int reviewersCount) {
        this.reviewersCount = reviewersCount;
    }

    public static Comparator<Triple_BusinessId_ReviewCount_ReviewersCount> reviewCountComparator = (t1,t2) -> {
        int compareReviewCount = Integer.compare(t1.reviewCount, t2.reviewCount);
        if(compareReviewCount == 0)
            return 1; //return t1.businessId.compareTo(t2.businessId); Usando return 1, notamos uma melhoria em cerca de 14% no tempo de execucao da query 6. Este tmb nao influencia dado que nao nos interessa a ordem por ids
        else return compareReviewCount;
    };

    public String toString() {
        return "Triple{" +
                "businessId='" + businessId + '\'' +
                ", reviewCount=" + reviewCount +
                ", reviewersCount=" + reviewersCount +
                '}';
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triple_BusinessId_ReviewCount_ReviewersCount that = (Triple_BusinessId_ReviewCount_ReviewersCount) o;
        return reviewCount == that.reviewCount && reviewersCount == that.reviewersCount && Objects.equals(businessId, that.businessId);
    }
}
