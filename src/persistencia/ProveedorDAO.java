package persistencia;

import java.util.ArrayList;
import java.util.List;

import conexion.Conexion;
import modelo.BaseDatos;
import modelo.Proveedor;
import modelogenerico.BaseDAO;

/**
 * Clase de Acceso a Datos (DAO) para la entidad {@link Proveedor}.
 * <p>
 * Gestiona las operaciones CRUD sobre la tabla <b>TablaProveedores</b>. Esta
 * clase utiliza la clase de utilidad {@link BaseDatos} para abstraer la
 * complejidad de las consultas JDBC directas.
 * </p>
 * * @version 1.0
 */
public class ProveedorDAO implements BaseDAO<Proveedor> {

	/**
	 * Busca un proveedor por su identificador único. * @param id Identificador del
	 * proveedor (Pid).
	 * 
	 * @return Objeto {@link Proveedor} encontrado o {@code null} si no existe.
	 */
	@Override
	public Proveedor buscarPorID(int id) {
		// 1. Instanciamos BaseDatos con la conexión activa
		BaseDatos bd = new BaseDatos(Conexion.getConexion());

		// 2. Ejecutamos la consulta
		// Nota: Utiliza la utilidad genérica para mapear el resultado
		ArrayList<Object[]> resultados = bd.consultar("TablaProveedores", "Pid, NombreP, NumeroTel", "Pid = " + id);

		// 3. Convertimos el resultado genérico (Object[]) a un objeto concreto
		// (Proveedor)
		if (!resultados.isEmpty()) {
			Object[] fila = resultados.get(0);
			return new Proveedor((int) fila[0], // Pid
					(String) fila[1], // NombreP
					(String) fila[2] // NumeroTel
			);
		}
		return null; // Si no se encontró nada
	}

	/**
	 * Recupera el catálogo completo de proveedores registrados.
	 * <p>
	 * Utilizado para llenar los ComboBox en la gestión de productos y pedidos.
	 * </p>
	 * * @return Lista de todos los proveedores.
	 */
	@Override
	public List<Proveedor> ObtenerTodo() {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());
		List<Proveedor> listaProveedores = new ArrayList<>();

		// Consulta sin condición (null) para traer todo
		ArrayList<Object[]> resultados = bd.consultar("TablaProveedores", "Pid, NombreP, NumeroTel", null);

		// Recorremos los resultados genéricos y los convertimos a objetos Proveedor
		for (Object[] fila : resultados) {
			listaProveedores.add(new Proveedor((int) fila[0], (String) fila[1], (String) fila[2]));
		}
		return listaProveedores;
	}

	/**
	 * Registra un nuevo proveedor en el sistema. * @param entidad Objeto con los
	 * datos del proveedor (Nombre, Teléfono).
	 * 
	 * @return {@code true} si la inserción fue exitosa.
	 */
	@Override
	public boolean agregar(Proveedor entidad) {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());

		// Preparamos los valores en un arreglo de Objetos
		Object[] valores = { entidad.getNombre(), entidad.getNumTel() };

		// Llamamos a insertar. Nos devuelve el ID generado automáticamente por la BD.
		int idGenerado = bd.insertar("TablaProveedores", "NombreP, NumeroTel", valores);

		if (idGenerado != -1) {
			entidad.setid(idGenerado); // Actualizamos el objeto con su nuevo ID
			return true;
		}
		return false;
	}

	/**
	 * Actualiza la información de un proveedor existente. * @param entidad Objeto
	 * con los datos modificados.
	 * 
	 * @return {@code true} si la actualización fue correcta.
	 */
	@Override
	public boolean modificar(Proveedor entidad) {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());

		// Valores a actualizar
		Object[] valores = { entidad.getNombre(), entidad.getNumTel() };

		// El método 'modificar' espera: tabla, asignaciones SQL (con ?), condición
		// WHERE, valores
		return bd.modificar("TablaProveedores", "NombreP=?, NumeroTel=?", "Pid=" + entidad.getid(), valores);
	}

	/**
	 * Elimina un proveedor de la base de datos.
	 * <p>
	 * <b>Nota:</b> Esta operación fallará si el proveedor tiene productos asignados
	 * en el inventario o historial de órdenes de compra, debido a restricciones de
	 * integridad (FK).
	 * </p>
	 * * @param id Identificador del proveedor a borrar.
	 * 
	 * @return {@code true} si se eliminó el registro.
	 */
	@Override
	public boolean borrar(int id) {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());
		// El método 'eliminar' espera: tabla, condicion SQL (con ?), valor del
		// parámetro
		return bd.eliminar("TablaProveedores", "Pid = ?", id);
	}
}