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

/**
 * Controlador para la administración de Órdenes de Compra (Pedidos a
 * Proveedores).
 * <p>
 * Hereda de {@link ControladorGenerico} para aprovechar la funcionalidad de
 * listado y búsqueda en tabla, pero implementa lógica específica para:
 * <ul>
 * <li>Recepción de mercancía (impacto en inventario).</li>
 * <li>Creación de nuevos pedidos (abre un sub-panel).</li>
 * <li>Cancelación lógica de pedidos.</li>
 * </ul>
 * </p>
 * * @version 1.2
 */
public class ControladorGestionPedidos extends ControladorGenerico<OrdenCompra> {

	/** Referencia a la vista específica de gestión. */
	private PanelGestionPedidos vistaGestion;

	/** Usuario actual (necesario para registrar quién recibió el pedido). */
	private Empleado usuarioActual;

	// DAOs auxiliares para las operaciones complejas
	private OrdenCompraDetalleDAO detalleDAO;
	private AlmacenProductosDAO almacenDAO;
	private EntradaInventarioDAO entradaDAO;
	private ProveedorDAO proveedorDAO;

	/**
	 * Constructor principal.
	 * <p>
	 * Inicializa los DAOs auxiliares y configura los listeners para los botones
	 * específicos de esta vista ("Recibir", "Crear Nuevo", "Ver Detalles").
	 * </p>
	 * * @param modelo DAO principal de Órdenes de Compra.
	 * 
	 * @param vista      Panel de gestión.
	 * @param usuario    Empleado logueado.
	 * @param almacenDAO DAO de productos (para actualizar stock).
	 * @param provDAO    DAO de proveedores.
	 */
	public ControladorGestionPedidos(OrdenCompraDAO modelo, PanelGestionPedidos vista, Empleado usuario,
			AlmacenProductosDAO almacenDAO, ProveedorDAO provDAO) {

		super(modelo, vista); // Llama al constructor padre para inicializar la tabla

		this.vistaGestion = vista;
		this.usuarioActual = usuario;

		// Instanciamos los DAOs que usaremos
		this.detalleDAO = new OrdenCompraDetalleDAO();
		this.entradaDAO = new EntradaInventarioDAO();
		this.almacenDAO = almacenDAO;
		this.proveedorDAO = provDAO;

		// Listeners personalizados
		this.vistaGestion.addRecibirPedidoListener(e -> recibirPedido());
		this.vistaGestion.addCrearNuevoPedidoListener(e -> mostrarPanelCrearPedido());
		this.vistaGestion.addVerDetallesListener(e -> verDetalles());

		// Sobrescribimos el comportamiento del botón "Borrar" para que ejecute
		// "Cancelar"
		vista.addBorrarListener(e -> cancelarPedido());
	}

	/**
	 * Procesa la recepción de mercancía de un pedido seleccionado.
	 * <p>
	 * <b>Flujo de la operación:</b>
	 * <ol>
	 * <li>Verifica que el pedido esté en estado "Pendiente".</li>
	 * <li>Solicita confirmación al usuario.</li>
	 * <li>Recupera los productos del pedido ({@code detalleDAO}).</li>
	 * <li>Por cada producto:
	 * <ul>
	 * <li>Aumenta el stock en {@code TablaAlmacen_Productos}.</li>
	 * <li>Crea un registro de auditoría en {@code TablaEntradasInventario}
	 * vinculado al usuario actual.</li>
	 * </ul>
	 * </li>
	 * <li>Actualiza el estado del pedido a "Recibido".</li>
	 * </ol>
	 * </p>
	 */
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
				// 2.1 Aumentar el stock físico
				almacenDAO.aumentarStock(det.getProductoId(), det.getCantidadPedida());

				// 2.2 Registrar en el historial de entradas (Auditoría)
				AlmacenProductos producto = almacenDAO.buscarPorID(det.getProductoId());
				String descripcionEntrada = (producto != null) ? producto.getDescripcion() : "Recepción Pedido #" + id;

