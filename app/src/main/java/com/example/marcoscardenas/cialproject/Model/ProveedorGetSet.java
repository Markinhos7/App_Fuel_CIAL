package com.example.marcoscardenas.cialproject.Model;

/**
 * Created by marcoscardenas on 13-01-17.
 */
public class ProveedorGetSet {

    String rut, razon_social, usuario_petroleo;

    public ProveedorGetSet(String rut, String razon_social ,String usuario_petroleo) {
        this.razon_social = razon_social;
        this.rut = rut;
        this.usuario_petroleo = usuario_petroleo;
    }

    public ProveedorGetSet() {
    }

    public String getUsuario_petroleo() {
        return usuario_petroleo;
    }

    public void setUsuario_petroleo(String usuario_petroleo) {
        this.usuario_petroleo = usuario_petroleo;
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
