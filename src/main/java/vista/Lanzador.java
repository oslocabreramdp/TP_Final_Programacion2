// -***- ESTE ES EL CAMBIO -***- (archivo nuevo)
package vista;

import javafx.application.Application;

// Esta clase solo arranca la parte grafica.
// Se hace asi (una clase aparte que no extiende Application) para que IntelliJ
// no tire el error "JavaFX runtime components are missing" al darle Run.
public class Lanzador {
    public static void main(String[] args) {
        Application.launch(VentanaPrincipal.class, args);
    }
}
