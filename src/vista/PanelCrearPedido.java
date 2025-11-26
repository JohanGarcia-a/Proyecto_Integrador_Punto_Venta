package vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import modelo.AlmacenProductos;
import modelo.OrdenCompraDetalle;
import modelo.Proveedor;

public class PanelCrearPedido extends JPanel {

	private static final long serialVersionUID = 1L;

	private JComboBox<Proveedor> comboProveedor;
	private JLabel Lfecha;

	private JTextField TbuscarProducto;
	private JButton BbuscarProducto;
	private JLabel LimagenProducto;
	private JTextField TnombreProducto;
	private JTextField Tdescripcion;
	private JTextField TcostoProducto;
	private JTextField TstockProducto;
	private JTextField TcantidadAPedir;
	private JButton BagregarAlPedido;

	private DefaultTableModel modeloTablaPedido;
	private JTable tablePedido;
	private JButton BquitarDelPedido;

	private JLabel LtotalPedido;
	private JButton BfinalizarPedido;
	private JButton BcancelarPedido;

	public PanelCrearPedido() {
		setLayout(new BorderLayout(10, 10));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		this.setPreferredSize(new Dimension(950, 620));

		// --- Panel Superior: Info Proveedor/Fecha ---
		JPanel panelInfoGeneral = crearPanelInfoGeneral();
		add(panelInfoGeneral, BorderLayout.NORTH);

		// --- Panel Central: Búsqueda de Productos y Carrito ---
		JPanel panelCentral = new JPanel(new GridBagLayout());

		// Panel Izquierdo: Búsqueda y detalles del producto
		JPanel panelProducto = crearPanelBusquedaProducto();
		GridBagConstraints gbc_panelProducto = new GridBagConstraints();
		gbc_panelProducto.gridx = 0;
		gbc_panelProducto.gridy = 0;
		gbc_panelProducto.weightx = 0.4;
		gbc_panelProducto.fill = GridBagConstraints.BOTH;
		gbc_panelProducto.insets = new Insets(0, 0, 5, 10);
		panelCentral.add(panelProducto, gbc_panelProducto);

		// Panel Derecho: Carrito de compras
		JPanel panelCarrito = crearPanelCarrito();
		GridBagConstraints gbc_panelCarrito = new GridBagConstraints();
		gbc_panelCarrito.insets = new Insets(0, 0, 5, 0);
		gbc_panelCarrito.gridx = 1;
		gbc_panelCarrito.gridy = 0;
		gbc_panelCarrito.weightx = 0.6;
		gbc_panelCarrito.fill = GridBagConstraints.BOTH;
		panelCentral.add(panelCarrito, gbc_panelCarrito);

		add(panelCentral, BorderLayout.CENTER);

		// --- Panel Inferior: Total y Botones de Acción ---
		JPanel panelAcciones = crearPanelAcciones();
		add(panelAcciones, BorderLayout.SOUTH);

		limpiarCampos();
	}

	private JPanel crearPanelInfoGeneral() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		panel.setBorder(BorderFactory.createTitledBorder("Información del Pedido"));

		panel.add(new JLabel("Proveedor:"));
		comboProveedor = new JComboBox<>();
		comboProveedor.setPreferredSize(new Dimension(200, 25));
		panel.add(comboProveedor);

		panel.add(new JLabel("Fecha:"));
		Lfecha = new JLabel();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Lfecha.setText(sdf.format(new Date()));
		panel.add(Lfecha);

