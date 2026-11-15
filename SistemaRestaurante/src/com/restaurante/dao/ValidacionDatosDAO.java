package com.restaurante.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

final class ValidacionDatosDAO {
    private ValidacionDatosDAO() {
    }

    static void validarCodigoNuevo(Connection conexion, String tabla, String columna,
            String codigo, String entidad) throws SQLException {
        String sql = "SELECT 1 FROM " + tabla + " WHERE " + columna + " = ?";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, codigo);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    throw new IllegalArgumentException(
                            "El código de " + entidad + " ya está registrado: " + codigo + ".");
                }
            }
        }
    }

    static SQLException traducirDuplicado(SQLException ex, String entidad, String codigo) {
        if (ex.getErrorCode() == 1062) {
            return new SQLException("El código de " + entidad + " ya está registrado: "
                    + codigo + ".", ex.getSQLState(), ex.getErrorCode(), ex);
        }
        return ex;
    }
}
