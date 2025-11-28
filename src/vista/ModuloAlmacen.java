package vista;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * Contenedor gráfico principal para el módulo de Almacén.
 * <p>
 * Esta vista no contiene lógica de negocio directa, sino que actúa como un
 * contenedor organizativo que utiliza un {@link JTabbedPane} para agrupar dos
 * sub-módulos relacionados:
 * <ul>
 * <li><b>Gestión de Productos:</b> CRUD de inventario
 * ({@link PanelAlmacenProductos}).</li>
 * <li><b>Pedidos a Proveedor:</b> Gestión de órdenes de compra
 * ({@link PanelGestionPedidos}).</li>
 * </ul>
 * </p>
 * <p>
 * Los controladores acceden a los paneles internos a través de los getters
 * públicos para asignar sus respectivos comportamientos.
 * </p>
 * 
 * @version 1.0
 */
public class ModuloAlmacen extends JPanel {

	private static final long serialVersionUID = 1L;

	/** Componente de pestañas para la navegación interna. */
	private JTabbedPane tabbedPane;

	// 1. Declaramos los dos paneles que irán DENTRO de las pestañas

	/** Sub-panel para la administración de productos (Inventario físico). */
	private PanelAlmacenProductos panelProductos;

	/** Sub-panel para la administración de órdenes de compra (Pedidos). */
	private PanelGestionPedidos panelPedidos;

	/**
	 * Constructor.
	 * <p>
	 * Configura el diseño (Layout), instancia los sub-paneles y los agrega al
	 * contenedor de pestañas con sus respectivos títulos y descripciones
	 * (tooltips).
	 * </p>
	 */
	public ModuloAlmacen() {
		setLayout(new BorderLayout());
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		// 2. Creamos una instancia de tu panel de productos existente
		panelProductos = new PanelAlmacenProductos();

		// 3. Creamos una instancia del panel de GESTIÓN (la lista)
		panelPedidos = new PanelGestionPedidos();

		// 4. Añadimos los paneles como pestañas
		tabbedPane.addTab("Gestión de Productos", null, panelProductos, "Administrar el inventario de productos");

		tabbedPane.addTab("Pedidos a Proveedor", null, panelPedidos, "Crear y recibir pedidos de proveedores");

		add(tabbedPane, BorderLayout.CENTER);
	}

	// --- Getters ---

	/**
	 * Obtiene la instancia del panel de productos.
	 * <p>
	 * Utilizado por el {@code ControladorAlmacen} para conectar la lógica con la
	 * vista.
	 * </p>
	 * 
	 * @return El panel de inventario.
	 */
	public PanelAlmacenProductos getPanelProductos() {
		return panelProductos;
	}

	/**
	 * Obtiene la instancia del panel de gestión de pedidos.
	 * <p>
	 * Utilizado por el {@code ControladorGestionPedidos} para conectar la lógica
	 * con la vista.
	 * </p>
	 * 
	 * @return El panel de lista de pedidos.
	 */
	public PanelGestionPedidos getPanelPedidos() {
		return panelPedidos;
	}
}