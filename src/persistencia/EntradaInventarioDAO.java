package persistencia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import conexion.Conexion;
import modelo.EntradaInventario;

/**
 * Clase de Acceso a Datos (DAO) para la entidad {@link EntradaInventario}.
 * <p>
 * Gestiona la persistencia del historial de movimientos de entrada de mercancía
 * en la tabla <b>TablaEntradasInventario</b>.
 * </p>
 * <p>
 * Esta clase no gestiona el stock en sí (eso lo hace
 * {@code AlmacenProductosDAO}), sino que actúa como una <b>bitácora de
 * auditoría</b> para registrar quién agregó qué, cuándo y cuánto.
 * </p>
 * 
 * @version 1.1
 */
public class EntradaInventarioDAO {

	/**
	 * Registra un nuevo evento de entrada de inventario.
	 * <p>
	 * Guarda el snapshot del movimiento, incluyendo una descripción del producto en
	 * ese momento, para mantener consistencia histórica.
	 * </p>
	 * 
	 * @param entrada Objeto con los datos del movimiento (Producto, Cantidad,
	 *                Usuario, Fecha).
	 * @return {@code true} si el registro se guardó correctamente.
	 */
	public boolean agregar(EntradaInventario entrada) {
		String sql = "INSERT INTO TablaEntradasInventario (ProductoID, CantidadAgregada, FechaEntrada, UsuarioID, ProductoDescripcion) VALUES (?, ?, ?, ?, ?)";
		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, entrada.getProductoId());
			ps.setInt(2, entrada.getCantidadAgregada());
			ps.setTimestamp(3, new java.sql.Timestamp(entrada.getFechaEntrada().getTime()));
			ps.setInt(4, entrada.getUsuarioId());

			// Añadimos la descripción del producto al INSERT para referencia futura
			ps.setString(5, entrada.getProductoDescripcion());

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error al registrar la entrada de inventario: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Recupera el historial completo de entradas de inventario.
	 * <p>
	 * Ejecuta una consulta compleja con:
	 * <ul>
	 * <li><b>JOIN</b> con {@code TablaAlmacen_Productos}: Para obtener el nombre
	 * actual del producto.</li>
	 * <li><b>LEFT JOIN</b> con {@code TablaEmpleados}: Para obtener el nombre del
	 * usuario que hizo el movimiento.</li>
	 * </ul>
	 * Los resultados se ordenan cronológicamente descendente (lo más reciente
	 * primero).
	 * </p>
	 * 
	 * @return Lista de entradas enriquecida con nombres para visualización en
	 *         reportes.
	 */
	public List<EntradaInventario> obtenerTodasLasEntradas() {
		List<EntradaInventario> entradas = new ArrayList<>();

		String sql = "SELECT e.EntradaID, p.Nombre, e.CantidadAgregada, e.FechaEntrada, u.NombreE, e.ProductoDescripcion "
				+ "FROM TablaEntradasInventario e " + "JOIN TablaAlmacen_Productos p ON e.ProductoID = p.Pid "
				+ "LEFT JOIN TablaEmpleados u ON e.UsuarioID = u.Eid " + "ORDER BY e.FechaEntrada DESC";

		try (Connection con = Conexion.getConexion();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				entradas.add(new EntradaInventario(rs.getInt("EntradaID"), rs.getString("Nombre"),
						rs.getInt("CantidadAgregada"), rs.getTimestamp("FechaEntrada"), rs.getString("NombreE"),
						rs.getString("ProductoDescripcion") // Recuperamos la descripción histórica
				));
			}
		} catch (SQLException e) {
			System.err.println("Error al obtener el historial de entradas: " + e.getMessage());
		}
		return entradas;
	}
}