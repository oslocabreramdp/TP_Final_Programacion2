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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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

// Ventana para cargar una propiedad nueva.
// Tiene las mismas verificaciones que cargarPropiedad() del Menu.
// La propiedad se guarda en la base y queda en TASACION hasta que la publique un administrador.
public class FormularioPropiedad {

    private PropiedadesDAO propiedadesDAO = new PropiedadesDAO();

    // las propiedades que ya estan cargadas, para no cargar la misma dos veces
    private List<Propiedad> existentes;

    private Stage ventana;

    // datos de todas las propiedades
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

    // solo departamento
    private VBox seccionDepartamento;
    private TextField campoNumeroDpto;
    private TextField campoPiso;
    private CheckBox checkAscensor;
    private CheckBox checkVistaAlMar;
    private ComboBox<Disposicion> comboDisposicion;

    // solo chalet
    private VBox seccionChalet;
    private TextField campoCantPisos;
    private CheckBox checkPileta;
    private CheckBox checkQuincho;
    private CheckBox checkJardin;

    private Label etiquetaErrores;

    public FormularioPropiedad(List<Propiedad> existentes) {
        this.existentes = existentes;
    }

    // abre la ventana y espera a que se cierre
    public void mostrar(Stage duenio) {
        ventana = new Stage();
        ventana.setTitle("Cargar propiedad");
        ventana.initOwner(duenio);
        // mientras esta abierta no se puede tocar la ventana de atras
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.setScene(crearEscena());
        ventana.showAndWait();
    }

