package modelo;

import java.sql.*;
import java.util.ArrayList;

/**
 * Clase de utilidad para la abstracción de operaciones CRUD en la base de
 * datos.
 * <p>
 * Provee métodos genéricos para Consultar, Insertar, Modificar y Eliminar
 * registros utilizando JDBC. Maneja internamente la creación de
 * {@link PreparedStatement} y la conversión de resultados a objetos Java.
 * </p>
 * <p>
 * <b>Nota:</b> Esta clase está diseñada para trabajar con una conexión
 * inyectada y no gestiona el cierre de la misma, permitiendo transacciones
 * múltiples.
 * </p>
 * * @version 1.0
 */
public class BaseDatos {

	/** Objeto de conexión activa a SQL Server. */
	private Connection conexion;

	/**
	 * Constructor. Recibe la conexión activa para realizar operaciones. * @param
	 * conexion Objeto {@link Connection} establecido previamente (usualmente desde
	 * {@code Conexion.getConexion()}).
	 */
	public BaseDatos(Connection conexion) {
		this.conexion = conexion;
	}

	// --- CONSULTAR (SELECT) ---

	/**
	 * Ejecuta una consulta de selección (SELECT) dinámica.
	 * <p>
	 * Recupera los datos respetando sus tipos originales (int, double, date, etc.)
	 * gracias al uso de {@code rs.getObject()}.
	 * </p>
	 * * @param tabla Nombre de la tabla en la base de datos.
	 * 
	 * @param campos    Cadena con las columnas a solicitar separadas por comas (ej.
	 *                  "id, nombre"). Si es {@code null}, se asume "*".
	 * @param condicion Cláusula WHERE sin la palabra reservada "WHERE" (ej. "id = 5
	 *                  AND estado = 'Activo'"). Puede ser {@code null}.
	 * @return Una lista ({@link ArrayList}) donde cada elemento es un arreglo de
	 *         {@link Object} que representa una fila. Retorna una lista vacía si no
	 *         hay resultados o si ocurre un error.
	 */
	public ArrayList<Object[]> consultar(String tabla, String campos, String condicion) {
		ArrayList<Object[]> resultados = new ArrayList<>();

		String sql = (campos == null) ? "SELECT * FROM " + tabla : "SELECT " + campos + " FROM " + tabla;

		if (condicion != null && !condicion.isEmpty()) {
			sql += " WHERE " + condicion;
		}

		try (PreparedStatement stmt = conexion.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

			ResultSetMetaData rsmd = rs.getMetaData();
			int numColumnas = rsmd.getColumnCount();

			while (rs.next()) {
				Object[] fila = new Object[numColumnas];
				for (int i = 1; i <= numColumnas; i++) {
					// Usamos getObject para que Java detecte si es número, texto o fecha
					// automáticamente
					fila[i - 1] = rs.getObject(i);
				}
				resultados.add(fila);
			}
		} catch (SQLException e) {
			System.err.println("❌ Error al consultar registros en " + tabla + ": " + e.getMessage());
		}
		return resultados;
	}

	// --- EXISTE (SELECT TOP 1) ---

	/**
	 * Verifica la existencia de un registro que cumpla con una condición
	 * específica.
	 * <p>
	 * Optimizado para SQL Server utilizando {@code SELECT TOP 1 1} para evitar
	 * escanear toda la tabla o recuperar datos innecesarios.
	 * </p>
	 * * @param tabla Nombre de la tabla.
	 * 
	 * @param condicion Condición de búsqueda (ej. "codigo = 'A001'").
	 * @return {@code true} si se encuentra al menos un registro, {@code false} en
	 *         caso contrario.
	 */
	public boolean existe(String tabla, String condicion) {
		boolean encontrado = false;
		String sql = "SELECT TOP 1 1 FROM " + tabla + " WHERE " + condicion;

		try (PreparedStatement stmt = conexion.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
			encontrado = rs.next();
		} catch (SQLException e) {
			System.err.println("❌ Error al verificar existencia en " + tabla + ": " + e.getMessage());
		}
		return encontrado;
	}

	// --- INSERTAR (INSERT) ---

