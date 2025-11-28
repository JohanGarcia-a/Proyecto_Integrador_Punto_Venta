package modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import modelogenerico.Entidad;

/**
 * Entidad que representa la cabecera de un Pedido o Orden de Compra a un
 * proveedor.
 * <p>
 * Mapea la tabla <b>TablaOrdenesCompra</b>. Funciona como la entidad "Maestra"
 * en la relación Maestro-Detalle, conteniendo la información general
 * (Proveedor, Fecha, Status) y una lista de {@link OrdenCompraDetalle} con los
 * productos específicos.
 * </p>
 * <p>
 * Gestiona el ciclo de vida del pedido: Creación ("Pendiente") -> Recepción
 * ("Recibido").
 * </p>
 * * @version 1.0
 */
public class OrdenCompra implements Entidad {

	/** Identificador único de la orden de compra (PK). */
	private int id;

	/** Identificador del proveedor al que se le hace el pedido (FK). */
	private int proveedorId;

	/** Fecha de creación del pedido. */
	private Date fecha;

	/** Estado actual del pedido. Valores: "Pendiente", "Recibido", "Cancelado". */
	private String status;

	/**
	 * * Nombre del proveedor asociado.
	 * <p>
	 * Atributo no persistente en la tabla de órdenes, obtenido mediante JOIN para
	 * mostrar en la interfaz.
	 * </p>
	 */
	private String nombreProveedor;

	/** Lista que contiene los renglones (productos) de este pedido. */
	private List<OrdenCompraDetalle> detalles;

	/**
	 * Constructor para <b>CREAR</b> un nuevo pedido en memoria.
	 * <p>
	 * Inicializa la lista de detalles vacía, establece la fecha al momento actual y
	 * fija el estado inicial como "Pendiente".
	 * </p>
	 */
	public OrdenCompra() {
		this.detalles = new ArrayList<>();
		this.fecha = new Date();
		this.status = "Pendiente";
	}

	/**
	 * Constructor para <b>LEER</b> un pedido existente desde la base de datos.
	 * <p>
	 * Se utiliza en {@code OrdenCompraDAO} para reconstruir el objeto cabecera.
	 * <b>Nota:</b> La lista de detalles no se carga en este constructor, se debe
	 * cargar por separado si es necesaria.
	 * </p>
	 * * @param id ID de la orden.
	 * 
	 * @param proveedorId     ID del proveedor.
	 * @param fecha           Fecha de registro.
	 * @param status          Estado actual.
	 * @param nombreProveedor Nombre del proveedor (Dato enriquecido).
	 */
	public OrdenCompra(int id, int proveedorId, Date fecha, String status, String nombreProveedor) {
		this.id = id;
		this.proveedorId = proveedorId;
		this.fecha = fecha;
		this.status = status;
		this.nombreProveedor = nombreProveedor;
		this.detalles = new ArrayList<>(); // Los detalles se cargan por separado
	}

	// --- Métodos para manejar los detalles (Relación Maestro-Detalle) ---

	/**
	 * Agrega un producto (detalle) a la lista de esta orden.
	 * <p>
	 * Vincula automáticamente el detalle con el ID de esta orden maestra (si ya
	 * existe).
	 * </p>
	 * 
	 * @param detalle Objeto {@link OrdenCompraDetalle} a agregar.
	 */
	public void agregarDetalle(OrdenCompraDetalle detalle) {
		this.detalles.add(detalle);
		detalle.setOrdenCompraId(this.id); // Asignamos el ID de esta orden al detalle
	}

	/**
	 * Elimina un detalle de la lista en memoria basado en su índice.
	 * 
	 * @param indice Posición en la lista (0 a N).
	 */
	public void quitarDetalle(int indice) {
		if (indice >= 0 && indice < detalles.size()) {
			detalles.remove(indice);
		}
	}

	public List<OrdenCompraDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<OrdenCompraDetalle> detalles) {
		this.detalles = detalles;
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
	 * Genera un arreglo de objetos para visualizar la orden en el panel de gestión.
	 * <p>
	 * Se utiliza en {@code PanelGestionPedidos}.
	 * </p>
	 * 
	 * @return Arreglo con: [ID, Proveedor, Fecha, Status].
	 */
	@Override
	public Object[] toTableRow() {
		return new Object[] { this.id, this.nombreProveedor, this.fecha, this.status };
	}

	// --- Getters y Setters Estándar ---

	public int getProveedorId() {
		return proveedorId;
	}

	public void setProveedorId(int proveedorId) {
		this.proveedorId = proveedorId;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNombreProveedor() {
		return nombreProveedor;
	}

	public void setNombreProveedor(String nombreProveedor) {
		this.nombreProveedor = nombreProveedor;
	}
}