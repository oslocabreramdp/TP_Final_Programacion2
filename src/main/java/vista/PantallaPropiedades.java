package vista;

import dao.PropiedadesDAO;
import dao.UsuariosDAO;
import enums.EstadoPublicacion;
import enums.TipoPropiedad;
import enums.TipoUsuario;
import enums.Zona;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import modelo.ProductorDeVenta;
import modelo.Propiedad;
import modelo.Usuario;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Pantalla con la tabla de propiedades. Es la misma para todos,
// pero los botones de abajo cambian segun quien entro:
//  - sin cuenta o CLIENTE: solo ve las DISPONIBLES y puede ver el detalle
//  - ADMINISTRADOR: ve todas, puede publicar, eliminar y cargar
//  - PRODUCTOR_DE_VENTA: ve todas, puede reservar, vender y cargar
// Todo lo que se cambia se guarda en la base con los DAO.
public class PantallaPropiedades {

    private VentanaPrincipal principal;
    private Usuario usuario; // si es null entro sin cuenta
    private PropiedadesDAO propiedadesDAO = new PropiedadesDAO();
    private UsuariosDAO usuariosDAO = new UsuariosDAO();

    // todas las propiedades que vinieron de la base
    private List<Propiedad> todas = new ArrayList<>();

    private TableView<Propiedad> tabla;
    private ComboBox<String> comboZona;
    private ComboBox<String> comboTipo;
    private TextField campoAmbientes;
    private TextField campoCalle;
    private Label mensaje;

    public PantallaPropiedades(VentanaPrincipal principal, Usuario usuario) {
        this.principal = principal;
        this.usuario = usuario;
    }

    public Scene crearEscena() {

        // ---------- arriba: saludo y boton para salir ----------
        Label saludo = new Label(armarSaludo());
        saludo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button botonSalir;
        if (usuario == null) {
            botonSalir = new Button("Volver");
        } else {
            botonSalir = new Button("Cerrar sesion");
        }
        botonSalir.setOnAction(e -> principal.mostrarLogin());

        HBox encabezado = new HBox(20, saludo, botonSalir);
        encabezado.setAlignment(Pos.CENTER_LEFT);

        // ---------- filtros (los mismos que menuFiltros del Menu) ----------
        comboZona = new ComboBox<>();
        comboZona.getItems().addAll("TODAS", "NORTE", "SUR", "CENTRO");
        comboZona.setValue("TODAS");

        comboTipo = new ComboBox<>();
        comboTipo.getItems().addAll("TODOS", "DEPARTAMENTO", "CHALET");
        comboTipo.setValue("TODOS");

        campoAmbientes = new TextField();
        campoAmbientes.setPromptText("minimo");
        campoAmbientes.setPrefWidth(70);

        campoCalle = new TextField();
        campoCalle.setPromptText("calle o una parte");

        Button botonFiltrar = new Button("Filtrar");
        botonFiltrar.setOnAction(e -> aplicarFiltros());

        Button botonLimpiar = new Button("Limpiar filtros");
        botonLimpiar.setOnAction(e -> limpiarFiltros());

        HBox filtros = new HBox(8,
                new Label("Zona:"), comboZona,
                new Label("Tipo:"), comboTipo,
                new Label("Ambientes:"), campoAmbientes,
                new Label("Calle:"), campoCalle,
                botonFiltrar, botonLimpiar);
        filtros.setAlignment(Pos.CENTER_LEFT);

        VBox arriba = new VBox(12, encabezado, filtros);
        arriba.setPadding(new Insets(0, 0, 10, 0));

        // ---------- tabla ----------
        tabla = new TableView<>();
        // PropertyValueFactory busca el get de cada atributo (ej: "precio" usa getPrecio())
        TableColumn<Propiedad, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Propiedad, TipoPropiedad> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoPropiedad"));

        TableColumn<Propiedad, String> colCalle = new TableColumn<>("Calle");
        colCalle.setCellValueFactory(new PropertyValueFactory<>("direccionCalle"));
        colCalle.setPrefWidth(160);

        TableColumn<Propiedad, Integer> colNumero = new TableColumn<>("Numero");
        colNumero.setCellValueFactory(new PropertyValueFactory<>("direccionNumero"));

        TableColumn<Propiedad, Zona> colZona = new TableColumn<>("Zona");
        colZona.setCellValueFactory(new PropertyValueFactory<>("zona"));

        TableColumn<Propiedad, Integer> colAmbientes = new TableColumn<>("Ambientes");
        colAmbientes.setCellValueFactory(new PropertyValueFactory<>("ambientes"));

        TableColumn<Propiedad, Integer> colPrecio = new TableColumn<>("Precio USD");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colPrecio.setPrefWidth(100);

        TableColumn<Propiedad, EstadoPublicacion> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setPrefWidth(110);

        tabla.getColumns().add(colId);
        tabla.getColumns().add(colTipo);
        tabla.getColumns().add(colCalle);
        tabla.getColumns().add(colNumero);
        tabla.getColumns().add(colZona);
        tabla.getColumns().add(colAmbientes);
        tabla.getColumns().add(colPrecio);
        tabla.getColumns().add(colEstado);
        tabla.setPlaceholder(new Label("No hay propiedades para mostrar"));

        // ---------- abajo: botones segun el rol ----------
        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER_LEFT);

