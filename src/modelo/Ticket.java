package modelo;

import java.text.SimpleDateFormat;

/**
 * Clase utilitaria encargada de generar la representación textual de una venta.
 * <p>
 * Su función es tomar un objeto {@link Venta} y formatearlo como un comprobante
 * o ticket imprimible en consola o impresoras de texto. Organiza la cabecera,
 * los detalles de los productos y los totales utilizando un ancho fijo de
 * caracteres.
 * </p>
 * <p>
 * <b>Nota:</b> Esta clase genera tickets de texto plano. Para reportes gráficos
 * o PDF, el sistema utiliza JasperReports en el paquete de controladores.
 * </p>
 * * @version 1.0
 */
public class Ticket {

	/** Objeto Venta que contiene la información a imprimir. */
	private Venta venta;

	/**
	 * Constante que define el ancho máximo de caracteres por línea para el formato
	 * del ticket.
	 */
	private static final int ANCHO_TICKET = 42;

	/**
	 * Constructor. Asocia una venta específica para generar su ticket. * @param
	 * venta Objeto {@link Venta} con los datos de cabecera y detalles ya cargados.
	 */
	public Ticket(Venta venta) {
		this.venta = venta;
	}

	/**
	 * Construye una cadena de texto con el formato visual del ticket.
	 * <p>
	 * El formato incluye:
	 * <ul>
	 * <li>Encabezado (Nombre de la tienda, Dirección, Teléfono).</li>
	 * <li>Metadatos (Folio, Fecha, Cliente, Empleado).</li>
	 * <li>Tabla de Productos (Cantidad, Nombre truncado, Precio Unitario,
	 * Subtotal).</li>
	 * <li>Total final.</li>
	 * <li>Pie de página (Agradecimiento).</li>
	 * </ul>
	 * Utiliza {@link StringBuilder} para la construcción eficiente de la cadena.
	 * </p>
	 * * @return Cadena de texto formateada lista para imprimir.
	 */
	public String generarTextoTicket() {
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		// --- ENCABEZADO ---
		sb.append("        MI TIENDA POS\n");
		sb.append("   Dirección del Negocio\n");
		sb.append("      Tel: 555-555-555\n\n");
		sb.append("Ticket No: ").append(venta.getid()).append("\n");
		sb.append("Fecha: ").append(sdf.format(venta.getFecha())).append("\n");
		sb.append("Cliente: ").append(venta.getNombreCliente() != null ? venta.getNombreCliente() : "N/A").append("\n");
		sb.append("Atendido por: ").append(venta.getNombreEmpleado() != null ? venta.getNombreEmpleado() : "N/A")
				.append("\n");

		sb.append("-".repeat(ANCHO_TICKET)).append("\n");
		sb.append("Cant Producto     P.Unitario   Subtotal\n");
		sb.append("-".repeat(ANCHO_TICKET)).append("\n");

		// --- DETALLES DE PRODUCTOS ---
		for (VentaDetalle detalle : venta.getDetalles()) {
			String nombreProducto = detalle.getNombreProducto();
			// Truncar nombres largos para no romper el formato de columnas
			if (nombreProducto.length() > 10) {
				nombreProducto = nombreProducto.substring(0, 10);
			}

			// Formato de columnas alineadas
			String lineaProducto = String.format("%-5s %-12s %10.2f %10.2f\n", detalle.getCantidad(), nombreProducto,
					detalle.getPrecioUnitario(), detalle.getSubtotal());
			sb.append(lineaProducto);

			// Mostrar descripción en una línea nueva si existe
			if (detalle.getDescripcion() != null && !detalle.getDescripcion().isEmpty()) {
				sb.append(String.format("      (%s)\n", detalle.getDescripcion()));
			}
		}

		sb.append("-".repeat(ANCHO_TICKET)).append("\n");
		sb.append(String.format("%30s %10.2f\n", "TOTAL:", venta.getTotal()));

		sb.append("\nGracias por su compra :)\n");
		sb.append("       ¡Vuelva pronto!\n");

		return sb.toString();
	}
}