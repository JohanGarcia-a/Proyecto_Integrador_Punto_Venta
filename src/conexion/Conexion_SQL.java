package conexion;

/**
 * Implementación concreta de conexión para Microsoft SQL Server.
 * <p>
 * Extiende {@link ConexionBD} y define el formato específico de la URL JDBC
 * para conectarse a instancias de SQL Server, incluyendo configuraciones de
 * seguridad.
 * </p>
 */
public class Conexion_SQL extends ConexionBD {

	/**
	 * Constructor que pasa las credenciales a la clase padre. * @param servidor
	 * Dirección del servidor (ej. localhost:1433).
	 * 
	 * @param baseDatos Nombre de la base de datos.
	 * @param usuario   Usuario de SQL Server.
	 * @param password  Contraseña de SQL Server.
	 */
	public Conexion_SQL(String servidor, String baseDatos, String usuario, String password) {
		super(servidor, baseDatos, usuario, password);
	}

	/**
	 * Construye la URL de conexión JDBC con el formato requerido por el driver de
	 * Microsoft.
	 * <p>
	 * Se incluyen las propiedades:
	 * <ul>
	 * <li><b>encrypt=false</b>: Para evitar requisitos estrictos de encriptación en
	 * desarrollo local.</li>
	 * <li><b>trustServerCertificate=true</b>: Para confiar en certificados SSL
	 * autofirmados y evitar errores de handshake.</li>
	 * </ul>
	 * </p>
	 * * @param servidor Dirección del host.
	 * 
	 * @param baseDatos Nombre de la BD.
	 * @return URL JDBC formateada (ej. jdbc:sqlserver://localhost...;).
	 */
	@Override
	protected String construirURL(String servidor, String baseDatos) {
		return "jdbc:sqlserver://" + servidor + ";databaseName=" + baseDatos
				+ ";encrypt=false;trustServerCertificate=true";
	}
}