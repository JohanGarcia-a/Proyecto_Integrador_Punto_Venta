package vista;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class ModuloAlmacen extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTabbedPane tabbedPane;

	// 1. Declaramos los dos paneles que irán DENTRO de las pestañas
	private PanelAlmacenProductos panelProductos;
	private PanelGestionPedidos panelPedidos; // <-- CAMBIO AQUÍ

	public ModuloAlmacen() {
		setLayout(new BorderLayout());
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		// 2. Creamos una instancia de tu panel de productos existente
		panelProductos = new PanelAlmacenProductos();

		// 3. Creamos una instancia del panel de GESTIÓN (la lista)
		panelPedidos = new PanelGestionPedidos(); // <-- CAMBIO AQUÍ

		// 4. Añadimos los paneles como pestañas
		tabbedPane.addTab("Gestión de Productos", null, panelProductos, "Administrar el inventario de productos");

		tabbedPane.addTab("Pedidos a Proveedor", null, panelPedidos, "Crear y recibir pedidos de proveedores");

		add(tabbedPane, BorderLayout.CENTER);
	}

	// --- Getters ---

	public PanelAlmacenProductos getPanelProductos() {
		return panelProductos;
	}

	// El getter ahora devuelve el panel de GESTIÓN
	public PanelGestionPedidos getPanelPedidos() { // <-- CAMBIO AQUÍ
		return panelPedidos;
	}
}