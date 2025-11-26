package conexion;

public class Conexion_SQL extends ConexionBD {

    public Conexion_SQL(String servidor, String baseDatos, String usuario, String password) {
        super(servidor, baseDatos, usuario, password);
    }

    @Override
    protected String construirURL(String servidor, String baseDatos) {
        // Configuramos la URL específica para SQL Server
        // Se agrega encrypt=false y trustServerCertificate=true para evitar errores de SSL comunes
        return "jdbc:sqlserver://" + servidor + ";databaseName=" + baseDatos + ";encrypt=false;trustServerCertificate=true";
    }
}