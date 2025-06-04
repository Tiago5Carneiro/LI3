public class GestReviewsAppMVC {
    public static void main(String[] args) {
        Controlador controlador = new Controlador(null);
        Vista vista = new Vista(controlador);
        vista.run();
    }
}
