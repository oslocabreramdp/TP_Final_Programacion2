package gestores;

import enums.EstadoPublicacion;
import enums.TipoPropiedad;
import enums.Zona;
import excepciones.ElementoDuplicadoException;
import excepciones.ElementoNoEncontradoException;
import modelo.Chalet;
import modelo.Departamento;
import modelo.Propiedad;

import java.util.ArrayList;
import java.util.List;

public class GestorPropiedades extends GestorGenerico<Propiedad> {

    public GestorPropiedades() {
        super();
    }


    @Override
    public void agregarElemento(Propiedad propiedad) throws ElementoDuplicadoException {
        if (propiedad == null) {
            throw new IllegalArgumentException("La propiedad no puede ser null");
        }
        super.agregarElemento(propiedad);
    }


    public List<Propiedad> buscarPorZona(Zona zona) {
        List<Propiedad> resultado = new ArrayList<>();
        for (Propiedad p : listar()) {
            if (p.getZona() == zona) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public List<Propiedad> buscarPorTipo(TipoPropiedad tipo) {
        List<Propiedad> resultado = new ArrayList<>();
        for (Propiedad p : listar()) {
            if (p.getTipoPropiedad() == tipo) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public List<Propiedad> buscarPorEstado(EstadoPublicacion estado) {
        List<Propiedad> resultado = new ArrayList<>();
        for (Propiedad p : listar()) {
            if (p.getEstado() == estado) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public List<Propiedad> buscarDisponibles() {
        return buscarPorEstado(EstadoPublicacion.DISPONIBLE);
    }


    public List<Propiedad> buscarPorAmbientes(int ambientes) {
        List<Propiedad> resultado = new ArrayList<>();
        for (Propiedad p : listar()) {
            if (p.getAmbientes() >= ambientes) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public List<Propiedad> buscarPorCalle(String texto) {
        List<Propiedad> resultado = new ArrayList<>();
        String buscado = texto.toLowerCase();
        for (Propiedad p : listar()) {
            if (p.getDireccionCalle() != null) {
                String calle = p.getDireccionCalle().toLowerCase();
                if (calle.contains(buscado)) {
                    resultado.add(p);
                }
            }
        }
        return resultado;
    }

    // en castear al tipo definido en el menu, despues lo arreglo
    public List<Propiedad> listarTipo(TipoPropiedad tipoPropiedad) {
        List<Propiedad> chalets = new ArrayList<>();
        for (Propiedad p : listar()) {
            if (p instanceof Chalet) {
                chalets.add((Chalet) p);
            }
        }
        return chalets;
    }

    public void cambiarEstado(int id, EstadoPublicacion nuevoEstado) throws ElementoNoEncontradoException {
        Propiedad p = buscarPorId(id);
        p.setEstado(nuevoEstado);
    }

    public void publicar(int id) throws ElementoNoEncontradoException {
        cambiarEstado(id, EstadoPublicacion.DISPONIBLE);
    }

    public void reservar(int id) throws ElementoNoEncontradoException {
        Propiedad p = buscarPorId(id);
        if (p.getEstado() != EstadoPublicacion.DISPONIBLE) {
            throw new IllegalStateException("Solo se puede reservar una propiedad DISPONIBLE (estado actual: " + p.getEstado() + ")");
        }
        p.setEstado(EstadoPublicacion.RESERVADA);
    }

    public void vender(int id) throws ElementoNoEncontradoException {
        Propiedad p = buscarPorId(id);
        if (p.getEstado() == EstadoPublicacion.VENDIDA) {
            throw new IllegalStateException("La propiedad " + id + " ya fue vendida");
        }
        p.setEstado(EstadoPublicacion.VENDIDA);
    }

    public int cantidad() {
        return listar().size();
    }

}
