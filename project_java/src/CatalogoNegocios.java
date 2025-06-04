import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface CatalogoNegocios extends Serializable {
    /**
     * Usado para determinar o nome do negocio
     * @param businessId o identificador do negocio de que queremos o nome
     * @return
     */
    String getBusinessName(String businessId);

    /**
     * Usado para determinar a cidade onde esta o negocio
     * @param businessId o identificador do negocio de que queremos a cidade
     * @return a cidade onde esta o negocio
     */
    String getBusinessCity(String businessId);

    /**
     * Usado para determinar o estado onde esta o negocio
     * @param businessId o identificador do negocio de que queremos o estado
     * @return o estado onde esta o negocio
     */
    String getBusinessState(String businessId);

    /**
     * Adiciona negocio ao catalogo
     * @param negocio o negocio que vai ser adiconado
     * @return Retorna true em caso de criacao e insercao com sucesso
     */
    boolean addNegocio(Negocio negocio);

    /**
     * Adiciona Negocio ao catalogo
     * @param nonParsedNegocio negocio que vai ser adiconado antes de ser parsed
     * @param delim a delimitacao no negocio não parsed
     * @param delimCategories a delimitacao das categorias no negocio nao parsed
     * @return Retorna true em caso de criacao e insercao com sucesso
     */
    boolean addNegocio(String nonParsedNegocio, String delim, String delimCategories);

    /**
     * Usado para criar uma lista com os Ids de todos os negocios
     * @return a lista de Ids de todos os negocios
     */
    List<String> getAllIdsAsList();

    /**
     * Usado para verificar a existencia de um negocio
     * @param businessId o identificador do negocio que queremos verificar a existencia
     * @return Retorna true no caso de existir
     */
    boolean containsBusiness(String businessId);
}
