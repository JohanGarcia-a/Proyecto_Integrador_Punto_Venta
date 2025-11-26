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

public class PanelHistorialVentas extends JPanel {
	private JTable tablaVentas;
	private DefaultTableModel modeloTabla;
	private JButton btnReimprimir;

	public PanelHistorialVentas() {
		setLayout(new BorderLayout(10, 10));
		setBorder(new TitledBorder("Historial de Ventas"));

		// Tabla para mostrar las ventas
		String[] columnas = { "ID Venta", "Fecha", "Cliente", "Empleado", "Total" };
		modeloTabla = new DefaultTableModel(columnas, 0);
		tablaVentas = new JTable(modeloTabla);
		tablaVentas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Solo se puede seleccionar una fila

		add(new JScrollPane(tablaVentas), BorderLayout.CENTER);

		// Panel con el botón de acción
		JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btnReimprimir = new JButton("Reimprimir Ticket Seleccionado");
		panelBotones.add(btnReimprimir);

		add(panelBotones, BorderLayout.SOUTH);
	}

	public void mostrarVentas(List<Venta> ventas) {
		modeloTabla.setRowCount(0); // Limpiar tabla
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		for (Venta venta : ventas) {
			Object[] fila = { venta.getid(), sdf.format(venta.getFecha()), venta.getNombreCliente(),
					venta.getNombreEmpleado(), String.format("%.2f", venta.getTotal()) };
			modeloTabla.addRow(fila);
		}
	}

	public int getIdVentaSeleccionada() {
		int filaSeleccionada = tablaVentas.getSelectedRow();
		if (filaSeleccionada == -1) {
			return -1; // No hay ninguna fila seleccionada
		}
		return (int) modeloTabla.getValueAt(filaSeleccionada, 0);
	}

	public void addReimprimirListener(ActionListener listener) {
		btnReimprimir.addActionListener(listener);
	}

	public void mostrarMensaje(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
	}

	public void mostrarError(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
	}
}