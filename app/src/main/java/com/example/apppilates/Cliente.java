package com.example.apppilates;

public class Cliente {
    private String nombre;
    private String cedula;
    private String cuota;
    private String fecha;

    public Cliente(String nombre, String cedula, String cuota, String fecha) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.cuota = cuota;
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCedula() {
        return cedula;
    }

    public String getCuota() {
        return cuota;
    }
    public String getFecha() {
        return fecha;
    }
}
