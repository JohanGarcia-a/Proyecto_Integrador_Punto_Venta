package vista;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

/**
 * Contenedor gráfico principal para el módulo de Reportes y Estadísticas.
 * <p>
 * Esta clase actúa como un <b>Dashboard</b> que centraliza las diferentes
 * vistas de reportes (Ventas, Inventario, Tickets y Caja) utilizando un
 * {@link JTabbedPane} para la navegación.
 * </p>
 * <p>
 * <b>Nota de Arquitectura:</b> Esta clase no contiene la lógica de los reportes
 * ni las tablas; su responsabilidad es instanciar los sub-paneles específicos y
 * proveer métodos de acceso (Getters) para que el {@code ControladorReportes}
 * pueda conectar la lógica a cada pestaña.
 * </p>
 * 
 * @version 1.0
 */
public class PanelReportes extends JPanel {

	private static final long serialVersionUID = 1L;

	/** Componente de pestañas para organizar las vistas. */
	private JTabbedPane tabbedPane;

	/** Sub-panel para filtros y tablas de ventas. */
	private PanelReporteVentas panelVentas;

	/** Sub-panel para alertas de stock y movimientos de inventario. */
	private PanelReporteInventario panelInventario;

	/** Sub-panel para reimpresión y devolución de tickets. */
	private PanelHistorialVentas panelTickets;

	/** Sub-panel para auditoría de cortes de caja pasados. */
	private PanelReporteCaja panelCaja;

	/**
	 * Constructor.
	 * <p>
	 * Inicializa el diseño (BorderLayout), crea las instancias de los 4 sub-paneles
	 * y los agrega como pestañas con títulos descriptivos y tooltips.
	 * </p>
	 */
	public PanelReportes() {
		setLayout(new BorderLayout());
		tabbedPane = new JTabbedPane();

		// 1. Pestaña de Ventas (Reportes financieros diarios/mensuales)
		panelVentas = new PanelReporteVentas();
		tabbedPane.addTab("Reportes de Ventas", null, panelVentas, "Reportes relacionados con las ventas");

		// 2. Pestaña de Inventario (Stock bajo, valoración)
		panelInventario = new PanelReporteInventario();
		tabbedPane.addTab("Reportes de Inventario", null, panelInventario, "Reportes relacionados con el inventario");

		// 3. Pestaña de Tickets (Historial transaccional)
		panelTickets = new PanelHistorialVentas();
		tabbedPane.addTab("Reportes de tickets", null, panelTickets, "Reportes relacionados con tickets");

		// 4. Pestaña de Caja (Historial de turnos)
		panelCaja = new PanelReporteCaja();
		tabbedPane.addTab("Historial de Caja", null, panelCaja, "Ver cortes de caja pasados");

		add(tabbedPane, BorderLayout.CENTER);
	}

	// --- Getters para la Inyección de Dependencias en el Controlador ---

	/**
	 * Obtiene la referencia al sub-panel de reportes de ventas.
	 * 
	 * @return Instancia de PanelReporteVentas.
	 */
	public PanelReporteVentas getPanelVentas() {
		return panelVentas;
	}

	/**
	 * Obtiene la referencia al sub-panel de reportes de inventario.
	 * 
	 * @return Instancia de PanelReporteInventario.
	 */
	public PanelReporteInventario getPanelInventario() {
		return panelInventario;
	}

	/**
	 * Obtiene la referencia al sub-panel de historial de tickets.
	 * 
	 * @return Instancia de PanelHistorialVentas.
	 */
	public PanelHistorialVentas getPanelTickets() {
		return panelTickets;
	}

	/**
	 * Obtiene la referencia al sub-panel de historial de caja.
	 * 
	 * @return Instancia de PanelReporteCaja.
	 */
	public PanelReporteCaja getPanelCaja() {
		return panelCaja;
	}
}