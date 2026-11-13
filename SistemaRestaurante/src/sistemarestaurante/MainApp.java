package sistemarestaurante;

import javax.swing.*;
import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
/**
 *
 * @author Bianca Clarissa Ramírez Arévalo
 */
public class MainApp extends JFrame{

    public MainApp() {
        setTitle("SISTEMA DE PEDIDOS DEL RESTAURANTE - Panel Principal");
        setSize(400, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        
        // Título visual
        JLabel lblTitulo = new JLabel("MENÚ PRINCIPAL", SwingConstants.CENTER);
        lblTitulo.setBounds(50, 20, 300, 30);
        add(lblTitulo);
        
        // Botón para abrir el JFrame de Mesas
        JButton btnMesas = new JButton("1. Gestionar Mesas");
        btnMesas.setBounds(80, 70, 240, 35);
        add(btnMesas);
        
        // Botón para abrir el JFrame de Detalles de Pedido
        JButton btnPedidos = new JButton("2. Gestionar Pedidos / Detalle");
        btnPedidos.setBounds(80, 120, 240, 35);
        add(btnPedidos);
        
        // Botón para la Factura en HTML
        JButton btnFactura = new JButton("3. Generar Factura en HTML");
        btnFactura.setBounds(80, 170, 240, 35);
        add(btnFactura);
        
        // --- ACCIONES DE LOS BOTONES ---
        btnMesas.addActionListener(e -> {
            FrmMesa frmMesa = new FrmMesa();
            frmMesa.setVisible(true);
        });
        
        btnPedidos.addActionListener(e -> {
            FrmDetallePedido frmDetalle = new FrmDetallePedido();
            frmDetalle.setVisible(true);
        });
        
        btnFactura.addActionListener(e -> exportarFacturaHTML());
    }
    
    // Método para crear y abrir una factura en HTML
    private void exportarFacturaHTML() {
        try {
            String contenidoHtml = 
                    "<html>\n" +
                    "<head>\n" +
                    "    <style>\n" +
                    "        body { font-family: Arial, sans-serif; margin: 40px; background-color: #f4f4f4; }\n" +
                    "        .ticket { background: white; padding: 20px; border-radius: 8px; width: 300px; margin: auto; box-shadow: 0px 0px 10px rgba(0,0,0,0.1); }\n" +
                    "        h2 { text-align: center; color: #33; }\n" +
                    "        table { width: 100%; border-collapse: collapse; margin-top: 15px; }\n" +
                    "        th, td { padding: 8px; text-align: left; border-bottom: 1px solid #ddd; }\n" +
                    "        .total { font-weight: bold; text-align: right; margin-top: 15px; }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class='ticket'>\n" +
                    "        <h2>RESTAURANTE</h2>\n" +
                    "        <p><b>Cliente:</b> Público General</p>\n" +
                    "        <p><b>Fecha:</b> 2026-11-12</p>\n" +
                    "        <table>\n" +
                    "            <tr><th>Platillo</th><th>Cant</th><th>Sub</th></tr>\n" +
                    "            <tr><td>Plato Típico</td><td>2</td><td>$10.00</td></tr>\n" +
                    "            <tr><td>Bebida Natural</td><td>2</td><td>$3.00</td></tr>\n" +
                    "        </table>\n" +
                    "        <p class='total'>TOTAL: $13.00</p>\n" +
                    "        <p style='text-align:center; font-size:12px; color:#777;'>¡Gracias por su compra!</p>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";
            
            // Guardar el archivo HTML temporalmente
            File archivoHtml = new File("ticket_factura.html");
            FileWriter escritor = new FileWriter(archivoHtml);
            escritor.write(contenidoHtml);
            escritor.close();
            
            // Abrir automáticamnete en el navegador web predeterminado
            if (Desktop.isDesktopSupported()){
                Desktop.getDesktop().browse(archivoHtml.toURI());
                JOptionPane.showMessageDialog(this, "¡Factura HTML generada y abierta en el navegador con éxito!");
        }
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Error al generar el HTML: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    public static void main(String [] args) {
        // Ejecutar la aplicación gráfica
        SwingUtilities.invokeLater(() -> {
            new FrmLogin().setVisible(true);
        });
    }
}
