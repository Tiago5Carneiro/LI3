import java.io.Serializable;
import java.util.Objects;

/**
 * Classe utilizada na class Stats
 *
 */

public class pairMesAno implements Serializable {
    private final int mes;
    private final int ano;

    public pairMesAno(int mes, int ano) {
        this.mes = mes;
        this.ano = ano;
    }

    public int getMes() {
        return mes;
    }

    public int getAno() {
        return ano;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        pairMesAno that = (pairMesAno) o;
        return mes == that.mes && ano == that.ano;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mes, ano);
    }

    @Override
    public String toString() {
        return "pairMesAno{" +
                "mes=" + mes +
                ", ano=" + ano +
                '}';
    }
}
