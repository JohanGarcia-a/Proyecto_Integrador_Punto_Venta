package persistencia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import conexion.Conexion;
import modelo.EntradaInventario;

public class EntradaInventarioDAO {

	public boolean agregar(EntradaInventario entrada) {
		String sql = "INSERT INTO TablaEntradasInventario (ProductoID, CantidadAgregada, FechaEntrada, UsuarioID, ProductoDescripcion) VALUES (?, ?, ?, ?, ?)";
		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, entrada.getProductoId());
			ps.setInt(2, entrada.getCantidadAgregada());
			ps.setTimestamp(3, new java.sql.Timestamp(entrada.getFechaEntrada().getTime()));
			ps.setInt(4, entrada.getUsuarioId());

			// Añadimos la descripción del producto al INSERT
			ps.setString(5, entrada.getProductoDescripcion());

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error al registrar la entrada de inventario: " + e.getMessage());
			return false;
		}
	}

	public List<EntradaInventario> obtenerTodasLasEntradas() {
		List<EntradaInventario> entradas = new ArrayList<>();
		// Ahora también seleccionamos la descripción guardada
		String sql = "SELECT e.EntradaID, p.Nombre, e.CantidadAgregada, e.FechaEntrada, u.NombreE, e.ProductoDescripcion "
				+ "FROM TablaEntradasInventario e " + "JOIN TablaAlmacen_Productos p ON e.ProductoID = p.Pid "
				+ "LEFT JOIN TablaEmpleados u ON e.UsuarioID = u.Eid " + "ORDER BY e.FechaEntrada DESC";

		try (Connection con = Conexion.getConexion();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				entradas.add(new EntradaInventario(rs.getInt("EntradaID"), rs.getString("Nombre"),
						rs.getInt("CantidadAgregada"), rs.getTimestamp("FechaEntrada"), rs.getString("NombreE"),
						rs.getString("ProductoDescripcion") // <-- Leemos la descripción
				));
			}
		} catch (SQLException e) {
			System.err.println("Error al obtener el historial de entradas: " + e.getMessage());
		}
		return entradas;
	}
}