	/**
	 * Inserta un nuevo registro en la base de datos de forma dinámica.
	 * <p>
	 * Construye la sentencia SQL utilizando *placeholders* (?) para prevenir
	 * inyección SQL.
	 * </p>
	 * * @param tabla Nombre de la tabla destino.
	 * 
	 * @param campos  Lista de columnas separadas por comas (ej. "Nombre, Edad").
	 * @param valores Arreglo de objetos con los valores a insertar. El orden debe
	 *                coincidir con {@code campos}.
	 * @return El ID autogenerado (clave primaria) si la inserción fue exitosa y la
	 *         tabla tiene identidad; retorna -1 si ocurre un error.
	 */
	public int insertar(String tabla, String campos, Object[] valores) {
		// Generamos los signos de interrogación dinámicamente (?,?,?)
		StringBuilder placeholders = new StringBuilder();
		for (int i = 0; i < valores.length; i++) {
			placeholders.append("?");
			if (i < valores.length - 1)
				placeholders.append(",");
		}

		String sql = "INSERT INTO " + tabla + " (" + campos + ") VALUES (" + placeholders.toString() + ")";
		int idGenerado = -1;

		// IMPORTANTE: Statement.RETURN_GENERATED_KEYS permite recuperar el ID
		// autoincrementable creado
		try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			for (int i = 0; i < valores.length; i++) {
				ps.setObject(i + 1, valores[i]);
			}

			int filasAfectadas = ps.executeUpdate();

			if (filasAfectadas > 0) {
				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next()) {
						idGenerado = rs.getInt(1);
						// System.out.println("✅ Registro insertado en " + tabla + ". ID: " +
						// idGenerado);
					}
				}
			}
		} catch (SQLException e) {
			System.err.println("❌ Error al insertar en " + tabla + ": " + e.getMessage());
		}
		return idGenerado;
	}

	// --- MODIFICAR (UPDATE) ---

	/**
	 * Actualiza registros existentes en la base de datos. * @param tabla Nombre de
	 * la tabla.
	 * 
	 * @param asignaciones Cadena con las asignaciones SQL parametrizadas (ej.
	 *                     "Nombre=?, Telefono=?").
	 * @param condicion    Condición WHERE para identificar los registros a
	 *                     modificar (ej. "id = 5").
	 * @param valores      Arreglo de objetos que sustituirán a los signos de
	 *                     interrogación en {@code asignaciones}.
	 * @return {@code true} si al menos una fila fue modificada, {@code false} si no
	 *         hubo cambios o hubo error.
	 */
	public boolean modificar(String tabla, String asignaciones, String condicion, Object[] valores) {
		// Ejemplo de asignaciones: "Nombre=?, Telefono=?"
		String sql = "UPDATE " + tabla + " SET " + asignaciones + " WHERE " + condicion;

		try (PreparedStatement ps = conexion.prepareStatement(sql)) {
			for (int i = 0; i < valores.length; i++) {
				ps.setObject(i + 1, valores[i]);
			}
			int filas = ps.executeUpdate();
			return filas > 0;
		} catch (SQLException e) {
			System.err.println("❌ Error al actualizar en " + tabla + ": " + e.getMessage());
			return false;
		}
	}

	// --- ELIMINAR (DELETE) ---

	/**
	 * Elimina registros de la base de datos basándose en una condición simple.
	 * * @param tabla Nombre de la tabla.
	 * 
	 * @param condicion      Condición SQL parametrizada (ej. "id = ?").
	 * @param valorCondicion El valor que sustituirá al signo de interrogación en la
	 *                       condición.
	 * @return {@code true} si se eliminó al menos un registro, {@code false} en
	 *         caso contrario.
	 */
	public boolean eliminar(String tabla, String condicion, Object valorCondicion) {
		String sql = "DELETE FROM " + tabla + " WHERE " + condicion;

		try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
			pstmt.setObject(1, valorCondicion);
			int resultado = pstmt.executeUpdate();
			return resultado > 0;
		} catch (SQLException e) {
			System.err.println("❌ Error al eliminar de " + tabla + ": " + e.getMessage());
			return false;
		}
	}
}