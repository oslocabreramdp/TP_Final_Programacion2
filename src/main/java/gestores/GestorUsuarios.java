package gestores;

import dao.UsuariosDAO;
import excepciones.ElementoDuplicadoException;
import excepciones.ElementoNoEncontradoException;
import modelo.Administrador;
import modelo.ProductorDeVenta;
import modelo.Propiedad;
import modelo.Usuario;

import java.sql.SQLException;
import java.util.List;

public class GestorUsuarios extends GestorGenerico<Usuario> {

    private final UsuariosDAO usuariosDAO;
    private final GestorPropiedades gestorPropiedades;

    public GestorUsuarios(GestorPropiedades gestorPropiedades) {
        super();
        this.usuariosDAO = new UsuariosDAO();
        this.gestorPropiedades = gestorPropiedades;
    }


    @Override
    public Usuario buscarPorId(int id) throws ElementoNoEncontradoException {
        try {
            Usuario u = usuariosDAO.buscarPorId(id);
            if (u == null) {
                throw new ElementoNoEncontradoException(id);
            }
            return u;
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos al buscar usuario por ID: " + e.getMessage(), e);
        }
    }

    public Usuario buscarPorMail(String mail) throws ElementoNoEncontradoException {
        try {
            Usuario u = usuariosDAO.buscarPorEmail(mail);
            if (u == null) {
                throw new ElementoNoEncontradoException("No se encontró un usuario con el email: " + mail);
            }
            return u;
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos al buscar usuario por email: " + e.getMessage(), e);
        }
    }


    public void aprobarProductor(int idAdmin, int idProductor) throws ElementoNoEncontradoException, SQLException {
        Usuario admin = buscarPorId(idAdmin);
        Usuario candidato = buscarPorId(idProductor);

        if (!(admin instanceof Administrador)) {
            throw new SecurityException("Solo un Administrador puede aprobar productores.");
        }

        if (!(candidato instanceof ProductorDeVenta)) {
            throw new IllegalArgumentException("El usuario con ID " + idProductor + " no es un Productor de Venta.");
        }

        boolean actualizado = usuariosDAO.cambiarEstado(idProductor, true);
        if (!actualizado) {
            throw new SQLException("No se pudo actualizar el estado del productor en la base de datos.");
        }
    }


    public void darAltaPropiedad(int idUsuario, Propiedad nuevaPropiedad) throws ElementoNoEncontradoException, SQLException, ElementoDuplicadoException {
        Usuario u = buscarPorId(idUsuario);

        boolean esProductorAprobado = (u instanceof ProductorDeVenta) && u.isActivo();
        boolean esAdmin = (u instanceof Administrador);

        if (esProductorAprobado || esAdmin) {
            gestorPropiedades.agregarElemento(nuevaPropiedad);
        } else {
            throw new SecurityException("El usuario no tiene permisos o aún no está aprobado para dar de alta propiedades.");
        }
    }

    public void darBajaPropiedad(int idUsuario, int idPropiedad) throws ElementoNoEncontradoException, SQLException {
        Usuario u = buscarPorId(idUsuario);

        boolean esProductorAprobado = (u instanceof ProductorDeVenta) && u.isActivo();
        boolean esAdmin = (u instanceof Administrador);

        if (esProductorAprobado || esAdmin) {
            gestorPropiedades.eliminarElemento(idPropiedad);
        } else {
            throw new SecurityException("No tienes permisos para dar de baja propiedades.");
        }
    }


    public List<Propiedad> buscarPropiedadPorDireccion(String calle) throws ElementoNoEncontradoException, SQLException {
        return gestorPropiedades.buscarPorCalle(calle);
    }


}
