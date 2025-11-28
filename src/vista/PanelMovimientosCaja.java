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

/**
 * Panel de interfaz gráfica para el registro de movimientos manuales de
 * efectivo.
 * <p>
 * Hereda de {@link VistaGenerica} y permite al usuario registrar "Ingresos"
 * (ej. fondo extra) o "Egresos" (ej. pago de servicios, retiros parciales) que
 * afectan el saldo de la caja pero no provienen de ventas de productos.
 * </p>
 * <p>
 * <b>Nota de Seguridad:</b> En este módulo, la opción de <b>Actualizar
 * (Modificar)</b> está deshabilitada intencionalmente para garantizar la
 * integridad financiera. Si hubo un error, se recomienda borrar el movimiento y
 * crear uno nuevo.
 * </p>
 * 
 * @version 1.1
 */
public class PanelMovimientosCaja extends VistaGenerica {

	private static final long serialVersionUID = 1L;

	/** Selector del tipo de movimiento ("Ingreso" o "Egreso"). */
	private JComboBox<String> comboTipo;

	/** Campo de texto para ingresar la cantidad monetaria. */
	private JTextField txtMonto;

	/** Campo de texto para justificar el movimiento. */
	private JTextField txtDescripcion;

	/**
	 * Constructor.
	 * <p>
	 * Configura el título "Movimientos de Caja" y las columnas de la tabla. Además,
	 * <b>oculta y deshabilita</b> el botón de actualización heredado de la vista
	 * genérica para forzar un flujo de "Solo Lectura/Escritura/Borrado".
	 * </p>
	 */
	public PanelMovimientosCaja() {
		// 1. Configurar título y columnas de la tabla
		super("Movimientos de Caja", new String[] { "ID", "Fecha", "Usuario", "Tipo", "Monto", "Descripción" });

		// 2. Desactivar el botón "Actualizar" (Por seguridad financiera no se editan)
		Bactualizar.setVisible(false);
		Bactualizar.setEnabled(false);
	}

	/**
	 * Construye el formulario de captura de movimientos.
	 * <p>
	 * Utiliza {@link GridBagLayout} para organizar los campos:
	 * <ul>
	 * <li><b>Tipo:</b> ComboBox (Ingreso/Egreso).</li>
	 * <li><b>Monto:</b> Valor numérico positivo.</li>
	 * <li><b>Descripción:</b> Texto obligatorio.</li>
	 * </ul>
	 * </p>
	 * 
	 * @return El panel configurado.
	 */
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

	/**
	 * Restablece el formulario a su estado inicial.
	 */
	@Override
	public void limpiarCampos() {
		Tbuscar.setText("");
		if (comboTipo.getItemCount() > 0)
			comboTipo.setSelectedIndex(0);
		txtMonto.setText("");
		txtDescripcion.setText("");
		table.clearSelection();
		// Reactivar el botón guardar si estaba desactivado por una selección previa
		Bguardar.setEnabled(true);
	}

	/**
	 * Carga los datos de un movimiento seleccionado en la tabla.
	 * <p>
	 * Al seleccionar un registro, se cargan los datos visualmente pero se bloquea
	 * el botón "Guardar" para evitar duplicados. Dado que "Actualizar" está
	 * deshabilitado globalmente, esto sirve principalmente para visualizar detalles
	 * o preparar el borrado.
	 * </p>
	 */
	@Override
	protected void cargarDatosFormulario() {
		int fila = table.getSelectedRow();
		if (fila != -1) {
			// Obtenemos los datos de la tabla (columnas 3, 4 y 5 según definición)
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

	/**
	 * Construye un objeto {@link MovimientoCaja} validado con los datos del
	 * formulario.
	 * <p>
	 * <b>Validaciones:</b>
	 * <ul>
	 * <li>La descripción no puede estar vacía.</li>
	 * <li>El monto debe ser numérico y mayor a 0.</li>
	 * </ul>
	 * <b>Nota:</b> El objeto retornado tiene IDs temporales (0 o -1). Es
	 * responsabilidad del {@code ControladorMovimientosCaja} inyectar el ID de
	 * Usuario y el ID de Corte correctos.
	 * </p>
	 * 
	 * @return Objeto movimiento listo para procesar, o {@code null} si hay errores.
	 */
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

		// Retornamos un objeto parcial; el Controlador inyectará UsuarioID y CorteID
		return new MovimientoCaja(0, -1, tipo, monto, descripcion);
	}
}