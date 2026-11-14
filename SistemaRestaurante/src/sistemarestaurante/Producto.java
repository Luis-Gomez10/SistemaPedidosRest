package modelo;

/**
 * @author AA24025, GG14009, CR25046 y RA25009
 * Producto disponible en el restaurante.
 */
public class Producto {
    private int id;
    private String nombre;
    private double precio;
    private String idProductos;
    private String categoria;
    private String descripcion;
    private double precioUnitario;
    private boolean disponible;

    public Producto(int id, String nombre, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }
    // agrego un nuevo constructor 

    public Producto(int id, String nombre, double precio, String idProductos, String categoria, String descripcion, double precioUnitario, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.idProductos = idProductos;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.precioUnitario = precioUnitario;
        this.disponible = disponible;
    }
    //creamos los getters y setters
    
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
   
    public String getIdProductos() {
        return idProductos;
    }
    public void setIdProductos(String idProductos) {
        this.idProductos = idProductos;
    }
    
    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }
    
     public double getPrecio() {
        return precio;
    }
    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }
    public void setPrecioUnitario(double precioUnitario) {
           this.precioUnitario = precioUnitario;
       }
    
    public boolean isDisponible() {
        return disponible;
    }
    public void setDisponible(boolean disponible) {
            this.disponible = disponible;
        }
    
    @Override
    public String toString() {
        return id + " - " + nombre + " ($" + String.format("%.2f", precio) + ")";
    }
}
