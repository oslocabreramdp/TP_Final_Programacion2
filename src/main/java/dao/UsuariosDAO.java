package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import enums.TipoUsuario;
import modelo.Administrador;
import modelo.Cliente;
import modelo.ProductorDeVenta;
import config.Conexion;
import modelo.Usuario;

public class UsuariosDAO {

    public Usuario mapear(ResultSet rs) throws SQLException {
        String tipoStr = rs.getString("tipo_usuario");
        TipoUsuario tipo = TipoUsuario.valueOf(tipoStr);

        int idUsuario = rs.getInt("id_usuario");
        String nombre = rs.getString("nombre");
        String apellido = rs.getString("apellido");
        String telefono = rs.getString("telefono");
        String email = rs.getString("email");
        String passHash = rs.getString("pass_hash");
        boolean activo = rs.getBoolean("activo");
        LocalDate fechaAlta = rs.getDate("fecha_alta").toLocalDate();

        switch (tipo) {
            case ADMINISTRADOR:
                String legajo = rs.getString("legajo");
                return new Administrador(idUsuario, nombre, apellido, telefono, email, passHash, activo, fechaAlta, legajo);

            case PRODUCTOR_DE_VENTA:
                String cuilProd = rs.getString("cuil");
                int cantidadVentas = rs.getInt("cantidad_ventas");
                double montoVentas = rs.getDouble("monto_ventas");
                return new ProductorDeVenta(idUsuario, nombre, apellido, telefono, email, passHash, activo, fechaAlta, cuilProd, cantidadVentas, montoVentas);

            case CLIENTE:
                String dniCliente = rs.getString("cuil");
                return new Cliente(idUsuario, nombre, apellido, telefono, email, passHash, activo, fechaAlta, dniCliente);

            default:
                throw new SQLException("Tipo de usuario desconocido en la base de datos: " + tipoStr);
        }
    }

    public List<Usuario> listarTodos() throws SQLException {
        String sql = "SELECT * FROM usuarios";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapear(rs));
            }
        }
        return usuarios;
    }

    public List<Usuario> listarPorTipo(TipoUsuario tipo) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE tipo_usuario = ?";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tipo.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapear(rs));
                }
            }
        }
        return usuarios;
    }

    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";

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

    public boolean insertar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (tipo_usuario, nombre, apellido, telefono, email, pass_hash, activo, fecha_alta, legajo, cuil, cantidad_ventas, monto_ventas) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.obtenerConexion();
             // 1) Pedir las claves autoincrementales generadas por MySQL
             PreparedStatement ps = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            // Parámetros comunes
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getApellido());
            ps.setString(4, usuario.getTelefono());
            ps.setString(5, usuario.getEmail());
            ps.setString(6, usuario.getContraseniaHash());
            ps.setBoolean(7, usuario.isActivo());
            ps.setDate(8, java.sql.Date.valueOf(usuario.getFechaAlta()));

            // Parámetros específicos según el tipo de usuario
            if (usuario instanceof Administrador) {
                Administrador admin = (Administrador) usuario;
                ps.setString(1, "ADMINISTRADOR");
                ps.setString(9, admin.getLegajo());

                ps.setNull(10, java.sql.Types.VARCHAR);
                ps.setNull(11, java.sql.Types.INTEGER);
                ps.setNull(12, java.sql.Types.DECIMAL);

            } else if (usuario instanceof ProductorDeVenta) {
                ProductorDeVenta productor = (ProductorDeVenta) usuario;
                ps.setString(1, "PRODUCTOR_DE_VENTA");
                ps.setNull(9, java.sql.Types.VARCHAR);
                ps.setString(10, productor.getCuil());
                ps.setInt(11, productor.getCantidadVentas());
                ps.setBigDecimal(12, java.math.BigDecimal.valueOf(productor.getMontoVentas()));

            } else if (usuario instanceof Cliente) {
                Cliente cliente = (Cliente) usuario;
                ps.setString(1, "CLIENTE");
                ps.setNull(9, java.sql.Types.VARCHAR);
                ps.setString(10, cliente.getDni());
                ps.setNull(11, java.sql.Types.INTEGER);
                ps.setNull(12, java.sql.Types.DECIMAL);
            }

            // 2) Ejecutar y recuperar el ID generado antes de devolver true
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        usuario.setId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public boolean actualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nombre = ?, apellido = ?, telefono = ?, email = ?, pass_hash = ?, activo = ?, " +
                "legajo = ?, cuil = ?, cantidad_ventas = ?, monto_ventas = ? WHERE id_usuario = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getTelefono());
            ps.setString(4, usuario.getEmail());
            ps.setString(5, usuario.getContraseniaHash());
            ps.setBoolean(6, usuario.isActivo());

            if (usuario instanceof Administrador) {
                Administrador admin = (Administrador) usuario;
                ps.setString(7, admin.getLegajo());
                // null explicitos
                ps.setNull(8, java.sql.Types.VARCHAR);
                ps.setNull(9, java.sql.Types.INTEGER);
                ps.setNull(10, java.sql.Types.DECIMAL);

            } else if (usuario instanceof ProductorDeVenta) {
                ProductorDeVenta productor = (ProductorDeVenta) usuario;
                // null explicito
                ps.setNull(7, java.sql.Types.VARCHAR);
                //
                ps.setString(8, productor.getCuil());
                ps.setInt(9, productor.getCantidadVentas());
                ps.setBigDecimal(10, java.math.BigDecimal.valueOf(productor.getMontoVentas()));

            } else if (usuario instanceof Cliente) {
                Cliente cliente = (Cliente) usuario;
                //null explicito
                ps.setNull(7, java.sql.Types.VARCHAR);
                //
                ps.setString(8, cliente.getDni());
                // null explicitos
                ps.setNull(9, java.sql.Types.INTEGER);
                ps.setNull(10, java.sql.Types.DECIMAL);
            }

            ps.setInt(11, usuario.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean cambiarEstado(int idUsuario, boolean nuevoEstado) throws SQLException {
        String sql = "UPDATE usuarios SET activo = ? WHERE id_usuario = ?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, nuevoEstado);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }

    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    private List<ProductorDeVenta> ejecutarConsultaProductores(String sql) throws SQLException {
        List<ProductorDeVenta> productores = new ArrayList<>();

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                //mapeamos el Usuario
                Usuario usuario = mapear(rs);
                // como sabemos que estamos trayendo productores con la consulta casteamos y listamos ya casteado
                if (usuario instanceof ProductorDeVenta) {
                    productores.add((ProductorDeVenta) usuario);
                }
            }
        }
        return productores;

    }

    public List<ProductorDeVenta> listarProductoresPorMontoDesc() throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE tipo_usuario = 'PRODUCTOR_DE_VENTA' ORDER BY monto_ventas DESC";
        return ejecutarConsultaProductores(sql);
    }

    public List<ProductorDeVenta> listarProductoresPorCantidadDesc() throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE tipo_usuario = 'PRODUCTOR_DE_VENTA' ORDER BY cantidad_ventas DESC";
        return ejecutarConsultaProductores(sql);
    }


}