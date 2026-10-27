package Gestores;

import java.util.ArrayList;
import java.util.List;

public class GestorGenerico<T extends Identificable> {
    private final List<T> lista;

    public GestorGenerico() {
        this.lista = new ArrayList<>();
    }

    public boolean existe(int id) {
        for (T e : lista) {
            if (e.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public List<T> listar() {
        return new ArrayList<>(lista);
    }

    public void agregarElemento(T elemento) throws ElementoDuplicadoExpection {
        if (existe(elemento.getId())) {
            throw new ElementoDuplicadoExpection("Ya existe un elemento con id " + elemento.getId());
        }
        lista.add(elemento);
    }

    public T buscarPorId(int id) throws ElementoNoEncontradoException {
        for (T e : lista) {
            if (e.getId() == id) {
                return e;
            }
        }
        throw new ElementoNoEncontradoException("No existe un elemento con id " + id);
    }

    public boolean eliminarElemento(int id) throws ElementoNoEncontradoException {
        return lista.remove(buscarPorId(id));
    }

    public List<T> buscarPor(UsoGestorGenerico<T> criterio) {
        List<T> resultado = new ArrayList<>();
        for (T e : lista) {
            if (criterio.cumple(e)) {
                resultado.add(e);
            }
        }
        return resultado;
    }

    public void actualizarElemento(T nuevo) throws ElementoNoEncontradoException {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == nuevo.getId()) {
                lista.set(i, nuevo);
                return;
            }

        }
        throw new ElementoNoEncontradoException("No existe un elemento con id" + nuevo.getId());
    }
}
