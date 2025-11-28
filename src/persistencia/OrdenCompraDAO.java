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

/**
 * Clase de Acceso a Datos (DAO) para la entidad {@link OrdenCompra}.
 * <p>
 * Gestiona las operaciones CRUD de los pedidos a proveedores en la tabla
 * <b>TablaOrdenesCompra</b>.
 * </p>
 * <p>
 * <b>Características Clave:</b>
 * <ul>
 * <li>Manejo de Transacciones SQL para guardar Cabecera y Detalles
 * atómicamente.</li>
 * <li>Consultas con JOINs para recuperar nombres de proveedores.</li>
 * <li>Métodos especializados para cambiar el estatus del pedido (Flujo de
 * trabajo).</li>
 * </ul>
 * </p>
 * * @version 1.1
 */
public class OrdenCompraDAO implements BaseDAO<OrdenCompra> {

	/**
	 * Registra un nuevo pedido completo en la base de datos (Cabecera + Detalles).
	 * <p>
	 * <b>Manejo Transaccional:</b> Desactiva el auto-commit para ejecutar múltiples
	 * inserciones como una sola unidad de trabajo:
	 * <ol>
	 * <li>Inserta la orden en {@code TablaOrdenesCompra}.</li>
	 * <li>Recupera el ID generado.</li>
	 * <li>Recorre la lista de detalles e inserta cada uno en
	 * {@code TablaOrdenCompraDetalle} usando ese ID.</li>
	 * </ol>
	 * Si ocurre algún error en cualquier paso, se ejecuta un <b>ROLLBACK</b> para
	 * deshacer todo.
	 * </p>
	 * * @param orden Objeto {@link OrdenCompra} con la lista de productos cargada.
	 * 
	 * @return {@code true} si la transacción completa fue exitosa.
	 */
	@Override
	public boolean agregar(OrdenCompra orden) {
		String sqlOrden = "INSERT INTO TablaOrdenesCompra(ProveedorID, Fecha, Status) VALUES (?, ?, ?)";
		// Se incluye el campo Descripcion en el insert del detalle
		String sqlDetalle = "INSERT INTO TablaOrdenCompraDetalle(OrdenID, ProductoID, CantidadPedida, CostoUnitario, Descripcion) VALUES (?, ?, ?, ?, ?)";
		Connection con = null;
		boolean exito = false;

		try {
			con = Conexion.getConexion();
			con.setAutoCommit(false); // Iniciar transacción segura

			// 1. Insertar la Orden Maestra
			try (PreparedStatement psOrden = con.prepareStatement(sqlOrden, Statement.RETURN_GENERATED_KEYS)) {
				psOrden.setInt(1, orden.getProveedorId());
				psOrden.setTimestamp(2, new java.sql.Timestamp(orden.getFecha().getTime()));
				psOrden.setString(3, orden.getStatus());
				psOrden.executeUpdate();

				// 2. Obtener el ID generado por la BD
				try (ResultSet generatedKeys = psOrden.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						int ordenIdGenerada = generatedKeys.getInt(1);
						orden.setid(ordenIdGenerada); // Actualizar el ID en el objeto Java

						// 3. Insertar cada Detalle vinculado al ID maestro
						for (OrdenCompraDetalle detalle : orden.getDetalles()) {
							try (PreparedStatement psDetalle = con.prepareStatement(sqlDetalle)) {
								psDetalle.setInt(1, ordenIdGenerada);
								psDetalle.setInt(2, detalle.getProductoId());
								psDetalle.setInt(3, detalle.getCantidadPedida());
								psDetalle.setDouble(4, detalle.getCostoUnitario());
								psDetalle.setString(5, detalle.getDescripcion());
								psDetalle.executeUpdate();
							}
						}
					}
				}
			}
			con.commit(); // Confirmar cambios si todo salió bien
			exito = true;
		} catch (SQLException e) {
			System.err.println("Error al registrar la orden de compra, haciendo rollback: " + e.getMessage());
			try {
				if (con != null)
					con.rollback(); // Deshacer cambios en caso de error
			} catch (SQLException ex) {
				System.err.println("Error al hacer rollback: " + ex.getMessage());
			}
		} finally {
			try {
				if (con != null) {
					con.setAutoCommit(true); // Restaurar estado por defecto
					con.close();
				}
			} catch (SQLException e) {
				System.err.println("Error al cerrar la conexión: " + e.getMessage());
			}
		}
		return exito;
	}

	/**
	 * Actualiza el estado de una orden de compra.
	 * <p>
	 * Utilizado para avanzar el flujo de trabajo, por ejemplo: de "Pendiente"
	 * (Recién creada) a "Recibido" (Stock actualizado) o "Cancelado".
	 * </p>
	 * * @param ordenId ID de la orden a modificar.
	 * 
	 * @param nuevoStatus Nuevo estado (Texto).
	 * @return {@code true} si la actualización fue correcta.
	 */
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

	/**
	 * Busca una orden por su ID.
	 * <p>
	 * Realiza un {@code INNER JOIN} con {@code TablaProveedores} para obtener el
	 * nombre del proveedor asociado.
	 * </p>
	 * * @param id Identificador de la orden.
	 * 
	 * @return Objeto {@link OrdenCompra} (solo cabecera, sin detalles) o
	 *         {@code null}.
	 */
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

	/**
	 * Recupera el historial completo de órdenes de compra. * @return Lista de
	 * órdenes ordenadas por fecha descendente (más recientes primero).
	 */
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

	/**
	 * Modifica los datos generales de la orden (Proveedor, Fecha, Status). * @param
	 * entidad Objeto con los nuevos datos.
	 * 
	 * @return {@code true} si se actualizó.
	 */
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

	/**
	 * Elimina físicamente una orden de compra.
	 * <p>
	 * <b>Nota:</b> Requiere que se eliminen primero los detalles o que la base de
	 * datos tenga configurado {@code ON DELETE CASCADE}.
	 * </p>
	 * * @param id ID de la orden.
	 * 
	 * @return {@code true} si se eliminó.
	 */
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