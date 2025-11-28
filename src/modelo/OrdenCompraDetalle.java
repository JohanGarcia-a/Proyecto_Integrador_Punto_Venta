package modelo;

/**
 * Entidad que representa un renglón o ítem individual dentro de una Orden de
 * Compra.
 * <p>
 * Mapea la tabla <b>TablaOrdenCompraDetalle</b>. Esta clase contiene la
 * información específica de qué producto se pidió, cuántas unidades y a qué
 * costo unitario.
 * </p>
 * <p>
 * Se utiliza tanto para construir el pedido en memoria ("Carrito de compra al
 * proveedor") como para recuperar el historial de pedidos pasados.
 * </p>
 * * @version 1.1
 */
public class OrdenCompraDetalle {

	/** Identificador único del detalle (PK). */
	private int id;

	/** Identificador de la Orden de Compra a la que pertenece (FK). */
	private int ordenCompraId;

	/** Identificador del producto solicitado (FK). */
	private int productoId;

	/** Número de unidades solicitadas al proveedor. */
	private int cantidadPedida;

	/** Costo por unidad acordado con el proveedor (distinto al precio de venta). */
	private double costoUnitario;

	// --- Campos informativos (No persistidos directamente en la tabla de detalle,
	// sino obtenidos por relación) ---

	/** Nombre comercial del producto. */
	private String nombreProducto;

	/**
	 * * Descripción del producto.
	 * <p>
	 * Añadido para facilitar la identificación visual del ítem en la tabla de
	 * pedidos.
	 * </p>
	 */
	private String descripcion;

	/**
	 * Constructor para <b>LEER</b> desde la Base de Datos.
	 * <p>
	 * Utilizado por el DAO cuando se consulta el historial. Incluye todos los IDs y
	 * los datos informativos (nombre y descripción) obtenidos mediante JOINs con la
	 * tabla de Productos.
	 * </p>
	 * * @param id ID del detalle.
	 * 
	 * @param ordenCompraId  ID de la orden padre.
	 * @param productoId     ID del producto.
	 * @param cantidadPedida Cantidad.
	 * @param costoUnitario  Costo.
	 * @param nombreProducto Nombre del producto.
	 * @param descripcion    Descripción del producto.
	 */
	public OrdenCompraDetalle(int id, int ordenCompraId, int productoId, int cantidadPedida, double costoUnitario,
			String nombreProducto, String descripcion) {
		this.id = id;
		this.ordenCompraId = ordenCompraId;
		this.productoId = productoId;
		this.cantidadPedida = cantidadPedida;
		this.costoUnitario = costoUnitario;
		this.nombreProducto = nombreProducto;
		this.descripcion = descripcion;
	}

	/**
	 * Constructor para <b>CREAR</b> un nuevo detalle (En memoria/Carrito).
	 * <p>
	 * Se utiliza desde la interfaz {@code PanelCrearPedido}. Al ser un objeto nuevo
	 * en memoria que aún no se guarda, los IDs de detalle y orden se inicializan en
	 * 0.
	 * </p>
	 * * @param productoId ID del producto seleccionado.
	 * 
	 * @param nombreProducto Nombre para mostrar.
	 * @param descripcion    Descripción para mostrar.
	 * @param cantidadPedida Cantidad a pedir.
	 * @param costoUnitario  Costo ingresado.
	 */
	public OrdenCompraDetalle(int productoId, String nombreProducto, String descripcion, int cantidadPedida,
			double costoUnitario) {
		this.id = 0;
		this.ordenCompraId = 0;
		this.productoId = productoId;
		this.nombreProducto = nombreProducto;
		this.descripcion = descripcion;
		this.cantidadPedida = cantidadPedida;
		this.costoUnitario = costoUnitario;
	}

	/**
	 * Genera un arreglo de objetos para visualizar el detalle en la tabla
	 * (Carrito).
	 * <p>
	 * Incluye el cálculo automático del <b>Subtotal</b> (Cantidad * Costo) para
	 * mostrarlo en la interfaz gráfica.
	 * </p>
	 * * @return Arreglo con: [ID Producto, Nombre, Descripción, Cantidad, Costo
	 * Unit., Subtotal].
	 */
	public Object[] toTableRow() {
		return new Object[] { this.productoId, this.nombreProducto, this.descripcion, // Se incluye la descripción en la
																						// visualización
				this.cantidadPedida, this.costoUnitario, (this.cantidadPedida * this.costoUnitario) // Cálculo del
																									// Subtotal de la
																									// línea
		};
	}

	// --- Getters y Setters ---

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOrdenCompraId() {
		return ordenCompraId;
	}

	public void setOrdenCompraId(int ordenCompraId) {
		this.ordenCompraId = ordenCompraId;
	}

	public int getProductoId() {
		return productoId;
	}

	public void setProductoId(int productoId) {
		this.productoId = productoId;
	}

	public int getCantidadPedida() {
		return cantidadPedida;
	}

	public void setCantidadPedida(int cantidadPedida) {
		this.cantidadPedida = cantidadPedida;
	}

	public double getCostoUnitario() {
		return costoUnitario;
	}

	public void setCostoUnitario(double costoUnitario) {
		this.costoUnitario = costoUnitario;
	}

	public String getNombreProducto() {
		return nombreProducto;
	}

	public void setNombreProducto(String nombreProducto) {
		this.nombreProducto = nombreProducto;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}