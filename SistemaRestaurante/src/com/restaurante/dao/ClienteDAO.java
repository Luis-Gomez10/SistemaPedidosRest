package com.restaurante.dao;

import com.restaurante.config.ConexionDB;
import com.restaurante.modelo.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    public void registrar(Cliente cliente) throws SQLException {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente es obligatorio.");
        }
        String sql = "INSERT INTO Cliente (id_cliente, nombre, telefono, activo) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            ValidacionDatosDAO.validarCodigoNuevo(conexion, "Cliente", "id_cliente",
                    cliente.getIdCliente(), "cliente");
            try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                sentencia.setString(1, cliente.getIdCliente());
                sentencia.setString(2, cliente.getNombre());
                sentencia.setString(3, cliente.getTelefono());
                sentencia.setBoolean(4, cliente.isActivo());
                sentencia.executeUpdate();
            } catch (SQLException ex) {
                throw ValidacionDatosDAO.traducirDuplicado(ex, "cliente",
                        cliente.getIdCliente());
            }
        }
    }

    public Cliente buscarPorId(String idCliente) throws SQLException {
        if (idCliente == null || idCliente.isBlank()) {
            throw new IllegalArgumentException("El código del cliente es obligatorio.");
        }
        String sql = "SELECT id_cliente, nombre, telefono, activo "
                + "FROM Cliente WHERE id_cliente = ?";
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, idCliente);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next()
                        ? new Cliente(resultado.getString("id_cliente"),
                                resultado.getString("nombre"), resultado.getString("telefono"),
                                resultado.getBoolean("activo"))
                        : null;
            }
        }
    }

    public List<Cliente> listarTodos() throws SQLException {
        String sql = "SELECT id_cliente, nombre, telefono, activo FROM Cliente "
                + "ORDER BY nombre, id_cliente";
        List<Cliente> clientes = new ArrayList<>();
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                clientes.add(mapearCliente(resultado));
            }
        }
        return clientes;
    }

    public List<Cliente> buscar(String filtro) throws SQLException {
        if (filtro == null || filtro.isBlank()) {
            return listarTodos();
        }
        String sql = "SELECT id_cliente, nombre, telefono, activo FROM Cliente "
                + "WHERE id_cliente LIKE ? OR nombre LIKE ? OR telefono LIKE ? "
                + "ORDER BY nombre, id_cliente";
        List<Cliente> clientes = new ArrayList<>();
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            String patron = "%" + filtro.trim() + "%";
            sentencia.setString(1, patron);
            sentencia.setString(2, patron);
            sentencia.setString(3, patron);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    clientes.add(mapearCliente(resultado));
                }
            }
        }
        return clientes;
    }

    public void actualizar(String idOriginal, Cliente cliente) throws SQLException {
        if (idOriginal == null || idOriginal.isBlank() || cliente == null) {
            throw new IllegalArgumentException("El código original y el cliente son obligatorios.");
        }
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            if (!idOriginal.equals(cliente.getIdCliente())) {
                try (PreparedStatement asociados = conexion.prepareStatement(
                        "SELECT COUNT(*) FROM Pedido WHERE id_cliente = ?")) {
                    asociados.setString(1, idOriginal);
                    try (ResultSet resultado = asociados.executeQuery()) {
                        resultado.next();
                        if (resultado.getLong(1) > 0) {
                            throw new IllegalStateException(
                                    "No se puede cambiar el código de un cliente con pedidos registrados.");
                        }
                    }
                }
                validarCodigoNoAsignado(conexion, cliente.getIdCliente(), idOriginal);
            }
            String sql = "UPDATE Cliente SET id_cliente = ?, nombre = ?, telefono = ?, "
                    + "activo = ? WHERE id_cliente = ?";
            try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                sentencia.setString(1, cliente.getIdCliente());
                sentencia.setString(2, cliente.getNombre());
                sentencia.setString(3, cliente.getTelefono());
                sentencia.setBoolean(4, cliente.isActivo());
                sentencia.setString(5, idOriginal);
                if (sentencia.executeUpdate() == 0) {
                    throw new SQLException("No existe el cliente " + idOriginal + ".");
                }
            } catch (SQLException ex) {
                throw ValidacionDatosDAO.traducirDuplicado(ex, "cliente",
                        cliente.getIdCliente());
            }
        }
    }

    public void eliminar(String idCliente) throws SQLException {
        if (idCliente == null || idCliente.isBlank()) {
            throw new IllegalArgumentException("El código del cliente es obligatorio.");
        }
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            boolean autoCommitOriginal = conexion.getAutoCommit();
            conexion.setAutoCommit(false);
            try {
                try (PreparedStatement bloquear = conexion.prepareStatement(
                        "SELECT id_cliente FROM Cliente WHERE id_cliente = ? FOR UPDATE")) {
                    bloquear.setString(1, idCliente);
                    try (ResultSet resultado = bloquear.executeQuery()) {
                        if (!resultado.next()) {
                            throw new SQLException("No existe el cliente " + idCliente + ".");
                        }
                    }
                }
                try (PreparedStatement pedidos = conexion.prepareStatement(
                        "SELECT COUNT(*) FROM Pedido WHERE id_cliente = ?")) {
                    pedidos.setString(1, idCliente);
                    try (ResultSet resultado = pedidos.executeQuery()) {
                        resultado.next();
                        if (resultado.getLong(1) > 0) {
                            throw new IllegalStateException(
                                    "No se puede eliminar un cliente con pedidos registrados.");
                        }
                    }
                }
                try (PreparedStatement eliminar = conexion.prepareStatement(
                        "DELETE FROM Cliente WHERE id_cliente = ?")) {
                    eliminar.setString(1, idCliente);
                    if (eliminar.executeUpdate() == 0) {
                        throw new SQLException("No existe el cliente " + idCliente + ".");
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

    private static void validarCodigoNoAsignado(Connection conexion, String idCliente,
            String idOriginal) throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(
                "SELECT 1 FROM Cliente WHERE id_cliente = ? AND id_cliente <> ?")) {
            sentencia.setString(1, idCliente);
            sentencia.setString(2, idOriginal);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    throw new IllegalArgumentException(
                            "El código de cliente ya está registrado: " + idCliente + ".");
                }
            }
        }
    }

    private static Cliente mapearCliente(ResultSet resultado) throws SQLException {
        return new Cliente(resultado.getString("id_cliente"),
                resultado.getString("nombre"), resultado.getString("telefono"),
                resultado.getBoolean("activo"));
    }
}
