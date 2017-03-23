package com.example.marcoscardenas.cialproject.Model;

/**
 * Created by marcoscardenas on 17-01-17.
 */

public class SurtidorGetSet {
    String descripcion;
    String codigo;
    int vigente;


    public SurtidorGetSet() {
    }
    public SurtidorGetSet(String descripcion, String codigo, int vigente) {
        this.descripcion = descripcion;
        this.codigo = codigo;
        this.vigente = vigente;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getVigente() {
        return vigente;
    }

    public void setVigente(int vigente) {
        this.vigente = vigente;
    }
}
