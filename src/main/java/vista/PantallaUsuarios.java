package vista;

import dao.UsuariosDAO;
import enums.TipoUsuario;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import modelo.Cliente;
import modelo.ProductorDeVenta;
import modelo.Usuario;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PantallaUsuarios {

    private VentanaPrincipal principal;
    private Usuario administrador;
    private UsuariosDAO usuariosDAO = new UsuariosDAO();

    private List<Usuario> todos = new ArrayList<>();

    private TableView<Usuario> tabla;
    private ComboBox<String> comboTipo;
    private ComboBox<String> comboEstado;
    private TextField campoBuscar;
    private Button botonCambiarEstado;
    private Label mensaje;

    public PantallaUsuarios(VentanaPrincipal principal, Usuario administrador) {
        this.principal = principal;
        this.administrador = administrador;
    }

    public Scene crearEscena() {
        HBox encabezado = Encabezado.crear(principal, administrador, Encabezado.USUARIOS);

        Label titulo = new Label("Usuarios");
        titulo.getStyleClass().add("titulo-seccion");
        Label subtitulo = new Label("Aproba productores de venta nuevos y da de alta o de baja a los clientes.");
        subtitulo.getStyleClass().add("subtitulo");
        VBox textos = new VBox(2, titulo, subtitulo);

        comboTipo = new ComboBox<>();
        comboTipo.getItems().addAll("TODOS", "CLIENTE", "PRODUCTOR_DE_VENTA", "ADMINISTRADOR");
        comboTipo.setValue("TODOS");
        comboTipo.setOnAction(e -> aplicarFiltros());

        comboEstado = new ComboBox<>();
        comboEstado.getItems().addAll("TODOS", "ACTIVOS", "INACTIVOS", "PENDIENTES");
        comboEstado.setValue("TODOS");
        comboEstado.setOnAction(e -> aplicarFiltros());

        campoBuscar = new TextField();
        campoBuscar.setPromptText("nombre, apellido o mail");
        campoBuscar.setPrefWidth(220);
        campoBuscar.textProperty().addListener((observable, anterior, nuevo) -> aplicarFiltros());

        HBox filtros = new HBox(10,
                crearFiltro("Tipo", comboTipo),
                crearFiltro("Estado", comboEstado),
                crearFiltro("Buscar", campoBuscar));
        filtros.setAlignment(Pos.BOTTOM_LEFT);

        Label tituloFiltros = new Label("Filtros");
        tituloFiltros.getStyleClass().add("titulo-tarjeta");
        VBox tarjetaFiltros = new VBox(10, tituloFiltros, filtros);
        tarjetaFiltros.getStyleClass().add("tarjeta");

        crearTabla();

        Button botonDetalle = new Button("Ver detalle");
        botonDetalle.setOnAction(e -> verDetalle());

        botonCambiarEstado = new Button("Activar / Desactivar");
        botonCambiarEstado.getStyleClass().add("boton-principal");
        botonCambiarEstado.setOnAction(e -> cambiarEstado());
        botonCambiarEstado.setDisable(true);

        mensaje = new Label();
        mensaje.getStyleClass().add("mensaje");

        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        HBox filaAbajo = new HBox(10, botonDetalle, botonCambiarEstado, espacio, mensaje);
        filaAbajo.setAlignment(Pos.CENTER_LEFT);

        VBox tarjetaTabla = new VBox(10, tabla, filaAbajo);
        tarjetaTabla.getStyleClass().add("tarjeta");
        VBox.setVgrow(tabla, Priority.ALWAYS);

        VBox contenido = new VBox(16, textos, tarjetaFiltros, tarjetaTabla);
        contenido.setPadding(new Insets(20, 24, 20, 24));
        VBox.setVgrow(tarjetaTabla, Priority.ALWAYS);

        BorderPane raiz = new BorderPane();
        raiz.setTop(encabezado);
        raiz.setCenter(contenido);
        raiz.getStyleClass().add(Encabezado.claseDelRol(administrador));

        recargar();

        return new Scene(raiz, 1150, 720);
    }

    private VBox crearFiltro(String texto, Region control) {
        Label etiqueta = new Label(texto);
        etiqueta.getStyleClass().add("etiqueta-campo");
        return new VBox(6, etiqueta, control);
    }

    private void crearTabla() {
        tabla = new TableView<>();

        TableColumn<Usuario, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(55);

        TableColumn<Usuario, TipoUsuario> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("rol"));
        colTipo.setPrefWidth(160);
        colTipo.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(TipoUsuario tipo, boolean vacio) {
                super.updateItem(tipo, vacio);
                if (vacio || tipo == null) {
                    setText(null);
                } else if (tipo == TipoUsuario.ADMINISTRADOR) {
                    setText("Administrador");
                } else if (tipo == TipoUsuario.PRODUCTOR_DE_VENTA) {
                    setText("Productor de venta");
                } else {
                    setText("Cliente");
                }
            }
        });

        TableColumn<Usuario, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNombre.setPrefWidth(120);

        TableColumn<Usuario, String> colApellido = new TableColumn<>("Apellido");
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colApellido.setPrefWidth(120);

        TableColumn<Usuario, String> colMail = new TableColumn<>("Mail");
        colMail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colMail.setPrefWidth(220);

        TableColumn<Usuario, String> colTelefono = new TableColumn<>("Telefono");
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colTelefono.setPrefWidth(120);

        TableColumn<Usuario, LocalDate> colAlta = new TableColumn<>("Alta");
        colAlta.setCellValueFactory(new PropertyValueFactory<>("fechaAlta"));
        colAlta.setPrefWidth(100);

        TableColumn<Usuario, Usuario> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(dato -> new SimpleObjectProperty<>(dato.getValue()));
        colEstado.setPrefWidth(190);
        colEstado.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(Usuario u, boolean vacio) {
                super.updateItem(u, vacio);
                setText(null);
                if (vacio || u == null) {
                    setGraphic(null);
                } else {
                    Label etiqueta = new Label(textoEstado(u));
                    etiqueta.getStyleClass().addAll("estado", claseEstado(u));
                    setGraphic(etiqueta);
                }
            }
        });

        tabla.getColumns().add(colId);
        tabla.getColumns().add(colTipo);
        tabla.getColumns().add(colNombre);
        tabla.getColumns().add(colApellido);
        tabla.getColumns().add(colMail);
        tabla.getColumns().add(colTelefono);
        tabla.getColumns().add(colAlta);
        tabla.getColumns().add(colEstado);
        tabla.setPlaceholder(new Label("No hay usuarios para mostrar"));
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        tabla.getSelectionModel().selectedItemProperty().addListener((observable, anterior, elegido) -> actualizarBoton(elegido));

        tabla.setRowFactory(t -> {
            TableRow<Usuario> fila = new TableRow<>();
            fila.setOnMouseClicked(evento -> {
                if (evento.getClickCount() == 2 && !fila.isEmpty()) {
                    verDetalle();
                }
            });
            return fila;
        });
    }

    private boolean estaPendiente(Usuario u) {
        return u instanceof ProductorDeVenta && !u.isActivo();
    }

    private String textoEstado(Usuario u) {
        if (estaPendiente(u)) {
            return "PENDIENTE";
        }
        if (u.isActivo()) {
            return "ACTIVO";
        }
        return "INACTIVO";
    }

    private String claseEstado(Usuario u) {
        if (estaPendiente(u)) {
            return "estado-pendiente";
        }
        if (u.isActivo()) {
            return "estado-activo";
        }
        return "estado-inactivo";
    }

    private void actualizarBoton(Usuario elegido) {
        if (elegido == null) {
            botonCambiarEstado.setText("Activar / Desactivar");
            botonCambiarEstado.setDisable(true);
        } else if (elegido instanceof ProductorDeVenta && !elegido.isActivo()) {
            botonCambiarEstado.setText("Aprobar productor");
            botonCambiarEstado.setDisable(false);
        } else if (elegido instanceof Cliente && elegido.isActivo()) {
            botonCambiarEstado.setText("Dar de baja al cliente");
            botonCambiarEstado.setDisable(false);
        } else if (elegido instanceof Cliente) {
            botonCambiarEstado.setText("Dar de alta al cliente");
            botonCambiarEstado.setDisable(false);
        } else {
            botonCambiarEstado.setText("Sin acciones para este usuario");
            botonCambiarEstado.setDisable(true);
        }
    }

    private void recargar() {
        try {
            todos = usuariosDAO.listarTodos();
            aplicarFiltros();
        } catch (SQLException e) {
            todos = new ArrayList<>();
            tabla.getItems().clear();
            mensaje.setText("Error con la base de datos: " + e.getMessage());
        }
    }

    private void aplicarFiltros() {
        String tipo = comboTipo.getValue();
        String estado = comboEstado.getValue();
        String buscado = campoBuscar.getText().trim().toLowerCase();

        List<Usuario> resultado = new ArrayList<>();
        for (Usuario u : todos) {
            boolean cumple = true;
            if (!tipo.equals("TODOS") && !u.getRol().name().equals(tipo)) {
                cumple = false;
            }
            if (estado.equals("ACTIVOS") && !u.isActivo()) {
                cumple = false;
            }
            if (estado.equals("INACTIVOS") && u.isActivo()) {
                cumple = false;
            }
            if (estado.equals("PENDIENTES") && !estaPendiente(u)) {
                cumple = false;
            }
            if (!buscado.isEmpty()) {
                String texto = (u.getNombre() + " " + u.getApellido() + " " + u.getEmail()).toLowerCase();
                if (!texto.contains(buscado)) {
                    cumple = false;
                }
            }
            if (cumple) {
                resultado.add(u);
            }
        }
        tabla.getItems().setAll(resultado);
        mensaje.setText("Mostrando " + resultado.size() + " de " + todos.size() + " usuarios");
    }

    private Usuario usuarioElegido() {
        Usuario elegido = tabla.getSelectionModel().getSelectedItem();
        if (elegido == null) {
            Avisos.mostrarAviso("Primero elegi un usuario de la tabla");
        }
        return elegido;
    }

    private void verDetalle() {
        Usuario u = usuarioElegido();
        if (u == null) {
            return;
        }
        Avisos.mostrarDetalle("Detalle del usuario", u.getNombre() + " " + u.getApellido() + "  -  " + textoEstado(u), u.toString());
    }

    private void cambiarEstado() {
        Usuario u = usuarioElegido();
        if (u == null) {
            return;
        }
        if (u.getId() == administrador.getId()) {
            Avisos.mostrarAviso("No podes cambiar tu propio estado");
            return;
        }

        String pregunta;
        String resultado;
        boolean nuevoEstado;
        if (u instanceof ProductorDeVenta && !u.isActivo()) {
            pregunta = "Aprobar a " + u.getNombre() + " " + u.getApellido() + " como productor de venta?";
            resultado = "Productor aprobado: ya puede iniciar sesion";
            nuevoEstado = true;
        } else if (u instanceof Cliente) {
            nuevoEstado = !u.isActivo();
            if (nuevoEstado) {
                pregunta = "Dar de alta al cliente " + u.getNombre() + " " + u.getApellido() + "?";
                resultado = "Cliente dado de alta";
            } else {
                pregunta = "Dar de baja al cliente " + u.getNombre() + " " + u.getApellido() + "? No va a poder iniciar sesion.";
                resultado = "Cliente dado de baja";
            }
        } else {
            Avisos.mostrarAviso("Solo se puede aprobar productores pendientes o dar de alta y baja a clientes");
            return;
        }

        if (!Avisos.preguntar(pregunta)) {
            return;
        }
        try {
            usuariosDAO.cambiarEstado(u.getId(), nuevoEstado);
            recargar();
            mensaje.setText(resultado);
        } catch (SQLException e) {
            Avisos.mostrarAviso("Error con la base de datos: " + e.getMessage());
        }
    }
}
