package persistencia;

import java.util.ArrayList;
import java.util.List;

import conexion.Conexion;
import modelo.BaseDatos;
import modelo.Empleado;
import modelogenerico.BaseDAO;

public class EmpleadoDAO implements BaseDAO<Empleado> {

	@Override
	public Empleado buscarPorID(int id) {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());
		ArrayList<Object[]> res = bd.consultar("TablaEmpleados", "Eid, NombreE, NumeroTel, Rol, Password",
				"Eid = " + id);

		if (!res.isEmpty()) {
			Object[] f = res.get(0);
			return new Empleado((int) f[0], // Eid
					(String) f[1], // NombreE
					(String) f[2], // NumeroTel
					(String) f[3], // Rol
					(String) f[4] // Password
			);
		}
		return null;
	}

	@Override
	public List<Empleado> ObtenerTodo() {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());
		List<Empleado> lista = new ArrayList<>();
		ArrayList<Object[]> res = bd.consultar("TablaEmpleados", "Eid, NombreE, NumeroTel, Rol, Password", null);

		for (Object[] f : res) {
			lista.add(new Empleado((int) f[0], (String) f[1], (String) f[2], (String) f[3], (String) f[4]));
		}
		return lista;
	}

	@Override
	public boolean agregar(Empleado e) {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());

		Object[] valores = { e.getNombre(), e.getNumTel(), e.getRol(), e.getContraseña() };

		int id = bd.insertar("TablaEmpleados", "NombreE, NumeroTel, Rol, Password", valores);

		if (id != -1) {
			e.setid(id);
			return true;
		}
		return false;
	}

	@Override
	public boolean modificar(Empleado e) {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());

		Object[] valores = { e.getNombre(), e.getNumTel(), e.getRol(), e.getContraseña() };

		return bd.modificar("TablaEmpleados", "NombreE=?, NumeroTel=?, Rol=?, Password=?", "Eid=" + e.getid(), valores);
	}

	@Override
	public boolean borrar(int id) {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());
		return bd.eliminar("TablaEmpleados", "Eid = ?", id);
	}

	// --- MÉTODO ESPECIAL PARA LOGIN ---
	public Empleado autenticar(String usuario, String password) {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());

		// Construimos la condición WHERE para buscar por usuario Y contraseña
		// NOTA: Para mayor seguridad en el futuro, evita concatenar strings directos,
		// pero por ahora mantenemos la lógica funcional con tu BaseDatos.
		String condicion = "NombreE = '" + usuario + "' AND Password = '" + password + "'";

		ArrayList<Object[]> res = bd.consultar("TablaEmpleados", "Eid, NombreE, NumeroTel, Rol, Password", condicion);

		if (!res.isEmpty()) {
			Object[] f = res.get(0);
			// Login exitoso: retornamos el objeto completo
			return new Empleado((int) f[0], (String) f[1], (String) f[2], (String) f[3], (String) f[4]);
		}
		return null; // Login fallido
	}
}