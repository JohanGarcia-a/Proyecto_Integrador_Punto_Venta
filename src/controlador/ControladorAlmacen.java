package controlador;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

import modelo.AlmacenProductos;
import modelo.Categorias;
import modelo.Empleado;
import modelo.EntradaInventario;
import modelo.Proveedor;
import modelogenerico.ModeloCombobox;
import persistencia.AlmacenProductosDAO;
import persistencia.CategoriaDAO;
import persistencia.EntradaInventarioDAO;
import persistencia.ProveedorDAO;
import vista.PanelAlmacenProductos;

/**
 * Controlador especializado en la gestión del inventario de productos.
 * <p>
 * Extiende la funcionalidad de {@link ControladorGenerico} para manejar la
 * lógica específica de {@link AlmacenProductos}, incluyendo:
 * <ul>
 * <li>Carga de listas desplegables (Proveedores y Categorías).</li>
 * <li>Búsqueda avanzada por Código o ID.</li>
 * <li><b>Auditoría de Stock:</b> Registro automático en el historial de
 * entradas cuando se agrega mercancía.</li>
 * <li>Gestión de alertas visuales (Semáforo de stock).</li>
 * </ul>
 * </p>
 * * @version 1.2
 */
public class ControladorAlmacen extends ControladorGenerico<AlmacenProductos> {

	/** DAO para acceder a la tabla de proveedores (llenado de ComboBox). */
	private ProveedorDAO proveedorDAO;

	/** Referencia a la vista específica de almacén. */
	private PanelAlmacenProductos panelAlmacen;

	/** DAO para acceder a la tabla de categorías (llenado de ComboBox). */
	private CategoriaDAO categoriaDAO;

	/**
	 * * Usuario que ha iniciado sesión en el sistema.
	 * <p>
	 * Esencial para registrar quién realizó las modificaciones de stock
	 * (Auditoría).
	 * </p>
	 */
	private Empleado usuarioActual;

	/**
	 * Constructor principal.
	 * <p>
	 * Inicializa los DAOs auxiliares, guarda la sesión del usuario actual y carga
	 * los datos iniciales en los componentes de la vista (ComboBoxes y Tabla).
	 * </p>
	 * * @param almacenDAO DAO principal para productos.
	 * 
	 * @param proveedorDAO DAO para proveedores.
	 * @param vista        Panel de interfaz gráfica.
	 * @param categoriaDAO DAO para categorías.
	 * @param usuario      Objeto {@link Empleado} logueado.
	 */
	public ControladorAlmacen(AlmacenProductosDAO almacenDAO, ProveedorDAO proveedorDAO, PanelAlmacenProductos vista,
			CategoriaDAO categoriaDAO, Empleado usuario) {
		super(almacenDAO, vista); // Inicializa la lógica genérica (tabla, botones básicos)

		this.proveedorDAO = proveedorDAO;
		this.panelAlmacen = vista;
		this.categoriaDAO = categoriaDAO;
		this.usuarioActual = usuario; // Guardamos el usuario para auditoría

		// Configuración inicial de la vista
		panelAlmacen.actualizarColoresTabla(); // Aplica renderizado condicional (Rojo/Amarillo/Verde)
		cargarProveedores();
		cargarCategorias();

		// Listener para habilitar/deshabilitar edición de stock mínimo
		this.panelAlmacen.addModifcarMinimoStok(e -> modificarStock());
	}

