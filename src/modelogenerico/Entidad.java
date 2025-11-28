package modelogenerico;

/**
 * Interfaz base que define el comportamiento mínimo requerido para cualquier
 * objeto del modelo de datos que quiera ser gestionado por el sistema CRUD
 * genérico.
 * <p>
 * Garantiza que todas las clases de negocio (Cliente, Producto, Proveedor)
 * tengan una forma estándar de acceder a su ID y de representarse visualmente
 * en una tabla.
 * </p>
 * 
 * @version 1.0
 */
public interface Entidad {

	/**
	 * Obtiene el identificador único (Clave Primaria) del registro.
	 * 
	 * @return El ID numérico de la entidad.
	 */
	int getid();

	/**
	 * Establece el identificador único del registro.
	 * <p>
	 * Usado principalmente después de una inserción en base de datos para asignar
	 * el ID autogenerado al objeto en memoria.
	 * </p>
	 * 
	 * @param id El nuevo ID numérico.
	 */
	void setid(int id);

	/**
	 * Convierte los datos del objeto en un arreglo de objetos para su
	 * visualización.
	 * <p>
	 * Este método es utilizado por el {@code DefaultTableModel} de las vistas para
	 * llenar las filas de la JTable automáticamente.
	 * </p>
	 * 
	 * @return Arreglo de objetos (Object[]) donde cada elemento corresponde a una
	 *         columna de la tabla.
	 */
	Object[] toTableRow();

}