package controlador;

import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;
import modelogenerico.BaseDAO;
import modelogenerico.Entidad;
import vista.VistaGenerica;

public class ControladorGenerico<T extends Entidad> {

	BaseDAO<T> modelo;
	VistaGenerica vista;

	public ControladorGenerico(BaseDAO<T> modelo, VistaGenerica vista) {
		this.modelo = modelo;
		this.vista = vista;
		mostrarTodo();
		vista.addGuardarListener(e -> guardar());
		vista.addBuscarListener(e -> buscar());
		vista.addBorrarListener(e -> borrar());
		vista.addActualizarListener(e -> modificar());
	}

	public void mostrarTodo() {

		List<T> Entidad = modelo.ObtenerTodo();
		vista.mostrarEntidades(Entidad);
	}

	private void guardar() {
		T entidad = (T) vista.getDatosDelFormulario();

		if (entidad != null && modelo.agregar(entidad)) {
				vista.mostrarMensaje("Guardado");
				vista.limpiarCampos();
				mostrarTodo();
			}
	}

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
				vista.mostrarEntidades(Collections.singletonList(entidad));
				vista.mostrarMensaje("Registro encontrado por ID: " + id);
			} else {
				vista.mostrarError("No se encontró registro con el ID " + id);
			}
		} catch (NumberFormatException ex) {
			vista.mostrarError("El ID debe ser un número válido.");
		}
	}

	public void borrar() {

		int id = vista.filaSelect();

		if (id == -1) {
			vista.mostrarError("Seleccione registro a borrar");
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
