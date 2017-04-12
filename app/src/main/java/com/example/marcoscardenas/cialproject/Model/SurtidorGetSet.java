package com.example.marcoscardenas.cialproject.Model;

/**
 * Created by marcoscardenas on 17-01-17.
 */

public class SurtidorGetSet {
    String descripcion;
    String codigo;
    int vigente;
    int id_categoria;


    public SurtidorGetSet() {
    }
    public SurtidorGetSet(String descripcion, String codigo, int vigente, int id_categoria) {
        this.descripcion = descripcion;
        this.codigo = codigo;
        this.vigente = vigente;
        this.id_categoria = id_categoria;
    }

    public int getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(int id_categoria) {
        this.id_categoria = id_categoria;
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
