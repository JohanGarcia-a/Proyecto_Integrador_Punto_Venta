package persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import conexion.Conexion;
import modelo.CorteCaja;

/**
 * Clase de Acceso a Datos (DAO) para la entidad {@link CorteCaja}.
 * <p>
 * Gestiona el ciclo de vida de los turnos de caja en la base de datos: Apertura
 * (Insertar), Verificación de Estado (Consultar Abiertos) y Cierre
 * (Actualizar).
 * </p>
 * * @version 1.1
 */
public class CorteCajaDAO {

	/**
	 * Registra la APERTURA de un nuevo turno de caja.
	 * <p>
	 * Inserta un registro con el monto inicial (fondo) y el estado "Abierto". La
	 * fecha de cierre y los montos finales quedan nulos o en cero.
	 * </p>
	 * * @param corte Objeto {@link CorteCaja} con los datos de inicio.
	 * 
	 * @return El ID autogenerado del nuevo corte, o -1 si hubo error.
	 */
	public int agregar(CorteCaja corte) {
		String sql = "INSERT INTO TablaCortesCaja (UsuarioID, FechaApertura, MontoInicial, Status) "
				+ "VALUES (?, ?, ?, ?)";

		try (Connection con = Conexion.getConexion();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setInt(1, corte.getUsuarioID());
			ps.setTimestamp(2, new java.sql.Timestamp(corte.getFechaApertura().getTime()));
			ps.setDouble(3, corte.getMontoInicial());
			ps.setString(4, corte.getStatus()); // "Abierto"

			int filasAfectadas = ps.executeUpdate();

			if (filasAfectadas > 0) {
				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next()) {
						return rs.getInt(1); // Devuelve el CorteID generado
					}
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al agregar el corte de caja: " + e.getMessage());
		}
		return -1; // Retorna -1 si falló
	}

	/**
	 * Verifica si un usuario ya tiene una caja abierta para el día de hoy.
	 * <p>
	 * <b>Nota Técnica:</b> Utiliza la función de SQL Server
	 * {@code CONVERT(DATE, ...)} para comparar solo la parte de la fecha
	 * (año-mes-día), ignorando la hora exacta. Esto evita que un usuario abra
	 * múltiples cajas el mismo día si no ha cerrado la anterior.
	 * </p>
	 * * @param usuarioID El ID del empleado logueado.
	 * 
	 * @return Objeto {@link CorteCaja} si existe uno abierto hoy, o {@code null} si
	 *         no.
	 */
	public CorteCaja buscarCorteAbiertoHoy(int usuarioID) {
		// SQL Server: CONVERT(DATE, ...) extrae solo la fecha (ignora la hora)
		String sql = "SELECT * FROM TablaCortesCaja " + "WHERE UsuarioID = ? " + "AND Status = 'Abierto' "
				+ "AND CONVERT(DATE, FechaApertura) = CONVERT(DATE, GETDATE())";

		CorteCaja corte = null;

		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, usuarioID);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					// Si encontramos uno, lo construimos con todos sus datos
					corte = new CorteCaja(rs.getInt("CorteID"), rs.getInt("UsuarioID"),
							rs.getTimestamp("FechaApertura"), rs.getDouble("MontoInicial"),
							rs.getTimestamp("FechaCierre"), rs.getDouble("MontoFinalSistema"),
							rs.getDouble("MontoFinalContado"), rs.getDouble("Diferencia"), rs.getString("Status"));
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al buscar corte abierto: " + e.getMessage());
		}
		return corte; // Devuelve el corte encontrado, o null
	}

	/**
	 * Realiza el CIERRE administrativo de la caja.
	 * <p>
	 * Actualiza el registro existente estableciendo la fecha de cierre, los montos
	 * finales calculados, la diferencia (sobrante/faltante) y cambia el estado a
	 * "Cerrado".
	 * </p>
	 * * @param corte El objeto CorteCaja con todos los datos del cierre calculados.
	 * 
	 * @return {@code true} si se actualizó con éxito.
	 */
	public boolean cerrarCorte(CorteCaja corte) {
		String sql = "UPDATE TablaCortesCaja SET " + "FechaCierre = ?, " + "MontoFinalSistema = ?, "
				+ "MontoFinalContado = ?, " + "Diferencia = ?, " + "Status = 'Cerrado' " + "WHERE CorteID = ?";

		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setTimestamp(1, new java.sql.Timestamp(corte.getFechaCierre().getTime()));
			ps.setDouble(2, corte.getMontoFinalSistema());
			ps.setDouble(3, corte.getMontoFinalContado());
			ps.setDouble(4, corte.getDiferencia());
			ps.setInt(5, corte.getCorteID());

			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			System.err.println("Error al cerrar el corte de caja: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Obtiene el historial completo de cortes de caja en un rango de fechas.
	 * <p>
	 * Utilizado para el módulo de Reportes. Ejecuta un {@code INNER JOIN} con la
	 * tabla de empleados para mostrar quién fue el responsable de cada turno.
	 * </p>
	 * * @param fechaInicio Fecha inicial del rango.
	 * 
	 * @param fechaFin Fecha final del rango.
	 * @return Lista de cortes enriquecida con nombres de usuarios.
	 */
	public List<CorteCaja> obtenerHistorialCortes(Date fechaInicio, Date fechaFin) {
		List<CorteCaja> lista = new ArrayList<>();
		// Hacemos JOIN con empleados para obtener el nombre (NombreE)
		String sql = "SELECT c.*, u.NombreE " + "FROM TablaCortesCaja c "
				+ "INNER JOIN TablaEmpleados u ON c.UsuarioID = u.Eid " + "WHERE c.FechaApertura BETWEEN ? AND ? "
				+ "ORDER BY c.FechaApertura DESC";

		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setTimestamp(1, new java.sql.Timestamp(fechaInicio.getTime()));
			ps.setTimestamp(2, new java.sql.Timestamp(fechaFin.getTime()));

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					// Usamos el constructor especial que incluye nombreUsuario para el reporte
					lista.add(new CorteCaja(rs.getInt("CorteID"), rs.getString("NombreE"), // Nombre del empleado
							rs.getTimestamp("FechaApertura"), rs.getTimestamp("FechaCierre"),
							rs.getDouble("MontoInicial"), rs.getDouble("MontoFinalSistema"),
							rs.getDouble("MontoFinalContado"), rs.getDouble("Diferencia"), rs.getString("Status")));
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al obtener historial de cortes: " + e.getMessage());
		}
		return lista;
	}
}