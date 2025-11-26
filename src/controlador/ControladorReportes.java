package controlador;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

// Importaciones de modelos
import modelo.AlmacenProductos;
import modelo.CorteCaja;
import modelo.Empleado;
import modelo.EntradaInventario;
import modelo.Venta;
import modelo.VentaDetalle;

// Importaciones de JasperReports
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

// Importaciones de DAO
import persistencia.AlmacenProductosDAO;
import persistencia.CorteCajaDAO;
import persistencia.EntradaInventarioDAO;
import persistencia.VentaDAO;
import persistencia.VentaDetalleDAO;

// Importación de la vista
import vista.PanelReportes;

public class ControladorReportes {

	private PanelReportes vista;
	private VentaDAO ventaDAO;
	private VentaDetalleDAO detalleDAO;
	private AlmacenProductosDAO productoDAO;
	private EntradaInventarioDAO entradaDAO;
	private CorteCajaDAO corteDAO;
	private Empleado usuarioActual;

	// Variables para guardar datos del último reporte de VENTAS generado
	private List<Venta> ultimasVentasConsultadas = null;
	private double ultimoTotalCalculadoVentas = 0.0;
	private String ultimoTituloReporteVentas = "";

	// Variables para guardar datos del último reporte de INVENTARIO generado
	private List<?> ultimosDatosConsultadosInventario = null;
	private String ultimoTituloReporteInventario = "";

	// Variables para guardar datos del último reporte de CAJA generado
	private List<CorteCaja> ultimosCortesConsultados = null; // <-- NUEVA VARIABLE

	public ControladorReportes(PanelReportes vista, VentaDAO vDAO, VentaDetalleDAO dDAO, AlmacenProductosDAO pDAO,
			EntradaInventarioDAO eDAO, Empleado usuario) {
		this.vista = vista;
		this.ventaDAO = vDAO;
		this.detalleDAO = dDAO;
		this.productoDAO = pDAO;
		this.entradaDAO = eDAO;
		this.usuarioActual = usuario;

		this.corteDAO = new CorteCajaDAO();

		// --- Asignación de Listeners ---

		// 1. Pestaña Ventas
		this.vista.getPanelVentas().addVentasHoyListener(e -> mostrarVentasHoyEnTabla());
		this.vista.getPanelVentas().addVentasMesSeleccionadoListener(e -> mostrarVentasMesSeleccionadoEnTabla());
		this.vista.getPanelVentas().addVerImprimirJasperListener(e -> generarJasperReporteVentas());

		this.vista.getPanelVentas().addExportarExcelListener(e -> {
			exportarAExcelCSV(this.vista.getPanelVentas().getTablaResultados());
		});

		// 2. Pestaña Inventario
		this.vista.getPanelInventario().addStockBajoListener(e -> mostrarStockBajoEnTabla());
		this.vista.getPanelInventario().addInventarioCompletoListener(e -> mostrarInventarioCompletoEnTabla());
		this.vista.getPanelInventario().addHistorialEntradasListener(e -> mostrarHistorialEntradasEnTabla());
		this.vista.getPanelInventario().addVerImprimirInventarioJasperListener(e -> generarJasperReporteInventario());

		// 3. Pestaña Tickets
		this.vista.getPanelTickets().addReimprimirListener(e -> reimprimirTicketSeleccionado());
		this.vista.getPanelTickets().addDevolucionListener(e -> realizarDevolucion());
		// 4. Pestaña Caja
		if (this.vista.getPanelCaja() != null) {
			this.vista.getPanelCaja().addGenerarListener(e -> mostrarReporteCaja());
			// --- Listener para Imprimir Reporte de Caja ---
			this.vista.getPanelCaja().addImprimirListener(e -> generarJasperReporteCaja());
		}

		cargarHistorialVentas(); // Carga inicial de tickets
	}

	// ==========================================
	// MÉTODOS DE VENTAS
	// ==========================================

	private void mostrarVentasHoyEnTabla() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date inicioDelDia = cal.getTime();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		Date finDelDia = cal.getTime();

