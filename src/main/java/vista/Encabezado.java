package vista;

import enums.TipoUsuario;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import modelo.Usuario;

public class Encabezado {

    public static final String PROPIEDADES = "PROPIEDADES";
    public static final String USUARIOS = "USUARIOS";
    public static final String RANKING = "RANKING";

    public static String claseDelRol(Usuario usuario) {
        if (usuario == null) {
            return "rol-visitante";
        }
        if (usuario.getRol() == TipoUsuario.ADMINISTRADOR) {
            return "rol-administrador";
        }
        if (usuario.getRol() == TipoUsuario.PRODUCTOR_DE_VENTA) {
            return "rol-productor";
        }
        return "rol-cliente";
    }

    public static String nombreDelRol(Usuario usuario) {
        if (usuario == null) {
            return "VISITANTE";
        }
        if (usuario.getRol() == TipoUsuario.ADMINISTRADOR) {
            return "ADMINISTRADOR";
        }
        if (usuario.getRol() == TipoUsuario.PRODUCTOR_DE_VENTA) {
            return "PRODUCTOR DE VENTA";
        }
        return "CLIENTE";
    }

    public static HBox crear(VentanaPrincipal principal, Usuario usuario, String seccionActual) {
        Label marca = new Label("CUCHUFLITO PROPIEDADES");
        marca.getStyleClass().add("encabezado-marca");

        HBox encabezado = new HBox(marca);
        encabezado.getStyleClass().add("encabezado");

        if (usuario != null && usuario.getRol() == TipoUsuario.ADMINISTRADOR) {
            Button botonPropiedades = crearBotonMenu("Propiedades", PROPIEDADES, seccionActual);
            botonPropiedades.setOnAction(e -> principal.mostrarPropiedades(usuario));

            Button botonUsuarios = crearBotonMenu("Usuarios", USUARIOS, seccionActual);
            botonUsuarios.setOnAction(e -> principal.mostrarUsuarios(usuario));

            Button botonRanking = crearBotonMenu("Ranking e informes", RANKING, seccionActual);
            botonRanking.setOnAction(e -> principal.mostrarRanking(usuario));

            HBox menu = new HBox(4, botonPropiedades, botonUsuarios, botonRanking);
            encabezado.getChildren().add(menu);
        }

        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        encabezado.getChildren().add(espacio);

        if (usuario != null) {
            Label nombre = new Label(usuario.getNombre() + " " + usuario.getApellido());
            nombre.getStyleClass().add("encabezado-usuario");
            encabezado.getChildren().add(nombre);
        }

        Label insignia = new Label(nombreDelRol(usuario));
        insignia.getStyleClass().add("insignia-rol");
        encabezado.getChildren().add(insignia);

        Button botonSalir;
        if (usuario == null) {
            botonSalir = new Button("Iniciar sesion");
        } else {
            botonSalir = new Button("Cerrar sesion");
        }
        botonSalir.getStyleClass().add("boton-encabezado");
        botonSalir.setOnAction(e -> principal.mostrarLogin());
        encabezado.getChildren().add(botonSalir);

        return encabezado;
    }

    private static Button crearBotonMenu(String texto, String seccion, String seccionActual) {
        Button boton = new Button(texto);
        boton.getStyleClass().add("boton-menu");
        if (seccion.equals(seccionActual)) {
            boton.getStyleClass().add("boton-menu-activo");
        }
        return boton;
    }
}
