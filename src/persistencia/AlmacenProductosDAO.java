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

public class AlmacenProductosDAO implements BaseDAO<AlmacenProductos> {

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

	// --- NUEVO MÉTODO PARA REPORTES ---
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