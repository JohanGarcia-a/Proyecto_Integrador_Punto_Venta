package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class Conexion {

	public static Connection getConexion() {
		String url = "jdbc:sqlserver://JOAHAN:1433;" + "database=MyPos;" + "user=sa;" + "password=3312;"
				+ "trustServerCertificate=true";

		try {

			Connection con = DriverManager.getConnection(url);

			return con;

		} catch (SQLException e) {

			JOptionPane.showMessageDialog(null, e.toString());

			return null;
			
		}
	}
}