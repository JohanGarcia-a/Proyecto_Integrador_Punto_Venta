package modelo;

import modelogenerico.Entidad;

public class AlmacenProductos implements Entidad {
	private int id;
	private String nombre;
	private String descripcion;
	private double precio;
	private String codigo;
	private int cantidad;
	private String ruta;
	private int categoriaId;
	private String categoriaNombre;
	private int proveedorId;
	private String proveedorNombre;
	private int stockMinimo; // --- MODIFICACIÓN AQUÍ ---

	// Constructor para LEER de la BD (con JOINs)
	public AlmacenProductos(int id, String nombre, String descripcion, double precio, String codigo, int cantidad,
			String ruta, int categoriaId, String categoriaNombre, int proveedorId, String proveedorNombre,
			int stockMinimo) { // --- MODIFICACIÓN AQUÍ ---
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.precio = precio;
		this.codigo = codigo;
		this.cantidad = cantidad;
		this.ruta = ruta;
		this.categoriaId = categoriaId;
		this.categoriaNombre = categoriaNombre;
		this.proveedorId = proveedorId;
		this.proveedorNombre = proveedorNombre;
		this.stockMinimo = stockMinimo; // --- MODIFICACIÓN AQUÍ ---
	}

	// Constructor para GUARDAR desde el formulario (solo con los IDs)
	public AlmacenProductos(int id, String nombre, String descripcion, double precio, String codigo, int cantidad,
			String ruta, int categoriaId, int proveedorId, int stockMinimo) { // --- MODIFICACIÓN AQUÍ ---
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.precio = precio;
		this.codigo = codigo;
		this.cantidad = cantidad;
		this.ruta = ruta;
		this.categoriaId = categoriaId;
		this.proveedorId = proveedorId;
		this.stockMinimo = stockMinimo; // --- MODIFICACIÓN AQUÍ ---
		this.categoriaNombre = "";
		this.proveedorNombre = "";
	}

	// --- Getters y Setters ---

	// --- MODIFICACIÓN AQUÍ: AÑADIR GETTER Y SETTER ---
	public int getStockMinimo() {
		return stockMinimo;
	}

	public void setStockMinimo(int stockMinimo) {
		this.stockMinimo = stockMinimo;
	}
	// ---------------------------------------------

	public int getCategoriaId() {
		return categoriaId;
	}

	public String getCategoriaNombre() {
		return categoriaNombre;
	}

	@Override
	public int getid() {
		return this.id;
	}

	@Override
	public void setid(int id) {
		this.id = id;
	}

	public String getProveedorNombre() {
		return proveedorNombre;
	}

	public int getProveedorId() {
		return proveedorId;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public String getRuta() {
		return ruta;
	}

	public void setRuta(String ruta) {
		this.ruta = ruta;
	}

	@Override
	public String toString() {
		return nombre + "," + descripcion;
	}

	// En MalmacenProductos.java
	@Override
	public Object[] toTableRow() {
		return new Object[] { this.id, this.categoriaNombre, this.proveedorNombre, this.nombre, this.descripcion,
				this.precio, this.codigo, this.cantidad, this.stockMinimo, this.ruta // <-- Asegúrate que esté aquí
		};
	}
}