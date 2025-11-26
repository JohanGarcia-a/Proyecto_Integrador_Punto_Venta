package modelo;

import java.util.Date;
import modelogenerico.Entidad;

public class MovimientoCaja implements Entidad {

    private int id;
    private int corteId;
    private int usuarioId;
    private Date fecha;
    private String tipoMovimiento; // "Ingreso" o "Egreso"
    private double monto;
    private String descripcion;

    // Campos extra para mostrar información (JOINs)
    private String nombreUsuario;

    public MovimientoCaja() {
    }

    // Constructor para GUARDAR un nuevo movimiento (sin ID, fecha actual)
    public MovimientoCaja(int corteId, int usuarioId, String tipoMovimiento, double monto, String descripcion) {
        this.corteId = corteId;
        this.usuarioId = usuarioId;
        this.tipoMovimiento = tipoMovimiento;
        this.monto = monto;
        this.descripcion = descripcion;
        this.fecha = new Date(); // Asignamos fecha actual por defecto
    }

    // Constructor para LEER de la base de datos (con todos los datos)
    public MovimientoCaja(int id, int corteId, int usuarioId, Date fecha, String tipoMovimiento, double monto, String descripcion, String nombreUsuario) {
        this.id = id;
        this.corteId = corteId;
        this.usuarioId = usuarioId;
        this.fecha = fecha;
        this.tipoMovimiento = tipoMovimiento;
        this.monto = monto;
        this.descripcion = descripcion;
        this.nombreUsuario = nombreUsuario;
    }

    // --- Métodos de la interfaz Entidad ---

    @Override
    public int getid() {
        return this.id;
    }

    @Override
    public void setid(int id) {
        this.id = id;
    }

    @Override
    public Object[] toTableRow() {
        // Esto define qué columnas se verán en la tabla del panel
        return new Object[] {
            this.id,
            this.fecha,
            this.nombreUsuario,
            this.tipoMovimiento,
            this.monto,
            this.descripcion
        };
    }

    @Override
    public String toString() {
        return tipoMovimiento + ": $" + monto;
    }

    // --- Getters y Setters ---

    public int getCorteId() {
        return corteId;
    }

    public void setCorteId(int corteId) {
        this.corteId = corteId;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
}