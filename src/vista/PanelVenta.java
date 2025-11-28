package vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import modelo.AlmacenProductos;
import modelo.Clientes;
import modelo.Empleado;
import modelo.VentaDetalle;

import java.awt.Font;

/**
 * Panel principal para la gestión del Punto de Venta (POS).
 * <p>
 * Esta es la interfaz operativa más importante del sistema, donde el cajero
 * realiza las transacciones diarias. Sus componentes principales son:
 * <ul>
 * <li><b>Buscador de Productos:</b> Permite encontrar artículos por
 * código/nombre y visualizar su imagen y stock.</li>
 * <li><b>Carrito de Compras:</b> Tabla (JTable) que lista los productos a
 * vender.</li>
 * <li><b>Panel de Totales:</b> Muestra en tiempo real el Subtotal, Descuento,
 * IVA y Total.</li>
 * <li><b>Método de Pago:</b> Selector (Radio Button) para Efectivo o
 * Tarjeta.</li>
 * </ul>
 * </p>
 * 
 * @version 1.2
 */
public class PanelVenta extends JPanel {

	private static final long serialVersionUID = 1L;

	// --- Componentes de Cabecera (Info General) ---
	private JComboBox<Clientes> comboCliente;
	private JComboBox<Empleado> comboEmpleado;
	private JLabel LnombreEmpleado;

	// --- Componentes de Búsqueda y Detalle de Producto ---
	private JTextField TbuscarProducto;
	private JButton BbuscarProducto;
	private JLabel LimagenProducto;
	private JTextField TnombreProducto;
	private JTextField TprecioProducto;
	private JTextField TstockProducto;
	private JTextField TcantidadAVender;
	private JButton BagregarAlCarrito;
	private JTextField Tdescripcion;
	private String rutaImagenProducto = "";

	// --- Componentes de Carrito (Tabla) ---
	private DefaultTableModel modeloTablaCarrito;
	private JTable tableCarrito;
	private JButton BquitarDelCarrito;

	// --- Componentes Financieros y de Cierre ---
	private JLabel LsubtotalVenta;
	private JTextField TdescuentoVenta;
	private JLabel LimpuestosVenta;
	private JLabel LtotalVenta;

	private JRadioButton radioEfectivo;
	private JRadioButton radioTarjeta;
	private ButtonGroup grupoMetodoPago;

	private JButton BfinalizarVenta;
	private JButton BcancelarVenta;

	/** Constante del Impuesto al Valor Agregado (16%). */
	public static final double IVA_PORCENTAJE = 0.16;

	/**
	 * Constructor.
	 * <p>
	 * Inicializa el panel utilizando un {@link BorderLayout} principal y delega la
	 * construcción de las tres áreas clave (Norte, Centro, Sur) a métodos privados
	 * para mantener el código limpio.
	 * </p>
	 */
	public PanelVenta() {
		setLayout(new BorderLayout(10, 10));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// --- Panel Superior: Info Cliente/Empleado/Fecha ---
		JPanel panelInfoGeneral = crearPanelInfoGeneral();
		add(panelInfoGeneral, BorderLayout.NORTH);

		// --- Panel Central: Contenedor dividido ---
		JPanel panelCentral = new JPanel(new GridBagLayout());

		// 1. Panel Izquierdo: Búsqueda y detalles del producto
		JPanel panelProducto = crearPanelBusquedaProducto();
		GridBagConstraints gbc_panelProducto = new GridBagConstraints();
		gbc_panelProducto.gridx = 0;
		gbc_panelProducto.gridy = 0;
		gbc_panelProducto.weightx = 0.4; // Ocupa el 40% del ancho
		gbc_panelProducto.fill = GridBagConstraints.BOTH;
		panelCentral.add(panelProducto, gbc_panelProducto);

		// 2. Panel Derecho: Carrito de compras (Tabla)
		JPanel panelCarrito = crearPanelCarrito();
		GridBagConstraints gbc_panelCarrito = new GridBagConstraints();
		gbc_panelCarrito.insets = new Insets(0, 0, 5, 0);
		gbc_panelCarrito.gridx = 1;
		gbc_panelCarrito.gridy = 0;
		gbc_panelCarrito.weightx = 0.6; // Ocupa el 60% del ancho
		gbc_panelCarrito.fill = GridBagConstraints.BOTH;
		panelCentral.add(panelCarrito, gbc_panelCarrito);

		add(panelCentral, BorderLayout.CENTER);

		// --- Panel Inferior: Totales, Pago y Botones ---
		JPanel panelAccionesVenta = crearPanelAccionesVenta();
		add(panelAccionesVenta, BorderLayout.SOUTH);

		// Inicializar estado limpio
		limpiarCampos();
	}

