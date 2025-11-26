package vista;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Calendar;

public class PanelReporteVentas extends JPanel {
	private JButton btnVentasHoy;
	private JComboBox<String> comboMes;
	private JSpinner spinnerAnio;
	private JButton btnGenerarVentasMesSeleccionado;
	private JButton btnVerImprimirJasper;
	private JButton btnExportarExcel;

	private JTable tablaResultados;
	private DefaultTableModel modeloTabla;
	private JLabel lblTotalVentas;
	private JComponent tnExportarExcel;

	public PanelReporteVentas() {
		setLayout(new BorderLayout(10, 10));
		setBorder(new TitledBorder("Generar Reporte de Ventas"));

		// Panel superior con los botones de acción
		JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
		btnVentasHoy = new JButton("Mostrar Ventas de Hoy"); // Cambiamos etiqueta
		panelBotones.add(btnVentasHoy);
		panelBotones.add(new JLabel(" | Reporte Mensual: "));
		String[] meses = { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre",
				"Octubre", "Noviembre", "Diciembre" };
		comboMes = new JComboBox<>(meses);
		panelBotones.add(comboMes);
		int anioActual = Calendar.getInstance().get(Calendar.YEAR);
		spinnerAnio = new JSpinner(new SpinnerNumberModel(anioActual, 1990, anioActual + 5, 1));
		spinnerAnio.setEditor(new JSpinner.NumberEditor(spinnerAnio, "#"));
		panelBotones.add(spinnerAnio);
		btnGenerarVentasMesSeleccionado = new JButton("Mostrar Ventas del Mes"); // Cambiamos etiqueta
		panelBotones.add(btnGenerarVentasMesSeleccionado);

		// --- 2. AÑADE EL BOTÓN DE JASPER ---
		btnVerImprimirJasper = new JButton("Ver/Imprimir Reporte (Día/Mes)");
		btnVerImprimirJasper.setEnabled(false); // Se activará después de generar un reporte en tabla
		panelBotones.add(btnVerImprimirJasper);

		btnExportarExcel = new JButton("Exportar a Excel");
		btnExportarExcel.setEnabled(false); // Desactivado hasta que haya datos
		panelBotones.add(btnExportarExcel);

		add(panelBotones, BorderLayout.NORTH);

		// Tabla y Panel Sur (sin cambios)
		modeloTabla = new DefaultTableModel();
		tablaResultados = new JTable(modeloTabla);
		add(new JScrollPane(tablaResultados), BorderLayout.CENTER);
		JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		lblTotalVentas = new JLabel("Total de Ventas: $0.00");
		lblTotalVentas.setFont(new Font("Tahoma", Font.BOLD, 16));
		panelSur.add(lblTotalVentas);
		add(panelSur, BorderLayout.SOUTH);
	}

	public void mostrarResultados(TableModel tableModel) {
		tablaResultados.setModel(tableModel);
		boolean hayDatos = tableModel.getRowCount() > 0;
		btnVerImprimirJasper.setEnabled(hayDatos);
		btnExportarExcel.setEnabled(hayDatos); // <--- 3. ACTIVAR SI HAY DATOS
	}

	public void actualizarTotal(double total) {
		lblTotalVentas.setText(String.format("Total de Ventas: $%.2f", total));
	}

	public int getMesSeleccionado() {
		return comboMes.getSelectedIndex();
	}

	public int getAnioSeleccionado() {
		return (Integer) spinnerAnio.getValue();
	}

	public void addVentasHoyListener(ActionListener listener) {
		btnVentasHoy.addActionListener(listener);
	}

	public void addVentasMesSeleccionadoListener(ActionListener listener) {
		btnGenerarVentasMesSeleccionado.addActionListener(listener);
	}

	// --- 3. AÑADE LISTENER PARA EL NUEVO BOTÓN ---
	public void addVerImprimirJasperListener(ActionListener listener) {
		btnVerImprimirJasper.addActionListener(listener);
	}

	public JComboBox<String> getComboMes() {
		return comboMes;
	}

	public void setComboMes(JComboBox<String> comboMes) {
		this.comboMes = comboMes;
	}

	public void addExportarExcelListener(ActionListener listener) {
		btnExportarExcel.addActionListener(listener);
	}

	// <--- 5. IMPORTANTE: GETTER PARA LA TABLA (El controlador lo necesita)
	public JTable getTablaResultados() {
		return tablaResultados;
	}
}