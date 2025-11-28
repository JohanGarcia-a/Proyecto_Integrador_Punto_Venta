package vista;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Calendar;

/**
 * Panel de interfaz gráfica para consultar el historial de Cortes de Caja.
 * <p>
 * Este panel permite a los administradores filtrar los turnos pasados por Mes y
 * Año para visualizar su desempeño financiero (Monto Inicial vs Final,
 * Diferencias).
 * </p>
 * <p>
 * Incluye la funcionalidad para generar un reporte impreso (PDF) de los
 * resultados mostrados en la tabla.
 * </p>
 * 
 * @version 1.0
 */
public class PanelReporteCaja extends JPanel {

	private static final long serialVersionUID = 1L;

	/** Selector de Mes (Enero-Diciembre). */
	private JComboBox<String> comboMes;

	/** Selector numérico de Año. */
	private JSpinner spinnerAnio;

	/** Botón para ejecutar la consulta en la base de datos. */
	private JButton btnGenerarReporte;

	/** Botón para exportar los resultados a PDF (JasperReports). */
	private JButton btnImprimir;

	/** Tabla para visualizar el listado de cortes. */
	private JTable tablaResultados;

	/** Modelo de datos de la tabla. */
	private DefaultTableModel modeloTabla;

	/**
	 * Constructor.
	 * <p>
	 * Configura la interfaz visual dividida en:
	 * <ul>
	 * <li><b>Norte:</b> Panel de filtros (Mes, Año) y botones de acción.</li>
	 * <li><b>Centro:</b> Tabla de resultados con las columnas: [ID, Usuario,
	 * Apertura, Cierre, Inicial, Esperado, Real, Diferencia, Estado].</li>
	 * </ul>
	 * Inicializa el spinner de año con el año actual del sistema.
	 * </p>
	 */
	public PanelReporteCaja() {
		setLayout(new BorderLayout(10, 10));
		setBorder(new TitledBorder("Historial de Cortes de Caja"));

		// --- Panel superior: Filtros ---
		JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));

		String[] meses = { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre",
				"Octubre", "Noviembre", "Diciembre" };
		comboMes = new JComboBox<>(meses);

		// Configuración del Spinner de Año (Rango: 2020 a Año Actual + 10)
		int anioActual = Calendar.getInstance().get(Calendar.YEAR);
		spinnerAnio = new JSpinner(new SpinnerNumberModel(anioActual, 2020, anioActual + 10, 1));
		spinnerAnio.setEditor(new JSpinner.NumberEditor(spinnerAnio, "#")); // Formato sin comas (ej. 2025)

		btnGenerarReporte = new JButton("Consultar Cortes");
		btnImprimir = new JButton("Imprimir PDF");
		btnImprimir.setEnabled(false); // Deshabilitado hasta que haya datos

		panelFiltros.add(new JLabel("Mes:"));
		panelFiltros.add(comboMes);
		panelFiltros.add(new JLabel("Año:"));
		panelFiltros.add(spinnerAnio);
		panelFiltros.add(btnGenerarReporte);
		panelFiltros.add(btnImprimir);

		add(panelFiltros, BorderLayout.NORTH);

		// --- Tabla central ---
		String[] columnas = { "ID", "Usuario", "Apertura", "Cierre", "Inicial", "Esperado", "Real", "Diferencia",
				"Estado" };
		modeloTabla = new DefaultTableModel(columnas, 0) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Tabla de solo lectura
			}
		};
		tablaResultados = new JTable(modeloTabla);
		add(new JScrollPane(tablaResultados), BorderLayout.CENTER);
	}

	/**
	 * Actualiza la tabla con los datos provenientes del controlador.
	 * <p>
	 * Además, controla el estado del botón "Imprimir PDF": se habilita solo si la
	 * consulta arrojó al menos un resultado.
	 * </p>
	 * 
	 * @param model El modelo de tabla con los datos filtrados.
	 */
	public void mostrarResultados(TableModel model) {
		tablaResultados.setModel(model);
		// Habilita el botón de impresión solo si hay filas en la tabla
		btnImprimir.setEnabled(model.getRowCount() > 0);
	}

	/**
	 * Obtiene el índice del mes seleccionado.
	 * 
	 * @return Entero de 0 (Enero) a 11 (Diciembre).
	 */
	public int getMesSeleccionado() {
		return comboMes.getSelectedIndex();
	}

	/**
	 * Obtiene el año seleccionado en el spinner.
	 * 
	 * @return El año como entero (ej. 2025).
	 */
	public int getAnioSeleccionado() {
		return (Integer) spinnerAnio.getValue();
	}

	/**
	 * Asigna el comportamiento al botón de "Consultar Cortes".
	 * 
	 * @param l Listener del controlador.
	 */
	public void addGenerarListener(ActionListener l) {
		btnGenerarReporte.addActionListener(l);
	}

	/**
	 * Asigna el comportamiento al botón de "Imprimir PDF".
	 * 
	 * @param l Listener del controlador.
	 */
	public void addImprimirListener(ActionListener l) {
		btnImprimir.addActionListener(l);
	}
}