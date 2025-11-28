package vista;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import modelo.Proveedor;

/**
 * Panel de interfaz gráfica para la gestión del catálogo de Proveedores.
 * <p>
 * Hereda de {@link VistaGenerica} y proporciona el formulario para registrar,
 * modificar y visualizar la información de los proveedores que surten el
 * inventario.
 * </p>
 * <p>
 * Se integra con el {@code ControladorGenerico<Proveedor>} para realizar las
 * operaciones CRUD.
 * </p>
 * 
 * @version 1.0
 */
public class PanelProveedor extends VistaGenerica {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Campo de texto para el nombre o razón social del proveedor. */
	private JTextField Tnombre;

	/** Campo de texto para el número de contacto. */
	private JTextField TnumCel;

	/**
	 * Constructor.
	 * <p>
	 * Inicializa la vista base con el título "Proveedores" y configura las columnas
	 * de la tabla: [ID (Pid), Nombre, Teléfono].
	 * </p>
	 */
	public PanelProveedor() {
		super("Proveedores", new String[] { "Pid", "Nombre de Proveedor", "Número Tel." });
	}

	/**
	 * Construye el panel central con los campos del formulario.
	 * <p>
	 * Utiliza {@link GridBagLayout} para organizar las etiquetas y campos de texto
	 * de manera alineada y responsiva.
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

		// Fila 0: Etiqueta y Campo Nombre
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

		// Fila 1: Etiqueta y Campo Teléfono
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

		// Fila 2: Botón Limpiar
		JButton Blimpiar = new JButton("Limpiar");
		GridBagConstraints gbc_Blimpiar = new GridBagConstraints();
		gbc_Blimpiar.gridx = 1;
		gbc_Blimpiar.gridy = 2;
		gbc_Blimpiar.anchor = GridBagConstraints.EAST;
		gbc_Blimpiar.weightx = 0.0;
		gbc_Blimpiar.fill = GridBagConstraints.NONE;
		Panel.add(Blimpiar, gbc_Blimpiar);

		// Listener para limpiar campos manualmente
		Blimpiar.addActionListener(e -> limpiarCampos());

		return Panel;
	}

	/**
	 * Restablece el formulario a su estado inicial (limpio).
	 * <p>
	 * Borra los textos y deselecciona la tabla para permitir un nuevo registro.
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
	 * Carga los datos del proveedor seleccionado en la tabla hacia los campos de
	 * texto.
	 * <p>
	 * Permite la edición visual del registro.
	 * </p>
	 */
	@Override
	protected void cargarDatosFormulario() {
		int filaSeleccionada = table.getSelectedRow();
		if (filaSeleccionada != -1) {
			String id = modeloTabla.getValueAt(filaSeleccionada, 0).toString();
			String nombre = modeloTabla.getValueAt(filaSeleccionada, 1).toString();
			String tpNumero = modeloTabla.getValueAt(filaSeleccionada, 2).toString();

			Tbuscar.setText(id); // Guarda el ID en el campo oculto/búsqueda
			Tnombre.setText(nombre);
			TnumCel.setText(tpNumero);
		}
	}

	/**
	 * Construye un objeto {@link Proveedor} con la información actual del
	 * formulario.
	 * <p>
	 * <b>Validación:</b> Verifica que el campo Nombre no esté vacío.
	 * </p>
	 * 
	 * @return Objeto proveedor listo para el controlador, o {@code null} si hay
	 *         errores.
	 */
	@Override
	public Proveedor getDatosDelFormulario() {

		String nombre = Tnombre.getText().trim();
		String numeroTel = TnumCel.getText().trim();

		if (nombre.isEmpty()) {
			mostrarError("El campo 'Nombre' no puede estar vacío.");
			return null;
		}

		// Obtiene el ID de la fila seleccionada (para editar) o -1 (para crear nuevo)
		int id = filaSelect();

		return new Proveedor(id, nombre, numeroTel);
	}
}