		List<Venta> ventas = ventaDAO.obtenerVentasPorFecha(inicioDelDia, finDelDia);

		String[] columnas = { "ID Venta", "Fecha", "Cliente", "Empleado", "Total" };
		DefaultTableModel model = new DefaultTableModel(columnas, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		double totalReporte = 0.0;
		if (ventas != null) {
			for (Venta venta : ventas) {
				model.addRow(new Object[] { venta.getid(), venta.getFecha(), venta.getNombreCliente(),
						venta.getNombreEmpleado(), venta.getTotal() });
				totalReporte += venta.getTotal();
			}
		}
		vista.getPanelVentas().mostrarResultados(model);
		vista.getPanelVentas().actualizarTotal(totalReporte);

		this.ultimasVentasConsultadas = ventas;
		this.ultimoTotalCalculadoVentas = totalReporte;
		this.ultimoTituloReporteVentas = "Reporte de Ventas del Día";
	}

	private void mostrarVentasMesSeleccionadoEnTabla() {
		int mes = vista.getPanelVentas().getMesSeleccionado();
		int anio = vista.getPanelVentas().getAnioSeleccionado();
		if (mes < 0) {
			JOptionPane.showMessageDialog(vista, "Seleccione un mes.", "Advertencia", JOptionPane.WARNING_MESSAGE);
			return;
		}

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, anio);
		cal.set(Calendar.MONTH, mes);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date inicioDeMes = cal.getTime();
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		Date finDeMes = cal.getTime();

		List<Venta> ventas = ventaDAO.obtenerVentasPorFecha(inicioDeMes, finDeMes);

		String[] columnas = { "ID Venta", "Fecha", "Cliente", "Empleado", "Total" };
		DefaultTableModel model = new DefaultTableModel(columnas, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		double totalReporte = 0.0;
		if (ventas != null) {
			for (Venta venta : ventas) {
				model.addRow(new Object[] { venta.getid(), venta.getFecha(), venta.getNombreCliente(),
						venta.getNombreEmpleado(), venta.getTotal() });
				totalReporte += venta.getTotal();
			}
		}
		vista.getPanelVentas().mostrarResultados(model);
		vista.getPanelVentas().actualizarTotal(totalReporte);

		this.ultimasVentasConsultadas = ventas;
		this.ultimoTotalCalculadoVentas = totalReporte;
		String nombreMes = "";
		JComboBox<String> comboMes = vista.getPanelVentas().getComboMes();
		if (comboMes != null && comboMes.getSelectedItem() != null) {
			nombreMes = comboMes.getSelectedItem().toString();
		}
		this.ultimoTituloReporteVentas = "Reporte de Ventas de " + nombreMes + " " + anio;
	}

