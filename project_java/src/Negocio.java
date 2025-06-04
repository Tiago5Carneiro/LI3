import java.io.Serializable;
import java.util.List;

public interface Negocio extends Serializable {
    /** Constructor **/

    /**
     * Usado para criar um negocio atraves de uma string
     * @param input um negócio que ainda nao foi parsed
     * @param delim a delimitação do negócio que ainda nao foi parsed
     * @return um negocio criado atraves da string
     */
    static Negocio newNegocio(String input, String delim){ return null; }

    /** Getters **/

    /**
     * Usado para determinar o Id de um negocio
     * @return o Id de um negocio
     */
    String getId();

    /**
     * Usado para determinar o nome de um negocio
     * @return o nome de um negocio
     */
    String getName();

    /**
     * Usado para determinar a cidade onde o negocio se localiza
     * @return a cidade onde o negocio se localiza
     */
    String getCity();

    /**
     * Usado para determinar o estado onde o negocio se localiza
     * @return o estado onde o negocio se localiza
     */
    String getState();

    /**
     * Usado para determinar as categorias do negocio
     * @return umas lista com categorias do negocio
     */
    List<String> getCategoriesAsList();

    /**
     * Usado para determinar o numero das categorias do negocio
     * @return o numero das categorias do negocio
     */
    int getNumberOfCategories();

    /**
     * Usado para criar um deep clone do negócio
     * @return o clone do negocio
     */
    Negocio Clone();

    /** Category Functions **/

    /**
     * Usado para adiconar uma categoria a um negocio
     * @param category a categoria a ser adicionada
     */
    void addCategory(String category);

    //remove uma categoria da lista

    /**
     * Usado para remove uma categoria do negócio
     * @param category a categoria a ser removida
     */
    void removeCategory(String category);

    //verifica se existe uma categoria na lista

    /**
     * Usado para verifica se existe uma categoria no negocio
     * @param category  a categoria que queremos verificar a existencia
     * @return Retorna true se a categoria existir
     */
    boolean hasCategory(String category);
}
