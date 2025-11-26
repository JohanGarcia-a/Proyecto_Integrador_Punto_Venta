package vista;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

public class PanelReportes extends JPanel {
	private JTabbedPane tabbedPane;
	private PanelReporteVentas panelVentas;
	private PanelReporteInventario panelInventario;
	private PanelHistorialVentas panelTickets;
	private PanelReporteCaja panelCaja;

	public PanelReportes() {
		setLayout(new BorderLayout());
		tabbedPane = new JTabbedPane();

		// Creamos el panel para la pestaña de "Ventas" y lo agregamos
		panelVentas = new PanelReporteVentas();
		tabbedPane.addTab("Reportes de Ventas", null, panelVentas, "Reportes relacionados con las ventas");

		// Creamos el panel para la pestaña de "Inventario" y lo agregamos
		panelInventario = new PanelReporteInventario();
		tabbedPane.addTab("Reportes de Inventario", null, panelInventario, "Reportes relacionados con el inventario");

		// Creamos el panel para la pestaña de "Inventario" y lo agregamos
		panelTickets = new PanelHistorialVentas();
		tabbedPane.addTab("Reportes de tickets", null, panelTickets, "Reportes relacionados con tickets");

		panelCaja = new PanelReporteCaja();
		tabbedPane.addTab("Historial de Caja", null, panelCaja, "Ver cortes de caja pasados");
		add(tabbedPane, BorderLayout.CENTER);
	}

	public PanelReporteVentas getPanelVentas() {
		return panelVentas;
	}

	public PanelReporteInventario getPanelInventario() {
		return panelInventario;
	}

	public PanelHistorialVentas getPanelTickets() {
		return panelTickets;
	}

	public PanelReporteCaja getPanelCaja() {
		return panelCaja;
	}
}