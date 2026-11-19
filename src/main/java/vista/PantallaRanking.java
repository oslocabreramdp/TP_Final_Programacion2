package vista;

import dao.PropiedadesDAO;
import dao.UsuariosDAO;
import enums.EstadoPublicacion;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
import java.util.List;

public class PantallaRanking {

    private VentanaPrincipal principal;
    private Usuario administrador;
    private PropiedadesDAO propiedadesDAO = new PropiedadesDAO();
    private UsuariosDAO usuariosDAO = new UsuariosDAO();

    private Label datoVendidas;
    private Label datoMonto;
    private Label datoDisponibles;
    private Label datoProductores;
    private GridPane grillaEstados;
    private TableView<ProductorDeVenta> tablaPorCantidad;
    private TableView<ProductorDeVenta> tablaPorMonto;
    private Label mensaje;

    public PantallaRanking(VentanaPrincipal principal, Usuario administrador) {
        this.principal = principal;
        this.administrador = administrador;
    }

    public Scene crearEscena() {
        HBox encabezado = Encabezado.crear(principal, administrador, Encabezado.RANKING);

        Label titulo = new Label("Ranking e informes");
        titulo.getStyleClass().add("titulo-seccion");
        Label subtitulo = new Label("Resumen de ventas y productores de venta ordenados por resultados.");
        subtitulo.getStyleClass().add("subtitulo");
        VBox textos = new VBox(2, titulo, subtitulo);

        Button botonActualizar = new Button("Actualizar");
        botonActualizar.setOnAction(e -> cargarDatos());

        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        HBox filaTitulo = new HBox(textos, espacio, botonActualizar);
        filaTitulo.setAlignment(Pos.CENTER_LEFT);

        datoVendidas = new Label("-");
        datoMonto = new Label("-");
        datoDisponibles = new Label("-");
        datoProductores = new Label("-");

        HBox resumen = new HBox(16,
                crearTarjetaDato("Propiedades vendidas", datoVendidas),
                crearTarjetaDato("Monto total vendido", datoMonto),
                crearTarjetaDato("Propiedades disponibles", datoDisponibles),
                crearTarjetaDato("Productores activos", datoProductores));

        grillaEstados = new GridPane();
        grillaEstados.setHgap(16);
        grillaEstados.setVgap(8);
        Label tituloEstados = new Label("Propiedades por estado");
        tituloEstados.getStyleClass().add("titulo-tarjeta");
        VBox tarjetaEstados = new VBox(12, tituloEstados, grillaEstados);
        tarjetaEstados.getStyleClass().add("tarjeta");
        tarjetaEstados.setMinWidth(230);

        tablaPorCantidad = crearTablaProductores();
        tablaPorMonto = crearTablaProductores();

        VBox tarjetaCantidad = crearTarjetaTabla("Por cantidad de ventas", tablaPorCantidad);
        VBox tarjetaMonto = crearTarjetaTabla("Por monto vendido", tablaPorMonto);
        HBox.setHgrow(tarjetaCantidad, Priority.ALWAYS);
        HBox.setHgrow(tarjetaMonto, Priority.ALWAYS);

        HBox abajo = new HBox(16, tarjetaEstados, tarjetaCantidad, tarjetaMonto);
        VBox.setVgrow(abajo, Priority.ALWAYS);

        mensaje = new Label();
        mensaje.getStyleClass().add("mensaje-error");

        VBox contenido = new VBox(16, filaTitulo, resumen, abajo, mensaje);
        contenido.setPadding(new Insets(20, 24, 20, 24));

        BorderPane raiz = new BorderPane();
        raiz.setTop(encabezado);
        raiz.setCenter(contenido);
        raiz.getStyleClass().add(Encabezado.claseDelRol(administrador));

        cargarDatos();

        return new Scene(raiz, 1150, 720);
    }

    private VBox crearTarjetaDato(String titulo, Label dato) {
        dato.getStyleClass().add("dato-numero");
        Label etiquetaTitulo = new Label(titulo);
        etiquetaTitulo.getStyleClass().add("dato-titulo");
        VBox tarjeta = new VBox(4, etiquetaTitulo, dato);
        tarjeta.getStyleClass().add("tarjeta");
        HBox.setHgrow(tarjeta, Priority.ALWAYS);
        tarjeta.setMaxWidth(Double.MAX_VALUE);
        return tarjeta;
    }

