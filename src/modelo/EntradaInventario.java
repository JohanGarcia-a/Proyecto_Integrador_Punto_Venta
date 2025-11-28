package modelo;

import java.util.Date;

/**
 * Entidad que representa un registro histórico de entrada de mercancía al
 * almacén.
 * <p>
 * Esta clase mapea la tabla <b>TablaEntradasInventario</b> y sirve como
 * bitácora de auditoría. Se utiliza cada vez que un usuario agrega stock
 * manualmente a un producto existente, permitiendo rastrear "Quién", "Cuándo" y
 * "Cuánto" se agregó.
 * </p>
 * * @version 1.0
 */
public class EntradaInventario {

	/** Identificador único del registro de entrada (PK). */
	private int id;

	/** Identificador del producto al que se le agregó stock (FK). */
	private int productoId;

	/** Nombre del producto (Dato informativo obtenido vía JOIN para reportes). */
	private String nombreProducto;

	/** Cantidad de unidades añadidas al inventario. */
	private int cantidadAgregada;

	/** Descripción o motivo de la entrada (Snapshot del producto o nota manual). */
	private String productoDescripcion;

	/** Fecha y hora exacta en que se realizó el movimiento. */
	private Date fechaEntrada;

	/** Identificador del usuario/empleado que realizó la acción (FK). */
	private int usuarioId;

	/** Nombre del usuario (Dato informativo obtenido vía JOIN para reportes). */
	private String nombreUsuario;

	/**
	 * Constructor para <b>REGISTRAR</b> una nueva entrada.
	 * <p>
	 * Se utiliza desde el {@code ControladorAlmacen} al momento de guardar un
	 * aumento de stock. No requiere ID (es autoincrementable) ni nombres resueltos,
	 * ya que estos datos ya existen en sus respectivas tablas.
	 * </p>
	 * * @param productoId ID del producto afectado.
	 * 
	 * @param cantidadAgregada    Cantidad sumada.
	 * @param fechaEntrada        Fecha actual.
	 * @param usuarioId           ID del empleado logueado.
	 * @param productoDescripcion Descripción del producto al momento de la entrada.
	 */
	public EntradaInventario(int productoId, int cantidadAgregada, Date fechaEntrada, int usuarioId,
			String productoDescripcion) {
		this.productoId = productoId;
		this.cantidadAgregada = cantidadAgregada;
		this.fechaEntrada = fechaEntrada;
		this.usuarioId = usuarioId;
		this.productoDescripcion = productoDescripcion;
	}

	/**
	 * Constructor para <b>LEER</b> el historial (Reportes).
	 * <p>
	 * Se utiliza en {@code EntradaInventarioDAO} al recuperar los registros de la
	 * base de datos. Incluye datos enriquecidos (Nombres de producto y usuario)
	 * obtenidos mediante JOINs para mostrarlos en la tabla de historial del panel
	 * de reportes.
	 * </p>
	 * * @param id ID del registro de entrada.
	 * 
	 * @param nombreProducto      Nombre del producto.
	 * @param cantidadAgregada    Cantidad que se agregó.
	 * @param fechaEntrada        Fecha del movimiento.
	 * @param nombreUsuario       Nombre del empleado responsable.
	 * @param productoDescripcion Descripción del producto.
	 */
	public EntradaInventario(int id, String nombreProducto, int cantidadAgregada, Date fechaEntrada,
			String nombreUsuario, String productoDescripcion) {
		this.id = id;
		this.nombreProducto = nombreProducto;
		this.cantidadAgregada = cantidadAgregada;
		this.fechaEntrada = fechaEntrada;
		this.nombreUsuario = nombreUsuario;
		this.productoDescripcion = productoDescripcion;
	}

	// --- Getters y Setters ---

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProductoId() {
		return productoId;
	}

	public void setProductoId(int productoId) {
		this.productoId = productoId;
	}

	public String getNombreProducto() {
		return nombreProducto;
	}

	public void setNombreProducto(String nombreProducto) {
		this.nombreProducto = nombreProducto;
	}

	public String getProductoDescripcion() {
		return productoDescripcion;
	}

	public void setProductoDescripcion(String productoDescripcion) {
		this.productoDescripcion = productoDescripcion;
	}

	public int getCantidadAgregada() {
		return cantidadAgregada;
	}

	public void setCantidadAgregada(int cantidadAgregada) {
		this.cantidadAgregada = cantidadAgregada;
	}

	public Date getFechaEntrada() {
		return fechaEntrada;
	}

	public void setFechaEntrada(Date fechaEntrada) {
		this.fechaEntrada = fechaEntrada;
	}

	public int getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(int usuarioId) {
		this.usuarioId = usuarioId;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}
}