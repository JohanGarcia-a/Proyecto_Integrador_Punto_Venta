package controlador;

import java.awt.Dimension;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import modelo.AlmacenProductos;
import modelo.Empleado;
import modelo.EntradaInventario;
import modelo.OrdenCompra;
import modelo.OrdenCompraDetalle;
import persistencia.AlmacenProductosDAO;
import persistencia.EntradaInventarioDAO;
import persistencia.OrdenCompraDAO;
import persistencia.OrdenCompraDetalleDAO;
import persistencia.ProveedorDAO;
import vista.PanelCrearPedido;
import vista.PanelGestionPedidos;

// 1. Hereda de Controlador_Generico para reusar la tabla y la búsqueda
public class ControladorGestionPedidos extends ControladorGenerico<OrdenCompra> {

	private PanelGestionPedidos vistaGestion;
	private Empleado usuarioActual;

	// 2. DAOs adicionales que este controlador necesita
	private OrdenCompraDetalleDAO detalleDAO;
	private AlmacenProductosDAO almacenDAO;
	private EntradaInventarioDAO entradaDAO;
	private ProveedorDAO proveedorDAO; // Para el popup de "Crear"

	// 3. El constructor recibe el usuario (¡importante!)
	public ControladorGestionPedidos(OrdenCompraDAO modelo, PanelGestionPedidos vista, Empleado usuario,
			AlmacenProductosDAO almacenDAO, ProveedorDAO provDAO) {

		super(modelo, vista); // Llama al constructor genérico (que ya llama a mostrarTodo())

		this.vistaGestion = vista;
		this.usuarioActual = usuario;

		// Instanciamos los DAOs que usaremos
		this.detalleDAO = new OrdenCompraDetalleDAO();
		this.entradaDAO = new EntradaInventarioDAO();
		this.almacenDAO = almacenDAO; // Reusamos el que viene del constructor
		this.proveedorDAO = provDAO; // Reusamos el que viene del constructor

		// 4. Añadimos los listeners para los botones NUEVOS
		this.vistaGestion.addRecibirPedidoListener(e -> recibirPedido());
		this.vistaGestion.addCrearNuevoPedidoListener(e -> mostrarPanelCrearPedido());
		this.vistaGestion.addVerDetallesListener(e -> verDetalles());
		// Sobrescribimos el botón "Borrar" genérico para que "Cancele"
		vista.addBorrarListener(e -> cancelarPedido());
	}

