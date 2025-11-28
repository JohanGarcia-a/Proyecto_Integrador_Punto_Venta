package controlador;

import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;
import modelogenerico.BaseDAO;
import modelogenerico.Entidad;
import vista.VistaGenerica;

/**
 * Controlador base que implementa la lógica CRUD estándar para cualquier
 * entidad del sistema.
 * <p>
 * Utiliza <b>Genéricos de Java</b> ({@code <T extends Entidad>}) para operar
 * sobre cualquier clase que respete la estructura básica del modelo (tener ID y
 * método {@code toTableRow}). Esto permite reutilizar el mismo código para
 * gestionar Clientes, Proveedores, Empleados, etc., reduciendo la duplicidad y
 * facilitando el mantenimiento.
 * </p>
 * <p>
 * Conecta automáticamente los eventos de la {@link VistaGenerica} (Guardar,
 * Buscar, Borrar, Actualizar) con las operaciones del {@link BaseDAO}.
 * </p>
 * * @param <T> El tipo de Entidad que este controlador gestionará (ej.
 * {@code Cliente}, {@code Proveedor}).
 * 
 * @version 1.0
 */
public class ControladorGenerico<T extends Entidad> {

	/** Instancia del DAO genérico para acceso a datos. */
	BaseDAO<T> modelo;

	/** Referencia a la vista genérica (Formulario + Tabla). */
	VistaGenerica vista;

	/**
	 * Constructor principal.
	 * <p>
	 * Inicializa el controlador, carga los datos iniciales en la tabla y asigna los
	 * "Listeners" (escuchadores de eventos) a los botones de la vista.
	 * </p>
	 * * @param modelo Implementación concreta del DAO (ej.
	 * {@code new ClienteDAO()}).
	 * 
	 * @param vista Implementación concreta de la vista (ej.
	 *              {@code new PanelCliente()}).
	 */
	public ControladorGenerico(BaseDAO<T> modelo, VistaGenerica vista) {
		this.modelo = modelo;
		this.vista = vista;

		// Carga inicial de datos
		mostrarTodo();

		// Asignación de comportamientos a los botones mediante expresiones Lambda
		vista.addGuardarListener(e -> guardar());
		vista.addBuscarListener(e -> buscar());
		vista.addBorrarListener(e -> borrar());
		vista.addActualizarListener(e -> modificar());
	}

	/**
	 * Recupera todos los registros desde el DAO y actualiza la tabla de la vista.
	 * <p>
	 * Es utilizado al inicio y después de cualquier operación CRUD (Guardar,
	 * Borrar, Modificar) para asegurar que el usuario vea la información
	 * actualizada.
	 * </p>
	 */
	public void mostrarTodo() {
		List<T> Entidad = modelo.ObtenerTodo();
		vista.mostrarEntidades(Entidad);
	}

	/**
	 * Lógica para guardar un nuevo registro.
	 * <p>
	 * 1. Obtiene el objeto construido desde el formulario de la vista.<br>
	 * 2. Llama al método {@code agregar} del DAO.<br>
	 * 3. Si es exitoso, muestra mensaje, limpia campos y refresca la tabla.
	 * </p>
	 */
	private void guardar() {
		// Casting seguro gracias a que T extiende de Entidad y la vista retorna tipos
		// compatibles
		@SuppressWarnings("unchecked")
		T entidad = (T) vista.getDatosDelFormulario();

		if (entidad != null && modelo.agregar(entidad)) {
			vista.mostrarMensaje("Guardado");
			vista.limpiarCampos();
			mostrarTodo();
		}
	}

	/**
	 * Realiza una búsqueda por ID numérico.
	 * <p>
	 * Lee el campo de texto de búsqueda de la vista. Si encuentra el registro,
	 * actualiza la tabla para mostrar únicamente ese resultado.
	 * </p>
	 */
	public void buscar() {
		String idText = vista.getTbuscar();
		if (idText.isEmpty()) {
			vista.mostrarError("Campo de búsqueda vacío");
			return;
		}
		try {
			int id = Integer.parseInt(idText);
			T entidad = modelo.buscarPorID(id);

			if (entidad != null) {
				// Mostramos una lista con un solo elemento
				vista.mostrarEntidades(Collections.singletonList(entidad));
				vista.mostrarMensaje("Registro encontrado por ID: " + id);
			} else {
				vista.mostrarError("No se encontró registro con el ID " + id);
			}
		} catch (NumberFormatException ex) {
			vista.mostrarError("El ID debe ser un número válido.");
		}
	}

	/**
	 * Elimina el registro seleccionado en la tabla.
	 * <p>
	 * 1. Verifica que haya una fila seleccionada.<br>
	 * 2. Solicita confirmación al usuario mediante un cuadro de diálogo.<br>
	 * 3. Si confirma, ejecuta el borrado en el DAO y refresca la vista.
	 * </p>
	 */
	public void borrar() {
		int id = vista.filaSelect();

		if (id == -1) {
			vista.mostrarError("Seleccione registro a borrar");
			return; // Detenemos la ejecución si no hay selección
		}

		int confirmacion = JOptionPane.showConfirmDialog(vista,
				"¿Seguro que desea eliminar el registro con ID " + id + "?", "Confirmar Eliminación",
				JOptionPane.YES_NO_OPTION);

		if (confirmacion == JOptionPane.YES_OPTION) {
			if (modelo.borrar(id)) {
				vista.mostrarMensaje("Eliminado con éxito.");
				vista.limpiarCampos();
				mostrarTodo();
			} else {
				vista.mostrarError("Error al eliminar");
			}
		}
	}

	/**
	 * Actualiza la información de un registro existente.
	 * <p>
	 * Requiere que el usuario seleccione una fila para obtener el ID original.
	 * Luego lee los nuevos datos del formulario y solicita confirmación antes de
	 * persistir los cambios.
	 * </p>
	 */
	public void modificar() {
		int id = vista.filaSelect();
		if (id == -1) {
			vista.mostrarError("Por favor, seleccione un registro de la tabla para modificar.");
			return;
		}

		@SuppressWarnings("unchecked")
		T entidad = (T) vista.getDatosDelFormulario();

		if (entidad != null) {
			int confirmacion = JOptionPane.showConfirmDialog(vista,
					"¿Seguro que desea modificar el registro con ID " + id + "?", "Confirmar Modificación",
					JOptionPane.YES_NO_OPTION);

			if (confirmacion == JOptionPane.YES_OPTION) {
				if (modelo.modificar(entidad)) {
					vista.mostrarMensaje("Actualizado con éxito.");
					vista.limpiarCampos();
					mostrarTodo();
				} else {
					vista.mostrarError("Error al actualizar.");
				}
			}
		}
	}
}