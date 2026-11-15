package com.restaurante.dao;

import com.restaurante.config.ConexionDB;
import com.restaurante.modelo.DetallePedido;
import com.restaurante.modelo.Pedido;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetallePedidoDAO {
    public List<DetalleResumen> consultarPorPedido(String idPedido) throws SQLException {
        if (idPedido == null || idPedido.isBlank()) {
            throw new IllegalArgumentException("El código del pedido es obligatorio.");
        }
        String sql = "SELECT d.id_detalle, d.id_producto, p.nombre, d.cantidad, "
                + "d.precio_aplicado, d.cantidad * d.precio_aplicado AS subtotal "
                + "FROM DetallePedido d JOIN Producto p ON p.id_producto = d.id_producto "
                + "WHERE d.id_pedido = ? ORDER BY d.id_detalle";
        List<DetalleResumen> detalles = new ArrayList<>();
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, idPedido);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    detalles.add(new DetalleResumen(resultado.getInt("id_detalle"),
                            resultado.getString("id_producto"), resultado.getString("nombre"),
                            resultado.getInt("cantidad"), resultado.getBigDecimal("precio_aplicado"),
                            resultado.getBigDecimal("subtotal")));
                }
            }
        }
        return detalles;
    }

    public void agregar(Pedido pedido, DetallePedido detalle) throws SQLException {
        if (pedido == null || detalle == null) {
            throw new IllegalArgumentException("El pedido y el detalle son obligatorios.");
        }
        String idPedido = pedido.getIdPedido();
        enTransaccion(conexion -> {
            validarPedidoEditable(conexion, idPedido);
            validarProductoDisponible(conexion, detalle.getProducto().getIdProducto());
            String sql = "INSERT INTO DetallePedido "
                    + "(id_pedido, id_producto, cantidad, precio_aplicado) VALUES (?, ?, ?, ?)";
            try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                sentencia.setString(1, idPedido);
                sentencia.setString(2, detalle.getProducto().getIdProducto());
                sentencia.setInt(3, detalle.getCantidad());
                sentencia.setBigDecimal(4, detalle.getPrecioAplicadoDecimal());
                sentencia.executeUpdate();
            }
            actualizarTotal(conexion, idPedido);
        });
        pedido.calcularTotal();
    }

    public void actualizarCantidad(int idDetalle, Pedido pedido, int cantidad)
            throws SQLException {
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido es obligatorio.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        String idPedido = pedido.getIdPedido();
        enTransaccion(conexion -> {
            validarPedidoEditable(conexion, idPedido);
            try (PreparedStatement sentencia = conexion.prepareStatement(
                    "UPDATE DetallePedido SET cantidad = ? "
                            + "WHERE id_detalle = ? AND id_pedido = ?")) {
                sentencia.setInt(1, cantidad);
                sentencia.setInt(2, idDetalle);
                sentencia.setString(3, idPedido);
                if (sentencia.executeUpdate() == 0) {
                    throw new SQLException("No existe el detalle indicado para ese pedido.");
                }
            }
            actualizarTotal(conexion, idPedido);
        });
        pedido.calcularTotal();
    }

    public void eliminar(int idDetalle, Pedido pedido) throws SQLException {
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido es obligatorio.");
        }
        String idPedido = pedido.getIdPedido();
        enTransaccion(conexion -> {
            validarPedidoEditable(conexion, idPedido);
            try (PreparedStatement sentencia = conexion.prepareStatement(
                    "DELETE FROM DetallePedido WHERE id_detalle = ? AND id_pedido = ?")) {
                sentencia.setInt(1, idDetalle);
                sentencia.setString(2, idPedido);
                if (sentencia.executeUpdate() == 0) {
                    throw new SQLException("No existe el detalle indicado para ese pedido.");
                }
            }
            actualizarTotal(conexion, idPedido);
        });
        pedido.calcularTotal();
    }

    private static void validarPedidoEditable(Connection conexion, String idPedido)
            throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(
                "SELECT estado FROM Pedido WHERE id_pedido = ? FOR UPDATE")) {
            sentencia.setString(1, idPedido);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (!resultado.next()) {
                    throw new SQLException("No existe el pedido " + idPedido + ".");
                }
                String estado = resultado.getString("estado");
                if ("CERRADO".equalsIgnoreCase(estado)
                        || "COMPLETADO".equalsIgnoreCase(estado)) {
                    throw new IllegalStateException(
                            "No se puede modificar un pedido cerrado o completado.");
                }
            }
        }
    }

    private static void validarProductoDisponible(Connection conexion, String idProducto)
            throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(
                "SELECT disponible FROM Producto WHERE id_producto = ? FOR UPDATE")) {
            sentencia.setString(1, idProducto);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (!resultado.next()) {
                    throw new SQLException("No existe el producto " + idProducto + ".");
                }
                if (!resultado.getBoolean("disponible")) {
                    throw new IllegalArgumentException("El producto no está disponible.");
                }
            }
        }
    }

    private static void actualizarTotal(Connection conexion, String idPedido)
            throws SQLException {
        String sql = "UPDATE Pedido SET total = ? WHERE id_pedido = ?";
        BigDecimal total;
        try (PreparedStatement suma = conexion.prepareStatement(
                "SELECT COALESCE(SUM(cantidad * precio_aplicado), 0.00) "
                        + "FROM DetallePedido WHERE id_pedido = ?")) {
            suma.setString(1, idPedido);
            try (ResultSet resultado = suma.executeQuery()) {
                resultado.next();
                total = resultado.getBigDecimal(1)
                        .setScale(2, java.math.RoundingMode.HALF_UP);
            }
        }
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setBigDecimal(1, total);
            sentencia.setString(2, idPedido);
            if (sentencia.executeUpdate() == 0) {
                throw new SQLException("No existe el pedido " + idPedido + ".");
            }
        }
    }

    private static void enTransaccion(OperacionSQL operacion) throws SQLException {
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            boolean autoCommitOriginal = conexion.getAutoCommit();
            conexion.setAutoCommit(false);
            try {
                operacion.ejecutar(conexion);
                conexion.commit();
            } catch (SQLException | RuntimeException ex) {
                conexion.rollback();
                throw ex;
            } finally {
                conexion.setAutoCommit(autoCommitOriginal);
            }
        }
    }

    @FunctionalInterface
    private interface OperacionSQL {
        void ejecutar(Connection conexion) throws SQLException;
    }

    public record DetalleResumen(int idDetalle, String idProducto, String nombreProducto,
            int cantidad, BigDecimal precioAplicado, BigDecimal subtotal) {
    }
}
