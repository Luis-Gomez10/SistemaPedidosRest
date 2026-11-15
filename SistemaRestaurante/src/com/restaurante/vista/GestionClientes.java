package com.restaurante.vista;

import com.restaurante.controlador.ConsultasControlador;
import com.restaurante.controlador.Validador;
import com.restaurante.modelo.Cliente;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

public class GestionClientes extends JFrame {
    private final ConsultasControlador controlador = new ConsultasControlador();
    private final List<Cliente> clientes = new ArrayList<>();
    private String idOriginalSeleccionado;

    private final JTextField txtCodigo = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtTelefono = new JTextField();
    private final JCheckBox chkActivo = new JCheckBox("Activo", true);
    private final JTextField txtBuscar = new JTextField(20);
    private final DefaultTableModel modeloClientes = new DefaultTableModel(
            new Object[]{"Código", "Nombre", "Teléfono", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tablaClientes = new JTable(modeloClientes);

    public GestionClientes() {
        construirVentana();
        try {
            actualizarTabla("");
        } catch (SQLException ex) {
            mostrarErrorBD(ex);
        }
    }

    private void construirVentana() {
        setTitle("Gestión de clientes del restaurante");
        setSize(820, 520);
        setMinimumSize(new Dimension(710, 430));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel datosPanel = new JPanel(new GridLayout(2, 4, 8, 8));
        datosPanel.setBorder(BorderFactory.createTitledBorder("Datos del cliente"));
        datosPanel.add(new JLabel("Código:"));
        datosPanel.add(txtCodigo);
        datosPanel.add(new JLabel("Nombre:"));
        datosPanel.add(txtNombre);
        datosPanel.add(new JLabel("Teléfono:"));
        datosPanel.add(txtTelefono);
        datosPanel.add(new JLabel("Estado:"));
        datosPanel.add(chkActivo);

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        agregarBoton(botonesPanel, "Registrar", e -> registrarCliente());
        agregarBoton(botonesPanel, "Modificar", e -> modificarCliente());
        agregarBoton(botonesPanel, "Cambiar estado", e -> cambiarEstado());
        agregarBoton(botonesPanel, "Eliminar", e -> eliminarCliente());
        agregarBoton(botonesPanel, "Limpiar", e -> limpiarFormulario());

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(datosPanel, BorderLayout.CENTER);
        norte.add(botonesPanel, BorderLayout.SOUTH);

        JPanel busquedaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        busquedaPanel.add(new JLabel("Buscar cliente:"));
        busquedaPanel.add(txtBuscar);
        agregarBoton(busquedaPanel, "Buscar",
                e -> ejecutar(() -> actualizarTabla(txtBuscar.getText().trim())));
        agregarBoton(busquedaPanel, "Mostrar todo", e -> {
            txtBuscar.setText("");
            ejecutar(() -> actualizarTabla(""));
        });

        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.setBorder(BorderFactory.createTitledBorder("Clientes registrados"));
        centro.add(busquedaPanel, BorderLayout.NORTH);
        centro.add(new JScrollPane(tablaClientes), BorderLayout.CENTER);
        tablaClientes.getSelectionModel().addListSelectionListener(this::seleccionarCliente);

        principal.add(norte, BorderLayout.NORTH);
        principal.add(centro, BorderLayout.CENTER);
        add(principal);
    }

    private void registrarCliente() {
        ejecutar(() -> {
            validarFormulario();
            Cliente nuevo = crearCliente(chkActivo.isSelected());
            controlador.registrarCliente(nuevo);
            limpiarFormulario();
            actualizarTabla(txtBuscar.getText().trim());
            informar("Cliente registrado correctamente.");
        });
    }

    private void modificarCliente() {
        ejecutar(() -> {
            if (idOriginalSeleccionado == null) {
                throw new IllegalStateException("Selecciona un cliente para modificar.");
            }
            validarFormulario();
            Cliente actualizado = crearCliente(chkActivo.isSelected());
            controlador.actualizarCliente(idOriginalSeleccionado, actualizado);
            limpiarFormulario();
            actualizarTabla(txtBuscar.getText().trim());
            informar("Cliente actualizado correctamente.");
        });
    }

    private void cambiarEstado() {
        ejecutar(() -> {
            if (idOriginalSeleccionado == null) {
                throw new IllegalStateException("Selecciona un cliente para cambiar su estado.");
            }
            validarFormulario();
            Cliente actualizado = crearCliente(!chkActivo.isSelected());
            controlador.actualizarCliente(idOriginalSeleccionado, actualizado);
            limpiarFormulario();
            actualizarTabla(txtBuscar.getText().trim());
            informar("Estado del cliente actualizado.");
        });
    }

    private void eliminarCliente() {
        ejecutar(() -> {
            if (idOriginalSeleccionado == null) {
                throw new IllegalStateException("Selecciona un cliente para eliminar.");
            }
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar el cliente seleccionado?", "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);
            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
            controlador.eliminarCliente(idOriginalSeleccionado);
            limpiarFormulario();
            actualizarTabla(txtBuscar.getText().trim());
            informar("Cliente eliminado correctamente.");
        });
    }

    private void validarFormulario() {
        if (!Validador.esCodigoValido(txtCodigo.getText())
                || !Validador.esTextoValido(txtNombre.getText())
                || !Validador.esTextoValido(txtTelefono.getText())) {
            throw new IllegalArgumentException(
                    "El código, el nombre y el teléfono son obligatorios.");
        }
        if (!txtTelefono.getText().trim().matches("[0-9\\-\\s+]{7,15}")) {
            throw new IllegalArgumentException("El número de teléfono no es válido.");
        }
    }

    private Cliente crearCliente(boolean activo) {
        return new Cliente(txtCodigo.getText().trim(), txtNombre.getText().trim(),
                txtTelefono.getText().trim(), activo);
    }

    private void actualizarTabla(String filtro) throws SQLException {
        idOriginalSeleccionado = null;
        tablaClientes.clearSelection();
        clientes.clear();
        clientes.addAll(controlador.consultarClientes(filtro));
        modeloClientes.setRowCount(0);
        for (Cliente cliente : clientes) {
            modeloClientes.addRow(new Object[]{cliente.getIdCliente(), cliente.getNombre(),
                cliente.getTelefono(), cliente.isActivo() ? "Activo" : "Inactivo"});
        }
    }

    private void seleccionarCliente(ListSelectionEvent evento) {
        if (evento.getValueIsAdjusting()) {
            return;
        }
        int fila = tablaClientes.getSelectedRow();
        if (fila < 0) {
            return;
        }
        int filaModelo = tablaClientes.convertRowIndexToModel(fila);
        String codigo = modeloClientes.getValueAt(filaModelo, 0).toString();
        for (Cliente cliente : clientes) {
            if (cliente.getIdCliente().equals(codigo)) {
                idOriginalSeleccionado = cliente.getIdCliente();
                txtCodigo.setText(cliente.getIdCliente());
                txtNombre.setText(cliente.getNombre());
                txtTelefono.setText(cliente.getTelefono());
                chkActivo.setSelected(cliente.isActivo());
                return;
            }
        }
    }

    private void limpiarFormulario() {
        idOriginalSeleccionado = null;
        txtCodigo.setText("");
        txtNombre.setText("");
        txtTelefono.setText("");
        chkActivo.setSelected(true);
        tablaClientes.clearSelection();
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
        JOptionPane.showMessageDialog(this, texto, "Gestión de clientes",
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
