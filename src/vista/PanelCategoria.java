package vista;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import modelo.Categorias;


public class PanelCategoria extends VistaGenerica {

	private JTextField Tnombre;

	public PanelCategoria() {
		super("Categorías", new String[] { "Cid", "Nombre de Categoria", "Cantidad de productos" });
	}

	@Override
	protected JPanel crearPanelCampos() {
		JPanel Panel = new JPanel();
		Panel.setLayout(new GridBagLayout());

		// Fila 0: Nombre
		JLabel Lnombre = new JLabel("Nombre:");
		GridBagConstraints gbc_Lnombre = new GridBagConstraints();
		gbc_Lnombre.insets = new Insets(5, 5, 5, 5);
		gbc_Lnombre.gridx = 0;
		gbc_Lnombre.gridy = 0;
		gbc_Lnombre.anchor = GridBagConstraints.EAST;
		Panel.add(Lnombre, gbc_Lnombre);

		Tnombre = new JTextField(20);
		GridBagConstraints gbc_Tnombre = new GridBagConstraints();
		gbc_Tnombre.insets = new Insets(5, 5, 5, 5);
		gbc_Tnombre.gridx = 1;
		gbc_Tnombre.gridy = 0;
		gbc_Tnombre.anchor = GridBagConstraints.WEST;
		gbc_Tnombre.weightx = 1.0;
		gbc_Tnombre.fill = GridBagConstraints.HORIZONTAL;
		Panel.add(Tnombre, gbc_Tnombre);

		// Fila 1: Botón Limpiar
		JButton Blimpiar = new JButton("Limpiar");
		GridBagConstraints gbc_Blimpiar = new GridBagConstraints();
		gbc_Blimpiar.insets = new Insets(5, 5, 5, 5);
		gbc_Blimpiar.gridx = 1;
		gbc_Blimpiar.gridy = 1;
		gbc_Blimpiar.anchor = GridBagConstraints.EAST;
		gbc_Blimpiar.fill = GridBagConstraints.NONE;
		Panel.add(Blimpiar, gbc_Blimpiar);
		Blimpiar.addActionListener(e -> limpiarCampos());

		return Panel;
	}

	@Override
	public void limpiarCampos() {
		Tnombre.setText("");
		Tbuscar.setText("");
		table.clearSelection();
		Bguardar.setEnabled(true);
		Bactualizar.setEnabled(false);
	}

	@Override
	protected void cargarDatosFormulario() {
		int filaSeleccionada = table.getSelectedRow();
		if (filaSeleccionada != -1) {
			String id = modeloTabla.getValueAt(filaSeleccionada, 0).toString();
			String nombre = modeloTabla.getValueAt(filaSeleccionada, 1).toString();

			Tbuscar.setText(id);
			Tnombre.setText(nombre);

			Bguardar.setEnabled(false);
			Bactualizar.setEnabled(true);
		}
	}

	@Override
	public Categorias getDatosDelFormulario() {
		String nombre = Tnombre.getText().trim();
		if (nombre.isEmpty()) {
			mostrarError("El campo 'Nombre' no puede estar vacío.");
			return null;
		}
		int id = filaSelect();
		return new Categorias(id, nombre);
	}
}