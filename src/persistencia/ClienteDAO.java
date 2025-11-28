package persistencia;

import java.util.ArrayList;
import java.util.List;

import conexion.Conexion;
import modelo.BaseDatos;
import modelo.Clientes;
import modelogenerico.BaseDAO;

/**
 * Clase de Acceso a Datos (DAO) para la entidad {@link Clientes}.
 * <p>
 * Gestiona las operaciones de lectura y escritura en la tabla
 * <b>TablaClientes</b> de la base de datos. Permite la administración del
 * catálogo de clientes para su selección en el módulo de ventas.
 * </p>
 * 
 * @version 1.0
 */
public class ClienteDAO implements BaseDAO<Clientes> {

	/**
	 * Busca un cliente específico mediante su clave primaria.
	 * <p>
	 * Recupera los datos (ID, Nombre y Teléfono) para instanciar el objeto.
	 * </p>
	 * 
	 * @param id Identificador único del cliente (Cid).
	 * @return Objeto {@link Clientes} con la información encontrada o {@code null}
	 *         si no existe.
	 */
	@Override
	public Clientes buscarPorID(int id) {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());

		// Consultamos la tabla clientes
		ArrayList<Object[]> resultados = bd.consultar("TablaClientes", "Cid, NombreC, NumeroTel", "Cid = " + id);

		if (!resultados.isEmpty()) {
			Object[] fila = resultados.get(0);
			return new Clientes((int) fila[0], // Cid
					(String) fila[1], // NombreC
					(String) fila[2] // NumeroTel
			);
		}
		return null;
	}

	/**
	 * Recupera todos los clientes registrados en el sistema.
	 * <p>
	 * Utilizado para llenar las tablas de gestión y los menús desplegables
	 * (ComboBox) en el punto de venta.
	 * </p>
	 * 
	 * @return Lista completa de objetos {@link Clientes}.
	 */
	@Override
	public List<Clientes> ObtenerTodo() {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());
		List<Clientes> lista = new ArrayList<>();

		// Traemos todos los clientes
		ArrayList<Object[]> resultados = bd.consultar("TablaClientes", "Cid, NombreC, NumeroTel", null);

		for (Object[] fila : resultados) {
			lista.add(new Clientes((int) fila[0], (String) fila[1], (String) fila[2]));
		}
		return lista;
	}

	/**
	 * Inserta un nuevo cliente en la base de datos.
	 * <p>
	 * Utiliza el método {@code insertar} de {@link BaseDatos} para obtener el ID
	 * autogenerado y asignarlo al objeto entidad.
	 * </p>
	 * 
	 * @param entidad Objeto con el Nombre y Teléfono a guardar.
	 * @return {@code true} si el registro fue exitoso.
	 */
	@Override
	public boolean agregar(Clientes entidad) {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());

		Object[] valores = { entidad.getNombre(), entidad.getNumTel() };

		// Insertamos y recuperamos el ID generado
		int idGenerado = bd.insertar("TablaClientes", "NombreC, NumeroTel", valores);

		if (idGenerado != -1) {
			entidad.setid(idGenerado);
			return true;
		}
		return false;
	}

	/**
	 * Actualiza la información de un cliente existente.
	 * 
	 * @param entidad Objeto con los datos modificados y el ID original.
	 * @return {@code true} si se actualizó el registro correctamente.
	 */
	@Override
	public boolean modificar(Clientes entidad) {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());

		Object[] valores = { entidad.getNombre(), entidad.getNumTel() };

		// Actualizamos
		return bd.modificar("TablaClientes", "NombreC=?, NumeroTel=?", "Cid=" + entidad.getid(), valores);
	}

	/**
	 * Elimina un cliente de la base de datos.
	 * <p>
	 * <b>Nota:</b> Si el cliente ya tiene historial de ventas asociadas en
	 * {@code TablaVentas}, la base de datos podría impedir la eliminación por
	 * integridad referencial (Foreign Key).
	 * </p>
	 * 
	 * @param id Identificador del cliente a borrar.
	 * @return {@code true} si la eliminación fue exitosa.
	 */
	@Override
	public boolean borrar(int id) {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());
		return bd.eliminar("TablaClientes", "Cid = ?", id);
	}
}