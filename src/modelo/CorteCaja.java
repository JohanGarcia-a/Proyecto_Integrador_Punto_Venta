package modelo;

import java.util.Date;

/**
 * Entidad que representa un turno operativo o "Corte de Caja".
 * <p>
 * Esta clase mapea la tabla <b>TablaCortesCaja</b> y gestiona el ciclo de vida
 * financiero de un turno: Apertura (Fondo inicial), Operación y Cierre
 * (Conciliación).
 * </p>
 * <p>
 * Es fundamental para el cálculo de diferencias (Sobrantes/Faltantes) mediante
 * la técnica de "Corte Ciego".
 * </p>
 * * @version 1.2
 */
public class CorteCaja {

	/** Identificador único del corte (PK). */
	private int corteID;

	/** Identificador del empleado responsable de la caja (FK). */
	private int usuarioID;

	/**
	 * * Nombre del empleado responsable.
	 * <p>
	 * Atributo no persistente en la tabla de cortes, obtenido mediante JOIN para
	 * reportes.
	 * </p>
	 */
	private String nombreUsuario;

	/** Fecha y hora exacta de la apertura de la caja. */
	private Date fechaApertura;

	/** Monto de dinero efectivo inicial (Fondo de caja). */
	private double montoInicial;

	/** Fecha y hora del cierre. Es {@code null} si el turno sigue activo. */
	private Date fechaCierre;

	/**
	 * * Monto total calculado por el software (Fondo + Ventas en Efectivo -
	 * Retiros). Representa lo que "debería haber".
	 */
	private double montoFinalSistema;

	/**
	 * * Monto físico ingresado por el cajero durante el cierre (Corte Ciego).
	 * Representa lo que "realmente hay".
	 */
	private double montoFinalContado;

	/**
	 * * Resultado de la conciliación:
	 * {@code montoFinalContado - montoFinalSistema}. <br>
	 * Positivo = Sobrante. Negativo = Faltante.
	 */
	private double diferencia;

	/** Estado del turno: "Abierto" o "Cerrado". */
	private String status;

	/**
	 * Constructor vacío por defecto.
	 */
	public CorteCaja() {
	}

	/**
	 * Constructor para <b>APERTURA</b> de caja.
	 * <p>
	 * Se utiliza cuando un usuario inicia un nuevo turno. Inicializa la fecha
	 * actual, el estado en "Abierto" y los montos de cierre en 0.
	 * </p>
	 * * @param usuarioID ID del empleado que abre la caja.
	 * 
	 * @param montoInicial Cantidad de dinero (fondo) con la que inicia.
	 */
	public CorteCaja(int usuarioID, double montoInicial) {
		this.usuarioID = usuarioID;
		this.montoInicial = montoInicial;
		this.fechaApertura = new Date(); // Asigna fecha/hora actual del sistema
		this.status = "Abierto";

		// Inicialización de valores de cierre en neutro/nulo
		this.fechaCierre = null;
		this.montoFinalSistema = 0.0;
		this.montoFinalContado = 0.0;
		this.diferencia = 0.0;
	}

	/**
	 * Constructor completo para <b>LECTURA CRUD</b> (Base de Datos).
	 * <p>
	 * Mapea exactamente las columnas de la tabla {@code TablaCortesCaja}. Utilizado
	 * por el DAO para recuperar el objeto tal cual está almacenado.
	 * </p>
	 * * @param corteID ID del corte.
	 * 
	 * @param usuarioID         ID del usuario.
	 * @param fechaApertura     Fecha de inicio.
	 * @param montoInicial      Fondo inicial.
	 * @param fechaCierre       Fecha de fin.
	 * @param montoFinalSistema Total esperado.
	 * @param montoFinalContado Total contado.
	 * @param diferencia        Variación financiera.
	 * @param status            Estado actual.
	 */
	public CorteCaja(int corteID, int usuarioID, Date fechaApertura, double montoInicial, Date fechaCierre,
			double montoFinalSistema, double montoFinalContado, double diferencia, String status) {
		this.corteID = corteID;
		this.usuarioID = usuarioID;
		this.fechaApertura = fechaApertura;
		this.montoInicial = montoInicial;
		this.fechaCierre = fechaCierre;
		this.montoFinalSistema = montoFinalSistema;
		this.montoFinalContado = montoFinalContado;
		this.diferencia = diferencia;
		this.status = status;
	}

	/**
	 * Constructor especializado para <b>REPORTES</b>.
	 * <p>
	 * Incluye el {@code nombreUsuario} en lugar del {@code usuarioID}. Se utiliza
	 * cuando se generan listados para JasperReports o tablas de historial donde es
	 * necesario mostrar quién realizó el corte.
	 * </p>
	 * * @param corteID ID del corte.
	 * 
	 * @param nombreUsuario     Nombre del empleado (Join con TablaEmpleados).
	 * @param fechaApertura     Fecha inicio.
	 * @param fechaCierre       Fecha fin.
	 * @param montoInicial      Fondo inicial.
	 * @param montoFinalSistema Total esperado.
	 * @param montoFinalContado Total contado.
	 * @param diferencia        Variación.
	 * @param status            Estado.
	 */
	public CorteCaja(int corteID, String nombreUsuario, Date fechaApertura, Date fechaCierre, double montoInicial,
			double montoFinalSistema, double montoFinalContado, double diferencia, String status) {
		this.corteID = corteID;
		this.nombreUsuario = nombreUsuario;
		this.fechaApertura = fechaApertura;
		this.fechaCierre = fechaCierre;
		this.montoInicial = montoInicial;
		this.montoFinalSistema = montoFinalSistema;
		this.montoFinalContado = montoFinalContado;
		this.diferencia = diferencia;
		this.status = status;
	}

	// --- Getters y Setters ---

	public int getCorteID() {
		return corteID;
	}

	public void setCorteID(int corteID) {
		this.corteID = corteID;
	}

	public int getUsuarioID() {
		return usuarioID;
	}

	public void setUsuarioID(int usuarioID) {
		this.usuarioID = usuarioID;
	}

	/**
	 * Obtiene el nombre del usuario responsable.
	 * 
	 * @return Nombre del empleado (solo disponible si se usó el constructor de
	 *         reporte).
	 */
	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public Date getFechaApertura() {
		return fechaApertura;
	}

	public void setFechaApertura(Date fechaApertura) {
		this.fechaApertura = fechaApertura;
	}

	public double getMontoInicial() {
		return montoInicial;
	}

	public void setMontoInicial(double montoInicial) {
		this.montoInicial = montoInicial;
	}

	public Date getFechaCierre() {
		return fechaCierre;
	}

	public void setFechaCierre(Date fechaCierre) {
		this.fechaCierre = fechaCierre;
	}

	public double getMontoFinalSistema() {
		return montoFinalSistema;
	}

	public void setMontoFinalSistema(double montoFinalSistema) {
		this.montoFinalSistema = montoFinalSistema;
	}

	public double getMontoFinalContado() {
		return montoFinalContado;
	}

	public void setMontoFinalContado(double montoFinalContado) {
		this.montoFinalContado = montoFinalContado;
	}

	public double getDiferencia() {
		return diferencia;
	}

	public void setDiferencia(double diferencia) {
		this.diferencia = diferencia;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}