package controlador;

import modelo.Empleado;
import persistencia.EmpleadoDAO;
import vista.Principal;
import vista.VistaLogin;

/**
 * Controlador encargado de gestionar el proceso de inicio de sesión (Login) y
 * seguridad inicial.
 * <p>
 * Coordina la interacción entre la ventana de login ({@link VistaLogin}) y la
 * capa de persistencia ({@link EmpleadoDAO}) para verificar las credenciales
 * del usuario.
 * </p>
 * <p>
 * Es el punto de entrada lógico de la aplicación; si la autenticación es
 * exitosa, se encarga de instanciar la ventana principal ({@link Principal})
 * inyectando la sesión del usuario.
 * </p>
 * 
 * @version 1.0
 */
public class ControladorLogin {

	/** Referencia a la ventana gráfica de login. */
	private VistaLogin vistaLogin;

	/** DAO para verificar credenciales contra la base de datos. */
	private EmpleadoDAO empleadoDAO;

	/**
	 * Constructor principal.
	 * <p>
	 * Inicializa las referencias y asigna el comportamiento (Listener) al botón de
	 * "Ingresar" de la vista para que responda a los clics del usuario.
	 * </p>
	 * 
	 * @param vista Instancia de la ventana de login ya creada.
	 * @param dao   Instancia del DAO de empleados para realizar consultas.
	 */
	public ControladorLogin(VistaLogin vista, EmpleadoDAO dao) {
		this.vistaLogin = vista;
		this.empleadoDAO = dao;

		// Añadimos el 'escuchador' al botón de la vista.
		// Cuando se haga clic o se presione Enter, se ejecutará el método
		// 'autenticarUsuario'.
		this.vistaLogin.addLoginListener(e -> autenticarUsuario());
	}

	/**
	 * Ejecuta la lógica de autenticación y control de flujo.
	 * <p>
	 * <b>Flujo de ejecución:</b>
	 * <ol>
	 * <li>Obtiene el usuario y contraseña de los campos de texto de la vista.</li>
	 * <li>Valida que los campos no estén vacíos (Validación de Interfaz).</li>
	 * <li>Llama al método {@code autenticar} del DAO para verificar en la BD.</li>
	 * <li><b>Si es exitoso:</b> Cierra la ventana de login y abre la ventana
	 * {@link Principal}, pasando el objeto {@link Empleado} logueado.</li>
	 * <li><b>Si falla:</b> Muestra un mensaje de error al usuario y no permite el
	 * acceso.</li>
	 * </ol>
	 * </p>
	 */
	private void autenticarUsuario() {
		// 1. Obtenemos los datos que el usuario escribió en la vista.
		String usuario = vistaLogin.getUsuario();
		String password = vistaLogin.getPassword();

		// 2. Validamos que los campos no estén vacíos.
		if (usuario.isEmpty() || password.isEmpty()) {
			vistaLogin.mostrarError("El usuario y la contraseña no pueden estar vacíos.");
			return;
		}

		// 3. Llamamos al DAO para que verifique las credenciales en la base de datos.
		Empleado empleadoLogueado = empleadoDAO.autenticar(usuario, password);

		// 4. Comprobamos el resultado.
		if (empleadoLogueado != null) {
			// ¡Login exitoso!
			// System.out.println("Login exitoso para: " + empleadoLogueado.getNombre());

			// Cerramos la ventana de login para limpiar la pantalla.
			vistaLogin.dispose();

			// Creamos y mostramos la ventana principal (Dashboard).
			// IMPORTANTE: Pasamos el objeto 'empleadoLogueado' para configurar los permisos
			// (Roles).
			Principal ventanaPrincipal = new Principal(empleadoLogueado);
			ventanaPrincipal.setVisible(true);
		} else {
			// Login fallido: Credenciales no coinciden.
			vistaLogin.mostrarError("Usuario o contraseña incorrectos.");
		}
	}
}