    private VBox crearTarjetaTabla(String titulo, TableView<ProductorDeVenta> tabla) {
        Label etiquetaTitulo = new Label(titulo);
        etiquetaTitulo.getStyleClass().add("titulo-tarjeta");
        VBox tarjeta = new VBox(12, etiquetaTitulo, tabla);
        tarjeta.getStyleClass().add("tarjeta");
        VBox.setVgrow(tabla, Priority.ALWAYS);
        return tarjeta;
    }

    private TableView<ProductorDeVenta> crearTablaProductores() {
        TableView<ProductorDeVenta> tabla = new TableView<>();

        TableColumn<ProductorDeVenta, String> colPuesto = new TableColumn<>("#");
        colPuesto.setPrefWidth(40);
        colPuesto.setSortable(false);
        colPuesto.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(String valor, boolean vacio) {
                super.updateItem(valor, vacio);
                if (vacio || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });

        TableColumn<ProductorDeVenta, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNombre.setPrefWidth(100);

        TableColumn<ProductorDeVenta, String> colApellido = new TableColumn<>("Apellido");
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colApellido.setPrefWidth(100);

        TableColumn<ProductorDeVenta, Integer> colCantidad = new TableColumn<>("Ventas");
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidadVentas"));
        colCantidad.setPrefWidth(70);

        TableColumn<ProductorDeVenta, Double> colMonto = new TableColumn<>("Monto");
        colMonto.setCellValueFactory(new PropertyValueFactory<>("montoVentas"));
        colMonto.setPrefWidth(130);
        colMonto.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(Double monto, boolean vacio) {
                super.updateItem(monto, vacio);
                if (vacio || monto == null) {
                    setText(null);
                } else {
                    setText(PantallaPropiedades.formatearPrecio(Math.round(monto)));
                }
            }
        });

        tabla.getColumns().add(colPuesto);
        tabla.getColumns().add(colNombre);
        tabla.getColumns().add(colApellido);
        tabla.getColumns().add(colCantidad);
        tabla.getColumns().add(colMonto);
        tabla.setPlaceholder(new Label("No hay productores"));
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        return tabla;
    }

    private void cargarDatos() {
        mensaje.setText("");
        try {
            List<Propiedad> propiedades = propiedadesDAO.listarTodos();

            int vendidas = 0;
            int disponibles = 0;
            long montoVendido = 0;
            for (Propiedad p : propiedades) {
                if (p.getEstado() == EstadoPublicacion.VENDIDA) {
                    vendidas++;
                    montoVendido = montoVendido + p.getPrecio();
                }
                if (p.getEstado() == EstadoPublicacion.DISPONIBLE) {
                    disponibles++;
                }
            }
            datoVendidas.setText(String.valueOf(vendidas));
            datoMonto.setText(PantallaPropiedades.formatearPrecio(montoVendido));
            datoDisponibles.setText(String.valueOf(disponibles));

            grillaEstados.getChildren().clear();
            int fila = 0;
            for (EstadoPublicacion estado : EstadoPublicacion.values()) {
                int cantidad = 0;
                for (Propiedad p : propiedades) {
                    if (p.getEstado() == estado) {
                        cantidad++;
                    }
                }
                Label etiquetaEstado = new Label(estado.name());
                etiquetaEstado.getStyleClass().addAll("estado", "estado-" + estado.name().toLowerCase());
                Label etiquetaCantidad = new Label(String.valueOf(cantidad));
                etiquetaCantidad.getStyleClass().add("titulo-tarjeta");
                grillaEstados.add(etiquetaEstado, 0, fila);
                grillaEstados.add(etiquetaCantidad, 1, fila);
                fila++;
            }
            Label etiquetaTotal = new Label("TOTAL");
            etiquetaTotal.getStyleClass().add("etiqueta-campo");
            Label cantidadTotal = new Label(String.valueOf(propiedades.size()));
            cantidadTotal.getStyleClass().add("titulo-tarjeta");
            grillaEstados.add(etiquetaTotal, 0, fila);
            grillaEstados.add(cantidadTotal, 1, fila);

            List<ProductorDeVenta> porCantidad = usuariosDAO.listarProductoresPorCantidadDesc();
            List<ProductorDeVenta> porMonto = usuariosDAO.listarProductoresPorMontoDesc();
            tablaPorCantidad.getItems().setAll(porCantidad);
            tablaPorMonto.getItems().setAll(porMonto);

            int activos = 0;
            for (ProductorDeVenta productor : porCantidad) {
                if (productor.isActivo()) {
                    activos++;
                }
            }
            datoProductores.setText(String.valueOf(activos));
        } catch (SQLException e) {
            mensaje.setText("Error con la base de datos: " + e.getMessage());
        }
    }
}
