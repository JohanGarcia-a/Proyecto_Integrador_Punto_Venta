package vista;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import modelo.Empleado;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPasswordField;

/**
 * Panel de interfaz gráfica para la gestión de Empleados (Usuarios del
 * sistema).
 * <p>
 * Hereda de {@link VistaGenerica} y permite administrar:
 * <ul>
 * <li>Datos personales (Nombre, Teléfono).</li>
 * <li>Permisos de acceso (Rol: ADMIN, GENERAL, SUPERVISOR).</li>
 * <li>Credenciales de seguridad (Contraseña).</li>
 * </ul>
 * </p>
 * 
 * @version 1.1
 */
public class PanelEmpleado extends VistaGenerica {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField Tnombre;
	private JTextField TnumCel;

	/** Selector de roles predefinidos. */
	private JComboBox<String> comboRol;

	/** Campo de texto enmascarado para la contraseña. */
	private JPasswordField Tpassword;

	/**
	 * Constructor.
	 * <p>
	 * Configura el título "Empleados" y las columnas de la tabla: [ID, Nombre,
	 * Teléfono, Rol]. La contraseña no se muestra en la tabla por seguridad.
	 * </p>
	 */
	public PanelEmpleado() {
		super("Empleados", new String[] { "Eid", "Nombre de Empleado", "Número Tel.", "Rol" });
	}

	/**
	 * Construye el formulario de edición de empleados.
	 * <p>
	 * Utiliza {@link GridBagLayout} para organizar los campos de Nombre, Teléfono,
	 * Rol (ComboBox) y Contraseña (PasswordField).
	 * </p>
	 * 
	 * @return El panel configurado.
	 */
	@Override
	protected JPanel crearPanelCampos() {
		JPanel Panel = new JPanel();
		GridBagLayout gbl_Panel = new GridBagLayout();
		gbl_Panel.columnWeights = new double[] { 0.0, 1.0 };
		Panel.setLayout(gbl_Panel);
		GridBagConstraints gbc_Panel = new GridBagConstraints();
		gbc_Panel.insets = new Insets(5, 5, 5, 5);
		gbc_Panel.fill = GridBagConstraints.HORIZONTAL;

		// Fila 0: Nombre
		JLabel Lnombre = new JLabel("Nombre:");
		GridBagConstraints gbc_Lnombre = new GridBagConstraints();
		gbc_Lnombre.insets = new Insets(0, 0, 5, 5);
		gbc_Lnombre.gridx = 0;
		gbc_Lnombre.gridy = 0;
		gbc_Lnombre.anchor = GridBagConstraints.EAST;
		Panel.add(Lnombre, gbc_Lnombre);

		Tnombre = new JTextField(20);
		GridBagConstraints gbc_Tnombre = new GridBagConstraints();
		gbc_Tnombre.insets = new Insets(0, 0, 5, 0);
		gbc_Tnombre.gridy = 0;
		gbc_Tnombre.gridx = 1;
		gbc_Tnombre.anchor = GridBagConstraints.WEST;
		gbc_Tnombre.weightx = 1.0;
		Panel.add(Tnombre, gbc_Tnombre);

		// Fila 1: Número de Teléfono
		JLabel LnumTel = new JLabel("Numero Tel:");
		GridBagConstraints gbc_LnumTel = new GridBagConstraints();
		gbc_LnumTel.insets = new Insets(0, 0, 5, 5);
		gbc_LnumTel.gridx = 0;
		gbc_LnumTel.gridy = 1;
		gbc_LnumTel.anchor = GridBagConstraints.EAST;
		Panel.add(LnumTel, gbc_LnumTel);

		TnumCel = new JTextField(20);
		GridBagConstraints gbc_TnumCel = new GridBagConstraints();
		gbc_TnumCel.insets = new Insets(0, 0, 5, 0);
		gbc_TnumCel.gridy = 1;
		gbc_TnumCel.gridx = 1;
		gbc_TnumCel.anchor = GridBagConstraints.WEST;
		gbc_TnumCel.weightx = 1.0;
		Panel.add(TnumCel, gbc_TnumCel);

		// Fila 2: Selector de Rol
		JLabel Lrol = new JLabel("Rol:");
		GridBagConstraints gbc_Lrol = new GridBagConstraints();
		gbc_Lrol.anchor = GridBagConstraints.EAST;
		gbc_Lrol.insets = new Insets(0, 0, 5, 5);
		gbc_Lrol.gridx = 0;
		gbc_Lrol.gridy = 2;
		Panel.add(Lrol, gbc_Lrol);

		comboRol = new JComboBox<String>();
		comboRol.setModel(new DefaultComboBoxModel<String>(new String[] { "ADMIN", "GENERAL", "SUPERVISOR" }));
		GridBagConstraints gbc_comboRol = new GridBagConstraints();
		gbc_comboRol.anchor = GridBagConstraints.WEST;
		gbc_comboRol.insets = new Insets(0, 0, 5, 0);
		gbc_comboRol.gridx = 1;
		gbc_comboRol.gridy = 2;
		Panel.add(comboRol, gbc_comboRol);

		// Fila 3: Contraseña (JPasswordField)
		JLabel Lpassword = new JLabel("Contraseña:");
		GridBagConstraints gbc_Lpassword = new GridBagConstraints();
		gbc_Lpassword.anchor = GridBagConstraints.EAST;
		gbc_Lpassword.insets = new Insets(0, 0, 5, 5);
		gbc_Lpassword.gridx = 0;
		gbc_Lpassword.gridy = 3;
		Panel.add(Lpassword, gbc_Lpassword);

		Tpassword = new JPasswordField();
		GridBagConstraints gbc_Tpassword = new GridBagConstraints();
		gbc_Tpassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_Tpassword.insets = new Insets(0, 0, 5, 0);
		gbc_Tpassword.gridx = 1;
		gbc_Tpassword.gridy = 3;
		Panel.add(Tpassword, gbc_Tpassword);

		// Fila 4: Botón Limpiar
		JButton Blimpiar = new JButton("Limpiar");
		GridBagConstraints gbc_Blimpiar = new GridBagConstraints();
		gbc_Blimpiar.gridx = 1;
		gbc_Blimpiar.gridy = 4;
		gbc_Blimpiar.anchor = GridBagConstraints.EAST;
		Panel.add(Blimpiar, gbc_Blimpiar);
		Blimpiar.addActionListener(e -> limpiarCampos());

		return Panel;
	}

