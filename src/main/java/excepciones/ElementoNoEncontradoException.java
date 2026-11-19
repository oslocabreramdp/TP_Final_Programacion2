package excepciones;

public class ElementoNoEncontradoException extends Exception {
    public ElementoNoEncontradoException(int id) {
        super("No se encontro ningun elemento con el identificador: " + id);
    }

    public ElementoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
