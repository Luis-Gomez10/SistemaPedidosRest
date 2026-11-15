package com.restaurante.dao;

import com.restaurante.config.ConexionDB;
import com.restaurante.modelo.Cliente;
import com.restaurante.modelo.Mesa;
import com.restaurante.modelo.Pedido;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PedidoDAO {
    private static final String CONSULTA_PEDIDOS = "SELECT p.id_pedido, p.fecha, p.estado, "
            + "p.total, c.id_cliente, c.nombre AS nombre_cliente, c.telefono, c.activo, "
            + "m.id_mesa, m.numero, m.capacidad, m.estado AS estado_mesa "
            + "FROM Pedido p LEFT JOIN Cliente c ON p.id_cliente = c.id_cliente "
            + "LEFT JOIN Mesa m ON p.id_mesa = m.id_mesa ";

    public void crear(Pedido pedido) throws SQLException {
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido es obligatorio.");
        }
        if (esEstadoFinal(pedido.getEstado())) {
            throw new IllegalStateException(
                    "Un pedido debe tener detalles antes de cerrarse o completarse.");
        }
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            boolean autoCommitOriginal = conexion.getAutoCommit();
            conexion.setAutoCommit(false);
            try {
                ValidacionDatosDAO.validarCodigoNuevo(conexion, "Pedido", "id_pedido",
                        pedido.getIdPedido(), "pedido");
                validarCliente(conexion, pedido.getCliente());
                validarMesaDisponible(conexion, pedido.getMesa());
                String sql = "INSERT INTO Pedido "
                        + "(id_pedido, fecha, estado, total, id_cliente, id_mesa) "
                        + "VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                    sentencia.setString(1, pedido.getIdPedido());
                    sentencia.setTimestamp(2, new Timestamp(pedido.getFecha().getTime()));
                    sentencia.setString(3, pedido.getEstado());
                    sentencia.setBigDecimal(4, BigDecimal.ZERO.setScale(2));
                    if (pedido.getCliente() == null) {
                        sentencia.setNull(5, java.sql.Types.VARCHAR);
                    } else {
                        sentencia.setString(5, pedido.getCliente().getIdCliente());
                    }
                    if (pedido.getMesa() == null) {
                        sentencia.setNull(6, java.sql.Types.VARCHAR);
                    } else {
                        sentencia.setString(6, pedido.getMesa().getIdMesa());
                    }
                    sentencia.executeUpdate();
                } catch (SQLException ex) {
                    throw ValidacionDatosDAO.traducirDuplicado(ex, "pedido",
                            pedido.getIdPedido());
                }
                if (pedido.getMesa() != null) {
                    try (PreparedStatement ocupar = conexion.prepareStatement(
                            "UPDATE Mesa SET estado = 'Ocupada' WHERE id_mesa = ?")) {
                        ocupar.setString(1, pedido.getMesa().getIdMesa());
                        if (ocupar.executeUpdate() == 0) {
                            throw new SQLException("No existe la mesa "
                                    + pedido.getMesa().getIdMesa() + ".");
                        }
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

    public BigDecimal recalcularTotal(String idPedido) throws SQLException {
        validarIdPedido(idPedido);
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            boolean autoCommitOriginal = conexion.getAutoCommit();
            conexion.setAutoCommit(false);
            try {
                try (PreparedStatement bloqueo = conexion.prepareStatement(
                        "SELECT id_pedido FROM Pedido WHERE id_pedido = ? FOR UPDATE")) {
                    bloqueo.setString(1, idPedido);
                    try (ResultSet resultado = bloqueo.executeQuery()) {
                        if (!resultado.next()) {
                            throw new SQLException("No existe el pedido " + idPedido + ".");
                        }
                    }
                }
                BigDecimal total = sumarDetalles(conexion, idPedido);
                try (PreparedStatement sentencia = conexion.prepareStatement(
                        "UPDATE Pedido SET total = ? WHERE id_pedido = ?")) {
                    sentencia.setBigDecimal(1, total);
                    sentencia.setString(2, idPedido);
                    if (sentencia.executeUpdate() == 0) {
                        throw new SQLException("No existe el pedido " + idPedido + ".");
                    }
                }
                conexion.commit();
                return total;
            } catch (SQLException | RuntimeException ex) {
                conexion.rollback();
                throw ex;
            } finally {
                conexion.setAutoCommit(autoCommitOriginal);
            }
        }
    }

    public boolean verificarYCorregirTotal(String idPedido) throws SQLException {
        validarIdPedido(idPedido);
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            boolean autoCommitOriginal = conexion.getAutoCommit();
            conexion.setAutoCommit(false);
            try {
                BigDecimal totalPersistido;
                try (PreparedStatement sentencia = conexion.prepareStatement(
                        "SELECT total FROM Pedido WHERE id_pedido = ? FOR UPDATE")) {
                    sentencia.setString(1, idPedido);
                    try (ResultSet resultado = sentencia.executeQuery()) {
                        if (!resultado.next()) {
                            throw new SQLException("No existe el pedido " + idPedido + ".");
                        }
                        totalPersistido = resultado.getBigDecimal("total")
                                .setScale(2, RoundingMode.HALF_UP);
                    }
                }
                BigDecimal totalDetalle = sumarDetalles(conexion, idPedido);
                boolean consistente = totalPersistido.compareTo(totalDetalle) == 0;
                if (!consistente) {
                    try (PreparedStatement sentencia = conexion.prepareStatement(
                            "UPDATE Pedido SET total = ? WHERE id_pedido = ?")) {
                        sentencia.setBigDecimal(1, totalDetalle);
                        sentencia.setString(2, idPedido);
                        sentencia.executeUpdate();
                    }
                }
                conexion.commit();
                return consistente;
            } catch (SQLException | RuntimeException ex) {
                conexion.rollback();
                throw ex;
            } finally {
                conexion.setAutoCommit(autoCommitOriginal);
            }
        }
    }

    public Pedido consultarPorId(String idPedido) throws SQLException {
        validarIdPedido(idPedido);
        List<Pedido> pedidos = consultarSimple(CONSULTA_PEDIDOS
                + "WHERE p.id_pedido = ?", idPedido);
        return pedidos.isEmpty() ? null : pedidos.get(0);
    }

    public List<Pedido> consultarPorMesa(String idMesa) throws SQLException {
        if (idMesa == null || idMesa.isBlank()) {
            throw new IllegalArgumentException("El código de la mesa es obligatorio.");
        }
        String sql = CONSULTA_PEDIDOS
                + "WHERE p.id_mesa = ? OR m.numero = ? ORDER BY p.fecha DESC";
        List<Pedido> pedidos = new ArrayList<>();
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, idMesa.trim());
            sentencia.setString(2, idMesa.trim());
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    pedidos.add(mapearPedido(resultado));
                }
            }
        }
        return pedidos;
    }

    public List<Pedido> consultarPorEstado(String estado) throws SQLException {
        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException("El estado del pedido es obligatorio.");
        }
        return consultarSimple(CONSULTA_PEDIDOS
                + "WHERE UPPER(p.estado) = UPPER(?) ORDER BY p.fecha DESC", estado.trim());
    }

    public List<Pedido> consultarPedidosActivos() throws SQLException {
        String sql = CONSULTA_PEDIDOS
                + "WHERE UPPER(TRIM(p.estado)) NOT IN ('CERRADO', 'COMPLETADO') "
                + "ORDER BY p.fecha DESC";
        List<Pedido> pedidos = new ArrayList<>();
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                pedidos.add(mapearPedido(resultado));
            }
        }
        return pedidos;
    }

    public List<Pedido> consultarPorFecha(Date desde, Date hasta) throws SQLException {
        validarRangoFechas(desde, hasta);
        return consultarRango(CONSULTA_PEDIDOS + "WHERE (? IS NULL OR p.fecha >= ?) "
                + "AND (? IS NULL OR p.fecha < ?) ORDER BY p.fecha DESC",
                null, desde, hasta, false);
    }

    public List<Pedido> consultarPorEstadoYFecha(String estado, Date desde, Date hasta)
            throws SQLException {
        if (estado != null && estado.isBlank()) {
            estado = null;
        }
        validarRangoFechas(desde, hasta);
        return consultarRango(CONSULTA_PEDIDOS + "WHERE (? IS NULL OR p.estado = ?) "
                + "AND (? IS NULL OR p.fecha >= ?) AND (? IS NULL OR p.fecha < ?) "
                + "ORDER BY p.fecha DESC", estado, desde, hasta, true);
    }

    public ResumenPedidos obtenerResumen() throws SQLException {
        String sql = "SELECT COUNT(*) AS cantidad, "
                + "COALESCE(SUM(total), 0.00) AS importe FROM Pedido";
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {
            resultado.next();
            return new ResumenPedidos(resultado.getLong("cantidad"),
                    resultado.getBigDecimal("importe").setScale(2, RoundingMode.HALF_UP));
        }
    }

    public List<VentaPorPedido> reporteVentasPorPedido(Date desde, Date hasta)
            throws SQLException {
        validarRangoFechas(desde, hasta);
        String sql = "SELECT id_pedido, fecha, total FROM Pedido "
                + "WHERE estado IN ('CERRADO', 'COMPLETADO') "
                + "AND (? IS NULL OR fecha >= ?) AND (? IS NULL OR fecha < ?) "
                + "ORDER BY fecha DESC";
        List<VentaPorPedido> reporte = new ArrayList<>();
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            setFecha(sentencia, 1, 2, desde);
            setFecha(sentencia, 3, 4, hasta);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    reporte.add(new VentaPorPedido(resultado.getString("id_pedido"),
                            resultado.getTimestamp("fecha"),
                            resultado.getBigDecimal("total")));
                }
            }
        }
        return reporte;
    }

    public void cerrarPedido(String idPedido, String estadoFinal) throws SQLException {
        validarIdPedido(idPedido);
        if (!"CERRADO".equalsIgnoreCase(estadoFinal)
                && !"COMPLETADO".equalsIgnoreCase(estadoFinal)) {
            throw new IllegalArgumentException(
                    "El estado final debe ser CERRADO o COMPLETADO.");
        }
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            boolean autoCommitOriginal = conexion.getAutoCommit();
            conexion.setAutoCommit(false);
            try {
                String mesaDelPedido = null;
                try (PreparedStatement bloqueo = conexion.prepareStatement(
                        "SELECT estado, id_mesa FROM Pedido WHERE id_pedido = ? FOR UPDATE")) {
                    bloqueo.setString(1, idPedido);
                    try (ResultSet resultado = bloqueo.executeQuery()) {
                        if (!resultado.next()) {
                            throw new SQLException("No existe el pedido " + idPedido + ".");
                        }
                        if (esEstadoFinal(resultado.getString("estado"))) {
                            throw new IllegalStateException(
                                    "El pedido ya está cerrado o completado.");
                        }
                        mesaDelPedido = resultado.getString("id_mesa");
                    }
                }
                long cantidadDetalles;
                try (PreparedStatement sentencia = conexion.prepareStatement(
                        "SELECT COUNT(*) FROM DetallePedido WHERE id_pedido = ?")) {
                    sentencia.setString(1, idPedido);
                    try (ResultSet resultado = sentencia.executeQuery()) {
                        resultado.next();
                        cantidadDetalles = resultado.getLong(1);
                    }
                }
                if (cantidadDetalles == 0) {
                    throw new IllegalStateException(
                            "No se puede cerrar un pedido sin productos.");
                }
                BigDecimal total = sumarDetalles(conexion, idPedido);
                try (PreparedStatement sentencia = conexion.prepareStatement(
                        "UPDATE Pedido SET total = ?, estado = ? WHERE id_pedido = ?")) {
                    sentencia.setBigDecimal(1, total);
                    sentencia.setString(2, estadoFinal.toUpperCase(java.util.Locale.ROOT));
                    sentencia.setString(3, idPedido);
                    sentencia.executeUpdate();
                }
                if (mesaDelPedido != null) {
                    try (PreparedStatement activos = conexion.prepareStatement(
                            "SELECT COUNT(*) FROM Pedido WHERE id_mesa = ? "
                                    + "AND UPPER(estado) NOT IN ('CERRADO', 'COMPLETADO')")) {
                        activos.setString(1, mesaDelPedido);
                        try (ResultSet resultado = activos.executeQuery()) {
                            resultado.next();
                            if (resultado.getLong(1) == 0) {
                                try (PreparedStatement liberar = conexion.prepareStatement(
                                        "UPDATE Mesa SET estado = 'Disponible' WHERE id_mesa = ?")) {
                                    liberar.setString(1, mesaDelPedido);
                                    liberar.executeUpdate();
                                }
                            }
                        }
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

    public void actualizarEstado(String idPedido, String nuevoEstado) throws SQLException {
        validarIdPedido(idPedido);
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            throw new IllegalArgumentException("El nuevo estado del pedido es obligatorio.");
        }
        String estadoNormalizado = nuevoEstado.trim().toUpperCase(Locale.ROOT)
                .replace('Á', 'A').replace('Ó', 'O').replace(' ', '_');
        if (!"ABIERTO".equals(estadoNormalizado)
                && !"EN_PREPARACION".equals(estadoNormalizado)) {
            throw new IllegalArgumentException(
                    "El estado debe ser ABIERTO o EN_PREPARACION. "
                    + "Para cerrar el pedido utiliza la opción de cierre definitivo.");
        }
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            boolean autoCommitOriginal = conexion.getAutoCommit();
            conexion.setAutoCommit(false);
            try {
                try (PreparedStatement bloqueo = conexion.prepareStatement(
                        "SELECT estado FROM Pedido WHERE id_pedido = ? FOR UPDATE")) {
                    bloqueo.setString(1, idPedido);
                    try (ResultSet resultado = bloqueo.executeQuery()) {
                        if (!resultado.next()) {
                            throw new SQLException("No existe el pedido " + idPedido + ".");
                        }
                        if (esEstadoFinal(resultado.getString("estado"))) {
                            throw new IllegalStateException(
                                    "No se puede cambiar el estado de un pedido cerrado "
                                    + "o completado.");
                        }
                    }
                }
                try (PreparedStatement sentencia = conexion.prepareStatement(
                        "UPDATE Pedido SET estado = ? WHERE id_pedido = ?")) {
                    sentencia.setString(1, estadoNormalizado);
                    sentencia.setString(2, idPedido);
                    sentencia.executeUpdate();
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

    private static List<Pedido> consultarSimple(String sql, String filtro) throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, filtro);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    pedidos.add(mapearPedido(resultado));
                }
            }
        }
        return pedidos;
    }

    private static List<Pedido> consultarRango(String sql, String estado, Date desde,
            Date hasta, boolean conEstado) throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            int indice = 1;
            if (conEstado) {
                sentencia.setString(indice++, estado);
                sentencia.setString(indice++, estado);
            }
            setFecha(sentencia, indice, indice + 1, desde);
            indice += 2;
            setFecha(sentencia, indice, indice + 1, hasta);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    pedidos.add(mapearPedido(resultado));
                }
            }
        }
        return pedidos;
    }

    private static void validarCliente(Connection conexion, Cliente cliente)
            throws SQLException {
        if (cliente == null) {
            return;
        }
        try (PreparedStatement sentencia = conexion.prepareStatement(
                "SELECT activo FROM Cliente WHERE id_cliente = ? FOR UPDATE")) {
            sentencia.setString(1, cliente.getIdCliente());
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (!resultado.next()) {
                    throw new SQLException("No existe el cliente "
                            + cliente.getIdCliente() + ".");
                }
                if (!resultado.getBoolean("activo")) {
                    throw new IllegalStateException("El cliente está inactivo y no puede "
                            + "asignarse a un pedido.");
                }
            }
        }
    }

    private static void validarMesaDisponible(Connection conexion, Mesa mesa)
            throws SQLException {
        if (mesa == null) {
            return;
        }
        try (PreparedStatement sentencia = conexion.prepareStatement(
                "SELECT estado FROM Mesa WHERE id_mesa = ? FOR UPDATE")) {
            sentencia.setString(1, mesa.getIdMesa());
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (!resultado.next()) {
                    throw new SQLException("No existe la mesa " + mesa.getIdMesa() + ".");
                }
                if (!"DISPONIBLE".equalsIgnoreCase(resultado.getString("estado"))) {
                    throw new IllegalStateException(
                            "La mesa no está disponible para un nuevo pedido.");
                }
            }
        }
    }

    private static BigDecimal sumarDetalles(Connection conexion, String idPedido)
            throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(
                "SELECT COALESCE(SUM(cantidad * precio_aplicado), 0.00) "
                        + "FROM DetallePedido WHERE id_pedido = ?")) {
            sentencia.setString(1, idPedido);
            try (ResultSet resultado = sentencia.executeQuery()) {
                resultado.next();
                return resultado.getBigDecimal(1).setScale(2, RoundingMode.HALF_UP);
            }
        }
    }

    private static void validarIdPedido(String idPedido) {
        if (idPedido == null || idPedido.isBlank()) {
            throw new IllegalArgumentException("El código del pedido es obligatorio.");
        }
    }

    private static void validarRangoFechas(Date desde, Date hasta) {
        if (desde != null && hasta != null && !desde.before(hasta)) {
            throw new IllegalArgumentException(
                    "La fecha final debe ser posterior al inicio del período.");
        }
    }

    private static boolean esEstadoFinal(String estado) {
        return estado != null && ("CERRADO".equalsIgnoreCase(estado.trim())
                || "COMPLETADO".equalsIgnoreCase(estado.trim()));
    }

    private static void setFecha(PreparedStatement sentencia, int nullIndex,
            int fechaIndex, Date fecha) throws SQLException {
        if (fecha == null) {
            sentencia.setNull(nullIndex, java.sql.Types.TIMESTAMP);
            sentencia.setNull(fechaIndex, java.sql.Types.TIMESTAMP);
        } else {
            Timestamp timestamp = new Timestamp(fecha.getTime());
            sentencia.setTimestamp(nullIndex, timestamp);
            sentencia.setTimestamp(fechaIndex, timestamp);
        }
    }

    private static Pedido mapearPedido(ResultSet resultado) throws SQLException {
        Cliente cliente = null;
        String idCliente = resultado.getString("id_cliente");
        if (idCliente != null) {
            cliente = new Cliente(idCliente, resultado.getString("nombre_cliente"),
                    resultado.getString("telefono"), resultado.getBoolean("activo"));
        }
        Mesa mesa = null;
        String idMesa = resultado.getString("id_mesa");
        if (idMesa != null) {
            mesa = new Mesa(idMesa, resultado.getString("numero"),
                    resultado.getInt("capacidad"), resultado.getString("estado_mesa"));
        }
        return new Pedido(resultado.getString("id_pedido"),
                resultado.getTimestamp("fecha"), resultado.getString("estado"),
                resultado.getBigDecimal("total").toPlainString(), cliente, mesa);
    }

    public record VentaPorPedido(String idPedido, Timestamp fecha, BigDecimal total) {
    }

    public record ResumenPedidos(long cantidadPedidos, BigDecimal importeTotal) {
    }
}
