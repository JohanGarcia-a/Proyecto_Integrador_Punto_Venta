package vista;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import modelo.MovimientoCaja;

public class PanelMovimientosCaja extends VistaGenerica {

	private static final long serialVersionUID = 1L;

	private JComboBox<String> comboTipo;
	private JTextField txtMonto;
	private JTextField txtDescripcion;

	public PanelMovimientosCaja() {
		// 1. Configurar título y columnas de la tabla
		super("Movimientos de Caja", new String[] { "ID", "Fecha", "Usuario", "Tipo", "Monto", "Descripción" });

		// 2. Desactivar el botón "Actualizar" (Por seguridad financiera no se editan)
		Bactualizar.setVisible(false);
		Bactualizar.setEnabled(false);
	}

	@Override
	protected JPanel crearPanelCampos() {
		JPanel panel = new JPanel(new GridBagLayout());

		// --- Fila 0: Tipo de Movimiento ---
		JLabel lblTipo = new JLabel("Tipo Movimiento:");
		GridBagConstraints gbc_lblTipo = new GridBagConstraints();
		gbc_lblTipo.anchor = GridBagConstraints.EAST;
		gbc_lblTipo.insets = new Insets(5, 5, 5, 5);
		gbc_lblTipo.gridx = 0;
		gbc_lblTipo.gridy = 0;
		panel.add(lblTipo, gbc_lblTipo);

		comboTipo = new JComboBox<>();
		comboTipo.setModel(new DefaultComboBoxModel<>(new String[] { "Ingreso", "Egreso" }));
		GridBagConstraints gbc_comboTipo = new GridBagConstraints();
		gbc_comboTipo.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboTipo.weightx = 1.0;
		gbc_comboTipo.insets = new Insets(5, 5, 5, 5);
		gbc_comboTipo.gridx = 1;
		gbc_comboTipo.gridy = 0;
		panel.add(comboTipo, gbc_comboTipo);

		// --- Fila 1: Monto ---
		JLabel lblMonto = new JLabel("Monto ($):");
		GridBagConstraints gbc_lblMonto = new GridBagConstraints();
		gbc_lblMonto.anchor = GridBagConstraints.EAST;
		gbc_lblMonto.insets = new Insets(5, 5, 5, 5);
		gbc_lblMonto.gridx = 0;
		gbc_lblMonto.gridy = 1;
		panel.add(lblMonto, gbc_lblMonto);

		txtMonto = new JTextField(10);
		GridBagConstraints gbc_txtMonto = new GridBagConstraints();
		gbc_txtMonto.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMonto.weightx = 1.0;
		gbc_txtMonto.insets = new Insets(5, 5, 5, 5);
		gbc_txtMonto.gridx = 1;
		gbc_txtMonto.gridy = 1;
		panel.add(txtMonto, gbc_txtMonto);

		// --- Fila 2: Descripción ---
		JLabel lblDescripcion = new JLabel("Descripción:");
		GridBagConstraints gbc_lblDescripcion = new GridBagConstraints();
		gbc_lblDescripcion.anchor = GridBagConstraints.EAST;
		gbc_lblDescripcion.insets = new Insets(5, 5, 5, 5);
		gbc_lblDescripcion.gridx = 0;
		gbc_lblDescripcion.gridy = 2;
		panel.add(lblDescripcion, gbc_lblDescripcion);

		txtDescripcion = new JTextField(20);
		GridBagConstraints gbc_txtDescripcion = new GridBagConstraints();
		gbc_txtDescripcion.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtDescripcion.weightx = 1.0;
		gbc_txtDescripcion.insets = new Insets(5, 5, 5, 5);
		gbc_txtDescripcion.gridx = 1;
		gbc_txtDescripcion.gridy = 2;
		panel.add(txtDescripcion, gbc_txtDescripcion);

		// --- Fila 3: Botón Limpiar ---
		JButton btnLimpiar = new JButton("Limpiar");
		GridBagConstraints gbc_btnLimpiar = new GridBagConstraints();
		gbc_btnLimpiar.anchor = GridBagConstraints.EAST;
		gbc_btnLimpiar.insets = new Insets(10, 5, 5, 5);
		gbc_btnLimpiar.gridx = 1;
		gbc_btnLimpiar.gridy = 3;
		panel.add(btnLimpiar, gbc_btnLimpiar);

		btnLimpiar.addActionListener(e -> limpiarCampos());

		return panel;
	}

	@Override
	public void limpiarCampos() {
		Tbuscar.setText("");
		if (comboTipo.getItemCount() > 0)
			comboTipo.setSelectedIndex(0);
		txtMonto.setText("");
		txtDescripcion.setText("");
		table.clearSelection();
		// Reactivar el botón guardar si estaba desactivado
		Bguardar.setEnabled(true);
	}

	@Override
	protected void cargarDatosFormulario() {
		int fila = table.getSelectedRow();
		if (fila != -1) {
			// Obtenemos los datos de la tabla (columnas 3, 4 y 5)
			String tipo = modeloTabla.getValueAt(fila, 3).toString();
			String monto = modeloTabla.getValueAt(fila, 4).toString();
			String descripcion = modeloTabla.getValueAt(fila, 5).toString();

			comboTipo.setSelectedItem(tipo);
			txtMonto.setText(monto);
			txtDescripcion.setText(descripcion);

			// Como no permitimos actualizar, al seleccionar solo permitimos borrar
			Bguardar.setEnabled(false);
		}
	}

	@Override
	public MovimientoCaja getDatosDelFormulario() {
		String tipo = (String) comboTipo.getSelectedItem();
		String descripcion = txtDescripcion.getText().trim();
		String textoMonto = txtMonto.getText().trim();

		if (descripcion.isEmpty()) {
			mostrarError("Debe ingresar una descripción (ej. 'Pago de Luz').");
			return null;
		}

		double monto = 0;
		try {
			monto = Double.parseDouble(textoMonto);
			if (monto <= 0) {
				mostrarError("El monto debe ser mayor a 0.");
				return null;
			}
		} catch (NumberFormatException e) {
			mostrarError("Ingrese un monto válido.");
			return null;
		}


		return new MovimientoCaja(0, -1, tipo, monto, descripcion);
	}
}