    private Scene crearEscena() {
        int anioActual = LocalDate.now().getYear();

        // ---------- datos generales ----------
        comboTipo = new ComboBox<>();
        comboTipo.getItems().addAll(TipoPropiedad.values());
        comboTipo.setPromptText("Elegi uno");
        // cuando cambia el tipo muestro los datos de departamento o de chalet
        comboTipo.setOnAction(e -> actualizarSecciones());

        comboZona = new ComboBox<>();
        comboZona.getItems().addAll(Zona.values());
        comboZona.setPromptText("Elegi una");

        campoCalle = new TextField();
        campoNumero = new TextField();
        campoNumero.setPromptText("1 a 99999");

        areaDescripcion = new TextArea();
        areaDescripcion.setPromptText("puede quedar vacia");
        areaDescripcion.setPrefRowCount(2);
        areaDescripcion.setPrefColumnCount(20);

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
        campoSuperficie.setPromptText("mayor a 0 (ej: 45,5)");
        campoTerreno = new TextField();
        campoTerreno.setPromptText("0 si no tiene");

        GridPane grillaGeneral = new GridPane();
        grillaGeneral.setHgap(10);
        grillaGeneral.setVgap(8);
        int fila = 0;
        grillaGeneral.add(new Label("Tipo:"), 0, fila);
        grillaGeneral.add(comboTipo, 1, fila);
        fila++;
        grillaGeneral.add(new Label("Zona:"), 0, fila);
        grillaGeneral.add(comboZona, 1, fila);
        fila++;
        grillaGeneral.add(new Label("Calle:"), 0, fila);
        grillaGeneral.add(campoCalle, 1, fila);
        fila++;
        grillaGeneral.add(new Label("Numero:"), 0, fila);
        grillaGeneral.add(campoNumero, 1, fila);
        fila++;
        grillaGeneral.add(new Label("Descripcion:"), 0, fila);
        grillaGeneral.add(areaDescripcion, 1, fila);
        fila++;
        grillaGeneral.add(new Label("Ambientes:"), 0, fila);
        grillaGeneral.add(campoAmbientes, 1, fila);
        fila++;
        grillaGeneral.add(new Label("Dormitorios:"), 0, fila);
        grillaGeneral.add(campoDormitorios, 1, fila);
        fila++;
        grillaGeneral.add(new Label("Banios:"), 0, fila);
        grillaGeneral.add(campoBanios, 1, fila);
        fila++;
        grillaGeneral.add(checkBalcon, 1, fila);
        fila++;
        grillaGeneral.add(checkCochera, 1, fila);
        fila++;
        grillaGeneral.add(new Label("Anio de construccion:"), 0, fila);
        grillaGeneral.add(campoAnio, 1, fila);
        fila++;
        grillaGeneral.add(new Label("Conservacion:"), 0, fila);
        grillaGeneral.add(comboConservacion, 1, fila);
        fila++;
        grillaGeneral.add(new Label("Superficie cubierta m2:"), 0, fila);
        grillaGeneral.add(campoSuperficie, 1, fila);
        fila++;
        grillaGeneral.add(new Label("Terreno m2:"), 0, fila);
        grillaGeneral.add(campoTerreno, 1, fila);

        // ---------- datos de departamento ----------
        campoNumeroDpto = new TextField();
        campoNumeroDpto.setPromptText("ej: 4B");
        campoPiso = new TextField();
        campoPiso.setPromptText("0 a 100 (0 es PB)");
        checkAscensor = new CheckBox("Tiene ascensor");
        checkVistaAlMar = new CheckBox("Tiene vista al mar");
        comboDisposicion = new ComboBox<>();
        comboDisposicion.getItems().addAll(Disposicion.values());
        comboDisposicion.setPromptText("Elegi una");

        GridPane grillaDepto = new GridPane();
        grillaDepto.setHgap(10);
        grillaDepto.setVgap(8);
        grillaDepto.add(new Label("Departamento:"), 0, 0);
        grillaDepto.add(campoNumeroDpto, 1, 0);
        grillaDepto.add(new Label("Piso:"), 0, 1);
        grillaDepto.add(campoPiso, 1, 1);
        grillaDepto.add(checkAscensor, 1, 2);
        grillaDepto.add(checkVistaAlMar, 1, 3);
        grillaDepto.add(new Label("Disposicion:"), 0, 4);
        grillaDepto.add(comboDisposicion, 1, 4);

        Label tituloDepto = new Label("Datos del departamento");
        tituloDepto.setStyle("-fx-font-weight: bold;");
        seccionDepartamento = new VBox(8, tituloDepto, grillaDepto);

        // ---------- datos de chalet ----------
        campoCantPisos = new TextField();
        campoCantPisos.setPromptText("1 a 5");
        checkPileta = new CheckBox("Tiene pileta");
        checkQuincho = new CheckBox("Tiene quincho");
        checkJardin = new CheckBox("Tiene jardin");

        GridPane grillaChalet = new GridPane();
        grillaChalet.setHgap(10);
        grillaChalet.setVgap(8);
        grillaChalet.add(new Label("Cantidad de pisos:"), 0, 0);
        grillaChalet.add(campoCantPisos, 1, 0);
        grillaChalet.add(checkPileta, 1, 1);
        grillaChalet.add(checkQuincho, 1, 2);
        grillaChalet.add(checkJardin, 1, 3);

        Label tituloChalet = new Label("Datos del chalet");
        tituloChalet.setStyle("-fx-font-weight: bold;");
        seccionChalet = new VBox(8, tituloChalet, grillaChalet);

        // al principio no se ve ninguna de las dos hasta que elija el tipo
        actualizarSecciones();

        // ---------- botones y errores ----------
        Button botonGuardar = new Button("Guardar");
        botonGuardar.setOnAction(e -> guardar());
        Button botonCancelar = new Button("Cancelar");
        botonCancelar.setOnAction(e -> ventana.close());

        HBox botones = new HBox(10, botonGuardar, botonCancelar);
        botones.setAlignment(Pos.CENTER_LEFT);

        etiquetaErrores = new Label();
        etiquetaErrores.setStyle("-fx-text-fill: red;");
        etiquetaErrores.setWrapText(true);

        Label titulo = new Label("CARGAR PROPIEDAD");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox contenido = new VBox(15, titulo, grillaGeneral, seccionDepartamento, seccionChalet, botones, etiquetaErrores);
        contenido.setPadding(new Insets(20));

        // con scroll por si la pantalla es chica
        ScrollPane scroll = new ScrollPane(contenido);
        scroll.setFitToWidth(true);

        return new Scene(scroll, 520, 700);
    }

    // muestra la parte de departamento o la de chalet segun el tipo elegido
    private void actualizarSecciones() {
        boolean esDepto = comboTipo.getValue() == TipoPropiedad.DEPARTAMENTO;
        boolean esChalet = comboTipo.getValue() == TipoPropiedad.CHALET;

        seccionDepartamento.setVisible(esDepto);
        seccionDepartamento.setManaged(esDepto); // managed false = no ocupa lugar

        seccionChalet.setVisible(esChalet);
        seccionChalet.setManaged(esChalet);
    }

