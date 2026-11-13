package sistemarestaurante;
/**
 *
 * @author Bianca Clarissa Ramírez Arévalo
 */
public class Mesa {
    private String idMesa;
    private String numero;
    private int capacidad;
    private String estado;
    
    // Constructor vacío
    public Mesa() {
        
    }
    
    public Mesa(String idMesa, String numero, int capacidad, String estado) {
        this.idMesa = idMesa;
        this.numero = numero;
        this.capacidad = capacidad;
        this.estado = estado;
    }
    
    public void cambiarEstado(String nuevoEstado) {
        this.estado = nuevoEstado;
    }
    
    // Getters y Setters
    public String getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(String idMesa) {
        this.idMesa = idMesa;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String gerEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    @Override
    public String toString() {
        return "Mesa ID: " + idMesa + " | N°: " + numero + " | Capacidad: " + capacidad + " | Estado: " + estado;
    }
}
