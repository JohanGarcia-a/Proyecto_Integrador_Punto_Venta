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

/**
 * Controlador central para la generación de reportes y estadísticas del
 * sistema.
 * <p>
 * Coordina la interacción entre el {@link PanelReportes} y múltiples DAOs para
 * obtener datos de Ventas, Inventario y Caja. Su función principal es
 * transformar estos datos en visualizaciones de tabla (Swing) y documentos
 * exportables (JasperReports PDF / Excel CSV).
 * </p>
 * 
 * @version 1.2
 */
public class ControladorReportes {

	private PanelReportes vista;

	// DAOs necesarios para consultar toda la información del sistema
	private VentaDAO ventaDAO;
	private VentaDetalleDAO detalleDAO;
	private AlmacenProductosDAO productoDAO;
	private EntradaInventarioDAO entradaDAO;
	private CorteCajaDAO corteDAO;

	/** Usuario que solicita el reporte (para firmar el documento generado). */
	private Empleado usuarioActual;

	// --- Variables de Estado (Caché) ---
	// Guardan los datos de la última consulta en tabla para poder enviarlos a
	// JasperReports sin re-consultar la BD.

	private List<Venta> ultimasVentasConsultadas = null;
	private double ultimoTotalCalculadoVentas = 0.0;
	private String ultimoTituloReporteVentas = "";

	private List<?> ultimosDatosConsultadosInventario = null;
	private String ultimoTituloReporteInventario = "";

	private List<CorteCaja> ultimosCortesConsultados = null;

	/**
	 * Constructor principal.
	 * <p>
	 * Inicializa todos los DAOs y asigna los Listeners a los botones de las 4
	 * pestañas del panel de reportes (Ventas, Inventario, Tickets, Caja).
	 * </p>
	 * * @param vista Panel principal de reportes.
	 * 
	 * @param vDAO    DAO de ventas.
	 * @param dDAO    DAO de detalles.
	 * @param pDAO    DAO de productos.
	 * @param eDAO    DAO de entradas de inventario.
	 * @param usuario Empleado logueado.
	 */
	public ControladorReportes(PanelReportes vista, VentaDAO vDAO, VentaDetalleDAO dDAO, AlmacenProductosDAO pDAO,
			EntradaInventarioDAO eDAO, Empleado usuario) {
		this.vista = vista;
		this.ventaDAO = vDAO;
		this.detalleDAO = dDAO;
		this.productoDAO = pDAO;
		this.entradaDAO = eDAO;
		this.usuarioActual = usuario;

		this.corteDAO = new CorteCajaDAO();

		// --- Asignación de Listeners por Módulo ---

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

		// 3. Pestaña Tickets (Historial y Devoluciones)
		this.vista.getPanelTickets().addReimprimirListener(e -> reimprimirTicketSeleccionado());
		this.vista.getPanelTickets().addDevolucionListener(e -> realizarDevolucion());

		// 4. Pestaña Caja (Historial de Cortes)
		if (this.vista.getPanelCaja() != null) {
			this.vista.getPanelCaja().addGenerarListener(e -> mostrarReporteCaja());
			this.vista.getPanelCaja().addImprimirListener(e -> generarJasperReporteCaja());
		}

		cargarHistorialVentas(); // Carga inicial de la tabla de tickets
	}

	// ==========================================
	// MÉTODOS DE VENTAS
	// ==========================================

	/**
	 * Consulta las ventas realizadas en el día actual (00:00 a 23:59).
	 */
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

		// Configuración del modelo de tabla
		String[] columnas = { "ID Venta", "Fecha", "Cliente", "Empleado", "Total" };
		@SuppressWarnings("serial")
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

		// Actualizar vista y caché
		vista.getPanelVentas().mostrarResultados(model);
		vista.getPanelVentas().actualizarTotal(totalReporte);

