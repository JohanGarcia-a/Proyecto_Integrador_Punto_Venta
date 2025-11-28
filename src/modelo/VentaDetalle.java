package modelo;

/**
 * Entidad que representa un renglón o ítem individual dentro de una Venta.
 * <p>
 * Mapea la tabla <b>TablaVentaDetalle</b>. Funciona como la entidad "Detalle"
 * en la relación Maestro-Detalle con la clase {@link Venta}.
 * </p>
 * <p>
 * Almacena la información específica de qué producto se vendió, cuántas
 * unidades y a qué precio, congelando el costo al momento de la transacción
 * para evitar inconsistencias futuras si el precio de lista cambia.
 * </p>
 * * @version 1.1
 */
public class VentaDetalle {

	/** Identificador único del detalle de venta (PK). */
	private int id;

	/**
	 * Identificador de la Venta (Cabecera) a la que pertenece este detalle (FK).
	 */
	private int ventaId;

	/** Identificador del producto vendido (FK). */
	private int productoId;

	/** Nombre del producto (Dato informativo para visualización). */
	private String nombreProducto;

	/**
	 * Descripción del producto (Dato informativo para visualización en el carrito).
	 */
	private String descripcion;

	/** Cantidad de unidades vendidas de este producto. */
	private int cantidad;

	/**
	 * * Precio de venta unitario al momento de la transacción.
	 * <p>
	 * Importante: Este valor se guarda para historial, independiente del precio
	 * actual del producto.
	 * </p>
	 */
	private double precioUnitario;

	/**
	 * * Monto total por este renglón.
	 * <p>
	 * Cálculo: {@code cantidad * precioUnitario}.
	 * </p>
	 */
	private double subtotal;

	/**
	 * Constructor completo para <b>LEER</b> desde la Base de Datos.
	 * <p>
	 * Utilizado por el DAO para reconstruir el objeto historial.
	 * </p>
	 * * @param id ID del detalle.
	 * 
	 * @param ventaId        ID de la venta padre.
	 * @param productoId     ID del producto.
	 * @param nombreProducto Nombre del producto.
	 * @param cantidad       Cantidad vendida.
	 * @param precioUnitario Precio congelado.
	 * @param subtotal       Subtotal calculado.
	 */
	public VentaDetalle(int id, int ventaId, int productoId, String nombreProducto, int cantidad, double precioUnitario,
			double subtotal) {
		super();
		this.id = id;
		this.ventaId = ventaId;
		this.productoId = productoId;
		this.nombreProducto = nombreProducto;
		this.cantidad = cantidad;
		this.precioUnitario = precioUnitario;
		this.subtotal = subtotal;
	}

	/**
	 * Constructor para <b>AGREGAR</b> al Carrito de Compras (En memoria).
	 * <p>
	 * Se utiliza desde {@code ControladorVenta} cuando el usuario agrega un
	 * producto a la venta actual. Calcula automáticamente el subtotal.
	 * </p>
	 * * @param productoId ID del producto seleccionado.
	 * 
	 * @param nombreProducto Nombre para mostrar.
	 * @param descripcion    Descripción para mostrar.
	 * @param cantidad       Cantidad solicitada.
	 * @param precioUnitario Precio actual del producto.
	 */
	public VentaDetalle(int productoId, String nombreProducto, String descripcion, int cantidad,
			double precioUnitario) {
		this.productoId = productoId;
		this.nombreProducto = nombreProducto;
		this.descripcion = descripcion;
		this.cantidad = cantidad;
		this.precioUnitario = precioUnitario;
		this.subtotal = cantidad * precioUnitario; // Cálculo automático al crear

		// Los IDs se quedan en 0 porque aún no está en la base de datos
		this.id = 0;
		this.ventaId = 0;
	}

	// --- Getters y Setters ---

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getVentaId() {
		return ventaId;
	}

	public void setVentaId(int ventaId) {
		this.ventaId = ventaId;
	}

	public int getProductoId() {
		return productoId;
	}

	public void setProductoId(int productoId) {
		this.productoId = productoId;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getNombreProducto() {
		return nombreProducto;
	}

	public void setNombreProducto(String nombreProducto) {
		this.nombreProducto = nombreProducto;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public double getPrecioUnitario() {
		return precioUnitario;
	}

	public void setPrecioUnitario(double precioUnitario) {
		this.precioUnitario = precioUnitario;
	}

	public double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}

	/**
	 * Genera un arreglo de objetos para visualizar el ítem en la tabla de Ventas
	 * (Carrito).
	 * <p>
	 * Orden de columnas: [ID Producto, Nombre, Descripción, Cantidad, Precio Unit.,
	 * Subtotal].
	 * </p>
	 * 
	 * @return Arreglo de datos para la interfaz gráfica.
	 */
	public Object[] toTableRow() {
		// Para la tabla del "carrito de compras" en la vista
		return new Object[] { this.productoId, this.nombreProducto, this.descripcion, this.cantidad,
				this.precioUnitario, this.subtotal };
	}
}