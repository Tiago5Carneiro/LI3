import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.StringTokenizer;

/**
 * Classe onde e guardada a informacao sobre as reviews
 *
 */

public class Review implements Avaliacao, Serializable {
    /** Instance Variables **/

    private final String id;
    private final String userId;
    private final String businessId;
    private float stars;
    private int useful;
    private int funny;
    private int cool;
    private LocalDateTime date;
    private String text;


    /** Constructors **/

    public Review(String id, String userId, String businessId, float stars, LocalDateTime date, String text) {
        this.id         = id;
        this.userId     = userId;
        this.businessId = businessId;
        this.stars      = stars;
        this.useful     = 0;
        this.funny      = 0;
        this.cool       = 0;
        this.date       = date;
        this.text       = text;
    }

    public Review(String id, String userId, String businessId, float stars, int useful, int funny, int cool, LocalDateTime date, String text) {
        this.id         = id;
        this.userId     = userId;
        this.businessId = businessId;
        this.stars      = stars;
        this.useful     = useful;
        this.funny      = funny;
        this.cool       = cool;
        this.date       = date;
        this.text       = text;
    }

    public Review(Review review) {
        this.id         = review.id;
        this.userId     = review.userId;
        this.businessId = review.businessId;
        this.stars      = review.stars;
        this.useful     = review.useful;
        this.funny      = review.funny;
        this.cool       = review.cool;
        this.date       = review.date;
        this.text       = review.text;
    }

    //Date Time Formatter
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Avaliacao newAvaliacao(String input, String delim) {
        StringTokenizer st = new StringTokenizer(input, delim);

        //Splits the string
        try {
            String id = st.nextToken(), userId = st.nextToken(), busId = st.nextToken();
            float stars = Float.parseFloat(st.nextToken()); if(stars < 0 || stars > 5) return null;
            int useful  = Integer.parseInt(st.nextToken()); if(useful < 0) return null;
            int funny   = Integer.parseInt(st.nextToken()); if(funny < 0) return null;
            int cool    = Integer.parseInt(st.nextToken()); if(cool < 0) return null;
            LocalDateTime dateTime = LocalDateTime.parse(st.nextToken(), Review.formatter);
            String text = st.hasMoreTokens() ? st.nextToken() : "";

            return new Review(id, userId, busId, stars, useful, funny, cool, dateTime, text);
        }
        catch (NullPointerException | NumberFormatException | java.time.format.DateTimeParseException | java.util.NoSuchElementException e) {
            return null;
        }
    }

    /** Getters **/


    public String getId() {
        return this.id;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getBusinessId() {
        return this.businessId;
    }

    public float getStars() {
        return this.stars;
    }

    public int getUseful() {
        return this.useful;
    }

    public int getFunny() {
        return this.funny;
    }

    public int getCool() {
        return this.cool;
    }

    public LocalDateTime getDate() {
        return this.date;
    }

    public String getText() {
        return this.text;
    }


    /** Setters and Incrementers **/

    public void setStars(float stars) { this.stars = stars; }

    public void incUseful() { this.useful++; }

    public void incFunny() { this.funny++; }

    public void incCool() { this.cool++; }

    public void setDate(LocalDateTime date) { this.date = date; }

    public void setText(String text){ this.text = text; }


    /** Clone **/

    public Review clone() {
        return new Review(this);
    }

    public Avaliacao Clone() {
        return new Review(this);
    }

    /** Equals **/

    public boolean equals(Object o){
        if(this == o) return true;
        if(this.getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return this.id.equals(review.id) &&
               this.businessId.equals(review.businessId) &&
               this.userId.equals(review.userId) &&
               this.stars == review.stars &&
               this.cool == review.cool &&
               this.funny == review.funny &&
               this.useful == review.useful &&
               this.text.equals(review.text) &&
               this.date.equals(review.date);
    }

    @Override
    public String toString() {
        return "Review{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", businessId='" + businessId + '\'' +
                ", stars=" + stars +
                ", useful=" + useful +
                ", funny=" + funny +
                ", cool=" + cool +
                ", date=" + date +
                ", text='" + text + '\'' +
                '}';
    }
}
