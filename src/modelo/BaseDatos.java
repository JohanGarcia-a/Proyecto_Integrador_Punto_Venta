package modelo;

import java.sql.*;
import java.util.ArrayList;

public class BaseDatos {
	private Connection conexion;

	// Constructor que recibe la conexión activa desde tu clase Conexion
	public BaseDatos(Connection conexion) {
		this.conexion = conexion;
	}

	// --- CONSULTAR (SELECT) ---
	// Devuelve ArrayList<Object[]> para mantener los tipos de datos (int, double,
	// date, etc.)
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
	// Optimizado para SQL Server
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
	// Devuelve el ID generado (int) o -1 si falla
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
		// autoincrementable
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
	// Devuelve true si se modificó algo
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
	// Devuelve true si se borró algo
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