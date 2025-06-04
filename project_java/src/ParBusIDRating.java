/**
 * Classe utilizada na query 10
 *
 */

public class ParBusIDRating {
    private String BusId;
    private float rating;

    public ParBusIDRating(String BusId, float rating){
        this.BusId = BusId;
        this.rating = rating;
    }

    public String getBusId() {
        return BusId;
    }

    public float getRating() {
        return rating;
    }

}
