package com.restaurante.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionDB {
    private static final String URL = valorEnv("DB_URL",
            "jdbc:mysql://localhost:3306/sistema_restaurante?useSSL=false&serverTimezone=UTC");
    private static final String USUARIO = valorEnv("DB_USER", "root");
    private static final String CONTRASENA = valorEnv("DB_PASSWORD", "");

    private ConexionDB() {
    }

    public static Connection obtenerConexion() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new SQLException("No se encontró el driver com.mysql.cj.jdbc.Driver. "
                    + "Agrega mysql-connector-j-8.0.33.jar al classpath.", ex);
        }
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }

    private static String valorEnv(String nombre, String predeterminado) {
        String valor = System.getenv(nombre);
        return valor == null ? predeterminado : valor;
    }
}
