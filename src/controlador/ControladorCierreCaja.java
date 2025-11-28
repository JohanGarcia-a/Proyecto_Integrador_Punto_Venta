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

/**
 * Controlador encargado de gestionar el proceso de Cierre de Caja (Corte
 * Ciego).
 * <p>
 * Coordina la interacción entre la vista de cierre, los datos financieros del
 * turno actual y la persistencia del cierre. Su función principal es comparar
 * el dinero registrado por el sistema contra el dinero físico contado por el
 * cajero.
 * </p>
 * * @version 1.1
 */
public class ControladorCierreCaja {

	/** Referencia a la interfaz gráfica del panel de cierre. */
	private PanelCierreCaja vista;

	/** DAO para actualizar el estado del corte. */
	private CorteCajaDAO corteDAO;

	/** DAO para consultar los totales vendidos en el turno. */
	private VentaDAO ventaDAO;

	/** Usuario que está realizando el cierre. */
	private Empleado usuario;

	/** Objeto que representa el turno actual (debe estar en estado 'Abierto'). */
	private CorteCaja corteActual;

	// --- Variables para guardar los cálculos ---

	/** Sumatoria de ventas realizadas en efectivo durante el turno. */
	private double totalVentasEfectivo = 0.0;

	/**
	 * Sumatoria de ventas realizadas con tarjeta (no afecta el arqueo de efectivo).
	 */
	private double totalVentasTarjeta = 0.0;

	/**
	 * * Total calculado que debería existir físicamente en la caja.
	 * <p>
	 * Fórmula: {@code MontoInicial + VentasEfectivo - Retiros + Ingresos}.
	 * </p>
	 */
	private double totalEsperadoEnCaja = 0.0;

	/**
	 * Constructor principal.
	 * <p>
	 * Inicializa el controlador, configura los listeners de eventos y carga los
	 * datos financieros preliminares en la vista.
	 * </p>
	 * * @param vista Panel de interfaz gráfica.
	 * 
	 * @param corteDAO    DAO de cortes de caja.
	 * @param ventaDAO    DAO de ventas.
	 * @param usuario     Empleado logueado.
	 * @param corteActual Objeto del corte abierto recuperado previamente.
	 */
	public ControladorCierreCaja(PanelCierreCaja vista, CorteCajaDAO corteDAO, VentaDAO ventaDAO, Empleado usuario,
			CorteCaja corteActual) {
		this.vista = vista;
		this.corteDAO = corteDAO;
		this.ventaDAO = ventaDAO;
		this.usuario = usuario;
		this.corteActual = corteActual;

		// 1. Asignar los listeners
		this.vista.addCerrarCajaListener(e -> cerrarCaja());

		// Listener en tiempo real: recalcula la diferencia mientras el usuario escribe
		// el monto contado
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
	 * Recupera y calcula los datos financieros del turno activo.
	 * <p>
	 * 1. Obtiene el resumen de ventas agrupado por método de pago.<br>
	 * 2. Calcula el total esperado en efectivo.<br>
	 * 3. Actualiza las etiquetas informativas de la vista.
	 * </p>
	 */
	private void cargarDatosIniciales() {
		// 1. Formatear la fecha
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		// 2. Poner los datos de apertura en la vista
		vista.setDatosApertura(usuario.getNombre(), sdf.format(corteActual.getFechaApertura()),
				corteActual.getMontoInicial());

		// 3. Obtener el resumen de ventas del DAO (Agrupado por método de pago)
		List<Venta> resumenVentas = ventaDAO.obtenerTotalesPorMetodoPago(corteActual.getCorteID());

		// 4. Procesar el resumen para separar Efectivo de Tarjeta
		for (Venta ventaResumen : resumenVentas) {
			if ("Efectivo".equalsIgnoreCase(ventaResumen.getMetodoPago())) {
				this.totalVentasEfectivo = ventaResumen.getTotal();
			} else if ("Tarjeta".equalsIgnoreCase(ventaResumen.getMetodoPago())) {
				this.totalVentasTarjeta = ventaResumen.getTotal();
			}
		}

		// 5. Calcular el total esperado EN EFECTIVO (Base + Ventas Efectivo)
		// Nota: Si implementaras Movimientos de Caja (Retiros), deberías restarlos
		// aquí.
		this.totalEsperadoEnCaja = corteActual.getMontoInicial() + this.totalVentasEfectivo;

		// 6. Actualizar las etiquetas en la vista (Muestra lo que el sistema sabe)
		vista.setTotalesSistema(totalVentasEfectivo, totalVentasTarjeta, totalEsperadoEnCaja);

		// 7. Calcular la diferencia inicial (asumiendo 0 contado)
		calcularDiferencia();
	}

	/**
	 * Realiza el cálculo dinámico del arqueo (Corte Ciego).
	 * <p>
	 * Lee lo que el usuario escribió en el campo de texto y lo compara con el total
	 * esperado. Actualiza la etiqueta de "Diferencia" en la interfaz.
	 * </p>
	 * * @return El valor de la diferencia ({@code Contado - Esperado}).
	 */
	private double calcularDiferencia() {
		double montoContado = 0.0;
		try {
			// Lee el valor del JTextField
			montoContado = vista.getMontoContado();
		} catch (NumberFormatException e) {
			// Si el campo está vacío o contiene letras, se asume 0 para no romper el
			// cálculo
			montoContado = 0.0;
		}

		// Calcula la diferencia (Positivo = Sobrante, Negativo = Faltante)
		double diferencia = montoContado - this.totalEsperadoEnCaja;

		// Actualiza la vista visualmente
		vista.setDiferencia(diferencia);
		return diferencia;
	}

	/**
	 * Ejecuta el proceso final de cierre de turno.
	 * <p>
	 * 1. Valida el monto ingresado.<br>
	 * 2. Solicita confirmación al usuario mostrando el resumen final.<br>
	 * 3. Actualiza el objeto {@link CorteCaja} con fecha de cierre y montos
	 * finales.<br>
	 * 4. Persiste los cambios en la base de datos y bloquea el botón de cierre.
	 * </p>
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

		// 1. Recalcular la diferencia final para asegurar precisión
		double diferenciaFinal = calcularDiferencia();

		// 2. Pedir confirmación explícita
		String mensajeConfirm = String.format(
				"¿Estás seguro de cerrar la caja?\n\n" + "Monto Esperado: $%.2f\n" + "Monto Contado: $%.2f\n"
						+ "Diferencia: $%.2f\n\n" + "Esta acción no se puede deshacer.",
				this.totalEsperadoEnCaja, montoContado, diferenciaFinal);

		int confirm = JOptionPane.showConfirmDialog(vista, mensajeConfirm, "Confirmar Cierre de Caja",
				JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			// 3. Actualizar el objeto de modelo
			corteActual.setFechaCierre(new Date());
			corteActual.setMontoFinalSistema(this.totalEsperadoEnCaja);
			corteActual.setMontoFinalContado(montoContado);
			corteActual.setDiferencia(diferenciaFinal);
			corteActual.setStatus("Cerrado");

			// 4. Guardar en la base de datos mediante DAO
			boolean exito = corteDAO.cerrarCorte(corteActual);

			if (exito) {
				vista.mostrarMensaje("¡Caja cerrada con éxito!");
				// Deshabilitamos el botón para evitar doble cierre accidental
				vista.getBtnCerrarCaja().setEnabled(false);
			} else {
				vista.mostrarError("Error: No se pudo guardar el cierre de caja en la base de datos.");
			}
		}
	}
}