	private void generarJasperReporteVentas() {
		if (ultimasVentasConsultadas == null || ultimasVentasConsultadas.isEmpty()) {
			JOptionPane.showMessageDialog(vista, "Primero debe generar un reporte en la tabla (Hoy o Mes).",
					"Sin Datos", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		try {
			InputStream archivoReporte = getClass().getResourceAsStream("/reportes/ventas_dia.jasper");
			if (archivoReporte == null) {
				JOptionPane.showMessageDialog(vista, "Archivo 'ventas_dia.jasper' no encontrado.", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(ultimasVentasConsultadas);
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("TOTAL_REPORTE", String.format("$ %.2f", ultimoTotalCalculadoVentas));
			parametros.put("FECHA_REPORTE", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
			parametros.put("EMPLEADO_GENERADOR", (this.usuarioActual != null) ? this.usuarioActual.getNombre() : "N/A");

			JasperPrint jasperPrint = JasperFillManager.fillReport(archivoReporte, parametros, dataSource);
			JasperViewer.viewReport(jasperPrint, false);

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(vista, "Error al generar el reporte Jasper: " + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	// Método para exportar la tabla actual a un archivo .csv (Excel compatible)
	private void exportarAExcelCSV(JTable tabla) {
		try {
			JFileChooser archivo = new JFileChooser();
			archivo.showSaveDialog(vista);
			File guardar = archivo.getSelectedFile();

			if (guardar != null) {
				// Aseguramos la extensión .csv
				guardar = new File(guardar.toString() + ".csv");

				try (FileWriter fw = new FileWriter(guardar)) {
					// 1. Escribir Encabezados
					for (int i = 0; i < tabla.getColumnCount(); i++) {
						fw.write(tabla.getColumnName(i) + ",");
					}
					fw.write("\n");

					// 2. Escribir Filas
					for (int i = 0; i < tabla.getRowCount(); i++) {
						for (int j = 0; j < tabla.getColumnCount(); j++) {
							// Obtenemos el valor y quitamos comas para no romper el CSV
							Object valor = tabla.getValueAt(i, j);
							String dato = (valor == null) ? "" : valor.toString().replace(",", "");
							fw.write(dato + ",");
						}
						fw.write("\n");
					}
					JOptionPane.showMessageDialog(vista, "Reporte exportado a Excel (CSV) exitosamente.");
				}
			}
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(vista, "Error al exportar: " + ex.getMessage());
		}
	}

	// ==========================================
	// MÉTODOS DE INVENTARIO
	// ==========================================

	private void mostrarStockBajoEnTabla() {
		List<AlmacenProductos> productos = productoDAO.obtenerProductosConStockBajo();
		String[] columnas = { "ID", "Producto", "Descripción", "Categoría", "Stock Actual", "Stock Mínimo" };
		DefaultTableModel model = new DefaultTableModel(columnas, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		if (productos != null) {
			for (AlmacenProductos p : productos) {
				model.addRow(new Object[] { p.getid(), p.getNombre(), p.getDescripcion(), p.getCategoriaNombre(),
						p.getCantidad(), p.getStockMinimo() });
			}
		}
		vista.getPanelInventario().mostrarResultados(model);

		this.ultimosDatosConsultadosInventario = productos; // Guardar datos para Jasper
		this.ultimoTituloReporteInventario = "Reporte de Productos con Stock Bajo";
	}

	private void mostrarInventarioCompletoEnTabla() {
		List<AlmacenProductos> productos = productoDAO.ObtenerTodo();
		String[] columnas = { "ID", "Producto", "Descripción", "Categoría", "Proveedor", "Precio", "Stock" };
		DefaultTableModel model = new DefaultTableModel(columnas, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		if (productos != null) {
			for (AlmacenProductos p : productos) {
				model.addRow(new Object[] { p.getid(), p.getNombre(), p.getDescripcion(), p.getCategoriaNombre(),
						p.getProveedorNombre(), p.getPrecio(), p.getCantidad() });
			}
		}
		vista.getPanelInventario().mostrarResultados(model);

		this.ultimosDatosConsultadosInventario = productos; // Guardar datos para Jasper
		this.ultimoTituloReporteInventario = "Reporte de Inventario Completo";
	}

	private void mostrarHistorialEntradasEnTabla() {
		List<EntradaInventario> entradas = entradaDAO.obtenerTodasLasEntradas();
		String[] columnas = { "Fecha", "Producto", "Descripción", "Cantidad Agregada", "Usuario" };
		DefaultTableModel model = new DefaultTableModel(columnas, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		if (entradas != null) {
			for (EntradaInventario entrada : entradas) {
				model.addRow(new Object[] { entrada.getFechaEntrada(), entrada.getNombreProducto(),
						entrada.getProductoDescripcion(), entrada.getCantidadAgregada(), entrada.getNombreUsuario() });
			}
		}
		vista.getPanelInventario().mostrarResultados(model);

		this.ultimosDatosConsultadosInventario = entradas; // Guardar datos para Jasper
		this.ultimoTituloReporteInventario = "Historial de Entradas";
	}

	private void generarJasperReporteInventario() {
		if (ultimosDatosConsultadosInventario == null || ultimosDatosConsultadosInventario.isEmpty()) {
			JOptionPane.showMessageDialog(vista,
					"Primero debe generar un reporte en la tabla (Stock Bajo, Completo o Historial).", "Sin Datos",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		String nombreArchivoJasper = "";
		// Decidimos qué plantilla usar
		if ("Reporte de Productos con Stock Bajo".equals(ultimoTituloReporteInventario)) {
			nombreArchivoJasper = "/reportes/stok_bajo.jasper";
		} else if ("Reporte de Inventario Completo".equals(ultimoTituloReporteInventario)) {
			nombreArchivoJasper = "/reportes/inventario_completo.jasper";
		} else if ("Historial de Entradas".equals(ultimoTituloReporteInventario)) {
			nombreArchivoJasper = "/reportes/historial_entradas.jasper";
		} else {
			JOptionPane.showMessageDialog(vista, "No se reconoce el tipo de reporte a generar.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			InputStream archivoReporte = getClass().getResourceAsStream(nombreArchivoJasper);
			if (archivoReporte == null) {
				JOptionPane.showMessageDialog(vista,
						"Archivo '" + nombreArchivoJasper.substring(1) + "' no encontrado.", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(ultimosDatosConsultadosInventario);
			Map<String, Object> parametros = new HashMap<>();

			parametros.put("P_TIENDA_NOMBRE", "MI TIENDA POS");

			JasperPrint jasperPrint = JasperFillManager.fillReport(archivoReporte, parametros, dataSource);
			JasperViewer.viewReport(jasperPrint, false);

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(vista, "Error al generar el reporte Jasper de inventario: " + e.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	// ==========================================
	// MÉTODOS DE HISTORIAL DE TICKETS
	// ==========================================

	private void cargarHistorialVentas() {
		List<Venta> listaVentas = ventaDAO.obtenerTodasLasVentas();
		vista.getPanelTickets().mostrarVentas(listaVentas);
	}

	private void reimprimirTicketSeleccionado() {
		int idVenta = vista.getPanelTickets().getIdVentaSeleccionada();
		if (idVenta == -1) {
			vista.getPanelTickets().mostrarError("Seleccione una venta para reimprimir.");
			return;
		}

		Venta ventaCompleta = ventaDAO.buscarVentaPorID(idVenta);
		if (ventaCompleta == null) {
			vista.getPanelTickets().mostrarError("Venta no encontrada.");
			return;
		}
		List<VentaDetalle> detalles = detalleDAO.buscarDetallesPorVentaID(idVenta);
		ventaCompleta.setDetalles(detalles);

		try {
			InputStream archivoReporte = getClass().getResourceAsStream("/reportes/ticket.jasper");
			if (archivoReporte == null) {
				vista.getPanelTickets().mostrarError("Archivo 'ticket.jasper' no encontrado.");
				return;
			}

			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(ventaCompleta.getDetalles());
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("P_TIENDA_NOMBRE", "MI TIENDA POS");
			parametros.put("P_TIENDA_DIRECCION", "Dirección del Negocio");
			parametros.put("P_TIENDA_TEL", "555-555-555");
			parametros.put("P_TICKET_NO", String.valueOf(ventaCompleta.getid()));
			parametros.put("P_FECHA", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(ventaCompleta.getFecha()));
			parametros.put("P_CLIENTE", ventaCompleta.getNombreCliente());
			parametros.put("P_EMPLEADO", ventaCompleta.getNombreEmpleado());
			parametros.put("P_TOTAL_VENTA", ventaCompleta.getTotal());

			JasperPrint jasperPrint = JasperFillManager.fillReport(archivoReporte, parametros, dataSource);

			Object[] options = { "Visualizar", "Imprimir", "Cancelar" };
			int seleccion = JOptionPane.showOptionDialog(vista.getPanelTickets(), "¿Qué desea hacer?", "Acción",
					JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

			if (seleccion == 0) { // Visualizar
				JasperViewer.viewReport(jasperPrint, false);
			} else if (seleccion == 1) { // Imprimir
				JasperPrintManager.printReport(jasperPrint, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			vista.getPanelTickets().mostrarError("Error al generar ticket: " + e.getMessage());
		}
	}

	private void realizarDevolucion() {
		int idVenta = vista.getPanelTickets().getIdVentaSeleccionada();
		if (idVenta == -1) {
			vista.getPanelTickets().mostrarError("Seleccione una venta de la tabla para devolver.");
			return;
		}

		// Confirmación de seguridad
		int confirm = JOptionPane.showConfirmDialog(vista.getPanelTickets(),
				"¿Está seguro de CANCELAR y DEVOLVER la Venta #" + idVenta + "?\n\n"
						+ "1. Los productos regresarán al Stock.\n" + "2. El monto de la venta se anulará ($0.00).\n"
						+ "3. Esta acción es irreversible.",
				"Confirmar Devolución", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		if (confirm == JOptionPane.YES_OPTION) {
			boolean exito = ventaDAO.realizarDevolucion(idVenta);

			if (exito) {
				vista.getPanelTickets().mostrarMensaje("Devolución realizada con éxito.\nInventario actualizado.");
				cargarHistorialVentas(); // Recargar la tabla para ver los cambios (Total debe ser 0)
			} else {
				vista.getPanelTickets().mostrarError("Ocurrió un error al procesar la devolución en la base de datos.");
			}
		}
	}
	// ==========================================
	// MÉTODOS DE CORTE DE CAJA
	// ==========================================

	private void mostrarReporteCaja() {
		int mes = vista.getPanelCaja().getMesSeleccionado();
		int anio = vista.getPanelCaja().getAnioSeleccionado();

		// Calcular fechas inicio/fin del mes
		Calendar cal = Calendar.getInstance();
		cal.set(anio, mes, 1, 0, 0, 0);
		Date inicio = cal.getTime();

		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		Date fin = cal.getTime();

		// Consultar DAO
		List<CorteCaja> lista = corteDAO.obtenerHistorialCortes(inicio, fin);

		// Guardamos la lista en la variable de clase para Jasper
		this.ultimosCortesConsultados = lista; // <-- GUARDAR DATOS PARA JASPER

		// Llenar tabla
		String[] columnas = { "ID", "Usuario", "Apertura", "Cierre", "Inicial", "Esperado", "Real", "Diferencia",
				"Estado" };
		DefaultTableModel model = new DefaultTableModel(columnas, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");

		for (CorteCaja c : lista) {
			model.addRow(new Object[] { c.getCorteID(), c.getNombreUsuario(), sdf.format(c.getFechaApertura()),
					(c.getFechaCierre() != null) ? sdf.format(c.getFechaCierre()) : "---", c.getMontoInicial(),
					c.getMontoFinalSistema(), c.getMontoFinalContado(), c.getDiferencia(), c.getStatus() });
		}
		vista.getPanelCaja().mostrarResultados(model);
	}

	// IMPRIMIR PDF DE CAJA ---
	private void generarJasperReporteCaja() {
		if (ultimosCortesConsultados == null || ultimosCortesConsultados.isEmpty()) {
			JOptionPane.showMessageDialog(vista, "Primero debe generar un reporte en la tabla.", "Sin Datos",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		try {
			// Asegúrate de haber compilado el .jrxml a .jasper y ponerlo en
			// /reportes/corte_caja.jasper
			InputStream archivoReporte = getClass().getResourceAsStream("/reportes/corte_caja.jasper");
			if (archivoReporte == null) {
				JOptionPane.showMessageDialog(vista,
						"Archivo 'corte_caja.jasper' no encontrado en el paquete de reportes.", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(ultimosCortesConsultados);
			Map<String, Object> parametros = new HashMap<>();

			JasperPrint jasperPrint = JasperFillManager.fillReport(archivoReporte, parametros, dataSource);
			JasperViewer.viewReport(jasperPrint, false);

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(vista, "Error al generar el reporte Jasper de Caja: " + e.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

}