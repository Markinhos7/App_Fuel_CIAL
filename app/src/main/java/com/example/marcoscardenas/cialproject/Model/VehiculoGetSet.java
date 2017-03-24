package com.example.marcoscardenas.cialproject.Model;

public class VehiculoGetSet {

	String patente,nombre_vehiculo;
	String codigo;
	String fecha;
	String forma_pago;
	String valor_pago;

	public VehiculoGetSet() {
	}

	public VehiculoGetSet(String patente, String nombre_vehiculo, String codigo) {
		this.patente = patente;
		this.nombre_vehiculo = nombre_vehiculo;
		this.codigo = codigo;

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

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getForma_pago() {
		return forma_pago;
	}

	public void setForma_pago(String forma_pago) {
		this.forma_pago = forma_pago;
	}

	public String getValor_pago() {
		return valor_pago;
	}

	public void setValor_pago(String valor_pago) {
		this.valor_pago = valor_pago;
	}
}
