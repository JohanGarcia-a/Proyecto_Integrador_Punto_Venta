package persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import conexion.Conexion;
import modelo.CorteCaja;

public class CorteCajaDAO {

    
    public int agregar(CorteCaja corte) {
        String sql = "INSERT INTO TablaCortesCaja (UsuarioID, FechaApertura, MontoInicial, Status) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, corte.getUsuarioID());
            ps.setTimestamp(2, new java.sql.Timestamp(corte.getFechaApertura().getTime()));
            ps.setDouble(3, corte.getMontoInicial());
            ps.setString(4, corte.getStatus()); // "Abierto"

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // Devuelve el CorteID generado
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al agregar el corte de caja: " + e.getMessage());
        }
        return -1; // Retorna -1 si falló
    }

    /**
     * Busca un corte de caja que esté 'Abierto' para un usuario específico en la fecha actual.
     * @param usuarioID El ID del empleado.
     * @return Un objeto CorteCaja si se encuentra, o null si no hay ninguno abierto hoy.
     */
    public CorteCaja buscarCorteAbiertoHoy(int usuarioID) {
        // SQL Server: CONVERT(DATE, ...) extrae solo la fecha (ignora la hora)
        String sql = "SELECT * FROM TablaCortesCaja " +
                     "WHERE UsuarioID = ? " +
                     "AND Status = 'Abierto' " +
                     "AND CONVERT(DATE, FechaApertura) = CONVERT(DATE, GETDATE())";
        
        CorteCaja corte = null;
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, usuarioID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Si encontramos uno, lo construimos
                    corte = new CorteCaja(
                        rs.getInt("CorteID"),
                        rs.getInt("UsuarioID"),
                        rs.getTimestamp("FechaApertura"),
                        rs.getDouble("MontoInicial"),
                        rs.getTimestamp("FechaCierre"),
                        rs.getDouble("MontoFinalSistema"),
                        rs.getDouble("MontoFinalContado"),
                        rs.getDouble("Diferencia"),
                        rs.getString("Status")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar corte abierto: " + e.getMessage());
        }
        return corte; // Devuelve el corte encontrado, o null
    }

    /**
     * Actualiza un corte de caja para marcarlo como 'Cerrado'.
     * (Lo usaremos más adelante en el módulo de Cierre de Caja).
     * @param corte El objeto CorteCaja con todos los datos del cierre.
     * @return true si se actualizó con éxito, false en caso contrario.
     */
    public boolean cerrarCorte(CorteCaja corte) {
        String sql = "UPDATE TablaCortesCaja SET " +
                     "FechaCierre = ?, " +
                     "MontoFinalSistema = ?, " +
                     "MontoFinalContado = ?, " +
                     "Diferencia = ?, " +
                     "Status = 'Cerrado' " +
                     "WHERE CorteID = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setTimestamp(1, new java.sql.Timestamp(corte.getFechaCierre().getTime()));
            ps.setDouble(2, corte.getMontoFinalSistema());
            ps.setDouble(3, corte.getMontoFinalContado());
            ps.setDouble(4, corte.getDiferencia());
            ps.setInt(5, corte.getCorteID());

            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al cerrar el corte de caja: " + e.getMessage());
            return false;
        }
    }
}