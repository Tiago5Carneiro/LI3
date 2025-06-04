import java.io.Serializable;

    /**
    *Classe que guarda a informacao pertinente sobre cada mes
    */

public class MonthInfo implements Serializable {
    private int reviewCount;
    private float averageRating;
    private int activeUsers;

    public MonthInfo (){
        this.reviewCount   = 0;
        this.averageRating = 0;
        this.activeUsers   = 0;
    }

    public MonthInfo (int reviewCount, float averageRating, int activeUsers){
        this.reviewCount   = reviewCount;
        this.averageRating = averageRating;
        this.activeUsers   = activeUsers;
    }

    //Gets

    public int getReviewCount() { return reviewCount; }

    public float getAverageRating() { return averageRating; }

    public int getActiveUsers() { return activeUsers; }

    //Incs

        /**
         * Apos receber um rating novo, esta funcao atualiza apropriadamente o valor do ranting do designado mes
         *
         */

    public void incReviewCount(float newRating) {
        this.averageRating = (this.averageRating * this.reviewCount + newRating) / (this.reviewCount + 1);
        this.reviewCount++;
    }

        /**
         * Incrementa a variavel de utilizadores ativos
         *
         */
    public void incActiveUsers() { this.activeUsers++; }

    //toString

    @Override
    public String toString() {
        return "MonthInfo{" +
                "reviewCount=" + reviewCount +
                ", averageRating=" + averageRating +
                ", activeUsers=" + activeUsers +
                '}';
    }
}