	/**
	 * Construye la barra superior con selectores de Cliente/Empleado y la fecha
	 * actual.
	 * 
	 * @return Panel configurado.
	 */
	private JPanel crearPanelInfoGeneral() {
		JLabel LfechaVenta;
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		panel.setBorder(BorderFactory.createTitledBorder("Información de Venta"));

		panel.add(new JLabel("Cliente:"));
		comboCliente = new JComboBox<>();
		comboCliente.setPreferredSize(new Dimension(200, 25));
		panel.add(comboCliente);

		panel.add(new JLabel("Empleado:"));
		comboEmpleado = new JComboBox<>();
		comboEmpleado.setPreferredSize(new Dimension(200, 25));
		panel.add(comboEmpleado);

		panel.add(new JLabel("Fecha:"));
		LfechaVenta = new JLabel();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		LfechaVenta.setText(sdf.format(new Date()));
		panel.add(LfechaVenta);

		return panel;
	}

	/**
	 * Construye el panel izquierdo para buscar productos.
	 * <p>
	 * Contiene el campo de búsqueda, la visualización de la imagen, los campos de
	 * detalle (Nombre, Precio, Stock) y el botón "Agregar al Carrito".
	 * </p>
	 * 
	 * @return Panel configurado.
	 */
	private JPanel crearPanelBusquedaProducto() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Buscar Producto"));
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWeights = new double[] { 0.0, 1.0, 0.0 };
		panel.setLayout(gbl_panel);

		// --- Fila 0: Campo Búsqueda ---
		JLabel LcodigoNombre = new JLabel("Código/Nombre:");
		GridBagConstraints gbc_LcodigoNombre = new GridBagConstraints();
		gbc_LcodigoNombre.insets = new Insets(5, 5, 5, 5);
		gbc_LcodigoNombre.gridx = 0;
		gbc_LcodigoNombre.gridy = 0;
		gbc_LcodigoNombre.anchor = GridBagConstraints.EAST;
		panel.add(LcodigoNombre, gbc_LcodigoNombre);

		TbuscarProducto = new JTextField(15);
		GridBagConstraints gbc_TbuscarProducto = new GridBagConstraints();
		gbc_TbuscarProducto.insets = new Insets(5, 5, 5, 5);
		gbc_TbuscarProducto.gridx = 1;
		gbc_TbuscarProducto.gridy = 0;
		gbc_TbuscarProducto.weightx = 1.0;
		gbc_TbuscarProducto.fill = GridBagConstraints.HORIZONTAL;
		panel.add(TbuscarProducto, gbc_TbuscarProducto);

		BbuscarProducto = new JButton("Buscar", new ImageIcon(PanelVenta.class.getResource("/Iconos/buscar.png")));
		GridBagConstraints gbc_BbuscarProducto = new GridBagConstraints();
		gbc_BbuscarProducto.insets = new Insets(5, 5, 5, 0);
		gbc_BbuscarProducto.gridx = 2;
		gbc_BbuscarProducto.gridy = 0;
		panel.add(BbuscarProducto, gbc_BbuscarProducto);

