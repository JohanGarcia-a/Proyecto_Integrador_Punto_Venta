package controlador;

import java.awt.event.ActionListener;
import java.util.List;

import modelo.CorteCaja;
import modelo.Empleado;
import modelo.MovimientoCaja;
import persistencia.MovimientoCajaDAO;
import vista.PanelMovimientosCaja;

/**
 * Controlador especializado en la gestión de Movimientos de Caja (Ingresos y
 * Egresos manuales).
 * <p>
 * Hereda de {@link ControladorGenerico} para aprovechar la estructura base,
 * pero modifica el comportamiento estándar para adaptarse al flujo de caja:
 * <ul>
 * <li><b>Filtro de Visualización:</b> Solo muestra los movimientos
 * pertenecientes al turno (corte) actual, no todo el historial.</li>
 * <li><b>Inyección de Dependencias:</b> Al guardar, asigna automáticamente el
 * ID del usuario logueado y el ID del corte abierto.</li>
 * </ul>
 * </p>
 * * @version 1.2
 */
public class ControladorMovimientosCaja extends ControladorGenerico<MovimientoCaja> {

	private PanelMovimientosCaja vistaMovimientos;
	private MovimientoCajaDAO modeloMovimientos;

	/** Usuario que está realizando la operación (Auditoría). */
	private Empleado usuarioActual;

	/** Turno de caja activo al cual se vincularán los movimientos. */
	private CorteCaja corteActual;

	/**
	 * Constructor principal.
	 * <p>
	 * Inicializa el controlador y realiza una configuración especial: <b>Remueve el
	 * listener de guardado genérico</b> y lo reemplaza por uno personalizado
	 * ({@code guardarMovimientoPersonalizado}), permitiendo controlar los datos
	 * antes de enviarlos a la BD.
	 * </p>
	 * * @param modelo DAO específico para movimientos.
	 * 
	 * @param vista   Panel de interfaz gráfica.
	 * @param usuario Empleado logueado en el sistema.
	 * @param corte   Objeto que representa el turno de caja abierto actualmente.
	 */
	public ControladorMovimientosCaja(MovimientoCajaDAO modelo, PanelMovimientosCaja vista, Empleado usuario,
			CorteCaja corte) {
		super(modelo, vista); // Inicializa la base genérica
		this.modeloMovimientos = modelo;
		this.vistaMovimientos = vista;
		this.usuarioActual = usuario;
		this.corteActual = corte;

		// 1. Truco técnico: Eliminamos el ActionListener genérico del botón guardar
		for (ActionListener al : vista.getBguardar().getActionListeners()) {
			vista.getBguardar().removeActionListener(al);
		}

		// 2. Agregamos nuestro propio listener personalizado con la lógica de caja
		vista.addGuardarListener(e -> guardarMovimientoPersonalizado());

		// 3. Cargar la tabla inicialmente con los datos del corte actual
		mostrarTodo();
	}

	/**
	 * Sobrescribe el método de visualización para aplicar un filtro de negocio.
	 * <p>
	 * En lugar de mostrar todos los movimientos históricos (comportamiento
	 * genérico), utiliza {@code obtenerMovimientosPorCorte} para listar solo lo que
	 * ha sucedido durante el turno abierto.
	 * </p>
	 */
	@Override
	public void mostrarTodo() {
		if (corteActual != null) {
			List<MovimientoCaja> lista = modeloMovimientos.obtenerMovimientosPorCorte(corteActual.getCorteID());
			vista.mostrarEntidades(lista);
		}
	}

	/**
	 * Lógica personalizada para registrar un ingreso o egreso.
	 * <p>
	 * Recupera los datos básicos del formulario (Monto, Descripción, Tipo) e
	 * <b>inyecta</b> los datos de contexto (ID de Corte y ID de Usuario) que la
	 * vista desconoce.
	 * </p>
	 */
	private void guardarMovimientoPersonalizado() {
		// 1. Obtener datos del formulario (Tipo, Monto, Descripción)
		MovimientoCaja movimiento = (MovimientoCaja) vista.getDatosDelFormulario();

		if (movimiento != null) {
			// 2. INYECTAR LOS DATOS FALTANTES (Contexto de Sesión)
			// Estos datos no vienen del formulario, vienen de la sesión actual
			movimiento.setCorteId(corteActual.getCorteID());
			movimiento.setUsuarioId(usuarioActual.getid());

			// 3. Validar si es un Egreso (Lógica de negocio opcional)
			if ("Egreso".equals(movimiento.getTipoMovimiento())) {
				// Aquí se podría validar si el monto a retirar <= saldo en caja
			}

			// 4. Guardar usando el DAO
			if (modeloMovimientos.agregar(movimiento)) {
				vista.mostrarMensaje("Movimiento registrado con éxito.");
				vista.limpiarCampos();
				mostrarTodo(); // Recargar la tabla filtrada
			} else {
				vista.mostrarError("Error al registrar el movimiento en la base de datos.");
			}
		}
	}
}