package com.example.apppilates.Logica;

import com.example.apppilates.Logica.Interfaces.IControladorClientes;

public class Fabrica {
    private static Fabrica instancia;

    private Fabrica() {
    };

    public static Fabrica getInstance() {
        if (instancia == null) {
            instancia = new Fabrica();
        }
        return instancia;
    }

    public IControladorClientes getIControladorClientes() {
        return new ControladorClientes();
    }

}
