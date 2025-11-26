package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import modelogenerico.Entidad;

public abstract class VistaGenerica extends JPanel {

	private static final long serialVersionUID = 1L;
	protected JTextField Tbuscar;
	protected DefaultTableModel modeloTabla;
	protected JTable table;
	protected JButton Bbuscar, Bguardar, Bactualizar, Bborrar;

	public VistaGenerica(String Entidad, String[] columnasTabla) {
		setLayout(new BorderLayout(0, 0));

		JPanel PanelInfo = new JPanel();

		PanelInfo.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		add(PanelInfo, BorderLayout.WEST);
		PanelInfo.setLayout(new BorderLayout(0, 0));
		// ---------------------------Busqueda----------------------------------------------
		JPanel panel_Buscar = new JPanel();
		panel_Buscar.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		PanelInfo.add(panel_Buscar, BorderLayout.NORTH);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel_Buscar.setLayout(gridBagLayout);

		panel_Buscar.setBorder(BorderFactory.createTitledBorder("BUSQUEDA"));
		JLabel Lbuscar = new JLabel("Buscar");
		GridBagConstraints gbc_Lbuscar = new GridBagConstraints();
		gbc_Lbuscar.insets = new Insets(0, 0, 5, 5);
		gbc_Lbuscar.gridx = 1;
		gbc_Lbuscar.gridy = 1;
		panel_Buscar.add(Lbuscar, gbc_Lbuscar);

		Tbuscar = new JTextField();
		GridBagConstraints gbc_tbuscar = new GridBagConstraints();
		gbc_tbuscar.insets = new Insets(0, 0, 5, 5);
		gbc_tbuscar.fill = GridBagConstraints.HORIZONTAL;
		gbc_tbuscar.gridx = 2;
		gbc_tbuscar.gridy = 1;
		panel_Buscar.add(Tbuscar, gbc_tbuscar);
		Tbuscar.setColumns(10);

		Bbuscar = new JButton("Buscar");
		Bbuscar.setIcon(new ImageIcon(VistaGenerica.class.getResource("/Iconos/buscar.png")));
		GridBagConstraints gbc_Bbuscar = new GridBagConstraints();
		gbc_Bbuscar.insets = new Insets(0, 0, 5, 0);
		gbc_Bbuscar.gridx = 3;
		gbc_Bbuscar.gridy = 1;
		panel_Buscar.add(Bbuscar, gbc_Bbuscar);
		// ------------------------------Campos----------------------------------------------
		JPanel Panel_campos = crearPanelCampos();
		Panel_campos.setBorder(BorderFactory.createTitledBorder("Informacion de " + Entidad));
		Panel_campos.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		PanelInfo.add(Panel_campos, BorderLayout.CENTER);

		JPanel panel_Botones = new JPanel();
		panel_Botones.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		PanelInfo.add(panel_Botones, BorderLayout.SOUTH);
		panel_Botones.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		Bguardar = new JButton("Guardar");
		Bguardar.setIcon(new ImageIcon(VistaGenerica.class.getResource("/Iconos/guardar.png")));
		panel_Botones.add(Bguardar);

		Bactualizar = new JButton("Actualizar");
		Bactualizar.setIcon(new ImageIcon(VistaGenerica.class.getResource("/Iconos/actualizar.png")));
		panel_Botones.add(Bactualizar);

		Bborrar = new JButton("Borrar");
		Bborrar.setIcon(new ImageIcon(VistaGenerica.class.getResource("/Iconos/eliminar.png")));
		panel_Botones.add(Bborrar);

		modeloTabla = new DefaultTableModel(columnasTabla, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		table = new JTable(modeloTabla);
		table.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
		table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
				cargarDatosFormulario();
			}
		});

		JScrollPane scrollPaneTabla = new JScrollPane(table);
		scrollPaneTabla.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
		add(scrollPaneTabla, BorderLayout.CENTER);

	}

	public String getTbuscar() {
		return Tbuscar.getText();
	}

	public void setTbuscar(String tbuscar) {
		Tbuscar.setText(tbuscar);
	}

	public DefaultTableModel getModeloTabla() {
		return modeloTabla;
	}

	public void setModeloTabla(DefaultTableModel modeloTabla) {
		this.modeloTabla = modeloTabla;
	}

	public JTable getTable() {
		return table;
	}

	public void setTable(JTable table) {
		this.table = table;
	}

	public JButton getBguardar() {
		return Bguardar;
	}

	public void setBguardar(JButton bguardar) {
		Bguardar = bguardar;
	}

	// metodo abstracto que ocuparan varias apartados como cliente, proveedor
	protected abstract JPanel crearPanelCampos();

	public abstract void limpiarCampos();

	protected abstract void cargarDatosFormulario();

	public abstract <T extends Entidad> T getDatosDelFormulario();

	public int filaSelect() {
		int filaselect = table.getSelectedRow();

		if (filaselect > -1) {

			return (int) table.getValueAt(filaselect, 0);

		}

		return -1;

	}

	public void mostrarMensaje(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
	}

	public void mostrarError(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
	}

	public <T extends Entidad> void mostrarEntidades(List<T> lista) {
		modeloTabla.setRowCount(0);
		for (T item : lista) {
			modeloTabla.addRow(item.toTableRow());
		}
	}

	public void addGuardarListener(ActionListener listener) {
		Bguardar.addActionListener(listener);
	}

	public void addBorrarListener(ActionListener listener) {
		Bborrar.addActionListener(listener);
	}

	public void addActualizarListener(ActionListener listener) {
		Bactualizar.addActionListener(listener);
	}

	public void addBuscarListener(ActionListener listener) {
		Bbuscar.addActionListener(listener);
	}

}
