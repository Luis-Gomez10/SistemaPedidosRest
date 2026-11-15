package com.restaurante.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import com.restaurante.controlador.Validador;
import com.restaurante.dao.MesaDAO;
import com.restaurante.modelo.Mesa;
import java.sql.SQLException;
/**
 *
 * @author Bianca Clarissa Ramírez Arévalo
 */
public class FrmMesa extends JFrame{
    private ArrayList<Mesa> listaMesas;
    private JTextField txtId, txtNumero, txtCapacidad, txtBuscar;
    private JComboBox<String> cmbEstado;
    private JTable tablaMesas;
    private DefaultTableModel modeloTabla;
    
    public FrmMesa() {
        listaMesas = new ArrayList<>();
        
        // Configuración de la ventana (JFrame)
        setTitle("Gestión de Mesas - Sistema Restaurante");
        setSize(800, 470);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        
        // Componentes visuales (Etiquetas y TextFields)
        JLabel lblId = new JLabel("ID Mesa:");
        lblId.setBounds(20, 20, 100, 25);
        add(lblId);
        
        txtId = new JTextField();
        txtId.setBounds(120, 20, 150, 25);
        add(txtId);
        
        JLabel lblNumero = new JLabel("N° Mesa:");
        lblNumero.setBounds(20, 60, 100, 25);
        add(lblNumero);
        
        txtNumero = new JTextField();;
        txtNumero.setBounds(120, 60, 150, 25);
        add(txtNumero);
        
        JLabel lblCapacidad = new JLabel("Capacidad:");
        lblCapacidad.setBounds(20, 100, 100, 25);
        add(lblCapacidad);
        
        txtCapacidad = new JTextField();
        txtCapacidad.setBounds(120, 100, 150, 25);
        add(txtCapacidad);
        
        JLabel lblEstado = new JLabel("Estado:");
        lblEstado.setBounds(20, 140, 100, 25);
        add(lblEstado);
        
        cmbEstado = new JComboBox<>(new String[]{"Disponible", "Ocupada", "Reservada"});
        cmbEstado.setBounds(120, 140, 150, 25);
        add(cmbEstado);

        JLabel lblBuscar = new JLabel("Buscar por ID o número:");
        lblBuscar.setBounds(510, 20, 200, 25);
        add(lblBuscar);
        txtBuscar = new JTextField();
        txtBuscar.setBounds(510, 45, 180, 25);
        add(txtBuscar);
        
        // Botón Registrar
        JButton btnRegistrar = new JButton("Registrar Mesa");
        btnRegistrar.setBounds(310, 20, 170, 30);
        add(btnRegistrar);

        JButton btnModificar = new JButton("Modificar / Cambiar Estado");
        btnModificar.setBounds(295, 60, 210, 30);
        add(btnModificar);

        JButton btnEliminar = new JButton("Eliminar por ID");
        btnEliminar.setBounds(310, 100, 170, 30);
        add(btnEliminar);

        JButton btnBuscar = new JButton("Buscar Mesa");
        btnBuscar.setBounds(510, 80, 180, 30);
        add(btnBuscar);
        
        // Tabla para listar las mesas
        tablaMesas = new JTable();
        tablaMesas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Número", "Capacidad", "Estado"}, 0);
        tablaMesas.setModel(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaMesas);
        scrollPane.setBounds(20, 190, 740, 220);
        add(scrollPane);
        
        // Acción del Botón Registrar
        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarMesa();
            }
        });

        btnEliminar.addActionListener(e -> eliminarMesa());
        btnModificar.addActionListener(e -> modificarMesa());
        btnBuscar.addActionListener(e -> buscarMesa());
        txtBuscar.addActionListener(e -> buscarMesa());
        tablaMesas.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && tablaMesas.getSelectedRow() >= 0) {
                cargarMesaEnFormulario(tablaMesas.getSelectedRow());
            }
        });
        
        try {
            cargarMesas();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudieron consultar las mesas en MySQL: " + ex.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void registrarMesa() {
        try {
            String id = txtId.getText().trim();
            String numero = txtNumero.getText().trim();
            if (!Validador.esTextoValido(id) || !Validador.esTextoValido(numero)
                    || !Validador.esEnteroPositivo(txtCapacidad.getText())) {
                JOptionPane.showMessageDialog(this, "Completa los campos y usa una capacidad mayor que cero.",
                        "Datos no válidos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int capacidad = Integer.parseInt(txtCapacidad.getText().trim());
            String estado = cmbEstado.getSelectedItem().toString();
            
            // Creamos el objeto usando clase Mesa
            Mesa nuevaMesa = new Mesa(id, numero, capacidad, estado);
            new MesaDAO().registrar(nuevaMesa);
            listaMesas.add(nuevaMesa);
            
            // Actualizar la tabla visual
            modeloTabla.addRow(new Object[]{id, numero, capacidad, estado});
            
            // Limpiar campos
            txtId.setText("");
            txtNumero.setText("");
            txtCapacidad.setText("");
            JOptionPane.showMessageDialog(this, "¡Mesa registrada con éxito!");
            
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Datos no válidos", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar la mesa en MySQL: " + ex.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarMesa() {
        try {
            String id = txtId.getText().trim();
            if (!Validador.esCodigoValido(id)) {
                JOptionPane.showMessageDialog(this, "Ingresa el ID de la mesa.",
                        "Datos no válidos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            new MesaDAO().eliminar(id);
            cargarMesas();
            JOptionPane.showMessageDialog(this, "Mesa eliminada correctamente.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No se pudo eliminar", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo eliminar la mesa en MySQL: " + ex.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarMesa() {
        try {
            String criterio = txtBuscar.getText().trim();
            if (criterio.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingresa un ID o número de mesa.",
                        "Datos no válidos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Mesa mesa = new MesaDAO().buscarPorCodigoONumero(criterio);
            if (mesa == null) {
                JOptionPane.showMessageDialog(this, "No se encontró la mesa indicada.",
                        "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            cargarMesas();
            seleccionarMesa(mesa.getIdMesa());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Datos no válidos", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo buscar la mesa en MySQL: " + ex.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarMesa() {
        try {
            int fila = tablaMesas.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this,
                        "Selecciona la mesa que deseas modificar.",
                        "Mesa no seleccionada", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String numero = txtNumero.getText().trim();
            String capacidadTexto = txtCapacidad.getText().trim();
            if (!Validador.esTextoValido(numero)
                    || !Validador.esEnteroPositivo(capacidadTexto)) {
                JOptionPane.showMessageDialog(this,
                        "Completa el número y usa una capacidad mayor que cero.",
                        "Datos no válidos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int filaModelo = tablaMesas.convertRowIndexToModel(fila);
            String id = modeloTabla.getValueAt(filaModelo, 0).toString();
            Mesa mesa = new Mesa(id, numero, Integer.parseInt(capacidadTexto),
                    cmbEstado.getSelectedItem().toString());
            new MesaDAO().actualizarMesa(mesa);
            cargarMesas();
            seleccionarMesa(id);
            JOptionPane.showMessageDialog(this, "Mesa modificada correctamente.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Datos no válidos", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo modificar la mesa en MySQL: " + ex.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarMesaEnFormulario(int filaVista) {
        int filaModelo = tablaMesas.convertRowIndexToModel(filaVista);
        txtId.setText(modeloTabla.getValueAt(filaModelo, 0).toString());
        txtNumero.setText(modeloTabla.getValueAt(filaModelo, 1).toString());
        txtCapacidad.setText(modeloTabla.getValueAt(filaModelo, 2).toString());
        cmbEstado.setSelectedItem(modeloTabla.getValueAt(filaModelo, 3).toString());
    }

    private void seleccionarMesa(String idMesa) {
        for (int fila = 0; fila < modeloTabla.getRowCount(); fila++) {
            if (idMesa.equals(modeloTabla.getValueAt(fila, 0))) {
                tablaMesas.setRowSelectionInterval(fila, fila);
                return;
            }
        }
    }
    
    private void cargarMesas() throws SQLException {
        listaMesas.clear();
        listaMesas.addAll(new MesaDAO().consultarMesas());
        modeloTabla.setRowCount(0);
        for (Mesa m: listaMesas) {
            modeloTabla.addRow(new Object[]{m.getIdMesa(), m.getNumero(), m.getCapacidad(), m.getEstado()});
        }
    }
}