		// --- Fila 1: Imagen del Producto ---
		LimagenProducto = new JLabel("");
		LimagenProducto.setIcon(new ImageIcon(PanelVenta.class.getResource("/Iconos/sinImagen.png")));
		LimagenProducto.setBorder(BorderFactory.createEtchedBorder());
		LimagenProducto.setHorizontalAlignment(SwingConstants.CENTER);
		LimagenProducto.setPreferredSize(new Dimension(220, 220));
		GridBagConstraints gbc_LimagenProducto = new GridBagConstraints();
		gbc_LimagenProducto.gridwidth = 3;
		gbc_LimagenProducto.insets = new Insets(5, 5, 5, 0);
		gbc_LimagenProducto.gridx = 0;
		gbc_LimagenProducto.gridy = 1;
		gbc_LimagenProducto.fill = GridBagConstraints.VERTICAL;
		panel.add(LimagenProducto, gbc_LimagenProducto);

		// --- Filas de Detalle (Nombre, Descripcion, Precio, Stock) ---
		// (Configuración repetitiva de GridBagLayout omitida para brevedad en docs)
		// ... [Código de configuración de TnombreProducto, Tdescripcion,
		// TprecioProducto, TstockProducto] ...

		// --- Fila 2: Nombre ---
		JLabel LnombreProducto = new JLabel("Nombre:");
		GridBagConstraints gbc_LnombreProducto = new GridBagConstraints();
		gbc_LnombreProducto.insets = new Insets(5, 5, 5, 5);
		gbc_LnombreProducto.gridx = 0;
		gbc_LnombreProducto.gridy = 2;
		gbc_LnombreProducto.anchor = GridBagConstraints.EAST;
		panel.add(LnombreProducto, gbc_LnombreProducto);

		TnombreProducto = new JTextField(20);
		TnombreProducto.setEditable(false);
		GridBagConstraints gbc_TnombreProducto = new GridBagConstraints();
		gbc_TnombreProducto.insets = new Insets(5, 5, 5, 0);
		gbc_TnombreProducto.gridx = 1;
		gbc_TnombreProducto.gridy = 2;
		gbc_TnombreProducto.gridwidth = 2;
		gbc_TnombreProducto.fill = GridBagConstraints.HORIZONTAL;
		panel.add(TnombreProducto, gbc_TnombreProducto);

		// --- Fila 3: Descripcion ---
		JLabel Ldescripcion = new JLabel("Descripcion:");
		GridBagConstraints gbc_ldescripcion = new GridBagConstraints();
		gbc_ldescripcion.anchor = GridBagConstraints.EAST;
		gbc_ldescripcion.insets = new Insets(0, 0, 5, 5);
		gbc_ldescripcion.gridx = 0;
		gbc_ldescripcion.gridy = 3;
		panel.add(Ldescripcion, gbc_ldescripcion);

		Tdescripcion = new JTextField(20);
		Tdescripcion.setText("");
		Tdescripcion.setEditable(false);
		GridBagConstraints gbc_tdescripcion = new GridBagConstraints();
		gbc_tdescripcion.gridwidth = 2;
		gbc_tdescripcion.insets = new Insets(5, 5, 5, 0);
		gbc_tdescripcion.fill = GridBagConstraints.HORIZONTAL;
		gbc_tdescripcion.gridx = 1;
		gbc_tdescripcion.gridy = 3;
		panel.add(Tdescripcion, gbc_tdescripcion);

		// --- Fila 4: Precio ---
		JLabel LprecioProducto = new JLabel("Precio Unitario:");
		GridBagConstraints gbc_LprecioProducto = new GridBagConstraints();
		gbc_LprecioProducto.insets = new Insets(5, 5, 5, 5);
		gbc_LprecioProducto.gridx = 0;
		gbc_LprecioProducto.gridy = 4;
		gbc_LprecioProducto.anchor = GridBagConstraints.EAST;
		panel.add(LprecioProducto, gbc_LprecioProducto);

		TprecioProducto = new JTextField(10);
		TprecioProducto.setEditable(false);
		GridBagConstraints gbc_TprecioProducto = new GridBagConstraints();
		gbc_TprecioProducto.insets = new Insets(5, 5, 5, 0);
		gbc_TprecioProducto.gridx = 1;
		gbc_TprecioProducto.gridy = 4;
		gbc_TprecioProducto.gridwidth = 2;
		gbc_TprecioProducto.fill = GridBagConstraints.HORIZONTAL;
		panel.add(TprecioProducto, gbc_TprecioProducto);

