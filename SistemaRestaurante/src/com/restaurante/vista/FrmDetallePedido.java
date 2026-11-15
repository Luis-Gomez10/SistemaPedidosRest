package com.restaurante.vista;

import com.restaurante.controlador.ConsultasControlador;
import com.restaurante.controlador.Validador;
import com.restaurante.dao.DetallePedidoDAO;
import com.restaurante.modelo.Cliente;
import com.restaurante.modelo.DetallePedido;
import com.restaurante.modelo.Mesa;
import com.restaurante.modelo.Pedido;
import com.restaurante.modelo.Producto;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class FrmDetallePedido extends JFrame {
    private final ConsultasControlador controlador = new ConsultasControlador();
    private final JTextField txtPedido = new JTextField();
    private final JTextField txtCliente = new JTextField();
    private final JTextField txtMesa = new JTextField();
    private final JTextField txtProducto = new JTextField();
    private final JTextField txtCantidad = new JTextField();
    private final JTextField txtNuevaCantidad = new JTextField();
    private final JComboBox<String> cmbEstadoPedido = new JComboBox<>(
            new String[]{"ABIERTO", "EN_PREPARACION"});
    private final JButton btnActualizarEstado = new JButton("Actualizar estado");
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"Detalle", "Producto", "Nombre", "Cantidad", "Precio", "Subtotal"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tablaDetalle = new JTable(modeloTabla);
    private final JLabel lblEstadoTotal = new JLabel("Pedido no cargado");
    private Pedido pedidoActual;

    public FrmDetallePedido() {
        setTitle("Gestión de pedidos y detalles - Sistema Restaurante");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        agregarEtiqueta("ID pedido:", 20, 20, 90);
        txtPedido.setBounds(110, 20, 130, 25);
        add(txtPedido);
        agregarEtiqueta("ID cliente (opcional):", 260, 20, 140);
        txtCliente.setBounds(400, 20, 130, 25);
        add(txtCliente);
        agregarEtiqueta("ID mesa (opcional):", 550, 20, 130);
        txtMesa.setBounds(680, 20, 100, 25);
        add(txtMesa);
        agregarBoton("Crear pedido", 20, 60, e -> crearPedido());
        agregarBoton("Cargar pedido", 175, 60, e -> cargarPedido());
        agregarEtiqueta("Nuevo estado:", 350, 60, 95);
        cmbEstadoPedido.setBounds(445, 60, 145, 25);
        add(cmbEstadoPedido);
        btnActualizarEstado.setBounds(610, 58, 180, 30);
        btnActualizarEstado.addActionListener(e -> actualizarEstadoPedido());
        add(btnActualizarEstado);

        agregarEtiqueta("ID producto:", 20, 110, 100);
        txtProducto.setBounds(120, 110, 130, 25);
        add(txtProducto);
        agregarEtiqueta("Cantidad:", 270, 110, 75);
        txtCantidad.setBounds(345, 110, 80, 25);
        add(txtCantidad);
        agregarBoton("Agregar producto", 445, 108, e -> agregarDetalle());
        agregarBoton("Cerrar pedido", 650, 108, e -> cerrarPedido());

        JScrollPane scroll = new JScrollPane(tablaDetalle);
        scroll.setBounds(20, 155, 840, 320);
        add(scroll);
        lblEstadoTotal.setBounds(20, 490, 400, 25);
        add(lblEstadoTotal);
        agregarEtiqueta("Nueva cantidad:", 20, 525, 110);
        txtNuevaCantidad.setBounds(130, 525, 80, 25);
        add(txtNuevaCantidad);
        agregarBoton("Actualizar cantidad", 225, 523, e -> actualizarCantidad());
        agregarBoton("Eliminar detalle", 415, 523, e -> eliminarDetalle());
    }

    private void crearPedido() {
        ejecutar(() -> {
            String id = txtPedido.getText().trim();
            if (!Validador.esCodigoValido(id)) {
                throw new IllegalArgumentException("El código del pedido es obligatorio.");
            }
            Cliente cliente = null;
            if (!txtCliente.getText().isBlank()) {
                cliente = controlador.buscarCliente(txtCliente.getText().trim());
                if (cliente == null) {
                    throw new IllegalArgumentException("No existe el cliente indicado.");
                }
            }
            Mesa mesa = null;
            if (!txtMesa.getText().isBlank()) {
                mesa = controlador.buscarMesa(txtMesa.getText().trim());
                if (mesa == null) {
                    throw new IllegalArgumentException("No existe la mesa indicada.");
                }
            }
            Pedido nuevoPedido = new Pedido(id, new Date(), "ABIERTO", cliente, mesa);
            controlador.crearPedido(nuevoPedido);
            pedidoActual = nuevoPedido;
            refrescarDetalles();
            mostrarMensaje("Pedido creado correctamente.");
        });
    }

    private void cargarPedido() {
        ejecutar(() -> {
            String id = txtPedido.getText().trim();
            if (!Validador.esCodigoValido(id)) {
                throw new IllegalArgumentException("El código del pedido es obligatorio.");
            }
            pedidoActual = controlador.consultarPedidoPorCodigo(id);
            if (pedidoActual == null) {
                limpiarTabla();
                lblEstadoTotal.setText("Pedido no encontrado");
                mostrarAlerta("No existe el pedido indicado.");
                return;
            }
            refrescarDetalles();
        });
    }

    private void agregarDetalle() {
        ejecutar(() -> {
            validarPedidoCargado();
            if (!Validador.esCodigoValido(txtProducto.getText())) {
                throw new IllegalArgumentException("El código del producto es obligatorio.");
            }
            if (!Validador.esEnteroPositivo(txtCantidad.getText())) {
                throw new IllegalArgumentException(
                        "La cantidad debe ser un entero positivo mayor que cero.");
            }
            Producto producto = controlador.buscarProducto(txtProducto.getText().trim());
            if (producto == null) {
                throw new IllegalArgumentException("No existe el producto indicado.");
            }
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            controlador.agregarDetalle(pedidoActual, new DetallePedido(producto, cantidad));
            refrescarDetalles();
            txtProducto.setText("");
            txtCantidad.setText("");
        });
    }

    private void actualizarCantidad() {
        ejecutar(() -> {
            validarPedidoCargado();
            int fila = tablaDetalle.getSelectedRow();
            if (fila < 0) {
                throw new IllegalArgumentException("Selecciona un detalle para modificar.");
            }
            if (!Validador.esEnteroPositivo(txtNuevaCantidad.getText())) {
                throw new IllegalArgumentException(
                        "La cantidad debe ser un entero positivo mayor que cero.");
            }
            int idDetalle = ((Number) modeloTabla.getValueAt(fila, 0)).intValue();
            int cantidad = Integer.parseInt(txtNuevaCantidad.getText().trim());
            controlador.actualizarCantidadDetalle(idDetalle, pedidoActual, cantidad);
            refrescarDetalles();
        });
    }

    private void eliminarDetalle() {
        ejecutar(() -> {
            validarPedidoCargado();
            int fila = tablaDetalle.getSelectedRow();
            if (fila < 0) {
                throw new IllegalArgumentException("Selecciona un detalle para eliminar.");
            }
            int idDetalle = ((Number) modeloTabla.getValueAt(fila, 0)).intValue();
            controlador.eliminarDetalle(idDetalle, pedidoActual);
            refrescarDetalles();
        });
    }

    private void cerrarPedido() {
        ejecutar(() -> {
            validarPedidoCargado();
            controlador.cerrarPedido(pedidoActual.getIdPedido(), "CERRADO");
            pedidoActual = controlador.consultarPedidoPorCodigo(pedidoActual.getIdPedido());
            refrescarDetalles();
            mostrarMensaje("Pedido cerrado correctamente.");
        });
    }

    private void actualizarEstadoPedido() {
        ejecutar(() -> {
            validarPedidoCargado();
            String nuevoEstado = (String) cmbEstadoPedido.getSelectedItem();
            controlador.actualizarEstadoPedido(pedidoActual.getIdPedido(), nuevoEstado);
            refrescarDetalles();
            mostrarMensaje("Estado del pedido actualizado correctamente.");
        });
    }

    private void refrescarDetalles() throws SQLException {
        Pedido pedidoPersistido = controlador.consultarPedidoPorCodigo(pedidoActual.getIdPedido());
        if (pedidoPersistido == null) {
            throw new SQLException("No existe el pedido " + pedidoActual.getIdPedido() + ".");
        }
        pedidoActual = pedidoPersistido;
        List<DetallePedidoDAO.DetalleResumen> detalles =
                controlador.consultarDetalles(pedidoActual.getIdPedido());
        modeloTabla.setRowCount(0);
        for (DetallePedidoDAO.DetalleResumen detalle : detalles) {
            modeloTabla.addRow(new Object[]{detalle.idDetalle(), detalle.idProducto(),
                detalle.nombreProducto(), detalle.cantidad(),
                detalle.precioAplicado().toPlainString(), detalle.subtotal().toPlainString()});
        }
        String mesa = pedidoActual.getMesa() == null
                ? "" : " | Mesa: " + pedidoActual.getMesa().getNumero();
        lblEstadoTotal.setText("Estado: " + pedidoActual.getEstado() + " | Total: $"
                + pedidoActual.getTotal() + mesa);
        boolean estadoFinal = "CERRADO".equalsIgnoreCase(pedidoActual.getEstado())
                || "COMPLETADO".equalsIgnoreCase(pedidoActual.getEstado());
        cmbEstadoPedido.setEnabled(!estadoFinal);
        btnActualizarEstado.setEnabled(!estadoFinal);
        if ("ABIERTO".equalsIgnoreCase(pedidoActual.getEstado())) {
            cmbEstadoPedido.setSelectedItem("ABIERTO");
        } else if ("EN_PREPARACION".equalsIgnoreCase(pedidoActual.getEstado())
                || "EN PREPARACIÓN".equalsIgnoreCase(pedidoActual.getEstado())
                || "EN PREPARACION".equalsIgnoreCase(pedidoActual.getEstado())) {
            cmbEstadoPedido.setSelectedItem("EN_PREPARACION");
        }
    }

    private void validarPedidoCargado() {
        if (pedidoActual == null) {
            throw new IllegalStateException("Crea o carga un pedido antes de continuar.");
        }
    }

    private void limpiarTabla() {
        modeloTabla.setRowCount(0);
    }

    private void ejecutar(Accion accion) {
        try {
            accion.ejecutar();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            mostrarAlerta(ex.getMessage());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo completar la operación en MySQL: " + ex.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarEtiqueta(String texto, int x, int y, int ancho) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setBounds(x, y, ancho, 25);
        add(etiqueta);
    }

    private void agregarBoton(String texto, int x, int y, ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.setBounds(x, y, texto.length() > 15 ? 180 : 145, 30);
        boton.addActionListener(accion);
        add(boton);
    }

    private void mostrarAlerta(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Validación",
                JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarMensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Sistema Restaurante",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @FunctionalInterface
    private interface Accion {
        void ejecutar() throws SQLException;
    }
}