				EntradaInventario entrada = new EntradaInventario(det.getProductoId(), det.getCantidadPedida(),
						new Date(), this.usuarioActual.getid(), // ¡Usuario que recibe!
						descripcionEntrada);
				entradaDAO.agregar(entrada);
			}

			// 3. Actualizar el status del pedido a "Recibido"
			((OrdenCompraDAO) this.modelo).modificarStatus(id, "Recibido");

			// 4. Refrescar la tabla para mostrar el nuevo estado
			mostrarTodo();
			vista.mostrarMensaje("Pedido #" + id + " recibido con éxito. Inventario actualizado.");
		}
	}

	/**
	 * Abre un panel emergente (modal) para crear una nueva orden de compra.
	 * <p>
	 * Instancia el {@link PanelCrearPedido} y su controlador correspondiente,
	 * mostrándolo dentro de un {@code JOptionPane}. Al cerrar la ventana, se
	 * refresca la lista principal para mostrar el nuevo pedido (si se creó).
	 * </p>
	 */
	private void mostrarPanelCrearPedido() {
		PanelCrearPedido panelCrear = new PanelCrearPedido();

		new ControladorCrearPedido((OrdenCompraDAO) this.modelo, this.almacenDAO, this.proveedorDAO, panelCrear);

		JOptionPane.showMessageDialog(vistaGestion, panelCrear, "Crear Nuevo Pedido a Proveedor",
				JOptionPane.PLAIN_MESSAGE, null);

		mostrarTodo();
	}

	/**
	 * Sobrescribe la acción de borrar para implementar una "Cancelación Lógica".
	 * <p>
	 * En lugar de eliminar el registro físico, cambia el estado a "Cancelado",
	 * manteniendo el historial.
	 * </p>
	 */
	@Override
	public void borrar() {
		cancelarPedido();
	}

	/**
	 * Cancela un pedido que aún no ha sido recibido.
	 */
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
			((OrdenCompraDAO) this.modelo).modificarStatus(id, "Cancelado");
			mostrarTodo();
		}
	}

	/**
	 * Muestra un cuadro de diálogo con el detalle de productos de una orden
	 * seleccionada.
	 * <p>
	 * Utiliza {@code OrdenCompraDetalleDAO} para recuperar los renglones y los
	 * muestra en una JTable de solo lectura.
	 * </p>
	 */
	private void verDetalles() {
		int id = vista.filaSelect();
		if (id == -1) {
			vista.mostrarError("Seleccione un pedido de la tabla para ver sus detalles.");
			return;
		}

		OrdenCompra orden = ((OrdenCompraDAO) this.modelo).buscarPorID(id);
		if (orden == null) {
			vista.mostrarError("No se encontró la orden.");
			return;
		}

		List<OrdenCompraDetalle> detalles = detalleDAO.buscarDetallesPorOrdenID(id);

		if (detalles.isEmpty()) {
			vista.mostrarMensaje("Este pedido no tiene productos asociados.");
			return;
		}

		// Construcción de la tabla temporal
		String[] columnas = { "ID Producto", "Producto", "Descripción", "Cantidad Pedida" };
		DefaultTableModel model = new DefaultTableModel(columnas, 0);

		for (OrdenCompraDetalle det : detalles) {
			model.addRow(new Object[] { det.getProductoId(), det.getNombreProducto(), det.getDescripcion(),
					det.getCantidadPedida() });
		}

		JTable tablaDetalles = new JTable(model);
		tablaDetalles.setEnabled(false);
		JScrollPane scrollPane = new JScrollPane(tablaDetalles);
		scrollPane.setPreferredSize(new Dimension(500, 250));

		JOptionPane.showMessageDialog(vista, scrollPane,
				"Detalles del Pedido #" + id + " (Proveedor: " + orden.getNombreProveedor() + ")",
				JOptionPane.INFORMATION_MESSAGE);
	}
}