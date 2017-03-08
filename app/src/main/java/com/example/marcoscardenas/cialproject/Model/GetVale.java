package com.example.marcoscardenas.cialproject.Model;

import java.math.BigInteger;

/**
 * Created by Marcos on 17-02-17.
 */

public class GetVale {
    public int id;
    public  int MES_PROCESO;
    public  int SURTIDOR ;
    public  int VEHICULO;
    public  int OBRA ;
    public  String RECIBE ;
    public  String USUARIO ;
    public  String FECHA ;
    public   int VALE ;
    public  int GUIA ;
    public  int SELLO ;
    public  int HOROMETRO ;
    public  int KILOMETRO ;
    public  String OBSERVACIONES ;

    public GetVale() {
    }

    public GetVale(int id, int SURTIDOR, int MES_PROCESO, int VEHICULO, int OBRA, String RECIBE, String USUARIO, String FECHA, int VALE, int GUIA,
                   int SELLO, int HOROMETRO, int KILOMETRO, String OBSERVACIONES) {
        this.id = id;
        this.SURTIDOR = SURTIDOR;
        this.MES_PROCESO = MES_PROCESO;
        this.VEHICULO = VEHICULO;
        this.OBRA = OBRA;
        this.RECIBE = RECIBE;
        this.USUARIO = USUARIO;
        this.FECHA = FECHA;
        this.VALE = VALE;
        this.GUIA = GUIA;
        this.SELLO = SELLO;
        this.HOROMETRO = HOROMETRO;
        this.KILOMETRO = KILOMETRO;
        this.OBSERVACIONES = OBSERVACIONES;
    }

    public int getMES_PROCESO() {
        return MES_PROCESO;
    }

    public void setMES_PROCESO(int MES_PROCESO) {
        this.MES_PROCESO = MES_PROCESO;
    }

    public int getSURTIDOR() {
        return SURTIDOR;
    }

    public void setSURTIDOR(int SURTIDOR) {
        this.SURTIDOR = SURTIDOR;
    }

    public int getVEHICULO() {
        return VEHICULO;
    }

    public void setVEHICULO(int VEHICULO) {
        this.VEHICULO = VEHICULO;
    }

    public int getOBRA() {
        return OBRA;
    }

    public void setOBRA(int OBRA) {
        this.OBRA = OBRA;
    }

    public String getRECIBE() {
        return RECIBE;
    }

    public void setRECIBE(String RECIBE) {
        this.RECIBE = RECIBE;
    }

    public String getUSUARIO() {
        return USUARIO;
    }

    public void setUSUARIO(String USUARIO) {
        this.USUARIO = USUARIO;
    }

    public String getFECHA() {
        return FECHA;
    }

    public void setFECHA(String FECHA) {
        this.FECHA = FECHA;
    }

    public int getVALE() {
        return VALE;
    }

    public void setVALE(int VALE) {
        this.VALE = VALE;
    }

    public int getGUIA() {
        return GUIA;
    }

    public void setGUIA(int GUIA) {
        this.GUIA = GUIA;
    }

    public int getSELLO() {
        return SELLO;
    }

    public void setSELLO(int SELLO) {
        this.SELLO = SELLO;
    }

    public int getHOROMETRO() {
        return HOROMETRO;
    }

    public void setHOROMETRO(int HOROMETRO) {
        this.HOROMETRO = HOROMETRO;
    }

    public int getKILOMETRO() {
        return KILOMETRO;
    }

    public void setKILOMETRO(int KILOMETRO) {
        this.KILOMETRO = KILOMETRO;
    }

    public String getOBSERVACIONES() {
        return OBSERVACIONES;
    }

    public void setOBSERVACIONES(String OBSERVACIONES) {
        this.OBSERVACIONES = OBSERVACIONES;
    }
}
