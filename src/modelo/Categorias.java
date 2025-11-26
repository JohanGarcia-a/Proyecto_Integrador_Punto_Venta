package modelo;

import modelogenerico.Entidad;

public class Categorias implements Entidad {
	private int id;
	private String nombre;
	private int conteoCalculado;

	public Categorias(int id, String nombre, int conteoCalculado) {
		this.id = id;
		this.nombre = nombre;
		this.conteoCalculado = conteoCalculado;
	}

	// Constructor para cuando creas una nueva categoría (no tiene conteo)
	public Categorias(int id, String nombre) {
		this.id = id;
		this.nombre = nombre;
		this.conteoCalculado = 0; 
	}

	public String getNombre() {
		return nombre;
	}

	public int getConteoCalculado() {
		return conteoCalculado;
	}

	@Override
	public int getid() {
		return this.id;
	}

	@Override
	public void setid(int id) {
		this.id = id;
	}

	@Override
	public Object[] toTableRow() {
		// Este es para el panel de gestión de categorías
		return new Object[] { this.id, this.nombre,this.conteoCalculado };
	}

	 @Override
	    public String toString() {
	        // Devuelve el texto en el formato que queremos: "Nombre (Conteo)"
	        return this.nombre + " (" + this.conteoCalculado + ")";
	    }
}