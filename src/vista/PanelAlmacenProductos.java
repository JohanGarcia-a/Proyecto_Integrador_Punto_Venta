package vista;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;

import controlador.StockColor;
import modelo.AlmacenProductos;
import modelo.Categorias;
import modelo.Proveedor;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JCheckBox;
import javax.swing.UIManager;

/**
 * Panel de interfaz gráfica para la gestión del inventario de productos (CRUD).
 * <p>
 * Hereda de {@link VistaGenerica} e implementa los campos específicos para
 * productos: Precio, Código, Stock, Proveedor, Categoría e Imagen.
 * </p>
 * <p>
 * Incluye lógica visual para ocultar columnas técnicas (Ruta, Stock Mínimo) y
 * aplicar el renderizador de colores {@link StockColor}.
 * </p>
 * 
 * @version 1.2
 */
public class PanelAlmacenProductos extends VistaGenerica {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField Tnombre;
	private JTextField Tdescripcion;
	private JTextField Tprecio;
	private JTextField Tcodigo;
	private JTextField Tstok;
	private JLabel Limagen;
	private JTextField TagregarCantidad;
	private JLabel Lstok, LajusteStock;
	private JLabel Lcategoria;
	private String ruta = "";
	private JLabel Lproveedor;
	private JScrollPane ScrollProveedor;
	private JComboBox<Proveedor> comboProveedor;
	private JScrollPane ScrollCategoria;
	private JComboBox<Categorias> comboCategoria;
	private JLabel LminStok;
	private JTextField TminStok;
	private JCheckBox checMinStok;
	private StockColor stockRenderer;

	/**
	 * Constructor.
	 * <p>
	 * Inicializa la vista con el título "Almacen/Productos" y configura las
	 * columnas de la tabla. Aplica el renderizador de colores para el semáforo de
	 * stock y oculta las columnas de "Stock Mínimo" y "Ruta de Imagen" (ancho = 0).
	 * </p>
	 */
	public PanelAlmacenProductos() {

		super("Almacen/Productos", new String[] { "Pid", "Categoria", "Proveedor", "Nombre", "Descripcion", "Precio",
				"Codigo", "Cantidad", "Stock Mín.", "Ruta de Imagen" });
		setBackground(UIManager.getColor("MenuItem.selectionBackground"));

		Bactualizar.setEnabled(false);

		// Configuración del renderizador de colores (Semáforo)
		this.stockRenderer = new StockColor();
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(stockRenderer);
		}

		// Ocultar columna Stock Mínimo (Índice 8)
		TableColumn stokmin = table.getColumnModel().getColumn(8);
		stokmin.setMinWidth(0);
		stokmin.setMaxWidth(0);
		stokmin.setPreferredWidth(0);
		stokmin.setResizable(false);

