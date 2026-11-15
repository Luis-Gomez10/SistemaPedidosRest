package com.restaurante.modelo;

import com.restaurante.dao.ClienteDAO;
import java.sql.SQLException;

/**
 * Representa a un cliente del restaurante.
 *
 * Cliente que puede asociarse a varios pedidos.
 */
public class Cliente {
    private String idCliente;
    private String nombre;
    private String telefono;
    private boolean activo;

    public Cliente(String idCliente, String nombre, String telefono) {
        this(idCliente, nombre, telefono, true);
    }

    public Cliente(String idCliente, String nombre, String telefono, boolean activo) {
        setIdCliente(idCliente);
        setNombre(nombre);
        setTelefono(telefono);
        setActivo(activo);
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = textoObligatorio(idCliente, "El ID del cliente es obligatorio.");
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = textoObligatorio(nombre, "El nombre del cliente es obligatorio.");
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = textoObligatorio(telefono, "El teléfono del cliente es obligatorio.");
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void registrarCliente() throws SQLException {
        new ClienteDAO().registrar(this);
    }

    public Cliente buscarCliente() throws SQLException {
        return new ClienteDAO().buscarPorId(idCliente);
    }

    private static String textoObligatorio(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
        return valor.trim();
    }

    @Override
    public String toString() {
        return idCliente + " - " + nombre + " (" + telefono + ")"
                + (activo ? " - Activo" : " - Inactivo");
    }
}
