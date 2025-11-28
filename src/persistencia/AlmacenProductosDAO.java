package persistencia;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import conexion.Conexion;
import modelo.AlmacenProductos;
import modelogenerico.BaseDAO;

/**
 * Clase de Acceso a Datos (DAO) para la entidad {@link AlmacenProductos}.
 * <p>
 * Gestiona todas las operaciones CRUD y consultas especializadas sobre la tabla
 * <b>TablaAlmacen_Productos</b> en la base de datos SQL Server.
 * </p>
 * <p>
 * Implementa la interfaz genérica {@link BaseDAO} para estandarizar el acceso.
 * </p>
 * * @version 1.2
 */
public class AlmacenProductosDAO implements BaseDAO<AlmacenProductos> {

	/**
	 * Busca un producto por su clave primaria (ID).
	 * <p>
	 * Ejecuta una consulta con <b>INNER JOIN</b> a las tablas de Proveedores y
	 * Categorías para recuperar los nombres descriptivos asociados, no solo los
	 * IDs.
	 * </p>
	 * * @param id Identificador único del producto.
	 * 
	 * @return Objeto {@link AlmacenProductos} encontrado o {@code null} si no
	 *         existe.
	 */
	@Override
	public AlmacenProductos buscarPorID(int id) {
		String sql = "SELECT p.*, tp.NombreP, tc.Nombre AS NombreCategoria " + "FROM TablaAlmacen_Productos p "
				+ "INNER JOIN TablaProveedores tp ON p.ProveedorID = tp.Pid "
				+ "INNER JOIN TablaCategorias tc ON p.CategoriaID = tc.Cid " + "WHERE p.Pid=?";
		AlmacenProductos productoEncontrado = null;

		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					productoEncontrado = new AlmacenProductos(rs.getInt("Pid"), rs.getString("Nombre"),
							rs.getString("Descripcion"), rs.getDouble("Precio"), rs.getString("Codigo"),
							rs.getInt("Cantidad"), rs.getString("Ruta_Imagen"), rs.getInt("CategoriaID"),
							rs.getString("NombreCategoria"), rs.getInt("ProveedorID"), rs.getString("NombreP"),
							rs.getInt("StockMinimo"));
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al buscar el producto: " + e.toString());
		}
		return productoEncontrado;
	}

	/**
	 * Busca un producto utilizando su código de barras o SKU exacto. * @param
	 * codigo Cadena del código a buscar.
	 * 
	 * @return Objeto {@link AlmacenProductos} o {@code null}.
	 */
	public AlmacenProductos buscarPorCodigo(String codigo) {
		String sql = "SELECT p.*, tp.NombreP, tc.Nombre AS NombreCategoria " + "FROM TablaAlmacen_Productos p "
				+ "INNER JOIN TablaProveedores tp ON p.ProveedorID = tp.Pid "
				+ "INNER JOIN TablaCategorias tc ON p.CategoriaID = tc.Cid " + "WHERE p.Codigo=?";
		AlmacenProductos productoEncontrado = null;

		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, codigo);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					productoEncontrado = new AlmacenProductos(rs.getInt("Pid"), rs.getString("Nombre"),
							rs.getString("Descripcion"), rs.getDouble("Precio"), rs.getString("Codigo"),
							rs.getInt("Cantidad"), rs.getString("Ruta_Imagen"), rs.getInt("CategoriaID"),
							rs.getString("NombreCategoria"), rs.getInt("ProveedorID"), rs.getString("NombreP"),
							rs.getInt("StockMinimo"));
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al buscar por código: " + e.toString());
		}
		return productoEncontrado;
	}

	/**
	 * Realiza una búsqueda aproximada de productos por nombre.
	 * <p>
	 * Utiliza el operador SQL <b>LIKE</b> con comodines (%) para encontrar
	 * coincidencias parciales.
	 * </p>
	 * * @param nombre Fragmento del nombre a buscar.
	 * 
	 * @return Lista de productos que coinciden con el criterio.
	 */
	public List<AlmacenProductos> buscarPorNombre(String nombre) {
		String sql = "SELECT p.*, tp.NombreP, tc.Nombre AS NombreCategoria " + "FROM TablaAlmacen_Productos p "
				+ "INNER JOIN TablaProveedores tp ON p.ProveedorID = tp.Pid "
				+ "INNER JOIN TablaCategorias tc ON p.CategoriaID = tc.Cid " + "WHERE p.Nombre LIKE ?";
		List<AlmacenProductos> productosEncontrados = new ArrayList<>();

		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, "%" + nombre + "%");
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					AlmacenProductos producto = new AlmacenProductos(rs.getInt("Pid"), rs.getString("Nombre"),
							rs.getString("Descripcion"), rs.getDouble("Precio"), rs.getString("Codigo"),
							rs.getInt("Cantidad"), rs.getString("Ruta_Imagen"), rs.getInt("CategoriaID"),
							rs.getString("NombreCategoria"), rs.getInt("ProveedorID"), rs.getString("NombreP"),
							rs.getInt("StockMinimo"));
					productosEncontrados.add(producto);
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al buscar por nombre: " + e.toString());
		}
		return productosEncontrados;
	}

	/**
	 * Recupera el catálogo completo de productos. * @return Lista con todos los
	 * registros de la base de datos.
	 */
	@Override
	public List<AlmacenProductos> ObtenerTodo() {
		List<AlmacenProductos> productos = new ArrayList<>();
		String sql = "SELECT p.*, tp.NombreP, tc.Nombre AS NombreCategoria " + "FROM TablaAlmacen_Productos p "
				+ "INNER JOIN TablaProveedores tp ON p.ProveedorID = tp.Pid "
				+ "INNER JOIN TablaCategorias tc ON p.CategoriaID = tc.Cid";

		try (Connection con = Conexion.getConexion();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				productos
						.add(new AlmacenProductos(rs.getInt("Pid"), rs.getString("Nombre"), rs.getString("Descripcion"),
								rs.getDouble("Precio"), rs.getString("Codigo"), rs.getInt("Cantidad"),
								rs.getString("Ruta_Imagen"), rs.getInt("CategoriaID"), rs.getString("NombreCategoria"),
								rs.getInt("ProveedorID"), rs.getString("NombreP"), rs.getInt("StockMinimo")));
			}
		} catch (SQLException e) {
			System.err.println("Error al obtener productos: " + e.toString());
		}
		return productos;
	}

	/**
	 * Inserta un nuevo producto en el inventario. * @param entidad Objeto con los
	 * datos a guardar.
	 * 
	 * @return {@code true} si la operación fue exitosa.
	 */
	@Override
	public boolean agregar(AlmacenProductos entidad) {
		String sql = "INSERT INTO TablaAlmacen_Productos(Nombre,Descripcion,Precio,Codigo,Cantidad,Ruta_Imagen,ProveedorID,CategoriaID,StockMinimo) VALUES (?,?,?,?,?,?,?,?,?)";
		boolean exito = false;
		try (Connection con = Conexion.getConexion();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, entidad.getNombre());
			ps.setString(2, entidad.getDescripcion());
			ps.setBigDecimal(3, BigDecimal.valueOf(entidad.getPrecio()));
			ps.setString(4, entidad.getCodigo());
			ps.setInt(5, entidad.getCantidad());
			ps.setString(6, entidad.getRuta());
			ps.setInt(7, entidad.getProveedorId());
			ps.setInt(8, entidad.getCategoriaId());
			ps.setInt(9, entidad.getStockMinimo());

			if (ps.executeUpdate() > 0) {
				exito = true;
			}
		} catch (SQLException e) {
			System.err.println("Producto no agregado: " + e.toString());
		}
		return exito;
	}

	/**
	 * Actualiza la información de un producto existente. * @param entidad Objeto
	 * con los datos modificados y el ID original.
	 * 
	 * @return {@code true} si se actualizó al menos un registro.
	 */
	@Override
	public boolean modificar(AlmacenProductos entidad) {
		String sql = "UPDATE TablaAlmacen_Productos SET Nombre=?, Descripcion=?, Precio=?, Codigo=?, Cantidad=?, Ruta_Imagen=?, ProveedorID=?, CategoriaID=?, StockMinimo=? WHERE Pid=?";
		boolean exito = false;

		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, entidad.getNombre());
			ps.setString(2, entidad.getDescripcion());
			ps.setDouble(3, entidad.getPrecio());
			ps.setString(4, entidad.getCodigo());
			ps.setInt(5, entidad.getCantidad());
			ps.setString(6, entidad.getRuta());
			ps.setInt(7, entidad.getProveedorId());
			ps.setInt(8, entidad.getCategoriaId());
			ps.setInt(9, entidad.getStockMinimo());
			ps.setInt(10, entidad.getid());

			if (ps.executeUpdate() > 0) {
				exito = true;
			}
		} catch (SQLException e) {
			System.err.println("Error al modificar producto: " + e.toString());
		}
		return exito;
	}

	/**
	 * Elimina físicamente un producto de la base de datos. * @param id ID del
	 * producto a borrar.
	 * 
	 * @return {@code true} si la operación fue exitosa.
	 */
	@Override
	public boolean borrar(int id) {
		String sql = "DELETE FROM TablaAlmacen_Productos WHERE Pid=?";
		boolean exito = false;
		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, id);
			if (ps.executeUpdate() > 0) {
				exito = true;
			}
		} catch (SQLException e) {
			System.err.println("Error al borrar producto: " + e.toString());
		}
		return exito;
	}

	/**
	 * Reporte Especial: Obtiene productos cuyo stock actual es menor o igual al
	 * mínimo.
	 * <p>
	 * Utilizado para generar alertas de reabastecimiento.
	 * </p>
	 * * @return Lista de productos en estado crítico.
	 */
	public List<AlmacenProductos> obtenerProductosConStockBajo() {
		List<AlmacenProductos> productos = new ArrayList<>();
		String sql = "SELECT p.*, tp.NombreP, tc.Nombre AS NombreCategoria " + "FROM TablaAlmacen_Productos p "
				+ "INNER JOIN TablaProveedores tp ON p.ProveedorID = tp.Pid "
				+ "INNER JOIN TablaCategorias tc ON p.CategoriaID = tc.Cid " + "WHERE p.Cantidad <= p.StockMinimo "
				+ "ORDER BY p.Cantidad ASC";

		try (Connection con = Conexion.getConexion();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				productos
						.add(new AlmacenProductos(rs.getInt("Pid"), rs.getString("Nombre"), rs.getString("Descripcion"),
								rs.getDouble("Precio"), rs.getString("Codigo"), rs.getInt("Cantidad"),
								rs.getString("Ruta_Imagen"), rs.getInt("CategoriaID"), rs.getString("NombreCategoria"),
								rs.getInt("ProveedorID"), rs.getString("NombreP"), rs.getInt("StockMinimo")));
			}
		} catch (SQLException e) {
			System.err.println("Error al obtener productos con stock bajo: " + e.toString());
		}
		return productos;
	}

	/**
	 * Incrementa la cantidad de stock de un producto específico.
	 * <p>
	 * Utilizado al recibir pedidos de proveedores o por ajustes manuales.
	 * </p>
	 * * @param productoId ID del producto.
	 * 
	 * @param cantidadAAgregar Cantidad a sumar al stock actual.
	 * @return {@code true} si la actualización fue correcta.
	 */
	public boolean aumentarStock(int productoId, int cantidadAAgregar) {
		String sql = "UPDATE TablaAlmacen_Productos SET Cantidad = Cantidad + ? WHERE Pid = ?";

		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, cantidadAAgregar);
			ps.setInt(2, productoId);

			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			System.err.println("Error al aumentar stock: " + e.toString());
			return false;
		}
	}
}