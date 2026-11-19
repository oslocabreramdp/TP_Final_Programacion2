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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import modelo.ProductorDeVenta;
import modelo.Propiedad;
import modelo.Usuario;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class PantallaPropiedades {

    private VentanaPrincipal principal;
    private Usuario usuario;
    private PropiedadesDAO propiedadesDAO = new PropiedadesDAO();
    private UsuariosDAO usuariosDAO = new UsuariosDAO();

    private List<Propiedad> todas = new ArrayList<>();

    private TableView<Propiedad> tabla;
    private ComboBox<String> comboZona;
    private ComboBox<String> comboTipo;
    private ComboBox<String> comboEstado;
    private TextField campoAmbientes;
    private TextField campoCalle;
    private TextField campoPrecioDesde;
    private TextField campoPrecioHasta;
    private Label mensaje;

    public PantallaPropiedades(VentanaPrincipal principal, Usuario usuario) {
        this.principal = principal;
        this.usuario = usuario;
    }

    public Scene crearEscena() {
        HBox encabezado = Encabezado.crear(principal, usuario, Encabezado.PROPIEDADES);

        Label titulo = new Label(armarTitulo());
        titulo.getStyleClass().add("titulo-seccion");
        Label subtitulo = new Label(armarSubtitulo());
        subtitulo.getStyleClass().add("subtitulo");
        VBox textos = new VBox(2, titulo, subtitulo);

        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        HBox filaTitulo = new HBox(textos, espacio);
        filaTitulo.setAlignment(Pos.CENTER_LEFT);

        if (puedeCargar()) {
            Button botonCargar = new Button("+ Cargar propiedad");
            botonCargar.getStyleClass().add("boton-principal");
            botonCargar.setOnAction(e -> cargarPropiedad());
            filaTitulo.getChildren().add(botonCargar);
        }
        if (usuario == null) {
            Button botonRegistrarse = new Button("Crear una cuenta");
            botonRegistrarse.getStyleClass().add("boton-principal");
            botonRegistrarse.setOnAction(e -> principal.mostrarRegistro());
            filaTitulo.getChildren().add(botonRegistrarse);
        }

        VBox tarjetaFiltros = crearFiltros();

        crearTabla();
        VBox tarjetaTabla = new VBox(10, tabla);
        tarjetaTabla.getStyleClass().add("tarjeta");
        VBox.setVgrow(tabla, Priority.ALWAYS);

        HBox botones = crearBotones();
        mensaje = new Label();
        mensaje.getStyleClass().add("mensaje");
        Region espacioAbajo = new Region();
        HBox.setHgrow(espacioAbajo, Priority.ALWAYS);
        HBox filaAbajo = new HBox(10, botones, espacioAbajo, mensaje);
        filaAbajo.setAlignment(Pos.CENTER_LEFT);
        tarjetaTabla.getChildren().add(filaAbajo);

        VBox contenido = new VBox(16, filaTitulo, tarjetaFiltros, tarjetaTabla);
        contenido.setPadding(new Insets(20, 24, 20, 24));
        VBox.setVgrow(tarjetaTabla, Priority.ALWAYS);

        BorderPane raiz = new BorderPane();
        raiz.setTop(encabezado);
        raiz.setCenter(contenido);
        raiz.getStyleClass().add(Encabezado.claseDelRol(usuario));

        recargar();

        return new Scene(raiz, 1150, 720);
    }

    private boolean esAdministrador() {
        return usuario != null && usuario.getRol() == TipoUsuario.ADMINISTRADOR;
    }

    private boolean esProductor() {
        return usuario != null && usuario.getRol() == TipoUsuario.PRODUCTOR_DE_VENTA;
    }

    private boolean veSoloDisponibles() {
        return usuario == null || usuario.getRol() == TipoUsuario.CLIENTE;
    }

    private boolean puedeCargar() {
        return esAdministrador() || esProductor();
    }

    private String armarTitulo() {
        if (usuario == null) {
            return "Catalogo de propiedades";
        }
        if (esAdministrador()) {
            return "Gestion de propiedades";
        }
        if (esProductor()) {
            return "Propiedades";
        }
        return "Hola " + usuario.getNombre() + ", estas son las propiedades disponibles";
    }

    private String armarSubtitulo() {
        if (usuario == null) {
            return "Estas viendo las propiedades disponibles. Crea una cuenta para tener tu usuario.";
        }
        if (esAdministrador()) {
            return "Publica, elimina, registra ventas y carga propiedades nuevas.";
        }
        if (esProductor()) {
            return "Reserva propiedades disponibles y carga propiedades nuevas para tasar.";
        }
        return "Usa los filtros para encontrar la que buscas. Doble clic en una fila para ver el detalle.";
    }

    private VBox crearFiltros() {
        comboZona = new ComboBox<>();
        comboZona.getItems().addAll("TODAS", "NORTE", "SUR", "CENTRO");
        comboZona.setValue("TODAS");

        comboTipo = new ComboBox<>();
        comboTipo.getItems().addAll("TODOS", "DEPARTAMENTO", "CHALET");
        comboTipo.setValue("TODOS");

        comboEstado = new ComboBox<>();
        comboEstado.getItems().add("TODOS");
        for (EstadoPublicacion estado : EstadoPublicacion.values()) {
            comboEstado.getItems().add(estado.name());
        }
        comboEstado.setValue("TODOS");

        campoAmbientes = new TextField();
        campoAmbientes.setPromptText("minimo");
        campoAmbientes.setPrefWidth(80);

        campoCalle = new TextField();
        campoCalle.setPromptText("calle o una parte");
        campoCalle.setPrefWidth(150);

        campoPrecioDesde = new TextField();
        campoPrecioDesde.setPromptText("USD");
        campoPrecioDesde.setPrefWidth(100);

        campoPrecioHasta = new TextField();
        campoPrecioHasta.setPromptText("USD");
        campoPrecioHasta.setPrefWidth(100);

        campoAmbientes.setOnAction(e -> aplicarFiltros());
        campoCalle.setOnAction(e -> aplicarFiltros());
        campoPrecioDesde.setOnAction(e -> aplicarFiltros());
        campoPrecioHasta.setOnAction(e -> aplicarFiltros());

        Button botonFiltrar = new Button("Filtrar");
        botonFiltrar.getStyleClass().add("boton-principal");
        botonFiltrar.setOnAction(e -> aplicarFiltros());

        Button botonLimpiar = new Button("Limpiar");
        botonLimpiar.setOnAction(e -> limpiarFiltros());
        botonFiltrar.setMinWidth(Region.USE_PREF_SIZE);
        botonLimpiar.setMinWidth(Region.USE_PREF_SIZE);
        comboZona.setPrefWidth(110);
        comboTipo.setPrefWidth(150);
        comboEstado.setPrefWidth(150);

        GridPane grilla = new GridPane();
        grilla.setHgap(10);
        grilla.setVgap(6);
        int columna = 0;
        columna = agregarFiltro(grilla, columna, "Zona", comboZona);
        columna = agregarFiltro(grilla, columna, "Tipo", comboTipo);
        if (!veSoloDisponibles()) {
            columna = agregarFiltro(grilla, columna, "Estado", comboEstado);
        }
        columna = agregarFiltro(grilla, columna, "Ambientes", campoAmbientes);
        columna = agregarFiltro(grilla, columna, "Calle", campoCalle);
        columna = agregarFiltro(grilla, columna, "Precio desde", campoPrecioDesde);
        columna = agregarFiltro(grilla, columna, "Precio hasta", campoPrecioHasta);

        HBox botones = new HBox(8, botonFiltrar, botonLimpiar);
        botones.setAlignment(Pos.BOTTOM_LEFT);
        grilla.add(botones, columna, 1);

        Label tituloFiltros = new Label("Filtros");
        tituloFiltros.getStyleClass().add("titulo-tarjeta");

        VBox tarjeta = new VBox(10, tituloFiltros, grilla);
        tarjeta.getStyleClass().add("tarjeta");
        return tarjeta;
    }

    private int agregarFiltro(GridPane grilla, int columna, String texto, Region control) {
        Label etiqueta = new Label(texto);
        etiqueta.getStyleClass().add("etiqueta-campo");
        grilla.add(etiqueta, columna, 0);
        grilla.add(control, columna, 1);
        return columna + 1;
    }

    private void crearTabla() {
        tabla = new TableView<>();

        TableColumn<Propiedad, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(55);

        TableColumn<Propiedad, TipoPropiedad> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoPropiedad"));
        colTipo.setPrefWidth(120);

        TableColumn<Propiedad, String> colCalle = new TableColumn<>("Calle");
        colCalle.setCellValueFactory(new PropertyValueFactory<>("direccionCalle"));
        colCalle.setPrefWidth(200);

        TableColumn<Propiedad, Integer> colNumero = new TableColumn<>("Numero");
        colNumero.setCellValueFactory(new PropertyValueFactory<>("direccionNumero"));
        colNumero.setPrefWidth(80);

        TableColumn<Propiedad, Zona> colZona = new TableColumn<>("Zona");
        colZona.setCellValueFactory(new PropertyValueFactory<>("zona"));
        colZona.setPrefWidth(90);

        TableColumn<Propiedad, Integer> colAmbientes = new TableColumn<>("Ambientes");
        colAmbientes.setCellValueFactory(new PropertyValueFactory<>("ambientes"));
        colAmbientes.setPrefWidth(90);

        TableColumn<Propiedad, Double> colSuperficie = new TableColumn<>("m2 cubiertos");
        colSuperficie.setCellValueFactory(new PropertyValueFactory<>("superficieCubierta"));
        colSuperficie.setPrefWidth(110);

        TableColumn<Propiedad, Integer> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colPrecio.setPrefWidth(130);
        colPrecio.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(Integer precio, boolean vacio) {
                super.updateItem(precio, vacio);
                if (vacio || precio == null) {
                    setText(null);
                } else {
                    setText(formatearPrecio(precio));
                }
            }
        });

        TableColumn<Propiedad, EstadoPublicacion> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setPrefWidth(140);
        colEstado.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(EstadoPublicacion estado, boolean vacio) {
                super.updateItem(estado, vacio);
                setText(null);
                if (vacio || estado == null) {
                    setGraphic(null);
                } else {
                    Label etiqueta = new Label(estado.name());
                    etiqueta.getStyleClass().addAll("estado", "estado-" + estado.name().toLowerCase());
                    setGraphic(etiqueta);
                }
            }
        });

        tabla.getColumns().add(colId);
        tabla.getColumns().add(colTipo);
        tabla.getColumns().add(colCalle);
        tabla.getColumns().add(colNumero);
        tabla.getColumns().add(colZona);
        tabla.getColumns().add(colAmbientes);
        tabla.getColumns().add(colSuperficie);
        tabla.getColumns().add(colPrecio);
        tabla.getColumns().add(colEstado);
        tabla.setPlaceholder(new Label("No hay propiedades para mostrar"));
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        tabla.setRowFactory(t -> {
            TableRow<Propiedad> fila = new TableRow<>();
            fila.setOnMouseClicked(evento -> {
                if (evento.getClickCount() == 2 && !fila.isEmpty()) {
                    verDetalle();
                }
            });
            return fila;
        });
    }

    private HBox crearBotones() {
        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER_LEFT);

        Button botonDetalle = new Button("Ver detalle");
        botonDetalle.setOnAction(e -> verDetalle());
        botones.getChildren().add(botonDetalle);

        if (esAdministrador()) {
            Button botonPublicar = new Button("Publicar");
            botonPublicar.getStyleClass().add("boton-exito");
            botonPublicar.setOnAction(e -> publicar());

            Button botonVenta = new Button("Registrar venta");
            botonVenta.setOnAction(e -> registrarVenta());

            Button botonEliminar = new Button("Eliminar");
            botonEliminar.getStyleClass().add("boton-peligro");
            botonEliminar.setOnAction(e -> eliminar());

            botones.getChildren().addAll(botonPublicar, botonVenta, botonEliminar);
        } else if (esProductor()) {
            Button botonReservar = new Button("Reservar");
            botonReservar.getStyleClass().add("boton-exito");
            botonReservar.setOnAction(e -> reservar());

            botones.getChildren().add(botonReservar);
        }
        return botones;
    }

    public static String formatearPrecio(long precio) {
        return "USD " + String.format(Locale.of("es", "AR"), "%,d", precio);
    }

    private void recargar() {
        try {
            todas = propiedadesDAO.listarTodos();
            aplicarFiltros();
        } catch (SQLException e) {
            todas = new ArrayList<>();
            tabla.getItems().clear();
            if (!mensaje.getStyleClass().contains("mensaje-error")) {
                mensaje.getStyleClass().add("mensaje-error");
            }
            mensaje.setText("Error con la base de datos: " + e.getMessage());
        }
    }

    private void aplicarFiltros() {
        int ambientesMinimo = 0;
        String textoAmbientes = campoAmbientes.getText().trim();
        if (!textoAmbientes.isEmpty()) {
            try {
                ambientesMinimo = Integer.parseInt(textoAmbientes);
            } catch (NumberFormatException e) {
                Avisos.mostrarAviso("En ambientes tenes que poner un numero entero");
                return;
            }
            if (ambientesMinimo < 0) {
                Avisos.mostrarAviso("Los ambientes no pueden ser negativos");
                return;
            }
        }

        int precioDesde = leerPrecio(campoPrecioDesde, "desde");
        if (precioDesde == -2) {
            return;
        }
        int precioHasta = leerPrecio(campoPrecioHasta, "hasta");
        if (precioHasta == -2) {
            return;
        }
        if (precioDesde != -1 && precioHasta != -1 && precioDesde > precioHasta) {
            Avisos.mostrarAviso("El precio desde no puede ser mayor que el precio hasta");
            return;
        }

        String zona = comboZona.getValue();
        String tipo = comboTipo.getValue();
        String estadoElegido = comboEstado.getValue();
        String calle = campoCalle.getText().trim().toLowerCase();

        List<Propiedad> resultado = new ArrayList<>();
        int totalQuePuedeVer = 0;
        for (Propiedad p : todas) {
            if (veSoloDisponibles() && p.getEstado() != EstadoPublicacion.DISPONIBLE) {
                continue;
            }
            totalQuePuedeVer++;

            boolean cumple = true;
            if (!zona.equals("TODAS") && !p.getZona().name().equals(zona)) {
                cumple = false;
            }
            if (!tipo.equals("TODOS") && !p.getTipoPropiedad().name().equals(tipo)) {
                cumple = false;
            }
            if (!veSoloDisponibles() && !estadoElegido.equals("TODOS") && !p.getEstado().name().equals(estadoElegido)) {
                cumple = false;
            }
            if (p.getAmbientes() < ambientesMinimo) {
                cumple = false;
            }
            if (!calle.isEmpty() && (p.getDireccionCalle() == null
                    || !p.getDireccionCalle().toLowerCase().contains(calle))) {
                cumple = false;
            }
            if (precioDesde != -1 && p.getPrecio() < precioDesde) {
                cumple = false;
            }
            if (precioHasta != -1 && p.getPrecio() > precioHasta) {
                cumple = false;
            }

            if (cumple) {
                resultado.add(p);
            }
        }

        tabla.getItems().setAll(resultado);
        mensaje.getStyleClass().remove("mensaje-error");
        mensaje.setText("Mostrando " + resultado.size() + " de " + totalQuePuedeVer + " propiedades");
    }

    private int leerPrecio(TextField campo, String nombre) {
        String texto = campo.getText().trim().replace(".", "");
        if (texto.isEmpty()) {
            return -1;
        }
        int precio;
        try {
            precio = Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            Avisos.mostrarAviso("En precio " + nombre + " tenes que poner un numero entero");
            return -2;
        }
        if (precio < 0) {
            Avisos.mostrarAviso("El precio " + nombre + " no puede ser negativo");
            return -2;
        }
        return precio;
    }

    private void limpiarFiltros() {
        comboZona.setValue("TODAS");
        comboTipo.setValue("TODOS");
        comboEstado.setValue("TODOS");
        campoAmbientes.clear();
        campoCalle.clear();
        campoPrecioDesde.clear();
        campoPrecioHasta.clear();
        aplicarFiltros();
    }

    private Propiedad propiedadElegida() {
        Propiedad elegida = tabla.getSelectionModel().getSelectedItem();
        if (elegida == null) {
            Avisos.mostrarAviso("Primero elegi una propiedad de la tabla");
        }
        return elegida;
    }

    private void verDetalle() {
        Propiedad p = propiedadElegida();
        if (p == null) {
            return;
        }
        Avisos.mostrarDetalle("Detalle de la propiedad",
                p.getDireccionCalle() + " " + p.getDireccionNumero() + "  -  " + formatearPrecio(p.getPrecio()),
                p.toString());
    }

    private void publicar() {
        Propiedad p = propiedadElegida();
        if (p == null) {
            return;
        }
        if (p.getEstado() == EstadoPublicacion.VENDIDA) {
            Avisos.mostrarAviso("Esa propiedad ya esta vendida, no se puede publicar");
            return;
        }
        if (p.getEstado() == EstadoPublicacion.DISPONIBLE) {
            Avisos.mostrarAviso("Esa propiedad ya esta publicada");
            return;
        }
        try {
            propiedadesDAO.cambiarEstado(p.getId(), EstadoPublicacion.DISPONIBLE);
            recargar();
            mensaje.setText("Propiedad " + p.getId() + " publicada");
        } catch (SQLException e) {
            Avisos.mostrarAviso("Error con la base de datos: " + e.getMessage());
        }
    }

    private void eliminar() {
        Propiedad p = propiedadElegida();
        if (p == null) {
            return;
        }
        boolean seguro = Avisos.preguntar("Seguro que queres eliminar la propiedad " + p.getId()
                + " (" + p.getDireccionCalle() + " " + p.getDireccionNumero() + ")? No se puede deshacer.");
        if (!seguro) {
            return;
        }
        try {
            propiedadesDAO.eliminar(p.getId());
            recargar();
            mensaje.setText("Propiedad " + p.getId() + " eliminada");
        } catch (SQLException e) {
            Avisos.mostrarAviso("Error con la base de datos: " + e.getMessage());
        }
    }

    private void reservar() {
        Propiedad p = propiedadElegida();
        if (p == null) {
            return;
        }
        if (p.getEstado() != EstadoPublicacion.DISPONIBLE) {
            Avisos.mostrarAviso("Solo se puede reservar una propiedad DISPONIBLE (esta esta " + p.getEstado() + ")");
            return;
        }
        boolean seguro = Avisos.preguntar("Reservar la propiedad " + p.getId() + " ("
                + p.getDireccionCalle() + " " + p.getDireccionNumero() + ")?");
        if (!seguro) {
            return;
        }
        try {
            propiedadesDAO.cambiarEstado(p.getId(), EstadoPublicacion.RESERVADA);
            recargar();
            mensaje.setText("Propiedad " + p.getId() + " reservada");
        } catch (SQLException e) {
            Avisos.mostrarAviso("Error con la base de datos: " + e.getMessage());
        }
    }

    private void registrarVenta() {
        Propiedad p = propiedadElegida();
        if (p == null) {
            return;
        }
        if (p.getEstado() == EstadoPublicacion.VENDIDA) {
            Avisos.mostrarAviso("Esa propiedad ya esta vendida");
            return;
        }
        if (p.getEstado() != EstadoPublicacion.DISPONIBLE && p.getEstado() != EstadoPublicacion.RESERVADA) {
            Avisos.mostrarAviso("Solo se puede vender una propiedad DISPONIBLE o RESERVADA (esta esta " + p.getEstado() + ")");
            return;
        }

        List<ProductorDeVenta> activos = new ArrayList<>();
        try {
            for (ProductorDeVenta productor : usuariosDAO.listarProductoresPorCantidadDesc()) {
                if (productor.isActivo()) {
                    activos.add(productor);
                }
            }
        } catch (SQLException e) {
            Avisos.mostrarAviso("Error con la base de datos: " + e.getMessage());
            return;
        }
        if (activos.isEmpty()) {
            Avisos.mostrarAviso("No hay productores de venta activos para asignarles la venta");
            return;
        }

        ComboBox<String> comboProductores = new ComboBox<>();
        for (ProductorDeVenta productor : activos) {
            comboProductores.getItems().add(productor.getNombre() + " " + productor.getApellido()
                    + "  (" + productor.getCantidadVentas() + " ventas)");
        }
        comboProductores.setPromptText("Elegi el productor");
        comboProductores.setPrefWidth(320);

        Label textoPropiedad = new Label("Propiedad " + p.getId() + ": " + p.getDireccionCalle() + " "
                + p.getDireccionNumero() + "  -  " + formatearPrecio(p.getPrecio()));
        Label textoProductor = new Label("Productor que concreto la venta:");
        textoProductor.getStyleClass().add("etiqueta-campo");
        VBox contenido = new VBox(10, textoPropiedad, textoProductor, comboProductores);

        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Registrar venta");
        dialogo.setHeaderText("Registrar venta");
        dialogo.getDialogPane().setContent(contenido);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Avisos.darEstilo(dialogo.getDialogPane());

        Optional<ButtonType> respuesta = dialogo.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
            return;
        }
        int posicion = comboProductores.getSelectionModel().getSelectedIndex();
        if (posicion < 0) {
            Avisos.mostrarAviso("No elegiste ningun productor, la venta no se registro");
            return;
        }
        ProductorDeVenta productor = activos.get(posicion);

        try {
            propiedadesDAO.cambiarEstado(p.getId(), EstadoPublicacion.VENDIDA);
            productor.setCantidadVentas(productor.getCantidadVentas() + 1);
            productor.setMontoVentas(productor.getMontoVentas() + p.getPrecio());
            usuariosDAO.actualizar(productor);
            recargar();
            mensaje.setText("Venta registrada: propiedad " + p.getId() + " por " + formatearPrecio(p.getPrecio())
                    + " a " + productor.getNombre() + " " + productor.getApellido());
        } catch (SQLException e) {
            Avisos.mostrarAviso("Error con la base de datos: " + e.getMessage());
        }
    }

    private void cargarPropiedad() {
        FormularioPropiedad formulario = new FormularioPropiedad(todas);
        formulario.mostrar(principal.getVentana());
        recargar();
    }
}
