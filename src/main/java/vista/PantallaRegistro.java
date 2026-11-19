package vista;

import dao.UsuariosDAO;
import excepciones.ContraseniaInvalidaException;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import modelo.Cliente;
import modelo.ProductorDeVenta;
import modelo.Usuario;
import utils.SecurityUtils;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PantallaRegistro {

    private VentanaPrincipal principal;
    private UsuariosDAO usuariosDAO = new UsuariosDAO();

    private RadioButton opcionCliente;
    private RadioButton opcionProductor;
    private TextField campoNombre;
    private TextField campoApellido;
    private TextField campoTelefono;
    private TextField campoMail;
    private PasswordField campoClave;
    private PasswordField campoRepetirClave;
    private Label etiquetaDocumento;
    private TextField campoDocumento;
    private Label avisoProductor;
    private Label cajaErrores;

    public PantallaRegistro(VentanaPrincipal principal) {
        this.principal = principal;
    }

    public Scene crearEscena() {
        Label titulo = new Label("Crear cuenta");
        titulo.getStyleClass().add("titulo-app");

        Label subtitulo = new Label("Completa tus datos para registrarte");
        subtitulo.getStyleClass().add("subtitulo");

        ToggleGroup grupoTipo = new ToggleGroup();
        opcionCliente = new RadioButton("Cliente");
        opcionCliente.setToggleGroup(grupoTipo);
        opcionCliente.setSelected(true);
        opcionProductor = new RadioButton("Productor de venta");
        opcionProductor.setToggleGroup(grupoTipo);
        grupoTipo.selectedToggleProperty().addListener((observable, anterior, nuevo) -> actualizarTipo());

        HBox filaTipo = new HBox(20, opcionCliente, opcionProductor);

        avisoProductor = new Label("Los productores de venta quedan pendientes hasta que un administrador los apruebe.");
        avisoProductor.getStyleClass().add("caja-info");
        avisoProductor.setWrapText(true);
        avisoProductor.setMaxWidth(Double.MAX_VALUE);
        avisoProductor.setMinHeight(Region.USE_PREF_SIZE);

        campoNombre = new TextField();
        campoApellido = new TextField();
        campoTelefono = new TextField();
        campoTelefono.setPromptText("Solo numeros, ej: 2235551234");
        campoMail = new TextField();
        campoMail.setPromptText("ejemplo@mail.com");
        campoClave = new PasswordField();
        campoClave.setPromptText("Mas de 8 caracteres, 1 numero y 1 mayuscula");
        campoRepetirClave = new PasswordField();
        etiquetaDocumento = crearEtiqueta("DNI");
        campoDocumento = new TextField();

        GridPane grilla = new GridPane();
        grilla.setHgap(12);
        grilla.setVgap(10);
        grilla.add(crearEtiqueta("Nombre"), 0, 0);
        grilla.add(campoNombre, 1, 0);
        grilla.add(crearEtiqueta("Apellido"), 0, 1);
        grilla.add(campoApellido, 1, 1);
        grilla.add(crearEtiqueta("Telefono"), 0, 2);
        grilla.add(campoTelefono, 1, 2);
        grilla.add(crearEtiqueta("Mail"), 0, 3);
        grilla.add(campoMail, 1, 3);
        grilla.add(crearEtiqueta("Clave"), 0, 4);
        grilla.add(campoClave, 1, 4);
        grilla.add(crearEtiqueta("Repetir clave"), 0, 5);
        grilla.add(campoRepetirClave, 1, 5);
        grilla.add(etiquetaDocumento, 0, 6);
        grilla.add(campoDocumento, 1, 6);
        campoNombre.setPrefWidth(270);

        cajaErrores = new Label();
        cajaErrores.getStyleClass().add("caja-errores");
        cajaErrores.setWrapText(true);
        cajaErrores.setMaxWidth(Double.MAX_VALUE);
        cajaErrores.setMinHeight(Region.USE_PREF_SIZE);
        cajaErrores.setVisible(false);
        cajaErrores.setManaged(false);

        Button botonRegistrar = new Button("Crear cuenta");
        botonRegistrar.getStyleClass().add("boton-principal");
        botonRegistrar.setDefaultButton(true);
        botonRegistrar.setOnAction(e -> registrar());

        Button botonVolver = new Button("Volver al inicio");
        botonVolver.setOnAction(e -> principal.mostrarLogin());

        HBox botones = new HBox(10, botonRegistrar, botonVolver);
        botones.setAlignment(Pos.CENTER_LEFT);

        VBox tarjeta = new VBox(14, titulo, subtitulo, crearEtiqueta("Tipo de cuenta"), filaTipo, avisoProductor, grilla, cajaErrores, botones);
        tarjeta.getStyleClass().add("tarjeta-login");
        tarjeta.setMaxWidth(470);
        tarjeta.setMaxHeight(VBox.USE_PREF_SIZE);

        actualizarTipo();

        StackPane centro = new StackPane(tarjeta);
        centro.setStyle("-fx-padding: 30;");

        ScrollPane scroll = new ScrollPane(centro);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);

        StackPane raiz = new StackPane(scroll);
        raiz.getStyleClass().add("fondo-login");

        return new Scene(raiz, 1000, 680);
    }

    private Label crearEtiqueta(String texto) {
        Label etiqueta = new Label(texto);
        etiqueta.getStyleClass().add("etiqueta-campo");
        return etiqueta;
    }

    private void actualizarTipo() {
        boolean esProductor = opcionProductor.isSelected();
        avisoProductor.setVisible(esProductor);
        avisoProductor.setManaged(esProductor);
        if (esProductor) {
            etiquetaDocumento.setText("CUIL");
            campoDocumento.setPromptText("11 numeros, sin guiones");
        } else {
            etiquetaDocumento.setText("DNI");
            campoDocumento.setPromptText("7 u 8 numeros, sin puntos");
        }
    }

    private void registrar() {
        List<String> errores = new ArrayList<>();
        boolean esProductor = opcionProductor.isSelected();

        String nombre = campoNombre.getText().trim();
        if (nombre.isEmpty()) {
            errores.add("El nombre no puede estar vacio");
        } else if (nombre.length() > 50) {
            errores.add("El nombre es muy largo (maximo 50)");
        }

        String apellido = campoApellido.getText().trim();
        if (apellido.isEmpty()) {
            errores.add("El apellido no puede estar vacio");
        } else if (apellido.length() > 50) {
            errores.add("El apellido es muy largo (maximo 50)");
        }

        String telefono = campoTelefono.getText().trim().replace(" ", "").replace("-", "");
        if (telefono.isEmpty()) {
            errores.add("El telefono no puede estar vacio");
        } else if (!telefono.matches("\\d{6,15}")) {
            errores.add("El telefono tiene que tener solo numeros (entre 6 y 15)");
        }

        String mail = campoMail.getText().trim();
        if (mail.isEmpty()) {
            errores.add("El mail no puede estar vacio");
        } else if (!esMailValido(mail)) {
            errores.add("El mail no es valido (ej: usuario@dominio.com)");
        }

        String clave = campoClave.getText();
        try {
            SecurityUtils.validarContrasenia(clave);
            if (!clave.equals(campoRepetirClave.getText())) {
                errores.add("Las claves no son iguales");
            }
        } catch (ContraseniaInvalidaException e) {
            errores.add(e.getMessage());
        }

        String documento = campoDocumento.getText().trim();
        if (esProductor) {
            if (!documento.matches("\\d{11}")) {
                errores.add("El CUIL tiene que tener exactamente 11 numeros, sin guiones");
            }
        } else {
            if (!documento.matches("\\d{7,8}")) {
                errores.add("El DNI tiene que tener 7 u 8 numeros, sin puntos");
            }
        }

        if (errores.isEmpty()) {
            try {
                if (usuariosDAO.buscarPorEmail(mail) != null) {
                    errores.add("Ya hay una cuenta con ese mail");
                }
            } catch (SQLException e) {
                errores.add("Error con la base de datos: " + e.getMessage());
            }
        }

        if (!errores.isEmpty()) {
            cajaErrores.setText("Revisa esto:\n- " + String.join("\n- ", errores));
            cajaErrores.setVisible(true);
            cajaErrores.setManaged(true);
            return;
        }
        cajaErrores.setVisible(false);
        cajaErrores.setManaged(false);

        String claveHash = SecurityUtils.hashSHA256(clave);
        LocalDate hoy = LocalDate.now();
        Usuario nuevo;
        if (esProductor) {
            nuevo = new ProductorDeVenta(nombre, apellido, telefono, mail, claveHash, false, hoy, documento, 0, 0.0);
        } else {
            nuevo = new Cliente(nombre, apellido, telefono, mail, claveHash, true, hoy, documento);
        }

        try {
            usuariosDAO.insertar(nuevo);
            if (esProductor) {
                Avisos.mostrarInformacion("Registro completado",
                        "Tu cuenta de productor fue creada. Vas a poder ingresar cuando un administrador la apruebe.");
            } else {
                Avisos.mostrarInformacion("Registro completado",
                        "Tu cuenta fue creada. Ya podes ingresar con tu mail y tu clave.");
            }
            principal.mostrarLogin();
        } catch (SQLException e) {
            cajaErrores.setText("Error con la base de datos: " + e.getMessage());
            cajaErrores.setVisible(true);
            cajaErrores.setManaged(true);
        }
    }

    private boolean esMailValido(String mail) {
        int posicionArroba = mail.indexOf('@');
        if (posicionArroba <= 0 || posicionArroba != mail.lastIndexOf('@')) {
            return false;
        }
        String dominio = mail.substring(posicionArroba + 1);
        return dominio.contains(".")
                && !dominio.startsWith(".")
                && !dominio.endsWith(".")
                && !mail.contains(" ")
                && mail.length() <= 100;
    }
}
