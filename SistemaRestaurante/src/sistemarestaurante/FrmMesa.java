package sistemarestaurante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
/**
 *
 * @author Bianca Clarissa Ramírez Arévalo
 */
public class FrmMesa extends JFrame{
    private ArrayList<Mesa> listaMesas;
    private JTextField txtId, txtNumero, txtCapacidad;
    private JComboBox<String> cmbEstado;
    private JTable tablaMesas;
    private DefaultTableModel modeloTabla;
    
    public FrmMesa() {
        listaMesas = new ArrayList<>();
        
        // Configuración de la ventana (JFrame)
        setTitle("Gestión de Mesas - Sistema Restaurante");
        setSize(600, 400);
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
        
        // Botón Registrar
        JButton btnRegistrar = new JButton("Registrar Mesa");
        btnRegistrar.setBounds(300, 40, 150, 30);
        add(btnRegistrar);
        
        // Tabla para listar las mesas
        tablaMesas = new JTable();
        modeloTabla = new DefaultTableModel(new Object[]{"ID", "Número", "Capacidad", "Estado"}, 0);
        tablaMesas.setModel(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaMesas);
        scrollPane.setBounds(20, 190, 540, 150);
        add(scrollPane);
        
        // Acción del Botón Registrar
        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarMesa();
            }
        });
        
        // Datos de prueba iniciales
        cargarDatosPrueba();
    }
    
    private void registrarMesa() {
        try {
            String id = txtId.getText();
            String numero = txtNumero.getText();
            int capacidad = Integer.parseInt(txtCapacidad.getText());
            String estado = cmbEstado.getSelectedItem().toString();
            
            // Creamos el objeto usando clase Mesa
            Mesa nuevaMesa = new Mesa(id, numero, capacidad, estado);
            listaMesas.add(nuevaMesa);
            
            // Actualizar la tabla visual
            modeloTabla.addRow(new Object[]{id, numero, capacidad, estado});
            
            // Limpiar campos
            txtId.setText("");
            txtNumero.setText("");
            txtCapacidad.setText("");
            JOptionPane.showMessageDialog(this, "¡Mesa registrada con éxito!");
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, introduce un número válido en capacidad.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarDatosPrueba() {
        listaMesas.add(new Mesa("M01", "1", 4, "Disponible"));
        listaMesas.add(new Mesa("M02", "2", 2, "Ocupada"));
        for (Mesa m: listaMesas) {
            modeloTabla.addRow(new Object[]{m.getIdMesa()});
        }
    }
}
