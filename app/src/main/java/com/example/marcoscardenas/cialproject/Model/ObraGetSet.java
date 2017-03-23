package com.example.marcoscardenas.cialproject.Model;

/**
 * Created by marcoscardenas on 17-01-17.
 */

public class ObraGetSet {
    String nombre;
    String cod_obra;
    String localidad;
    int finalizada;
    int visible_petroleo;

    public ObraGetSet(String nombre, String cod_obra, String localidad, int finalizada, int visible_petroleo) {
        this.nombre = nombre;
        this.cod_obra = cod_obra;
        this.localidad = localidad;
        this.finalizada = finalizada;
        this.visible_petroleo = visible_petroleo;
    }

    public ObraGetSet() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCod_obra() {
        return cod_obra;
    }

    public void setCod_obra(String cod_obra) {
        this.cod_obra = cod_obra;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public int getFinalizada() {
        return finalizada;
    }

    public void setFinalizada(int finalizada) {
        this.finalizada = finalizada;
    }

    public int getVisible_petroleo() {
        return visible_petroleo;
    }

    public void setVisible_petroleo(int visible_petroleo) {
        this.visible_petroleo = visible_petroleo;
    }
}
