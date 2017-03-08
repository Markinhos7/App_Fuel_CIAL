package com.example.marcoscardenas.cialproject.Model;

/**
 * Created by marcoscardenas on 17-01-17.
 */

public class ObraGetSet {
    String nombre;
    int cod_obra;

    public ObraGetSet(String nombre, int cod_obra) {
        this.nombre = nombre;
        this.cod_obra = cod_obra;
    }

    public ObraGetSet() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCod_obra() {
        return cod_obra;
    }

    public void setCod_obra(int cod_obra) {
        this.cod_obra = cod_obra;
    }
}
