package controlador;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import modelo.AlmacenProductos;
import modelo.OrdenCompra;
import modelo.OrdenCompraDetalle;
import modelo.Proveedor;
import persistencia.AlmacenProductosDAO;
import persistencia.OrdenCompraDAO;
import persistencia.ProveedorDAO;
import vista.PanelCrearPedido;

/**
 * Controlador encargado de la lógica para CREAR una nueva Orden de Compra a
 * proveedores.
 * <p>
 * Gestiona la interacción en el {@link PanelCrearPedido}. Sus responsabilidades
 * principales son:
 * <ul>
 * <li>Buscar productos en el catálogo para reabastecimiento.</li>
 * <li><b>Validar reglas de negocio:</b> Asegurar que todos los productos de la
 * orden pertenezcan al mismo proveedor.</li>
 * <li>Administrar el "carrito" temporal de productos a pedir (en memoria).</li>
 * <li>Persistir la orden final con estado inicial "Pendiente".</li>
 * </ul>
 * </p>
 * * @version 1.1
 */
public class ControladorCrearPedido {

	private OrdenCompraDAO ordenCompraDAO;
	private AlmacenProductosDAO productoDAO;
	private ProveedorDAO proveedorDAO;

	private PanelCrearPedido vistaPedido;
	private AlmacenProductos productoSeleccionado;

	/**
	 * Objeto que mantiene el estado actual del pedido en construcción (Cabecera +
	 * Lista de Detalles).
	 */
	private OrdenCompra ordenActual;

	/**
	 * Constructor principal.
	 * <p>
	 * Inicializa los DAOs, crea una nueva instancia de {@link OrdenCompra} vacía,
	 * carga la lista de proveedores en la vista y asigna los listeners a los
	 * botones.
	 * </p>
	 * * @param ocDAO DAO para guardar la orden final.
	 * 
	 * @param pDAO    DAO para buscar productos.
	 * @param provDAO DAO para llenar el combo de proveedores.
	 * @param vista   Panel gráfico de creación de pedidos.
	 */
	public ControladorCrearPedido(OrdenCompraDAO ocDAO, AlmacenProductosDAO pDAO, ProveedorDAO provDAO,
			PanelCrearPedido vista) {
		this.ordenCompraDAO = ocDAO;
		this.productoDAO = pDAO;
		this.proveedorDAO = provDAO;
		this.vistaPedido = vista;

		this.ordenActual = new OrdenCompra();
		this.productoSeleccionado = null;

		cargarProveedoresEnComboBox();

		this.vistaPedido.addBuscarProductoListener(e -> buscarProducto());
		this.vistaPedido.addAgregarAlPedidoListener(e -> agregarAlPedido());
		this.vistaPedido.addFinalizarPedidoListener(e -> finalizarPedido());
		this.vistaPedido.addQuitarDelPedidoListener(e -> quitarProductoDelPedido());
		this.vistaPedido.addCancelarPedidoListener(e -> vista.limpiarCampos());
	}

	/**
	 * Recupera todos los proveedores y los carga en el JComboBox de la vista.
	 */
	private void cargarProveedoresEnComboBox() {
		List<Proveedor> proveedores = proveedorDAO.ObtenerTodo();
		if (proveedores != null) {
			vistaPedido.cargarProveedores(proveedores);
		}
	}

	/**
	 * Busca un producto en el inventario por ID, Código o Nombre.
	 * <p>
	 * Sigue una jerarquía de búsqueda:
	 * <ol>
	 * <li>Intenta buscar por ID numérico.</li>
	 * <li>Si falla, busca por Código de barras exacto.</li>
	 * <li>Si falla, busca coincidencias por Nombre.</li>
	 * </ol>
	 * Si encuentra múltiples coincidencias por nombre, permite al usuario elegir
	 * mediante un cuadro de diálogo.
	 * </p>
	 */
	private void buscarProducto() {
		String terminoBusqueda = vistaPedido.getCodigoProductoBuscado().trim();
		if (terminoBusqueda.isEmpty()) {
			vistaPedido.mostrarError("El campo de búsqueda no puede estar vacío.");
			return;
		}

		List<AlmacenProductos> productosEncontrados = new ArrayList<>();
		AlmacenProductos productoUnico = null;

		// 1. Intento por ID
		try {
			int id = Integer.parseInt(terminoBusqueda);
			productoUnico = productoDAO.buscarPorID(id);
			if (productoUnico != null)
				productosEncontrados.add(productoUnico);
		} catch (NumberFormatException e) {
			// No es ID, continuamos
		}

		// 2. Intento por Código
		if (productosEncontrados.isEmpty()) {
			productoUnico = productoDAO.buscarPorCodigo(terminoBusqueda);
			if (productoUnico != null)
				productosEncontrados.add(productoUnico);
		}

		// 3. Intento por Nombre
		if (productosEncontrados.isEmpty()) {
			productosEncontrados = productoDAO.buscarPorNombre(terminoBusqueda);
		}

		// Procesar resultados
		if (productosEncontrados.isEmpty()) {
			vistaPedido.mostrarError("No se encontraron productos con ese ID, código o nombre.");
			this.productoSeleccionado = null;
		} else if (productosEncontrados.size() == 1) {
			this.productoSeleccionado = productosEncontrados.get(0);
			vistaPedido.mostrarDatosProducto(productoSeleccionado);
		} else {
			// Selección manual si hay ambigüedad
			AlmacenProductos[] opciones = productosEncontrados.toArray(new AlmacenProductos[0]);
			AlmacenProductos productoElegido = (AlmacenProductos) JOptionPane.showInputDialog(vistaPedido,
					"Se encontraron varios productos, elija uno:", "Seleccionar Producto", JOptionPane.QUESTION_MESSAGE,
					null, opciones, opciones[0]);

			if (productoElegido != null) {
				this.productoSeleccionado = productoElegido;
				vistaPedido.mostrarDatosProducto(productoElegido);
			} else {
				this.productoSeleccionado = null;
			}
		}
	}

