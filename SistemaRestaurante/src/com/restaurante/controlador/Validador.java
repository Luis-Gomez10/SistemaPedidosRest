package com.restaurante.controlador;

/**
 *
 * @author luis gomez
 */
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Validador {

    // Validar texto obligatorio (no nulo ni solo espacios)
    public static boolean esTextoValido(String texto) {
        return texto != null && !texto.isBlank();
    }

    // Validar entero positivo (> 0) para capacidades y cantidades
    public static boolean esEnteroPositivo(String valor) {
        if (!esTextoValido(valor)) {
            return false;
        }
        try {
            int num = Integer.parseInt(valor.trim());
            return num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean esPrecioValido(String valor) {
        if (!esTextoValido(valor)) {
            return false;
        }
        try {
            BigDecimal precio = new BigDecimal(valor.trim()).setScale(2, RoundingMode.HALF_UP);
            return precio.signum() > 0 && precio.precision() <= 10;
        } catch (NumberFormatException | ArithmeticException e) {
            return false;
        }
    }

    public static boolean esCodigoValido(String codigo) {
        return esTextoValido(codigo);
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
