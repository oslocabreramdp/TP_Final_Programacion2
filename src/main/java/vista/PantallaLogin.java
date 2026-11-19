package vista;

import dao.UsuariosDAO;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import modelo.Usuario;
import utils.SecurityUtils;

import java.sql.SQLException;

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
        Label titulo = new Label("CUCHUFLITO");
        titulo.getStyleClass().add("titulo-app");

        Label subtitulo = new Label("PROPIEDADES  -  Ingresa a tu cuenta");
        subtitulo.getStyleClass().add("subtitulo");

        Label etiquetaMail = new Label("Mail");
        etiquetaMail.getStyleClass().add("etiqueta-campo");
        campoMail = new TextField();
        campoMail.setPromptText("ejemplo@mail.com");

        Label etiquetaClave = new Label("Clave");
        etiquetaClave.getStyleClass().add("etiqueta-campo");
        campoClave = new PasswordField();
        campoClave.setPromptText("Tu clave");
        campoClave.setOnAction(e -> iniciarSesion());

        mensaje = new Label();
        mensaje.getStyleClass().add("mensaje-error");
        mensaje.setWrapText(true);
        mensaje.setMaxWidth(300);
        mensaje.setMinHeight(Region.USE_PREF_SIZE);
        mensaje.visibleProperty().bind(mensaje.textProperty().isNotEmpty());
        mensaje.managedProperty().bind(mensaje.textProperty().isNotEmpty());

        Button botonIngresar = new Button("Ingresar");
        botonIngresar.getStyleClass().add("boton-principal");
        botonIngresar.setMaxWidth(Double.MAX_VALUE);
        botonIngresar.setDefaultButton(true);
        botonIngresar.setOnAction(e -> iniciarSesion());

        Button botonCatalogo = new Button("Ver catalogo sin cuenta");
        botonCatalogo.setMaxWidth(Double.MAX_VALUE);
        botonCatalogo.setOnAction(e -> principal.mostrarPropiedades(null));

        Label textoRegistro = new Label("No tenes cuenta?");
        textoRegistro.getStyleClass().add("subtitulo");
        Button botonRegistro = new Button("Registrate");
        botonRegistro.getStyleClass().add("boton-link");
        botonRegistro.setOnAction(e -> principal.mostrarRegistro());
        HBox filaRegistro = new HBox(4, textoRegistro, botonRegistro);
        filaRegistro.setAlignment(Pos.CENTER);

        Button botonSalir = new Button("Salir");
        botonSalir.getStyleClass().add("boton-link");
        botonSalir.setOnAction(e -> principal.getVentana().close());

        VBox campos = new VBox(6, etiquetaMail, campoMail, etiquetaClave, campoClave);

        VBox tarjeta = new VBox(14, titulo, subtitulo, campos, mensaje, botonIngresar, botonCatalogo, filaRegistro, botonSalir);
        tarjeta.getStyleClass().add("tarjeta-login");
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setMaxWidth(380);
        tarjeta.setMaxHeight(VBox.USE_PREF_SIZE);
        campos.setMaxWidth(300);
        botonIngresar.setMaxWidth(300);
        botonCatalogo.setMaxWidth(300);

        StackPane raiz = new StackPane(tarjeta);
        raiz.getStyleClass().add("fondo-login");

        return new Scene(raiz, 1000, 640);
    }

    private void iniciarSesion() {
        String mail = campoMail.getText().trim();
        String clave = campoClave.getText();

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
                mensaje.setText("No hay ningun usuario con ese mail");
                return;
            }
            if (!usuario.isActivo()) {
                mensaje.setText("El usuario esta inactivo o pendiente de aprobacion. Habla con un administrador.");
                return;
            }
            String claveHash = SecurityUtils.hashSHA256(clave);
            if (!claveHash.equals(usuario.getContraseniaHash())) {
                mensaje.setText("La clave es incorrecta");
                return;
            }
            principal.mostrarPropiedades(usuario);
        } catch (SQLException e) {
            mensaje.setText("Error con la base de datos: " + e.getMessage());
        }
    }
}
