package com.example.marcoscardenas.cialproject.Model;

/**
 * Created by marcoscardenas on 17-01-17.
 */

public class MesprocesoGetSet {
    String nombre;
    int id;

    public MesprocesoGetSet(int id,String nombre) {
        this.nombre = nombre;
        this.id = id;
    }

    public MesprocesoGetSet() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
