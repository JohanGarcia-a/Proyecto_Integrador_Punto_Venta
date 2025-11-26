package modelo;

public class VentaDetalle {
	private int id;
	private int ventaId;
	private int productoId;
	private String nombreProducto;
	private String descripcion;
	private int cantidad;
	private double precioUnitario;
	private double subtotal;

	public VentaDetalle(int id, int ventaId, int productoId, String nombreProducto, int cantidad,
			double precioUnitario, double subtotal) {
		super();
		this.id = id;
		this.ventaId = ventaId;
		this.productoId = productoId;
		this.nombreProducto = nombreProducto;
		this.cantidad = cantidad;
		this.precioUnitario = precioUnitario;
		this.subtotal = subtotal;
	}

	public VentaDetalle(int productoId, String nombreProducto, String descripcion, int cantidad,
			double precioUnitario) {
		this.productoId = productoId;
		this.nombreProducto = nombreProducto;
		this.descripcion = descripcion;
		this.cantidad = cantidad;
		this.precioUnitario = precioUnitario;
		this.subtotal = cantidad * precioUnitario;

		// Los IDs se quedan en 0 porque aún no está en la base de datos
		this.id = 0;
		this.ventaId = 0;
	}

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

	public Object[] toTableRow() {
		// Para la tabla del "carrito de compras" en la vista
		return new Object[] { this.productoId, this.nombreProducto, this.descripcion, this.cantidad,
				this.precioUnitario, this.subtotal };
	}
}