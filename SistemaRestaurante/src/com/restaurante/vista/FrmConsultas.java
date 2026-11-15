package com.restaurante.vista;

import com.restaurante.controlador.ConsultasControlador;
import com.restaurante.modelo.Pedido;
import com.restaurante.modelo.Producto;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
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

public class FrmConsultas extends JFrame {
    private final ConsultasControlador controlador = new ConsultasControlador();
    private final JTextField txtCodigo = new JTextField();
    private final JTextField txtMesa = new JTextField();
    private final JTextField txtDesde = new JTextField();
    private final JTextField txtHasta = new JTextField();
    private final JComboBox<String> cmbEstado = new JComboBox<>(
            new String[]{"ABIERTO", "EN_PREPARACION", "COMPLETADO", "CERRADO"});
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Código", "Fecha", "Estado", "Total", "Cliente", "Mesa"}, 0);

    public FrmConsultas() {
        setTitle("Consultas y reportes - Sistema Restaurante");
        setSize(900, 590);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        agregarEtiqueta("Código pedido:", 20, 20, 110);
        txtCodigo.setBounds(130, 20, 125, 25);
        add(txtCodigo);
        agregarBoton("Buscar pedido", 265, 18, e -> buscarPorCodigo());

        agregarEtiqueta("ID/número mesa:", 385, 20, 105);
        txtMesa.setBounds(490, 20, 100, 25);
        add(txtMesa);
        agregarBoton("Pedidos por mesa", 600, 18, e -> buscarPorMesa());

        agregarEtiqueta("Estado:", 20, 60, 55);
        cmbEstado.setBounds(75, 60, 150, 25);
        add(cmbEstado);
        agregarBoton("Filtrar estado", 235, 58, e -> buscarPorEstado());

        agregarEtiqueta("Desde (AAAA-MM-DD):", 420, 60, 150);
        txtDesde.setBounds(570, 60, 110, 25);
        add(txtDesde);
        agregarEtiqueta("Hasta:", 690, 60, 45);
        txtHasta.setBounds(735, 60, 110, 25);
        add(txtHasta);
        agregarBoton("Buscar por fechas", 20, 100, e -> buscarPorFechas());
        agregarBoton("Catálogo de productos", 180, 100, e -> listarProductos());
        agregarBoton("Resumen de pedidos", 370, 100, e -> mostrarResumen());
        agregarBoton("Verificar total", 560, 100, e -> verificarTotal());
        agregarBoton("Cerrar pedido", 700, 100, e -> cerrarPedido());
        agregarBoton("Pedidos Activos", 20, 140, e -> buscarPedidosActivos());
        agregarBoton("Solo Productos Disponibles", 180, 140,
                e -> listarProductosDisponibles());

        JTable tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 180, 840, 355);
        add(scroll);
    }

    private void buscarPorCodigo() {
        ejecutar(() -> {
            Pedido pedido = controlador.consultarPedidoPorCodigo(txtCodigo.getText().trim());
            mostrarPedidos(pedido == null ? List.of() : List.of(pedido));
            if (pedido == null) {
                mensaje("No existe el pedido indicado.");
            }
        });
    }

    private void buscarPorMesa() {
        ejecutar(() -> mostrarPedidos(
                controlador.consultarPedidosPorMesa(txtMesa.getText().trim())));
    }

    private void buscarPorEstado() {
        ejecutar(() -> mostrarPedidos(
                controlador.consultarPedidosPorEstado((String) cmbEstado.getSelectedItem())));
    }

    private void buscarPorFechas() {
        ejecutar(() -> {
            Date desde = leerFecha(txtDesde.getText(), false);
            Date hasta = leerFecha(txtHasta.getText(), true);
            if (desde == null && hasta == null) {
                throw new IllegalArgumentException(
                        "Indica al menos una fecha para consultar el período.");
            }
            if (desde != null && hasta != null && !desde.before(hasta)) {
                throw new IllegalArgumentException(
                        "La fecha Hasta debe ser igual o posterior a Desde.");
            }
            mostrarPedidos(controlador.consultarPedidosPorFecha(desde, hasta));
        });
    }

    private void listarProductos() {
        ejecutar(() -> mostrarProductos(controlador.listarProductos()));
    }

    private void listarProductosDisponibles() {
        ejecutar(() -> mostrarProductos(controlador.listarProductosDisponibles()));
    }

    private void mostrarProductos(List<Producto> productos) {
        modelo.setColumnIdentifiers(new Object[]{"Código", "Producto", "Precio", "Disponible"});
        modelo.setRowCount(0);
        for (Producto producto : productos) {
            modelo.addRow(new Object[]{producto.getIdProducto(), producto.getNombre(),
                producto.getPrecioUnitarioDecimal().toPlainString(),
                producto.isDisponible() ? "Sí" : "No"});
        }
    }

    private void buscarPedidosActivos() {
        ejecutar(() -> mostrarPedidos(controlador.consultarPedidosActivos()));
    }

    private void mostrarResumen() {
        ejecutar(() -> {
            var resumen = controlador.obtenerResumenPedidos();
            JOptionPane.showMessageDialog(this,
                    "Cantidad total de pedidos: " + resumen.cantidadPedidos()
                    + "\nImporte total: $" + resumen.importeTotal().toPlainString(),
                    "Resumen de pedidos", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void verificarTotal() {
        ejecutar(() -> {
            String idPedido = txtCodigo.getText().trim();
            boolean consistente = controlador.verificarYCorregirTotal(idPedido);
            JOptionPane.showMessageDialog(this, consistente
                    ? "El total coincide con la suma de los detalles."
                    : "Se detectó una diferencia y el total se recalculó desde los detalles.",
                    "Verificación del total", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void cerrarPedido() {
        ejecutar(() -> {
            String idPedido = txtCodigo.getText().trim();
            controlador.cerrarPedido(idPedido, "CERRADO");
            Pedido pedido = controlador.consultarPedidoPorCodigo(idPedido);
            mostrarPedidos(pedido == null ? List.of() : List.of(pedido));
            mensaje("El pedido se cerró correctamente.");
        });
    }

    private void mostrarPedidos(List<Pedido> pedidos) {
        modelo.setColumnIdentifiers(
                new Object[]{"Código", "Fecha", "Estado", "Total", "Cliente", "Mesa"});
        modelo.setRowCount(0);
        for (Pedido pedido : pedidos) {
            modelo.addRow(new Object[]{pedido.getIdPedido(), pedido.getFecha(),
                pedido.getEstado(), pedido.getTotal(),
                pedido.getCliente() == null ? "" : pedido.getCliente().getNombre(),
                pedido.getMesa() == null ? "" : pedido.getMesa().getNumero()});
        }
    }

    private Date leerFecha(String texto, boolean exclusiva) {
        String valor = texto.trim();
        if (valor.isEmpty()) {
            return null;
        }
        try {
            LocalDate fecha = LocalDate.parse(valor);
            if (exclusiva) {
                fecha = fecha.plusDays(1);
            }
            return java.sql.Date.valueOf(fecha);
        } catch (DateTimeException | ArithmeticException ex) {
            throw new IllegalArgumentException("Usa una fecha válida con formato AAAA-MM-DD.");
        }
    }

    private void ejecutar(Accion accion) {
        try {
            accion.ejecutar();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Validación", JOptionPane.WARNING_MESSAGE);
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
        boton.setBounds(x, y, texto.length() > 18 ? 180 : 145, 30);
        boton.addActionListener(accion);
        add(boton);
    }

    private void mensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Sistema Restaurante",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @FunctionalInterface
    private interface Accion {
        void ejecutar() throws SQLException;
    }
}
