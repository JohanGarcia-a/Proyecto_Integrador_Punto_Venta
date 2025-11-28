package modelo;

import modelogenerico.Entidad;

/**
 * Entidad que representa a un cliente registrado en el sistema.
 * <p>
 * Mapea la tabla <b>TablaClientes</b> de la base de datos. Implementa la
 * interfaz {@link Entidad} para ser gestionada por el controlador genérico en
 * los módulos de administración y para ser seleccionada en el punto de venta.
 * </p>
 * 
 * @version 1.0
 */
public class Clientes implements Entidad {

	/** Identificador único del cliente (PK). */
	private int id;

	/** Nombre completo o razón social del cliente. */
	private String nombre;

	/** Número de teléfono de contacto. */
	private String numTel;

	/**
	 * Constructor completo para instanciar un cliente.
	 * 
	 * @param id     Identificador único (o -1 si es nuevo).
	 * @param nombre Nombre del cliente.
	 * @param numTel Número de teléfono.
	 */
	public Clientes(int id, String nombre, String numTel) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.numTel = numTel;
	}

	// --- Implementación de la Interfaz Entidad ---

	@Override
	public int getid() {
		return id;
	}

	@Override
	public void setid(int id) {
		this.id = id;
	}

	/**
	 * Genera un arreglo de objetos para representar al cliente en una tabla.
	 * <p>
	 * Se utiliza en {@code PanelCliente} para llenar el modelo de la JTable.
	 * </p>
	 * 
	 * @return Arreglo con: [ID, Nombre, Teléfono].
	 */
	@Override
	public Object[] toTableRow() {
		return new Object[] { this.id, this.nombre, this.numTel };
	}

	// --- Métodos de la clase Object ---

	/**
	 * Devuelve el nombre del cliente como representación en texto.
	 * <p>
	 * <b>Importante:</b> Este método es utilizado automáticamente por los
	 * componentes {@code JComboBox} en la interfaz de Ventas. Si se elimina, el
	 * combo mostraría la referencia de memoria en lugar del nombre legible.
	 * </p>
	 * 
	 * @return El nombre del cliente.
	 */
	@Override
	public String toString() {
		return nombre;
	}

	// --- Getters y Setters Estándar ---

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
}