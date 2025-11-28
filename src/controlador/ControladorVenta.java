package controlador;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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

/**
 * Controlador principal del proceso de ventas.
 * <p>
 * Gestiona la lógica de negocio para:
 * <ul>
 * <li>Búsqueda y selección de productos.</li>
 * <li>Administración del "carrito de compras" ({@link Venta} y
 * {@link VentaDetalle}).</li>
 * <li>Cálculo en tiempo real de subtotales, impuestos (IVA) y descuentos.</li>
 * <li>Finalización de la transacción y generación del ticket.</li>
 * </ul>
 * </p>
 * 
 * @version 1.2
 */
public class ControladorVenta {

	private VentaDAO ventaDAO;
	private AlmacenProductosDAO productoDAO;
	private ClienteDAO clienteDAO;
	private EmpleadoDAO empleadoDAO;

	private PanelVenta vistaVenta;
	private AlmacenProductos productoSeleccionado;

	/**
	 * Objeto que mantiene el estado actual de la venta en curso (Cabecera +
	 * Detalles).
	 */
	private Venta ventaActual;

	/** ID del turno de caja abierto al que se vincularán las ventas. */
	private int corteCajaIDActual;

	/**
	 * Constructor principal.
	 * <p>
	 * Inicializa los DAOs, carga los datos iniciales en los ComboBox
	 * (Clientes/Empleados), crea una nueva instancia de Venta vacía y asigna los
	 * listeners a la vista.
	 * </p>
	 * * @param vDAO DAO de ventas.
	 * 
	 * @param pDAO    DAO de productos.
	 * @param cDAO    DAO de clientes.
	 * @param eDAO    DAO de empleados.
	 * @param vista   Panel gráfico de venta.
	 * @param corteID ID del corte de caja activo.
	 */
	public ControladorVenta(VentaDAO vDAO, AlmacenProductosDAO pDAO, ClienteDAO cDAO, EmpleadoDAO eDAO,
			PanelVenta vista, int corteID) {
		this.ventaDAO = vDAO;
		this.productoDAO = pDAO;
		this.clienteDAO = cDAO;
		this.empleadoDAO = eDAO;
		this.vistaVenta = vista;
		this.corteCajaIDActual = corteID;
		this.productoSeleccionado = null;
		this.ventaActual = new Venta(); // Inicia una nueva transacción en memoria

		cargarClientesEnComboBox();
		cargarEmpleadosEnComboBox();

		// Listeners de acciones principales
		this.vistaVenta.addBuscarProductoListener(e -> buscarProducto());
		this.vistaVenta.addAgregarCarritoListener(e -> agregarAlCarrito());
		this.vistaVenta.addFinalizarVentaListener(e -> finalizarVenta());
		this.vistaVenta.addQuitarDelCarritoListener(e -> quitarProductodelCarrito());

		// Listener en tiempo real para recalcular totales al escribir descuento
		this.vistaVenta.addDescuentoListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				recalcularTotales();
			}
		});
	}

	/**
	 * Realiza el cálculo financiero completo de la venta actual.
	 * <p>
	 * <b>Algoritmo:</b>
	 * <ol>
	 * <li>Suma los subtotales de los productos
	 * ({@code ventaActual.recalcularSubtotal()}).</li>
	 * <li>Lee el descuento ingresado por el usuario.</li>
	 * <li>Calcula la Base Gravable = Subtotal - Descuento.</li>
	 * <li>Calcula Impuestos = Base * IVA (16%).</li>
	 * <li>Calcula Total Final = Base + Impuestos.</li>
	 * </ol>
	 * Actualiza tanto el modelo de datos como la vista.
	 * </p>
	 */
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

		// IVA definido como constante en la Vista (0.16)
		double impuestos = baseParaImpuestos * PanelVenta.IVA_PORCENTAJE;

		double totalFinal = baseParaImpuestos + impuestos;

		// Actualizamos el modelo
		ventaActual.setSubtotal(subtotal);
		ventaActual.setDescuento(descuento);
		ventaActual.setImpuestos(impuestos);
		ventaActual.setTotal(totalFinal);

		// Actualizamos la interfaz gráfica
		vistaVenta.actualizarTotales(subtotal, descuento, impuestos, totalFinal);
	}

	/**
	 * Busca un producto en el catálogo por ID, Código o Nombre.
	 * <p>
	 * Utiliza la misma lógica de jerarquía que en otros controladores: primero
	 * intenta ID, luego Código exacto, y finalmente Nombre parcial.
	 * </p>
	 */
	private void buscarProducto() {
		String terminoBusqueda = vistaVenta.getCodigoProductoBuscado().trim();
		if (terminoBusqueda.isEmpty()) {
			vistaVenta.mostrarError("El campo de búsqueda no puede estar vacío.");
			return;
		}

		List<AlmacenProductos> productosEncontrados = new ArrayList<>();
		AlmacenProductos productoUnico = null;

		// 1. Búsqueda por ID
		try {
			int id = Integer.parseInt(terminoBusqueda);
			productoUnico = productoDAO.buscarPorID(id);
			if (productoUnico != null) {
				productosEncontrados.add(productoUnico);
			}
		} catch (NumberFormatException e) {
			// No es número, ignorar
		}

		// 2. Búsqueda por Código
		if (productosEncontrados.isEmpty()) {
			productoUnico = productoDAO.buscarPorCodigo(terminoBusqueda);
			if (productoUnico != null) {
				productosEncontrados.add(productoUnico);
			}
		}

		// 3. Búsqueda por Nombre
		if (productosEncontrados.isEmpty()) {
			productosEncontrados = productoDAO.buscarPorNombre(terminoBusqueda);
		}

		// Procesamiento de resultados
		if (productosEncontrados.isEmpty()) {
			vistaVenta.mostrarError("No se encontraron productos con ese ID, código o nombre.");
			this.productoSeleccionado = null;
		} else if (productosEncontrados.size() == 1) {
			AlmacenProductos producto = productosEncontrados.get(0);
			this.productoSeleccionado = producto;
			vistaVenta.mostrarDatosProducto(producto);
		} else {
			// Selección manual en caso de ambigüedad
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

	/**
	 * Agrega el producto seleccionado actualmente al carrito de venta.
	 * <p>
	 * Realiza validaciones de cantidad (positivo) y stock disponible. Si es
	 * exitoso, crea un {@link VentaDetalle}, lo añade al modelo y recalcula los
	 * totales.
	 * </p>
	 */
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

		// Validación de Stock
		if (cantidadAVender > productoSeleccionado.getCantidad()) {
			vistaVenta.mostrarError("No hay suficiente stock. Disponible: " + productoSeleccionado.getCantidad());
			return;
		}

		VentaDetalle detalle = new VentaDetalle(productoSeleccionado.getid(), productoSeleccionado.getNombre(),
				productoSeleccionado.getDescripcion(), cantidadAVender, productoSeleccionado.getPrecio());

		ventaActual.agregarDetalle(detalle);
		vistaVenta.agregarDetalleAlCarrito(detalle);

		recalcularTotales(); // Actualizar sumas financieras

		this.productoSeleccionado = null;
	}

	/**
	 * Elimina un producto de la lista de venta antes de finalizar.
	 */
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

			recalcularTotales(); // Recalcular al eliminar ítem
		}
	}

	/**
	 * Ejecuta el cierre de la venta.
	 * <p>
	 * 1. Valida que haya productos y actores (Cliente/Empleado) seleccionados.<br>
	 * 2. Asigna los datos finales al objeto {@link Venta} (Fecha, CorteID,
	 * Totales).<br>
	 * 3. Delega la persistencia al {@link VentaDAO}.<br>
	 * 4. Si es exitoso, genera el ticket, lo imprime en consola y reinicia la
	 * interfaz.
	 * </p>
	 */
	private void finalizarVenta() {
		if (ventaActual.getDetalles().isEmpty()) {
			vistaVenta.mostrarError("No se puede finalizar una venta sin productos en el carrito.");
			return;
		}

		Clientes clienteSeleccionado = vistaVenta.getClienteSeleccionado();
		Empleado empleadoSeleccionado = vistaVenta.getEmpleadoSeleccionado();

		if (clienteSeleccionado == null || empleadoSeleccionado == null) {
			vistaVenta.mostrarError("Debe seleccionar un cliente y un empleado para la venta.");
			return;
		}

		recalcularTotales(); // Último recálculo de seguridad

		// Asignación de datos finales
		ventaActual.setClienteId(clienteSeleccionado.getid());
		ventaActual.setNombreCliente(clienteSeleccionado.getNombre());
		ventaActual.setEmpleadoId(empleadoSeleccionado.getid());
		ventaActual.setNombreEmpleado(empleadoSeleccionado.getNombre());
		ventaActual.setMetodoPago(vistaVenta.getMetodoPagoSeleccionado());
		ventaActual.setFecha(new Date());
		ventaActual.setCorteID(this.corteCajaIDActual); // Vinculación crítica con la caja

		boolean exito = ventaDAO.agregar(ventaActual); // Transacción SQL

		if (exito) {
			vistaVenta.mostrarMensaje("Venta finalizada con éxito. ID de Venta: " + ventaActual.getid());

			// Generación de Ticket (Texto plano)
			Ticket ticket = new Ticket(this.ventaActual);
			String textoTicket = ticket.generarTextoTicket();
			System.out.println("--- INICIO DEL TICKET ---");
			System.out.println(textoTicket);
			System.out.println("---- FIN DEL TICKET ----");

			// Limpieza y Reinicio
			vistaVenta.limpiarCampos();
			this.ventaActual = new Venta();
			recalcularTotales(); // Resetear etiquetas a $0.00

		} else {
			vistaVenta.mostrarError("Ocurrió un error al guardar la venta en la base de datos.");
		}
	}

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