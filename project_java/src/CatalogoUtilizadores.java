import java.io.Serializable;

public interface CatalogoUtilizadores extends Serializable {

    /**
     * Usado para adicionar um utlizador
     * @param utilizador o Utilizador que vai ser adiconado
     * @return Retorna true em caso de criacao e insercao com sucesso
     */
    boolean addUtilizador(Utilizador utilizador);

    /**
     * Usado para adicionar um utlizador
     * @param nonParsedUtilizador é o Utilizador que vai ser adiconado que nao esta parsed
     * @param delim o delimitador do Utilizador que nao esta parsed
     * @param delimFriends o delimitador dos Friends no Utilizador que nao estao parsed
     * @return Retorna true em caso de criacao e insercao com sucesso
     */
    boolean addUtilizador(String nonParsedUtilizador, String delim, boolean readFriends, String delimFriends);

    /**
     * Usado para verificar se um utlizador existe na lista
     * @param userId o identificador do utilizador a que vai ser verficar a existencia
     * @return Retorna true se o utilizador existir
     */
    boolean containsUser(String userId);
}
