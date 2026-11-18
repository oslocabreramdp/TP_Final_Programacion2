package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import config.Conexion;
import enums.Disposicion;
import enums.EstadoConservacion;
import enums.EstadoPublicacion;
import enums.TipoPropiedad;
import enums.Zona;
import modelo.Chalet;
import modelo.Departamento;
import modelo.Propiedad;

public class PropiedadesDAO {

    public Propiedad mapear(ResultSet rs) throws SQLException {
        String tipoStr = rs.getString("tipo_propiedad");
        TipoPropiedad tipo = TipoPropiedad.valueOf(tipoStr);

        int id = rs.getInt("id_propiedad");
        String descripcion = rs.getString("descripcion");
        Zona zona = Zona.valueOf(rs.getString("zona"));
        String direccionCalle = rs.getString("direccion_calle");
        int direccionNumero = rs.getInt("direccion_numero");
        boolean balcon = rs.getBoolean("balcon");
        int ambientes = rs.getInt("ambientes");
        int dormitorios = rs.getInt("dormitorios");
        int banios = rs.getInt("banios");
        boolean cochera = rs.getBoolean("cochera");
        int anioConstruccion = rs.getInt("anio_construccion");
        LocalDate fechaPublicacion = rs.getDate("fecha_publicacion").toLocalDate();
        EstadoPublicacion estado = EstadoPublicacion.valueOf(rs.getString("estado_propiedad"));
        EstadoConservacion estadoConservacion = EstadoConservacion.valueOf(rs.getString("estado_conservacion"));
        double terreno = rs.getDouble("terreno");
        double superficieCubierta = rs.getDouble("superficie_cubierta");

        switch (tipo) {
            case DEPARTAMENTO:
                String numeroDpto = rs.getString("departamento");
                int piso = rs.getInt("piso");
                boolean ascensor = rs.getBoolean("ascensor");
                boolean vistaAlMar = rs.getBoolean("vista_al_mar");
                String dispStr = rs.getString("disposicion");
                Disposicion disposicion = dispStr != null ? Disposicion.valueOf(dispStr) : Disposicion.NINGUNA;

                // -***- ESTE ES EL CAMBIO -***-
                // antes se hacia return new Departamento(...) directo y se perdian el id, el estado
                // y la fecha que vienen de la base (quedaba todo en TASACION y con el id del contador)
                Departamento departamento = new Departamento(
                        TipoPropiedad.DEPARTAMENTO, descripcion, zona, direccionCalle, direccionNumero,
                        balcon, ambientes, dormitorios, banios, cochera, anioConstruccion,
                        estadoConservacion, terreno, superficieCubierta,
                        numeroDpto, piso, ascensor, vistaAlMar, disposicion
                );
                departamento.setId(id);
                departamento.setEstado(estado);
                departamento.setFechaPublicacion(fechaPublicacion);
                return departamento;

            case CHALET:
                int cantPisos = rs.getInt("cantidad_pisos");
                boolean pileta = rs.getBoolean("pileta");
                boolean quincho = rs.getBoolean("quincho");
                boolean jardin = rs.getBoolean("jardin");

                // -***- ESTE ES EL CAMBIO -***- (lo mismo que en departamento)
                Chalet chalet = new Chalet(
                        TipoPropiedad.CHALET, descripcion, zona, direccionCalle, direccionNumero,
                        balcon, ambientes, dormitorios, banios, cochera, anioConstruccion,
                        estadoConservacion, terreno, superficieCubierta,
                        cantPisos, pileta, quincho, jardin
                );
                chalet.setId(id);
                chalet.setEstado(estado);
                chalet.setFechaPublicacion(fechaPublicacion);
                return chalet;

            default:
                throw new SQLException("Tipo de propiedad desconocido en la base de datos: " + tipoStr);
        }
    }

