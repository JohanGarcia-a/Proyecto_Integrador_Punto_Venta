package modelogenerico;

import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 * Clase utilitaria para la gestión de componentes visuales {@link JComboBox}.
 * <p>
 * Provee métodos estáticos para cargar listas de objetos genéricos en los
 * combos de la interfaz gráfica, facilitando la selección de entidades (ej.
 * elegir un Proveedor al crear un producto).
 * </p>
 * 
 * @version 1.0
 */
public class ModeloCombobox {

	/**
	 * Carga una lista de objetos en un JComboBox.
	 * <p>
	 * Utiliza el método {@code toString()} de cada objeto para determinar qué texto
	 * mostrar en la lista desplegable.
	 * </p>
	 * 
	 * @param <T>      El tipo de objeto que contendrá el combo (ej. Proveedor,
	 *                 Categoria).
	 * @param comboBox El componente visual JComboBox destino.
	 * @param items    La lista de datos a cargar.
	 */
	public static <T> void cargarComboBox(JComboBox<T> comboBox, List<T> items) {

		// Creamos un modelo de datos compatible con el tipo T
		DefaultComboBoxModel<T> model = new DefaultComboBoxModel<>();

		// Añadimos los elementos al modelo
		for (T item : items) {
			model.addElement(item);
		}

		// Asignamos el modelo lleno al componente visual
		comboBox.setModel(model);

		// Deseleccionamos el primer elemento por defecto para obligar al usuario a
		// elegir (UX)
		if (model.getSize() > 0) {
			comboBox.setSelectedIndex(-1);
		}
	}
}