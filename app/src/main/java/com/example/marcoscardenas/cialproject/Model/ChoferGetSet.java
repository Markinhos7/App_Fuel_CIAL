package com.example.marcoscardenas.cialproject.Model;

/**
 * Created by marcoscardenas on 13-01-17.
 */
public class ChoferGetSet {

    String rut, razon_social;

    public ChoferGetSet(String rut, String razon_social) {
        this.razon_social = razon_social;
        this.rut = rut;
    }

    public ChoferGetSet() {
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getRazon_social() {
        return razon_social;
    }

    public void setRazon_social(String razon_social) {
        this.razon_social = razon_social;
    }
}
