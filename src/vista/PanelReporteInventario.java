package vista;

import javax.swing.*; 
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class PanelReporteInventario extends JPanel {
	private JButton btnStockBajo;
	private JButton btnMostrarInventarioCompleto;
	private JButton btnMostrarHistorialEntradas;
	private JButton btnVerImprimirInventarioJasper; // Botón para Jasper

	private JTable tablaResultados;
	private DefaultTableModel modeloTabla;

	public PanelReporteInventario() {
		setLayout(new BorderLayout(10, 10));
		setBorder(new TitledBorder("Generar Reporte de Inventario"));

		// Panel superior con los botones de acción
		JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
		btnStockBajo = new JButton("Mostrar Stock Bajo");
		btnMostrarInventarioCompleto = new JButton("Mostrar Inventario Completo");
		btnMostrarHistorialEntradas = new JButton("Mostrar Historial de Entradas");

		panelBotones.add(btnStockBajo);
		panelBotones.add(btnMostrarInventarioCompleto);
		panelBotones.add(btnMostrarHistorialEntradas);

		// Botón para generar el reporte Jasper
		btnVerImprimirInventarioJasper = new JButton("Ver/Imprimir Reporte");
		btnVerImprimirInventarioJasper.setEnabled(false); // Se activa al mostrar datos
		panelBotones.add(btnVerImprimirInventarioJasper);

		add(panelBotones, BorderLayout.NORTH);

		// Tabla para mostrar los resultados
		modeloTabla = new DefaultTableModel();
		tablaResultados = new JTable(modeloTabla);
		// Evitar edición directa en la tabla
		tablaResultados.setDefaultEditor(Object.class, null);
		add(new JScrollPane(tablaResultados), BorderLayout.CENTER);
	}

	/**
	 * Actualiza la tabla con los datos del reporte generado. Habilita o deshabilita
	 * el botón de Jasper según si hay datos.
	 * 
	 * @param tableModel El modelo de tabla con los datos.
	 */
	public void mostrarResultados(TableModel tableModel) {
		tablaResultados.setModel(tableModel);
		// Habilita el botón Jasper solo si la tabla tiene filas
		btnVerImprimirInventarioJasper.setEnabled(tableModel.getRowCount() > 0);
	}

	// --- Métodos para añadir Listeners a los botones ---

	public void addStockBajoListener(ActionListener listener) {
		btnStockBajo.addActionListener(listener);
	}

	public void addInventarioCompletoListener(ActionListener listener) {
		btnMostrarInventarioCompleto.addActionListener(listener);
	}

	public void addHistorialEntradasListener(ActionListener listener) {
		btnMostrarHistorialEntradas.addActionListener(listener);
	}

	public void addVerImprimirInventarioJasperListener(ActionListener listener) {
		btnVerImprimirInventarioJasper.addActionListener(listener);
	}

	// --- Getter opcional para la tabla (si se necesitara externamente) ---
	public JTable getTablaResultados() {
		return tablaResultados;
	}
}