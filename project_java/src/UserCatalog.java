import java.io.Serializable;
import java.util.*;

/**
 * Classe onde sao catalogados os Utilizadores, utilizando um map com o Id do utilizador como chave para o fazer
 *
 */

public class UserCatalog implements CatalogoUtilizadores, Serializable {
    private final Map<String,Utilizador> catalogo;

    public UserCatalog(){
        this.catalogo = new HashMap<>();
    }

    public boolean addUtilizador(Utilizador utilizador) {
        if(utilizador == null) return false;
        this.catalogo.put(utilizador.getId(),utilizador.Clone());
        return true;
    }

    public boolean addUtilizador(String nonParsedUtilizador, String delim, boolean readFriends, String delimFriends) {
        if(nonParsedUtilizador == null || delim == null) return false;
        Utilizador utilizador = readFriends ? User.newUtilizador(nonParsedUtilizador, delim, delimFriends) : User.newUtilizadorNoFriends(nonParsedUtilizador, delim);
        if(utilizador == null) return false;
        this.catalogo.put(utilizador.getId(),utilizador);
        return true;
    }

    public boolean containsUser(String userId) {
        return this.catalogo.containsKey(userId);
    }

}
