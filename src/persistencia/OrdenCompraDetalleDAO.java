package persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import conexion.Conexion;
import modelo.OrdenCompraDetalle;

public class OrdenCompraDetalleDAO {

	// Este es el método clave que usaremos al "Recibir Pedido"
	public List<OrdenCompraDetalle> buscarDetallesPorOrdenID(int ordenId) {
		List<OrdenCompraDetalle> detalles = new ArrayList<>();

		// --- CAMBIO 1: Añadir d.Descripcion al SELECT ---
		String sql = "SELECT d.*, p.Nombre, d.Descripcion " + "FROM TablaOrdenCompraDetalle d "
				+ "INNER JOIN TablaAlmacen_Productos p ON d.ProductoID = p.Pid " + "WHERE d.OrdenID = ?";

		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, ordenId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					detalles.add(new OrdenCompraDetalle(rs.getInt("DetalleID"), rs.getInt("OrdenID"),
							rs.getInt("ProductoID"), rs.getInt("CantidadPedida"), rs.getDouble("CostoUnitario"),
							rs.getString("Nombre"), // El nombre del producto
							// --- CAMBIO 2: Añadir el parámetro Descripcion al constructor ---
							rs.getString("Descripcion")));
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al buscar detalles de la orden: " + e.getMessage());
		}
		return detalles;
	}
}