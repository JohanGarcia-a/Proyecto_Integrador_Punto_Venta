package controlador;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

import modelo.AlmacenProductos;
import modelo.Categorias;
import modelo.Empleado; // <-- Importante
import modelo.EntradaInventario;
import modelo.Proveedor;
import modelogenerico.ModeloCombobox;
import persistencia.AlmacenProductosDAO;
import persistencia.CategoriaDAO;
import persistencia.EntradaInventarioDAO;
import persistencia.ProveedorDAO;
import vista.PanelAlmacenProductos;

public class ControladorAlmacen extends ControladorGenerico<AlmacenProductos> {

	private ProveedorDAO proveedorDAO;
	private PanelAlmacenProductos panelAlmacen;
	private CategoriaDAO categoriaDAO;
	private Empleado usuarioActual; // --- MODIFICACIÓN 1: Añadir atributo para guardar el usuario

	// --- MODIFICACIÓN 2: El constructor ahora recibe el Empleado que inició sesión
	// ---
	public ControladorAlmacen(AlmacenProductosDAO almacenDAO, ProveedorDAO proveedorDAO, PanelAlmacenProductos vista,
			CategoriaDAO categoriaDAO, Empleado usuario) {
		super(almacenDAO, vista);

		this.proveedorDAO = proveedorDAO;
		this.panelAlmacen = vista;
		this.categoriaDAO = categoriaDAO;
		this.usuarioActual = usuario; // Guardamos el usuario

		panelAlmacen.actualizarColoresTabla();
		cargarProveedores();
		cargarCategorias();

		this.panelAlmacen.addModifcarMinimoStok(e -> modificarStock());
	}

	@Override
	public void buscar() {
		String textoBusqueda = vista.getTbuscar();
		if (textoBusqueda.isEmpty()) {
			vista.mostrarError("El campo de búsqueda está vacío.");
			return;
		}
		AlmacenProductosDAO almacenDAO = (AlmacenProductosDAO) this.modelo;
		AlmacenProductos productoEncontrado = null;
		productoEncontrado = almacenDAO.buscarPorCodigo(textoBusqueda);
		if (productoEncontrado == null) {
			try {
				int id = Integer.parseInt(textoBusqueda);
				productoEncontrado = almacenDAO.buscarPorID(id);
			} catch (NumberFormatException e) {
				// No es un error, solo no era un ID
			}
		}
		if (productoEncontrado != null) {
			vista.mostrarEntidades(Collections.singletonList(productoEncontrado));
			vista.mostrarMensaje("Producto encontrado.");
		} else {
			vista.mostrarError("No se encontró ningún producto con ese código o ID.");
		}
	}

	@Override
	public void modificar() {
		int id = vista.filaSelect();
		if (id == -1) {
			vista.mostrarError("Por favor, seleccione un registro de la tabla para modificar.");
			return;
		}

		int cantidadAgregada = 0;
		try {
			String textoCantidad = panelAlmacen.getTagregarCantidad();
			if (textoCantidad != null && !textoCantidad.trim().isEmpty()) {
				cantidadAgregada = Integer.parseInt(textoCantidad);
			}
		} catch (NumberFormatException e) {
			vista.mostrarError("La cantidad a agregar debe ser un número válido.");
			return;
		}

		AlmacenProductos entidad = (AlmacenProductos) vista.getDatosDelFormulario();
		if (entidad == null) {
			return;
		}

		if (cantidadAgregada > 0) {
			// --- MODIFICACIÓN 3: Usar el ID del usuario actual en lugar del '1' ---
			EntradaInventario nuevaEntrada = new EntradaInventario(entidad.getid(), cantidadAgregada, new Date(),
					this.usuarioActual.getid(), // <-- ¡CAMBIO IMPORTANTE!
					entidad.getDescripcion());

			EntradaInventarioDAO entradaDAO = new EntradaInventarioDAO();
			if (!entradaDAO.agregar(nuevaEntrada)) {
				vista.mostrarError(
						"¡Atención! No se pudo registrar la entrada en el historial. La modificación se ha cancelado.");
				return;
			}
		}

		int confirmacion = JOptionPane.showConfirmDialog(vista,
				"¿Seguro que desea modificar el registro con ID " + id + "?", "Confirmar Modificación",
				JOptionPane.YES_NO_OPTION);

		if (confirmacion == JOptionPane.YES_OPTION) {
			if (modelo.modificar(entidad)) {
				vista.mostrarMensaje("Actualizado con éxito.");
				vista.limpiarCampos();
				mostrarTodo();
			} else {
				vista.mostrarError("Error al actualizar el producto.");
			}
		}
	}

	private void cargarProveedores() {
		List<Proveedor> listaProveedores = proveedorDAO.ObtenerTodo();
		ModeloCombobox.cargarComboBox(panelAlmacen.getComboProveedor(), listaProveedores);
	}

	private void cargarCategorias() {
		List<Categorias> listaCategorias = categoriaDAO.ObtenerTodo();
		ModeloCombobox.cargarComboBox(panelAlmacen.getComboCategoria(), listaCategorias);
	}

	public void modificarStock() {
		if (panelAlmacen.getChecMinStok().isSelected()) {
			panelAlmacen.getTminStok().setEnabled(true);
			panelAlmacen.getLminStok().setEnabled(true);
		} else {
			panelAlmacen.getTminStok().setEnabled(false);
			panelAlmacen.getLminStok().setEnabled(false);
		}
	}
}