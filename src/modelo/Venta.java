package modelo;

import java.util.Date;
import java.util.List;
import modelogenerico.Entidad;
import java.util.ArrayList;

/**
 * Entidad que representa la cabecera de una transacción de venta.
 * <p>
 * Esta clase actúa como el objeto "Maestro" en la relación Maestro-Detalle.
 * Almacena la información general de la transacción (Cliente, Empleado, Fecha,
 * Totales) y contiene una lista de {@link VentaDetalle} con los productos
 * específicos vendidos.
 * </p>
 * <p>
 * Es fundamental para el módulo de facturación y reportes, y debe estar siempre
 * vinculada a un {@link CorteCaja} abierto (campo {@code corteID}).
 * </p>
 * * @version 1.2
 */
public class Venta implements Entidad {

	/** Identificador único de la venta (PK). Número de folio/ticket. */
	private int id;

	/** Identificador del cliente que realiza la compra (FK). */
	private int clienteId;

	/** Identificador del empleado (cajero) que procesa la venta (FK). */
	private int empleadoId;

	/** Fecha y hora exacta de la transacción. */
	private Date fecha;

	/**
	 * * Identificador del corte de caja al que pertenece la venta (FK).
	 * <p>
	 * Indispensable para agrupar las ganancias por turno.
	 * </p>
	 */
	private int corteID;

	// --- SECCIÓN FINANCIERA ---

	/**
	 * Sumatoria de los subtotales de los productos antes de aplicar impuestos o
	 * descuentos.
	 */
	private double subtotal;

	/** Monto monetario descontado del subtotal. */
	private double descuento;

	/** Monto calculado de impuestos (IVA) sobre la base gravable. */
	private double impuestos;

	/** Monto final a cobrar al cliente (Subtotal - Descuento + Impuestos). */
	private double total;

	// --- DATOS INFORMATIVOS (JOINs) ---

	/** Nombre del cliente (para visualización en reportes/tickets). */
	private String nombreCliente;

	/** Nombre del empleado (para visualización en reportes/tickets). */
	private String nombreEmpleado;

	/** Forma de pago seleccionada (ej. "Efectivo", "Tarjeta"). */
	private String metodoPago;

	/** Lista que contiene los renglones (ítems) de la venta. */
	private List<VentaDetalle> detalles;

	/**
	 * Constructor por defecto.
	 * <p>
	 * Inicializa la lista de detalles vacía y restablece los acumuladores
	 * financieros a 0.0 para evitar valores nulos al comenzar una nueva venta.
	 * </p>
	 */
	public Venta() {
		this.detalles = new ArrayList<>();
		this.total = 0.0;
		this.subtotal = 0.0;
		this.descuento = 0.0;
		this.impuestos = 0.0;
	}

	/**
	 * Recorre la lista de detalles y suma sus importes individuales.
	 * <p>
	 * Este método actualiza el atributo {@code subtotal} de la instancia.
	 * <b>Nota:</b> No calcula impuestos ni descuentos, solo la suma bruta de los
	 * productos.
	 * </p>
	 * * @return El nuevo subtotal calculado.
	 */
	public double recalcularSubtotal() {
		double nuevoSubtotal = 0.0;
		for (VentaDetalle detalle : this.detalles) {
			nuevoSubtotal += detalle.getSubtotal();
		}
		this.subtotal = nuevoSubtotal; // Actualizamos el campo interno
		return this.subtotal;
	}

	/**
	 * Agrega un producto (detalle) a la transacción en memoria. * @param detalle
	 * Objeto {@link VentaDetalle} con el producto y cantidad.
	 */
	public void agregarDetalle(VentaDetalle detalle) {
		this.detalles.add(detalle);
	}

	/**
	 * Elimina un producto de la lista de venta en memoria. * @param indice Posición
	 * del elemento en la lista (índice de la tabla visual).
	 */
	public void quitarDetalle(int indice) {
		if (indice >= 0 && indice < detalles.size()) {
			detalles.remove(indice);
		}
	}

	public void setDetalles(List<VentaDetalle> detalles) {
		this.detalles = detalles;
	}

	public List<VentaDetalle> getDetalles() {
		return detalles;
	}

	// --- Implementación de la Interfaz Entidad ---

	@Override
	public int getid() {
		return this.id;
	}

	@Override
	public void setid(int id) {
		this.id = id;
	}

	/**
	 * Genera un arreglo de objetos para representar la venta en el historial.
	 * <p>
	 * Se utiliza en {@code PanelHistorialVentas}.
	 * </p>
	 * * @return Arreglo con: [ID, Fecha, Cliente, Empleado, Total Final].
	 */
	@Override
	public Object[] toTableRow() {
		return new Object[] { this.id, this.fecha, this.nombreCliente, this.nombreEmpleado, this.total };
	}

	// --- Getters y Setters Estándar ---

	public int getClienteId() {
		return clienteId;
	}

	public void setClienteId(int clienteId) {
		this.clienteId = clienteId;
	}

	public int getEmpleadoId() {
		return empleadoId;
	}

	public void setEmpleadoId(int empleadoId) {
		this.empleadoId = empleadoId;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public double getTotal() {
		return this.total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public String getNombreCliente() {
		return nombreCliente;
	}

	public void setNombreCliente(String nombreCliente) {
		this.nombreCliente = nombreCliente;
	}

	public String getNombreEmpleado() {
		return nombreEmpleado;
	}

	public void setNombreEmpleado(String nombreEmpleado) {
		this.nombreEmpleado = nombreEmpleado;
	}

	public String getMetodoPago() {
		return metodoPago;
	}

	public void setMetodoPago(String metodoPago) {
		this.metodoPago = metodoPago;
	}

	public int getCorteID() {
		return corteID;
	}

	public void setCorteID(int corteID) {
		this.corteID = corteID;
	}

	public double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}

	public double getDescuento() {
		return descuento;
	}

	public void setDescuento(double descuento) {
		this.descuento = descuento;
	}

	public double getImpuestos() {
		return impuestos;
	}

	public void setImpuestos(double impuestos) {
		this.impuestos = impuestos;
	}
}