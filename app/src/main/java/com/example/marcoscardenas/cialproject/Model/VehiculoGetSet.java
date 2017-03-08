package com.example.marcoscardenas.cialproject.Model;

public class VehiculoGetSet {

	String patente,nombre_vehiculo;
	int codigo;

	public VehiculoGetSet() {
	}

	public VehiculoGetSet(String patente, int codigo, String nombre_vehiculo) {
		this.patente = patente;
		this.codigo = codigo;
		this.nombre_vehiculo = nombre_vehiculo;
	}

	public String getPatente() {
		return patente;
	}

	public void setPatente(String patente) {
		this.patente = patente;
	}

	public String getNombre_vehiculo() {
		return nombre_vehiculo;
	}

	public void setNombre_vehiculo(String nombre_vehiculo) {
		this.nombre_vehiculo = nombre_vehiculo;
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}
}
