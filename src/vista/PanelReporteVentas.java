package vista;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Calendar;

/**
 * Panel de interfaz gráfica para la generación de reportes financieros de
 * Ventas.
 * <p>
 * Proporciona herramientas de filtrado temporal (Ventas del Día vs. Ventas
 * Mensuales) y múltiples formatos de salida para la información:
 * <ul>
 * <li><b>Visualización en Tabla:</b> Vista rápida de los datos.</li>
 * <li><b>Totalización:</b> Cálculo automático de la suma de ingresos.</li>
 * <li><b>Exportación:</b> Generación de documentos PDF (JasperReports) y Excel
 * (CSV).</li>
 * </ul>
 * </p>
 * 
 * @version 1.2
 */
public class PanelReporteVentas extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Botón para consultar las ventas del día actual (00:00 - 23:59). */
	private JButton btnVentasHoy;

	/** Selector de Mes para reportes históricos. */
	private JComboBox<String> comboMes;

	/** Selector numérico de Año. */
	private JSpinner spinnerAnio;

	/** Botón para ejecutar la consulta mensual. */
	private JButton btnGenerarVentasMesSeleccionado;

	/** Botón para generar el PDF con JasperReports. */
	private JButton btnVerImprimirJasper;

	/** Botón para exportar la tabla a formato CSV/Excel. */
	private JButton btnExportarExcel;

	private JTable tablaResultados;
	private DefaultTableModel modeloTabla;

	/** Etiqueta que muestra la suma total de las ventas listadas. */
	private JLabel lblTotalVentas;

	// Componente auxiliar interno
	private JComponent tnExportarExcel;

	/**
	 * Constructor.
	 * <p>
	 * Configura la interfaz dividida en:
	 * <ul>
	 * <li><b>Panel Norte (Filtros y Acciones):</b> Contiene los botones de consulta
	 * y exportación. Los botones de exportación inician deshabilitados.</li>
	 * <li><b>Panel Centro (Resultados):</b> Tabla JTable para mostrar el detalle de
	 * las ventas.</li>
	 * <li><b>Panel Sur (Totales):</b> Etiqueta con fuente grande para mostrar el
	 * ingreso total.</li>
	 * </ul>
	 * </p>
	 */
	public PanelReporteVentas() {
		setLayout(new BorderLayout(10, 10));
		setBorder(new TitledBorder("Generar Reporte de Ventas"));

		// --- Panel superior con los botones de acción y filtros ---
		JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));

		btnVentasHoy = new JButton("Mostrar Ventas de Hoy");
		panelBotones.add(btnVentasHoy);

		panelBotones.add(new JLabel(" | Reporte Mensual: "));

		String[] meses = { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre",
				"Octubre", "Noviembre", "Diciembre" };
		comboMes = new JComboBox<>(meses);
		panelBotones.add(comboMes);

		// Configuración del Spinner de Año
		int anioActual = Calendar.getInstance().get(Calendar.YEAR);
		spinnerAnio = new JSpinner(new SpinnerNumberModel(anioActual, 1990, anioActual + 5, 1));
		spinnerAnio.setEditor(new JSpinner.NumberEditor(spinnerAnio, "#"));
		panelBotones.add(spinnerAnio);

		btnGenerarVentasMesSeleccionado = new JButton("Mostrar Ventas del Mes");
		panelBotones.add(btnGenerarVentasMesSeleccionado);

		// --- Botones de Exportación (Inician desactivados) ---
		btnVerImprimirJasper = new JButton("Ver/Imprimir Reporte (Día/Mes)");
		btnVerImprimirJasper.setEnabled(false);
		panelBotones.add(btnVerImprimirJasper);

		btnExportarExcel = new JButton("Exportar a Excel");
		btnExportarExcel.setEnabled(false);
		panelBotones.add(btnExportarExcel);

		add(panelBotones, BorderLayout.NORTH);

		// --- Tabla y Panel Sur ---
		modeloTabla = new DefaultTableModel();
		tablaResultados = new JTable(modeloTabla);
		add(new JScrollPane(tablaResultados), BorderLayout.CENTER);

		JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		lblTotalVentas = new JLabel("Total de Ventas: $0.00");
		lblTotalVentas.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelSur.add(lblTotalVentas);
		add(panelSur, BorderLayout.SOUTH);
	}

	/**
	 * Actualiza la tabla con los datos obtenidos por el controlador.
	 * <p>
	 * <b>Lógica de UI:</b> Verifica si el modelo de datos contiene filas. Si hay
	 * datos, habilita los botones de "Imprimir Jasper" y "Exportar Excel". Si no,
	 * los deshabilita para prevenir errores.
	 * </p>
	 * 
	 * @param tableModel El modelo de tabla con los registros de ventas encontrados.
	 */
	public void mostrarResultados(TableModel tableModel) {
		tablaResultados.setModel(tableModel);
		boolean hayDatos = tableModel.getRowCount() > 0;

		// Gestión de estado de los botones de exportación
		btnVerImprimirJasper.setEnabled(hayDatos);
		btnExportarExcel.setEnabled(hayDatos);
	}

	/**
	 * Actualiza la etiqueta de totalización en la parte inferior.
	 * 
	 * @param total La suma monetaria de las ventas listadas.
	 */
	public void actualizarTotal(double total) {
		lblTotalVentas.setText(String.format("Total de Ventas: $%.2f", total));
	}

	// --- Getters para obtener los filtros seleccionados ---

	public int getMesSeleccionado() {
		return comboMes.getSelectedIndex();
	}

	public int getAnioSeleccionado() {
		return (Integer) spinnerAnio.getValue();
	}

	public JComboBox<String> getComboMes() {
		return comboMes;
	}

	public void setComboMes(JComboBox<String> comboMes) {
		this.comboMes = comboMes;
	}

	/**
	 * Obtiene la referencia a la tabla de resultados.
	 * <p>
	 * Utilizado por el controlador para leer los datos celda por celda al momento
	 * de exportar a Excel (CSV).
	 * </p>
	 * 
	 * @return La JTable con los datos visibles.
	 */
	public JTable getTablaResultados() {
		return tablaResultados;
	}

	// --- Listeners para delegar acciones al Controlador ---

	public void addVentasHoyListener(ActionListener listener) {
		btnVentasHoy.addActionListener(listener);
	}

	public void addVentasMesSeleccionadoListener(ActionListener listener) {
		btnGenerarVentasMesSeleccionado.addActionListener(listener);
	}

	public void addVerImprimirJasperListener(ActionListener listener) {
		btnVerImprimirJasper.addActionListener(listener);
	}

	public void addExportarExcelListener(ActionListener listener) {
		btnExportarExcel.addActionListener(listener);
	}
}