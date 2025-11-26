package persistencia;

import java.util.ArrayList;
import java.util.List;

import conexion.Conexion;
import modelo.BaseDatos;
import modelo.Categorias;
import modelogenerico.BaseDAO;

public class CategoriaDAO implements BaseDAO<Categorias> {

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

	@Override
	public List<Categorias> ObtenerTodo() {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());
		List<Categorias> lista = new ArrayList<>();

		// TRUCO: Como BaseDatos.consultar hace "SELECT [campos] FROM [tabla]
		// [condicion]",
		// y no usamos WHERE, metemos el JOIN y el GROUP BY en la parte de la "tabla".
		String campos = "c.Cid, c.Nombre, COUNT(p.Pid) AS ConteoProductos";
		String tablaCompleja = "TablaCategorias c " + "LEFT JOIN TablaAlmacen_Productos p ON c.Cid = p.CategoriaID "
				+ "GROUP BY c.Cid, c.Nombre " + "ORDER BY c.Nombre";

		ArrayList<Object[]> res = bd.consultar(tablaCompleja, campos, null);

		for (Object[] fila : res) {
			lista.add(new Categorias((int) fila[0], // Cid
					(String) fila[1], // Nombre
					(int) fila[2] // ConteoProductos
			));
		}
		return lista;
	}

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

	@Override
	public boolean modificar(Categorias entidad) {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());
		Object[] valores = { entidad.getNombre() };

		return bd.modificar("TablaCategorias", "Nombre=?", "Cid=" + entidad.getid(), valores);
	}

	@Override
	public boolean borrar(int id) {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());
		return bd.eliminar("TablaCategorias", "Cid = ?", id);
	}
}