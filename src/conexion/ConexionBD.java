package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class ConexionBD {

	// Atributos protegidos para que los hijos puedan acceder si es necesario
	protected Connection conexion;
	private String url;
	private String usuario;
	private String password;

	// Constructor
	public ConexionBD(String servidor, String baseDatos, String usuario, String password) {
		this.usuario = usuario;
		this.password = password;
		// Llama al método abstracto que implementará el hijo
		this.url = this.construirURL(servidor, baseDatos);
		conectar(); // Establecer la conexión al crear la instancia
	}

	// Método abstracto obligatorio para los hijos
	protected abstract String construirURL(String servidor, String baseDatos);

	// Método para establecer la conexión
	private void conectar() {
		try {
			conexion = DriverManager.getConnection(url, usuario, password);
			// System.out.println("✅ Conexión establecida correctamente con " + url);
		} catch (SQLException e) {
			System.err.println("❌ Error al conectar a la BD: " + e.getMessage());
		}
	}

	// Método para obtener la conexión activa
	public Connection getConexion() {
		return conexion;
	}

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