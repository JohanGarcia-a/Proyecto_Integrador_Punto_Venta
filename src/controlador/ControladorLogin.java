package controlador;

import modelo.Empleado;
import persistencia.EmpleadoDAO;
import vista.Principal;
import vista.VistaLogin;

public class ControladorLogin {
	private VistaLogin vistaLogin;
	private EmpleadoDAO empleadoDAO;

	public ControladorLogin(VistaLogin vista, EmpleadoDAO dao) {
		this.vistaLogin = vista;
		this.empleadoDAO = dao;

		// Añadimos el 'escuchador' al botón de la vista.
		// Cuando se haga clic, se ejecutará el método 'autenticarUsuario'.
		this.vistaLogin.addLoginListener(e -> autenticarUsuario());
	}

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
			System.out.println("Login exitoso para: " + empleadoLogueado.getNombre());

			// Cerramos la ventana de login.
			vistaLogin.dispose();

			// Creamos y mostramos la ventana principal, pasándole el empleado que inició
			// sesión.
			Principal ventanaPrincipal = new Principal(empleadoLogueado);
			ventanaPrincipal.setVisible(true);
		} else {
			// Login fallido.
			vistaLogin.mostrarError("Usuario o contraseña incorrectos.");
		}
	}
}