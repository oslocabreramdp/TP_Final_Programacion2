package vista;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

import java.net.URL;
import java.util.Optional;

public class Avisos {

    public static void mostrarAviso(String texto) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Atencion");
        alerta.setHeaderText(null);
        alerta.setContentText(texto);
        darEstilo(alerta.getDialogPane());
        alerta.showAndWait();
    }

    public static void mostrarInformacion(String titulo, String texto) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(texto);
        darEstilo(alerta.getDialogPane());
        alerta.showAndWait();
    }

    public static void mostrarDetalle(String titulo, String encabezado, String texto) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(texto);
        darEstilo(alerta.getDialogPane());
        alerta.showAndWait();
    }

    public static boolean preguntar(String texto) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar");
        alerta.setHeaderText(null);
        alerta.setContentText(texto);
        darEstilo(alerta.getDialogPane());
        Optional<ButtonType> respuesta = alerta.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.OK;
    }

    public static void darEstilo(DialogPane panel) {
        URL estilos = Avisos.class.getResource("/vista/estilos.css");
        if (estilos != null) {
            panel.getStylesheets().add(estilos.toExternalForm());
        }
        panel.setMinWidth(420);
    }
}
