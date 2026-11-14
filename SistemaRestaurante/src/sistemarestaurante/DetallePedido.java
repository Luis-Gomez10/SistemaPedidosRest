package modelo;

/**
 * @author AA24025, GG14009, CR25046 y RA25009
 *
 * Relación:
 * DetallePedido * ---- 1 Producto
 */
public class DetallePedido {
    private final Producto producto;
    private int cantidad;

    public DetallePedido(Producto producto, int cantidad) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser null.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }

        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        this.cantidad = cantidad;
    }

    public double getSubtotal() {
        return producto.getPrecio() * cantidad;
    }

    @Override
    public String toString() {
        return producto.getNombre()
                + " x " + cantidad
                + " = $" + String.format("%.2f", getSubtotal());
    }
}
