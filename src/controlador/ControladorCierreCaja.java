package controlador;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

import modelo.CorteCaja;
import modelo.Empleado;
import modelo.Venta;
import persistencia.CorteCajaDAO;
import persistencia.VentaDAO;
import vista.PanelCierreCaja;

public class ControladorCierreCaja {

	private PanelCierreCaja vista;
	private CorteCajaDAO corteDAO;
	private VentaDAO ventaDAO;
	private Empleado usuario;
	private CorteCaja corteActual;

	// --- Variables para guardar los cálculos ---
	private double totalVentasEfectivo = 0.0;
	private double totalVentasTarjeta = 0.0;
	private double totalEsperadoEnCaja = 0.0;

	public ControladorCierreCaja(PanelCierreCaja vista, CorteCajaDAO corteDAO, VentaDAO ventaDAO, Empleado usuario,
			CorteCaja corteActual) {
		this.vista = vista;
		this.corteDAO = corteDAO;
		this.ventaDAO = ventaDAO;
		this.usuario = usuario;
		this.corteActual = corteActual;

		// 1. Asignar los listeners
		this.vista.addCerrarCajaListener(e -> cerrarCaja());

		// Este listener recalcula la diferencia CADA VEZ que el usuario teclea
		this.vista.addMontoContadoListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				calcularDiferencia();
			}
		});

		// 2. Cargar los datos iniciales en la vista
		cargarDatosIniciales();
	}

	/**
	 * Carga los datos del corte abierto y calcula los totales de ventas.
	 */
	private void cargarDatosIniciales() {
		// 1. Formatear la fecha
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		// 2. Poner los datos de apertura en la vista
		vista.setDatosApertura(usuario.getNombre(), sdf.format(corteActual.getFechaApertura()),
				corteActual.getMontoInicial());

		// 3. Obtener el resumen de ventas del DAO
		List<Venta> resumenVentas = ventaDAO.obtenerTotalesPorMetodoPago(corteActual.getCorteID());

		// 4. Procesar el resumen
		for (Venta ventaResumen : resumenVentas) {
			if ("Efectivo".equalsIgnoreCase(ventaResumen.getMetodoPago())) {
				this.totalVentasEfectivo = ventaResumen.getTotal();
			} else if ("Tarjeta".equalsIgnoreCase(ventaResumen.getMetodoPago())) {
				this.totalVentasTarjeta = ventaResumen.getTotal();
			}
		}

		// 5. Calcular el total esperado EN EFECTIVO
		this.totalEsperadoEnCaja = corteActual.getMontoInicial() + this.totalVentasEfectivo;

		// 6. Actualizar las etiquetas en la vista
		vista.setTotalesSistema(totalVentasEfectivo, totalVentasTarjeta, totalEsperadoEnCaja);

		// 7. Calcular la diferencia (con $0.00 contado al inicio)
		calcularDiferencia();
	}

	/**
	 * Lee el monto contado por el usuario y actualiza la etiqueta de Diferencia.
	 */
	private double calcularDiferencia() {
		double montoContado = 0.0;
		try {
			// Lee el valor del JTextField
			montoContado = vista.getMontoContado();
		} catch (NumberFormatException e) {
			// Si el campo está vacío o es inválido, se asume 0
			montoContado = 0.0;
		}

		// Calcula la diferencia
		double diferencia = montoContado - this.totalEsperadoEnCaja;

		// Actualiza la vista
		vista.setDiferencia(diferencia);
		return diferencia; // Devuelve la diferencia calculada
	}

	/**
	 * Se ejecuta al presionar el botón "Cerrar Caja".
	 */
	private void cerrarCaja() {
		double montoContado = 0.0;
		try {
			montoContado = vista.getMontoContado();
			if (montoContado < 0) {
				vista.mostrarError("El monto contado no puede ser negativo.");
				return;
			}
		} catch (NumberFormatException e) {
			vista.mostrarError("Por favor, ingresa un monto válido en 'Monto Físico Contado'.");
			return;
		}

		// 1. Recalcular la diferencia final
		double diferenciaFinal = calcularDiferencia();

		// 2. Pedir confirmación
		String mensajeConfirm = String.format(
				"¿Estás seguro de cerrar la caja?\n\n" + "Monto Esperado: $%.2f\n" + "Monto Contado: $%.2f\n"
						+ "Diferencia: $%.2f\n\n" + "Esta acción no se puede deshacer.",
				this.totalEsperadoEnCaja, montoContado, diferenciaFinal);

		int confirm = JOptionPane.showConfirmDialog(vista, mensajeConfirm, "Confirmar Cierre de Caja",
				JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			// 3. Actualizar el objeto CorteCaja
			corteActual.setFechaCierre(new Date());
			corteActual.setMontoFinalSistema(this.totalEsperadoEnCaja);
			corteActual.setMontoFinalContado(montoContado);
			corteActual.setDiferencia(diferenciaFinal);
			corteActual.setStatus("Cerrado");

			// 4. Guardar en la base de datos
			boolean exito = corteDAO.cerrarCorte(corteActual);

			if (exito) {
				vista.mostrarMensaje("¡Caja cerrada con éxito!");
				// Deshabilitamos el botón para evitar doble cierre
				vista.getBtnCerrarCaja().setEnabled(false);
				// (El usuario tendrá que navegar a otro panel)
			} else {
				vista.mostrarError("Error: No se pudo guardar el cierre de caja en la base de datos.");
			}
		}
	}
}