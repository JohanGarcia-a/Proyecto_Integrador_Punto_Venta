package controlador;

import java.awt.event.ActionListener;
import java.util.List;

import modelo.CorteCaja;
import modelo.Empleado;
import modelo.MovimientoCaja;
import persistencia.MovimientoCajaDAO;
import vista.PanelMovimientosCaja;

public class ControladorMovimientosCaja extends ControladorGenerico<MovimientoCaja> {

	private PanelMovimientosCaja vistaMovimientos;
	private MovimientoCajaDAO modeloMovimientos;
	private Empleado usuarioActual;
	private CorteCaja corteActual;

	public ControladorMovimientosCaja(MovimientoCajaDAO modelo, PanelMovimientosCaja vista, Empleado usuario,
			CorteCaja corte) {
		super(modelo, vista);
		this.modeloMovimientos = modelo;
		this.vistaMovimientos = vista;
		this.usuarioActual = usuario;
		this.corteActual = corte;

		for (ActionListener al : vista.getBguardar().getActionListeners()) {
			vista.getBguardar().removeActionListener(al);
		}

		// 2. Agregamos nuestro propio listener personalizado
		vista.addGuardarListener(e -> guardarMovimientoPersonalizado());

		// 3. Cargar la tabla inicialmente con los datos del corte actual
		mostrarTodo();
	}

	@Override
	public void mostrarTodo() {
		// Sobrescribimos este método para que SOLO muestre los movimientos
		// del corte de caja que está abierto actualmente.
		if (corteActual != null) {
			List<MovimientoCaja> lista = modeloMovimientos.obtenerMovimientosPorCorte(corteActual.getCorteID());
			vista.mostrarEntidades(lista);
		}
	}

	private void guardarMovimientoPersonalizado() {
		// 1. Obtener datos del formulario (Tipo, Monto, Descripción)
		MovimientoCaja movimiento = (MovimientoCaja) vista.getDatosDelFormulario();

		if (movimiento != null) {
			// 2. INYECTAR LOS DATOS FALTANTES
			// Estos datos no vienen del formulario, vienen de la sesión actual
			movimiento.setCorteId(corteActual.getCorteID());
			movimiento.setUsuarioId(usuarioActual.getid());

			// 3. Validar si es un Egreso y si hay dinero suficiente (Opcional pero
			// recomendado)
			if ("Egreso".equals(movimiento.getTipoMovimiento())) {
				// Aquí podrías consultar el saldo actual si quisieras ser estricto
			}

			// 4. Guardar usando el DAO
			if (modeloMovimientos.agregar(movimiento)) {
				vista.mostrarMensaje("Movimiento registrado con éxito.");
				vista.limpiarCampos();
				mostrarTodo(); // Recargar la tabla
			} else {
				vista.mostrarError("Error al registrar el movimiento en la base de datos.");
			}
		}
	}
}