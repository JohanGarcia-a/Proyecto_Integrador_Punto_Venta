package persistencia;

import java.util.ArrayList;
import java.util.List;

import conexion.Conexion;
import modelo.BaseDatos;
import modelo.Empleado;
import modelogenerico.BaseDAO;

/**
 * Clase de Acceso a Datos (DAO) para la entidad {@link Empleado}.
 * <p>
 * Gestiona la persistencia de los usuarios del sistema en la tabla
 * <b>TablaEmpleados</b>. Además de las operaciones CRUD estándar, incluye la
 * lógica crítica de seguridad para la autenticación (Login).
 * </p>
 * * @version 1.1
 */
public class EmpleadoDAO implements BaseDAO<Empleado> {

	/**
	 * Busca un empleado por su identificador único (ID).
	 * <p>
	 * Recupera todos los datos, incluyendo credenciales y rol, necesarios para
	 * instanciar el objeto {@link Empleado}.
	 * </p>
	 * * @param id Identificador del empleado (Eid).
	 * 
	 * @return Objeto {@link Empleado} encontrado o {@code null} si no existe.
	 */
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

	/**
	 * Recupera el listado completo de empleados registrados.
	 * <p>
	 * Utilizado para la gestión administrativa de usuarios
	 * (Alta/Baja/Modificación).
	 * </p>
	 * * @return Lista de todos los empleados.
	 */
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

	/**
	 * Registra un nuevo empleado en el sistema. * @param e Objeto con los datos del
	 * nuevo empleado (Nombre, Teléfono, Rol, Contraseña).
	 * 
	 * @return {@code true} si la inserción fue exitosa y se generó un ID.
	 */
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

	/**
	 * Actualiza los datos de un empleado existente.
	 * <p>
	 * <b>Nota:</b> Actualiza todos los campos, incluyendo el Rol y la Contraseña.
	 * </p>
	 * * @param e Objeto con los datos modificados.
	 * 
	 * @return {@code true} si la actualización fue correcta.
	 */
	@Override
	public boolean modificar(Empleado e) {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());

		Object[] valores = { e.getNombre(), e.getNumTel(), e.getRol(), e.getContraseña() };

		return bd.modificar("TablaEmpleados", "NombreE=?, NumeroTel=?, Rol=?, Password=?", "Eid=" + e.getid(), valores);
	}

	/**
	 * Elimina un empleado de la base de datos. * @param id Identificador del
	 * empleado a borrar.
	 * 
	 * @return {@code true} si se eliminó el registro.
	 */
	@Override
	public boolean borrar(int id) {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());
		return bd.eliminar("TablaEmpleados", "Eid = ?", id);
	}

	// --- MÉTODO ESPECIAL PARA LOGIN ---

	/**
	 * Verifica las credenciales de acceso de un usuario.
	 * <p>
	 * Realiza una consulta buscando coincidencia exacta entre el nombre de usuario
	 * y la contraseña proporcionada. Es el núcleo del módulo de seguridad.
	 * </p>
	 * * @param usuario Nombre de usuario ingresado en la vista Login.
	 * 
	 * @param password Contraseña ingresada.
	 * @return Objeto {@link Empleado} completo (con Rol) si las credenciales son
	 *         válidas, o {@code null} si son incorrectas.
	 */
	public Empleado autenticar(String usuario, String password) {
		BaseDatos bd = new BaseDatos(Conexion.getConexion());

		// Construimos la condición WHERE para buscar por usuario Y contraseña
		String condicion = "NombreE = '" + usuario + "' AND Password = '" + password + "'";

		ArrayList<Object[]> res = bd.consultar("TablaEmpleados", "Eid, NombreE, NumeroTel, Rol, Password", condicion);

		if (!res.isEmpty()) {
			Object[] f = res.get(0);
			// Login exitoso: retornamos el objeto completo para que la sesión tenga el Rol
			// y ID
			return new Empleado((int) f[0], (String) f[1], (String) f[2], (String) f[3], (String) f[4]);
		}
		return null; // Login fallido
	}
}