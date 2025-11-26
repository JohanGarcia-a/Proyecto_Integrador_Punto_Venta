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

public class VentaDAO {

	public boolean agregar(Venta venta) {
		// --- CAMBIO 1: El SQL ahora incluye todas las columnas nuevas ---
		String sqlVenta = "INSERT INTO TablaVentas(ClienteID, EmpleadoID, FechaVenta, Total, MetodoPago, Subtotal, Descuento, Impuestos, CorteID) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		String sqlDetalle = "INSERT INTO TablaVentaDetalle(VentaID, ProductoID, Cantidad, PrecioUnitario, Subtotal) VALUES (?, ?, ?, ?, ?)";
		String sqlUpdateStock = "UPDATE TablaAlmacen_Productos SET Cantidad = Cantidad - ? WHERE Pid = ?";
		Connection con = null;
		boolean exito = false;

		try {
			con = Conexion.getConexion();
			con.setAutoCommit(false); // Inicia la transacción

			// --- CAMBIO 2: Se actualiza el PreparedStatement ---
			try (PreparedStatement psVenta = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
				psVenta.setInt(1, venta.getClienteId());
				psVenta.setInt(2, venta.getEmpleadoId());
				psVenta.setTimestamp(3, new java.sql.Timestamp(venta.getFecha().getTime()));

				// Estos son los nuevos campos que añadimos:
				psVenta.setDouble(4, venta.getTotal()); // El Total FINAL
				psVenta.setString(5, venta.getMetodoPago()); // "Efectivo" o "Tarjeta"
				psVenta.setDouble(6, venta.getSubtotal()); // La suma de productos
				psVenta.setDouble(7, venta.getDescuento()); // El monto descontado
				psVenta.setDouble(8, venta.getImpuestos()); // El monto de IVA
				psVenta.setInt(9, venta.getCorteID()); // El ID del corte de caja

				psVenta.executeUpdate();
				// --- FIN DEL CAMBIO 2 ---

				try (ResultSet generatedKeys = psVenta.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						int ventaIdGenerada = generatedKeys.getInt(1);
						venta.setid(ventaIdGenerada);

						// Bucle para detalles y stock (esto no cambia)
						for (VentaDetalle detalle : venta.getDetalles()) {
							try (PreparedStatement psDetalle = con.prepareStatement(sqlDetalle)) {
								psDetalle.setInt(1, ventaIdGenerada);
								psDetalle.setInt(2, detalle.getProductoId());
								psDetalle.setInt(3, detalle.getCantidad());
								psDetalle.setDouble(4, detalle.getPrecioUnitario());
								psDetalle.setDouble(5, detalle.getSubtotal());
								psDetalle.executeUpdate();
							}
							try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdateStock)) {
								psUpdate.setInt(1, detalle.getCantidad());
								psUpdate.setInt(2, detalle.getProductoId());
								psUpdate.executeUpdate();
							}
						}
					}
				}
			}
			con.commit(); // Finaliza la transacción
			exito = true;
		} catch (SQLException e) {
			System.err.println("Error al registrar la venta, haciendo rollback: " + e.getMessage());
			try {
				if (con != null) {
					con.rollback();
				}
			} catch (SQLException ex) {
				System.err.println("Error al hacer rollback: " + ex.getMessage());
			}
		} finally {
			try {
				if (con != null) {
					con.setAutoCommit(true);
					con.close();
				}
			} catch (SQLException e) {
				System.err.println("Error al cerrar la conexión: " + e.getMessage());
			}
		}
		return exito;
	}

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
	
	public boolean realizarDevolucion(int ventaId) {
		Connection con = null;
		boolean exito = false;

		// SQL 1: Obtener qué productos se vendieron y cuántos
		String sqlDetalles = "SELECT ProductoID, Cantidad FROM TablaVentaDetalle WHERE VentaID = ?";

		// SQL 2: Regresar esos productos al inventario
		String sqlRestock = "UPDATE TablaAlmacen_Productos SET Cantidad = Cantidad + ? WHERE Pid = ?";

		// SQL 3: Anular la venta visualmente (Total a 0 y marcar cliente)
		// Nota: Hacemos esto para no borrar el registro histórico, pero anular su valor
		// financiero.
		String sqlAnular = "UPDATE TablaVentas SET Total = 0, ClienteID = NULL WHERE VentaID = ?";
		// Opcional: Si tienes una columna 'Status', úsala. Si no, este truco funciona.

		try {
			con = Conexion.getConexion();
			con.setAutoCommit(false); // Iniciar transacción segura

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