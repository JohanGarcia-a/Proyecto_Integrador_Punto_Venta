package modelo;

import modelogenerico.Entidad;

/**
 * Entidad que representa una clasificación lógica para los productos del
 * almacén.
 * <p>
 * Implementa la interfaz {@link Entidad} para compatibilidad con el controlador
 * genérico. Además de los datos básicos, esta clase tiene la capacidad de
 * almacenar un dato derivado (conteo de productos) para mostrar estadísticas
 * rápidas en la interfaz.
 * </p>
 * 
 * @version 1.0
 */
public class Categorias implements Entidad {

	/** Identificador único de la categoría (PK). */
	private int id;

	/** Nombre descriptivo de la categoría (ej. "Bebidas", "Lácteos"). */
	private String nombre;

	/**
	 * * Valor calculado que representa la cantidad de productos asociados a esta
	 * categoría.
	 * <p>
	 * Este valor no se almacena en la tabla de categorías, sino que se obtiene
	 * mediante consultas SQL con {@code COUNT} y {@code GROUP BY} en el DAO.
	 * </p>
	 */
	private int conteoCalculado;

	/**
	 * Constructor completo.
	 * <p>
	 * Se utiliza principalmente en las consultas de lectura donde se desea mostrar
	 * al usuario cuántos productos existen en cada categoría.
	 * </p>
	 * 
	 * @param id              Identificador de la categoría.
	 * @param nombre          Nombre de la categoría.
	 * @param conteoCalculado Número de productos asociados.
	 */
	public Categorias(int id, String nombre, int conteoCalculado) {
		this.id = id;
		this.nombre = nombre;
		this.conteoCalculado = conteoCalculado;
	}

	/**
	 * Constructor básico para creación o edición.
	 * <p>
	 * Se utiliza cuando se crea una nueva categoría desde la interfaz, donde el
	 * conteo de productos es irrelevante o es cero por defecto.
	 * </p>
	 * 
	 * @param id     Identificador de la categoría (o -1 si es nueva).
	 * @param nombre Nombre de la categoría.
	 */
	public Categorias(int id, String nombre) {
		this.id = id;
		this.nombre = nombre;
		this.conteoCalculado = 0;
	}

	// --- Getters y Setters ---

	public String getNombre() {
		return nombre;
	}

	/**
	 * Obtiene el número de productos asociados a esta categoría.
	 * 
	 * @return Cantidad de productos (0 si no se cargó con el constructor completo).
	 */
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

	/**
	 * Genera un arreglo de objetos para representar la fila en la tabla de gestión.
	 * 
	 * @return Arreglo con: [ID, Nombre, Conteo de Productos].
	 */
	@Override
	public Object[] toTableRow() {
		// Este es para el panel de gestión de categorías
		return new Object[] { this.id, this.nombre, this.conteoCalculado };
	}

	/**
	 * Devuelve una representación en cadena formateada para componentes de UI.
	 * <p>
	 * El formato es: <b>"Nombre (Conteo)"</b>. Esto es especialmente útil para los
	 * {@code JComboBox} en el módulo de Almacén, permitiendo al usuario ver el
	 * nombre de la categoría y cuántos productos tiene al mismo tiempo.
	 * </p>
	 * 
	 * @return Cadena formateada.
	 */
	@Override
	public String toString() {
		return this.nombre + " (" + this.conteoCalculado + ")";
	}
}