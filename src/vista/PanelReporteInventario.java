package vista;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Panel de interfaz gráfica para la generación de reportes relacionados con el
 * Almacén.
 * <p>
 * Proporciona acceso rápido a tres tipos de consultas clave:
 * <ul>
 * <li><b>Stock Bajo:</b> Productos que necesitan reabastecimiento urgente.</li>
 * <li><b>Inventario Completo:</b> Valoración total de la mercancía.</li>
 * <li><b>Historial de Entradas:</b> Auditoría de quién agregó stock y
 * cuándo.</li>
 * </ul>
 * </p>
 * <p>
 * Incluye un botón inteligente para exportar a PDF (JasperReports) que solo se
 * habilita cuando hay datos visibles en la tabla.
 * </p>
 * 
 * @version 1.1
 */
public class PanelReporteInventario extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Botón para consultar productos con stock menor al mínimo permitido. */
	private JButton btnStockBajo;

	/** Botón para listar todo el catálogo actual. */
	private JButton btnMostrarInventarioCompleto;

	/** Botón para ver la bitácora de ingresos de mercancía. */
	private JButton btnMostrarHistorialEntradas;

	/** Botón para generar el documento PDF basado en la consulta actual. */
	private JButton btnVerImprimirInventarioJasper;

	/** Tabla para visualizar los resultados de cualquiera de las 3 consultas. */
	private JTable tablaResultados;

	/** Modelo de datos para la tabla. */
	private DefaultTableModel modeloTabla;

	/**
	 * Constructor.
	 * <p>
	 * Configura el diseño del panel, inicializa los botones de acción y prepara la
	 * tabla de resultados en modo de "solo lectura" (no editable).
	 * </p>
	 */
	public PanelReporteInventario() {
		setLayout(new BorderLayout(10, 10));
		setBorder(new TitledBorder("Generar Reporte de Inventario"));

		// Panel superior con los botones de acción (FlowLayout para alineación
		// izquierda)
		JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));

		btnStockBajo = new JButton("Mostrar Stock Bajo");
		btnMostrarInventarioCompleto = new JButton("Mostrar Inventario Completo");
		btnMostrarHistorialEntradas = new JButton("Mostrar Historial de Entradas");

		panelBotones.add(btnStockBajo);
		panelBotones.add(btnMostrarInventarioCompleto);
		panelBotones.add(btnMostrarHistorialEntradas);

		// Configuración del botón Jasper
		btnVerImprimirInventarioJasper = new JButton("Ver/Imprimir Reporte");
		btnVerImprimirInventarioJasper.setEnabled(false); // Deshabilitado por defecto hasta que haya datos
		panelBotones.add(btnVerImprimirInventarioJasper);

		add(panelBotones, BorderLayout.NORTH);

		// Configuración de la Tabla
		modeloTabla = new DefaultTableModel();
		tablaResultados = new JTable(modeloTabla);

		// Truco para evitar la edición de celdas por parte del usuario
		tablaResultados.setDefaultEditor(Object.class, null);

		add(new JScrollPane(tablaResultados), BorderLayout.CENTER);
	}

	/**
	 * Actualiza la tabla con los datos provenientes del controlador.
	 * <p>
	 * Además, controla el estado del botón de impresión: Si la tabla recibe datos,
	 * el botón se habilita; si la tabla se vacía, el botón se deshabilita.
	 * </p>
	 * 
	 * @param tableModel El modelo de tabla con los nuevos datos (columnas y filas).
	 */
	public void mostrarResultados(TableModel tableModel) {
		tablaResultados.setModel(tableModel);
		// Lógica de UI: Habilita el botón Jasper solo si la tabla tiene al menos una
		// fila
		btnVerImprimirInventarioJasper.setEnabled(tableModel.getRowCount() > 0);
	}

	// --- Métodos para delegar la gestión de eventos al Controlador ---

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

	// --- Getters ---

	public JTable getTablaResultados() {
		return tablaResultados;
	}
}