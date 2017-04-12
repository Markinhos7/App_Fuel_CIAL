package com.example.marcoscardenas.cialproject.Model;

import java.math.BigInteger;

/**
 * Created by Marcos on 17-02-17.
 */

public class GetVale {
    public String Id;
    public  int id_mes_proceso;
    public  int id_surtidor ;
    public  int id_vehiculo;
    public  int id_obra ;
    public  String rut_recibe ;
    public  String usuario_crea ;
    public  String fecha_crea ;
    public   int numero_vale ;
    public  int  numero_guia_proveedor;
    public  int numero_sello ;
    public  int contador_hr;
    public  int contador_km;
    public  String observaciones ;
    public int cantidad;

    public GetVale() {

    }

    public GetVale(String id, int id_mes_proceso, int id_surtidor, int id_vehiculo, int id_obra, String rut_recibe, String usuario_crea, String fecha_crea, int numero_vale, int numero_guia_proveedor, int numero_sello, int contador_hr, int contador_km, String observaciones,int cantidad) {
        this.Id = id;
        this.id_mes_proceso = id_mes_proceso;
        this.id_surtidor = id_surtidor;
        this.id_vehiculo = id_vehiculo;
        this.id_obra = id_obra;
        this.rut_recibe = rut_recibe;
        this.usuario_crea = usuario_crea;
        this.fecha_crea = fecha_crea;
        this.numero_vale = numero_vale;
        this.numero_guia_proveedor = numero_guia_proveedor;
        this.numero_sello = numero_sello;
        this.contador_hr = contador_hr;
        this.contador_km = contador_km;
        this.observaciones = observaciones;
        this.cantidad = cantidad;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public int getId_mes_proceso() {
        return id_mes_proceso;
    }

    public void setId_mes_proceso(int id_mes_proceso) {
        this.id_mes_proceso = id_mes_proceso;
    }

    public int getId_surtidor() {
        return id_surtidor;
    }

    public void setId_surtidor(int id_surtidor) {
        this.id_surtidor = id_surtidor;
    }

    public int getId_vehiculo() {
        return id_vehiculo;
    }

    public void setId_vehiculo(int id_vehiculo) {
        this.id_vehiculo = id_vehiculo;
    }

    public int getId_obra() {
        return id_obra;
    }

    public void setId_obra(int id_obra) {
        this.id_obra = id_obra;
    }

    public String getRut_recibe() {
        return rut_recibe;
    }

    public void setRut_recibe(String rut_recibe) {
        this.rut_recibe = rut_recibe;
    }

    public String getUsuario_crea() {
        return usuario_crea;
    }

    public void setUsuario_crea(String usuario_crea) {
        this.usuario_crea = usuario_crea;
    }

    public String getFecha_crea() {
        return fecha_crea;
    }

    public void setFecha_crea(String fecha_crea) {
        this.fecha_crea = fecha_crea;
    }

    public int getNumero_vale() {
        return numero_vale;
    }

    public void setNumero_vale(int numero_vale) {
        this.numero_vale = numero_vale;
    }

    public int getNumero_guia_proveedor() {
        return numero_guia_proveedor;
    }

    public void setNumero_guia_proveedor(int numero_guia_proveedor) {
        this.numero_guia_proveedor = numero_guia_proveedor;
    }

    public int getNumero_sello() {
        return numero_sello;
    }

    public void setNumero_sello(int numero_sello) {
        this.numero_sello = numero_sello;
    }

    public int getContador_hr() {
        return contador_hr;
    }

    public void setContador_hr(int contador_hr) {
        this.contador_hr = contador_hr;
    }

    public int getContador_km() {
        return contador_km;
    }

    public void setContador_km(int contador_km) {
        this.contador_km = contador_km;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
