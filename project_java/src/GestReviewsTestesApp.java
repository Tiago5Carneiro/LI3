public class GestReviewsTestesApp {
    public static void main(String[] args) {
        Controlador controlador = new Controlador(null);
        VistaTestesApp vista = new VistaTestesApp(controlador);
        vista.run();
    }
}
