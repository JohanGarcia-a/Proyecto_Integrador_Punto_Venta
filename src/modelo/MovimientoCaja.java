package modelo;

import java.util.Date;
import modelogenerico.Entidad;

/**
 * Entidad que representa un movimiento financiero manual (Ingreso o Egreso)
 * dentro de un turno de caja.
 * <p>
 * Mapea la tabla <b>TablaMovimientosCaja</b>. Se utiliza para registrar
 * operaciones que afectan el saldo de la caja pero que no son ventas de
 * productos (ej. "Pago de luz", "Retiro de efectivo", "Ingreso de cambio
 * inicial extra").
 * </p>
 * <p>
 * Estos registros se suman o restan al cálculo del "Monto Final Sistema" en el
 * cierre de caja.
 * </p>
 * * @version 1.0
 */
public class MovimientoCaja implements Entidad {

	/** Identificador único del movimiento (PK). */
	private int id;

	/** Identificador del corte de caja al que pertenece este movimiento (FK). */
	private int corteId;

	/** Identificador del usuario que registró el movimiento (FK). */
	private int usuarioId;

	/** Fecha y hora exacta del registro. */
	private Date fecha;

	/** Tipo de operación. Valores esperados: "Ingreso" o "Egreso". */
	private String tipoMovimiento;

	/** Cantidad monetaria del movimiento. */
	private double monto;

	/** Nota explicativa o justificación del movimiento. */
	private String descripcion;

	/**
	 * * Nombre del usuario responsable.
	 * <p>
	 * Atributo no persistente en esta tabla, obtenido mediante JOIN con
	 * TablaEmpleados para mostrar en la interfaz.
	 * </p>
	 */
	private String nombreUsuario;

	/**
	 * Constructor vacío por defecto.
	 */
	public MovimientoCaja() {
	}

	/**
	 * Constructor para <b>GUARDAR</b> un nuevo movimiento.
	 * <p>
	 * Se utiliza desde el {@code ControladorMovimientosCaja}. Asigna
	 * automáticamente la fecha y hora actual ({@code new Date()}) al momento de
	 * crear el objeto.
	 * </p>
	 * * @param corteId ID del corte de caja activo.
	 * 
	 * @param usuarioId      ID del empleado logueado.
	 * @param tipoMovimiento "Ingreso" o "Egreso".
	 * @param monto          Cantidad de dinero.
	 * @param descripcion    Razón del movimiento.
	 */
	public MovimientoCaja(int corteId, int usuarioId, String tipoMovimiento, double monto, String descripcion) {
		this.corteId = corteId;
		this.usuarioId = usuarioId;
		this.tipoMovimiento = tipoMovimiento;
		this.monto = monto;
		this.descripcion = descripcion;
		this.fecha = new Date(); // Asignamos fecha actual por defecto
	}

	/**
	 * Constructor para <b>LEER</b> el historial desde la base de datos.
	 * <p>
	 * Utilizado por el DAO para mapear los resultados de la consulta SQL,
	 * incluyendo el nombre del usuario obtenido por JOIN.
	 * </p>
	 * * @param id ID del movimiento.
	 * 
	 * @param corteId        ID del corte asociado.
	 * @param usuarioId      ID del usuario responsable.
	 * @param fecha          Fecha del movimiento.
	 * @param tipoMovimiento Tipo ("Ingreso"/"Egreso").
	 * @param monto          Cantidad.
	 * @param descripcion    Motivo.
	 * @param nombreUsuario  Nombre del empleado (Dato enriquecido).
	 */
	public MovimientoCaja(int id, int corteId, int usuarioId, Date fecha, String tipoMovimiento, double monto,
			String descripcion, String nombreUsuario) {
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

	/**
	 * Genera un arreglo de objetos para visualizar el movimiento en la tabla del
	 * panel.
	 * <p>
	 * Orden de columnas: [ID, Fecha, Usuario, Tipo, Monto, Descripción].
	 * </p>
	 * 
	 * @return Arreglo de datos para la fila.
	 */
	@Override
	public Object[] toTableRow() {
		// Esto define qué columnas se verán en la tabla del panel
		return new Object[] { this.id, this.fecha, this.nombreUsuario, this.tipoMovimiento, this.monto,
				this.descripcion };
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