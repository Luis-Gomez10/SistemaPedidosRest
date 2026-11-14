package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa a un cliente del restaurante.
 *
 * Relación:
 * Cliente 1 ---- 0..* Pedido
 * Asociación navegable desde Cliente hacia sus pedidos.
 */
public class Cliente {
    private int id;
    private String nombre;
    private String telefono;
    private String idClientes;
    private boolean activo;
    private final List<Pedido> pedidos;

    public Cliente(int id, String nombre, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.pedidos = new ArrayList<>();
    }
    
    // nuevo constructor que incluye las modificaciones que le hice al codigo

    public Cliente(int id, String nombre, String telefono, String idClientes, boolean activo, List<Pedido> pedidos) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.idClientes = idClientes;
        this.activo = activo;
        this.pedidos = pedidos;
    }
    

    public int getId() {
        return id;
    }
    public void setId(int id) {
            this.id = id;
        }
    
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
           this.nombre = nombre;
       }
    
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
            this.telefono = telefono;
        }
    
    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public String getIdClientes() {
        return idClientes;
    }
    public void setIdClientes(String idClientes) {
            this.idClientes = idClientes;
        }
    
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    public void agregarPedido(Pedido pedido) {
        if (pedido != null && !pedidos.contains(pedido)) {
            pedidos.add(pedido);
        }
    }

    @Override
    public String toString() {
        return (idClientes != null ? idClientes : id) + " - " + nombre + " (" + telefono + ")";
    }
}
