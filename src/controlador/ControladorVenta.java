package controlador;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import java.awt.event.KeyAdapter; // <-- IMPORTACIÓN NUEVA
import java.awt.event.KeyEvent; // <-- IMPORTACIÓN NUEVA

import modelo.AlmacenProductos;
import modelo.Clientes;
import modelo.Empleado;
import modelo.Ticket;
import modelo.Venta;
import modelo.VentaDetalle;
import persistencia.AlmacenProductosDAO;
import persistencia.ClienteDAO;
import persistencia.EmpleadoDAO;
import persistencia.VentaDAO;
import vista.PanelVenta;

public class ControladorVenta {
	private VentaDAO ventaDAO;
	private AlmacenProductosDAO productoDAO;
	private ClienteDAO clienteDAO;
	private EmpleadoDAO empleadoDAO;

	private PanelVenta vistaVenta;
	private AlmacenProductos productoSeleccionado;
	private Venta ventaActual;
	private int corteCajaIDActual;

	public ControladorVenta(VentaDAO vDAO, AlmacenProductosDAO pDAO, ClienteDAO cDAO, EmpleadoDAO eDAO,
			PanelVenta vista, int corteID) { 
		this.ventaDAO = vDAO;
		this.productoDAO = pDAO;
		this.clienteDAO = cDAO;
		this.empleadoDAO = eDAO;
		this.vistaVenta = vista;
		this.corteCajaIDActual = corteID;
		this.productoSeleccionado = null;
		this.ventaActual = new Venta(); 

		cargarClientesEnComboBox();
		cargarEmpleadosEnComboBox();

		this.vistaVenta.addBuscarProductoListener(e -> buscarProducto());
		this.vistaVenta.addAgregarCarritoListener(e -> agregarAlCarrito());
		this.vistaVenta.addFinalizarVentaListener(e -> finalizarVenta());
		this.vistaVenta.addQuitarDelCarritoListener(e -> quitarProductodelCarrito());

		
		this.vistaVenta.addDescuentoListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				recalcularTotales();
			}
		});
		
	}

	
	private void recalcularTotales() {

		double subtotal = ventaActual.recalcularSubtotal();

		double descuento = 0.0;
		try {
			String textoDescuento = vistaVenta.getTdescuentoVenta().getText();
			if (textoDescuento != null && !textoDescuento.isEmpty()) {
				descuento = Double.parseDouble(textoDescuento);
			}
			if (descuento < 0) {
				descuento = 0;
			}
		} catch (NumberFormatException e) {
			descuento = 0.0;
		}

		double baseParaImpuestos = subtotal - descuento;
		if (baseParaImpuestos < 0) {
			baseParaImpuestos = 0;
		}
		double impuestos = baseParaImpuestos * PanelVenta.IVA_PORCENTAJE;

		double totalFinal = baseParaImpuestos + impuestos;

		ventaActual.setSubtotal(subtotal);
		ventaActual.setDescuento(descuento);
		ventaActual.setImpuestos(impuestos);
		ventaActual.setTotal(totalFinal);

		vistaVenta.actualizarTotales(subtotal, descuento, impuestos, totalFinal);
	}

	private void buscarProducto() {
		String terminoBusqueda = vistaVenta.getCodigoProductoBuscado().trim();
		if (terminoBusqueda.isEmpty()) {
			vistaVenta.mostrarError("El campo de búsqueda no puede estar vacío.");
			return;
		}

		List<AlmacenProductos> productosEncontrados = new ArrayList<>();
		AlmacenProductos productoUnico = null;

		try {
			int id = Integer.parseInt(terminoBusqueda);
			productoUnico = productoDAO.buscarPorID(id);
			if (productoUnico != null) {
				productosEncontrados.add(productoUnico);
			}
		} catch (NumberFormatException e) {
			// No es un número, continuamos.
		}

		if (productosEncontrados.isEmpty()) {
			productoUnico = productoDAO.buscarPorCodigo(terminoBusqueda);
			if (productoUnico != null) {
				productosEncontrados.add(productoUnico);
			}
		}

		if (productosEncontrados.isEmpty()) {
			productosEncontrados = productoDAO.buscarPorNombre(terminoBusqueda);
		}

		if (productosEncontrados.isEmpty()) {
			vistaVenta.mostrarError("No se encontraron productos con ese ID, código o nombre.");
			this.productoSeleccionado = null;
		} else if (productosEncontrados.size() == 1) {
			AlmacenProductos producto = productosEncontrados.get(0);
			this.productoSeleccionado = producto;
			vistaVenta.mostrarDatosProducto(producto);
		} else {
			AlmacenProductos[] opciones = productosEncontrados.toArray(new AlmacenProductos[0]);
			AlmacenProductos productoElegido = (AlmacenProductos) JOptionPane.showInputDialog(vistaVenta,
					"Se encontraron varios productos, por favor elija uno:", "Seleccionar Producto",
					JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

			if (productoElegido != null) {
				this.productoSeleccionado = productoElegido;
				vistaVenta.mostrarDatosProducto(productoElegido);
			} else {
				this.productoSeleccionado = null;
			}
		}
	}

	// --- ESTE MÉTODO HA SIDO MODIFICADO ---
	private void agregarAlCarrito() {
		if (productoSeleccionado == null) {
			vistaVenta.mostrarError("Primero debe buscar y encontrar un producto válido.");
			return;
		}
		int cantidadAVender;
		try {
			cantidadAVender = vistaVenta.getCantidadAVender();
			if (cantidadAVender <= 0) {
				vistaVenta.mostrarError("La cantidad debe ser mayor que cero.");
				return;
			}
		} catch (NumberFormatException e) {
			vistaVenta.mostrarError("Por favor, ingrese un número válido en la cantidad.");
			return;
		}
		if (cantidadAVender > productoSeleccionado.getCantidad()) {
			vistaVenta.mostrarError("No hay suficiente stock. Disponible: " + productoSeleccionado.getCantidad());
			return;
		}

		VentaDetalle detalle = new VentaDetalle(productoSeleccionado.getid(), productoSeleccionado.getNombre(),
				productoSeleccionado.getDescripcion(), cantidadAVender, productoSeleccionado.getPrecio());

		ventaActual.agregarDetalle(detalle);
		vistaVenta.agregarDetalleAlCarrito(detalle);

		// --- CAMBIO: Llamar al nuevo método de cálculo ---
		recalcularTotales();
		// --- FIN DEL CAMBIO ---

		this.productoSeleccionado = null;
	}
	// --- FIN DEL MÉTODO MODIFICADO ---

	// --- ESTE MÉTODO HA SIDO MODIFICADO ---
	private void quitarProductodelCarrito() {
		int filaSeleccionada = vistaVenta.getFilaSeleccionadaCarrito();
		if (filaSeleccionada == -1) {
			JOptionPane.showMessageDialog(vistaVenta, "Debe seleccionar un producto a eliminar de la lista.");
			return;
		}

		int confirmacion = JOptionPane.showConfirmDialog(vistaVenta,
				"¿Seguro que desea quitar este producto del carrito?", "Confirmar eliminación",
				JOptionPane.YES_NO_OPTION);

		if (confirmacion == JOptionPane.YES_OPTION) {
			ventaActual.quitarDetalle(filaSeleccionada);
			vistaVenta.quitarDetalleDelCarrito(filaSeleccionada);

			// --- CAMBIO: Llamar al nuevo método de cálculo ---
			recalcularTotales();
			// --- FIN DEL CAMBIO ---
		}
	}
	// --- FIN DEL MÉTODO MODIFICADO ---

	// --- ESTE MÉTODO HA SIDO MODIFICADO ---
	private void finalizarVenta() {
		if (ventaActual.getDetalles().isEmpty()) {
			vistaVenta.mostrarError("No se puede finalizar una venta sin productos en el carrito.");
			return;
		}

		// Obtenemos los OBJETOS COMPLETOS desde la vista.
		Clientes clienteSeleccionado = vistaVenta.getClienteSeleccionado();
		Empleado empleadoSeleccionado = vistaVenta.getEmpleadoSeleccionado();

		if (clienteSeleccionado == null || empleadoSeleccionado == null) {
			vistaVenta.mostrarError("Debe seleccionar un cliente y un empleado para la venta.");
			return;
		}

		
		recalcularTotales();

		ventaActual.setClienteId(clienteSeleccionado.getid());
		ventaActual.setNombreCliente(clienteSeleccionado.getNombre());
		ventaActual.setEmpleadoId(empleadoSeleccionado.getid());
		ventaActual.setNombreEmpleado(empleadoSeleccionado.getNombre());
		ventaActual.setMetodoPago(vistaVenta.getMetodoPagoSeleccionado());
		ventaActual.setFecha(new Date());
		ventaActual.setCorteID(this.corteCajaIDActual); 
		
		boolean exito = ventaDAO.agregar(ventaActual);
	

		if (exito) {
			vistaVenta.mostrarMensaje("Venta finalizada con éxito. ID de Venta: " + ventaActual.getid());

			Ticket ticket = new Ticket(this.ventaActual);
			String textoTicket = ticket.generarTextoTicket();
			System.out.println("--- INICIO DEL TICKET ---");
			System.out.println(textoTicket);
			System.out.println("---- FIN DEL TICKET ----");

			vistaVenta.limpiarCampos();
			this.ventaActual = new Venta();

			// --- AÑADIDO: Resetear los totales a 0 en la vista ---
			recalcularTotales();
			// --------------------------------------------------

		} else {
			vistaVenta.mostrarError("Ocurrió un error al guardar la venta en la base de datos.");
		}
	}
	// --- FIN DEL MÉTODO MODIFICADO ---

	// --- ESTOS MÉTODOS NO CAMBIAN ---
	private void cargarClientesEnComboBox() {
		List<Clientes> clientes = clienteDAO.ObtenerTodo();
		if (clientes != null) {
			vistaVenta.cargarClientes(clientes);
		}
	}

	private void cargarEmpleadosEnComboBox() {
		List<Empleado> empleados = empleadoDAO.ObtenerTodo();
		if (empleados != null) {
			vistaVenta.cargarEmpleados(empleados);
		}
	}
}