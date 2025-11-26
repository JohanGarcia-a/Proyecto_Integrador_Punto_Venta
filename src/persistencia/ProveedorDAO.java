package persistencia;

import java.util.ArrayList;
import java.util.List;

import conexion.Conexion;
import modelo.BaseDatos; 
import modelo.Proveedor;
import modelogenerico.BaseDAO;

public class ProveedorDAO implements BaseDAO<Proveedor> {

	@Override
	public Proveedor buscarPorID(int id) {
		// 1. Instanciamos BaseDatos con la conexión
		BaseDatos bd = new BaseDatos(Conexion.getConexion());

		// 2. Ejecutamos la consulta (pedimos campos específicos o * si quieres todos)
		// Nota: "Pid = " + id funciona bien aquí.
		ArrayList<Object[]> resultados = bd.consultar("TablaProveedores", "Pid, NombreP, NumeroTel", "Pid = " + id);

		// 3. Convertimos el resultado (si existe) a un objeto Proveedor
		if (!resultados.isEmpty()) {
			Object[] fila = resultados.get(0);
			// Hacemos cast (conversión) de Object a los tipos correctos
			return new Proveedor((int) fila[0], // Pid
					(String) fila[1], // NombreP
					(String) fila[2] // NumeroTel
			);
		}
		return null; // Si no se encontró nada
	}

	@Override
	public List<Proveedor> ObtenerTodo() {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());
		List<Proveedor> listaProveedores = new ArrayList<>();

	
		ArrayList<Object[]> resultados = bd.consultar("TablaProveedores", "Pid, NombreP, NumeroTel", null);

		// Recorremos los resultados genéricos y los convertimos a Proveedores
		for (Object[] fila : resultados) {
			listaProveedores.add(new Proveedor((int) fila[0], (String) fila[1], (String) fila[2]));
		}
		return listaProveedores;
	}

	@Override
	public boolean agregar(Proveedor entidad) {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());

		// Preparamos los valores en un arreglo de Objetos
		Object[] valores = { entidad.getNombre(), entidad.getNumTel() };

		// Llamamos a insertar. Nos devuelve el ID generado.
		int idGenerado = bd.insertar("TablaProveedores", "NombreP, NumeroTel", valores);

		if (idGenerado != -1) {
			entidad.setid(idGenerado); // Actualizamos el objeto con su nuevo ID
			return true;
		}
		return false;
	}

	@Override
	public boolean modificar(Proveedor entidad) {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());

		// Valores a actualizar
		Object[] valores = { entidad.getNombre(), entidad.getNumTel() };

		// OJO: La condición "Pid=" se concatena aquí, los valores van en el array
		// El método 'modificar' espera: tabla, asignaciones(sql), condicion, valores
		return bd.modificar("TablaProveedores", "NombreP=?, NumeroTel=?", "Pid=" + entidad.getid(), valores);
	}

	@Override
	public boolean borrar(int id) {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());
		// El método 'eliminar' espera: tabla, condicion(sql con ?), valor
		return bd.eliminar("TablaProveedores", "Pid = ?", id);
	}
}