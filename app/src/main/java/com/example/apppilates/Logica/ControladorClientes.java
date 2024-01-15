package com.example.apppilates.Logica;

import com.example.apppilates.Logica.Interfaces.IControladorClientes;

public class ControladorClientes implements IControladorClientes {

    public void altaCliente(String nombre, String apellido, String telefono, float cuota, boolean pago){
        ManejadorClientes mc = ManejadorClientes.getInstance();
        Cliente cliente = new Cliente(nombre, apellido, telefono, cuota, false);
        mc.addCliente(cliente);
    }

    public DTCliente buscarCliente(String nombre){
        ManejadorClientes mc = ManejadorClientes.getInstance();
        Cliente cliente = mc.obtenerCliente(nombre);
        DTCliente datosCliente = new DTCliente(cliente.getNombre(), cliente.getApellido(), cliente.getTelefono(), cliente.getCuota(), false);
        return datosCliente;
    }

    public String[] listarClientes(){
        ManejadorClientes mc = ManejadorClientes.getInstance();
        String[] clientes = mc.listarClientes();
        return clientes;
    }

}