	/**
	 * Limpia todos los campos del formulario, incluyendo la contraseña.
	 */
	@Override
	public void limpiarCampos() {
		Tnombre.setText("");
		TnumCel.setText("");
		Tbuscar.setText("");
		comboRol.setSelectedIndex(-1);
		Tpassword.setText("");
		table.clearSelection();
	}

	/**
	 * Carga los datos del empleado seleccionado en el formulario.
	 * <p>
	 * <b>Nota de Seguridad:</b> El campo de contraseña se deja en blanco
	 * intencionalmente. El usuario solo debe escribir en él si desea cambiar la
	 * contraseña actual.
	 * </p>
	 */
	@Override
	protected void cargarDatosFormulario() {
		int filaSeleccionada = table.getSelectedRow();
		if (filaSeleccionada != -1) {
			String id = modeloTabla.getValueAt(filaSeleccionada, 0).toString();
			String nombre = modeloTabla.getValueAt(filaSeleccionada, 1).toString();
			String tpNumero = modeloTabla.getValueAt(filaSeleccionada, 2).toString();
			String rol = modeloTabla.getValueAt(filaSeleccionada, 3).toString();

			Tbuscar.setText(id);
			Tnombre.setText(nombre);
			TnumCel.setText(tpNumero);
			comboRol.setSelectedItem(rol);

			// Por seguridad, la contraseña no se muestra.
			Tpassword.setText("");
		}
	}

	/**
	 * Construye un objeto {@link Empleado} con los datos del formulario.
	 * <p>
	 * <b>Validación de Contraseña:</b>
	 * <ul>
	 * <li>Si es un registro NUEVO, la contraseña es obligatoria.</li>
	 * <li>Si es una EDICIÓN, el campo vacío se puede interpretar como "no cambiar
	 * contraseña" (aunque esta lógica debe ser soportada por el
	 * DAO/Controlador).</li>
	 * </ul>
	 * </p>
	 * 
	 * @return Objeto empleado o {@code null} si hay errores de validación.
	 */
	@Override
	public Empleado getDatosDelFormulario() {
		String nombre = Tnombre.getText().trim();
		String numeroTel = TnumCel.getText().trim();
		String rol = (String) comboRol.getSelectedItem();
		String password = new String(Tpassword.getPassword());

		if (nombre.isEmpty()) {
			mostrarError("El campo 'Nombre' no puede estar vacío.");
			return null;
		}

		int id = filaSelect();

		// Validación: Contraseña obligatoria solo para nuevos registros
		if (id == -1 && password.isEmpty()) {
			mostrarError("La contraseña no puede estar vacía para un nuevo empleado.");
			return null;
		}

		return new Empleado(id, nombre, numeroTel, rol, password);
	}
}