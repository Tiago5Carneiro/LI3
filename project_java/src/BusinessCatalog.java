import java.io.Serializable;
import java.util.*;

public class BusinessCatalog implements CatalogoNegocios, Serializable {
    private final Map<String,Negocio> catalogo;

    public BusinessCatalog(){
        this.catalogo = new HashMap<>();
    }

    public String getBusinessName(String businessId) {
        Negocio negocio = this.catalogo.get(businessId);
        if(negocio != null)
            return negocio.getName();
        else
            return null;
    }

    public String getBusinessCity(String businessId) {
        Negocio negocio = this.catalogo.get(businessId);
        if(negocio != null)
            return negocio.getCity();
        else
            return null;
    }

    public String getBusinessState(String businessId) {
        Negocio negocio = this.catalogo.get(businessId);
        if(negocio != null)
            return negocio.getState();
        else
            return null;
    }

    public boolean addNegocio(Negocio negocio) {
        if(negocio == null) return false;
        this.catalogo.put(negocio.getId(),negocio.Clone());
        return true;
    }

    public boolean addNegocio(String nonParsedNegocio, String delim, String delimCategories) {
        if(nonParsedNegocio == null || delim == null) return false;
        Negocio negocio = Business.newNegocio(nonParsedNegocio, delim, delimCategories);
        if(negocio == null) return false;
        this.catalogo.put(negocio.getId(),negocio);
        return true;
    }

    public List<String> getAllIdsAsList() {
        return new ArrayList<>(this.catalogo.keySet());
    }

    public boolean containsBusiness(String businessId) {
        return this.catalogo.containsKey(businessId);
    }
}
