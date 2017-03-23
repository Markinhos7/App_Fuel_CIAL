package com.example.marcoscardenas.cialproject.Model;

/**
 * Created by Marcos on 16-03-17.
 */

public class UsuarioGetSet {
    String usuario;
    String password;
    int id;

    public UsuarioGetSet(String usuario, String password, int id) {
        this.usuario = usuario;
        this.password = password;
        this.id = id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
