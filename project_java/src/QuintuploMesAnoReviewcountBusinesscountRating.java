import java.io.Serializable;
import java.util.Comparator;

/**
 * Classe utilizada na query 3
 *
 */

public class QuintuploMesAnoReviewcountBusinesscountRating implements Serializable {
    private int mes;
    private int ano;
    private int reviewCount;
    private int businessCount;
    private float rating;

    public QuintuploMesAnoReviewcountBusinesscountRating(int mes, int ano, int reviewCount, int businessCount, float rating) {
        this.mes = mes;
        this.ano = ano;
        this.reviewCount = reviewCount;
        this.businessCount = businessCount;
        this.rating = rating;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public int getBusinessCount() {
        return businessCount;
    }

    public void setBusinessCount(int businessCount) {
        this.businessCount = businessCount;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public static Comparator<QuintuploMesAnoReviewcountBusinesscountRating> comparatorData = (q1, q2) -> {
        int aux1 = q1.getAno() * 12 + q1.getMes(),
            aux2 = q2.getAno() * 12 + q2.getMes();
        return aux1 - aux2;
    };

    @Override
    public String toString() {
        return "QuintuploMesAnoReviewcountBusinesscountRating{" +
                "mes=" + mes +
                ", ano=" + ano +
                ", reviewCount=" + reviewCount +
                ", businessCount=" + businessCount +
                ", rating=" + rating +
                '}';
    }
}
