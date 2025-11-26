package modelo;

import java.text.SimpleDateFormat;

public class Ticket {

	private Venta venta;
	private static final int ANCHO_TICKET = 42;

	public Ticket(Venta venta) {
		this.venta = venta;
	}

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
			if (nombreProducto.length() > 10) {
				nombreProducto = nombreProducto.substring(0, 10);
			}

			String lineaProducto = String.format("%-5s %-12s %10.2f %10.2f\n", detalle.getCantidad(), nombreProducto,
					detalle.getPrecioUnitario(), detalle.getSubtotal());
			sb.append(lineaProducto);

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
