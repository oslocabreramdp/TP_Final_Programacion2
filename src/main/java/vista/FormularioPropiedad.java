package vista;

import dao.PropiedadesDAO;
import enums.Disposicion;
import enums.EstadoConservacion;
import enums.TipoPropiedad;
import enums.Zona;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Chalet;
import modelo.Departamento;
import modelo.Propiedad;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FormularioPropiedad {

    private PropiedadesDAO propiedadesDAO = new PropiedadesDAO();

    private List<Propiedad> existentes;

    private Stage ventana;

    private ComboBox<TipoPropiedad> comboTipo;
    private ComboBox<Zona> comboZona;
    private TextField campoCalle;
    private TextField campoNumero;
    private TextArea areaDescripcion;
    private TextField campoAmbientes;
    private TextField campoDormitorios;
    private TextField campoBanios;
    private CheckBox checkBalcon;
    private CheckBox checkCochera;
    private TextField campoAnio;
    private ComboBox<EstadoConservacion> comboConservacion;
    private TextField campoSuperficie;
    private TextField campoTerreno;

    private VBox seccionDepartamento;
    private TextField campoNumeroDpto;
    private TextField campoPiso;
    private CheckBox checkAscensor;
    private CheckBox checkVistaAlMar;
    private ComboBox<Disposicion> comboDisposicion;

    private VBox seccionChalet;
    private TextField campoCantPisos;
    private CheckBox checkPileta;
    private CheckBox checkQuincho;
    private CheckBox checkJardin;

    private Label etiquetaErrores;

    public FormularioPropiedad(List<Propiedad> existentes) {
        this.existentes = existentes;
    }

    public void mostrar(Stage duenio) {
        ventana = new Stage();
        ventana.setTitle("Cargar propiedad");
        ventana.initOwner(duenio);
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.setScene(crearEscena());
        ventana.showAndWait();
    }

    private Scene crearEscena() {
        int anioActual = LocalDate.now().getYear();

        comboTipo = new ComboBox<>();
        comboTipo.getItems().addAll(TipoPropiedad.values());
        comboTipo.setPromptText("Elegi uno");
        comboTipo.setOnAction(e -> actualizarSecciones());

        comboZona = new ComboBox<>();
        comboZona.getItems().addAll(Zona.values());
        comboZona.setPromptText("Elegi una");

        campoCalle = new TextField();
        campoNumero = new TextField();
        campoNumero.setPromptText("1 a 99999");

        areaDescripcion = new TextArea();
        areaDescripcion.setPromptText("Puede quedar vacia");
        areaDescripcion.setPrefRowCount(2);
        areaDescripcion.setWrapText(true);

        campoAmbientes = new TextField();
        campoAmbientes.setPromptText("1 a 8");
        campoDormitorios = new TextField();
        campoDormitorios.setPromptText("0 a los ambientes");
        campoBanios = new TextField();
        campoBanios.setPromptText("1 a 6");

        checkBalcon = new CheckBox("Tiene balcon");
        checkCochera = new CheckBox("Tiene cochera");

        campoAnio = new TextField();
        campoAnio.setPromptText("1900 a " + anioActual);

        comboConservacion = new ComboBox<>();
        comboConservacion.getItems().addAll(EstadoConservacion.values());
        comboConservacion.setPromptText("Elegi uno");

        campoSuperficie = new TextField();
        campoSuperficie.setPromptText("Mayor a 0 (ej: 45,5)");
        campoTerreno = new TextField();
        campoTerreno.setPromptText("0 si no tiene");

        GridPane grillaUbicacion = crearGrilla();
        grillaUbicacion.add(crearEtiqueta("Tipo"), 0, 0);
        grillaUbicacion.add(comboTipo, 1, 0);
        grillaUbicacion.add(crearEtiqueta("Zona"), 2, 0);
        grillaUbicacion.add(comboZona, 3, 0);
        grillaUbicacion.add(crearEtiqueta("Calle"), 0, 1);
        grillaUbicacion.add(campoCalle, 1, 1);
        grillaUbicacion.add(crearEtiqueta("Numero"), 2, 1);
        grillaUbicacion.add(campoNumero, 3, 1);
        grillaUbicacion.add(crearEtiqueta("Descripcion"), 0, 2);
        grillaUbicacion.add(areaDescripcion, 1, 2, 3, 1);

        GridPane grillaCaracteristicas = crearGrilla();
        grillaCaracteristicas.add(crearEtiqueta("Ambientes"), 0, 0);
        grillaCaracteristicas.add(campoAmbientes, 1, 0);
        grillaCaracteristicas.add(crearEtiqueta("Dormitorios"), 2, 0);
        grillaCaracteristicas.add(campoDormitorios, 3, 0);
        grillaCaracteristicas.add(crearEtiqueta("Banios"), 0, 1);
        grillaCaracteristicas.add(campoBanios, 1, 1);
        grillaCaracteristicas.add(crearEtiqueta("Anio de construccion"), 2, 1);
        grillaCaracteristicas.add(campoAnio, 3, 1);
        grillaCaracteristicas.add(crearEtiqueta("Superficie cubierta m2"), 0, 2);
        grillaCaracteristicas.add(campoSuperficie, 1, 2);
        grillaCaracteristicas.add(crearEtiqueta("Terreno m2"), 2, 2);
        grillaCaracteristicas.add(campoTerreno, 3, 2);
        grillaCaracteristicas.add(crearEtiqueta("Conservacion"), 0, 3);
        grillaCaracteristicas.add(comboConservacion, 1, 3);
        grillaCaracteristicas.add(new HBox(20, checkBalcon, checkCochera), 2, 3, 2, 1);

        campoNumeroDpto = new TextField();
        campoNumeroDpto.setPromptText("ej: 4B");
        campoPiso = new TextField();
        campoPiso.setPromptText("0 a 100 (0 es PB)");
        checkAscensor = new CheckBox("Tiene ascensor");
        checkVistaAlMar = new CheckBox("Tiene vista al mar");
        comboDisposicion = new ComboBox<>();
        comboDisposicion.getItems().addAll(Disposicion.values());
        comboDisposicion.setPromptText("Elegi una");

        GridPane grillaDepto = crearGrilla();
        grillaDepto.add(crearEtiqueta("Departamento"), 0, 0);
        grillaDepto.add(campoNumeroDpto, 1, 0);
        grillaDepto.add(crearEtiqueta("Piso"), 2, 0);
        grillaDepto.add(campoPiso, 3, 0);
        grillaDepto.add(crearEtiqueta("Disposicion"), 0, 1);
        grillaDepto.add(comboDisposicion, 1, 1);
        grillaDepto.add(new HBox(20, checkAscensor, checkVistaAlMar), 2, 1, 2, 1);
        seccionDepartamento = crearTarjeta("Datos del departamento", grillaDepto);

        campoCantPisos = new TextField();
        campoCantPisos.setPromptText("1 a 5");
        checkPileta = new CheckBox("Tiene pileta");
        checkQuincho = new CheckBox("Tiene quincho");
        checkJardin = new CheckBox("Tiene jardin");

        GridPane grillaChalet = crearGrilla();
        grillaChalet.add(crearEtiqueta("Cantidad de pisos"), 0, 0);
        grillaChalet.add(campoCantPisos, 1, 0);
        grillaChalet.add(new HBox(20, checkPileta, checkQuincho, checkJardin), 0, 1, 4, 1);
        seccionChalet = crearTarjeta("Datos del chalet", grillaChalet);

        actualizarSecciones();

        Button botonGuardar = new Button("Guardar propiedad");
        botonGuardar.getStyleClass().add("boton-principal");
        botonGuardar.setOnAction(e -> guardar());
        Button botonCancelar = new Button("Cancelar");
        botonCancelar.setOnAction(e -> ventana.close());

        HBox botones = new HBox(10, botonGuardar, botonCancelar);
        botones.setAlignment(Pos.CENTER_LEFT);

        etiquetaErrores = new Label();
        etiquetaErrores.getStyleClass().add("caja-errores");
        etiquetaErrores.setWrapText(true);
        etiquetaErrores.setMaxWidth(Double.MAX_VALUE);
        etiquetaErrores.setMinHeight(Region.USE_PREF_SIZE);
        ocultarErrores();

        Label titulo = new Label("Cargar propiedad");
        titulo.getStyleClass().add("titulo-seccion");
        Label subtitulo = new Label("La propiedad queda en TASACION hasta que un administrador la publique.");
        subtitulo.getStyleClass().add("subtitulo");

        VBox contenido = new VBox(14, new VBox(2, titulo, subtitulo),
                crearTarjeta("Ubicacion", grillaUbicacion),
                crearTarjeta("Caracteristicas", grillaCaracteristicas),
                seccionDepartamento, seccionChalet, etiquetaErrores, botones);
        contenido.setPadding(new Insets(20));

        ScrollPane scroll = new ScrollPane(contenido);
        scroll.setFitToWidth(true);

        Scene escena = new Scene(scroll, 760, 720);
        VentanaPrincipal.aplicarEstilos(escena);
        return escena;
    }

    private GridPane crearGrilla() {
        GridPane grilla = new GridPane();
        grilla.setHgap(12);
        grilla.setVgap(10);
        ColumnConstraints etiquetas = new ColumnConstraints(150);
        ColumnConstraints campos = new ColumnConstraints(150, 170, Double.MAX_VALUE);
        grilla.getColumnConstraints().addAll(etiquetas, campos, etiquetas, campos);
        return grilla;
    }

    private Label crearEtiqueta(String texto) {
        Label etiqueta = new Label(texto);
        etiqueta.getStyleClass().add("etiqueta-campo");
        return etiqueta;
    }

    private VBox crearTarjeta(String titulo, GridPane grilla) {
        Label etiquetaTitulo = new Label(titulo);
        etiquetaTitulo.getStyleClass().add("titulo-tarjeta");
        VBox tarjeta = new VBox(12, etiquetaTitulo, grilla);
        tarjeta.getStyleClass().add("tarjeta");
        return tarjeta;
    }

    private void actualizarSecciones() {
        boolean esDepto = comboTipo.getValue() == TipoPropiedad.DEPARTAMENTO;
        boolean esChalet = comboTipo.getValue() == TipoPropiedad.CHALET;

        seccionDepartamento.setVisible(esDepto);
        seccionDepartamento.setManaged(esDepto);

        seccionChalet.setVisible(esChalet);
        seccionChalet.setManaged(esChalet);
    }

    private void mostrarErrores(String texto) {
        etiquetaErrores.setText(texto);
        etiquetaErrores.setVisible(true);
        etiquetaErrores.setManaged(true);
    }

    private void ocultarErrores() {
        etiquetaErrores.setText("");
        etiquetaErrores.setVisible(false);
        etiquetaErrores.setManaged(false);
    }

    private void guardar() {
        List<String> errores = new ArrayList<>();
        int anioActual = LocalDate.now().getYear();

        TipoPropiedad tipo = comboTipo.getValue();
        if (tipo == null) {
            errores.add("Elegi el tipo de propiedad");
        }
        Zona zona = comboZona.getValue();
        if (zona == null) {
            errores.add("Elegi la zona");
        }

        String calle = campoCalle.getText().trim();
        if (calle.isEmpty()) {
            errores.add("La calle no puede estar vacia");
        }
        int numero = leerEntero(campoNumero, "Numero de la calle", 1, 99999, errores);

        String descripcion = areaDescripcion.getText().trim();
        if (descripcion.isEmpty()) {
            descripcion = " ";
        }

        int ambientes = leerEntero(campoAmbientes, "Ambientes", 1, 8, errores);
        int dormitorios = leerEntero(campoDormitorios, "Dormitorios", 0, 8, errores);
        if (ambientes != -1 && dormitorios != -1 && dormitorios > ambientes) {
            errores.add("No puede tener mas dormitorios (" + dormitorios + ") que ambientes (" + ambientes + ")");
        }
        int banios = leerEntero(campoBanios, "Banios", 1, 6, errores);

        boolean balcon = checkBalcon.isSelected();
        boolean cochera = checkCochera.isSelected();

        int anioConstruccion = leerEntero(campoAnio, "Anio de construccion", 1900, anioActual, errores);

        EstadoConservacion estadoConservacion = comboConservacion.getValue();
        if (estadoConservacion == null) {
            errores.add("Elegi el estado de conservacion");
        }

        double superficieCubierta = leerDecimal(campoSuperficie, "Superficie cubierta", 0, false, errores);
        double terreno = leerDecimal(campoTerreno, "Terreno", 0, true, errores);

        String numeroDpto = "";
        int piso = 0;
        Disposicion disposicion = null;
        int cantPisos = 0;

        if (tipo == TipoPropiedad.DEPARTAMENTO) {
            numeroDpto = campoNumeroDpto.getText().trim();
            if (numeroDpto.isEmpty()) {
                errores.add("El numero o letra del departamento no puede estar vacio");
            }
            piso = leerEntero(campoPiso, "Piso", 0, 100, errores);
            disposicion = comboDisposicion.getValue();
            if (disposicion == null) {
                errores.add("Elegi la disposicion");
            }
        } else if (tipo == TipoPropiedad.CHALET) {
            cantPisos = leerEntero(campoCantPisos, "Cantidad de pisos", 1, 5, errores);
        }

        if (!errores.isEmpty()) {
            mostrarErrores("Revisa esto:\n- " + String.join("\n- ", errores));
            return;
        }
        ocultarErrores();

        for (Propiedad p : existentes) {
            if (tipo == TipoPropiedad.DEPARTAMENTO && p instanceof Departamento) {
                Departamento d = (Departamento) p;
                if (d.getDireccionCalle().equalsIgnoreCase(calle)
                        && d.getDireccionNumero() == numero
                        && d.getNumeroDpto() != null
                        && d.getNumeroDpto().equalsIgnoreCase(numeroDpto)) {
                    mostrarErrores("Ese departamento ya esta cargado (id " + d.getId() + ")");
                    return;
                }
            }
            if (tipo == TipoPropiedad.CHALET && p instanceof Chalet
                    && p.getDireccionCalle().equalsIgnoreCase(calle)
                    && p.getDireccionNumero() == numero) {
                mostrarErrores("Ese chalet ya esta cargado (id " + p.getId() + ")");
                return;
            }
        }

        Propiedad nueva;
        if (tipo == TipoPropiedad.DEPARTAMENTO) {
            nueva = new Departamento(tipo, descripcion, zona, calle, numero, balcon, ambientes, dormitorios, banios,
                    cochera, anioConstruccion, estadoConservacion, terreno, superficieCubierta,
                    numeroDpto, piso, checkAscensor.isSelected(), checkVistaAlMar.isSelected(), disposicion);
        } else {
            nueva = new Chalet(tipo, descripcion, zona, calle, numero, balcon, ambientes, dormitorios, banios,
                    cochera, anioConstruccion, estadoConservacion, terreno, superficieCubierta,
                    cantPisos, checkPileta.isSelected(), checkQuincho.isSelected(), checkJardin.isSelected());
        }

        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION);
        confirmar.setTitle("Confirmar");
        confirmar.setHeaderText("Guardar esta propiedad? Precio calculado: " + PantallaPropiedades.formatearPrecio(nueva.getPrecio()));
        confirmar.setContentText(nueva.toString());
        Avisos.darEstilo(confirmar.getDialogPane());
        Optional<ButtonType> respuesta = confirmar.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
            return;
        }

        try {
            propiedadesDAO.insertar(nueva);
            Avisos.mostrarInformacion("Listo", "Propiedad guardada. Queda en TASACION hasta que la publique un administrador.");
            ventana.close();
        } catch (SQLException e) {
            mostrarErrores("Error con la base de datos: " + e.getMessage());
        }
    }

    private int leerEntero(TextField campo, String nombre, int minimo, int maximo, List<String> errores) {
        String texto = campo.getText().trim();
        if (texto.isEmpty()) {
            errores.add(nombre + ": no puede estar vacio");
            return -1;
        }
        int numero;
        try {
            numero = Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            errores.add(nombre + ": tiene que ser un numero entero");
            return -1;
        }
        if (numero < minimo || numero > maximo) {
            errores.add(nombre + ": tiene que estar entre " + minimo + " y " + maximo);
            return -1;
        }
        return numero;
    }

    private double leerDecimal(TextField campo, String nombre, double minimo, boolean puedeSerIgual, List<String> errores) {
        String texto = campo.getText().trim().replace(",", ".");
        if (texto.isEmpty()) {
            errores.add(nombre + ": no puede estar vacio");
            return -1;
        }
        double numero;
        try {
            numero = Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            errores.add(nombre + ": tiene que ser un numero (ej: 45,5)");
            return -1;
        }
        if (puedeSerIgual && numero < minimo) {
            errores.add(nombre + ": no puede ser menor a " + (int) minimo);
            return -1;
        }
        if (!puedeSerIgual && numero <= minimo) {
            errores.add(nombre + ": tiene que ser mayor a " + (int) minimo);
            return -1;
        }
        return numero;
    }
}