		// Ocultar columna Ruta de Imagen (Índice 9)
		TableColumn rutaImagen = table.getColumnModel().getColumn(9);
		rutaImagen.setMinWidth(0);
		rutaImagen.setMaxWidth(0);
		rutaImagen.setPreferredWidth(0);
		rutaImagen.setResizable(false);
	}

	/**
	 * Construye el panel de campos de formulario específico para Productos.
	 * <p>
	 * Utiliza un {@link BorderLayout} principal que contiene:
	 * <ul>
	 * <li><b>Norte:</b> Panel con campos de texto (Nombre, Precio, Proveedor, etc.)
	 * organizados con {@link GridBagLayout}.</li>
	 * <li><b>Centro:</b> Panel para la visualización de la imagen del
	 * producto.</li>
	 * </ul>
	 * </p>
	 * 
	 * @return El panel configurado.
	 */
	@Override
	protected JPanel crearPanelCampos() {

		// 1. El panel principal (contenedor) usa BorderLayout
		JPanel Panel = new JPanel(new BorderLayout());

		// 2. Creamos el sub-panel para el FORMULARIO (campos de texto)
		JPanel panelFormulario = new JPanel();
		GridBagLayout gbl_panelFormulario = new GridBagLayout();
		gbl_panelFormulario.columnWeights = new double[] { 0.0, 1.0 }; // Columna 1 crece
		panelFormulario.setLayout(gbl_panelFormulario);

		// --- Configuración de componentes (Proveedor, Categoria, Nombre, etc.) ---
		// (Se omite el detalle repetitivo de GridBagConstraints por brevedad en la
		// documentación,
		// pero el código completo configura cada campo en su posición de la rejilla).

		// ---------------------Proveedor (Fila 0)-------------------------
		Lproveedor = new JLabel("Proveedor:");
		GridBagConstraints gbc_lproveedor = new GridBagConstraints();
		gbc_lproveedor.anchor = GridBagConstraints.EAST;
		gbc_lproveedor.insets = new Insets(0, 0, 5, 5);
		gbc_lproveedor.gridx = 0;
		gbc_lproveedor.gridy = 0;
		panelFormulario.add(Lproveedor, gbc_lproveedor);

		ScrollProveedor = new JScrollPane();
		GridBagConstraints gbc_scrollProveedor = new GridBagConstraints();
		gbc_scrollProveedor.anchor = GridBagConstraints.WEST;
		gbc_scrollProveedor.insets = new Insets(0, 0, 5, 5);
		gbc_scrollProveedor.fill = GridBagConstraints.HORIZONTAL;
		gbc_scrollProveedor.weightx = 1.0;
		gbc_scrollProveedor.gridx = 1;
		gbc_scrollProveedor.gridy = 0;
		comboProveedor = new JComboBox<Proveedor>();
		ScrollProveedor.setViewportView(comboProveedor);
		panelFormulario.add(ScrollProveedor, gbc_scrollProveedor);

		// ... (Resto de la configuración de campos: Categoria, Nombre, Precio, Codigo,
		// Stock) ...

		// -----------------------Categoria (Fila 1)----------------------------------
		Lcategoria = new JLabel("Categoria");
		GridBagConstraints gbc_lcategoria = new GridBagConstraints();
		gbc_lcategoria.anchor = GridBagConstraints.EAST;
		gbc_lcategoria.insets = new Insets(0, 0, 5, 5);
		gbc_lcategoria.gridx = 0;
		gbc_lcategoria.gridy = 1;
		panelFormulario.add(Lcategoria, gbc_lcategoria);

		ScrollCategoria = new JScrollPane();
		GridBagConstraints gbc_scrollCategoria = new GridBagConstraints();
		gbc_scrollCategoria.anchor = GridBagConstraints.WEST;
		gbc_scrollCategoria.insets = new Insets(0, 0, 5, 5);
		gbc_scrollCategoria.fill = GridBagConstraints.HORIZONTAL;
		gbc_scrollCategoria.weightx = 1.0;
		gbc_scrollCategoria.gridx = 1;
		gbc_scrollCategoria.gridy = 1;
		panelFormulario.add(ScrollCategoria, gbc_scrollCategoria);
		comboCategoria = new JComboBox<Categorias>();
		ScrollCategoria.setViewportView(comboCategoria);

		// ... [Continúa la definición de campos Nombre, Descripción, Precio, Código,
		// Stock] ...

		// ----------------nombre (Fila 2)----------------
		JLabel Lnombre = new JLabel("Nombre:");
		GridBagConstraints gbc_Lnombre = new GridBagConstraints();
		gbc_Lnombre.insets = new Insets(0, 0, 5, 5);
		gbc_Lnombre.gridx = 0;
		gbc_Lnombre.gridy = 2;
		gbc_Lnombre.anchor = GridBagConstraints.EAST;
		panelFormulario.add(Lnombre, gbc_Lnombre);

		Tnombre = new JTextField(20);
		GridBagConstraints gbc_Tnombre = new GridBagConstraints();
		gbc_Tnombre.insets = new Insets(0, 0, 5, 5);
		gbc_Tnombre.gridy = 2;
		gbc_Tnombre.gridx = 1;
		gbc_Tnombre.anchor = GridBagConstraints.WEST;
		gbc_Tnombre.weightx = 1.0;
		gbc_Tnombre.fill = GridBagConstraints.HORIZONTAL;
		panelFormulario.add(Tnombre, gbc_Tnombre);

		// --------------------descripcion (Fila 3)----------------------
		JLabel Ldescripcion = new JLabel("Descripcion:");
		Ldescripcion.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_Ldescripcion = new GridBagConstraints();
		gbc_Ldescripcion.insets = new Insets(0, 0, 5, 5);
		gbc_Ldescripcion.gridx = 0;
		gbc_Ldescripcion.gridy = 3;
		gbc_Ldescripcion.anchor = GridBagConstraints.EAST;
		gbc_Ldescripcion.weightx = 0.0;
		panelFormulario.add(Ldescripcion, gbc_Ldescripcion);

		Tdescripcion = new JTextField(20);
		GridBagConstraints gbc_tdescripcion = new GridBagConstraints();
		gbc_tdescripcion.insets = new Insets(0, 0, 5, 5);
		gbc_tdescripcion.gridy = 3;
		gbc_tdescripcion.gridx = 1;
		gbc_tdescripcion.anchor = GridBagConstraints.WEST;
		gbc_tdescripcion.weightx = 1.0;
		gbc_tdescripcion.fill = GridBagConstraints.HORIZONTAL;
		panelFormulario.add(Tdescripcion, gbc_tdescripcion);

		// ------------------------precio (Fila 4)--------------------
		JLabel Lprecio = new JLabel("Precio:");
		GridBagConstraints gbc_Lprecio = new GridBagConstraints();
		gbc_Lprecio.anchor = GridBagConstraints.EAST;
		gbc_Lprecio.insets = new Insets(0, 0, 5, 5);
		gbc_Lprecio.gridx = 0;
		gbc_Lprecio.gridy = 4;
		panelFormulario.add(Lprecio, gbc_Lprecio);

		Tprecio = new JTextField(20);
		Tprecio.setText("");
		GridBagConstraints gbc_tprecio = new GridBagConstraints();
		gbc_tprecio.anchor = GridBagConstraints.WEST;
		gbc_tprecio.insets = new Insets(0, 0, 5, 5);
		gbc_tprecio.fill = GridBagConstraints.HORIZONTAL;
		gbc_tprecio.weightx = 1.0;
		gbc_tprecio.gridx = 1;
		gbc_tprecio.gridy = 4;
		panelFormulario.add(Tprecio, gbc_tprecio);

		// --------------------codigo (Fila 5)-----------------------------
		JLabel LnumTel_2 = new JLabel("Codigo:");
		GridBagConstraints gbc_LnumTel_2 = new GridBagConstraints();
		gbc_LnumTel_2.anchor = GridBagConstraints.EAST;
		gbc_LnumTel_2.insets = new Insets(0, 0, 5, 5);
		gbc_LnumTel_2.gridx = 0;
		gbc_LnumTel_2.gridy = 5;
		panelFormulario.add(LnumTel_2, gbc_LnumTel_2);

		Tcodigo = new JTextField(20);
		Tcodigo.setText("");
		GridBagConstraints gbc_tcodigo = new GridBagConstraints();
		gbc_tcodigo.anchor = GridBagConstraints.WEST;
		gbc_tcodigo.insets = new Insets(0, 0, 5, 5);
		gbc_tcodigo.fill = GridBagConstraints.HORIZONTAL;
		gbc_tcodigo.weightx = 1.0;
		gbc_tcodigo.gridx = 1;
		gbc_tcodigo.gridy = 5;
		panelFormulario.add(Tcodigo, gbc_tcodigo);

		// -----------------------cantidad (Fila 6)-----------------------------------
		Lstok = new JLabel("Stok:");
		GridBagConstraints gbc_lstok = new GridBagConstraints();
		gbc_lstok.anchor = GridBagConstraints.EAST;
		gbc_lstok.insets = new Insets(0, 0, 5, 5);
		gbc_lstok.gridx = 0;
		gbc_lstok.gridy = 6;
		panelFormulario.add(Lstok, gbc_lstok);

		Tstok = new JTextField(20);
		Tstok.setText("");
		GridBagConstraints gbc_tstok = new GridBagConstraints();
		gbc_tstok.anchor = GridBagConstraints.WEST;
		gbc_tstok.insets = new Insets(0, 0, 5, 5);
		gbc_tstok.fill = GridBagConstraints.HORIZONTAL;
		gbc_tstok.weightx = 1.0;
		gbc_tstok.gridx = 1;
		gbc_tstok.gridy = 6;
		panelFormulario.add(Tstok, gbc_tstok);

		// -----------------------Minimo Stok (Fila
		// 7)-----------------------------------
		LminStok = new JLabel("Minimo Stok:");
		GridBagConstraints gbc_lminStok = new GridBagConstraints();
		gbc_lminStok.anchor = GridBagConstraints.EAST;
		gbc_lminStok.insets = new Insets(0, 0, 5, 5);
		gbc_lminStok.gridx = 0;
		gbc_lminStok.gridy = 7;
		panelFormulario.add(LminStok, gbc_lminStok);

		TminStok = new JTextField();
		GridBagConstraints gbc_tminStok = new GridBagConstraints();
		gbc_tminStok.anchor = GridBagConstraints.WEST;
		gbc_tminStok.insets = new Insets(0, 0, 5, 5);
		gbc_tminStok.fill = GridBagConstraints.HORIZONTAL;
		gbc_tminStok.weightx = 1.0;
		gbc_tminStok.gridx = 1;
		gbc_tminStok.gridy = 7;
		panelFormulario.add(TminStok, gbc_tminStok);
		TminStok.setColumns(10);

		checMinStok = new JCheckBox("Modificar");
		GridBagConstraints gbc_checMinStok = new GridBagConstraints();
		gbc_checMinStok.insets = new Insets(0, 0, 5, 5);
		gbc_checMinStok.gridx = 2;
		gbc_checMinStok.gridy = 7;
		panelFormulario.add(checMinStok, gbc_checMinStok);

		// -------------------agregarCantidad (Fila 8)-------------------------
		LajusteStock = new JLabel("Ajuste Stok(+):");
		LajusteStock.setEnabled(false);
		GridBagConstraints gbc_lagregarStok = new GridBagConstraints();
		gbc_lagregarStok.anchor = GridBagConstraints.EAST;
		gbc_lagregarStok.insets = new Insets(0, 0, 5, 5);
		gbc_lagregarStok.gridx = 0;
		gbc_lagregarStok.gridy = 8;
		panelFormulario.add(LajusteStock, gbc_lagregarStok);

		TagregarCantidad = new JTextField();
		TagregarCantidad.setEnabled(false);
		GridBagConstraints gbc_tagregarCantidad = new GridBagConstraints();
		gbc_tagregarCantidad.anchor = GridBagConstraints.WEST;
		gbc_tagregarCantidad.insets = new Insets(0, 0, 5, 5);
		gbc_tagregarCantidad.fill = GridBagConstraints.HORIZONTAL;
		gbc_tagregarCantidad.weightx = 1.0;
		gbc_tagregarCantidad.gridx = 1;
		gbc_tagregarCantidad.gridy = 8;
		panelFormulario.add(TagregarCantidad, gbc_tagregarCantidad);
		TagregarCantidad.setColumns(10);

		// 3. Creamos el sub-panel para la IMAGEN y BOTONES
		JPanel panelImagen = new JPanel(new GridBagLayout());

		Limagen = new JLabel("Imagen");
		Limagen.setBorder(BorderFactory.createEtchedBorder());
		Limagen.setHorizontalAlignment(SwingConstants.CENTER);
		Limagen.setBackground(Color.LIGHT_GRAY);
		Limagen.setPreferredSize(new Dimension(220, 220));
		GridBagConstraints gbc_Limagen = new GridBagConstraints();
		gbc_Limagen.insets = new Insets(0, 0, 5, 0);
		gbc_Limagen.gridx = 0;
		gbc_Limagen.gridy = 0;
		panelImagen.add(Limagen, gbc_Limagen);

		JButton BagregarImagen = new JButton("Agregar Imagen");
		BagregarImagen.setIcon(new ImageIcon(PanelAlmacenProductos.class.getResource("/Iconos/agregar.png")));
		GridBagConstraints gbc_BagregarImagen = new GridBagConstraints();
		gbc_BagregarImagen.insets = new Insets(0, 0, 5, 0);
		gbc_BagregarImagen.gridx = 0;
		gbc_BagregarImagen.gridy = 1;
		panelImagen.add(BagregarImagen, gbc_BagregarImagen);
		BagregarImagen.addActionListener(e -> agregarImagen());

		JButton Blimpiar = new JButton("Limpiar");
		GridBagConstraints gbc_Blimpiar = new GridBagConstraints();
		gbc_Blimpiar.insets = new Insets(0, 0, 5, 0);
		gbc_Blimpiar.gridx = 0;
		gbc_Blimpiar.gridy = 2;
		gbc_Blimpiar.anchor = GridBagConstraints.EAST;
		panelImagen.add(Blimpiar, gbc_Blimpiar);
		Blimpiar.addActionListener(e -> limpiarCampos());

		// 4. Añadimos los sub-paneles al panel principal
		Panel.add(panelFormulario, BorderLayout.NORTH);
		Panel.add(panelImagen, BorderLayout.CENTER);

		return Panel;
	}

	// --- Getters y Setters para interacción con el Controlador ---

	public JTextField getTnombre() {
		return Tnombre;
	}

	public void setTnombre(JTextField tnombre) {
		Tnombre = tnombre;
	}

	// ... (Otros getters y setters estándar para campos de texto) ...

	/**
	 * Restablece el formulario a su estado inicial.
	 * <p>
	 * Habilita/deshabilita los campos según corresponda para un nuevo registro (por
	 * ejemplo, bloquea el ajuste de stock manual) y limpia las imágenes.
	 * </p>
	 */
	@Override
	public void limpiarCampos() {

		Bguardar.setEnabled(true);
		Bactualizar.setEnabled(false);

		Tstok.setEnabled(true);
		Lstok.setEnabled(true);
		TminStok.setEnabled(true);
		LminStok.setEnabled(true);
		TagregarCantidad.setEnabled(false);
		LajusteStock.setEnabled(false);

		Tbuscar.setText("");
		Tnombre.setText("");
		Tdescripcion.setText("");
		Tprecio.setText("");
		Tcodigo.setText("");
		Tstok.setText("");
		TagregarCantidad.setText("");
		Limagen.setIcon(null);
		Limagen.setText("Imagen");
		this.ruta = "";

		table.clearSelection();
		if (comboProveedor.getItemCount() > 0) {
			comboProveedor.setSelectedIndex(-1);
		}

		if (comboCategoria.getItemCount() > 0) {
			comboCategoria.setSelectedIndex(-1);
		}
	}

	public void addModifcarMinimoStok(ActionListener listener) {
		checMinStok.addActionListener(listener);
	}

	/**
	 * Abre un JFileChooser para seleccionar una imagen local y mostrarla en el
	 * panel.
	 */
	public void agregarImagen() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("Imágenes", "jpg", "png", "jpeg"));
		int resultado = fileChooser.showOpenDialog(null);

		if (resultado == JFileChooser.APPROVE_OPTION) {
			File archivo = fileChooser.getSelectedFile();
			ruta = archivo.getAbsolutePath();

			// Escalar y mostrar en el JLabel
			ImageIcon icon = new ImageIcon(ruta);
			Image img = icon.getImage().getScaledInstance(220, 220, Image.SCALE_SMOOTH);
			Limagen.setIcon(new ImageIcon(img));
		}
	}

	/**
	 * Carga los datos de la fila seleccionada en la tabla hacia los campos del
	 * formulario.
	 * <p>
	 * Incluye lógica para seleccionar automáticamente los items correctos en los
	 * ComboBoxes de Categoría y Proveedor basándose en el nombre (String).
	 * </p>
	 */
	@Override
	protected void cargarDatosFormulario() {
		int filaSeleccionada = table.getSelectedRow();
		if (filaSeleccionada != -1) {

			// Cambio de estado de botones para modo "Edición"
			Bguardar.setEnabled(false);
			Bactualizar.setEnabled(true);

			Tstok.setEnabled(false); // No se edita stock directo
			Lstok.setEnabled(false);
			TminStok.setEnabled(false);
			LminStok.setEnabled(false);
			TagregarCantidad.setEnabled(true); // Se habilita el ajuste manual
			LajusteStock.setEnabled(true);

			// Recuperación de valores del modelo de la tabla
			String id = modeloTabla.getValueAt(filaSeleccionada, 0).toString();
			String categoria = modeloTabla.getValueAt(filaSeleccionada, 1).toString();
			String nombreProveedor = modeloTabla.getValueAt(filaSeleccionada, 2).toString();
			String nombre = modeloTabla.getValueAt(filaSeleccionada, 3).toString();
			String descripcion = modeloTabla.getValueAt(filaSeleccionada, 4).toString();
			String precio = modeloTabla.getValueAt(filaSeleccionada, 5).toString();
			String codigo = modeloTabla.getValueAt(filaSeleccionada, 6).toString();
			String cantidad = modeloTabla.getValueAt(filaSeleccionada, 7).toString();

			String stockMinimo = modeloTabla.getValueAt(filaSeleccionada, 8).toString();
			Object valorRuta = modeloTabla.getValueAt(filaSeleccionada, 9);
			String rutaImagen = (valorRuta != null) ? valorRuta.toString() : "";

			// Llenado de campos
			Tbuscar.setText(id);
			Tnombre.setText(nombre);
			Tdescripcion.setText(descripcion);
			Tprecio.setText(precio);
			Tcodigo.setText(codigo);
			Tstok.setText(cantidad);
			TminStok.setText(stockMinimo);

			// Selección automática en ComboBoxes
			for (int i = 0; i < comboCategoria.getItemCount(); i++) {
				if (comboCategoria.getItemAt(i).getNombre().equals(categoria)) {
					comboCategoria.setSelectedIndex(i);
					break;
				}
			}

			for (int i = 0; i < comboProveedor.getItemCount(); i++) {
				if (comboProveedor.getItemAt(i).getNombre().equals(nombreProveedor)) {
					comboProveedor.setSelectedIndex(i);
					break;
				}
			}

			// Carga de imagen
			this.ruta = rutaImagen;
			if (!rutaImagen.isEmpty() && new File(rutaImagen).exists()) {
				ImageIcon icon = new ImageIcon(rutaImagen);
				Image img = icon.getImage().getScaledInstance(220, 220, Image.SCALE_SMOOTH);
				Limagen.setIcon(new ImageIcon(img));
				Limagen.setText("");
			} else {
				Limagen.setIcon(null);
				Limagen.setText("Imagen");
			}
		}
	}

	/**
	 * Construye un objeto {@link AlmacenProductos} a partir de los datos ingresados
	 * en el formulario.
	 * <p>
	 * Incluye validaciones básicas (campos vacíos) y la lógica para sumar la
	 * "Cantidad Agregada" al stock actual si estamos en modo edición.
	 * </p>
	 * 
	 * @return Objeto producto listo para ser procesado por el controlador, o null
	 *         si hay errores.
	 */
	@Override
	public AlmacenProductos getDatosDelFormulario() {
		if (Tnombre.getText().trim().isEmpty() || Tprecio.getText().trim().isEmpty()) {
			mostrarError("Los campos 'Nombre' y 'Precio' no pueden estar vacíos.");
			return null;
		}
		if (comboProveedor.getSelectedItem() == null || comboCategoria.getSelectedItem() == null) {
			mostrarError("Debe seleccionar un proveedor y una categoría.");
			return null;
		}

		try {
			int id = filaSelect();
			String nombre = Tnombre.getText().trim();
			String descripcion = Tdescripcion.getText().trim();
			double precio = Double.parseDouble(Tprecio.getText().trim());
			String codigo = Tcodigo.getText().trim();

			Proveedor proveedorSeleccionado = (Proveedor) comboProveedor.getSelectedItem();
			int proveedorId = proveedorSeleccionado.getid();
			Categorias categoriaSeleccionada = (Categorias) comboCategoria.getSelectedItem();
			int categoriaId = categoriaSeleccionada.getid();

			// Cálculo del stock total
			int cantidadActual = Tstok.getText().trim().isEmpty() ? 0 : Integer.parseInt(Tstok.getText().trim());
			int cantidadAgregada = (TagregarCantidad.isEnabled() && !TagregarCantidad.getText().trim().isEmpty())
					? Integer.parseInt(TagregarCantidad.getText().trim())
					: 0;
			int cantidadTotal = cantidadActual + cantidadAgregada;

			String minStockTexto = TminStok.getText().trim();
			int stockMinimo = minStockTexto.isEmpty() ? 10 : Integer.parseInt(minStockTexto);

			return new AlmacenProductos(id, nombre, descripcion, precio, codigo, cantidadTotal, ruta, categoriaId,
					proveedorId, stockMinimo);

		} catch (Exception e) {
			mostrarError("Error en los datos del formulario: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Fuerza el repintado de la tabla para asegurar que los colores del stock se
	 * actualicen.
	 */
	public void actualizarColoresTabla() {
		if (table != null) {
			table.repaint();
		}
	}

	// --- Getters de componentes UI para el Controlador ---

	public JComboBox<Proveedor> getComboProveedor() {
		return comboProveedor;
	}

	public JComboBox<Categorias> getComboCategoria() {
		return comboCategoria;
	}

	public void setComboCategoria(JComboBox<Categorias> comboCategoria) {
		this.comboCategoria = comboCategoria;
	}

	public void setComboBox(JComboBox<Proveedor> comboBox) {
		this.comboProveedor = comboBox;
	}

	public String getTagregarCantidad() {
		return TagregarCantidad.getText();
	}

	public void setTagregarCantidad(String tagregarCantidad) {
		TagregarCantidad.setText(tagregarCantidad);
	}

	public JLabel getLajusteStock() {
		return LajusteStock;
	}

	public JTextField getTminStok() {
		return TminStok;
	}

	public JLabel getLminStok() {
		return LminStok;
	}

	public JCheckBox getChecMinStok() {
		return checMinStok;
	}
}