	// 5. Método para el botón "Recibir Pedido"
	private void recibirPedido() {
		int id = vista.filaSelect();
		if (id == -1) {
			vista.mostrarError("Seleccione un pedido de la tabla.");
			return;
		}

		// Verificamos el estado del pedido
		OrdenCompra orden = ((OrdenCompraDAO) this.modelo).buscarPorID(id);
		if (orden == null) {
			vista.mostrarError("No se encontró el pedido.");
			return;
		}
		if (!"Pendiente".equalsIgnoreCase(orden.getStatus())) {
			vista.mostrarError("Este pedido ya fue '" + orden.getStatus() + "' y no se puede recibir.");
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(vista,
				"¿Seguro que desea recibir el Pedido #" + id + "?\n"
						+ "Esta acción agregará los productos al inventario.",
				"Confirmar Recepción", JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {

			// 1. Obtener los detalles (la lista de productos) del pedido
			List<OrdenCompraDetalle> detalles = detalleDAO.buscarDetallesPorOrdenID(id);
			if (detalles.isEmpty()) {
				vista.mostrarError("Error: El pedido no tiene productos para recibir.");
				return;
			}

			// 2. Recorrer los detalles y actualizar el stock
			for (OrdenCompraDetalle det : detalles) {
				// 2.1 Aumentar el stock
				almacenDAO.aumentarStock(det.getProductoId(), det.getCantidadPedida());

				// 2.2 Registrar en el historial de entradas (¡REUSAMOS TU LÓGICA!)
				AlmacenProductos producto = almacenDAO.buscarPorID(det.getProductoId());
				String descripcionEntrada = (producto != null) ? producto.getDescripcion() : "Recepción Pedido #" + id;

				EntradaInventario entrada = new EntradaInventario(det.getProductoId(), det.getCantidadPedida(),
						new Date(), this.usuarioActual.getid(), // ¡Usamos el usuario que está logueado!
						descripcionEntrada);
				entradaDAO.agregar(entrada);
			}

			// 3. Actualizar el status del pedido a "Recibido"
			((OrdenCompraDAO) this.modelo).modificarStatus(id, "Recibido");

			// 4. Refrescar la tabla
			mostrarTodo();
			vista.mostrarMensaje("Pedido #" + id + " recibido con éxito. Inventario actualizado.");
		}
	}

	// 6. Método para el botón "Crear Nuevo Pedido"
	private void mostrarPanelCrearPedido() {
		// Creamos la vista (Módulo 1)
		PanelCrearPedido panelCrear = new PanelCrearPedido();

		// Creamos su controlador
		new ControladorCrearPedido((OrdenCompraDAO) this.modelo, // Reusamos el DAO de órdenes
				this.almacenDAO, // Reusamos el DAO de productos
				this.proveedorDAO, // Reusamos el DAO de proveedores
				panelCrear);

		// Mostramos el panel en una ventana emergente (JOptionPane)
		JOptionPane.showMessageDialog(vistaGestion, // El panel padre
				panelCrear, // El panel a mostrar
				"Crear Nuevo Pedido a Proveedor", // Título de la ventana
				JOptionPane.PLAIN_MESSAGE, null // Sin ícono
		);

		// Al cerrar el popup, refrescamos la lista de pedidos
		mostrarTodo();
	}

	// 7. Sobrescribimos el método borrar() de la clase padre
	@Override
	public void borrar() {
		cancelarPedido();
	}

	private void cancelarPedido() {
		int id = vista.filaSelect();
		if (id == -1) {
			vista.mostrarError("Seleccione un pedido de la tabla.");
			return;
		}

		OrdenCompra orden = ((OrdenCompraDAO) this.modelo).buscarPorID(id);
		if (orden == null) {
			vista.mostrarError("No se encontró el pedido.");
			return;
		}
		if (!"Pendiente".equalsIgnoreCase(orden.getStatus())) {
			vista.mostrarError("Solo se pueden cancelar pedidos que estén 'Pendiente'.");
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(vista, "¿Seguro que desea CANCELAR el Pedido #" + id + "?",
				"Confirmar Cancelación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		if (confirm == JOptionPane.YES_OPTION) {
			// 3. Actualizar el status del pedido a "Cancelado"
			((OrdenCompraDAO) this.modelo).modificarStatus(id, "Cancelado");
			mostrarTodo(); // Refrescar
		}
	}

	// --- MÉTODO NUEVO PARA VER DETALLES ---
	private void verDetalles() {
		int id = vista.filaSelect();
		if (id == -1) {
			vista.mostrarError("Seleccione un pedido de la tabla para ver sus detalles.");
			return;
		}

		// Buscar la orden para tener el nombre del proveedor
		OrdenCompra orden = ((OrdenCompraDAO) this.modelo).buscarPorID(id);
		if (orden == null) {
			vista.mostrarError("No se encontró la orden.");
			return;
		}

		// 1. Buscar los detalles en la BD
		List<OrdenCompraDetalle> detalles = detalleDAO.buscarDetallesPorOrdenID(id);

		if (detalles.isEmpty()) {
			vista.mostrarMensaje("Este pedido no tiene productos asociados.");
			return;
		}

		// 2. Crear un modelo de tabla para el popup
		String[] columnas = { "ID Producto", "Producto", "Descripción", "Cantidad Pedida" };
		DefaultTableModel model = new DefaultTableModel(columnas, 0);

		for (OrdenCompraDetalle det : detalles) {
			model.addRow(new Object[] { det.getProductoId(), det.getNombreProducto(), det.getDescripcion(),
					det.getCantidadPedida() });
		}

		// 3. Crear la tabla y meterla en un JScrollPane
		JTable tablaDetalles = new JTable(model);
		tablaDetalles.setEnabled(false); // Para que no se pueda editar
		JScrollPane scrollPane = new JScrollPane(tablaDetalles);
		scrollPane.setPreferredSize(new Dimension(500, 250)); // Tamaño del popup

		// 4. Mostrar el JOptionPane con la tabla
		JOptionPane.showMessageDialog(vista, // Padre
				scrollPane, // Contenido
				"Detalles del Pedido #" + id + " (Proveedor: " + orden.getNombreProveedor() + ")", // Título
				JOptionPane.INFORMATION_MESSAGE);
	}
}