    private void guardar() {
        // voy juntando todos los errores para mostrarlos juntos
        List<String> errores = new ArrayList<>();
        int anioActual = LocalDate.now().getYear();

        // 1) tipo y zona
        TipoPropiedad tipo = comboTipo.getValue();
        if (tipo == null) {
            errores.add("Elegi el tipo de propiedad");
        }
        Zona zona = comboZona.getValue();
        if (zona == null) {
            errores.add("Elegi la zona");
        }

        // 2) direccion
        String calle = campoCalle.getText().trim();
        if (calle.isEmpty()) {
            errores.add("La calle no puede estar vacia");
        }
        int numero = leerEntero(campoNumero, "Numero de la calle", 1, 99999, errores);

        // 3) descripcion (puede quedar vacia)
        String descripcion = areaDescripcion.getText().trim();
        if (descripcion.isEmpty()) {
            descripcion = " ";
        }

        // 4) ambientes, dormitorios y banios
        int ambientes = leerEntero(campoAmbientes, "Ambientes", 1, 8, errores);
        int dormitorios = leerEntero(campoDormitorios, "Dormitorios", 0, 8, errores);
        // no puede tener mas dormitorios que ambientes (solo si los dos estan bien)
        if (ambientes != -1 && dormitorios != -1 && dormitorios > ambientes) {
            errores.add("No puede tener mas dormitorios (" + dormitorios + ") que ambientes (" + ambientes + ")");
        }
        int banios = leerEntero(campoBanios, "Banios", 1, 6, errores);

        // 5) balcon y cochera (son checkbox, no hace falta verificar)
        boolean balcon = checkBalcon.isSelected();
        boolean cochera = checkCochera.isSelected();

        // 6) anio (no puede ser del futuro)
        int anioConstruccion = leerEntero(campoAnio, "Anio de construccion", 1900, anioActual, errores);

        // 7) estado de conservacion
        EstadoConservacion estadoConservacion = comboConservacion.getValue();
        if (estadoConservacion == null) {
            errores.add("Elegi el estado de conservacion");
        }

        // 8) superficies (la cubierta mayor a 0 porque con eso se calcula el precio)
        double superficieCubierta = leerDecimal(campoSuperficie, "Superficie cubierta", 0, false, errores);
        double terreno = leerDecimal(campoTerreno, "Terreno", 0, true, errores);

        // 9) datos que dependen del tipo
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

        // si hubo algun error los muestro y no guardo
        if (!errores.isEmpty()) {
            etiquetaErrores.setText("Revisa esto:\n- " + String.join("\n- ", errores));
            return;
        }
        etiquetaErrores.setText("");

        // 10) verifico que no este cargada (igual que en el Menu)
        for (Propiedad p : existentes) {
            if (tipo == TipoPropiedad.DEPARTAMENTO && p instanceof Departamento) {
                Departamento d = (Departamento) p;
                if (d.getDireccionCalle().equalsIgnoreCase(calle)
                        && d.getDireccionNumero() == numero
                        && d.getNumeroDpto() != null
                        && d.getNumeroDpto().equalsIgnoreCase(numeroDpto)) {
                    etiquetaErrores.setText("Ese departamento ya esta cargado (id " + d.getId() + ")");
                    return;
                }
            }
            if (tipo == TipoPropiedad.CHALET && p instanceof Chalet
                    && p.getDireccionCalle().equalsIgnoreCase(calle)
                    && p.getDireccionNumero() == numero) {
                etiquetaErrores.setText("Ese chalet ya esta cargado (id " + p.getId() + ")");
                return;
            }
        }

        // 11) armo la propiedad
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

        // 12) muestro como quedo y pregunto si la guardo
        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION);
        confirmar.setTitle("Confirmar");
        confirmar.setHeaderText("Guardar esta propiedad? (precio calculado: USD " + nueva.getPrecio() + ")");
        confirmar.setContentText(nueva.toString());
        Optional<ButtonType> respuesta = confirmar.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
            return;
        }

        // 13) la guardo en la base
        try {
            propiedadesDAO.insertar(nueva);
            Alert listo = new Alert(Alert.AlertType.INFORMATION);
            listo.setTitle("Listo");
            listo.setHeaderText(null);
            listo.setContentText("Propiedad guardada. Queda en TASACION hasta que la publique un administrador.");
            listo.showAndWait();
            ventana.close();
        } catch (SQLException e) {
            etiquetaErrores.setText("Error con la base de datos: " + e.getMessage());
        }
    }

    // lee un numero entero de un campo y verifica que este entre minimo y maximo
    // si esta mal agrega el error a la lista y devuelve -1
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

    // lee un numero con coma o punto (ej: 45,5 o 45.5)
    // si puedeSerIgual es true acepta el minimo (ej: terreno 0), si es false tiene que ser mayor (ej: superficie)
    // si esta mal agrega el error a la lista y devuelve -1
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
            errores.add(nombre + ": no puede ser menor a " + minimo);
            return -1;
        }
        if (!puedeSerIgual && numero <= minimo) {
            errores.add(nombre + ": tiene que ser mayor a " + minimo);
            return -1;
        }
        return numero;
    }
}
