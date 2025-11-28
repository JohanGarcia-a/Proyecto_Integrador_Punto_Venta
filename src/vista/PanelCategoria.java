package vista;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import modelo.Categorias;

/**
 * Panel de interfaz gráfica para la gestión de Categorías de productos.
 * <p>
 * Hereda de {@link VistaGenerica} y proporciona un formulario simplificado para
 * crear y editar categorías.
 * </p>
 * <p>
 * <b>Nota Visual:</b> Aunque el formulario solo pide el "Nombre", la tabla de
 * esta vista está configurada para mostrar una columna adicional "Cantidad de
 * productos", la cual es un dato calculado (solo lectura) proveniente de la
 * base de datos.
 * </p>
 * 
 * @version 1.0
 */
public class PanelCategoria extends VistaGenerica {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Campo de texto para ingresar el nombre de la categoría. */
	private JTextField Tnombre;

	/**
	 * Constructor.
	 * <p>
	 * Configura el título del módulo como "Categorías" y define las columnas de la
	 * tabla: [ID, Nombre, Cantidad de productos].
	 * </p>
	 */
	public PanelCategoria() {
		super("Categorías", new String[] { "Cid", "Nombre de Categoria", "Cantidad de productos" });
	}

	/**
	 * Construye el panel de campos de formulario.
	 * <p>
	 * Utiliza un diseño {@link GridBagLayout} para alinear la etiqueta, el campo de
	 * texto y el botón de limpieza.
	 * </p>
	 * 
	 * @return El panel configurado con los componentes de edición.
	 */
	@Override
	protected JPanel crearPanelCampos() {
		JPanel Panel = new JPanel();
		Panel.setLayout(new GridBagLayout());

		// Fila 0: Etiqueta y Campo de Nombre
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

		// Fila 1: Botón para limpiar el formulario manualmente
		JButton Blimpiar = new JButton("Limpiar");
		GridBagConstraints gbc_Blimpiar = new GridBagConstraints();
		gbc_Blimpiar.insets = new Insets(5, 5, 5, 5);
		gbc_Blimpiar.gridx = 1;
		gbc_Blimpiar.gridy = 1;
		gbc_Blimpiar.anchor = GridBagConstraints.EAST;
		gbc_Blimpiar.fill = GridBagConstraints.NONE;
		Panel.add(Blimpiar, gbc_Blimpiar);

		// Listener (Lambda) para el botón limpiar
		Blimpiar.addActionListener(e -> limpiarCampos());

		return Panel;
	}

	/**
	 * Restablece el formulario a su estado inicial (Modo Creación).
	 * <p>
	 * Limpia el campo de nombre, quita la selección de la tabla y habilita el botón
	 * "Guardar".
	 * </p>
	 */
	@Override
	public void limpiarCampos() {
		Tnombre.setText("");
		Tbuscar.setText("");
		table.clearSelection();
		Bguardar.setEnabled(true);
		Bactualizar.setEnabled(false);
	}

	/**
	 * Carga los datos de la categoría seleccionada en la tabla hacia el campo de
	 * texto.
	 * <p>
	 * Cambia el estado de los botones a "Modo Edición" (Desactiva Guardar, Activa
	 * Actualizar).
	 * </p>
	 */
	@Override
	protected void cargarDatosFormulario() {
		int filaSeleccionada = table.getSelectedRow();
		if (filaSeleccionada != -1) {
			String id = modeloTabla.getValueAt(filaSeleccionada, 0).toString();
			String nombre = modeloTabla.getValueAt(filaSeleccionada, 1).toString();

			Tbuscar.setText(id); // El ID se guarda en el campo oculto/búsqueda de la vista padre
			Tnombre.setText(nombre);

			Bguardar.setEnabled(false);
			Bactualizar.setEnabled(true);
		}
	}

	/**
	 * Recopila la información ingresada por el usuario para crear un objeto
	 * {@link Categorias}.
	 * <p>
	 * Incluye validación para asegurar que el nombre no esté vacío.
	 * </p>
	 * 
	 * @return Objeto categoría listo para el controlador, o {@code null} si la
	 *         validación falla.
	 */
	@Override
	public Categorias getDatosDelFormulario() {
		String nombre = Tnombre.getText().trim();

		if (nombre.isEmpty()) {
			mostrarError("El campo 'Nombre' no puede estar vacío.");
			return null;
		}

		int id = filaSelect(); // Obtiene el ID si se seleccionó una fila, o -1 si es nuevo
		return new Categorias(id, nombre);
	}
}