		this.ultimasVentasConsultadas = ventas;
		this.ultimoTotalCalculadoVentas = totalReporte;
		this.ultimoTituloReporteVentas = "Reporte de Ventas del Día";
	}

	/**
	 * Consulta las ventas de un mes específico seleccionado en los ComboBox.
	 */
	private void mostrarVentasMesSeleccionadoEnTabla() {
		int mes = vista.getPanelVentas().getMesSeleccionado();
		int anio = vista.getPanelVentas().getAnioSeleccionado();
		if (mes < 0) {
			JOptionPane.showMessageDialog(vista, "Seleccione un mes.", "Advertencia", JOptionPane.WARNING_MESSAGE);
			return;
		}

		// Cálculo del primer y último día del mes
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, anio);
		cal.set(Calendar.MONTH, mes);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		Date inicioDeMes = cal.getTime();

		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, 23);
		Date finDeMes = cal.getTime();

		List<Venta> ventas = ventaDAO.obtenerVentasPorFecha(inicioDeMes, finDeMes);

		// Llenado de tabla (Idéntico a ventas del día)
		String[] columnas = { "ID Venta", "Fecha", "Cliente", "Empleado", "Total" };
		@SuppressWarnings("serial")
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

		// Obtener nombre del mes para el título
		String nombreMes = "";
		JComboBox<String> comboMes = vista.getPanelVentas().getComboMes();
		if (comboMes != null && comboMes.getSelectedItem() != null) {
			nombreMes = comboMes.getSelectedItem().toString();
		}
		this.ultimoTituloReporteVentas = "Reporte de Ventas de " + nombreMes + " " + anio;
	}

	/**
	 * Genera un reporte PDF utilizando la librería JasperReports.
	 * <p>
	 * Utiliza la lista {@code ultimasVentasConsultadas} como fuente de datos
	 * (DataSource), evitando una nueva consulta a la base de datos.
	 * </p>
	 */
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

	/**
	 * Exporta el contenido visual de cualquier JTable a un archivo CSV compatible
	 * con Excel. * @param tabla La tabla Swing que contiene los datos a exportar.
	 */
	private void exportarAExcelCSV(JTable tabla) {
		try {
			JFileChooser archivo = new JFileChooser();
			archivo.showSaveDialog(vista);
			File guardar = archivo.getSelectedFile();

			if (guardar != null) {
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
							// Limpieza de datos (quitar comas internas para no romper el CSV)
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
	// MÉTODOS DE INVENTARIO (Stock Bajo, Completo, Historial)
	// ==========================================

	private void mostrarStockBajoEnTabla() {
		List<AlmacenProductos> productos = productoDAO.obtenerProductosConStockBajo();
		// Lógica de llenado de tabla...
		String[] columnas = { "ID", "Producto", "Descripción", "Categoría", "Stock Actual", "Stock Mínimo" };
		DefaultTableModel model = new DefaultTableModel(columnas, 0); // (Simplificado para brevedad)
		// ... bucle de llenado ...
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
		DefaultTableModel model = new DefaultTableModel(columnas, 0);
		// ... bucle de llenado ...
		if (productos != null) {
			for (AlmacenProductos p : productos) {
				model.addRow(new Object[] { p.getid(), p.getNombre(), p.getDescripcion(), p.getCategoriaNombre(),
						p.getProveedorNombre(), p.getPrecio(), p.getCantidad() });
			}
		}
		vista.getPanelInventario().mostrarResultados(model);

		this.ultimosDatosConsultadosInventario = productos;
		this.ultimoTituloReporteInventario = "Reporte de Inventario Completo";
	}

	private void mostrarHistorialEntradasEnTabla() {
		List<EntradaInventario> entradas = entradaDAO.obtenerTodasLasEntradas();
		String[] columnas = { "Fecha", "Producto", "Descripción", "Cantidad Agregada", "Usuario" };
		DefaultTableModel model = new DefaultTableModel(columnas, 0);
		// ... bucle de llenado ...
		if (entradas != null) {
			for (EntradaInventario entrada : entradas) {
				model.addRow(new Object[] { entrada.getFechaEntrada(), entrada.getNombreProducto(),
						entrada.getProductoDescripcion(), entrada.getCantidadAgregada(), entrada.getNombreUsuario() });
			}
		}
		vista.getPanelInventario().mostrarResultados(model);

		this.ultimosDatosConsultadosInventario = entradas;
		this.ultimoTituloReporteInventario = "Historial de Entradas";
	}

	/**
	 * Genera el reporte PDF correspondiente según el último botón presionado en la
	 * pestaña de Inventario.
	 * <p>
	 * Selecciona dinámicamente el archivo .jasper (plantilla) basándose en el
	 * título del reporte actual.
	 * </p>
	 */
	private void generarJasperReporteInventario() {
		if (ultimosDatosConsultadosInventario == null || ultimosDatosConsultadosInventario.isEmpty()) {
			JOptionPane.showMessageDialog(vista, "Primero debe generar un reporte en la tabla.", "Sin Datos",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		String nombreArchivoJasper = "";
		// Selección de plantilla
		if ("Reporte de Productos con Stock Bajo".equals(ultimoTituloReporteInventario)) {
			nombreArchivoJasper = "/reportes/stok_bajo.jasper";
		} else if ("Reporte de Inventario Completo".equals(ultimoTituloReporteInventario)) {
			nombreArchivoJasper = "/reportes/inventario_completo.jasper";
		} else if ("Historial de Entradas".equals(ultimoTituloReporteInventario)) {
			nombreArchivoJasper = "/reportes/historial_entradas.jasper";
		} else {
			return;
		}

		try {
			InputStream archivoReporte = getClass().getResourceAsStream(nombreArchivoJasper);
			if (archivoReporte == null) {
				JOptionPane.showMessageDialog(vista, "Plantilla no encontrada.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(ultimosDatosConsultadosInventario);
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("P_TIENDA_NOMBRE", "MI TIENDA POS");

			JasperPrint jasperPrint = JasperFillManager.fillReport(archivoReporte, parametros, dataSource);
			JasperViewer.viewReport(jasperPrint, false);

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(vista, "Error al generar reporte: " + e.getMessage());
		}
	}

	// ==========================================
	// MÉTODOS DE TICKETS Y DEVOLUCIONES
	// ==========================================

	private void cargarHistorialVentas() {
		List<Venta> listaVentas = ventaDAO.obtenerTodasLasVentas();
		vista.getPanelTickets().mostrarVentas(listaVentas);
	}

	/**
	 * Permite volver a imprimir (o visualizar) el ticket de una venta pasada.
	 * <p>
	 * Recupera la cabecera y los detalles de la venta seleccionada y reutiliza la
	 * plantilla {@code ticket.jasper}.
	 * </p>
	 */
	private void reimprimirTicketSeleccionado() {
		int idVenta = vista.getPanelTickets().getIdVentaSeleccionada();
		if (idVenta == -1) {
			vista.getPanelTickets().mostrarError("Seleccione una venta para reimprimir.");
			return;
		}

		Venta ventaCompleta = ventaDAO.buscarVentaPorID(idVenta);
		List<VentaDetalle> detalles = detalleDAO.buscarDetallesPorVentaID(idVenta);
		ventaCompleta.setDetalles(detalles);

		try {
			InputStream archivoReporte = getClass().getResourceAsStream("/reportes/ticket.jasper");
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(ventaCompleta.getDetalles());
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("P_TIENDA_NOMBRE", "MI TIENDA POS");
			parametros.put("P_TICKET_NO", String.valueOf(ventaCompleta.getid()));
			// ... resto de parámetros ...
			parametros.put("P_TIENDA_DIRECCION", "Dirección del Negocio");
			parametros.put("P_TIENDA_TEL", "555-555-555");

			parametros.put("P_FECHA", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(ventaCompleta.getFecha()));
			parametros.put("P_CLIENTE", ventaCompleta.getNombreCliente());
			parametros.put("P_EMPLEADO", ventaCompleta.getNombreEmpleado());
			parametros.put("P_TOTAL_VENTA", ventaCompleta.getTotal());

			JasperPrint jasperPrint = JasperFillManager.fillReport(archivoReporte, parametros, dataSource);
			JasperViewer.viewReport(jasperPrint, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Procesa la devolución de una venta completa.
	 * <p>
	 * Llama al método especial del DAO que reintegra el stock y anula el monto
	 * financiero.
	 * </p>
	 */
	private void realizarDevolucion() {
		int idVenta = vista.getPanelTickets().getIdVentaSeleccionada();
		if (idVenta == -1)
			return;

		int confirm = JOptionPane.showConfirmDialog(vista.getPanelTickets(),
				"¿Está seguro de CANCELAR y DEVOLVER la Venta #" + idVenta + "?\nEsta acción es irreversible.",
				"Confirmar Devolución", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		if (confirm == JOptionPane.YES_OPTION) {
			if (ventaDAO.realizarDevolucion(idVenta)) {
				vista.getPanelTickets().mostrarMensaje("Devolución realizada con éxito.\nInventario actualizado.");
				cargarHistorialVentas(); // Refrescar tabla
			} else {
				vista.getPanelTickets().mostrarError("Error al procesar la devolución.");
			}
		}
	}

	// ==========================================
	// MÉTODOS DE CORTE DE CAJA
	// ==========================================

	/**
	 * Genera el reporte histórico de cortes de caja para el mes seleccionado.
	 */
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
		this.ultimosCortesConsultados = lista; // Guardar caché para Jasper

		// Llenar tabla
		String[] columnas = { "ID", "Usuario", "Apertura", "Cierre", "Inicial", "Esperado", "Real", "Diferencia",
				"Estado" };
		DefaultTableModel model = new DefaultTableModel(columnas, 0);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");

		for (CorteCaja c : lista) {
			model.addRow(new Object[] { c.getCorteID(), c.getNombreUsuario(), sdf.format(c.getFechaApertura()),
					(c.getFechaCierre() != null) ? sdf.format(c.getFechaCierre()) : "---", c.getMontoInicial(),
					c.getMontoFinalSistema(), c.getMontoFinalContado(), c.getDiferencia(), c.getStatus() });
		}
		vista.getPanelCaja().mostrarResultados(model);
	}

	private void generarJasperReporteCaja() {
		if (ultimosCortesConsultados == null || ultimosCortesConsultados.isEmpty())
			return;

		try {
			InputStream archivoReporte = getClass().getResourceAsStream("/reportes/corte_caja.jasper");
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(ultimosCortesConsultados);
			JasperPrint jasperPrint = JasperFillManager.fillReport(archivoReporte, new HashMap<>(), dataSource);
			JasperViewer.viewReport(jasperPrint, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}