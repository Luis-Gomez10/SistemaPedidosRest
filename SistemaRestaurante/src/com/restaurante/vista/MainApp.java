package com.restaurante.vista;

import javax.swing.*;

/**
 *
 * @author Bianca Clarissa Ramírez Arévalo
 */
public class MainApp extends JFrame{

    public MainApp() {
        setTitle("SISTEMA DE PEDIDOS DEL RESTAURANTE - Panel Principal");
        setSize(400, 390);
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
        
        JButton btnConsultas = new JButton("3. Consultas y reportes");
        btnConsultas.setBounds(80, 170, 240, 35);
        add(btnConsultas);

        JButton btnCatalogo = new JButton("4. Gestionar productos");
        btnCatalogo.setBounds(80, 220, 240, 35);
        add(btnCatalogo);

        JButton btnClientes = new JButton("5. Gestionar clientes");
        btnClientes.setBounds(80, 270, 240, 35);
        add(btnClientes);
        
        // --- ACCIONES DE LOS BOTONES ---
        btnMesas.addActionListener(e -> {
            FrmMesa frmMesa = new FrmMesa();
            frmMesa.setVisible(true);
        });
        
        btnPedidos.addActionListener(e -> {
            FrmDetallePedido frmDetalle = new FrmDetallePedido();
            frmDetalle.setVisible(true);
        });
        
        btnConsultas.addActionListener(e -> new FrmConsultas().setVisible(true));

        btnCatalogo.addActionListener(e -> new GestionProductos().setVisible(true));
        btnClientes.addActionListener(e -> new GestionClientes().setVisible(true));
    }
    
    public static void main(String [] args) {
        // Ejecutar la aplicación gráfica
        SwingUtilities.invokeLater(() -> {
            new FrmLogin().setVisible(true);
        });
    }
}
