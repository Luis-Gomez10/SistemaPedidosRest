package sistemarestaurante;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 *
 * @author Bianca Clarissa Ramírez Arévalo
 */
public class FrmLogin extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtContraseña;
    
    public FrmLogin() {
        setTitle("Inicio de Sesión - Sistema de Pedidos del Restaurante");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        
        // Etiqueta de Título
        JLabel lblTitulo = new JLabel("ACCESO AL SISTEMA");
        lblTitulo.setBounds(120, 20, 180, 25);
        add(lblTitulo);
        
        // Campo Usuario / Correo
        JLabel lblUsuario = new JLabel("Usuario / Correo:");
        lblUsuario.setBounds(40, 70, 110, 25);
        add(lblUsuario);
        
        txtUsuario = new JTextField();
        txtUsuario.setBounds(150, 70, 118, 25);
        add(txtUsuario);
        
        // Campo Contraseña
        JLabel lblContraseña = new JLabel("Contraseña:");
        lblContraseña.setBounds(40, 110, 110, 25);
        add(lblContraseña);
        
        txtContraseña = new JPasswordField();
        txtContraseña.setBounds(150, 110, 180, 25);
        add(txtContraseña);
        
        // Botón Iniciar Sesión
        JButton btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBounds(130, 160, 140, 30);
        add(btnLogin);
        
        // Acción del botón
        btnLogin.addActionListener(new ActionListener() { 
            @Override
            public void actionPerformed(ActionEvent e) {
                validarAcceso();
            }
        });
    }
    
    private void validarAcceso() {
        String usuario = txtUsuario.getText().trim();
        String contraseña = new String(txtContraseña.getPassword()).trim();
        
        // Validación 1: Campos vacíos (según rúbrica)
        if (usuario.isEmpty() || contraseña.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos obligatorios.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validación 2: Credenciales
        // Credencial de prueba temporal: admin / 12345
        if (usuario.equals("admin") && contraseña.equals("12345")) {
            JOptionPane.showMessageDialog(this, "¡Bienvenido al sistema, " + usuario + "!", "Acceso COrrecto", JOptionPane.INFORMATION_MESSAGE);
            
            // Abrir el Panel Principal (MainApp)
            MainApp panelPrincipal = new MainApp();
            panelPrincipal.setVisible(true);
            
            // Cerrar la ventana de Login
            this.dispose();
        } else {
            // Validación 3: Credenciales incorrectas
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos. Intente nuevamente.", "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
            
            // Limpiar contraseña por seguridad
            txtContraseña.setText("");
        }
    }
}
