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
public class FrmDetallePedido extends JFrame{
    private ArrayList<DetallePedido> listaDetalles;
    private JTextField txtPlatillo, txtCantidad, txtPrecio;
    private JTable tablaDetalle;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotalAcumulado;
    
    public FrmDetallePedido() {
        listaDetalles = new ArrayList<>();
        
        setTitle("Detalle de Pedido - Sistema Restaurante");
        setSize(650, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        
        // Componentes visuales
        JLabel lblPlatillo = new JLabel("Nombre Platillo:");
        lblPlatillo.setBounds(20, 20, 120, 25);
        add(lblPlatillo);
        
        txtPlatillo = new JTextField();
        txtPlatillo.setBounds(140, 20, 160, 25);
        add(txtPlatillo);
        
        JLabel lblCantidad = new JLabel("Cantidad:");
        lblCantidad.setBounds(20, 60, 120, 25);
        add(lblCantidad);
        
        txtCantidad = new JTextField();
        txtCantidad.setBounds(140, 60, 160, 25);
        add(txtCantidad);
        
        JLabel lblPrecio = new JLabel("Precio Unitario ($):");
        lblPrecio.setBounds(20, 100, 120, 25);
        add(lblPrecio);
        
        txtPrecio = new JTextField();
        txtPrecio.setBounds(140, 100, 160, 25);
        add(txtPrecio);
        
        // Botón Agregar
        JButton btnAgregar = new JButton("Agregar al Pedido");
        btnAgregar.setBounds(330, 60, 160, 30);
        add(btnAgregar);
        
        // Tabla de detalles
        tablaDetalle = new JTable();
        modeloTabla = new DefaultTableModel(new Object[]{"Platillo", "Cantidad", "Precio Unitario", "Subtotal"}, 0);
        tablaDetalle.setModel(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaDetalle);
        scrollPane.setBounds(20, 150, 590, 170);
        add(scrollPane);
        
        // Etiqueta de Total
        lblTotalAcumulado = new JLabel("TOTAL: $0.00");
        lblTotalAcumulado.setBounds(420, 340, 190, 30);
        add(lblTotalAcumulado);
        
        // Acción del botón 
        btnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarDetalle();
            }
        });
    }
    
    private void agregarDetalle () {
        try {
            String nombre = txtPlatillo.getText();
            int cantidad = Integer.parseInt(txtCantidad.getText());
            double precio = Double.parseDouble(txtPrecio.getText());
            
            // Usamos clase DetallePedido y su método calcularSubtotal()
            DetallePedido detalle = new DetallePedido(nombre, cantidad, precio);
            listaDetalles.add(detalle);
            
            // Agregar a la tabla
            modeloTabla.addRow(new Object[]{nombre, cantidad, precio, detalle.calcularSubtotal()});
            
            // Actualizar total general
            calcularTotalGeneral();
            
            // Limpiar Campos
            txtPlatillo.setText("");
            txtCantidad.setText("");
            txtPrecio.setText("");
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Verifica que cantidad y precio sean numéricos.", "Error", JOptionPane.ERROR_MESSAGE);          
        }
    }
    
    private void calcularTotalGeneral() {
        double total = 0;
        for (DetallePedido d: listaDetalles) {
            total += d.calcularSubtotal();
        }
        lblTotalAcumulado.setText(String.format("TOTAL: $%.2f", total));
    }
}
