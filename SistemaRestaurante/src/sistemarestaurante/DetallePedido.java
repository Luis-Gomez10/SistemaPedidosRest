package sistemarestaurante;
/**
 *
 * @author Bianca Clarissa Ramírez Arévalo
 */
public class DetallePedido {
    private String nombrePlatillo; // Para identificar qué se pidió
    private int cantidad;
    private double precioAplicado;
    
    // Constructor vacío
    public DetallePedido() {
        
        
    }
    
    // Constructor con los parámetros exactos del diagrama
    public DetallePedido(String nombrePlatillo, int cantidad, double precioAplicado) {
        this.nombrePlatillo = nombrePlatillo;
        this.cantidad = cantidad; 
        this.precioAplicado = precioAplicado;
    }
    
    // Método según el diagrama
    public double calcularSubtotal() {
        return this.cantidad * this.precioAplicado; 
    }
    
    // Getters y Setters
    public String getNombrePlatillo() {
        return nombrePlatillo;
    }
    
    public int getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
    public double getPrecioAplicado() {
        return precioAplicado;
    }
    
    public void setPrecioAplicado(double precioAplicado) {
        this.precioAplicado = precioAplicado;
    }
    
    @Override
    public String toString() {
        return cantidad + "x " + nombrePlatillo + " (Precio Unitario: $" + precioAplicado + ") - Subtotal: $" + calcularSubtotal();
    }
}
