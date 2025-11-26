package persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import conexion.Conexion;
import modelo.VentaDetalle;

public class VentaDetalleDAO {

	public List<VentaDetalle> buscarDetallesPorVentaID(int ventaId) {
		List<VentaDetalle> detalles = new ArrayList<>();
		String sql = "SELECT vd.ProductoID, p.Nombre, p.Descripcion, vd.Cantidad, vd.PrecioUnitario "
				+ "FROM TablaVentaDetalle vd " + "JOIN TablaAlmacen_Productos p ON vd.ProductoID = p.Pid "
				+ "WHERE vd.VentaID = ?";

		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, ventaId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					VentaDetalle detalle = new VentaDetalle(rs.getInt("ProductoID"), rs.getString("Nombre"),
							rs.getString("Descripcion"), rs.getInt("Cantidad"), rs.getDouble("PrecioUnitario"));
					detalles.add(detalle);
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al buscar detalles de venta: " + e.getMessage());
		}
		return detalles;
	}
}