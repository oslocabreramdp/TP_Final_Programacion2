package vista;

import javafx.application.Application;
import javafx.stage.Stage;
import modelo.Usuario;

// Es la clase que extiende Application (como en la clase 19).
// Tiene la ventana (Stage) y va cambiando la escena (Scene) segun la pantalla que toque.
public class VentanaPrincipal extends Application {

    private Stage ventana;

    @Override
    public void start(Stage stage) {
        this.ventana = stage;
        ventana.setTitle("CUCHUFLITO PROPIEDADES");
        mostrarLogin();
        ventana.show();
    }

    // pantalla de inicio de sesion
    public void mostrarLogin() {
        PantallaLogin login = new PantallaLogin(this);
        ventana.setScene(login.crearEscena());
        ventana.centerOnScreen();
    }

    // pantalla con la tabla de propiedades
    // si usuario es null es que entro sin cuenta (solo ve el catalogo)
    public void mostrarPropiedades(Usuario usuario) {
        PantallaPropiedades pantalla = new PantallaPropiedades(this, usuario);
        ventana.setScene(pantalla.crearEscena());
        ventana.centerOnScreen();
    }

    public Stage getVentana() {
        return ventana;
    }
}
