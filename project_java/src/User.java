import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Classe onde é guardada a informacao relativa as Negocios
 */

public class User implements Utilizador, Serializable {
    /** Instance Variables **/
    private final String id;
    private final String name;
    private Set<String> friends;
    private boolean friendsParsed;

    //Friends admited as parsed
    public User(String id, String name, Set<String> friends) {
        this.id = id;
        this.name = name;
        if(friends != null)
            this.friends = new HashSet<>(friends);
        else
            this.friends = new HashSet<>();
        this.friendsParsed = true;
    }

    //Friends will not be parsed. Use 'parseFriends' if you want to.
    public User(String id, String name, String friends) {
        this.id = id;
        this.name = name;
        this.friends = new HashSet<>(); if(!friends.isEmpty()) this.friends.add(friends);
        this.friendsParsed = false;
    }

    public User(User user){
        this.id = user.getId();
        this.name = user.getName();
        this.friends = new HashSet<>(user.friends);
        this.friendsParsed = user.friendsParsed;
    }

    public static Utilizador newUtilizador(String input, String delim, String delimFriends) {
        String[] campos = input.split(delim);
        String friends;
        if(campos.length == 3) friends = campos[2];
        else if (campos.length == 2) friends = "";
        else return null;

        User user = new User(campos[0], campos[1], friends);
        if(delimFriends != null) {
            user.parseFriends(delimFriends);
            user.friendsParsed = true;
        }
        return user;
    }

    public static Utilizador newUtilizadorNoFriends(String input, String delim) {
        StringTokenizer st = new StringTokenizer(input, delim);

        //Splits the string
        try {
            String id = st.nextToken(), name = st.nextToken();

            return new User(id, name, "");
        }
        catch (java.util.NoSuchElementException e) {
            return null;
        }
    }

    /** Getters **/

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getFriendsAsList() {
        this.parseFriends(";");
        return new ArrayList<>(this.friends);
    }

    public int getNumberOfFriends() {
        this.parseFriends(";");
        return this.friends.size();
    }

    /** Clone **/

    public User clone() {
        return new User(this);
    }

    public Utilizador Clone() {
        return this.clone();
    }

    /** Friends Functions **/

    //Dá parse aos amigos que tenham sido fornecidos no construtor. É necessário fornecer o delimitador
    public void parseFriends(String delim){
        //Só faz parse caso tenha a string que é suposto dar parse nos amigos
        if(this.friends.size() == 0) return;

        //Gets the string with all friends
        Iterator<String> it = this.friends.iterator();
        if(!it.hasNext()) return;
        String input = it.next();

        //Removes that string from the collection
        this.friends.remove(input);

        //Splits the string into an array of Strings(Friends)
        String[] friends = input.split(delim);

        //Adds all the friends to the collection
        this.friends.addAll(Arrays.asList(friends));
    }

    //Se os amigos ainda nao tiveram sido parsed, retornará false, assim como no caso de terem sido parsed e o amigo nao existir mesmo
    public boolean hasFriend(String friend) {
        return this.friends.contains(friend);
    }

    //Returns -1 se os amigos ainda nao tiverem sido parsed
    public int addFriend(String friend) {
        if(!this.friendsParsed) return -1;
        this.friends.add(friend);
        return 0;
    }

    /** toString **/
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", friends=" + friends +
                ", friendsParsed=" + friendsParsed +
                '}';
    }
}