		// --- Fila 5: Stock ---
		JLabel LstockProducto = new JLabel("Stock Disponible:");
		GridBagConstraints gbc_LstockProducto = new GridBagConstraints();
		gbc_LstockProducto.insets = new Insets(5, 5, 5, 5);
		gbc_LstockProducto.gridx = 0;
		gbc_LstockProducto.gridy = 5;
		gbc_LstockProducto.anchor = GridBagConstraints.EAST;
		panel.add(LstockProducto, gbc_LstockProducto);

		TstockProducto = new JTextField(10);
		TstockProducto.setEditable(false);
		GridBagConstraints gbc_TstockProducto = new GridBagConstraints();
		gbc_TstockProducto.insets = new Insets(5, 5, 5, 0);
		gbc_TstockProducto.gridx = 1;
		gbc_TstockProducto.gridy = 5;
		gbc_TstockProducto.gridwidth = 2;
		gbc_TstockProducto.fill = GridBagConstraints.HORIZONTAL;
		panel.add(TstockProducto, gbc_TstockProducto);

		// --- Fila 6: Cantidad a Vender ---
		JLabel LcantidadAVender = new JLabel("Cantidad:");
		GridBagConstraints gbc_LcantidadAVender = new GridBagConstraints();
		gbc_LcantidadAVender.insets = new Insets(5, 5, 5, 5);
		gbc_LcantidadAVender.gridx = 0;
		gbc_LcantidadAVender.gridy = 6;
		gbc_LcantidadAVender.anchor = GridBagConstraints.EAST;
		panel.add(LcantidadAVender, gbc_LcantidadAVender);

		TcantidadAVender = new JTextField(5);
		GridBagConstraints gbc_TcantidadAVender = new GridBagConstraints();
		gbc_TcantidadAVender.insets = new Insets(5, 5, 5, 0);
		gbc_TcantidadAVender.gridx = 1;
		gbc_TcantidadAVender.gridy = 6;
		gbc_TcantidadAVender.gridwidth = 2;
		gbc_TcantidadAVender.fill = GridBagConstraints.HORIZONTAL;
		panel.add(TcantidadAVender, gbc_TcantidadAVender);

		// --- Fila 7: Botón Agregar ---
		BagregarAlCarrito = new JButton("Agregar al Carrito",
				new ImageIcon(PanelVenta.class.getResource("/Iconos/guardar.png")));
		GridBagConstraints gbc_BagregarAlCarrito = new GridBagConstraints();
		gbc_BagregarAlCarrito.insets = new Insets(5, 5, 0, 0);
		gbc_BagregarAlCarrito.gridx = 0;
		gbc_BagregarAlCarrito.gridy = 7;
		gbc_BagregarAlCarrito.gridwidth = 3;
		gbc_BagregarAlCarrito.fill = GridBagConstraints.HORIZONTAL;
		panel.add(BagregarAlCarrito, gbc_BagregarAlCarrito);

