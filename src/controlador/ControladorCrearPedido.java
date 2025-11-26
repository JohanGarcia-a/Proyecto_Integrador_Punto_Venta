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

public class ControladorCrearPedido {

	private OrdenCompraDAO ordenCompraDAO;
	private AlmacenProductosDAO productoDAO;
	private ProveedorDAO proveedorDAO;

	private PanelCrearPedido vistaPedido;
	private AlmacenProductos productoSeleccionado;
	private OrdenCompra ordenActual;

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

	private void cargarProveedoresEnComboBox() {
		List<Proveedor> proveedores = proveedorDAO.ObtenerTodo();
		if (proveedores != null) {
			vistaPedido.cargarProveedores(proveedores);
		}
	}

	private void buscarProducto() {
		String terminoBusqueda = vistaPedido.getCodigoProductoBuscado().trim();
		if (terminoBusqueda.isEmpty()) {
			vistaPedido.mostrarError("El campo de búsqueda no puede estar vacío.");
			return;
		}

		List<AlmacenProductos> productosEncontrados = new ArrayList<>();
		AlmacenProductos productoUnico = null;

		try {
			int id = Integer.parseInt(terminoBusqueda);
			productoUnico = productoDAO.buscarPorID(id);
			if (productoUnico != null)
				productosEncontrados.add(productoUnico);
		} catch (NumberFormatException e) {
			 }

		if (productosEncontrados.isEmpty()) {
			productoUnico = productoDAO.buscarPorCodigo(terminoBusqueda);
			if (productoUnico != null)
				productosEncontrados.add(productoUnico);
		}

		if (productosEncontrados.isEmpty()) {
			productosEncontrados = productoDAO.buscarPorNombre(terminoBusqueda);
		}

		if (productosEncontrados.isEmpty()) {
			vistaPedido.mostrarError("No se encontraron productos con ese ID, código o nombre.");
			this.productoSeleccionado = null;
		} else if (productosEncontrados.size() == 1) {
			this.productoSeleccionado = productosEncontrados.get(0);
			vistaPedido.mostrarDatosProducto(productoSeleccionado);
		} else {
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

		OrdenCompraDetalle detalle = new OrdenCompraDetalle(productoSeleccionado.getid(),
				productoSeleccionado.getNombre(), productoSeleccionado.getDescripcion(), cantidadAPedir, costoUnitario);

		ordenActual.agregarDetalle(detalle);
		vistaPedido.agregarDetalleAlPedido(detalle);

		if (ordenActual.getDetalles().size() == 1) { // Si este fue el PRIMER producto...
			vistaPedido.setProveedorComboBoxEnabled(false);
		}

		recalcularTotal();
		this.productoSeleccionado = null;
	}

	private void quitarProductoDelPedido() {
		int filaSeleccionada = vistaPedido.getFilaSeleccionadaPedido();
		if (filaSeleccionada == -1) {

			vistaPedido.mostrarError("Debe seleccionar un producto a eliminar de la lista.");
			return;
		}

		ordenActual.quitarDetalle(filaSeleccionada);
		vistaPedido.quitarDetalleDelPedido(filaSeleccionada);

		recalcularTotal(); // Recalculamos
	}

	// Método privado para actualizar el total
	private void recalcularTotal() {
		double totalActual = 0;
		for (OrdenCompraDetalle d : ordenActual.getDetalles()) {
			totalActual += d.getCostoUnitario() * d.getCantidadPedida();
		}
		vistaPedido.actualizarTotalPedido(totalActual);
	}

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
		ordenActual.setStatus("Pendiente"); // El estado inicial

		// Llamamos al DAO para la transacción
		boolean exito = ordenCompraDAO.agregar(ordenActual);

		if (exito) {
			vistaPedido.mostrarMensaje(
					"Pedido #" + ordenActual.getid() + " generado con éxito. Queda 'Pendiente' de recepción.");
			vistaPedido.limpiarCampos();
			this.ordenActual = new OrdenCompra(); // Preparamos una nueva orden
		} else {
			vistaPedido.mostrarError("Ocurrió un error al guardar el pedido en la base de datos.");
		}
	}
}