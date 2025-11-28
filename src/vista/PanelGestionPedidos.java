package vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import modelo.Proveedor;
import javax.swing.ImageIcon;

/**
 * Panel de interfaz gráfica para la administración general de Órdenes de
 * Compra.
 * <p>
 * Hereda de {@link VistaGenerica} para aprovechar la funcionalidad de la tabla
 * de listado y la barra de búsqueda, pero modifica el comportamiento estándar:
 * <ul>
 * <li><b>Oculta</b> los botones CRUD genéricos (Guardar, Actualizar) porque la
 * creación de pedidos es un proceso complejo.</li>
 * <li><b>Agrega</b> botones de acción específicos: "Crear Nuevo", "Recibir
 * Pedido" y "Ver Detalles".</li>
 * <li>Reemplaza el panel de campos de formulario por una barra de herramientas
 * de acciones.</li>
 * </ul>
 * </p>
 * 
 * @version 1.2
 */
public class PanelGestionPedidos extends VistaGenerica {

	// Botones de acción específicos para el flujo de compras

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Botón para procesar la recepción de mercancía (Entrada al almacén). */
	private JButton btnRecibirPedido;

	/** Botón que abre el sub-panel para registrar un nuevo pedido. */
	private JButton btnCrearNuevoPedido;

	/**
	 * Botón para visualizar los productos específicos de una orden seleccionada.
	 */
	private JButton btnVerDetalles;

	/**
	 * Constructor.
	 * <p>
	 * Configura el título "Gestión de Pedidos" y las columnas de la tabla. Además,
	 * oculta los botones {@code Bguardar} y {@code Bactualizar} heredados, ya que
	 * no se utilizan en esta vista de listado.
	 * </p>
	 */
	public PanelGestionPedidos() {
		super("Gestión de Pedidos", new String[] { "ID Pedido", "Proveedor", "Fecha", "Status" });

		// Ocultamos los botones genéricos que no aplican aquí
		Bguardar.setVisible(false);
		Bactualizar.setVisible(false);
	}

	/**
	 * Sobrescribe la creación del panel de campos.
	 * <p>
	 * En lugar de devolver un formulario con cajas de texto (como en Clientes),
	 * devuelve un panel que contiene los botones de acción específicos (Crear,
	 * Recibir, Ver).
	 * </p>
	 * 
	 * @return Panel contenedor con los botones de gestión.
	 */
	@Override
	protected JPanel crearPanelCampos() {
		JPanel panelFormularioVacio = new JPanel(new BorderLayout());
		JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panelAcciones.setPreferredSize(new Dimension(0, 150));

		// Botón para "Crear"
		btnCrearNuevoPedido = new JButton("Crear Nuevo Pedido");
		btnCrearNuevoPedido.setIcon(new ImageIcon(PanelGestionPedidos.class.getResource("/Iconos/guardar.png")));
		panelAcciones.add(btnCrearNuevoPedido);

		// Botón para "Recibir"
		btnRecibirPedido = new JButton("Recibir Pedido Seleccionado");
		btnRecibirPedido.setIcon(new ImageIcon(PanelGestionPedidos.class.getResource("/Iconos/actualizar.png")));
		panelAcciones.add(btnRecibirPedido);

		// Botón para "Ver Detalles"
		btnVerDetalles = new JButton("Ver Detalles");
		btnVerDetalles.setIcon(new ImageIcon(PanelGestionPedidos.class.getResource("/Iconos/agregar.png")));
		panelAcciones.add(btnVerDetalles);

		panelFormularioVacio.add(panelAcciones, BorderLayout.NORTH);
		return panelFormularioVacio;
	}

	// --- Métodos vacíos (obligatorios por la herencia abstracta) ---
	// Como esta vista no tiene campos de texto para editar directamente la orden,
	// estos métodos no realizan ninguna acción o retornan null.

	@Override
	public void limpiarCampos() {
		table.clearSelection();
	}

	@Override
	protected void cargarDatosFormulario() {
		// No hay formulario de texto que cargar al seleccionar una fila
	}

	@Override
	public Proveedor getDatosDelFormulario() {
		// No hay formulario que leer para construir un objeto
		return null;
	}

	// --- Métodos para asignar Listeners desde el Controlador ---

	public void addRecibirPedidoListener(ActionListener listener) {
		btnRecibirPedido.addActionListener(listener);
	}

	public void addCrearNuevoPedidoListener(ActionListener listener) {
		btnCrearNuevoPedido.addActionListener(listener);
	}

	public void addVerDetallesListener(ActionListener listener) {
		btnVerDetalles.addActionListener(listener);
	}
}