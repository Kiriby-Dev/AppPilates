package com.example.apppilates.Logica.Interfaces;

import com.example.apppilates.Logica.DTCliente;

public interface IControladorClientes {
    public abstract void altaCliente(String nombre, String apellido, String telefono, float cuota, boolean pago);
    public abstract DTCliente buscarCliente(String nombre);
    public abstract String[] listarClientes();
}
