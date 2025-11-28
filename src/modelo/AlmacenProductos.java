package modelo;

import modelogenerico.Entidad;

/**
 * Entidad que representa un producto dentro del inventario del sistema.
 * <p>
 * Esta clase mapea la tabla <b>TablaAlmacen_Productos</b> de la base de datos.
 * Implementa la interfaz {@link Entidad} para permitir su manejo a través de
 * controladores genéricos y su visualización en tablas.
 * </p>
 * <p>
 * Incluye atributos de relación (IDs y Nombres) para Categorías y Proveedores,
 * así como el control de stock mínimo para alertas visuales.
 * </p>
 * * @version 1.1
 */
public class AlmacenProductos implements Entidad {

	/** Identificador único del producto (PK). */
	private int id;

	/** Nombre comercial del producto. */
	private String nombre;

	/** Descripción detallada o características del producto. */
	private String descripcion;

	/** Precio de venta unitario. */
	private double precio;

	/** Código de barras, SKU o identificador interno. */
	private String codigo;

	/** Cantidad actual en existencia (Stock físico). */
	private int cantidad;

	/** Ruta absoluta o relativa de la imagen del producto en el disco local. */
	private String ruta;

	/** Identificador de la categoría (FK). */
	private int categoriaId;

	/** Nombre de la categoría (Obtenido vía JOIN). */
	private String categoriaNombre;

	/** Identificador del proveedor (FK). */
	private int proveedorId;

	/** Nombre del proveedor (Obtenido vía JOIN). */
	private String proveedorNombre;

	/**
	 * * Cantidad mínima permitida antes de generar una alerta de reabastecimiento.
	 * <p>
	 * Utilizado por el renderizador de la tabla para colorear filas
	 * (Amarillo/Rojo).
	 * </p>
	 */
	private int stockMinimo;

	/**
	 * Constructor completo para lectura desde Base de Datos.
	 * <p>
	 * Se utiliza cuando se recuperan registros mediante consultas SQL que incluyen
	 * JOINs con las tablas de Categorías y Proveedores para obtener sus nombres.
	 * </p>
	 * * @param id ID del producto.
	 * 
	 * @param nombre          Nombre del producto.
	 * @param descripcion     Descripción.
	 * @param precio          Precio unitario.
	 * @param codigo          Código de barras/SKU.
	 * @param cantidad        Stock actual.
	 * @param ruta            Ruta de la imagen.
	 * @param categoriaId     ID de la categoría.
	 * @param categoriaNombre Nombre de la categoría.
	 * @param proveedorId     ID del proveedor.
	 * @param proveedorNombre Nombre del proveedor.
	 * @param stockMinimo     Nivel de stock para alertas.
	 */
	public AlmacenProductos(int id, String nombre, String descripcion, double precio, String codigo, int cantidad,
			String ruta, int categoriaId, String categoriaNombre, int proveedorId, String proveedorNombre,
			int stockMinimo) {
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
		this.stockMinimo = stockMinimo;
	}

	/**
	 * Constructor para creación o actualización desde formularios.
	 * <p>
	 * Se utiliza cuando se capturan datos desde la interfaz gráfica
	 * (PanelAlmacenProductos), donde usualmente solo se tienen los IDs
	 * seleccionados en los ComboBoxes, pero no necesariamente los nombres de texto
	 * plano de las relaciones.
	 * </p>
	 * * @param id ID del producto (o -1 si es nuevo).
	 * 
	 * @param nombre      Nombre del producto.
	 * @param descripcion Descripción.
	 * @param precio      Precio unitario.
	 * @param codigo      Código de barras/SKU.
	 * @param cantidad    Stock actual.
	 * @param ruta        Ruta de la imagen.
	 * @param categoriaId ID de la categoría seleccionada.
	 * @param proveedorId ID del proveedor seleccionado.
	 * @param stockMinimo Nivel de stock para alertas.
	 */
	public AlmacenProductos(int id, String nombre, String descripcion, double precio, String codigo, int cantidad,
			String ruta, int categoriaId, int proveedorId, int stockMinimo) {
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.precio = precio;
		this.codigo = codigo;
		this.cantidad = cantidad;
		this.ruta = ruta;
		this.categoriaId = categoriaId;
		this.proveedorId = proveedorId;
		this.stockMinimo = stockMinimo;
		// Inicializamos cadenas vacías para evitar NullPointerException en vistas si se
		// accede a ellas
		this.categoriaNombre = "";
		this.proveedorNombre = "";
	}

	// --- Getters y Setters ---

	/**
	 * Obtiene el nivel de stock mínimo configurado.
	 * 
	 * @return Cantidad límite para alertas.
	 */
	public int getStockMinimo() {
		return stockMinimo;
	}

	public void setStockMinimo(int stockMinimo) {
		this.stockMinimo = stockMinimo;
	}

	public int getCategoriaId() {
		return categoriaId;
	}

	/**
	 * Obtiene el nombre de la categoría asociada.
	 * 
	 * @return Nombre de la categoría o cadena vacía si no se cargó con JOIN.
	 */
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

	/**
	 * Obtiene el nombre del proveedor asociado.
	 * 
	 * @return Nombre del proveedor o cadena vacía si no se cargó con JOIN.
	 */
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

	/**
	 * Devuelve una representación en cadena del producto. Útil para depuración
	 * rápida.
	 * 
	 * @return Cadena formato "Nombre,Descripcion".
	 */
	@Override
	public String toString() {
		return nombre + "," + descripcion;
	}

	/**
	 * Genera un arreglo de objetos representativo de la fila para una JTable.
	 * <p>
	 * El orden de los elementos coincide con las columnas definidas en
	 * {@code PanelAlmacenProductos}. Incluye columnas ocultas como stock mínimo y
	 * ruta de imagen.
	 * </p>
	 * * @return Arreglo de objetos con: [ID, Categoria, Proveedor, Nombre,
	 * Descripcion, Precio, Codigo, Cantidad, StockMin, Ruta].
	 */
	@Override
	public Object[] toTableRow() {
		return new Object[] { this.id, this.categoriaNombre, this.proveedorNombre, this.nombre, this.descripcion,
				this.precio, this.codigo, this.cantidad, this.stockMinimo, this.ruta };
	}
}