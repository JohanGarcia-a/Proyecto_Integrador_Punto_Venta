package persistencia;

import java.util.ArrayList;
import java.util.List;

import conexion.Conexion;
import modelo.BaseDatos;
import modelo.Clientes;
import modelogenerico.BaseDAO;

public class ClienteDAO implements BaseDAO<Clientes> {

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

	@Override
	public boolean modificar(Clientes entidad) {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());

		Object[] valores = { entidad.getNombre(), entidad.getNumTel() };

		// Actualizamos
		return bd.modificar("TablaClientes", "NombreC=?, NumeroTel=?", "Cid=" + entidad.getid(), valores);
	}

	@Override
	public boolean borrar(int id) {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());
		return bd.eliminar("TablaClientes", "Cid = ?", id);
	}
}