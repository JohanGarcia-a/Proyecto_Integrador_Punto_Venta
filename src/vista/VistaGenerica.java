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

/**
 * Clase base abstracta para la creación de paneles de gestión CRUD (Create,
 * Read, Update, Delete).
 * <p>
 * Provee una estructura visual estandarizada compuesta por:
 * <ul>
 * <li><b>Panel Izquierdo:</b> Contiene la barra de búsqueda, el formulario de
 * campos (abstracto) y los botones de acción (Guardar, Actualizar,
 * Borrar).</li>
 * <li><b>Panel Central:</b> Contiene una {@link JTable} para listar los
 * registros.</li>
 * </ul>
 * </p>
 * <p>
 * Obliga a las clases hijas a implementar la lógica específica de los campos
 * del formulario y la extracción de datos, mientras que la lógica de la tabla y
 * los botones es manejada genéricamente.
 * </p>
 * 
 * @version 1.0
 */
public abstract class VistaGenerica extends JPanel {

	private static final long serialVersionUID = 1L;

	/** Campo de texto para ingresar el término de búsqueda (ID o texto). */
	protected JTextField Tbuscar;

	/** Modelo de datos para la tabla de resultados. */
	protected DefaultTableModel modeloTabla;

	/** Tabla principal para mostrar la lista de entidades. */
	protected JTable table;

	/** Botones de acción estándar. */
	protected JButton Bbuscar, Bguardar, Bactualizar, Bborrar;

	/**
	 * Constructor base.
	 * <p>
	 * Configura el diseño general (BorderLayout) y construye los componentes
	 * comunes: panel de búsqueda, panel de botones y tabla de resultados.
	 * </p>
	 * 
	 * @param Entidad       Nombre de la entidad a gestionar (usado en el título del
	 *                      borde).
	 * @param columnasTabla Arreglo de Strings con los nombres de las columnas de la
	 *                      tabla.
	 */
	public VistaGenerica(String Entidad, String[] columnasTabla) {
		setLayout(new BorderLayout(0, 0));

		JPanel PanelInfo = new JPanel();
		PanelInfo.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		add(PanelInfo, BorderLayout.WEST);
		PanelInfo.setLayout(new BorderLayout(0, 0));

		// ---------------------------Panel de
		// Busqueda----------------------------------------------
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

		// ------------------------------Panel de Campos
		// (Abstracto)----------------------------------------------
		// Llama al método implementado por la hija para obtener los campos específicos
		JPanel Panel_campos = crearPanelCampos();
		Panel_campos.setBorder(BorderFactory.createTitledBorder("Informacion de " + Entidad));
		Panel_campos.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		PanelInfo.add(Panel_campos, BorderLayout.CENTER);

		// ------------------------------Panel de
		// Botones----------------------------------------------
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

		// Configuración de la Tabla
		modeloTabla = new DefaultTableModel(columnasTabla, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Tabla no editable directamente
			}
		};

		table = new JTable(modeloTabla);
		table.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));

		// Listener de selección de tabla: Carga los datos en el formulario
		// automáticamente
		table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
				cargarDatosFormulario();
			}
		});

		JScrollPane scrollPaneTabla = new JScrollPane(table);
		scrollPaneTabla.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
		add(scrollPaneTabla, BorderLayout.CENTER);
	}

	// --- Métodos Abstractos (Contrato para las clases hijas) ---

	/**
	 * Debe construir y devolver un {@link JPanel} que contenga los campos de texto,
	 * etiquetas y componentes específicos para editar la entidad.
	 * 
	 * @return Panel configurado con los campos del formulario.
	 */
	protected abstract JPanel crearPanelCampos();

	/**
	 * Debe limpiar todos los campos del formulario y restablecer el estado inicial
	 * de la vista.
	 */
	public abstract void limpiarCampos();

	/**
	 * Debe leer la fila seleccionada en la tabla y poblar los campos del formulario
	 * con esos datos.
	 */
	protected abstract void cargarDatosFormulario();

	/**
	 * Debe leer los valores actuales de los campos del formulario y construir un
	 * objeto Entidad.
	 * 
	 * @param <T> Tipo genérico que extiende de Entidad.
	 * @return La entidad construida o null si hay errores de validación.
	 */
	public abstract <T extends Entidad> T getDatosDelFormulario();

	// --- Métodos de Utilidad Comunes ---

	/**
	 * Obtiene el ID (valor de la columna 0) de la fila seleccionada actualmente.
	 * 
	 * @return El ID numérico o -1 si no hay selección.
	 */
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

	/**
	 * Actualiza la tabla con una lista de entidades.
	 * <p>
	 * Utiliza el método {@code toTableRow()} de la interfaz {@link Entidad} para
	 * convertir cada objeto en una fila de datos.
	 * </p>
	 * 
	 * @param <T>   Tipo de entidad.
	 * @param lista Lista de objetos a mostrar.
	 */
	public <T extends Entidad> void mostrarEntidades(List<T> lista) {
		modeloTabla.setRowCount(0); // Limpiar tabla
		for (T item : lista) {
			modeloTabla.addRow(item.toTableRow());
		}
	}

	// --- Getters, Setters y Listeners ---

	public String getTbuscar() {
		return Tbuscar.getText();
	}

	public void setTbuscar(String tbuscar) {
		Tbuscar.setText(tbuscar);
	}

	public JButton getBguardar() {
		return Bguardar;
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