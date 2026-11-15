package com.restaurante.modelo;

import com.restaurante.dao.ProductoDAO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

/**
 * @author AA24025, GG14009, CR25046 y RA25009
 * Producto disponible en el restaurante.
 */
public class Producto {
    private String idProducto;
    private String nombre;
    private String categoria;
    private String descripcion;
    private BigDecimal precioUnitario;
    private boolean disponible;

    public Producto(String idProducto, String nombre, double precioUnitario, boolean disponible) {
        this(idProducto, nombre, "Otros", "", BigDecimal.valueOf(precioUnitario), disponible);
    }

    public Producto(String idProducto, String nombre, BigDecimal precioUnitario,
            boolean disponible) {
        this(idProducto, nombre, "Otros", "", precioUnitario, disponible);
    }

    public Producto(String idProducto, String nombre, String categoria, String descripcion,
            BigDecimal precioUnitario, boolean disponible) {
        setIdProducto(idProducto);
        setNombre(nombre);
        setCategoria(categoria);
        setDescripcion(descripcion);
        setPrecioUnitario(precioUnitario);
        setDisponible(disponible);
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = textoObligatorio(idProducto, "El ID del producto es obligatorio.");
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = textoObligatorio(nombre, "El nombre del producto es obligatorio.");
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = textoObligatorio(categoria, "La categoría del producto es obligatoria.");
        if (this.categoria.length() > 50) {
            throw new IllegalArgumentException("La categoría no debe exceder 50 caracteres.");
        }
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion == null ? "" : descripcion.trim();
        if (this.descripcion.length() > 255) {
            throw new IllegalArgumentException("La descripción no debe exceder 255 caracteres.");
        }
    }

    public double getPrecioUnitario() {
        return precioUnitario.doubleValue();
    }

    public BigDecimal getPrecioUnitarioDecimal() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        if (!Double.isFinite(precioUnitario)) {
            throw new IllegalArgumentException("El precio debe ser numérico y mayor que cero.");
        }
        setPrecioUnitario(BigDecimal.valueOf(precioUnitario));
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        if (precioUnitario == null) {
            throw new IllegalArgumentException("El precio es obligatorio.");
        }
        BigDecimal precioRedondeado = precioUnitario.setScale(2, RoundingMode.HALF_UP);
        if (precioRedondeado.signum() <= 0 || precioRedondeado.precision() > 10) {
            throw new IllegalArgumentException("El precio debe ser finito y mayor que cero.");
        }
        this.precioUnitario = precioRedondeado;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public void registrarProductos() throws SQLException {
        new ProductoDAO().registrar(this);
    }

    public Producto buscarProducto() throws SQLException {
        return new ProductoDAO().buscarPorId(idProducto);
    }

    private static String textoObligatorio(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
        return valor.trim();
    }

    @Override
    public String toString() {
        return idProducto + " - " + nombre + " ($" + precioUnitario.toPlainString() + ")";
    }
}
