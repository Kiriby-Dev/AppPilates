package com.example.apppilates.Logica;

import java.util.HashMap;
import java.util.Map;

public class ManejadorClientes {
    private Map<String, Cliente> clientes;

    private static ManejadorClientes instancia = null;

    private ManejadorClientes() {
        clientes = new HashMap<String, Cliente>();
    }

    public static ManejadorClientes getInstance() {
        if (instancia == null) {
            instancia = new ManejadorClientes();
        }

        return instancia;
    }

    public void addCliente(Cliente cliente) {
        String nombre = cliente.getNombre();
        clientes.put(nombre, cliente);
        System.out.println(clientes.size());
    }

    public Cliente obtenerCliente(String nombre) {
        return (Cliente) clientes.get(nombre);
    }

    public String[] listarClientes(){
        String[] nombresClientes;

        // Verificamos si el mapa de clientes no está vacío
        if(clientes != null && !clientes.isEmpty()){
            int size = clientes.size();
            nombresClientes = new String[size];
            int i = 0;

            // Recorremos el mapa y obtenemos los nombres de los clientes
            for(Map.Entry<String, Cliente> entry : clientes.entrySet()){
                String nombreCliente = entry.getKey();
                nombresClientes[i] = nombreCliente;
                i++;
            }
        } else {
            // En caso de que no haya clientes, devolvemos un array vacío o null
            nombresClientes = new String[0]; // También podrías devolver null si prefieres
        }

        return nombresClientes;
    }


}

