package modelo;

import modelogenerico.Entidad;

public class Clientes implements Entidad {
	private int id;
	private String nombre;
	private String numTel;

	public Clientes(int id, String nombre, String numTel) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.numTel = numTel;
	}

	@Override
	public int getid() {
		return id;
	}

	@Override
	public void setid(int id) {
		this.id = id;

	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getNumTel() {
		return numTel;
	}

	public void setNumTel(String numTel) {
		this.numTel = numTel;
	}

	@Override
	public Object[] toTableRow() {

		return new Object[] { this.id, this.nombre, this.numTel };
	}

	@Override
	public String toString() {
		return nombre;
	}

}
