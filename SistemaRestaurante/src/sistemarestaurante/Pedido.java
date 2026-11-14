package sistemarestaurante;

/**
 *
 * @author luis Gomez
 */

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private String codigo;
    private String fecha;
    private String codigoCliente; // Opcional ("PUBLICO_GENERAL")
    private String codigoMesa;    // Opcional ("PARA_LLEVAR")
    private String estado;        // "ABIERTO", "EN_PREPARACION", "CERRADO"
    private List<DetallePedido> detalles;
    private double total;

    public Pedido(String codigo, String codigoCliente, String codigoMesa) {
        this.codigo = codigo;
        this.fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.codigoCliente = (codigoCliente == null || codigoCliente.isEmpty()) ? "SIN_REGISTRO" : codigoCliente;
        this.codigoMesa = (codigoMesa == null || codigoMesa.isEmpty()) ? "SIN_MESA" : codigoMesa;
        this.estado = "ABIERTO";
        this.detalles = new ArrayList<>();
        this.total = 0.0;
    }

    // Recalcular total con precisión decimal
    public void recalcularTotal() {
        double suma = 0.0;
        for (DetallePedido d : detalles) {
            suma += d.getSubtotal();
        }
        this.total = Math.round(suma * 100.0) / 100.0;
    }

    // Validación de estado antes de modificar
    public boolean permiteModificacion() {
        return "ABIERTO".equalsIgnoreCase(this.estado) || "EN_PREPARACION".equalsIgnoreCase(this.estado);
    }

    // Getters y Setters
    public String getCodigo() { return codigo; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getCodigoCliente() { return codigoCliente; }
    public void setCodigoCliente(String codigoCliente) { this.codigoCliente = codigoCliente; }
    public String getCodigoMesa() { return codigoMesa; }
    public void setCodigoMesa(String codigoMesa) { this.codigoMesa = codigoMesa; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public List<DetallePedido> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedido> detalles) { 
        this.detalles = detalles; 
        recalcularTotal();
    }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
