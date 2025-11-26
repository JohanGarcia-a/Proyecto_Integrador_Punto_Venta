package modelogenerico;

import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

public class ModeloCombobox {
    
    // El <T> indica que esta función trabaja con un tipo genérico
	 public static <T> void cargarComboBox(JComboBox<T> comboBox, List<T> items) {
        
        // El modelo también debe ser del mismo tipo genérico T
        DefaultComboBoxModel<T> model = new DefaultComboBoxModel<>();
        
        // Añadir los elementos de la lista al modelo
        for (T item : items) {
            model.addElement(item);
        }
        
        // Asignar el modelo al JComboBox
        comboBox.setModel(model);
        
        //(si quieres que el usuario elija explícitamente)
        if (model.getSize() > 0) {
            comboBox.setSelectedIndex(-1);
        }
    }
}