package com.restaurante.controlador;

import com.restaurante.dao.ClienteDAO;
import com.restaurante.dao.DetallePedidoDAO;
import com.restaurante.dao.MesaDAO;
import com.restaurante.dao.PedidoDAO;
import com.restaurante.dao.ProductoDAO;
import com.restaurante.modelo.Cliente;
import com.restaurante.modelo.DetallePedido;
import com.restaurante.modelo.Mesa;
import com.restaurante.modelo.Pedido;
import com.restaurante.modelo.Producto;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class ConsultasControlador {
    private final MesaDAO mesaDAO = new MesaDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final DetallePedidoDAO detallePedidoDAO = new DetallePedidoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    public Pedido consultarPedidoPorCodigo(String idPedido) throws SQLException {
        return pedidoDAO.consultarPorId(idPedido);
    }

    public List<Pedido> consultarPedidosPorMesa(String idMesa) throws SQLException {
        return pedidoDAO.consultarPorMesa(idMesa);
    }

    public List<Pedido> consultarPedidosPorEstado(String estado) throws SQLException {
        return pedidoDAO.consultarPorEstado(estado);
    }

    public List<Pedido> consultarPedidosActivos() throws SQLException {
        return pedidoDAO.consultarPedidosActivos();
    }

    public List<Pedido> consultarPedidosPorFecha(Date desde, Date hasta) throws SQLException {
        return pedidoDAO.consultarPorFecha(desde, hasta);
    }

    public List<Producto> listarProductos() throws SQLException {
        return productoDAO.listarTodos();
    }

    public List<Producto> listarProductosDisponibles() throws SQLException {
        return productoDAO.listarDisponibles();
    }

    public PedidoDAO.ResumenPedidos obtenerResumenPedidos() throws SQLException {
        return pedidoDAO.obtenerResumen();
    }

    public boolean verificarYCorregirTotal(String idPedido) throws SQLException {
        return pedidoDAO.verificarYCorregirTotal(idPedido);
    }

    public void cerrarPedido(String idPedido, String estadoFinal) throws SQLException {
        pedidoDAO.cerrarPedido(idPedido, estadoFinal);
    }

    public void actualizarEstadoPedido(String idPedido, String nuevoEstado)
            throws SQLException {
        pedidoDAO.actualizarEstado(idPedido, nuevoEstado);
    }

    public void agregarDetalle(Pedido pedido, DetallePedido detalle) throws SQLException {
        detallePedidoDAO.agregar(pedido, detalle);
    }

    public void actualizarCantidadDetalle(int idDetalle, Pedido pedido, int cantidad)
            throws SQLException {
        detallePedidoDAO.actualizarCantidad(idDetalle, pedido, cantidad);
    }

    public void eliminarDetalle(int idDetalle, Pedido pedido) throws SQLException {
        detallePedidoDAO.eliminar(idDetalle, pedido);
    }

    public BigDecimal recalcularTotal(String idPedido) throws SQLException {
        return pedidoDAO.recalcularTotal(idPedido);
    }

    public List<Mesa> consultarMesasDisponibles() throws SQLException {
        return mesaDAO.consultarMesasDisponibles();
    }

    public void eliminarMesa(String idMesa) throws SQLException {
        mesaDAO.eliminar(idMesa);
    }

    public void crearPedido(Pedido pedido) throws SQLException {
        pedidoDAO.crear(pedido);
    }

    public void registrarCliente(Cliente cliente) throws SQLException {
        clienteDAO.registrar(cliente);
    }

    public List<Cliente> consultarClientes(String filtro) throws SQLException {
        return clienteDAO.buscar(filtro);
    }

    public void actualizarCliente(String idOriginal, Cliente cliente) throws SQLException {
        clienteDAO.actualizar(idOriginal, cliente);
    }

    public void eliminarCliente(String idCliente) throws SQLException {
        clienteDAO.eliminar(idCliente);
    }

    public void registrarProducto(Producto producto) throws SQLException {
        productoDAO.registrar(producto);
    }

    public List<Producto> consultarProductos(String filtro) throws SQLException {
        return productoDAO.buscar(filtro);
    }

    public void actualizarProducto(String idOriginal, Producto producto) throws SQLException {
        productoDAO.actualizar(idOriginal, producto);
    }

    public void eliminarProducto(String idProducto) throws SQLException {
        productoDAO.eliminar(idProducto);
    }

    public Cliente buscarCliente(String idCliente) throws SQLException {
        return clienteDAO.buscarPorId(idCliente);
    }

    public Mesa buscarMesa(String idMesa) throws SQLException {
        return mesaDAO.buscarPorId(idMesa);
    }

    public Producto buscarProducto(String idProducto) throws SQLException {
        return productoDAO.buscarPorId(idProducto);
    }

    public List<DetallePedidoDAO.DetalleResumen> consultarDetalles(String idPedido)
            throws SQLException {
        return detallePedidoDAO.consultarPorPedido(idPedido);
    }

    public List<Pedido> consultarPedidos(String estado, Date desde, Date hasta)
            throws SQLException {
        return pedidoDAO.consultarPorEstadoYFecha(estado, desde, hasta);
    }

    public List<PedidoDAO.VentaPorPedido> reporteVentas(Date desde, Date hasta)
            throws SQLException {
        return pedidoDAO.reporteVentasPorPedido(desde, hasta);
    }
}
