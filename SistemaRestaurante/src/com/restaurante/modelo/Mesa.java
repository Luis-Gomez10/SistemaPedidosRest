package com.restaurante.modelo;

import com.restaurante.dao.MesaDAO;
import java.sql.SQLException;
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
        setIdMesa(idMesa);
        setNumero(numero);
        setCapacidad(capacidad);
        setEstado(estado);
    }

    public void cambiarEstado() throws SQLException {
        new MesaDAO().actualizarEstado(this);
    }

    public void consultarMesas() throws SQLException {
        new MesaDAO().consultarMesas().forEach(System.out::println);
    }
    
    // Getters y Setters
    public String getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(String idMesa) {
        this.idMesa = textoObligatorio(idMesa, "El ID de la mesa es obligatorio.");
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = textoObligatorio(numero, "El número de mesa es obligatorio.");
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero.");
        }
        this.capacidad = capacidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = textoObligatorio(estado, "El estado de la mesa es obligatorio.");
    }

    private static String textoObligatorio(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
        return valor.trim();
    }
    
    @Override
    public String toString() {
        return "Mesa ID: " + idMesa + " | N°: " + numero + " | Capacidad: " + capacidad + " | Estado: " + estado;
    }
}
