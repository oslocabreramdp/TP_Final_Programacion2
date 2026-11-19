package vista;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import modelo.Usuario;

import java.net.URL;

public class VentanaPrincipal extends Application {

    private Stage ventana;

    @Override
    public void start(Stage stage) {
        this.ventana = stage;
        ventana.setTitle("CUCHUFLITO PROPIEDADES");
        ventana.setMinWidth(900);
        ventana.setMinHeight(600);
        mostrarLogin();
        ventana.show();
    }

    public void mostrarLogin() {
        PantallaLogin login = new PantallaLogin(this);
        cambiarEscena(login.crearEscena());
    }

    public void mostrarRegistro() {
        PantallaRegistro registro = new PantallaRegistro(this);
        cambiarEscena(registro.crearEscena());
    }

    public void mostrarPropiedades(Usuario usuario) {
        PantallaPropiedades pantalla = new PantallaPropiedades(this, usuario);
        cambiarEscena(pantalla.crearEscena());
    }

    public void mostrarUsuarios(Usuario administrador) {
        PantallaUsuarios pantalla = new PantallaUsuarios(this, administrador);
        cambiarEscena(pantalla.crearEscena());
    }

    public void mostrarRanking(Usuario administrador) {
        PantallaRanking pantalla = new PantallaRanking(this, administrador);
        cambiarEscena(pantalla.crearEscena());
    }

    private void cambiarEscena(Scene escena) {
        aplicarEstilos(escena);
        ventana.setScene(escena);
        ventana.centerOnScreen();
    }

    public static void aplicarEstilos(Scene escena) {
        URL estilos = VentanaPrincipal.class.getResource("/vista/estilos.css");
        if (estilos != null) {
            escena.getStylesheets().add(estilos.toExternalForm());
        }
    }

    public Stage getVentana() {
        return ventana;
    }
}
