package conexion;

import java.sql.Connection;

public class Conexion {

	// Configuración centralizada
	// Ajusta estos valores si cambian en el futuro
	private static final String SERVIDOR = "JOAHAN:1433";
	private static final String BASE_DATOS = "MyPos";
	private static final String USUARIO = "sa";
	private static final String PASSWORD = "3312";

	// Este método estático actúa como "fábrica" o puente.
	// Instancia tus clases obligatorias (Conexion_SQL -> ConexionBD) y devuelve la
	// conexión.
	public static Connection getConexion() {

		// Aquí se cumple tu requisito: Se instancia Conexion_SQL que hereda de
		// ConexionBD
		ConexionBD miConexion = new Conexion_SQL(SERVIDOR, BASE_DATOS, USUARIO, PASSWORD);

		return miConexion.getConexion();
	}
}