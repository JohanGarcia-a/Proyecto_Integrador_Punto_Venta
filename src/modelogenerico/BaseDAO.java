package modelogenerico;

import java.util.List;

/**
 * Interfaz genérica que define las operaciones CRUD estándar para el acceso a
 * datos.
 * <p>
 * Utiliza <b>Genéricos de Java</b> ({@code <T extends Entidad>}) para asegurar
 * que estas operaciones solo se realicen sobre objetos que implementen la
 * interfaz {@link Entidad}.
 * </p>
 * 
 * @param <T> El tipo de clase del modelo (ej. Cliente, Producto) sobre el cual
 *            operará el DAO.
 * @version 1.0
 */
public interface BaseDAO<T extends Entidad> {

	/**
	 * Busca un registro específico por su identificador único.
	 * 
	 * @param id La clave primaria del registro a buscar.
	 * @return El objeto encontrado o {@code null} si no existe.
	 */
	T buscarPorID(int id);

	/**
	 * Recupera todos los registros existentes en la tabla correspondiente.
	 * 
	 * @return Una lista conteniendo todos los objetos encontrados.
	 */
	List<T> ObtenerTodo();

	/**
	 * Inserta un nuevo registro en la base de datos.
	 * 
	 * @param entidad El objeto con los datos a guardar.
	 * @return {@code true} si la operación fue exitosa.
	 */
	boolean agregar(T entidad);

	/**
	 * Actualiza la información de un registro existente.
	 * 
	 * @param entidad El objeto con los datos modificados y su ID original.
	 * @return {@code true} si la actualización fue exitosa.
	 */
	boolean modificar(T entidad);

	/**
	 * Elimina un registro de la base de datos.
	 * 
	 * @param id El identificador del registro a borrar.
	 * @return {@code true} si la eliminación fue exitosa.
	 */
	boolean borrar(int id);

}