	/**
	 * Sobrescribe la búsqueda genérica para permitir buscar por Código de Barras o
	 * ID.
	 * <p>
	 * Prioriza la búsqueda exacta por código. Si no encuentra nada, intenta
	 * interpretar el texto como un ID numérico.
	 * </p>
	 */
	@Override
	public void buscar() {
		String textoBusqueda = vista.getTbuscar();
		if (textoBusqueda.isEmpty()) {
			vista.mostrarError("El campo de búsqueda está vacío.");
			return;
		}

		AlmacenProductosDAO almacenDAO = (AlmacenProductosDAO) this.modelo;
		AlmacenProductos productoEncontrado = null;

		// 1. Intento de búsqueda por Código (String exacto)
		productoEncontrado = almacenDAO.buscarPorCodigo(textoBusqueda);

		// 2. Si falla, intento de búsqueda por ID (Numérico)
		if (productoEncontrado == null) {
			try {
				int id = Integer.parseInt(textoBusqueda);
				productoEncontrado = almacenDAO.buscarPorID(id);
			} catch (NumberFormatException e) {
				// No es un error crítico, simplemente el texto no era un número
			}
		}

		// Resultados
		if (productoEncontrado != null) {
			vista.mostrarEntidades(Collections.singletonList(productoEncontrado));
			vista.mostrarMensaje("Producto encontrado.");
		} else {
			vista.mostrarError("No se encontró ningún producto con ese código o ID.");
		}
	}

	/**
	 * Sobrescribe la modificación genérica para incluir lógica de auditoría de
	 * inventario.
	 * <p>
	 * Si el usuario ingresó un valor en el campo "Agregar Cantidad" (> 0), se crea
	 * automáticamente un registro en {@link EntradaInventario} vinculando al
	 * {@code usuarioActual}.
	 * </p>
	 */
	@Override
	public void modificar() {
		int id = vista.filaSelect();
		if (id == -1) {
			vista.mostrarError("Por favor, seleccione un registro de la tabla para modificar.");
			return;
		}

		// 1. Leer la cantidad a agregar (si existe)
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

		// 2. Obtener datos del formulario
		AlmacenProductos entidad = (AlmacenProductos) vista.getDatosDelFormulario();
		if (entidad == null) {
			return;
		}

		// 3. Auditoría: Registrar entrada si hubo aumento de stock
		if (cantidadAgregada > 0) {
			// Creamos el registro histórico con el usuario de la sesión actual
			EntradaInventario nuevaEntrada = new EntradaInventario(entidad.getid(), cantidadAgregada, new Date(),
					this.usuarioActual.getid(), // ID del empleado logueado
					entidad.getDescripcion());

			EntradaInventarioDAO entradaDAO = new EntradaInventarioDAO();
			if (!entradaDAO.agregar(nuevaEntrada)) {
				vista.mostrarError(
						"¡Atención! No se pudo registrar la entrada en el historial. La modificación se ha cancelado por seguridad.");
				return;
			}
		}

		// 4. Confirmación y Actualización del Producto
		int confirmacion = JOptionPane.showConfirmDialog(vista,
				"¿Seguro que desea modificar el registro con ID " + id + "?", "Confirmar Modificación",
				JOptionPane.YES_NO_OPTION);

		if (confirmacion == JOptionPane.YES_OPTION) {
			if (modelo.modificar(entidad)) {
				vista.mostrarMensaje("Actualizado con éxito.");
				vista.limpiarCampos();
				mostrarTodo(); // Recargar tabla para ver nuevos colores/cantidades
			} else {
				vista.mostrarError("Error al actualizar el producto.");
			}
		}
	}

	/**
	 * Carga la lista de proveedores activos en el ComboBox de la vista.
	 */
	private void cargarProveedores() {
		List<Proveedor> listaProveedores = proveedorDAO.ObtenerTodo();
		ModeloCombobox.cargarComboBox(panelAlmacen.getComboProveedor(), listaProveedores);
	}

	/**
	 * Carga la lista de categorías en el ComboBox de la vista.
	 */
	private void cargarCategorias() {
		List<Categorias> listaCategorias = categoriaDAO.ObtenerTodo();
		ModeloCombobox.cargarComboBox(panelAlmacen.getComboCategoria(), listaCategorias);
	}

	/**
	 * Habilita o deshabilita los campos de edición de "Stock Mínimo" según el
	 * estado del CheckBox.
	 * <p>
	 * Listener asociado al evento de interfaz {@code addModifcarMinimoStok}.
	 * </p>
	 */
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