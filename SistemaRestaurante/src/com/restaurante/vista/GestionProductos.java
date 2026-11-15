package com.restaurante.vista;

import com.restaurante.controlador.ConsultasControlador;
import com.restaurante.controlador.Validador;
import com.restaurante.modelo.Producto;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

public class GestionProductos extends JFrame {
    private final ConsultasControlador controlador = new ConsultasControlador();
    private final List<Producto> productos = new ArrayList<>();
    private String idOriginalSeleccionado;

    private final JTextField txtCodigo = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JComboBox<String> cmbCategoria = new JComboBox<>(
            new String[]{"Alimentos", "Bebidas", "Postres", "Otros"});
    private final JTextField txtDescripcion = new JTextField();
    private final JTextField txtPrecio = new JTextField();
    private final JCheckBox chkDisponible = new JCheckBox("Disponible", true);
    private final JTextField txtBuscar = new JTextField(20);
    private final DefaultTableModel modeloProductos = new DefaultTableModel(
            new Object[]{"Código", "Nombre", "Categoría", "Descripción", "Precio", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tablaProductos = new JTable(modeloProductos);

    public GestionProductos() {
        construirVentana();
        try {
            actualizarTabla("");
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }

    private void construirVentana() {
        setTitle("Gestión de productos del restaurante");
        setSize(900, 560);
        setMinimumSize(new Dimension(780, 470));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel datosPanel = new JPanel(new GridLayout(3, 4, 8, 8));
        datosPanel.setBorder(BorderFactory.createTitledBorder("Datos del producto"));
        datosPanel.add(new JLabel("Código:"));
        datosPanel.add(txtCodigo);
        datosPanel.add(new JLabel("Nombre:"));
        datosPanel.add(txtNombre);
        datosPanel.add(new JLabel("Categoría:"));
        datosPanel.add(cmbCategoria);
        datosPanel.add(new JLabel("Precio:"));
        datosPanel.add(txtPrecio);
        datosPanel.add(new JLabel("Descripción:"));
        datosPanel.add(txtDescripcion);
        datosPanel.add(new JLabel("Disponibilidad:"));
        datosPanel.add(chkDisponible);

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        agregarBoton(botonesPanel, "Registrar", e -> registrarProducto());
        agregarBoton(botonesPanel, "Modificar", e -> modificarProducto());
        agregarBoton(botonesPanel, "Cambiar disponibilidad", e -> cambiarDisponibilidad());
        agregarBoton(botonesPanel, "Eliminar", e -> eliminarProducto());
        agregarBoton(botonesPanel, "Limpiar", e -> limpiarFormulario());

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(datosPanel, BorderLayout.CENTER);
        norte.add(botonesPanel, BorderLayout.SOUTH);

        JPanel busquedaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        busquedaPanel.add(new JLabel("Buscar por código, nombre, categoría o descripción:"));
        busquedaPanel.add(txtBuscar);
        agregarBoton(busquedaPanel, "Buscar",
                e -> ejecutar(() -> actualizarTabla(txtBuscar.getText().trim())));
        agregarBoton(busquedaPanel, "Mostrar todo", e -> {
            txtBuscar.setText("");
            ejecutar(() -> actualizarTabla(""));
        });

        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.setBorder(BorderFactory.createTitledBorder("Productos registrados"));
        centro.add(busquedaPanel, BorderLayout.NORTH);
        centro.add(new JScrollPane(tablaProductos), BorderLayout.CENTER);
        tablaProductos.getSelectionModel().addListSelectionListener(this::seleccionarProducto);

        principal.add(norte, BorderLayout.NORTH);
        principal.add(centro, BorderLayout.CENTER);
        add(principal);
    }

    private void registrarProducto() {
        ejecutar(() -> {
            validarFormulario();
            controlador.registrarProducto(crearProducto(chkDisponible.isSelected()));
            limpiarFormulario();
            actualizarTabla(txtBuscar.getText().trim());
            informar("Producto registrado correctamente.");
        });
    }

    private void modificarProducto() {
        ejecutar(() -> {
            if (idOriginalSeleccionado == null) {
                throw new IllegalStateException("Selecciona un producto para modificar.");
            }
            validarFormulario();
            controlador.actualizarProducto(idOriginalSeleccionado,
                    crearProducto(chkDisponible.isSelected()));
            limpiarFormulario();
            actualizarTabla(txtBuscar.getText().trim());
            informar("Producto actualizado correctamente.");
        });
    }

    private void cambiarDisponibilidad() {
        ejecutar(() -> {
            Producto seleccionado = productoSeleccionado();
            Producto actualizado = new Producto(seleccionado.getIdProducto(),
                    seleccionado.getNombre(), seleccionado.getCategoria(),
                    seleccionado.getDescripcion(), seleccionado.getPrecioUnitarioDecimal(),
                    !seleccionado.isDisponible());
            controlador.actualizarProducto(idOriginalSeleccionado, actualizado);
            limpiarFormulario();
            actualizarTabla(txtBuscar.getText().trim());
            informar("Disponibilidad del producto actualizada.");
        });
    }

    private void eliminarProducto() {
        ejecutar(() -> {
            productoSeleccionado();
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar el producto seleccionado?", "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);
            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
            controlador.eliminarProducto(idOriginalSeleccionado);
            limpiarFormulario();
            actualizarTabla(txtBuscar.getText().trim());
            informar("Producto eliminado correctamente.");
        });
    }

    private void validarFormulario() {
        if (!Validador.esCodigoValido(txtCodigo.getText())
                || !Validador.esTextoValido(txtNombre.getText())
                || !Validador.esPrecioValido(txtPrecio.getText())) {
            throw new IllegalArgumentException(
                    "Completa código y nombre; el precio debe ser numérico y mayor que cero.");
        }
    }

    private Producto crearProducto(boolean disponible) {
        return new Producto(txtCodigo.getText().trim(), txtNombre.getText().trim(),
                (String) cmbCategoria.getSelectedItem(), txtDescripcion.getText(),
                new BigDecimal(txtPrecio.getText().trim()), disponible);
    }

    private void actualizarTabla(String filtro) throws SQLException {
        idOriginalSeleccionado = null;
        tablaProductos.clearSelection();
        productos.clear();
        productos.addAll(controlador.consultarProductos(filtro));
        modeloProductos.setRowCount(0);
        for (Producto producto : productos) {
            modeloProductos.addRow(new Object[]{producto.getIdProducto(), producto.getNombre(),
                producto.getCategoria(), producto.getDescripcion(),
                producto.getPrecioUnitarioDecimal().toPlainString(),
                producto.isDisponible() ? "Disponible" : "No disponible"});
        }
    }

    private void seleccionarProducto(ListSelectionEvent evento) {
        if (evento.getValueIsAdjusting()) {
            return;
        }
        int fila = tablaProductos.getSelectedRow();
        if (fila < 0) {
            return;
        }
        int filaModelo = tablaProductos.convertRowIndexToModel(fila);
        String codigo = modeloProductos.getValueAt(filaModelo, 0).toString();
        for (Producto producto : productos) {
            if (producto.getIdProducto().equals(codigo)) {
                idOriginalSeleccionado = producto.getIdProducto();
                txtCodigo.setText(producto.getIdProducto());
                txtNombre.setText(producto.getNombre());
                cmbCategoria.setSelectedItem(producto.getCategoria());
                txtDescripcion.setText(producto.getDescripcion());
                txtPrecio.setText(producto.getPrecioUnitarioDecimal().toPlainString());
                chkDisponible.setSelected(producto.isDisponible());
                return;
            }
        }
    }

    private Producto productoSeleccionado() {
        if (idOriginalSeleccionado == null) {
            throw new IllegalStateException("Selecciona un producto de la tabla.");
        }
        for (Producto producto : productos) {
            if (producto.getIdProducto().equals(idOriginalSeleccionado)) {
                return producto;
            }
        }
        throw new IllegalStateException("El producto seleccionado ya no está en la lista.");
    }

    private void limpiarFormulario() {
        idOriginalSeleccionado = null;
        txtCodigo.setText("");
        txtNombre.setText("");
        cmbCategoria.setSelectedIndex(0);
        txtDescripcion.setText("");
        txtPrecio.setText("");
        chkDisponible.setSelected(true);
        tablaProductos.clearSelection();
    }

    private void ejecutar(Accion accion) {
        try {
            accion.ejecutar();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }

    private void mostrarErrorBD(SQLException ex) {
        JOptionPane.showMessageDialog(this,
                "No se pudo completar la operación en MySQL: " + ex.getMessage(),
                "Error de base de datos", JOptionPane.ERROR_MESSAGE);
    }

    private void informar(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Gestión de productos",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void agregarBoton(JPanel panel, String texto,
            java.awt.event.ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(accion);
        panel.add(boton);
    }

    @FunctionalInterface
    private interface Accion {
        void ejecutar() throws SQLException;
    }
}
