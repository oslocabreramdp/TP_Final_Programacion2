package dao;

import excepciones.ElementoDuplicadoException;
import excepciones.ElementoNoEncontradoException;
import gestores.GestorGenerico;
import interfaces.UsoGestorGenerico;
import modelo.Chalet;
import modelo.Departamento;
import modelo.Propiedad;

import java.sql.*;
import java.util.List;

// todavia no esta terminado, despues lo seguimos.
// falta listar, buscar, actualizar y borrar en la base
public class PropiedadesDAO extends GestorGenerico<Propiedad> {


    @Override
    public boolean eliminarElemento(int id) throws ElementoNoEncontradoException {
        return super.eliminarElemento(id);
    }


    @Override
    public List<Propiedad> buscarPor(UsoGestorGenerico<Propiedad> criterio) {
        return super.buscarPor(criterio);
    }

    @Override
    public void actualizarElemento(Propiedad nuevo) throws ElementoNoEncontradoException {
        super.actualizarElemento(nuevo);
    }

    @Override
    public List<Propiedad> listar() {
        return super.listar();
    }

    @Override
    public boolean existe(int id) {
        return super.existe(id);
    }

    public List<Propiedad> listarTodos() throws SQLException {
        return List.of();
    }


    public Propiedad buscarPorIdEnBD(int id) throws SQLException {
        return null;
    }

    @Override
    public void agregarElemento(Propiedad propiedad) throws ElementoDuplicadoException {
        String sql = "INSERT INTO propiedades (tipo_propiedad, descripcion, zona, direccion_calle, direccion_numero, " +
                "precio, balcon, ambientes, dormitorios, banios, cochera, anio_construccion, fecha_publicacion, " +
                "estado_propiedad, estado_conservacion, terreno, superficie_cubierta, " +
                "departamento, piso, ascensor, vista_al_mar, disposicion, " +
                "cantidad_pisos, pileta, quincho, jardin) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = config.Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, propiedad.getId());
            ps.setString(2, propiedad.getDireccionCalle());
            ps.setInt(3, propiedad.getDireccionNumero());
            ps.setString(5, propiedad.getTipoPropiedad().name());
            ps.setInt(6, propiedad.getAmbientes());
            ps.setString(7, propiedad.getZona().name());
            ps.setInt(8, propiedad.getBanios());
            ps.setInt(9, propiedad.getAnioConstruccion());
            ps.setBoolean(10, propiedad.isBalcon());
            ps.setBoolean(11, propiedad.isCochera());
            ps.setDouble(12, propiedad.getTerreno());
            ps.setDouble(13, propiedad.getPrecio());
            ps.setDouble(17, propiedad.getSuperficieCubierta());

            if (propiedad instanceof Departamento){
                ps.setInt(4, ((Departamento) propiedad).getPiso());
                ps.setString(5, ((Departamento) propiedad).getNumeroDpto());
                ps.setBoolean(19, ((Departamento) propiedad).isVistaAlMar());
                ps.setString(20, ((Departamento) propiedad).getDisposicion().name());
            }

            if (propiedad instanceof Chalet){
                ps.setInt(4, ((Chalet) propiedad).getCantPisos());
                ps.setBoolean(5, ((Chalet) propiedad).isJardin());
                ps.setBoolean(19, ((Chalet) propiedad).isQuincho());
                ps.setBoolean(24, ((Chalet) propiedad).isPileta());
            }

        } catch (SQLException e) {
            e.getMessage();
        }

    }
}