		return panel;
	}

	/**
	 * Construye el panel derecho que contiene la tabla del carrito.
	 * 
	 * @return Panel configurado con JScrollPane y JTable.
	 */
	private JPanel crearPanelCarrito() {
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.setBorder(BorderFactory.createTitledBorder("Productos en Carrito"));

		String[] columnasCarrito = { "ID", "Producto", "Descripcion", "Cantidad", "Precio Unitario", "Subtotal" };
		modeloTablaCarrito = new DefaultTableModel(columnasCarrito, 0) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tableCarrito = new JTable(modeloTablaCarrito);
		tableCarrito.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
		JScrollPane scrollPaneCarrito = new JScrollPane(tableCarrito);
		panel.add(scrollPaneCarrito, BorderLayout.CENTER);

		JPanel panelBotonesCarrito = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		BquitarDelCarrito = new JButton("Quitar del Carrito",
				new ImageIcon(PanelVenta.class.getResource("/Iconos/borrar.png")));
		panelBotonesCarrito.add(BquitarDelCarrito);
		panel.add(panelBotonesCarrito, BorderLayout.SOUTH);
		return panel;
	}

	/**
	 * Construye el panel inferior con los cálculos financieros y botones finales.
	 * <p>
	 * Se divide en dos sub-secciones:
	 * <ul>
	 * <li><b>Izquierda:</b> Campos de Subtotal, Descuento, Impuestos y Total.</li>
	 * <li><b>Derecha:</b> Selección de método de pago y botones de acción.</li>
	 * </ul>
	 * </p>
	 * 
	 * @return Panel configurado.
	 */
	private JPanel crearPanelAccionesVenta() {
		JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
		panelPrincipal.setPreferredSize(new Dimension(100, 100));
		panelPrincipal.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));

		// --- Sección 1: Desglose de Totales ---
		JPanel panelTotales = new JPanel(new GridBagLayout());

		Font fontTitulos = new Font("Tahoma", Font.BOLD, 14);
		Font fontValores = new Font("Tahoma", Font.PLAIN, 14);
		Font fontTotal = new Font("Tahoma", Font.BOLD, 24);

		// ... [Configuración de etiquetas LsubtotalVenta, TdescuentoVenta,
		// LimpuestosVenta, LtotalVenta] ...

		// --- Fila 0: Subtotal ---
		JLabel LsubtotalTitulo = new JLabel("Subtotal:");
		LsubtotalTitulo.setFont(fontTitulos);
		GridBagConstraints gbc_LsubtotalTitulo = new GridBagConstraints();
		gbc_LsubtotalTitulo.insets = new Insets(5, 5, 5, 5);
		gbc_LsubtotalTitulo.anchor = GridBagConstraints.EAST;
		gbc_LsubtotalTitulo.gridx = 0;
		gbc_LsubtotalTitulo.gridy = 0;
		panelTotales.add(LsubtotalTitulo, gbc_LsubtotalTitulo);

		LsubtotalVenta = new JLabel("$0.00");
		LsubtotalVenta.setFont(fontValores);
		GridBagConstraints gbc_LsubtotalVenta = new GridBagConstraints();
		gbc_LsubtotalVenta.insets = new Insets(5, 5, 5, 5);
		gbc_LsubtotalVenta.anchor = GridBagConstraints.WEST;
		gbc_LsubtotalVenta.gridx = 1;
		gbc_LsubtotalVenta.gridy = 0;
		panelTotales.add(LsubtotalVenta, gbc_LsubtotalVenta);

		// --- Fila 1: Descuento ---
		JLabel LdescuentoTitulo = new JLabel("Descuento ($):");
		LdescuentoTitulo.setFont(fontTitulos);
		GridBagConstraints gbc_LdescuentoTitulo = new GridBagConstraints();
		gbc_LdescuentoTitulo.insets = new Insets(5, 5, 5, 5);
		gbc_LdescuentoTitulo.anchor = GridBagConstraints.EAST;
		gbc_LdescuentoTitulo.gridx = 0;
		gbc_LdescuentoTitulo.gridy = 1;
		panelTotales.add(LdescuentoTitulo, gbc_LdescuentoTitulo);

		TdescuentoVenta = new JTextField("0.00", 8);
		TdescuentoVenta.setFont(fontValores);
		GridBagConstraints gbc_TdescuentoVenta = new GridBagConstraints();
		gbc_TdescuentoVenta.insets = new Insets(5, 5, 5, 5);
		gbc_TdescuentoVenta.anchor = GridBagConstraints.WEST;
		gbc_TdescuentoVenta.fill = GridBagConstraints.HORIZONTAL; // Hacer que llene
		gbc_TdescuentoVenta.gridx = 1;
		gbc_TdescuentoVenta.gridy = 1;
		panelTotales.add(TdescuentoVenta, gbc_TdescuentoVenta);

		// --- Fila 3: Total ---
		JLabel LtotalTitulo = new JLabel("TOTAL:");
		LtotalTitulo.setFont(fontTotal);
		GridBagConstraints gbc_LtotalTitulo = new GridBagConstraints();
		gbc_LtotalTitulo.insets = new Insets(5, 5, 5, 5);
		gbc_LtotalTitulo.anchor = GridBagConstraints.EAST;
		gbc_LtotalTitulo.gridx = 5;
		gbc_LtotalTitulo.gridy = 1;
		panelTotales.add(LtotalTitulo, gbc_LtotalTitulo);

		LtotalVenta = new JLabel("$0.00");
		LtotalVenta.setFont(fontTotal);
		GridBagConstraints gbc_LtotalVenta = new GridBagConstraints();
		gbc_LtotalVenta.insets = new Insets(5, 5, 5, 0);
		gbc_LtotalVenta.anchor = GridBagConstraints.WEST;
		gbc_LtotalVenta.gridx = 6;
		gbc_LtotalVenta.gridy = 1;
		panelTotales.add(LtotalVenta, gbc_LtotalVenta);

		// --- Fila 2: Impuestos (IVA) ---
		JLabel LimpuestosTitulo = new JLabel(String.format("Impuestos (%.0f%%):", IVA_PORCENTAJE * 100));
		LimpuestosTitulo.setFont(fontTitulos);
		GridBagConstraints gbc_LimpuestosTitulo = new GridBagConstraints();
		gbc_LimpuestosTitulo.insets = new Insets(5, 5, 0, 5);
		gbc_LimpuestosTitulo.anchor = GridBagConstraints.EAST;
		gbc_LimpuestosTitulo.gridx = 0;
		gbc_LimpuestosTitulo.gridy = 2;
		panelTotales.add(LimpuestosTitulo, gbc_LimpuestosTitulo);

		LimpuestosVenta = new JLabel("$0.00");
		LimpuestosVenta.setFont(fontValores);
		GridBagConstraints gbc_LimpuestosVenta = new GridBagConstraints();
		gbc_LimpuestosVenta.insets = new Insets(5, 5, 0, 5);
		gbc_LimpuestosVenta.anchor = GridBagConstraints.WEST;
		gbc_LimpuestosVenta.gridx = 1;
		gbc_LimpuestosVenta.gridy = 2;
		panelTotales.add(LimpuestosVenta, gbc_LimpuestosVenta);

		// --- Sección 2: Acciones y Métodos de Pago ---
		JPanel panelAcciones = new JPanel(new GridBagLayout());

		grupoMetodoPago = new ButtonGroup();

		// ... [Configuración de RadioButtons y Botones Finalizar/Cancelar] ...

		// --- Fila 0: Método de Pago ---
		JLabel LmetodoPago = new JLabel("Método de Pago:");
		LmetodoPago.setFont(fontTitulos);
		GridBagConstraints gbc_LmetodoPago = new GridBagConstraints();
		gbc_LmetodoPago.insets = new Insets(5, 10, 5, 10);
		gbc_LmetodoPago.fill = GridBagConstraints.HORIZONTAL;
		gbc_LmetodoPago.gridx = 0;
		gbc_LmetodoPago.gridy = 0;
		gbc_LmetodoPago.gridwidth = 2;
		panelAcciones.add(LmetodoPago, gbc_LmetodoPago);

		// --- Fila 2: Botón Finalizar Venta ---
		BfinalizarVenta = new JButton("Finalizar Venta",
				new ImageIcon(PanelVenta.class.getResource("/Iconos/guardar.png")));
		GridBagConstraints gbc_BfinalizarVenta = new GridBagConstraints();
		gbc_BfinalizarVenta.insets = new Insets(5, 10, 5, 10);
		gbc_BfinalizarVenta.fill = GridBagConstraints.HORIZONTAL;
		gbc_BfinalizarVenta.gridx = 2;
		gbc_BfinalizarVenta.gridy = 0;
		gbc_BfinalizarVenta.gridwidth = 2;
		panelAcciones.add(BfinalizarVenta, gbc_BfinalizarVenta);

		// Radio Buttons
		radioTarjeta = new JRadioButton("Tarjeta");
		radioTarjeta.setFont(fontValores);
		// ... constraints ...
		GridBagConstraints gbc_radioTarjeta = new GridBagConstraints();
		gbc_radioTarjeta.insets = new Insets(5, 10, 0, 10);
		gbc_radioTarjeta.fill = GridBagConstraints.HORIZONTAL;
		gbc_radioTarjeta.gridx = 0;
		gbc_radioTarjeta.gridy = 1;
		panelAcciones.add(radioTarjeta, gbc_radioTarjeta);
		grupoMetodoPago.add(radioTarjeta);

		radioEfectivo = new JRadioButton("Efectivo");
		radioEfectivo.setFont(fontValores);
		radioEfectivo.setSelected(true); // Default
		// ... constraints ...
		GridBagConstraints gbc_radioEfectivo = new GridBagConstraints();
		gbc_radioEfectivo.insets = new Insets(5, 10, 0, 10);
		gbc_radioEfectivo.fill = GridBagConstraints.HORIZONTAL;
		gbc_radioEfectivo.gridx = 1;
		gbc_radioEfectivo.gridy = 1;
		panelAcciones.add(radioEfectivo, gbc_radioEfectivo);
		grupoMetodoPago.add(radioEfectivo);

		// Botón Cancelar
		BcancelarVenta = new JButton("Cancelar Venta",
				new ImageIcon(PanelVenta.class.getResource("/Iconos/eliminar.png")));
		GridBagConstraints gbc_BcancelarVenta = new GridBagConstraints();
		gbc_BcancelarVenta.insets = new Insets(5, 10, 0, 10);
		gbc_BcancelarVenta.fill = GridBagConstraints.HORIZONTAL;
		gbc_BcancelarVenta.gridx = 2;
		gbc_BcancelarVenta.gridy = 1;
		gbc_BcancelarVenta.gridwidth = 2;
		panelAcciones.add(BcancelarVenta, gbc_BcancelarVenta);

		// Añadir paneles al panel principal
		panelPrincipal.add(panelTotales, BorderLayout.CENTER);
		panelPrincipal.add(panelAcciones, BorderLayout.EAST);

		return panelPrincipal;
	}

	/**
	 * Limpia y reinicia todos los campos para una nueva venta.
	 */
	public void limpiarCampos() {
		TbuscarProducto.setText("");
		LimagenProducto.setText("");
		TnombreProducto.setText("");
		TprecioProducto.setText("");
		TstockProducto.setText("");
		TcantidadAVender.setText("");
		Tdescripcion.setText("");
		LimagenProducto.setIcon(new ImageIcon(PanelVenta.class.getResource("/Iconos/sinImagen.png")));
		modeloTablaCarrito.setRowCount(0);

		LsubtotalVenta.setText("$0.00");
		TdescuentoVenta.setText("0.00");
		LimpuestosVenta.setText("$0.00");
		LtotalVenta.setText("$0.00");
		radioEfectivo.setSelected(true);

		if (comboCliente.getItemCount() > 0)
			comboCliente.setSelectedIndex(0);
		if (comboEmpleado.getItemCount() > 0)
			comboEmpleado.setSelectedIndex(0);
	}

	/**
	 * Actualiza las etiquetas financieras con los valores calculados.
	 * 
	 * @param subtotal   Suma de productos.
	 * @param descuento  Descuento aplicado.
	 * @param impuestos  IVA calculado.
	 * @param totalFinal Monto a cobrar.
	 */
	public void actualizarTotales(double subtotal, double descuento, double impuestos, double totalFinal) {
		LsubtotalVenta.setText(String.format("$%.2f", subtotal));

		try {
			if (Double.parseDouble(TdescuentoVenta.getText()) != descuento) {
				TdescuentoVenta.setText(String.format("%.2f", descuento));
			}
		} catch (Exception e) {
			TdescuentoVenta.setText(String.format("%.2f", descuento));
		}
		LimpuestosVenta.setText(String.format("$%.2f", impuestos));
		LtotalVenta.setText(String.format("$%.2f", totalFinal));
	}

	// --- Métodos de Interacción con el Controlador ---

	public JTextField getTdescuentoVenta() {
		return TdescuentoVenta;
	}

	public String getMetodoPagoSeleccionado() {
		if (radioEfectivo.isSelected())
			return "Efectivo";
		else if (radioTarjeta.isSelected())
			return "Tarjeta";
		else
			return "N/A";
	}

	public void addDescuentoListener(KeyListener listener) {
		TdescuentoVenta.addKeyListener(listener);
	}

	public void agregarDetalleAlCarrito(VentaDetalle detalle) {
		modeloTablaCarrito.addRow(detalle.toTableRow());
		// Limpieza rápida para el siguiente producto
		TbuscarProducto.setText("");
		Tdescripcion.setText("");
		LimagenProducto.setIcon(new ImageIcon(PanelVenta.class.getResource("/Iconos/sinImagen.png")));
		LimagenProducto.setText("");
		TnombreProducto.setText("");
		TprecioProducto.setText("");
		TstockProducto.setText("");
		TcantidadAVender.setText("");
	}

	public void quitarDetalleDelCarrito(int fila) {
		modeloTablaCarrito.removeRow(fila);
	}

	public Clientes getClienteSeleccionado() {
		return (Clientes) comboCliente.getSelectedItem();
	}

	public Empleado getEmpleadoSeleccionado() {
		return (Empleado) comboEmpleado.getSelectedItem();
	}

	public String getCodigoProductoBuscado() {
		return TbuscarProducto.getText().trim();
	}

	public int getCantidadAVender() throws NumberFormatException {
		return Integer.parseInt(TcantidadAVender.getText().trim());
	}

	public int getFilaSeleccionadaCarrito() {
		return tableCarrito.getSelectedRow();
	}

	public void mostrarDatosProducto(AlmacenProductos producto) {
		TnombreProducto.setText(producto.getNombre());
		Tdescripcion.setText(producto.getDescripcion());
		TprecioProducto.setText(String.valueOf(producto.getPrecio()));
		TstockProducto.setText(String.valueOf(producto.getCantidad()));
		TcantidadAVender.setText("1"); // Por defecto, agrega 1 unidad

		this.rutaImagenProducto = producto.getRuta();
		if (rutaImagenProducto != null && !rutaImagenProducto.isEmpty() && new File(rutaImagenProducto).exists()) {
			ImageIcon icon = new ImageIcon(rutaImagenProducto);
			Image img = icon.getImage().getScaledInstance(220, 220, Image.SCALE_SMOOTH);
			LimagenProducto.setIcon(new ImageIcon(img));
		} else {
			LimagenProducto.setIcon(new ImageIcon(PanelVenta.class.getResource("/Iconos/sinImagen.png")));
		}
	}

	// ... [Resto de Getters y Setters de Carga de Combos estándar] ...
	public void cargarClientes(List<Clientes> clientes) {
		comboCliente.removeAllItems();
		for (Clientes cliente : clientes) {
			comboCliente.addItem(cliente);
		}
		if (!clientes.isEmpty()) {
			comboCliente.setSelectedIndex(0);
		}
	}

	public void cargarEmpleados(List<Empleado> empleados) {
		comboEmpleado.removeAllItems();
		for (Empleado empleado : empleados) {
			comboEmpleado.addItem(empleado);
		}
		if (!empleados.isEmpty()) {
			comboEmpleado.setSelectedIndex(0);
		}
	}

	// Listeners
	public void addBuscarProductoListener(ActionListener listener) {
		BbuscarProducto.addActionListener(listener);
	}

	public void addAgregarCarritoListener(ActionListener listener) {
		BagregarAlCarrito.addActionListener(listener);
	}

	public void addQuitarDelCarritoListener(ActionListener listener) {
		BquitarDelCarrito.addActionListener(listener);
	}

	public void addFinalizarVentaListener(ActionListener listener) {
		BfinalizarVenta.addActionListener(listener);
	}

	public void addCancelarVentaListener(ActionListener listener) {
		BcancelarVenta.addActionListener(listener);
	}

	public void mostrarMensaje(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
	}

	public void mostrarError(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
	}
}