	/**
	 * Agrega el producto seleccionado a la lista de pedido (Carrito).
	 * <p>
	 * <b>Regla de Negocio Crítica:</b> Verifica que el producto seleccionado
	 * pertenezca al proveedor definido para la orden. Si el usuario intenta mezclar
	 * proveedores, se bloquea la acción.
	 * </p>
	 * <p>
	 * Además, si es el primer producto agregado, bloquea el selector de proveedores
	 * en la vista para mantener la consistencia.
	 * </p>
	 */
	private void agregarAlPedido() {
		if (productoSeleccionado == null) {
			vistaPedido.mostrarError("Primero debe buscar y encontrar un producto válido.");
			return;
		}

		Proveedor proveedorDelPedido = vistaPedido.getProveedorSeleccionado();
		if (proveedorDelPedido == null) {
			vistaPedido.mostrarError("Por favor, seleccione un proveedor en la parte superior.");
			return;
		}

		// Validación de proveedor cruzado
		if (!ordenActual.getDetalles().isEmpty()
				&& proveedorDelPedido.getid() != productoSeleccionado.getProveedorId()) {

			vistaPedido.mostrarError("Este pedido es para '" + proveedorDelPedido.getNombre() + "'.\n"
					+ "No puede agregar productos de un proveedor diferente.");
			return;
		}

		int cantidadAPedir;
		double costoUnitario;
		try {
			cantidadAPedir = vistaPedido.getCantidadAPedir();
			if (cantidadAPedir <= 0) {
				vistaPedido.mostrarError("La cantidad debe ser mayor que cero.");
				return;
			}
			costoUnitario = Double.parseDouble(vistaPedido.getTcostoProducto().getText());

		} catch (NumberFormatException e) {
			vistaPedido.mostrarError("Por favor, ingrese un número válido en la cantidad o costo.");
			return;
		}

		// Creación del detalle en memoria
		OrdenCompraDetalle detalle = new OrdenCompraDetalle(productoSeleccionado.getid(),
				productoSeleccionado.getNombre(), productoSeleccionado.getDescripcion(), cantidadAPedir, costoUnitario);

		ordenActual.agregarDetalle(detalle);
		vistaPedido.agregarDetalleAlPedido(detalle);

		// Bloqueo de UI para consistencia
		if (ordenActual.getDetalles().size() == 1) { // Si este fue el PRIMER producto...
			vistaPedido.setProveedorComboBoxEnabled(false);
		}

		recalcularTotal();
		this.productoSeleccionado = null;
	}

	/**
	 * Elimina un producto de la lista actual del pedido.
	 */
	private void quitarProductoDelPedido() {
		int filaSeleccionada = vistaPedido.getFilaSeleccionadaPedido();
		if (filaSeleccionada == -1) {
			vistaPedido.mostrarError("Debe seleccionar un producto a eliminar de la lista.");
			return;
		}

		ordenActual.quitarDetalle(filaSeleccionada);
		vistaPedido.quitarDetalleDelPedido(filaSeleccionada);

		recalcularTotal(); // Actualizar suma total visual
	}

	/**
	 * Recorre la lista de detalles y suma (Costo * Cantidad) para mostrar el total
	 * estimado.
	 */
	private void recalcularTotal() {
		double totalActual = 0;
		for (OrdenCompraDetalle d : ordenActual.getDetalles()) {
			totalActual += d.getCostoUnitario() * d.getCantidadPedida();
		}
		vistaPedido.actualizarTotalPedido(totalActual);
	}

	/**
	 * Guarda la orden de compra en la base de datos.
	 * <p>
	 * Asigna el estado inicial "Pendiente" y delega la transacción al
	 * {@link OrdenCompraDAO}. Si tiene éxito, limpia la interfaz para permitir un
	 * nuevo pedido.
	 * </p>
	 */
	private void finalizarPedido() {
		if (ordenActual.getDetalles().isEmpty()) {
			vistaPedido.mostrarError("No se puede finalizar un pedido sin productos.");
			return;
		}

		Proveedor proveedorSeleccionado = vistaPedido.getProveedorSeleccionado();
		if (proveedorSeleccionado == null) {
			vistaPedido.mostrarError("Debe seleccionar un proveedor.");
			return;
		}

		// Asignamos los datos al objeto "maestro"
		ordenActual.setProveedorId(proveedorSeleccionado.getid());
		ordenActual.setNombreProveedor(proveedorSeleccionado.getNombre());
		ordenActual.setStatus("Pendiente"); // El estado inicial obligatorio

		// Llamamos al DAO para la transacción
		boolean exito = ordenCompraDAO.agregar(ordenActual);

		if (exito) {
			vistaPedido.mostrarMensaje(
					"Pedido #" + ordenActual.getid() + " generado con éxito. Queda 'Pendiente' de recepción.");
			vistaPedido.limpiarCampos();
			this.ordenActual = new OrdenCompra(); // Reiniciamos el objeto para una nueva orden
		} else {
			vistaPedido.mostrarError("Ocurrió un error al guardar el pedido en la base de datos.");
		}
	}
}