package persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import conexion.Conexion;
import modelo.VentaDetalle;

/**
 * Clase de Acceso a Datos (DAO) para la entidad {@link VentaDetalle}.
 * <p>
 * Se especializa en la recuperación de los renglones (ítems) asociados a una
 * venta histórica.
 * </p>
 * <p>
 * <b>Uso Principal:</b> Es utilizada por el módulo de "Historial de Ventas" y
 * la función de "Reimprimir Ticket", ya que recupera qué productos específicos
 * se vendieron en una transacción pasada.
 * </p>
 * * @version 1.0
 */
public class VentaDetalleDAO {

	/**
	 * Recupera la lista de productos asociados a una venta específica.
	 * <p>
	 * Ejecuta una consulta con <b>INNER JOIN</b> hacia
	 * {@code TablaAlmacen_Productos} para obtener el nombre y descripción del
	 * producto. Esto es necesario porque la tabla de detalles solo almacena el ID
	 * del producto, pero el usuario necesita leer el nombre en el ticket.
	 * </p>
	 * * @param ventaId Identificador de la venta cabecera.
	 * 
	 * @return Lista de objetos {@link VentaDetalle} con la información lista para
	 *         visualizar o imprimir.
	 */
	public List<VentaDetalle> buscarDetallesPorVentaID(int ventaId) {
		List<VentaDetalle> detalles = new ArrayList<>();

		// SQL optimizado: Recupera datos transaccionales (Cantidad, Precio)
		// y datos informativos (Nombre, Descripción) en una sola consulta.
		String sql = "SELECT vd.ProductoID, p.Nombre, p.Descripcion, vd.Cantidad, vd.PrecioUnitario "
				+ "FROM TablaVentaDetalle vd " + "JOIN TablaAlmacen_Productos p ON vd.ProductoID = p.Pid "
				+ "WHERE vd.VentaID = ?";

		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, ventaId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					// Construimos el objeto detalle con la información recuperada
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