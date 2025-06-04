import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;

public interface Avaliacao extends Serializable {
    /**
     * Construtor
     * Cria uma avaliacao atraves de uma string
     */
    static Avaliacao newAvaliacao(String input, String delim) { return null; }

    /**
     * Usada para determinar o Id da avaliacao
     * @return Id da avaliacao
     */
    String getId();

    /**
     * Usada para determinar o Id do utlizador avaliador
     * @return Id do utlizador avaliador
     */
    String getUserId();

    /**
     * Usada para determinar o Id do negocio avaliado
     * @return Id do negocio avaliado
     */
    String getBusinessId();

    /**
     * Usada para determinar o parametro estrelas da avaliacao
     * @return Stars da avaliacao
     */
    float  getStars();

    /**
     * Usada para determinar o parametro Util da avaliacao
     * @return parametro Useful da avaliacao
     */
    int    getUseful();

    /**
     * Usada para determinar o parametro Engraçado da avaliacao
     * @return parametro Engraçado da avaliacao
     */
    int    getFunny();

    /**
     * Usada para determinar o parametro Fixe da avaliacao
     * @return parametro Fixe da avaliacao
     */
    int    getCool();

    /**
     * Usada para determinar a data da criação da avaliacao
     * @return data da criação da avaliacao
     */
    LocalDateTime getDate();

    /**
     * Usada para determinar o comentário da avaliacao
     * @return comentario da avaliacao
     */
    String getText();

    /** Setters e incrementers **/

    /**
     * Usado para modificar o parametro Stars da avaliacao
     * @param stars o valor que vai substituir o valor no parametro Stars da avaliacao
     */
    void setStars(float stars);

    /**
     * Usado para aumentar o parametro Useful da avaliacao em 1
     */
    void incUseful();

    /**
     * Usado para aumentar o parametro Funny da avaliacao em 1
     */
    void incFunny();

    /**
     * Usado para aumentar o parametro Cool da avaliacao em 1
     */
    void incCool();

    /**
     * Usado para modificar o parametro Date da avaliacao
     * @param date a data que vai substituir a data no parametro Date da avaliacao
     */
    void setDate(LocalDateTime date);

    /**
     * Usado para modificar o comentário da avaliacao
     * @param text o texto que vai substituir o texto no parametro Text da avaliacao
     */
    void setText(String text);

    /** Booleans **/

    /**
     * Usado para verificar se uma avaliacao tem impacto
     * @return Retorna true se a avaliacao tem impacto
     */
    default boolean isImpactful(){ return this.getCool() + this.getFunny() + this.getUseful() != 0; }

    /**
     * Usado para fazer um deep clone da avaliacao
     * @return o clone da avaliacao
     */
    Avaliacao Clone();

    /** Comparators **/

    /**
     * Comparador que segue a ordem natural da data.
     */
    Comparator<Avaliacao> dateComparator = (Comparator<Avaliacao> & Serializable) (d1,d2) -> {
        int compare = d1.getDate().compareTo(d2.getDate());
        if(compare == 0) return 1;
        else return compare;
    };

    /**
     * Usado para verifica se duas avaliacoes foram feitas no mesmo mes e ano
     * @param avaliacao1 Uma das avaliacoes a serem comparadas
     * @param avaliacao2 Uma das avaliacoes a serem comparadas
     * @return Retorna true se as avaliacoes foram feitas no mesmo mes e ano
     */
    static boolean hasSameMonthAndYear (Avaliacao avaliacao1, Avaliacao avaliacao2){
        LocalDateTime dateTime1 = avaliacao1.getDate();
        LocalDateTime dateTime2 = avaliacao2.getDate();
        return dateTime1.getMonthValue() == dateTime2.getMonthValue() && dateTime1.getYear() == dateTime2.getYear();
    }
}
