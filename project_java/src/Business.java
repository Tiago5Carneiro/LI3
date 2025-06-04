import java.io.Serializable;
import java.util.*;

/**
 * Classe onde é guardada a informacao relativa as Negocios
 */

public class Business implements Negocio, Serializable {
    /** Instance variables **/
    private final String id;
    private final String name;
    private final String city;
    private final String state;
    private Set<String> categories;

    /** Constructors **/

    public Business(String id, String name, String city, String state, Collection<String> categories) {
        this.id         = id;
        this.name       = name;
        this.city       = city;
        this.state      = state;
        if(categories != null)
            this.categories = new HashSet<>(categories);
        else
            this.categories = new HashSet<>();
    }

    public Business(Business business) {
        this.id         = business.getId();
        this.name       = business.getName();
        this.city       = business.getCity();
        this.state      = business.getState();
        this.categories = new HashSet<>(business.categories);
    }

    public static Negocio newNegocio(String input, String delim, String delimCategories) {
        String[] campos = input.split(delim);
        if (campos.length == 5) {
            String[] categories = campos[4].split(delimCategories);
            return new Business(campos[0], campos[1], campos[2], campos[3], Arrays.asList(categories));
        }
        else if(campos.length == 4)
            return new Business(campos[0], campos[1], campos[2], campos[3], null);
        else
            return null;
    }

    /** Getters **/

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getCity() {
        return this.city;
    }

    public String getState() {
        return this.state;
    }

    public List<String> getCategoriesAsList() {
        return new ArrayList<>(this.categories);
    }

    public int getNumberOfCategories() {
        return this.categories.size();
    }

    /** Category Functions **/

    public void addCategory(String category) {
        this.categories.add(category);
    }

    public void removeCategory(String category){
        this.categories.remove(category);
    }

    public boolean hasCategory(String category) {
        return this.categories.contains(category);
    }

    /** Clone **/

    public Business clone() {
        return new Business(this);
    }

    public Negocio Clone() {
        return this.clone();
    }
}
