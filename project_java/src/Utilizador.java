import java.io.Serializable;
import java.util.List;

public interface Utilizador extends Serializable {
    //Constructor

    /**
     * Usado para criar um utilizador atraves de uma string
     * @param input um utilizador que ainda nao foi parsed
     * @param delim a delimitacao do utilizador que ainda nao foi parsed
     * @param delimFriends a delimitacao do Friends do utilizador que ainda nao foi parsed
     * @return um negocio criado atraves da string
     */
    static Utilizador newUtilizador(String input, String delim, String delimFriends){ return null; }

    /**
     * Usado para criar um utilizador sem o campo dos amigos atraves de uma string
     * @param input um utilizador que ainda nao foi parsed
     * @param delim a delimitacao do utilizador que ainda nao foi parsed
     * @return um negocio criado atraves da string
     */
    static Utilizador newUtilizadorNoFriends(String input, String delim) { return null; };

    /** Getters **/

    /**
     * Usado para determinar o Id do utilizador
     * @return o Id do utilizador
     */
    String getId();

    /**
     * Usado para determinar o nome do utilizador
     * @return o nome do utilizador
     */
    String getName();

    /**
     * Usado para determinar os amigos do utilizador
     * @return a lista de amigos do utlizador
     */
    List<String> getFriendsAsList();

    /**
     * Usado para determinar o número de amigos do utilizador
     * @return o número de amigos do utilizador
     */
    int getNumberOfFriends();

    /**
     * Usado para criar um deep clone do Utilizador
     * @return o clone do Utilizador
     */
    Utilizador Clone();

    /** Friends functions **/

    /**
     *  Usado para adiconar um amigo a lista de amigos
     * @param friend o amigo que vai ser adicionada
     * @return
     */
    int addFriend(String friend);

    /**
     * Usado para verificar se um amigo existe na lista
     * @param friend o amigo que queremos verificar a existencia
     * @return Retorna true se o amigo existir
     */
    boolean hasFriend(String friend);
}
