package com.example.marcoscardenas.cialproject.Model;

/**
 * Created by Marcos on 16-03-17.
 */

public class UsuarioGetSet {
    String usuario;
    String password;
    String id;
    int vigente;

    public UsuarioGetSet(String usuario, String password,String id , int vigente) {
        this.usuario = usuario;
        this.password = password;
        this.id = id;
        this.vigente = vigente;
    }

    public UsuarioGetSet() {
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVigente() {
        return vigente;
    }
    public void setVigente(int vigente) {
        this.vigente = vigente;
    }
}
