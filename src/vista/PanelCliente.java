package vista;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import modelo.Clientes;

/**
 * Panel de interfaz gráfica para la gestión del catálogo de Clientes.
 * <p>
 * Hereda de {@link VistaGenerica} e implementa el formulario específico para
 * capturar los datos de un cliente (Nombre y Número de Teléfono).
 * </p>
 * <p>
 * Es utilizada por el {@code ControladorGenerico<Clientes>} para realizar las
 * operaciones CRUD.
 * </p>
 * 
 * @version 1.0
 */
public class PanelCliente extends VistaGenerica {

	private static final long serialVersionUID = 1L;

	/** Campo de texto para ingresar el nombre del cliente. */
	private JTextField Tnombre;

	/** Campo de texto para ingresar el número de teléfono o celular. */
	private JTextField TnumCel;

	/**
	 * Constructor.
	 * <p>
	 * Inicializa la vista base con el título "Clientes" y configura las columnas de
	 * la tabla de listado: [ID, Nombre, Teléfono].
	 * </p>
	 */
	public PanelCliente() {
		super("Clientes", new String[] { "Cid", "Nombre de Cliente", "Número Tel." });
	}

	/**
	 * Construye el panel central con los campos de formulario.
	 * <p>
	 * Utiliza un {@link GridBagLayout} para alinear las etiquetas y los campos de
	 * texto.
	 * </p>
	 * 
	 * @return El panel configurado con los componentes de edición.
	 */
	@Override
	protected JPanel crearPanelCampos() {

		JPanel Panel = new JPanel();

		Panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc_Panel = new GridBagConstraints();
		gbc_Panel.insets = new Insets(5, 5, 5, 5);
		gbc_Panel.fill = GridBagConstraints.HORIZONTAL;

		// Fila 0: Etiqueta y Campo de Nombre
		JLabel Lnombre = new JLabel("Nombre:");
		GridBagConstraints gbc_Lnombre = new GridBagConstraints();
		gbc_Lnombre.gridx = 0;
		gbc_Lnombre.gridy = 0;
		gbc_Lnombre.anchor = GridBagConstraints.EAST;
		Panel.add(Lnombre, gbc_Lnombre);

		Tnombre = new JTextField(20);
		GridBagConstraints gbc_Tnombre = new GridBagConstraints();
		gbc_Tnombre.gridx = 1;
		gbc_Tnombre.anchor = GridBagConstraints.WEST;
		gbc_Tnombre.weightx = 1.0;
		Panel.add(Tnombre, gbc_Tnombre);

		// Fila 1: Etiqueta y Campo de Teléfono
		JLabel LnumTel = new JLabel("Numero Tel:");
		GridBagConstraints gbc_LnumTel = new GridBagConstraints();
		gbc_LnumTel.gridx = 0;
		gbc_LnumTel.gridy = 1;
		gbc_LnumTel.anchor = GridBagConstraints.EAST;
		gbc_LnumTel.weightx = 0.0;
		Panel.add(LnumTel, gbc_LnumTel);

		TnumCel = new JTextField(20);
		GridBagConstraints gbc_TnumCel = new GridBagConstraints();
		gbc_TnumCel.gridx = 1;
		gbc_TnumCel.anchor = GridBagConstraints.WEST;
		gbc_TnumCel.weightx = 1.0;
		Panel.add(TnumCel, gbc_TnumCel);

		// Fila 2: Botón Limpiar (Acción manual para resetear formulario)
		JButton Blimpiar = new JButton("Limpiar");
		GridBagConstraints gbc_Blimpiar = new GridBagConstraints();
		gbc_Blimpiar.gridx = 1;
		gbc_Blimpiar.gridy = 2;
		gbc_Blimpiar.anchor = GridBagConstraints.EAST;
		gbc_Blimpiar.weightx = 0.0;
		gbc_Blimpiar.fill = GridBagConstraints.NONE;
		Panel.add(Blimpiar, gbc_Blimpiar);

		// Asignación de evento mediante expresión Lambda
		Blimpiar.addActionListener(e -> limpiarCampos());

		return Panel;
	}

	/**
	 * Restablece el formulario a su estado inicial.
	 * <p>
	 * Borra el contenido de los campos de texto y quita la selección de la tabla.
	 * </p>
	 */
	@Override
	public void limpiarCampos() {
		Tnombre.setText("");
		TnumCel.setText("");
		Tbuscar.setText("");
		table.clearSelection();
	}

	/**
	 * Carga los datos del cliente seleccionado en la tabla hacia los campos del
	 * formulario.
	 * <p>
	 * Permite la edición de un registro existente.
	 * </p>
	 */
	@Override
	protected void cargarDatosFormulario() {
		int filaSeleccionada = table.getSelectedRow();
		if (filaSeleccionada != -1) {
			String id = modeloTabla.getValueAt(filaSeleccionada, 0).toString();
			String nombre = modeloTabla.getValueAt(filaSeleccionada, 1).toString();
			String tpNumero = modeloTabla.getValueAt(filaSeleccionada, 2).toString();

			Tbuscar.setText(id); // El ID se carga en el campo de búsqueda (usado como ID oculto)
			Tnombre.setText(nombre);
			TnumCel.setText(tpNumero);
		}
	}

	/**
	 * Construye un objeto {@link Clientes} a partir de la información del
	 * formulario.
	 * <p>
	 * Realiza una validación básica: El nombre no puede estar vacío.
	 * </p>
	 * 
	 * @return Objeto cliente listo para ser procesado por el controlador, o
	 *         {@code null} si hay errores.
	 */
	@Override
	public Clientes getDatosDelFormulario() {

		String nombre = Tnombre.getText().trim();
		String numeroTel = TnumCel.getText().trim();

		if (nombre.isEmpty()) {
			mostrarError("El campo 'Nombre' no puede estar vacío.");
			return null;
		}

		// Si hay una fila seleccionada, recuperamos su ID para editar. Si no, es -1
		// (Nuevo).
		int id = filaSelect();

		return new Clientes(id, nombre, numeroTel);
	}
}