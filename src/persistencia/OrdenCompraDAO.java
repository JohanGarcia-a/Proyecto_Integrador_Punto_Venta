package persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import conexion.Conexion;
import modelo.OrdenCompra;
import modelo.OrdenCompraDetalle;
import modelogenerico.BaseDAO;

public class OrdenCompraDAO implements BaseDAO<OrdenCompra> {

	// Este método es una TRANSACCIÓN (como en VentaDAO)
	@Override
	public boolean agregar(OrdenCompra orden) {
		String sqlOrden = "INSERT INTO TablaOrdenesCompra(ProveedorID, Fecha, Status) VALUES (?, ?, ?)";
		// --- CAMBIO 1: Añadir Descripcion al SQL ---
		String sqlDetalle = "INSERT INTO TablaOrdenCompraDetalle(OrdenID, ProductoID, CantidadPedida, CostoUnitario, Descripcion) VALUES (?, ?, ?, ?, ?)";
		Connection con = null;
		boolean exito = false;

		try {
			con = Conexion.getConexion();
			con.setAutoCommit(false); // Iniciar transacción

			// 1. Insertar la Orden Maestra
			try (PreparedStatement psOrden = con.prepareStatement(sqlOrden, Statement.RETURN_GENERATED_KEYS)) {
				psOrden.setInt(1, orden.getProveedorId());
				psOrden.setTimestamp(2, new java.sql.Timestamp(orden.getFecha().getTime()));
				psOrden.setString(3, orden.getStatus());
				psOrden.executeUpdate();

				// 2. Obtener el ID generado
				try (ResultSet generatedKeys = psOrden.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						int ordenIdGenerada = generatedKeys.getInt(1);
						orden.setid(ordenIdGenerada); // Actualizar el ID en el objeto

						// 3. Insertar cada Detalle
						for (OrdenCompraDetalle detalle : orden.getDetalles()) {
							try (PreparedStatement psDetalle = con.prepareStatement(sqlDetalle)) {
								psDetalle.setInt(1, ordenIdGenerada);
								psDetalle.setInt(2, detalle.getProductoId());
								psDetalle.setInt(3, detalle.getCantidadPedida());
								psDetalle.setDouble(4, detalle.getCostoUnitario());
								// --- CAMBIO 2: Añadir el parámetro para Descripcion ---
								psDetalle.setString(5, detalle.getDescripcion());
								psDetalle.executeUpdate();
							}
						}
					}
				}
			}
			con.commit(); // Confirmar transacción
			exito = true;
		} catch (SQLException e) {
			System.err.println("Error al registrar la orden de compra, haciendo rollback: " + e.getMessage());
			try {
				if (con != null)
					con.rollback();
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

	// Método para cambiar el estado (ej. de "Pendiente" a "Recibido")
	public boolean modificarStatus(int ordenId, String nuevoStatus) {
		String sql = "UPDATE TablaOrdenesCompra SET Status = ? WHERE OrdenID = ?";
		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, nuevoStatus);
			ps.setInt(2, ordenId);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error al modificar status de la orden: " + e.toString());
			return false;
		}
	}

	@Override
	public OrdenCompra buscarPorID(int id) {
		String sql = "SELECT o.*, p.NombreP " + "FROM TablaOrdenesCompra o "
				+ "INNER JOIN TablaProveedores p ON o.ProveedorID = p.Pid " + "WHERE o.OrdenID = ?";
		OrdenCompra orden = null;

		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					orden = new OrdenCompra(rs.getInt("OrdenID"), rs.getInt("ProveedorID"), rs.getTimestamp("Fecha"),
							rs.getString("Status"), rs.getString("NombreP"));
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al buscar orden por ID: " + e.getMessage());
		}
		return orden;
	}

	@Override
	public List<OrdenCompra> ObtenerTodo() {
		List<OrdenCompra> ordenes = new ArrayList<>();
		String sql = "SELECT o.*, p.NombreP " + "FROM TablaOrdenesCompra o "
				+ "INNER JOIN TablaProveedores p ON o.ProveedorID = p.Pid " + "ORDER BY o.Fecha DESC";

		try (Connection con = Conexion.getConexion();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				ordenes.add(new OrdenCompra(rs.getInt("OrdenID"), rs.getInt("ProveedorID"), rs.getTimestamp("Fecha"),
						rs.getString("Status"), rs.getString("NombreP")));
			}
		} catch (SQLException e) {
			System.err.println("Error al obtener todas las órdenes: " + e.getMessage());
		}
		return ordenes;
	}

	@Override
	public boolean modificar(OrdenCompra entidad) {
		String sql = "UPDATE TablaOrdenesCompra SET ProveedorID = ?, Fecha = ?, Status = ? WHERE OrdenID = ?";
		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, entidad.getProveedorId());
			ps.setTimestamp(2, new java.sql.Timestamp(entidad.getFecha().getTime()));
			ps.setString(3, entidad.getStatus());
			ps.setInt(4, entidad.getid());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error al modificar la orden: " + e.toString());
			return false;
		}
	}

	@Override
	public boolean borrar(int id) {
		String sql = "DELETE FROM TablaOrdenesCompra WHERE OrdenID = ?";
		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error al borrar la orden: " + e.toString());
			return false;
		}
	}
}