    public List<Propiedad> listarTodos() throws SQLException {
        String sql = "SELECT * FROM propiedades";
        List<Propiedad> propiedades = new ArrayList<>();

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                propiedades.add(mapear(rs));
            }
        }
        return propiedades;
    }

    public Propiedad buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM propiedades WHERE id_propiedad = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public boolean insertar(Propiedad propiedad) throws SQLException {
        String sql = "INSERT INTO propiedades (tipo_propiedad, descripcion, zona, direccion_calle, direccion_numero, precio, balcon, " +
                "ambientes, dormitorios, banios, cochera, anio_construccion, fecha_publicacion, estado_propiedad, estado_conservacion, " +
                "terreno, superficie_cubierta, departamento, piso, ascensor, vista_al_mar, disposicion, cantidad_pisos, pileta, quincho, jardin) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {


            ps.setString(1, propiedad.getTipoPropiedad().name());
            ps.setString(2, propiedad.getDescripcion());
            ps.setString(3, propiedad.getZona().name());
            ps.setString(4, propiedad.getDireccionCalle());
            ps.setInt(5, propiedad.getDireccionNumero());
            ps.setInt(6, propiedad.getPrecio()); // Precio calculado en modelo
            ps.setBoolean(7, propiedad.isBalcon());
            ps.setInt(8, propiedad.getAmbientes());
            ps.setInt(9, propiedad.getDormitorios());
            ps.setInt(10, propiedad.getBanios());
            ps.setInt(11, propiedad.isCochera() ? 1 : 0);
            ps.setInt(12, propiedad.getAnioConstruccion());
            ps.setDate(13, java.sql.Date.valueOf(propiedad.getFechaPublicacion()));
            ps.setString(14, propiedad.getEstado().name());
            ps.setString(15, propiedad.getEstadoConservacion().name());
            ps.setBigDecimal(16, java.math.BigDecimal.valueOf(propiedad.getTerreno()));
            ps.setBigDecimal(17, java.math.BigDecimal.valueOf(propiedad.getSuperficieCubierta()));


            if (propiedad instanceof Departamento) {
                Departamento dpto = (Departamento) propiedad;
                ps.setString(18, dpto.getNumeroDpto());
                ps.setInt(19, dpto.getPiso());
                ps.setBoolean(20, dpto.isAcensor());
                ps.setBoolean(21, dpto.isVistaAlMar());
                ps.setString(22, dpto.getDisposicion().name());

                // Null explicitos para CHALET
                ps.setNull(23, java.sql.Types.INTEGER);
                ps.setNull(24, java.sql.Types.BOOLEAN);
                ps.setNull(25, java.sql.Types.BOOLEAN);
                ps.setNull(26, java.sql.Types.BOOLEAN);

            } else if (propiedad instanceof Chalet) {
                Chalet chalet = (Chalet) propiedad;
                // Null explicito para DEPARTAMENTO
                ps.setNull(18, java.sql.Types.VARCHAR);
                ps.setNull(19, java.sql.Types.INTEGER);
                ps.setNull(20, java.sql.Types.BOOLEAN);
                ps.setNull(21, java.sql.Types.BOOLEAN);
                ps.setNull(22, java.sql.Types.VARCHAR);

                ps.setInt(23, chalet.getCantPisos());
                ps.setBoolean(24, chalet.isPileta());
                ps.setBoolean(25, chalet.isQuincho());
                ps.setBoolean(26, chalet.isJardin());
            }

            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Propiedad propiedad) throws SQLException {
        String sql = "UPDATE propiedades SET tipo_propiedad = ?, descripcion = ?, zona = ?, direccion_calle = ?, direccion_numero = ?, " +
                "precio = ?, balcon = ?, ambientes = ?, dormitorios = ?, banios = ?, cochera = ?, anio_construccion = ?, " +
                "estado_propiedad = ?, estado_conservacion = ?, terreno = ?, superficie_cubierta = ?, " +
                "departamento = ?, piso = ?, ascensor = ?, vista_al_mar = ?, disposicion = ?, " +
                "cantidad_pisos = ?, pileta = ?, quincho = ?, jardin = ? WHERE id_propiedad = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, propiedad.getTipoPropiedad().name());
            ps.setString(2, propiedad.getDescripcion());
            ps.setString(3, propiedad.getZona().name());
            ps.setString(4, propiedad.getDireccionCalle());
            ps.setInt(5, propiedad.getDireccionNumero());
            ps.setInt(6, propiedad.getPrecio());
            ps.setBoolean(7, propiedad.isBalcon());
            ps.setInt(8, propiedad.getAmbientes());
            ps.setInt(9, propiedad.getDormitorios());
            ps.setInt(10, propiedad.getBanios());
            ps.setBoolean(11, propiedad.isCochera());
            ps.setInt(12, propiedad.getAnioConstruccion());
            ps.setString(13, propiedad.getEstado().name());
            ps.setString(14, propiedad.getEstadoConservacion().name());
            ps.setBigDecimal(15, java.math.BigDecimal.valueOf(propiedad.getTerreno()));
            ps.setBigDecimal(16, java.math.BigDecimal.valueOf(propiedad.getSuperficieCubierta()));

            if (propiedad instanceof Departamento) {
                Departamento dpto = (Departamento) propiedad;
                ps.setString(17, dpto.getNumeroDpto());
                ps.setInt(18, dpto.getPiso());
                ps.setBoolean(19, dpto.isAcensor());
                ps.setBoolean(20, dpto.isVistaAlMar());
                ps.setString(21, dpto.getDisposicion().name());

                ps.setNull(22, java.sql.Types.INTEGER);
                ps.setNull(23, java.sql.Types.BOOLEAN);
                ps.setNull(24, java.sql.Types.BOOLEAN);
                ps.setNull(25, java.sql.Types.BOOLEAN);

            } else if (propiedad instanceof Chalet) {
                Chalet chalet = (Chalet) propiedad;
                ps.setNull(17, java.sql.Types.VARCHAR);
                ps.setNull(18, java.sql.Types.INTEGER);
                ps.setNull(19, java.sql.Types.BOOLEAN);
                ps.setNull(20, java.sql.Types.BOOLEAN);
                ps.setNull(21, java.sql.Types.VARCHAR);

                ps.setInt(22, chalet.getCantPisos());
                ps.setBoolean(23, chalet.isPileta());
                ps.setBoolean(24, chalet.isQuincho());
                ps.setBoolean(25, chalet.isJardin());
            }

            ps.setInt(26, propiedad.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean cambiarEstado(int idPropiedad, EstadoPublicacion nuevoEstado) throws SQLException {
        String sql = "UPDATE propiedades SET estado_propiedad = ? WHERE id_propiedad = ?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado.name());
            ps.setInt(2, idPropiedad);
            return ps.executeUpdate() > 0;
        }
    }

    // -***- ESTE ES EL CAMBIO -***-
    // borra la propiedad de la base (lo usa el administrador en la parte grafica)
    public boolean eliminar(int idPropiedad) throws SQLException {
        String sql = "DELETE FROM propiedades WHERE id_propiedad = ?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPropiedad);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Propiedad> listarPorZona(Zona zona) throws SQLException {
        String sql = "SELECT * FROM propiedades WHERE zona = ?";
        List<Propiedad> lista = new ArrayList<>();

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, zona.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public List<Propiedad> listarPorAmbientes(int ambientes) throws SQLException {
        String sql = "SELECT * FROM propiedades WHERE ambientes = ?";
        List<Propiedad> lista = new ArrayList<>();

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ambientes);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public List<Propiedad> listarPorTipo(TipoPropiedad tipo) throws SQLException {
        String sql = "SELECT * FROM propiedades WHERE tipo_propiedad = ?";
        List<Propiedad> lista = new ArrayList<>();

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tipo.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public List<Propiedad> listarDisponibles() throws SQLException {
        String sql = "SELECT * FROM propiedades WHERE estado_propiedad = 'DISPONIBLE'";
        List<Propiedad> lista = new ArrayList<>();

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Propiedad> listarPorRangoPrecio(double precioMin, double precioMax) throws SQLException {
        String sql = "SELECT * FROM propiedades WHERE precio BETWEEN ? AND ? ORDER BY precio ASC";
        List<Propiedad> lista = new ArrayList<>();

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Asignamos los parámetros de manera segura para evitar SQL Injection
            ps.setDouble(1, precioMin);
            ps.setDouble(2, precioMax);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public boolean actualizarPrecio(int idPropiedad, double nuevoPrecio) throws SQLException {
        String sql = "UPDATE propiedades SET precio = ? WHERE id_propiedad = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, nuevoPrecio);
            ps.setInt(2, idPropiedad);

            return ps.executeUpdate() > 0;
        }
    }

    public List<Propiedad> listarPorCalle(String calle) throws SQLException {
        String sql = "SELECT * FROM propiedades WHERE direccion_calle LIKE ?";
        List<Propiedad> lista = new ArrayList<>();

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Concatenamos '%' para buscar coincidencias parciales de forma segura
            ps.setString(1, "%" + calle + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }




}