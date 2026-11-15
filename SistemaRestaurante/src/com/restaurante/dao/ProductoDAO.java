package com.restaurante.dao;

import com.restaurante.config.ConexionDB;
import com.restaurante.modelo.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    private static final String COLUMNAS_PRODUCTO = "id_producto, nombre, categoria, "
            + "descripcion, precio_unitario, disponible";

    public void registrar(Producto producto) throws SQLException {
        if (producto == null) {
            throw new IllegalArgumentException("El producto es obligatorio.");
        }
        String sql = "INSERT INTO Producto (id_producto, nombre, categoria, descripcion, "
                + "precio_unitario, disponible) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            ValidacionDatosDAO.validarCodigoNuevo(conexion, "Producto", "id_producto",
                    producto.getIdProducto(), "producto");
            try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                asignarProducto(sentencia, producto);
                sentencia.executeUpdate();
            } catch (SQLException ex) {
                throw ValidacionDatosDAO.traducirDuplicado(ex, "producto",
                        producto.getIdProducto());
            }
        }
    }

    public Producto buscarPorId(String idProducto) throws SQLException {
        if (idProducto == null || idProducto.isBlank()) {
            throw new IllegalArgumentException("El código del producto es obligatorio.");
        }
        String sql = "SELECT " + COLUMNAS_PRODUCTO + " FROM Producto WHERE id_producto = ?";
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, idProducto);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? mapearProducto(resultado) : null;
            }
        }
    }

    public List<Producto> listarTodos() throws SQLException {
        return consultar("SELECT " + COLUMNAS_PRODUCTO + " FROM Producto "
                + "ORDER BY nombre, id_producto");
    }

    public List<Producto> listarDisponibles() throws SQLException {
        return consultar("SELECT " + COLUMNAS_PRODUCTO + " FROM Producto "
                + "WHERE disponible = 1 ORDER BY nombre, id_producto");
    }

    public List<Producto> buscar(String filtro) throws SQLException {
        if (filtro == null || filtro.isBlank()) {
            return listarTodos();
        }
        String sql = "SELECT " + COLUMNAS_PRODUCTO + " FROM Producto "
                + "WHERE id_producto LIKE ? OR nombre LIKE ? OR categoria LIKE ? "
                + "OR descripcion LIKE ? ORDER BY nombre, id_producto";
        String patron = "%" + filtro.trim() + "%";
        List<Producto> productos = new ArrayList<>();
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            for (int indice = 1; indice <= 4; indice++) {
                sentencia.setString(indice, patron);
            }
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    productos.add(mapearProducto(resultado));
                }
            }
        }
        return productos;
    }

    public void actualizar(String idOriginal, Producto producto) throws SQLException {
        if (idOriginal == null || idOriginal.isBlank() || producto == null) {
            throw new IllegalArgumentException(
                    "El código original y el producto son obligatorios.");
        }
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            boolean autoCommitOriginal = conexion.getAutoCommit();
            conexion.setAutoCommit(false);
            try {
                try (PreparedStatement bloquear = conexion.prepareStatement(
                        "SELECT id_producto FROM Producto WHERE id_producto = ? FOR UPDATE")) {
                    bloquear.setString(1, idOriginal);
                    try (ResultSet resultado = bloquear.executeQuery()) {
                        if (!resultado.next()) {
                            throw new SQLException("No existe el producto " + idOriginal + ".");
                        }
                    }
                }
                if (!idOriginal.equals(producto.getIdProducto())) {
                    try (PreparedStatement detalles = conexion.prepareStatement(
                            "SELECT COUNT(*) FROM DetallePedido WHERE id_producto = ?")) {
                        detalles.setString(1, idOriginal);
                        try (ResultSet resultado = detalles.executeQuery()) {
                            resultado.next();
                            if (resultado.getLong(1) > 0) {
                                throw new IllegalStateException("No se puede cambiar el código "
                                        + "de un producto asociado a detalles de pedido.");
                            }
                        }
                    }
                    validarCodigoNoAsignado(conexion, producto.getIdProducto(), idOriginal);
                }
                String sql = "UPDATE Producto SET id_producto = ?, nombre = ?, categoria = ?, "
                        + "descripcion = ?, precio_unitario = ?, disponible = ? "
                        + "WHERE id_producto = ?";
                try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                    sentencia.setString(1, producto.getIdProducto());
                    sentencia.setString(2, producto.getNombre());
                    sentencia.setString(3, producto.getCategoria());
                    sentencia.setString(4, producto.getDescripcion());
                    sentencia.setBigDecimal(5, producto.getPrecioUnitarioDecimal());
                    sentencia.setBoolean(6, producto.isDisponible());
                    sentencia.setString(7, idOriginal);
                    if (sentencia.executeUpdate() == 0) {
                        throw new SQLException("No existe el producto " + idOriginal + ".");
                    }
                } catch (SQLException ex) {
                    throw ValidacionDatosDAO.traducirDuplicado(ex, "producto",
                            producto.getIdProducto());
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

    public void eliminar(String idProducto) throws SQLException {
        if (idProducto == null || idProducto.isBlank()) {
            throw new IllegalArgumentException("El código del producto es obligatorio.");
        }
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            boolean autoCommitOriginal = conexion.getAutoCommit();
            conexion.setAutoCommit(false);
            try {
                try (PreparedStatement bloquear = conexion.prepareStatement(
                        "SELECT id_producto FROM Producto WHERE id_producto = ? FOR UPDATE")) {
                    bloquear.setString(1, idProducto);
                    try (ResultSet resultado = bloquear.executeQuery()) {
                        if (!resultado.next()) {
                            throw new SQLException("No existe el producto " + idProducto + ".");
                        }
                    }
                }
                try (PreparedStatement detalles = conexion.prepareStatement(
                        "SELECT COUNT(*) FROM DetallePedido WHERE id_producto = ?")) {
                    detalles.setString(1, idProducto);
                    try (ResultSet resultado = detalles.executeQuery()) {
                        resultado.next();
                        if (resultado.getLong(1) > 0) {
                            throw new IllegalStateException(
                                    "No se puede eliminar un producto con detalles de pedido.");
                        }
                    }
                }
                try (PreparedStatement eliminar = conexion.prepareStatement(
                        "DELETE FROM Producto WHERE id_producto = ?")) {
                    eliminar.setString(1, idProducto);
                    if (eliminar.executeUpdate() == 0) {
                        throw new SQLException("No existe el producto " + idProducto + ".");
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

    private static List<Producto> consultar(String sql) throws SQLException {
        List<Producto> productos = new ArrayList<>();
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                productos.add(mapearProducto(resultado));
            }
        }
        return productos;
    }

    private static void asignarProducto(PreparedStatement sentencia, Producto producto)
            throws SQLException {
        sentencia.setString(1, producto.getIdProducto());
        sentencia.setString(2, producto.getNombre());
        sentencia.setString(3, producto.getCategoria());
        sentencia.setString(4, producto.getDescripcion());
        sentencia.setBigDecimal(5, producto.getPrecioUnitarioDecimal());
        sentencia.setBoolean(6, producto.isDisponible());
    }

    private static void validarCodigoNoAsignado(Connection conexion, String idProducto,
            String idOriginal) throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(
                "SELECT 1 FROM Producto WHERE id_producto = ? AND id_producto <> ?")) {
            sentencia.setString(1, idProducto);
            sentencia.setString(2, idOriginal);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    throw new IllegalArgumentException(
                            "El código de producto ya está registrado: " + idProducto + ".");
                }
            }
        }
    }

    private static Producto mapearProducto(ResultSet resultado) throws SQLException {
        return new Producto(resultado.getString("id_producto"),
                resultado.getString("nombre"), resultado.getString("categoria"),
                resultado.getString("descripcion"), resultado.getBigDecimal("precio_unitario"),
                resultado.getBoolean("disponible"));
    }
}
