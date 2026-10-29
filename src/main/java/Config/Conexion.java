package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class    Conexion {

    // TODO completá los datos de conexión de TU instalación de MySQL.
    //  - URL: "jdbc:mysql://servidor:puerto/base" (servidor localhost, puerto 3306, base inmobiliaria)
    //  - USUARIO y CLAVE: los que usás para entrar a MySQL Workbench
    private static final String URL = "jdbc:mysql://localhost:3306/inmobiliaria";
    private static final String USUARIO = "root";
    private static final String CLAVE = "MySQL+17082016";

    public static Connection obtenerConexion() throws SQLException {

        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
}
