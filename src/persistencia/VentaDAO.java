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
import modelo.Venta;
import modelo.VentaDetalle;

/**
 * Clase de Acceso a Datos (DAO) para la entidad {@link Venta}.
 * <p>
 * Gestiona el ciclo de vida transaccional de las ventas. Esta clase es crítica
 * para la integridad del negocio, ya que coordina la inserción de la cabecera
 * de venta, sus detalles y la actualización (resta) del stock en una sola
 * operación atómica.
 * </p>
 * * @version 1.2
 */
public class VentaDAO {

	/**
	 * Registra una venta completa en la base de datos de manera transaccional.
	 * <p>
	 * <b>Flujo de la Transacción (ACID):</b>
	 * <ol>
	 * <li>Desactiva el auto-commit.</li>
	 * <li>Inserta la cabecera en {@code TablaVentas} (incluyendo impuestos y corte
	 * ID).</li>
	 * <li>Recupera el ID generado (Folio).</li>
	 * <li>Para cada detalle en la lista:
	 * <ul>
	 * <li>Inserta el registro en {@code TablaVentaDetalle}.</li>
	 * <li>Ejecuta un {@code UPDATE} en {@code TablaAlmacen_Productos} restando la
	 * cantidad vendida.</li>
	 * </ul>
	 * </li>
	 * <li>Si todo es correcto, hace {@code commit()}. Si falla algo, hace
	 * {@code rollback()}.</li>
	 * </ol>
	 * </p>
	 * * @param venta Objeto {@link Venta} con todos los datos y la lista de
	 * detalles cargada.
	 * 
	 * @return {@code true} si la venta se procesó exitosamente.
	 */
	public boolean agregar(Venta venta) {
		// SQL incluyendo todas las columnas financieras y de auditoría
		String sqlVenta = "INSERT INTO TablaVentas(ClienteID, EmpleadoID, FechaVenta, Total, MetodoPago, Subtotal, Descuento, Impuestos, CorteID) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		String sqlDetalle = "INSERT INTO TablaVentaDetalle(VentaID, ProductoID, Cantidad, PrecioUnitario, Subtotal) VALUES (?, ?, ?, ?, ?)";
		// SQL para descontar inventario automáticamente
		String sqlUpdateStock = "UPDATE TablaAlmacen_Productos SET Cantidad = Cantidad - ? WHERE Pid = ?";

		Connection con = null;
		boolean exito = false;

		try {
			con = Conexion.getConexion();
			con.setAutoCommit(false); // Inicia la transacción manual

			// 1. Insertar Cabecera
			try (PreparedStatement psVenta = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
				psVenta.setInt(1, venta.getClienteId());
				psVenta.setInt(2, venta.getEmpleadoId());
				psVenta.setTimestamp(3, new java.sql.Timestamp(venta.getFecha().getTime()));

				// Campos financieros
				psVenta.setDouble(4, venta.getTotal()); // Total FINAL a pagar
				psVenta.setString(5, venta.getMetodoPago()); // "Efectivo" o "Tarjeta"
				psVenta.setDouble(6, venta.getSubtotal()); // Suma bruta de productos
				psVenta.setDouble(7, venta.getDescuento()); // Descuento aplicado
				psVenta.setDouble(8, venta.getImpuestos()); // IVA calculado
				psVenta.setInt(9, venta.getCorteID()); // Vinculación con el turno de caja

				psVenta.executeUpdate();

				// 2. Recuperar ID generado (Folio del Ticket)
				try (ResultSet generatedKeys = psVenta.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						int ventaIdGenerada = generatedKeys.getInt(1);
						venta.setid(ventaIdGenerada);

						// 3. Procesar Detalles y Stock
						for (VentaDetalle detalle : venta.getDetalles()) {
							// 3.1 Guardar detalle
							try (PreparedStatement psDetalle = con.prepareStatement(sqlDetalle)) {
								psDetalle.setInt(1, ventaIdGenerada);
								psDetalle.setInt(2, detalle.getProductoId());
								psDetalle.setInt(3, detalle.getCantidad());
								psDetalle.setDouble(4, detalle.getPrecioUnitario());
								psDetalle.setDouble(5, detalle.getSubtotal());
								psDetalle.executeUpdate();
							}
							// 3.2 Actualizar inventario (Restar)
							try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdateStock)) {
								psUpdate.setInt(1, detalle.getCantidad());
								psUpdate.setInt(2, detalle.getProductoId());
								psUpdate.executeUpdate();
							}
						}
					}
				}
			}
			con.commit(); // Confirmar cambios permanentemente
			exito = true;
		} catch (SQLException e) {
			System.err.println("Error al registrar la venta, haciendo rollback: " + e.getMessage());
			try {
				if (con != null) {
					con.rollback(); // Revertir cambios en caso de error
				}
			} catch (SQLException ex) {
				System.err.println("Error al hacer rollback: " + ex.getMessage());
			}
		} finally {
			try {
				if (con != null) {
					con.setAutoCommit(true); // Restaurar comportamiento por defecto
					con.close();
				}
			} catch (SQLException e) {
				System.err.println("Error al cerrar la conexión: " + e.getMessage());
			}
		}
		return exito;
	}

	/**
	 * Recupera el historial completo de ventas.
	 * <p>
	 * Realiza JOINs con Clientes y Empleados para mostrar nombres en lugar de IDs.
	 * </p>
	 * * @return Lista de ventas ordenadas por ID descendente.
	 */
	public List<Venta> obtenerTodasLasVentas() {
		List<Venta> ventas = new ArrayList<>();

		String sql = "SELECT v.VentaID, v.FechaVenta, c.NombreC, e.NombreE, v.Total " + "FROM TablaVentas v "
				+ "JOIN TablaClientes c ON v.ClienteID = c.Cid " + "JOIN TablaEmpleados e ON v.EmpleadoID = e.Eid "
				+ "ORDER BY v.VentaID DESC";

		try (Connection con = Conexion.getConexion();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Venta venta = new Venta();

				venta.setid(rs.getInt("VentaID"));
				venta.setFecha(rs.getTimestamp("FechaVenta"));
				venta.setNombreCliente(rs.getString("NombreC"));
				venta.setNombreEmpleado(rs.getString("NombreE"));
				venta.setTotal(rs.getDouble("Total"));
				ventas.add(venta);
			}
		} catch (SQLException e) {
			System.err.println("Error al obtener el historial de ventas: " + e.getMessage());
		}
		return ventas;
	}

	/**
	 * Busca una venta específica por su folio. * @param id Número de ticket/venta.
	 * 
	 * @return Objeto {@link Venta} (Cabecera) o {@code null}.
	 */
	public Venta buscarVentaPorID(int id) {
		Venta venta = null;

		String sql = "SELECT v.VentaID, v.FechaVenta, c.NombreC, e.NombreE, v.Total " + "FROM TablaVentas v "
				+ "JOIN TablaClientes c ON v.ClienteID = c.Cid " + "JOIN TablaEmpleados e ON v.EmpleadoID = e.Eid "
				+ "WHERE v.VentaID = ?";

		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					venta = new Venta();

					venta.setid(rs.getInt("VentaID"));
					venta.setFecha(rs.getTimestamp("FechaVenta"));
					venta.setNombreCliente(rs.getString("NombreC"));
					venta.setNombreEmpleado(rs.getString("NombreE"));
					venta.setTotal(rs.getDouble("Total"));
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al buscar venta por ID: " + e.getMessage());
		}
		return venta;
	}

	/**
	 * Filtra las ventas realizadas en un rango de fechas.
	 * <p>
	 * Utilizado para generar reportes mensuales o diarios.
	 * </p>
	 * * @param fechaInicio Inicio del rango.
	 * 
	 * @param fechaFin Fin del rango.
	 * @return Lista de ventas dentro del periodo.
	 */
	public List<Venta> obtenerVentasPorFecha(Date fechaInicio, Date fechaFin) {
		List<Venta> ventas = new ArrayList<>();

		String sql = "SELECT v.VentaID, v.FechaVenta, c.NombreC, e.NombreE, v.Total " + "FROM TablaVentas v "
				+ "JOIN TablaClientes c ON v.ClienteID = c.Cid " + "JOIN TablaEmpleados e ON v.EmpleadoID = e.Eid "
				+ "WHERE v.FechaVenta BETWEEN ? AND ? " + "ORDER BY v.VentaID DESC";

		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setTimestamp(1, new java.sql.Timestamp(fechaInicio.getTime()));
			ps.setTimestamp(2, new java.sql.Timestamp(fechaFin.getTime()));

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Venta venta = new Venta();

					venta.setid(rs.getInt("VentaID"));
					venta.setFecha(rs.getTimestamp("FechaVenta"));
					venta.setNombreCliente(rs.getString("NombreC"));
					venta.setNombreEmpleado(rs.getString("NombreE"));
					venta.setTotal(rs.getDouble("Total"));
					ventas.add(venta);
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al obtener ventas por fecha: " + e.getMessage());
		}
		return ventas;
	}

	/**
	 * Obtiene un resumen financiero agrupado por método de pago para un corte de
	 * caja específico.
	 * <p>
	 * Esencial para el Cierre de Caja: permite saber cuánto se vendió en "Efectivo"
	 * (para comparar con el dinero físico) y cuánto en "Tarjeta" (dinero digital).
	 * </p>
	 * * @param corteID ID del turno a consultar.
	 * 
	 * @return Lista de objetos Venta simplificados donde {@code MetodoPago} y
	 *         {@code Total} contienen el resumen.
	 */
	public List<Venta> obtenerTotalesPorMetodoPago(int corteID) {
		List<Venta> resumen = new ArrayList<>();

		String sql = "SELECT MetodoPago, SUM(Total) as SumaTotal " + "FROM TablaVentas " + "WHERE CorteID = ? "
				+ "GROUP BY MetodoPago";

		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, corteID);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Venta ventaResumen = new Venta();
					ventaResumen.setMetodoPago(rs.getString("MetodoPago"));
					ventaResumen.setTotal(rs.getDouble("SumaTotal")); // Usamos el campo Total para guardar la suma
					resumen.add(ventaResumen);
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al obtener totales por método de pago: " + e.getMessage());
		}
		return resumen;
	}

	/**
	 * Procesa la devolución/cancelación de una venta.
	 * <p>
	 * <b>Lógica Inversa:</b>
	 * <ol>
	 * <li>Consulta los productos vendidos en esa transacción.</li>
	 * <li>Suma las cantidades de vuelta al inventario (Restock).</li>
	 * <li>Anula el monto total de la venta (lo pone en 0) para que no afecte el
	 * cierre de caja, pero mantiene el registro histórico.</li>
	 * </ol>
	 * </p>
	 * * @param ventaId ID de la venta a cancelar.
	 * 
	 * @return {@code true} si la devolución se procesó correctamente.
	 */
	public boolean realizarDevolucion(int ventaId) {
		Connection con = null;
		boolean exito = false;

		String sqlDetalles = "SELECT ProductoID, Cantidad FROM TablaVentaDetalle WHERE VentaID = ?";
		String sqlRestock = "UPDATE TablaAlmacen_Productos SET Cantidad = Cantidad + ? WHERE Pid = ?";
		// Anulamos el valor financiero para que no cuente en el corte
		String sqlAnular = "UPDATE TablaVentas SET Total = 0, ClienteID = NULL WHERE VentaID = ?";

		try {
			con = Conexion.getConexion();
			con.setAutoCommit(false); // Transacción segura

			// Paso 1: Recuperar productos
			try (PreparedStatement psDet = con.prepareStatement(sqlDetalles)) {
				psDet.setInt(1, ventaId);
				try (ResultSet rs = psDet.executeQuery()) {
					// Preparamos la consulta de restock
					try (PreparedStatement psRestock = con.prepareStatement(sqlRestock)) {
						while (rs.next()) {
							int prodId = rs.getInt("ProductoID");
							int cant = rs.getInt("Cantidad");

							// Paso 2: Devolver al stock uno por uno
							psRestock.setInt(1, cant); // Sumar cantidad
							psRestock.setInt(2, prodId); // Al producto ID
							psRestock.executeUpdate();
						}
					}
				}
			}

			// Paso 3: Marcar venta como devuelta/cancelada
			try (PreparedStatement psAnular = con.prepareStatement(sqlAnular)) {
				psAnular.setInt(1, ventaId);
				psAnular.executeUpdate();
			}

			con.commit(); // Confirmar cambios
			exito = true;

		} catch (SQLException e) {
			System.err.println("Error en devolución: " + e.getMessage());
			try {
				if (con != null)
					con.rollback();
			} catch (SQLException ex) {
			}
		} finally {
			try {
				if (con != null) {
					con.setAutoCommit(true);
					con.close();
				}
			} catch (SQLException e) {
			}
		}
		return exito;
	}
}