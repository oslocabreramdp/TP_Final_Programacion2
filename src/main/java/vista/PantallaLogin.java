// -***- ESTE ES EL CAMBIO -***- (archivo nuevo)
package vista;

import dao.UsuariosDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import modelo.Usuario;
import utils.SecurityUtils;

import java.sql.SQLException;

// Pantalla de inicio de sesion. Hace lo mismo que iniciarSecion() del Menu.
public class PantallaLogin {

    private VentanaPrincipal principal;
    private UsuariosDAO usuariosDAO = new UsuariosDAO();

    private TextField campoMail;
    private PasswordField campoClave;
    private Label mensaje;

    public PantallaLogin(VentanaPrincipal principal) {
        this.principal = principal;
    }

    public Scene crearEscena() {
        Label titulo = new Label("BIENVENIDO A CUCHUFLITO PROPIEDADES");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        campoMail = new TextField();
        campoMail.setPromptText("ejemplo@mail.com");

        campoClave = new PasswordField();
        campoClave.setPromptText("clave");

        // mail y clave uno abajo del otro
        GridPane grilla = new GridPane();
        grilla.setHgap(10);
        grilla.setVgap(10);
        grilla.setAlignment(Pos.CENTER);
        grilla.add(new Label("Mail:"), 0, 0);
        grilla.add(campoMail, 1, 0);
        grilla.add(new Label("Clave:"), 0, 1);
        grilla.add(campoClave, 1, 1);

        Button botonIngresar = new Button("Ingresar");
        Button botonCatalogo = new Button("Ver catalogo sin cuenta");
        Button botonSalir = new Button("Salir");

        botonIngresar.setOnAction(e -> iniciarSesion());
        // si aprieta enter en la clave tambien intenta entrar
        campoClave.setOnAction(e -> iniciarSesion());
        botonCatalogo.setOnAction(e -> principal.mostrarPropiedades(null));
        botonSalir.setOnAction(e -> principal.getVentana().close());

        HBox botones = new HBox(10, botonIngresar, botonCatalogo, botonSalir);
        botones.setAlignment(Pos.CENTER);

        // aca se muestran los errores en rojo
        mensaje = new Label();
        mensaje.setStyle("-fx-text-fill: red;");

        VBox raiz = new VBox(20, titulo, grilla, botones, mensaje);
        raiz.setAlignment(Pos.CENTER);
        raiz.setPadding(new Insets(30));

        return new Scene(raiz, 520, 320);
    }

    private void iniciarSesion() {
        String mail = campoMail.getText().trim();
        String clave = campoClave.getText();

        // verificaciones antes de ir a la base
        if (mail.isEmpty()) {
            mensaje.setText("Escribi el mail");
            return;
        }
        if (clave.isEmpty()) {
            mensaje.setText("Escribi la clave");
            return;
        }

        try {
            Usuario usuario = usuariosDAO.buscarPorEmail(mail);
            if (usuario == null) {
                mensaje.setText("Usuario no encontrado");
                return;
            }
            if (!usuario.isActivo()) {
                mensaje.setText("El usuario esta dado de baja, habla con un administrador");
                return;
            }
            String claveHash = SecurityUtils.hashSHA256(clave);
            if (!claveHash.equals(usuario.getContraseniaHash())) {
                mensaje.setText("La contrasenia esta mal");
                return;
            }

            // entro bien, la pantalla de propiedades se arma segun el rol
            principal.mostrarPropiedades(usuario);

        } catch (SQLException e) {
            mensaje.setText("Error con la base de datos: " + e.getMessage());
        }
    }
}
