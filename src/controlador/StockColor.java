package controlador;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class StockColor extends DefaultTableCellRenderer {

	// --- MODIFICACIÓN AQUÍ ---
	// Se definen los índices para ambas columnas necesarias.
	private static final int COLUMNA_STOCK = 7;
	private static final int COLUMNA_STOCK_MINIMO = 8; // Índice de la columna (oculta) de stock mínimo.

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		try {
			// --- MODIFICACIÓN AQUÍ ---
			// Leemos ambos valores directamente de la fila que se está dibujando.
			int stock = Integer.parseInt(table.getValueAt(row, COLUMNA_STOCK).toString());
			int stockMinimo = Integer.parseInt(table.getValueAt(row, COLUMNA_STOCK_MINIMO).toString());

			// Si la fila está seleccionada, usamos los colores de selección por defecto
			if (isSelected) {
				c.setBackground(table.getSelectionBackground());
				c.setForeground(table.getSelectionForeground());
			} else {
				// Si no está seleccionada, aplicamos nuestra lógica de colores
				if (stock <= stockMinimo) {
					c.setBackground(Color.red); // Rojo para stock CERO
					c.setForeground(Color.WHITE);
				} else if (stock <= stockMinimo * 2) {
					c.setBackground(new Color(255, 255, 153)); // Amarillo para stock BAJO
					c.setForeground(Color.BLACK);
				} else {
					c.setBackground(new Color(150, 255, 50));
					c.setForeground(table.getForeground());
				}
			}
		} catch (Exception e) {
			// Si ocurre un error, usamos los colores por defecto para evitar que el
			// programa se detenga.
			c.setBackground(table.getBackground());
			c.setForeground(table.getForeground());
		}

		return c;
	}
}