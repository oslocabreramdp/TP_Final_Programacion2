package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Enums.TipoUsuario;
import Modelo.Administrador;
import Modelo.Cliente;
import Modelo.ProductorDeVenta;
import config.Conexion;
import Modelo.Usuario;

public class UsuariosDAO {

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

        String sql = "SELECT * FROM productos WHERE id_usuario = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapear(rs);
            }

            return  null;

        }
    }



    public boolean insertar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (tipo_usuario, nombre, apellido, telefono, email, pass_hash, activo, fecha_alta, legajo, cuil, cantidad_ventas, monto_ventas) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getApellido());
            ps.setString(4, usuario.getTelefono()); // Es VARCHAR en la BD, se usa setString
            ps.setString(5, usuario.getEmail());
            ps.setString(6, usuario.getContraseniaHash());
            ps.setBoolean(7, usuario.isActivo());
            ps.setDate(8, java.sql.Date.valueOf(usuario.getFechaAlta()));

            if (usuario instanceof Administrador) {
                Administrador admin = (Administrador) usuario;

                ps.setString(1, "ADMINISTRADOR");
                ps.setString(9, admin.getLegajo());

                //NULL explícitos
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


                if (ps.executeUpdate() != 0) {
                    return true;
                }

            }
        }
        return false;
        }


    public boolean actualizar(Usuario usuario) throws SQLException {

        String sql = "UPDATE productos SET     nombre=?, apellido=?, telefono=?, email=?, pass_hash=?, activo=?, cuil=?, cantidad_ventas=?, monto_ventas=? WHERE id=?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getTelefono());
            ps.setString(4, usuario.getEmail());
            ps.setString(5, usuario.getContraseniaHash());
            ps.setBoolean(6, usuario.isActivo());

            if (usuario instanceof ProductorDeVenta) {
                ProductorDeVenta productor = (ProductorDeVenta) usuario;

                ps.setString(7, productor.getCuil());
                ps.setInt(8, productor.getCantidadVentas());
                ps.setBigDecimal(9, java.math.BigDecimal.valueOf(productor.getMontoVentas()));

            } else if (usuario instanceof Cliente) {
                Cliente cliente = (Cliente) usuario;

                ps.setString(7, cliente.getDni());
                ps.setNull(8, java.sql.Types.INTEGER);
                ps.setNull(9, java.sql.Types.DECIMAL);



                if (ps.executeUpdate() != 0) {
                    return true;
                }


            }
        }
        return false;
    }

/*    @Override
    public boolean eliminar(int id) throws SQLException {
        // TODO Ejercicio 7: DELETE del producto con el id indicado.
        //  ¡Con WHERE! Devolver true si executeUpdate() eliminó alguna fila.

        String sql = "DELETE FROM productos WHERE id = ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate()!=0) {
                return true;
            }
            return false;
        }
    }

    @Override
    public List<Producto> buscarPorNombre(String texto) throws SQLException {
        // TODO Opcional A: SELECT de los productos cuyo nombre contiene el texto (LIKE).

        List<Producto> p = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE nombre LIKE ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
        ) {

            ps.setString(1, "%"+texto+"%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                p.add(mapear(rs));
            }

        }

        return p;
    }


    public List<Producto> listarStockBajo(int minimo) throws SQLException {
        // TODO Opcional B: SELECT de los productos con stock menor al mínimo.

        List<Producto> p = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE stock BETWEEN 0 AND ?";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, minimo);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                p.add(mapear(rs));
            }

            return p;
        }
    }

*/
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
                    String dniCliente = rs.getString("cuil"); // Usando la columna cuil para DNI/CUIL
                    return new Cliente(idUsuario, nombre, apellido, telefono, email, passHash, activo, fechaAlta, dniCliente);

                default:
                    throw new SQLException("Tipo de usuario desconocido en la base de datos: " + tipoStr);
            }
        }
    }
