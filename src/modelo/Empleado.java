package modelo;

import modelogenerico.Entidad;

public class Empleado implements Entidad {
	private int id;
	private String nombre;
	private String numTel;
	private String rol;
	private String contraseña;

	public Empleado(int id, String nombre, String numTel, String rol, String contraseña) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.numTel = numTel;
		this.rol = rol;
		this.contraseña = contraseña;
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

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	public String getContraseña() {
		return contraseña;
	}

	public void setContraseña(String contraseña) {
		this.contraseña = contraseña;
	}

	@Override
	public Object[] toTableRow() {

		return new Object[] { this.id, this.nombre, this.numTel, this.rol };
	}

	@Override
	public String toString() {
		return nombre;
	}

}
