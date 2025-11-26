package persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import conexion.Conexion;
import modelo.Categorias;
import modelogenerico.BaseDAO;

public class CategoriaDAO implements BaseDAO<Categorias> {

	@Override
	public Categorias buscarPorID(int id) {
		String sql = "SELECT Cid, Nombre FROM TablaCategorias WHERE Cid=?";
		Categorias categoriaEncontrada = null;

		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					categoriaEncontrada = new Categorias(rs.getInt("Cid"), rs.getString("Nombre"));
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al buscar la categoría: " + e.toString());
		}
		return categoriaEncontrada;
	}
	

	public List<Categorias> ObtenerTodo() {
		List<Categorias> categorias = new ArrayList<>();
		String sql = "SELECT c.Cid, c.Nombre, COUNT(p.Pid) AS ConteoProductos " + "FROM TablaCategorias c "
				+ "LEFT JOIN TablaAlmacen_Productos p ON c.Cid = p.CategoriaID " + "GROUP BY c.Cid, c.Nombre "
				+ "ORDER BY c.Nombre";

		try (Connection con = Conexion.getConexion();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				categorias.add(new Categorias(rs.getInt("Cid"), rs.getString("Nombre"), rs.getInt("ConteoProductos")));
			}
		} catch (SQLException e) {
			System.err.println("Error al obtener categorías: " + e.getMessage());
		}
		return categorias;
	}

	@Override
	public boolean agregar(Categorias entidad) {
		String sql = "INSERT INTO TablaCategorias(Nombre) VALUES (?)";
		boolean exito = false;

		try (Connection con = Conexion.getConexion();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setString(1, entidad.getNombre());

			if (ps.executeUpdate() > 0) {
				try (ResultSet idGenerado = ps.getGeneratedKeys()) {
					if (idGenerado.next()) {
						entidad.setid(idGenerado.getInt(1));
						exito = true;
					}
				}
			}
		} catch (SQLException e) {
			System.err.println("Categoría no agregada: " + e.toString());
		}
		return exito;
	}

	@Override
	public boolean modificar(Categorias entidad) {
		String sql = "UPDATE TablaCategorias SET Nombre=? WHERE Cid=?";
		boolean exito = false;

		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, entidad.getNombre());
			ps.setInt(2, entidad.getid());
			if (ps.executeUpdate() > 0) {
				exito = true;
			}
		} catch (SQLException e) {
			System.err.println("Error al modificar categoría: " + e.toString());
		}
		return exito;
	}

	@Override
	public boolean borrar(int id) {
		String sql = "DELETE FROM TablaCategorias WHERE Cid=?";
		boolean exito = false;
		try (Connection con = Conexion.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, id);
			if (ps.executeUpdate() > 0) {
				exito = true;
			}
		} catch (SQLException e) {
			System.err.println("Error al borrar categoría: " + e.toString());
		}
		return exito;
	}
}