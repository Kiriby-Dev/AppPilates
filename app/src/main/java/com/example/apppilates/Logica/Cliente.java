package com.example.apppilates.Logica;

public class Cliente {
    private String nombre;
    private String apellido;
    private String telefono;
    private float cuota;
    private boolean pago;

    public Cliente(String nombre, String apellido, String telefono, float cuota, boolean pago){
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.cuota = cuota;
        this.pago = false;
    }

    public String getNombre(){
        return this.nombre;
    }

    public String getApellido(){
        return this.apellido;
    }

    public String getTelefono(){
        return this.telefono;
    }

    public float getCuota(){
        return this.cuota;
    }

    public boolean getPago(){
        return this.pago;
    }
}