        Button botonDetalle = new Button("Ver detalle");
        botonDetalle.setOnAction(e -> verDetalle());
        botones.getChildren().add(botonDetalle);

        if (esAdministrador()) {
            Button botonPublicar = new Button("Publicar");
            botonPublicar.setOnAction(e -> publicar());

            Button botonEliminar = new Button("Eliminar");
            botonEliminar.setOnAction(e -> eliminar());

            Button botonCargar = new Button("Cargar propiedad");
            botonCargar.setOnAction(e -> cargarPropiedad());

            botones.getChildren().addAll(botonPublicar, botonEliminar, botonCargar);

        } else if (esProductor()) {
            Button botonReservar = new Button("Reservar");
            botonReservar.setOnAction(e -> reservar());

            Button botonVender = new Button("Vender");
            botonVender.setOnAction(e -> vender());

            Button botonCargar = new Button("Cargar propiedad");
            botonCargar.setOnAction(e -> cargarPropiedad());

            botones.getChildren().addAll(botonReservar, botonVender, botonCargar);
        }

        mensaje = new Label();

        VBox abajo = new VBox(8, botones, mensaje);
        abajo.setPadding(new Insets(10, 0, 0, 0));

        // ---------- todo junto ----------
        BorderPane raiz = new BorderPane();
        raiz.setTop(arriba);
        raiz.setCenter(tabla);
        raiz.setBottom(abajo);
        raiz.setPadding(new Insets(15));

        recargar();

