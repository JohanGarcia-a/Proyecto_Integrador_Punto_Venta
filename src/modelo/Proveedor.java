package modelo;

import modelogenerico.Entidad;

/**
 * Entidad que representa a un proveedor o suministrador de productos.
 * <p>
 * Mapea la tabla <b>TablaProveedores</b> de la base de datos. Esta clase es
 * fundamental para la gestión de inventario, ya que cada producto está
 * vinculado a un proveedor, y para el módulo de compras, donde se generan
 * pedidos dirigidos a estas entidades.
 * </p>
 * * @version 1.0
 */
public class Proveedor implements Entidad {

	/** Identificador único del proveedor (PK). */
	private int id;

	/** Nombre o Razón Social del proveedor. */
	private String nombre;

	/** Número de teléfono de contacto o atención a clientes. */
	private String numTel;

	/**
	 * Constructor completo para instanciar un proveedor.
	 * 
	 * @param id     Identificador único (o -1 si es nuevo).
	 * @param nombre Nombre de la empresa o proveedor.
	 * @param numTel Teléfono de contacto.
	 */
	public Proveedor(int id, String nombre, String numTel) {
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
	 * Genera un arreglo de objetos para visualizar al proveedor en la tabla de
	 * gestión.
	 * <p>
	 * Se utiliza en {@code PanelProveedor} y por el controlador genérico.
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
	 * Devuelve el nombre del proveedor.
	 * <p>
	 * <b>Importante:</b> Este método es vital para los componentes
	 * {@code JComboBox}. Permite que al seleccionar un proveedor en el módulo de
	 * "Crear Pedido" o "Alta de Producto", se vea el nombre legible en lugar de la
	 * referencia del objeto.
	 * </p>
	 * 
	 * @return Nombre del proveedor.
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