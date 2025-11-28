package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase abstracta que define la estructura base para cualquier conexión a base
 * de datos.
 * <p>
 * Maneja el ciclo de vida de la conexión (apertura, almacenamiento y cierre),
 * delegando la construcción de la cadena de conexión (URL JDBC) a las clases
 * hijas.
 * </p>
 */
public abstract class ConexionBD {

	/** Objeto de conexión de Java SQL. Protegido para acceso desde clases hijas. */
	protected Connection conexion;

	/** Cadena de conexión JDBC completa. */
	private String url;

	/** Usuario de la base de datos. */
	private String usuario;

	/** Contraseña de la base de datos. */
	private String password;

	/**
	 * Constructor base. Inicializa las credenciales y establece la conexión
	 * inmediatamente. * @param servidor Dirección del host y puerto.
	 * 
	 * @param baseDatos Nombre de la base de datos.
	 * @param usuario   Usuario con permisos.
	 * @param password  Contraseña del usuario.
	 */
	public ConexionBD(String servidor, String baseDatos, String usuario, String password) {
		this.usuario = usuario;
		this.password = password;
		// Llama al método abstracto implementado por el hijo para obtener la URL
		// correcta
		this.url = this.construirURL(servidor, baseDatos);
		conectar(); // Intenta conectar al momento de instanciar
	}

	/**
	 * Método abstracto para construir la URL JDBC específica de cada motor de base
	 * de datos. * @param servidor Dirección del servidor.
	 * 
	 * @param baseDatos Nombre de la BD.
	 * @return Cadena de conexión JDBC formateada correctamente.
	 */
	protected abstract String construirURL(String servidor, String baseDatos);

	/**
	 * Intenta establecer la conexión con el {@link DriverManager}.
	 * <p>
	 * Captura {@link SQLException} y muestra errores en la consola de error
	 * estándar en caso de fallo.
	 * </p>
	 */
	private void conectar() {
		try {
			conexion = DriverManager.getConnection(url, usuario, password);
			// System.out.println("✅ Conexión establecida correctamente con " + url);
		} catch (SQLException e) {
			System.err.println("❌ Error al conectar a la BD: " + e.getMessage());
		}
	}

	/**
	 * Devuelve el objeto Connection activo. * @return La conexión actual o null si
	 * no se pudo establecer.
	 */
	public Connection getConexion() {
		return conexion;
	}

	/**
	 * Cierra la conexión activa si está abierta.
	 * <p>
	 * Es recomendable llamar a este método al finalizar la aplicación o el ciclo de
	 * vida de la conexión para liberar recursos del servidor.
	 * </p>
	 */
	public void cerrarConexion() {
		try {
			if (conexion != null && !conexion.isClosed()) {
				conexion.close();
			}
		} catch (SQLException e) {
			System.err.println("❌ Error al cerrar la conexión: " + e.getMessage());
		}
	}
}