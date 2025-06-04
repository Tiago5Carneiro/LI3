/**
 * Classe encontrada no link fornecido abaixo. Foi alterada conforme as necessidades. Mais concretamente, implementação da paginacao.
 * @author https://www.logicbig.com/how-to/code-snippets/jcode-java-cmd-command-line-table.html
 */

import java.security.InvalidParameterException;
import java.util.*;

/**
 * Versao simples de uma tabela para Command Line.
 */
public class CommandLineTable {
    private static final String HORIZONTAL_SEP = "-";
    private String verticalSep;
    private String joinSep;
    private String[] headers;
    private List<String[]> rows;
    private boolean rightAlign;
    private int rowsPerPage;

    /**
     * Construtor da tabela.
     */
    public CommandLineTable() {
        setShowVerticalLines(true);
        this.rows = new ArrayList<>();
        this.rowsPerPage = 50;
    }

    /**
     * Construtor parametrizado da tabela.
     * @param rowsPerPage Numero de linhas a dar print para uma única página.
     */
    public CommandLineTable(int rowsPerPage) {
        setShowVerticalLines(true);
        this.rows = new ArrayList<>();
        this.rowsPerPage = rowsPerPage;
    }

    /**
     * Usada para determinar se as palavras devem ser alinhas pela direita ou pela esquerda.
     * @param rightAlign Booleano cujo valor verdadeiro indica que as palavras vão ser alinhas pela direita.
     */
    public void setRightAlign(boolean rightAlign) {
        this.rightAlign = rightAlign;
    }

    /**
     * Usada para indicar se e suposto mostrar as linhas verticais.
     * @param showVerticalLines Booleano que em caso de ser verdadeiro serao mostradas as linhas verticais.
     */
    public void setShowVerticalLines(boolean showVerticalLines) {
        verticalSep = showVerticalLines ? "|" : "";
        joinSep = showVerticalLines ? "+" : " ";
    }

    /**
     * Funcao usada para definir o cabeçalho.
     * @param headers Nomes das celulas pertencentes ao cabecalho.
     */
    public void setHeaders(String... headers) {
        this.headers = headers;
    }


    /**
     * Funcao que adiciona uma linha nova.
     * @param cells Celulas a adicionar numa nova linha.
     */
    public void addRow(String... cells) {
        rows.add(cells);
    }

    /**
     * Altera o numero de linhas por pagina.
     * @param rowsPerPage Numero de linhas por página.
     */
    public void setRowsPerPage(int rowsPerPage){
        if(rowsPerPage > 0)
            this.rowsPerPage = rowsPerPage;
    }

    /**
     * Funcao que conta o numero de paginas que podem ser imprimidas em funcao do valor fornecido para o numero de linhas por pagina (por omissao este numero e 50).
     * @return o número de páginas que podem ser imprimidas.
     */
    public int getPageCount(){
        return (this.rows.size() / this.rowsPerPage) + 1;
    }

    /**
     * Imprime toda a tabela na linha de comandos.
     */
    public void print() {
        int[] maxWidths = headers != null ?
                Arrays.stream(headers).mapToInt(String::length).toArray() : null;

        for (String[] cells : rows) {
            if (maxWidths == null) {
                maxWidths = new int[cells.length];
            }
            if (cells.length != maxWidths.length) {
                throw new IllegalArgumentException("Number of row-cells and headers should be consistent");
            }
            for (int i = 0; i < cells.length; i++) {
                maxWidths[i] = Math.max(maxWidths[i], cells[i].length());
            }
        }

        if (headers != null) {
            printLine(maxWidths);
            printRow(headers, maxWidths);
            printLine(maxWidths);
        }
        for (String[] cells : rows) {
            printRow(cells, maxWidths);
        }
        if (headers != null) {
            printLine(maxWidths);
        }
    }

    /**
     * Imprime uma pagina da tabela na linha de comandos.
     * @param page Página que se pretende visualizar.
     * @throws InvalidParameterException Quando o numero de pagina e invalido.
     */
    public void print(int page) throws InvalidParameterException {
        if(page <= 0 || page > getPageCount()) throw new InvalidParameterException("Número de página inválido!");

        int[] maxWidths = headers != null ?
                Arrays.stream(headers).mapToInt(String::length).toArray() : null;

        //Obtem o index da linha a partir da qual vai dar print
        int rowStartIndex = (page - 1) * rowsPerPage,
            rowEndIndex   = Math.min(rowStartIndex + rowsPerPage - 1, this.rows.size() - 1);

        //Se o index for superior ou igual ao número existente de linhas, apenas os headers vão ser imprimidos
        for (int row = rowStartIndex; row <= rowEndIndex ; row++) {
            String[] cells = this.rows.get(row);
            if (maxWidths == null) {
                maxWidths = new int[cells.length];
            }
            if (cells.length != maxWidths.length) {
                throw new IllegalArgumentException("Number of row-cells and headers should be consistent");
            }
            for (int i = 0; i < cells.length; i++) {
                maxWidths[i] = Math.max(maxWidths[i], cells[i].length());
            }
        }

        if (headers != null) {
            printLine(maxWidths);
            printRow(headers, maxWidths);
            printLine(maxWidths);
        }
        for (int row = rowStartIndex; row <= rowEndIndex ; row++){
            String[] cells = this.rows.get(row);
            printRow(cells, maxWidths);
        }
        if (headers != null) {
            printLine(maxWidths);
        }
    }

    /**
     * Funcao que recebendo um numero de uma pagina da print a essa mesma pagina
     */
    public void printTableByPage(){
        Scanner scanner = new Scanner(System.in);
        int page = 1;
        do {
            try {
                print(page);
            }
            catch (InvalidParameterException e){
                System.out.println(e.getMessage());
            }

            System.out.print("Insira uma página (" + 1 + "/" + getPageCount() + ") ou -1 se pretende sair: ");

            try { page = scanner.nextInt(); }
            catch (InputMismatchException e) {
                page = 1;
                System.out.println("Não é número!");
            }
        }while (page != -1);
    }

    /**
     * Imprime o separador horizontal das linhas.
     * @param columnWidths Array com a largura necessaria para que todas as celulas de uma linha sejam impressas corretamente.
     */
    private void printLine(int[] columnWidths) {
        for (int i = 0; i < columnWidths.length; i++) {
            String line = String.join("", Collections.nCopies(columnWidths[i] +
                    verticalSep.length() + 1, HORIZONTAL_SEP));
            System.out.print(joinSep + line + (i == columnWidths.length - 1 ? joinSep : ""));
        }
        System.out.println();
    }

    /**
     * Imprime uma linha no terminal.
     * @param cells Celulas a imprimir nessa linha.
     * @param maxWidths Largura máxima que cada celula pode ter.
     */
    private void printRow(String[] cells, int[] maxWidths) {
        for (int i = 0; i < cells.length; i++) {
            String s = cells[i];
            String verStrTemp = i == cells.length - 1 ? verticalSep : "";
            if (rightAlign) {
                System.out.printf("%s %" + maxWidths[i] + "s %s", verticalSep, s, verStrTemp);
            } else {
                System.out.printf("%s %-" + maxWidths[i] + "s %s", verticalSep, s, verStrTemp);
            }
        }
        System.out.println();
    }
}