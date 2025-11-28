package main;

import java.awt.EventQueue;
import javax.swing.UIManager;

import controlador.ControladorLogin;
import persistencia.EmpleadoDAO;
import vista.VistaLogin;

/**
 * Clase principal que contiene el punto de entrada (Entry Point) de la
 * aplicación.
 * <p>
 * Su responsabilidad es:
 * <ol>
 * <li>Configurar el aspecto visual (Look and Feel) para que la aplicación se
 * integre con el sistema operativo.</li>
 * <li>Iniciar el hilo de eventos de Swing (EDT).</li>
 * <li>Ensamblar la primera tríada MVC (VistaLogin + EmpleadoDAO +
 * ControladorLogin) para arrancar el sistema.</li>
 * </ol>
 * </p>
 * 
 * @version 1.0
 */
public class Main {

	/**
	 * Método principal estándar de Java.
	 * <p>
	 * Se ejecuta al iniciar el programa.
	 * </p>
	 * 
	 * @param args Argumentos de línea de comandos (no utilizados en esta versión).
	 */
	public static void main(String[] args) {
		try {
			// Establece el estilo visual nativo del Sistema Operativo (Windows/Linux/Mac)
			// Esto hace que los botones y ventanas se vean "modernos" y no con el estilo
			// Java antiguo (Metal).
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Ejecuta la creación de la interfaz gráfica dentro del Hilo de Despacho de
		// Eventos (EDT)
		// Esto es una buena práctica en Swing para evitar problemas de concurrencia
		// visual.
		EventQueue.invokeLater(() -> {

			// 1. Instanciamos la VISTA (Ventana gráfica)
			VistaLogin vistaLogin = new VistaLogin();

			// 2. Instanciamos el MODELO/DAO (Acceso a datos)
			EmpleadoDAO empleadoDAO = new EmpleadoDAO();

			// 3. Instanciamos el CONTROLADOR y le inyectamos la Vista y el Modelo
			// El controlador se encarga de conectar los botones de la vista con la lógica
			// del DAO
			new ControladorLogin(vistaLogin, empleadoDAO);

			// 4. Hacemos visible la primera ventana
			vistaLogin.setVisible(true);
		});
	}
}