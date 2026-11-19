package gestores;

import dao.PropiedadesDAO;
import enums.EstadoPublicacion;
import enums.TipoPropiedad;
import enums.Zona;
import excepciones.ElementoDuplicadoException;
import excepciones.ElementoNoEncontradoException;
import modelo.Propiedad;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GestorPropiedades extends GestorGenerico<Propiedad> {

    private final PropiedadesDAO propiedadesDAO;

    public GestorPropiedades() {
        super();
        this.propiedadesDAO = new PropiedadesDAO();
    }

    @Override
    public void agregarElemento(Propiedad propiedad) throws ElementoDuplicadoException {
        if (propiedad == null) {
            throw new IllegalArgumentException("La propiedad no puede ser null");
        }
        try {
            boolean insertado = propiedadesDAO.insertar(propiedad);
            if (!insertado) {
                throw new RuntimeException("No se pudo insertar la propiedad en la base de datos.");
            }
            super.agregarElemento(propiedad);
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos al guardar la propiedad: " + e.getMessage(), e);
        }
    }

    /**
     * Carga propiedades ya existentes desde la BD en memoria sin volver a ejecutar INSERT.
     */
    public void cargarDesdeBaseDeDatos() throws ElementoDuplicadoException {
        try {
            for (Propiedad p : propiedadesDAO.listarTodos()) {
                if (!existe(p.getId())) {
                    super.agregarElemento(p);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos al cargar propiedades: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminarElemento(int id) throws ElementoNoEncontradoException {
        buscarPorId(id); // Valida que exista en memoria antes de borrar
        try {
            boolean eliminadoEnBd = propiedadesDAO.eliminar(id);
            if (!eliminadoEnBd) {
                throw new ElementoNoEncontradoException(id);
            }
            return super.eliminarElemento(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos al eliminar la propiedad: " + e.getMessage(), e);
        }
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

    public List<Propiedad> listarTipo(TipoPropiedad tipoPropiedad) {
        List<Propiedad> resultado = new ArrayList<>();
        for (Propiedad p : listar()) {
            if (p.getTipoPropiedad() == tipoPropiedad) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public void cambiarEstado(int id, EstadoPublicacion nuevoEstado) throws ElementoNoEncontradoException {
        Propiedad p = buscarPorId(id);
        actualizarEstadoPersistido(p, nuevoEstado);
    }

    public void publicar(int id) throws ElementoNoEncontradoException {
        Propiedad p = buscarPorId(id);
        actualizarEstadoPersistido(p, EstadoPublicacion.DISPONIBLE);
    }

    public void reservar(int id) throws ElementoNoEncontradoException {
        Propiedad p = buscarPorId(id);
        if (p.getEstado() != EstadoPublicacion.DISPONIBLE) {
            throw new IllegalStateException("Solo se puede reservar una propiedad DISPONIBLE (estado actual: " + p.getEstado() + ")");
        }
        actualizarEstadoPersistido(p, EstadoPublicacion.RESERVADA);
    }

    public void vender(int id) throws ElementoNoEncontradoException {
        Propiedad p = buscarPorId(id);
        if (p.getEstado() == EstadoPublicacion.VENDIDA) {
            throw new IllegalStateException("La propiedad " + id + " ya fue vendida");
        }
        actualizarEstadoPersistido(p, EstadoPublicacion.VENDIDA);
    }

    private void actualizarEstadoPersistido(Propiedad p, EstadoPublicacion nuevoEstado) {
        try {
            boolean actualizado = propiedadesDAO.cambiarEstado(p.getId(), nuevoEstado);
            if (!actualizado) {
                throw new RuntimeException("No se pudo actualizar el estado de la propiedad ID " + p.getId() + " en la base de datos.");
            }
            p.setEstado(nuevoEstado);
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos al cambiar estado de la propiedad: " + e.getMessage(), e);
        }
    }

    public int cantidad() {
        return listar().size();
    }
}