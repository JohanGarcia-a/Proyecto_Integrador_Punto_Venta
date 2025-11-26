package vista;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Calendar;

public class PanelReporteCaja extends JPanel {
	private JComboBox<String> comboMes;
	private JSpinner spinnerAnio;
	private JButton btnGenerarReporte;
	private JButton btnImprimir;

	private JTable tablaResultados;
	private DefaultTableModel modeloTabla;

	public PanelReporteCaja() {
		setLayout(new BorderLayout(10, 10));
		setBorder(new TitledBorder("Historial de Cortes de Caja"));

		// Panel superior: Filtros
		JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));

		String[] meses = { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre",
				"Octubre", "Noviembre", "Diciembre" };
		comboMes = new JComboBox<>(meses);

		int anioActual = Calendar.getInstance().get(Calendar.YEAR);
		spinnerAnio = new JSpinner(new SpinnerNumberModel(anioActual, 2020, anioActual + 10, 1));
		spinnerAnio.setEditor(new JSpinner.NumberEditor(spinnerAnio, "#"));

		btnGenerarReporte = new JButton("Consultar Cortes");
		btnImprimir = new JButton("Imprimir PDF");
		btnImprimir.setEnabled(false);

		panelFiltros.add(new JLabel("Mes:"));
		panelFiltros.add(comboMes);
		panelFiltros.add(new JLabel("Año:"));
		panelFiltros.add(spinnerAnio);
		panelFiltros.add(btnGenerarReporte);
		panelFiltros.add(btnImprimir);

		add(panelFiltros, BorderLayout.NORTH);

		// Tabla central
		String[] columnas = { "ID", "Usuario", "Apertura", "Cierre", "Inicial", "Esperado", "Real", "Diferencia",
				"Estado" };
		modeloTabla = new DefaultTableModel(columnas, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tablaResultados = new JTable(modeloTabla);
		add(new JScrollPane(tablaResultados), BorderLayout.CENTER);
	}

	public void mostrarResultados(TableModel model) {
		tablaResultados.setModel(model);
		btnImprimir.setEnabled(model.getRowCount() > 0);
	}

	public int getMesSeleccionado() {
		return comboMes.getSelectedIndex();
	}

	public int getAnioSeleccionado() {
		return (Integer) spinnerAnio.getValue();
	}

	public void addGenerarListener(ActionListener l) {
		btnGenerarReporte.addActionListener(l);
	}

	public void addImprimirListener(ActionListener l) {
		btnImprimir.addActionListener(l);
	}
}