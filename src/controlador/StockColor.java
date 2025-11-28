package controlador;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderizador personalizado para las celdas de la tabla de inventario
 * (JTable).
 * <p>
 * Extiende {@link DefaultTableCellRenderer} para modificar la apariencia visual
 * de las filas basándose en la lógica de negocio del stock. Implementa un
 * sistema de "Semáforo" para alertar al usuario sobre el estado de los
 * productos:
 * <ul>
 * <li><b>Rojo:</b> Stock Crítico (Menor o igual al mínimo).</li>
 * <li><b>Amarillo:</b> Stock de Alerta (Menor o igual al doble del
 * mínimo).</li>
 * <li><b>Verde:</b> Stock Saludable.</li>
 * </ul>
 * </p>
 * * @version 1.1
 */
public class StockColor extends DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Índice de la columna en el modelo de tabla que contiene la Cantidad actual
	 * (Stock).
	 */
	private static final int COLUMNA_STOCK = 7;

	/**
	 * Índice de la columna en el modelo de tabla que contiene el Stock Mínimo.
	 * <p>
	 * Nota: Esta columna suele estar oculta en la vista (width=0), pero sus datos
	 * son necesarios para el cálculo.
	 * </p>
	 */
	private static final int COLUMNA_STOCK_MINIMO = 8;

	/**
	 * Método principal del renderizado. Se ejecuta para cada celda que la tabla
	 * necesita dibujar.
	 * <p>
	 * <b>Lógica:</b>
	 * <ol>
	 * <li>Obtiene los valores de Stock y Stock Mínimo de la fila actual.</li>
	 * <li>Verifica si la fila está seleccionada por el usuario (para mantener el
	 * color de selección azul por defecto).</li>
	 * <li>Si no está seleccionada, compara los valores numéricos y asigna el color
	 * de fondo (Background) correspondiente.</li>
	 * </ol>
	 * Incluye manejo de excepciones para evitar que un dato nulo o malformado rompa
	 * el pintado de la tabla completa.
	 * </p>
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		// Llamamos al método padre para obtener el componente base (JLabel) con la
		// configuración estándar
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		try {
			// Leemos los valores necesarios del modelo de datos de la tabla
			// Usamos Integer.parseInt para asegurar comparaciones numéricas correctas
			int stock = Integer.parseInt(table.getValueAt(row, COLUMNA_STOCK).toString());
			int stockMinimo = Integer.parseInt(table.getValueAt(row, COLUMNA_STOCK_MINIMO).toString());

			// Prioridad 1: Si el usuario seleccionó la fila, respetamos el estilo de
			// selección del sistema
			if (isSelected) {
				c.setBackground(table.getSelectionBackground());
				c.setForeground(table.getSelectionForeground());
			} else {
				// Prioridad 2: Lógica de Semáforo de Inventario
				if (stock <= stockMinimo) {
					c.setBackground(Color.red); // Crítico: Se necesita reabastecimiento urgente
					c.setForeground(Color.WHITE); // Texto blanco para contraste
				} else if (stock <= stockMinimo * 2) {
					c.setBackground(new Color(255, 255, 153)); // Advertencia (Amarillo pálido)
					c.setForeground(Color.BLACK);
				} else {
					c.setBackground(new Color(150, 255, 50)); // Saludable (Verde claro)
					c.setForeground(table.getForeground());
				}
			}
		} catch (Exception e) {
			// Fallback de seguridad: Si ocurre un error al parsear los números (ej. celda
			// vacía),
			// se usan los colores por defecto de la tabla para no dejar la celda en
			// blanco/gris.
			c.setBackground(table.getBackground());
			c.setForeground(table.getForeground());
		}

		return c;
	}
}