		return panel;
	}

	private JPanel crearPanelBusquedaProducto() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Buscar Producto"));

		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWeights = new double[] { 0.0, 1.0, 0.0 };
		panel.setLayout(gbl_panel);

		// --- Fila 0: Búsqueda ---
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
		gbc_TbuscarProducto.fill = GridBagConstraints.HORIZONTAL;
		panel.add(TbuscarProducto, gbc_TbuscarProducto);

		BbuscarProducto = new JButton("Buscar", new ImageIcon(PanelVenta.class.getResource("/Iconos/buscar.png")));
		GridBagConstraints gbc_BbuscarProducto = new GridBagConstraints();
		gbc_BbuscarProducto.insets = new Insets(5, 5, 5, 5);
		gbc_BbuscarProducto.gridx = 2;
		gbc_BbuscarProducto.gridy = 0;
		panel.add(BbuscarProducto, gbc_BbuscarProducto);

		// --- Fila 1: Imagen ---
		LimagenProducto = new JLabel("");
		LimagenProducto.setIcon(new ImageIcon(PanelVenta.class.getResource("/Iconos/sinImagen.png")));
		LimagenProducto.setBorder(BorderFactory.createEtchedBorder());
		LimagenProducto.setHorizontalAlignment(SwingConstants.CENTER);
		LimagenProducto.setPreferredSize(new Dimension(220, 220));
		GridBagConstraints gbc_LimagenProducto = new GridBagConstraints();
		gbc_LimagenProducto.insets = new Insets(5, 5, 5, 5);
		gbc_LimagenProducto.gridx = 0;
		gbc_LimagenProducto.gridy = 1;
		gbc_LimagenProducto.gridwidth = 3;
		panel.add(LimagenProducto, gbc_LimagenProducto);

		// --- Fila 2: Nombre ---
		JLabel Lnombre = new JLabel("Nombre:");
		GridBagConstraints gbc_Lnombre = new GridBagConstraints();
		gbc_Lnombre.insets = new Insets(5, 5, 5, 5);
		gbc_Lnombre.gridx = 0;
		gbc_Lnombre.gridy = 2;
		gbc_Lnombre.anchor = GridBagConstraints.EAST;
		panel.add(Lnombre, gbc_Lnombre);

		TnombreProducto = new JTextField(20);
		TnombreProducto.setEditable(false);
		GridBagConstraints gbc_TnombreProducto = new GridBagConstraints();
		gbc_TnombreProducto.insets = new Insets(5, 5, 5, 5);
		gbc_TnombreProducto.gridx = 1;
		gbc_TnombreProducto.gridy = 2;
		gbc_TnombreProducto.gridwidth = 2;
		gbc_TnombreProducto.fill = GridBagConstraints.HORIZONTAL;
		panel.add(TnombreProducto, gbc_TnombreProducto);

		// --- Fila 3: Descripcion --- (PETICIÓN AÑADIDA)
		JLabel Ldescripcion = new JLabel("Descripcion:");
		GridBagConstraints gbc_Ldescripcion = new GridBagConstraints();
		gbc_Ldescripcion.insets = new Insets(5, 5, 5, 5);
		gbc_Ldescripcion.gridx = 0;
		gbc_Ldescripcion.gridy = 3;
		gbc_Ldescripcion.anchor = GridBagConstraints.EAST;
		panel.add(Ldescripcion, gbc_Ldescripcion);

		Tdescripcion = new JTextField(20);
		Tdescripcion.setEditable(false);
		GridBagConstraints gbc_Tdescripcion = new GridBagConstraints();
		gbc_Tdescripcion.insets = new Insets(5, 5, 5, 5);
		gbc_Tdescripcion.gridx = 1;
		gbc_Tdescripcion.gridy = 3;
		gbc_Tdescripcion.gridwidth = 2;
		gbc_Tdescripcion.fill = GridBagConstraints.HORIZONTAL;
		panel.add(Tdescripcion, gbc_Tdescripcion);

		// --- Fila 4: Costo ---
		JLabel Lcosto = new JLabel("Costo Unitario:");
		GridBagConstraints gbc_Lcosto = new GridBagConstraints();
		gbc_Lcosto.insets = new Insets(5, 5, 5, 5);
		gbc_Lcosto.gridx = 0;
		gbc_Lcosto.gridy = 4;
		gbc_Lcosto.anchor = GridBagConstraints.EAST;
		panel.add(Lcosto, gbc_Lcosto);

		TcostoProducto = new JTextField(10);
		TcostoProducto.setEditable(true); // Permitir editar el costo de compra
		GridBagConstraints gbc_TcostoProducto = new GridBagConstraints();
		gbc_TcostoProducto.insets = new Insets(5, 5, 5, 5);
		gbc_TcostoProducto.gridx = 1;
		gbc_TcostoProducto.gridy = 4;
		gbc_TcostoProducto.gridwidth = 2;
		gbc_TcostoProducto.fill = GridBagConstraints.HORIZONTAL;
		panel.add(TcostoProducto, gbc_TcostoProducto);

		// --- Fila 5: Stock ---
		JLabel Lstock = new JLabel("Stock Actual:");
		GridBagConstraints gbc_Lstock = new GridBagConstraints();
		gbc_Lstock.insets = new Insets(5, 5, 5, 5);
		gbc_Lstock.gridx = 0;
		gbc_Lstock.gridy = 5;
		gbc_Lstock.anchor = GridBagConstraints.EAST;
		panel.add(Lstock, gbc_Lstock);

		TstockProducto = new JTextField(10);
		TstockProducto.setEditable(false);
		GridBagConstraints gbc_TstockProducto = new GridBagConstraints();
		gbc_TstockProducto.insets = new Insets(5, 5, 5, 5);
		gbc_TstockProducto.gridx = 1;
		gbc_TstockProducto.gridy = 5;
		gbc_TstockProducto.gridwidth = 2;
		gbc_TstockProducto.fill = GridBagConstraints.HORIZONTAL;
		panel.add(TstockProducto, gbc_TstockProducto);

		// --- Fila 6: Cantidad a Pedir ---
		JLabel Lcantidad = new JLabel("Cantidad a Pedir:");
		GridBagConstraints gbc_Lcantidad = new GridBagConstraints();
		gbc_Lcantidad.insets = new Insets(5, 5, 5, 5);
		gbc_Lcantidad.gridx = 0;
		gbc_Lcantidad.gridy = 6;
		gbc_Lcantidad.anchor = GridBagConstraints.EAST;
		panel.add(Lcantidad, gbc_Lcantidad);

		TcantidadAPedir = new JTextField(5);
		GridBagConstraints gbc_TcantidadAPedir = new GridBagConstraints();
		gbc_TcantidadAPedir.insets = new Insets(5, 5, 5, 5);
		gbc_TcantidadAPedir.gridx = 1;
		gbc_TcantidadAPedir.gridy = 6;
		gbc_TcantidadAPedir.gridwidth = 2;
		gbc_TcantidadAPedir.fill = GridBagConstraints.HORIZONTAL;
		panel.add(TcantidadAPedir, gbc_TcantidadAPedir);

		// --- Fila 7: Botón Agregar ---
		BagregarAlPedido = new JButton("Agregar al Pedido",
				new ImageIcon(PanelVenta.class.getResource("/Iconos/guardar.png")));
		GridBagConstraints gbc_BagregarAlPedido = new GridBagConstraints();
		gbc_BagregarAlPedido.insets = new Insets(5, 5, 5, 5);
		gbc_BagregarAlPedido.gridx = 0;
		gbc_BagregarAlPedido.gridy = 7;
		gbc_BagregarAlPedido.gridwidth = 3;
		gbc_BagregarAlPedido.fill = GridBagConstraints.HORIZONTAL;
		panel.add(BagregarAlPedido, gbc_BagregarAlPedido);

		return panel;
	}

	private JPanel crearPanelCarrito() {
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.setBorder(BorderFactory.createTitledBorder("Productos en Pedido"));

		// --- ARREGLO DE TABLA ---
		String[] columnas = { "ID", "Producto", "Descripcion", "Cantidad", "Costo Unit.", "Subtotal" };
		// -------------------------

		modeloTablaPedido = new DefaultTableModel(columnas, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tablePedido = new JTable(modeloTablaPedido);
		tablePedido.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
		panel.add(new JScrollPane(tablePedido), BorderLayout.CENTER);

		JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		BquitarDelPedido = new JButton("Quitar del Pedido",
				new ImageIcon(PanelVenta.class.getResource("/Iconos/borrar.png")));
		panelBotones.add(BquitarDelPedido);
		panel.add(panelBotones, BorderLayout.SOUTH);

		return panel;
	}

	private JPanel crearPanelAcciones() {
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.setBorder(BorderFactory.createEtchedBorder());

		JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		LtotalPedido = new JLabel("Total Pedido: $0.00");
		LtotalPedido.setFont(LtotalPedido.getFont().deriveFont(Font.BOLD, 24f));
		panelTotal.add(LtotalPedido);
		panel.add(panelTotal, BorderLayout.CENTER);

		JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		BfinalizarPedido = new JButton("Finalizar Pedido",
				new ImageIcon(PanelVenta.class.getResource("/Iconos/guardar.png")));
		BcancelarPedido = new JButton("Cancelar Pedido",
				new ImageIcon(PanelVenta.class.getResource("/Iconos/eliminar.png")));
		panelBotones.add(BfinalizarPedido);
		panelBotones.add(BcancelarPedido);
		panel.add(panelBotones, BorderLayout.EAST);

		return panel;
	}

	// --- Métodos Públicos (para el Controlador) ---

	public void mostrarMensaje(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
	}

	public void mostrarError(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
	}

	public void limpiarCampos() {
		TbuscarProducto.setText("");
		LimagenProducto.setIcon(new ImageIcon(PanelVenta.class.getResource("/Iconos/sinImagen.png")));
		TnombreProducto.setText("");
		Tdescripcion.setText(""); // <-- ARREGLO AQUÍ
		TcostoProducto.setText("");
		TstockProducto.setText("");
		TcantidadAPedir.setText("");
		modeloTablaPedido.setRowCount(0);
		LtotalPedido.setText("Total Pedido: $0.00");
		comboProveedor.setEnabled(true);
		if (comboProveedor.getItemCount() > 0)
			comboProveedor.setSelectedIndex(0);
	}

	public void cargarProveedores(List<Proveedor> proveedores) {
		comboProveedor.removeAllItems();
		for (Proveedor p : proveedores)
			comboProveedor.addItem(p);
		if (!proveedores.isEmpty())
			comboProveedor.setSelectedIndex(0);
	}

	public void mostrarDatosProducto(AlmacenProductos producto) {
		TnombreProducto.setText(producto.getNombre());
		Tdescripcion.setText(producto.getDescripcion());

		TcostoProducto.setText(String.valueOf(producto.getPrecio()));
		TstockProducto.setText(String.valueOf(producto.getCantidad()));
		TcantidadAPedir.setText("1");

		if (comboProveedor.isEnabled()) {
			int proveedorIdDelProducto = producto.getProveedorId();
			for (int i = 0; i < comboProveedor.getItemCount(); i++) {
				Proveedor p = comboProveedor.getItemAt(i);
				if (p.getid() == proveedorIdDelProducto) {
					comboProveedor.setSelectedItem(p);
					break;
				}
			}
		}

		String rutaImagen = producto.getRuta();
		if (rutaImagen != null && !rutaImagen.isEmpty() && new File(rutaImagen).exists()) {
			ImageIcon icon = new ImageIcon(rutaImagen);
			Image img = icon.getImage().getScaledInstance(220, 220, Image.SCALE_SMOOTH);
			LimagenProducto.setIcon(new ImageIcon(img));
			LimagenProducto.setText("");
		} else {
			LimagenProducto.setIcon(new ImageIcon(PanelCrearPedido.class.getResource("/Iconos/sinImagen.png")));
			LimagenProducto.setText("");
		}

	}

	public void agregarDetalleAlPedido(OrdenCompraDetalle detalle) {
		modeloTablaPedido.addRow(detalle.toTableRow());
		
		TbuscarProducto.setText("");
		LimagenProducto.setIcon(new ImageIcon(PanelVenta.class.getResource("/Iconos/sinImagen.png")));
		TnombreProducto.setText("");
		Tdescripcion.setText(""); 
		TcostoProducto.setText("");
		TstockProducto.setText("");
		TcantidadAPedir.setText("");
	}

	public void quitarDetalleDelPedido(int fila) {
		modeloTablaPedido.removeRow(fila);
	}

	public void actualizarTotalPedido(double total) {
		LtotalPedido.setText(String.format("Total Pedido: $%.2f", total));
	}

	// --- Getters para el Controlador ---

	public Proveedor getProveedorSeleccionado() {
		return (Proveedor) comboProveedor.getSelectedItem();
	}

	public void setProveedorComboBoxEnabled(boolean enabled) {
		comboProveedor.setEnabled(enabled);
	}

	public String getCodigoProductoBuscado() {
		return TbuscarProducto.getText().trim();
	}

	public int getCantidadAPedir() throws NumberFormatException {
		return Integer.parseInt(TcantidadAPedir.getText().trim());
	}

	public int getFilaSeleccionadaPedido() {
		return tablePedido.getSelectedRow();
	}

	public JTextField getTcostoProducto() {
		return TcostoProducto;
	}

	// --- Listeners para el Controlador ---

	public void addBuscarProductoListener(ActionListener listener) {
		BbuscarProducto.addActionListener(listener);
	}

	public void addAgregarAlPedidoListener(ActionListener listener) {
		BagregarAlPedido.addActionListener(listener);
	}

	public void addQuitarDelPedidoListener(ActionListener listener) {
		BquitarDelPedido.addActionListener(listener);
	}

	public void addFinalizarPedidoListener(ActionListener listener) {
		BfinalizarPedido.addActionListener(listener);
	}

	public void addCancelarPedidoListener(ActionListener listener) {
		BcancelarPedido.addActionListener(listener);
	}
}