package com.example.marcoscardenas.cialproject.Model;

/**
 * Created by marcoscardenas on 17-01-17.
 */

public class MesprocesoGetSet {
    String proceso;
    String id;
    int abierta;
    int mes;
    int ano;


    public MesprocesoGetSet() {
    }

    public MesprocesoGetSet(String proceso, String id, int abierta, int mes, int ano) {
        this.proceso = proceso;
        this.id = id;
        this.abierta = abierta;
        this.mes = mes;
        this.ano = ano;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAbierta() {
        return abierta;
    }

    public void setAbierta(int abierta) {
        this.abierta = abierta;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }
}
