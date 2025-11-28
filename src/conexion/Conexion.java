package conexion;

import java.sql.Connection;

/**
 * Clase utilitaria que provee un punto de acceso global a la conexión de la
 * base de datos.
 * <p>
 * Actúa como una fábrica estática que encapsula la configuración de
 * credenciales y la instanciación de la implementación específica
 * ({@link Conexion_SQL}).
 * </p>
 * 
 * 
 * @version 1.0
 */
public class Conexion {

	/** Dirección del servidor y puerto (ej. localhost:1433 o NombrePC:1433). */
	private static final String SERVIDOR = "JOAHAN:1433";

	/** Nombre de la base de datos en SQL Server. */
	private static final String BASE_DATOS = "MyPos";

	/** Usuario de autenticación SQL (ej. sa). */
	private static final String USUARIO = "sa";

	/** Contraseña de autenticación SQL. */
	private static final String PASSWORD = "3312";

	/**
	 * Obtiene una instancia activa de la conexión a la base de datos.
	 * <p>
	 * Este método instancia internamente la clase {@link Conexion_SQL} utilizando
	 * las credenciales constantes definidas en esta clase.
	 * </p>
	 * * @return Objeto {@link java.sql.Connection} listo para realizar consultas, o
	 * {@code null} si la conexión falló.
	 */
	public static Connection getConexion() {
		// Instancia la clase hija específica para SQL Server pasando los parámetros
		ConexionBD miConexion = new Conexion_SQL(SERVIDOR, BASE_DATOS, USUARIO, PASSWORD);

		// Retorna el objeto Connection gestionado por la clase padre
		return miConexion.getConexion();
	}
}