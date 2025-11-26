package persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import conexion.Conexion;
import modelo.Empleado;
import modelogenerico.BaseDAO;
import vista.PanelEmpleado;

public class EmpleadoDAO implements BaseDAO<Empleado> {
	PanelEmpleado vistaEmpleado = new PanelEmpleado();

	@Override
	public Empleado buscarPorID(int id) {
		// --- MODIFICACIÓN: Se añade la columna Password a la consulta ---
		String sql = "SELECT Eid,NombreE,NumeroTel,Rol,Password FROM TablaEmpleados WHERE Eid=?";
		Empleado empleadoEncontrado = null;

		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					// --- MODIFICACIÓN: Se pasa la contraseña al constructor ---
					empleadoEncontrado = new Empleado(rs.getInt("Eid"), rs.getString("NombreE"),
							rs.getString("NumeroTel"), rs.getString("Rol"), rs.getString("Password"));
				}
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(vistaEmpleado, "Error al buscar el empleado: " + e.toString());
		}
		return empleadoEncontrado;
	}

	@Override
	public List<Empleado> ObtenerTodo() {
		List<Empleado> empleados = new ArrayList<>();
		// --- MODIFICACIÓN: Se añade la columna Password a la consulta ---
		String sql = "SELECT Eid,NombreE,NumeroTel,Rol,Password FROM TablaEmpleados";

		try (Connection con = Conexion.getConexion();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				// --- MODIFICACIÓN: Se pasa la contraseña al constructor ---
				empleados.add(new Empleado(rs.getInt("Eid"), rs.getString("NombreE"), rs.getString("NumeroTel"),
						rs.getString("Rol"), rs.getString("Password")));
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(vistaEmpleado, e.toString());
		}
		return empleados;
	}

	@Override
	public boolean agregar(Empleado entidad) {
		// --- MODIFICACIÓN 1: Se añade la columna Password al SQL ---
		String sql = "INSERT INTO TablaEmpleados(NombreE,NumeroTel,Rol,Password) VALUES (?,?,?,?)";
		boolean Exito = false;

		try (Connection con = Conexion.getConexion();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setString(1, entidad.getNombre());
			ps.setString(2, entidad.getNumTel());
			ps.setString(3, entidad.getRol());
			ps.setString(4, entidad.getContraseña()); // <-- MODIFICACIÓN 2: Se añade el parámetro de la contraseña

			if (ps.executeUpdate() > 0) {
				try (ResultSet idGenerado = ps.getGeneratedKeys()) {
					if (idGenerado.next()) {
						entidad.setid(idGenerado.getInt(1));
						Exito = true;
					}
				}
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(vistaEmpleado, "Empleado no agregado: " + e.toString());
		}
		return Exito;
	}

	@Override
	public boolean modificar(Empleado entidad) {
		// --- MODIFICACIÓN 1: Se añade la columna Password al SQL ---
		String sql = "UPDATE TablaEmpleados SET NombreE=?, NumeroTel=?, Rol=?, Password=? WHERE Eid=?";
		boolean exito = false;

		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, entidad.getNombre());
			ps.setString(2, entidad.getNumTel());
			ps.setString(3, entidad.getRol());
			ps.setString(4, entidad.getContraseña()); // <-- MODIFICACIÓN 2: Se añade el parámetro de la contraseña
			ps.setInt(5, entidad.getid()); // <-- MODIFICACIÓN 3: El índice del ID ahora es 5

			if (ps.executeUpdate() > 0) {
				exito = true;
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(vistaEmpleado, "Error al modificar " + e.toString());
		}
		return exito;
	}

	@Override
	public boolean borrar(int id) {
		// (Este método no necesita cambios)
		String sql = "DELETE FROM TablaEmpleados WHERE Eid=? ";
		boolean exito = false;
		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, id);
			if (ps.executeUpdate() > 0) {
				exito = true;
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(vistaEmpleado, "Error al borrar " + e.toString());
		}
		return exito;
	}

	// --- NUEVO MÉTODO PARA EL LOGIN ---
	public Empleado autenticar(String nombreUsuario, String contaseña) {
		Empleado empleado = null;
		String sql = "SELECT Eid, NombreE, NumeroTel, Rol, Password FROM TablaEmpleados WHERE NombreE = ? AND Password = ?";

		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, nombreUsuario);
			ps.setString(2, contaseña);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					empleado = new Empleado(rs.getInt("Eid"), rs.getString("NombreE"), rs.getString("NumeroTel"),
							rs.getString("Rol"), rs.getString("Password"));
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al autenticar empleado: " + e.getMessage());
			// Nota: En el login, es mejor no mostrar un JOptionPane, solo registrar el
			// error.
		}
		return empleado; // Devuelve el objeto Empleado si lo encuentra, o null si no
	}
}