package persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import conexion.Conexion;
import modelo.OrdenCompraDetalle;

/**
 * Clase de Acceso a Datos (DAO) para la entidad {@link OrdenCompraDetalle}.
 * <p>
 * Gestiona la recuperación de los renglones individuales (productos) asociados
 * a una Orden de Compra maestra.
 * </p>
 * <p>
 * <b>Uso Principal:</b> Es fundamental durante el proceso de <b>Recepción de
 * Pedidos</b>. El sistema usa esta clase para saber exactamente qué productos y
 * qué cantidades debe sumar al inventario cuando una orden cambia de estado a
 * "Recibido".
 * </p>
 * * @version 1.1
 */
public class OrdenCompraDetalleDAO {

	/**
	 * Recupera todos los detalles (productos) asociados a una Orden de Compra
	 * específica.
	 * <p>
	 * Ejecuta un {@code INNER JOIN} con {@code TablaAlmacen_Productos} para obtener
	 * el nombre actual del producto, facilitando la visualización en la interfaz de
	 * recepción.
	 * </p>
	 * * @param ordenId El ID de la Orden de Compra maestra (Cabecera).
	 * 
	 * @return Lista de objetos {@link OrdenCompraDetalle} con la información de
	 *         cantidad, costo y descripción.
	 */
	public List<OrdenCompraDetalle> buscarDetallesPorOrdenID(int ordenId) {
		List<OrdenCompraDetalle> detalles = new ArrayList<>();

		// Seleccionamos los datos del detalle y el nombre del producto vía JOIN
		String sql = "SELECT d.*, p.Nombre, d.Descripcion " + "FROM TablaOrdenCompraDetalle d "
				+ "INNER JOIN TablaAlmacen_Productos p ON d.ProductoID = p.Pid " + "WHERE d.OrdenID = ?";

		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, ordenId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					// Mapeamos el resultado al objeto modelo
					detalles.add(new OrdenCompraDetalle(rs.getInt("DetalleID"), rs.getInt("OrdenID"),
							rs.getInt("ProductoID"), rs.getInt("CantidadPedida"), rs.getDouble("CostoUnitario"),
							rs.getString("Nombre"), // Nombre del producto (JOIN)
							rs.getString("Descripcion") // Descripción específica del renglón
					));
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al buscar detalles de la orden: " + e.getMessage());
		}
		return detalles;
	}
}