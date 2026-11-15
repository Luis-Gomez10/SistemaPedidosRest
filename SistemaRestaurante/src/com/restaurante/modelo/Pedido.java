package com.restaurante.modelo;

import com.restaurante.dao.PedidoDAO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Date;

public class Pedido {
    private String idPedido;
    private Date fecha;
    private String estado;
    private BigDecimal total;
    private Cliente cliente;
    private Mesa mesa;

    public Pedido(String idPedido, Date fecha, String estado, Cliente cliente, Mesa mesa) {
        this(idPedido, fecha, estado, "0.00", cliente, mesa);
    }

    public Pedido(String idPedido, Date fecha, String estado, String total,
            Cliente cliente, Mesa mesa) {
        setIdPedido(idPedido);
        setFecha(fecha);
        setEstado(estado);
        setTotalInicial(total);
        this.cliente = cliente;
        this.mesa = mesa;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        validarEditable();
        this.idPedido = textoObligatorio(idPedido, "El ID del pedido es obligatorio.");
    }

    public Date getFecha() {
        return new Date(fecha.getTime());
    }

    public void setFecha(Date fecha) {
        validarEditable();
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha del pedido es obligatoria.");
        }
        this.fecha = new Date(fecha.getTime());
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        String nuevoEstado = textoObligatorio(estado, "El estado del pedido es obligatorio.");
        if (esEstadoFinal(this.estado) && !this.estado.equalsIgnoreCase(nuevoEstado)) {
            throw new IllegalStateException("Un pedido cerrado o completado es inmutable.");
        }
        this.estado = nuevoEstado;
    }

    public String getTotal() {
        return total.toPlainString();
    }

    public BigDecimal getTotalDecimal() {
        return total;
    }

    private void setTotalInicial(String total) {
        if (total == null) {
            throw new IllegalArgumentException("El total debe ser un importe decimal no negativo.");
        }
        try {
            BigDecimal importe = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP);
            if (importe.signum() < 0 || importe.precision() > 10) {
                throw new IllegalArgumentException(
                        "El total debe ser un importe decimal no negativo de hasta 10 dígitos.");
            }
            this.total = importe;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "El total debe ser un importe decimal no negativo.", ex);
        }
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        validarEditable();
        this.cliente = cliente;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        validarEditable();
        this.mesa = mesa;
    }

    public void crearPedido() throws SQLException {
        new PedidoDAO().crear(this);
    }

    public double calcularTotal() throws SQLException {
        java.math.BigDecimal totalCalculado = new PedidoDAO().recalcularTotal(idPedido);
        total = totalCalculado.setScale(2, RoundingMode.HALF_UP);
        return totalCalculado.doubleValue();
    }

    private void validarEditable() {
        if (esEstadoFinal(estado)) {
            throw new IllegalStateException("Un pedido cerrado o completado es inmutable.");
        }
    }

    private static boolean esEstadoFinal(String valor) {
        return valor != null && ("CERRADO".equalsIgnoreCase(valor)
                || "COMPLETADO".equalsIgnoreCase(valor));
    }

    private static String textoObligatorio(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
        return valor.trim();
    }
}
