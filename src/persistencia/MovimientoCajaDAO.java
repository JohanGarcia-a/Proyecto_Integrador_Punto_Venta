package persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import conexion.Conexion;
import modelo.MovimientoCaja;
import modelogenerico.BaseDAO;

public class MovimientoCajaDAO implements BaseDAO<MovimientoCaja> {

	@Override
	public boolean agregar(MovimientoCaja movimiento) {
		String sql = "INSERT INTO TablaMovimientosCaja(CorteID, UsuarioID, Fecha, TipoMovimiento, Monto, Descripcion) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		boolean exito = false;

		try (Connection con = Conexion.getConexion();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setInt(1, movimiento.getCorteId());
			ps.setInt(2, movimiento.getUsuarioId());
			ps.setTimestamp(3, new java.sql.Timestamp(movimiento.getFecha().getTime()));
			ps.setString(4, movimiento.getTipoMovimiento());
			ps.setDouble(5, movimiento.getMonto());
			ps.setString(6, movimiento.getDescripcion());

			if (ps.executeUpdate() > 0) {
				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next()) {
						movimiento.setid(rs.getInt(1));
						exito = true;
					}
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al registrar movimiento: " + e.toString());
		}
		return exito;
	}

	// --- MÉTODO CLAVE PARA LA VISTA DE HOY ---
	public List<MovimientoCaja> obtenerMovimientosPorCorte(int corteID) {
		List<MovimientoCaja> lista = new ArrayList<>();
		// Hacemos JOIN con empleados para saber quién registró el movimiento
		String sql = "SELECT m.*, u.NombreE " + "FROM TablaMovimientosCaja m "
				+ "INNER JOIN TablaEmpleados u ON m.UsuarioID = u.Eid " + "WHERE m.CorteID = ? "
				+ "ORDER BY m.Fecha DESC";

		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, corteID);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					lista.add(new MovimientoCaja(rs.getInt("MovimientoID"), rs.getInt("CorteID"),
							rs.getInt("UsuarioID"), rs.getTimestamp("Fecha"), rs.getString("TipoMovimiento"),
							rs.getDouble("Monto"), rs.getString("Descripcion"), rs.getString("NombreE") // Nombre del
																										// empleado
					));
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al listar movimientos del corte: " + e.toString());
		}
		return lista;
	}

	// --- MÉTODO CLAVE PARA EL CIERRE DE CAJA ---
	// Nos dice cuánto dinero extra entró o salió en este turno
	public double obtenerTotalPorTipo(int corteID, String tipoMovimiento) {
		double total = 0.0;
		String sql = "SELECT SUM(Monto) FROM TablaMovimientosCaja WHERE CorteID = ? AND TipoMovimiento = ?";

		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, corteID);
			ps.setString(2, tipoMovimiento); // "Ingreso" o "Egreso"

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					total = rs.getDouble(1);
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al calcular total de movimientos: " + e.toString());
		}
		return total;
	}

	// --- Métodos de la interfaz BaseDAO (necesarios por contrato) ---

	@Override
	public List<MovimientoCaja> ObtenerTodo() {
		// Este método trae TODO el historial (podría ser mucho),
		// normalmente usaremos obtenerMovimientosPorCorte
		List<MovimientoCaja> lista = new ArrayList<>();
		String sql = "SELECT m.*, u.NombreE FROM TablaMovimientosCaja m INNER JOIN TablaEmpleados u ON m.UsuarioID = u.Eid";
		try (Connection con = Conexion.getConexion();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				lista.add(new MovimientoCaja(rs.getInt("MovimientoID"), rs.getInt("CorteID"), rs.getInt("UsuarioID"),
						rs.getTimestamp("Fecha"), rs.getString("TipoMovimiento"), rs.getDouble("Monto"),
						rs.getString("Descripcion"), rs.getString("NombreE")));
			}
		} catch (SQLException e) {
			System.err.println(e.toString());
		}
		return lista;
	}

	@Override
	public MovimientoCaja buscarPorID(int id) {
		MovimientoCaja mov = null;
		String sql = "SELECT m.*, u.NombreE FROM TablaMovimientosCaja m INNER JOIN TablaEmpleados u ON m.UsuarioID = u.Eid WHERE MovimientoID=?";
		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					mov = new MovimientoCaja(rs.getInt("MovimientoID"), rs.getInt("CorteID"), rs.getInt("UsuarioID"),
							rs.getTimestamp("Fecha"), rs.getString("TipoMovimiento"), rs.getDouble("Monto"),
							rs.getString("Descripcion"), rs.getString("NombreE"));
				}
			}
		} catch (SQLException e) {
			System.err.println(e.toString());
		}
		return mov;
	}

	@Override
	public boolean modificar(MovimientoCaja entidad) {
		// Por seguridad, los movimientos de dinero NO suelen modificarse,
		// se recomienda borrarlos y crearlos de nuevo, pero implementamos esto por la
		// interfaz.
		String sql = "UPDATE TablaMovimientosCaja SET TipoMovimiento=?, Monto=?, Descripcion=? WHERE MovimientoID=?";
		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, entidad.getTipoMovimiento());
			ps.setDouble(2, entidad.getMonto());
			ps.setString(3, entidad.getDescripcion());
			ps.setInt(4, entidad.getid());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error al modificar: " + e.toString());
			return false;
		}
	}

	@Override
	public boolean borrar(int id) {
		String sql = "DELETE FROM TablaMovimientosCaja WHERE MovimientoID=?";
		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error al borrar: " + e.toString());
			return false;
		}
	}
}