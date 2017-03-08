package com.example.marcoscardenas.cialproject.Model;

/**
 * Created by marcoscardenas on 17-01-17.
 */

public class SurtidorGetSet {
    String descripcion;
    int codigo;

    public SurtidorGetSet() {
    }

    public SurtidorGetSet(String descripcion, int codigo) {
        this.descripcion = descripcion;
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