        return new Scene(raiz, 1000, 600);
    }

    // ================= quien entro =================

    private boolean esAdministrador() {
        return usuario != null && usuario.getRol() == TipoUsuario.ADMINISTRADOR;
    }

    private boolean esProductor() {
        return usuario != null && usuario.getRol() == TipoUsuario.PRODUCTOR_DE_VENTA;
    }

    // el cliente y el que entra sin cuenta solo ven las disponibles
    private boolean veSoloDisponibles() {
        return usuario == null || usuario.getRol() == TipoUsuario.CLIENTE;
    }

    private String armarSaludo() {
        if (usuario == null) {
            return "CATALOGO DE PROPIEDADES";
        }
        if (esAdministrador()) {
            return "Hola administrador " + usuario.getNombre();
        }
        if (esProductor()) {
            return "Hola productor " + usuario.getNombre();
        }
        return "Hola cliente " + usuario.getNombre();
    }

    // ================= cargar y filtrar =================

    // vuelve a traer todo de la base y aplica los filtros que esten puestos
    private void recargar() {
        try {
            todas = propiedadesDAO.listarTodos();
            aplicarFiltros();
        } catch (SQLException e) {
            todas = new ArrayList<>();
            tabla.getItems().clear();
            mensaje.setText("Error con la base de datos: " + e.getMessage());
        }
    }

    private void aplicarFiltros() {
        // verifico que en ambientes hayan puesto un numero (si pusieron algo)
        int ambientesMinimo = 0;
        String textoAmbientes = campoAmbientes.getText().trim();
        if (!textoAmbientes.isEmpty()) {
            try {
                ambientesMinimo = Integer.parseInt(textoAmbientes);
            } catch (NumberFormatException e) {
                mostrarAviso("En ambientes tenes que poner un numero");
                return;
            }
            if (ambientesMinimo < 0) {
                mostrarAviso("Los ambientes no pueden ser negativos");
                return;
            }
        }

        String zona = comboZona.getValue();
        String tipo = comboTipo.getValue();
        String calle = campoCalle.getText().trim().toLowerCase();

        List<Propiedad> resultado = new ArrayList<>();
        for (Propiedad p : todas) {
            boolean cumple = true;

            if (veSoloDisponibles() && p.getEstado() != EstadoPublicacion.DISPONIBLE) {
                cumple = false;
            }
            if (!zona.equals("TODAS") && !p.getZona().name().equals(zona)) {
                cumple = false;
            }
            if (!tipo.equals("TODOS") && !p.getTipoPropiedad().name().equals(tipo)) {
                cumple = false;
            }
            if (p.getAmbientes() < ambientesMinimo) {
                cumple = false;
            }
            if (!calle.isEmpty() && (p.getDireccionCalle() == null
                    || !p.getDireccionCalle().toLowerCase().contains(calle))) {
                cumple = false;
            }

            if (cumple) {
                resultado.add(p);
            }
        }

        tabla.getItems().setAll(resultado);
        mensaje.setText("Se muestran " + resultado.size() + " propiedades");
    }

    private void limpiarFiltros() {
        comboZona.setValue("TODAS");
        comboTipo.setValue("TODOS");
        campoAmbientes.clear();
        campoCalle.clear();
        aplicarFiltros();
    }

    // ================= botones =================

    // devuelve la propiedad elegida en la tabla, o null si no eligio ninguna
    private Propiedad propiedadElegida() {
        Propiedad elegida = tabla.getSelectionModel().getSelectedItem();
        if (elegida == null) {
            mostrarAviso("Primero elegi una propiedad de la tabla");
        }
        return elegida;
    }

    private void verDetalle() {
        Propiedad p = propiedadElegida();
        if (p == null) {
            return;
        }
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Detalle");
        alerta.setHeaderText("Propiedad " + p.getId());
        alerta.setContentText(p.toString());
        alerta.showAndWait();
    }

    // ADMINISTRADOR: pasa la propiedad a DISPONIBLE
    private void publicar() {
        Propiedad p = propiedadElegida();
        if (p == null) {
            return;
        }
        if (p.getEstado() == EstadoPublicacion.VENDIDA) {
            mostrarAviso("Esa propiedad ya esta vendida, no se puede publicar");
            return;
        }
        if (p.getEstado() == EstadoPublicacion.DISPONIBLE) {
            mostrarAviso("Esa propiedad ya esta publicada");
            return;
        }
        try {
            propiedadesDAO.cambiarEstado(p.getId(), EstadoPublicacion.DISPONIBLE);
            recargar();
            mensaje.setText("Propiedad " + p.getId() + " publicada");
        } catch (SQLException e) {
            mostrarAviso("Error con la base de datos: " + e.getMessage());
        }
    }

    // ADMINISTRADOR: borra la propiedad
    private void eliminar() {
        Propiedad p = propiedadElegida();
        if (p == null) {
            return;
        }
        boolean seguro = preguntar("Seguro que queres eliminar la propiedad " + p.getId()
                + " (" + p.getDireccionCalle() + " " + p.getDireccionNumero() + ")? No se puede deshacer.");
        if (!seguro) {
            return;
        }
        try {
            propiedadesDAO.eliminar(p.getId());
            recargar();
            mensaje.setText("Propiedad " + p.getId() + " eliminada");
        } catch (SQLException e) {
            mostrarAviso("Error con la base de datos: " + e.getMessage());
        }
    }

    // PRODUCTOR: solo se puede reservar si esta DISPONIBLE
    private void reservar() {
        Propiedad p = propiedadElegida();
        if (p == null) {
            return;
        }
        if (p.getEstado() != EstadoPublicacion.DISPONIBLE) {
            mostrarAviso("Solo se puede reservar una propiedad DISPONIBLE (esta esta " + p.getEstado() + ")");
            return;
        }
        try {
            propiedadesDAO.cambiarEstado(p.getId(), EstadoPublicacion.RESERVADA);
            recargar();
            mensaje.setText("Propiedad " + p.getId() + " reservada");
        } catch (SQLException e) {
            mostrarAviso("Error con la base de datos: " + e.getMessage());
        }
    }

    // PRODUCTOR: se puede vender si esta DISPONIBLE o RESERVADA, y se le suma la venta al productor
    private void vender() {
        Propiedad p = propiedadElegida();
        if (p == null) {
            return;
        }
        if (p.getEstado() == EstadoPublicacion.VENDIDA) {
            mostrarAviso("Esa propiedad ya esta vendida");
            return;
        }
        if (p.getEstado() != EstadoPublicacion.DISPONIBLE && p.getEstado() != EstadoPublicacion.RESERVADA) {
            mostrarAviso("Solo se puede vender una propiedad DISPONIBLE o RESERVADA (esta esta " + p.getEstado() + ")");
            return;
        }
        boolean seguro = preguntar("Vender la propiedad " + p.getId() + " por USD " + p.getPrecio() + "?");
        if (!seguro) {
            return;
        }
        try {
            propiedadesDAO.cambiarEstado(p.getId(), EstadoPublicacion.VENDIDA);

            // se le suma la venta al productor (igual que en el Menu)
            if (usuario instanceof ProductorDeVenta) {
                ProductorDeVenta productor = (ProductorDeVenta) usuario;
                productor.setCantidadVentas(productor.getCantidadVentas() + 1);
                productor.setMontoVentas(productor.getMontoVentas() + p.getPrecio());
                usuariosDAO.actualizar(productor);
            }

            recargar();
            mensaje.setText("Propiedad " + p.getId() + " vendida por USD " + p.getPrecio());
        } catch (SQLException e) {
            mostrarAviso("Error con la base de datos: " + e.getMessage());
        }
    }

    // ADMINISTRADOR y PRODUCTOR: abre el formulario y cuando se cierra recarga la tabla
    private void cargarPropiedad() {
        FormularioPropiedad formulario = new FormularioPropiedad(todas);
        formulario.mostrar(principal.getVentana());
        recargar();
    }

    // ================= ventanitas =================

    private void mostrarAviso(String texto) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Atencion");
        alerta.setHeaderText(null);
        alerta.setContentText(texto);
        alerta.showAndWait();
    }

    // devuelve true si apreto Aceptar
    private boolean preguntar(String texto) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar");
        alerta.setHeaderText(null);
        alerta.setContentText(texto);
        Optional<ButtonType> respuesta = alerta.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.OK;
    }
}
