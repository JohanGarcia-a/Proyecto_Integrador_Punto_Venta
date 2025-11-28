package persistencia;

import java.util.ArrayList;
import java.util.List;

import conexion.Conexion;
import modelo.BaseDatos;
import modelo.Categorias;
import modelogenerico.BaseDAO;

/**
 * Clase de Acceso a Datos (DAO) para la entidad {@link Categorias}.
 * <p>
 * Gestiona las operaciones CRUD sobre la tabla <b>TablaCategorias</b>. Además
 * de las operaciones básicas, esta clase incluye lógica SQL avanzada para
 * calcular estadísticas en tiempo real (conteo de productos por categoría).
 * </p>
 * * @version 1.1
 */
public class CategoriaDAO implements BaseDAO<Categorias> {

	/**
	 * Busca una categoría específica por su ID.
	 * <p>
	 * Recupera los datos básicos (ID y Nombre). No realiza el conteo de productos
	 * en esta consulta para mantener la operación ligera.
	 * </p>
	 * * @param id Identificador de la categoría.
	 * 
	 * @return Objeto {@link Categorias} o {@code null} si no existe.
	 */
	@Override
	public Categorias buscarPorID(int id) {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());
		ArrayList<Object[]> res = bd.consultar("TablaCategorias", "Cid, Nombre", "Cid = " + id);

		if (!res.isEmpty()) {
			Object[] fila = res.get(0);
			return new Categorias((int) fila[0], (String) fila[1]);
		}
		return null;
	}

	/**
	 * Obtiene el listado completo de categorías con estadísticas.
	 * <p>
	 * <b>Nota Técnica:</b> Ejecuta una consulta compleja utilizando
	 * {@code LEFT JOIN} con la tabla de productos y una agrupación
	 * {@code GROUP BY}. Esto permite llenar el atributo {@code conteoCalculado} del
	 * modelo, mostrando al usuario cuántos productos existen dentro de cada
	 * categoría (ej. "Bebidas (15)").
	 * </p>
	 * * @return Lista de categorías enriquecida con el conteo de productos.
	 */
	@Override
	public List<Categorias> ObtenerTodo() {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());
		List<Categorias> lista = new ArrayList<>();

		// TRUCO: Como BaseDatos.consultar hace "SELECT [campos] FROM [tabla]
		// [condicion]",
		// inyectamos el JOIN y el GROUP BY en el parámetro de la "tabla" para construir
		// la sentencia SQL completa.
		String campos = "c.Cid, c.Nombre, COUNT(p.Pid) AS ConteoProductos";
		String tablaCompleja = "TablaCategorias c " + "LEFT JOIN TablaAlmacen_Productos p ON c.Cid = p.CategoriaID "
				+ "GROUP BY c.Cid, c.Nombre " + "ORDER BY c.Nombre";

		ArrayList<Object[]> res = bd.consultar(tablaCompleja, campos, null);

		for (Object[] fila : res) {
			lista.add(new Categorias((int) fila[0], // Cid
					(String) fila[1], // Nombre
					(int) fila[2] // ConteoProductos (Resultado del COUNT)
			));
		}
		return lista;
	}

	/**
	 * Inserta una nueva categoría en la base de datos. * @param entidad Objeto con
	 * el nombre de la nueva categoría.
	 * 
	 * @return {@code true} si la inserción fue exitosa.
	 */
	@Override
	public boolean agregar(Categorias entidad) {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());
		Object[] valores = { entidad.getNombre() };

		int id = bd.insertar("TablaCategorias", "Nombre", valores);

		if (id != -1) {
			entidad.setid(id);
			return true;
		}
		return false;
	}

	/**
	 * Actualiza el nombre de una categoría existente. * @param entidad Objeto con
	 * el ID original y el nuevo nombre.
	 * 
	 * @return {@code true} si se realizó la modificación.
	 */
	@Override
	public boolean modificar(Categorias entidad) {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());
		Object[] valores = { entidad.getNombre() };

		return bd.modificar("TablaCategorias", "Nombre=?", "Cid=" + entidad.getid(), valores);
	}

	/**
	 * Elimina una categoría de la base de datos.
	 * <p>
	 * <b>Advertencia de Integridad Referencial:</b> Si la categoría tiene productos
	 * asignados en {@code TablaAlmacen_Productos}, la base de datos podría impedir
	 * la eliminación lanzando una excepción de clave foránea (FK), a menos que esté
	 * configurada con cascada.
	 * </p>
	 * * @param id ID de la categoría a borrar.
	 * 
	 * @return {@code true} si se eliminó correctamente.
	 */
	@Override
	public boolean borrar(int id) {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());
		return bd.eliminar("TablaCategorias", "Cid = ?", id);
	}
}