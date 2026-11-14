package sistemarestaurante;

/**
 *
 * @author luis gomez
 */
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Validador {

    // Validar texto obligatorio (no nulo ni solo espacios)
    public static boolean esTextoValido(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    // Validar entero positivo (> 0) para capacidades y cantidades
    public static boolean esEnteroPositivo(String valor) {
        try {
            int num = Integer.parseInt(valor.trim());
            return num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Validar decimal no negativo (>= 0.0) para precios
    public static boolean esPrecioValido(String valor) {
        try {
            double num = Double.parseDouble(valor.trim());
            return num >= 0.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Validar formato básico de correo
    public static boolean esCorreoValido(String correo) {
        if (!esTextoValido(correo)) return true; // Es opcional en clientes
        return correo.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    // para contraseñas
    public static String encriptarSHA256(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar contraseña", e);
        }
    }
}
