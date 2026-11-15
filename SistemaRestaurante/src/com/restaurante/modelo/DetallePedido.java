package com.restaurante.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author AA24025, GG14009, CR25046 y RA25009
 *
 * Relación:
 * DetallePedido * ---- 1 Producto
 */
public class DetallePedido {
    private final Producto producto;
    private final int cantidad;
    private final BigDecimal precioAplicado;

    public DetallePedido(Producto producto, int cantidad) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser null.");
        }
        if (!producto.isDisponible()) {
            throw new IllegalArgumentException("No se puede agregar un producto no disponible.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }

        this.producto = producto;
        this.cantidad = cantidad;
        this.precioAplicado = producto.getPrecioUnitarioDecimal()
                .setScale(2, RoundingMode.HALF_UP);
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioAplicado() {
        return precioAplicado.doubleValue();
    }

    public double calcularSubtotal() {
        return calcularSubtotalDecimal().doubleValue();
    }

    public BigDecimal getPrecioAplicadoDecimal() {
        return precioAplicado;
    }

    public BigDecimal calcularSubtotalDecimal() {
        return precioAplicado.multiply(BigDecimal.valueOf(cantidad))
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return producto.getNombre()
                + " x " + cantidad
                + " = $" + calcularSubtotalDecimal().toPlainString();
    }
}
