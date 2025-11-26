package modelo;

public class OrdenCompraDetalle {

	private int id; // DetalleID
	private int ordenCompraId; // OrdenID
	private int productoId; // ProductoID
	private int cantidadPedida;
	private double costoUnitario;

	// Campos extra para JOINs
	private String nombreProducto;
	private String descripcion; // <-- CAMBIO 1: AÑADIR CAMPO

	// Constructor para cuando LEEMOS de la BD
	// (Añadimos descripcion al final)
	public OrdenCompraDetalle(int id, int ordenCompraId, int productoId, int cantidadPedida, double costoUnitario,
			String nombreProducto, String descripcion) { // <-- CAMBIO 2
		this.id = id;
		this.ordenCompraId = ordenCompraId;
		this.productoId = productoId;
		this.cantidadPedida = cantidadPedida;
		this.costoUnitario = costoUnitario;
		this.nombreProducto = nombreProducto;
		this.descripcion = descripcion; // <-- CAMBIO 3
	}

	// Constructor para cuando CREAMOS un detalle nuevo (en el "carrito")
	// (Añadimos descripcion)
	public OrdenCompraDetalle(int productoId, String nombreProducto, String descripcion, int cantidadPedida,
			double costoUnitario) { // <-- CAMBIO 4
		this.id = 0;
		this.ordenCompraId = 0;
		this.productoId = productoId;
		this.nombreProducto = nombreProducto;
		this.descripcion = descripcion; // <-- CAMBIO 5
		this.cantidadPedida = cantidadPedida;
		this.costoUnitario = costoUnitario;
	}

	// Este toTableRow() es para el "carrito" del panel "Crear Pedido"
	public Object[] toTableRow() {
		return new Object[] { this.productoId, this.nombreProducto, this.descripcion, // <-- CAMBIO 6: AÑADIR A LA FILA
				this.cantidadPedida, this.costoUnitario, (this.cantidadPedida * this.costoUnitario) // Subtotal
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

	// --- AÑADIR GETTER Y SETTER PARA DESCRIPCION ---
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}