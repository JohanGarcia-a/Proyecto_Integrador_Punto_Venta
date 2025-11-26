package vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import modelo.Proveedor; // Asegúrate de que esta importación esté (aunque no la usemos directamente)
import javax.swing.ImageIcon;

// Esta SÍ hereda de Vista_generica
public class PanelGestionPedidos extends VistaGenerica {

	// 1. Los botones extra que no están en la clase genérica
	private JButton btnRecibirPedido;
	private JButton btnCrearNuevoPedido;
	private JButton btnVerDetalles; // <-- 1. AÑADIR EL NUEVO BOTÓN

	public PanelGestionPedidos() {
		super("Gestión de Pedidos", new String[] { "ID Pedido", "Proveedor", "Fecha", "Status" });

		Bguardar.setVisible(false);
		Bactualizar.setVisible(false);
	}

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

		// --- 2. AÑADIR EL NUEVO BOTÓN A LA VISTA ---
		btnVerDetalles = new JButton("Ver Detalles");
		btnVerDetalles.setIcon(new ImageIcon(PanelGestionPedidos.class.getResource("/Iconos/agregar.png")));
		panelAcciones.add(btnVerDetalles);
		// --- FIN DEL CAMBIO ---

		panelFormularioVacio.add(panelAcciones, BorderLayout.NORTH);
		return panelFormularioVacio;
	}

	// --- Métodos vacíos (obligatorios por la herencia) ---

	@Override
	public void limpiarCampos() {
		table.clearSelection();
	}

	@Override
	protected void cargarDatosFormulario() {
		// No hay formulario que cargar
	}

	@Override
	public Proveedor getDatosDelFormulario() {
		// No hay formulario que leer
		return null;
	}

	// --- Getters para que el Controlador acceda a los botones ---

	public void addRecibirPedidoListener(ActionListener listener) {
		btnRecibirPedido.addActionListener(listener);
	}

	public void addCrearNuevoPedidoListener(ActionListener listener) {
		btnCrearNuevoPedido.addActionListener(listener);
	}

	// --- 3. AÑADIR EL LISTENER PARA EL NUEVO BOTÓN ---
	public void addVerDetallesListener(ActionListener listener) {
		btnVerDetalles.addActionListener(listener);
	}
	// --- FIN DEL CAMBIO ---
}