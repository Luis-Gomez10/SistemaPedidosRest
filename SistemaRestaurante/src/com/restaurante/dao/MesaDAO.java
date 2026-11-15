package com.restaurante.dao;

import com.restaurante.config.ConexionDB;
import com.restaurante.modelo.Mesa;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MesaDAO {
    public void registrar(Mesa mesa) throws SQLException {
        if (mesa == null) {
            throw new IllegalArgumentException("La mesa es obligatoria.");
        }
        String sql = "INSERT INTO Mesa (id_mesa, numero, capacidad, estado) VALUES (?, ?, ?, ?)";
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            ValidacionDatosDAO.validarCodigoNuevo(conexion, "Mesa", "id_mesa",
                    mesa.getIdMesa(), "mesa");
            try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                sentencia.setString(1, mesa.getIdMesa());
                sentencia.setString(2, mesa.getNumero());
                sentencia.setInt(3, mesa.getCapacidad());
                sentencia.setString(4, mesa.getEstado());
                sentencia.executeUpdate();
            } catch (SQLException ex) {
                throw ValidacionDatosDAO.traducirDuplicado(ex, "mesa", mesa.getIdMesa());
            }
        }
    }

    public void actualizarEstado(Mesa mesa) throws SQLException {
        if (mesa == null) {
            throw new IllegalArgumentException("La mesa es obligatoria.");
        }
        String sql = "UPDATE Mesa SET estado = ? WHERE id_mesa = ?";
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, mesa.getEstado());
            sentencia.setString(2, mesa.getIdMesa());
            if (sentencia.executeUpdate() == 0) {
                throw new SQLException("No existe la mesa " + mesa.getIdMesa() + ".");
            }
        }
    }

    public void actualizarMesa(Mesa mesa) throws SQLException {
        if (mesa == null) {
            throw new IllegalArgumentException("La mesa es obligatoria.");
        }
        if (mesa.getIdMesa() == null || mesa.getIdMesa().isBlank()
                || mesa.getNumero() == null || mesa.getNumero().isBlank()
                || mesa.getCapacidad() <= 0
                || mesa.getEstado() == null || mesa.getEstado().isBlank()) {
            throw new IllegalArgumentException("Completa todos los datos de la mesa "
                    + "y usa una capacidad mayor que cero.");
        }
        String sql = "UPDATE Mesa SET numero = ?, capacidad = ?, estado = ? "
                + "WHERE id_mesa = ?";
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, mesa.getNumero());
            sentencia.setInt(2, mesa.getCapacidad());
            sentencia.setString(3, mesa.getEstado());
            sentencia.setString(4, mesa.getIdMesa());
            if (sentencia.executeUpdate() == 0) {
                try (PreparedStatement comprobar = conexion.prepareStatement(
                        "SELECT 1 FROM Mesa WHERE id_mesa = ?")) {
                    comprobar.setString(1, mesa.getIdMesa());
                    try (ResultSet resultado = comprobar.executeQuery()) {
                        if (!resultado.next()) {
                            throw new SQLException("No existe la mesa "
                                    + mesa.getIdMesa() + ".");
                        }
                    }
                }
            }
        }
    }

    public List<Mesa> consultarMesas() throws SQLException {
        return consultar("SELECT id_mesa, numero, capacidad, estado FROM Mesa ORDER BY numero");
    }

    public Mesa buscarPorId(String idMesa) throws SQLException {
        if (idMesa == null || idMesa.isBlank()) {
            throw new IllegalArgumentException("El código de la mesa es obligatorio.");
        }
        String sql = "SELECT id_mesa, numero, capacidad, estado FROM Mesa WHERE id_mesa = ?";
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, idMesa);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next()
                        ? new Mesa(resultado.getString("id_mesa"),
                                resultado.getString("numero"), resultado.getInt("capacidad"),
                                resultado.getString("estado"))
                        : null;
            }
        }
    }

    public Mesa buscarPorCodigoONumero(String criterio) throws SQLException {
        if (criterio == null || criterio.isBlank()) {
            throw new IllegalArgumentException("Ingresa el código o número de la mesa.");
        }
        String sql = "SELECT id_mesa, numero, capacidad, estado FROM Mesa "
                + "WHERE id_mesa = ? OR numero = ? "
                + "ORDER BY CASE WHEN id_mesa = ? THEN 0 ELSE 1 END LIMIT 1";
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            String valor = criterio.trim();
            sentencia.setString(1, valor);
            sentencia.setString(2, valor);
            sentencia.setString(3, valor);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next()
                        ? new Mesa(resultado.getString("id_mesa"),
                                resultado.getString("numero"), resultado.getInt("capacidad"),
                                resultado.getString("estado"))
                        : null;
            }
        }
    }

    public List<Mesa> consultarMesasDisponibles() throws SQLException {
        return consultar("SELECT id_mesa, numero, capacidad, estado FROM Mesa "
                + "WHERE LOWER(estado) = 'disponible' ORDER BY numero");
    }

    public void eliminar(String idMesa) throws SQLException {
        if (idMesa == null || idMesa.isBlank()) {
            throw new IllegalArgumentException("El código de la mesa es obligatorio.");
        }
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            boolean autoCommitOriginal = conexion.getAutoCommit();
            conexion.setAutoCommit(false);
            try {
                try (PreparedStatement bloquearMesa = conexion.prepareStatement(
                        "SELECT id_mesa FROM Mesa WHERE id_mesa = ? FOR UPDATE")) {
                    bloquearMesa.setString(1, idMesa);
                    try (ResultSet resultado = bloquearMesa.executeQuery()) {
                        if (!resultado.next()) {
                            throw new SQLException("No existe la mesa " + idMesa + ".");
                        }
                    }
                }
                try (PreparedStatement activos = conexion.prepareStatement(
                        "SELECT COUNT(*) FROM Pedido WHERE id_mesa = ? "
                                + "AND UPPER(estado) NOT IN ('CERRADO', 'COMPLETADO')")) {
                    activos.setString(1, idMesa);
                    try (ResultSet resultado = activos.executeQuery()) {
                        resultado.next();
                        if (resultado.getLong(1) > 0) {
                            throw new IllegalStateException(
                                    "No se puede eliminar una mesa con pedidos activos.");
                        }
                    }
                }
                try (PreparedStatement eliminar = conexion.prepareStatement(
                        "DELETE FROM Mesa WHERE id_mesa = ?")) {
                    eliminar.setString(1, idMesa);
                    if (eliminar.executeUpdate() == 0) {
                        throw new SQLException("No existe la mesa " + idMesa + ".");
                    }
                }
                conexion.commit();
            } catch (SQLException | RuntimeException ex) {
                conexion.rollback();
                throw ex;
            } finally {
                conexion.setAutoCommit(autoCommitOriginal);
            }
        }
    }

    private List<Mesa> consultar(String sql) throws SQLException {
        List<Mesa> mesas = new ArrayList<>();
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                mesas.add(new Mesa(resultado.getString("id_mesa"),
                        resultado.getString("numero"), resultado.getInt("capacidad"),
                        resultado.getString("estado")));
            }
        }
        return mesas;
    }
}
