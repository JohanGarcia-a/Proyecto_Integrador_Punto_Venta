package vista;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import modelo.Venta;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Panel de interfaz gráfica que visualiza el registro histórico de
 * transacciones.
 * <p>
 * Este panel es gestionado por el {@code ControladorReportes} (pestaña
 * "Tickets"). Su función principal es listar todas las ventas realizadas y
 * ofrecer herramientas post-venta:
 * <ul>
 * <li><b>Reimpresión:</b> Generar nuevamente el ticket de una venta
 * pasada.</li>
 * <li><b>Devolución:</b> Cancelar una venta y retornar los productos al
 * inventario.</li>
 * </ul>
 * </p>
 * 
 * @version 1.1
 */
public class PanelHistorialVentas extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Tabla principal para mostrar los datos. */
	private JTable tablaVentas;

	/** Modelo de datos para manipular las filas de la tabla. */
	private DefaultTableModel modeloTabla;

	/** Botón para accionar la reimpresión del comprobante. */
	private JButton btnReimprimir;

	/** Botón para procesar la cancelación/devolución de la venta. */
	private JButton btnDevolucion;

	/**
	 * Constructor.
	 * <p>
	 * Configura el diseño visual, inicializa la tabla con las columnas [ID, Fecha,
	 * Cliente, Empleado, Total] y restringe la selección a una sola fila a la vez
	 * (SINGLE_SELECTION) para evitar ambigüedades al reimprimir o devolver.
	 * </p>
	 */
	public PanelHistorialVentas() {
		setLayout(new BorderLayout(10, 10));
		setBorder(new TitledBorder("Historial de Ventas"));

		// Configuración de la Tabla
		String[] columnas = { "ID Venta", "Fecha", "Cliente", "Empleado", "Total" };
		modeloTabla = new DefaultTableModel(columnas, 0);
		tablaVentas = new JTable(modeloTabla);

		// Restricción importante: Solo una venta a la vez
		tablaVentas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		add(new JScrollPane(tablaVentas), BorderLayout.CENTER);

		// Panel inferior con botones de acción
		JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		btnDevolucion = new JButton("Devolver Venta (Cancelar)");
		panelBotones.add(btnDevolucion);

		btnReimprimir = new JButton("Reimprimir Ticket Seleccionado");
		panelBotones.add(btnReimprimir);

		add(panelBotones, BorderLayout.SOUTH);
	}

	/**
	 * Actualiza el contenido de la tabla con una lista de ventas.
	 * <p>
	 * Limpia las filas existentes y formatea la fecha (dd/MM/yyyy HH:mm:ss) y el
	 * total monetario antes de agregarlos al modelo visual.
	 * </p>
	 * 
	 * @param ventas Lista de objetos {@link Venta} recuperados de la base de datos.
	 */
	public void mostrarVentas(List<Venta> ventas) {
		modeloTabla.setRowCount(0); // Limpiar tabla
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		for (Venta venta : ventas) {
			Object[] fila = { venta.getid(), sdf.format(venta.getFecha()), venta.getNombreCliente(),
					venta.getNombreEmpleado(), String.format("%.2f", venta.getTotal()) };
			modeloTabla.addRow(fila);
		}
	}

	/**
	 * Recupera el ID (Clave Primaria) de la venta que el usuario ha seleccionado en
	 * la tabla.
	 * <p>
	 * Utilizado por el controlador para saber sobre qué registro actuar (Reimprimir
	 * o Devolver).
	 * </p>
	 * 
	 * @return El ID de la venta seleccionada, o -1 si no hay selección.
	 */
	public int getIdVentaSeleccionada() {
		int filaSeleccionada = tablaVentas.getSelectedRow();
		if (filaSeleccionada == -1) {
			return -1; // No hay ninguna fila seleccionada
		}
		// Obtenemos el valor de la columna 0 (ID Venta)
		return (int) modeloTabla.getValueAt(filaSeleccionada, 0);
	}

	// --- Listeners para conectar con el Controlador ---

	public void addReimprimirListener(ActionListener listener) {
		btnReimprimir.addActionListener(listener);
	}

	public void addDevolucionListener(ActionListener listener) {
		btnDevolucion.addActionListener(listener);
	}

	// --- Métodos de retroalimentación al usuario ---

	public void mostrarMensaje(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
	}

	public void mostrarError(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
	}
}