package vista;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.EtchedBorder;

import persistencia.AlmacenProductosDAO;
import persistencia.CategoriaDAO;
import persistencia.ClienteDAO;
import persistencia.CorteCajaDAO;
import persistencia.EmpleadoDAO;
import persistencia.EntradaInventarioDAO;
import persistencia.OrdenCompraDAO;
import persistencia.ProveedorDAO;
import persistencia.VentaDAO;
import persistencia.VentaDetalleDAO;
import modelo.Clientes;
import modelo.CorteCaja;
import modelo.Proveedor;
import modelo.Empleado; // <-- Importante
import modelo.Categorias;
import controlador.ControladorAlmacen;
import controlador.ControladorCierreCaja;
import controlador.ControladorGestionPedidos;
import controlador.ControladorReportes;
import controlador.ControladorVenta;
import controlador.ControladorGenerico;
import javax.swing.ImageIcon;

public class Principal extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel panelContenido;
	private ButtonGroup buttonGroup = new ButtonGroup();

	// Guardar el usuario que ha iniciado sesión ---
	private Empleado usuarioActual;

	public Principal(Empleado usuarioLogueado) {
		this.usuarioActual = usuarioLogueado; // Guardamos el usuario

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 700);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setTitle("POS-02 - Usuario: " + this.usuarioActual.getNombre()); // Título personalizado

		JPanel contentPane = new JPanel(new BorderLayout(5, 5));
		setContentPane(contentPane);

		JPanel panelBotones = new JPanel();
		panelBotones.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelBotones.setPreferredSize(new Dimension(180, 0));
		contentPane.add(panelBotones, BorderLayout.WEST);
		panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));
		panelBotones.add(Box.createRigidArea(new Dimension(0, 15)));

		panelContenido = new JPanel(new BorderLayout(0, 0));
		panelContenido.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		contentPane.add(panelContenido, BorderLayout.CENTER);

		// --- Creación de Botones del Menú ---
		agregarBotonMenu(panelBotones, "Clientes");
		agregarBotonMenu(panelBotones, "Proveedores");
		agregarBotonMenu(panelBotones, "Categorias");
		JToggleButton botonEmpleados = agregarBotonMenu(panelBotones, "Empleados");
		agregarBotonMenu(panelBotones, "Almacen/Productos");
		agregarBotonMenu(panelBotones, "Ventas");
		JToggleButton botonReportes = agregarBotonMenu(panelBotones, "Reportes");
		agregarBotonMenu(panelBotones, "Cierre de Caja");
		agregarBotonMenu(panelBotones, "Movimientos de Caja");
		if (!"ADMIN".equalsIgnoreCase(usuarioActual.getRol())) {
			botonEmpleados.setEnabled(false);
			botonReportes.setEnabled(false);
		}
	}

	private JToggleButton agregarBotonMenu(JPanel panel, String nombre) {
		JToggleButton boton = new JToggleButton(nombre);

		boton.setFont(new Font("Calisto MT", Font.BOLD, 13));
		boton.setMaximumSize(new Dimension(160, 40));
		boton.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonGroup.add(boton);
		panel.add(boton);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));

		boton.addActionListener(e -> {
			if ("Clientes".equals(nombre)) {
				boton.setIcon(new ImageIcon(Principal.class.getResource("/Iconos/Cliente.png")));
				ClienteDAO clienteModelo = new ClienteDAO();
				PanelCliente clienteVista = new PanelCliente();
				new ControladorGenerico<Clientes>(clienteModelo, clienteVista);
				cargarPanel(clienteVista);
			} else if ("Proveedores".equals(nombre)) {
				boton.setIcon(new ImageIcon(Principal.class.getResource("/Iconos/proveedor.png")));
				ProveedorDAO proveedorModelo = new ProveedorDAO();
				PanelProveedor proveedorVista = new PanelProveedor();
				new ControladorGenerico<Proveedor>(proveedorModelo, proveedorVista);
				cargarPanel(proveedorVista);
			} else if ("Categorias".equals(nombre)) {
				boton.setIcon(new ImageIcon(Principal.class.getResource("/Iconos/categorias.png")));
				CategoriaDAO categoriaModelo = new CategoriaDAO();
				PanelCategoria categoriaVista = new PanelCategoria();
				new ControladorGenerico<Categorias>(categoriaModelo, categoriaVista);
				cargarPanel(categoriaVista);
			} else if ("Empleados".equals(nombre)) {
				boton.setIcon(new ImageIcon(Principal.class.getResource("/Iconos/empleados.png")));
				EmpleadoDAO empleadoModelo = new EmpleadoDAO();
				PanelEmpleado empleadoVista = new PanelEmpleado();
				new ControladorGenerico<Empleado>(empleadoModelo, empleadoVista);
				cargarPanel(empleadoVista);
			} else if ("Almacen/Productos".equals(nombre)) {
				boton.setIcon(new ImageIcon(Principal.class.getResource("/Iconos/almacen.png")));
				ModuloAlmacen moduloAlmacenVista = new ModuloAlmacen();
				AlmacenProductosDAO productoModelo = new AlmacenProductosDAO();
				ProveedorDAO proveedorModelo = new ProveedorDAO();
				CategoriaDAO categoriaModelo = new CategoriaDAO();
				OrdenCompraDAO ordenModelo = new OrdenCompraDAO();

				PanelAlmacenProductos productoVista = moduloAlmacenVista.getPanelProductos();
				new ControladorAlmacen(productoModelo, proveedorModelo, productoVista, categoriaModelo,
						this.usuarioActual);
				PanelGestionPedidos pedidoVista = moduloAlmacenVista.getPanelPedidos();
				new ControladorGestionPedidos(ordenModelo, pedidoVista, this.usuarioActual, productoModelo,
						proveedorModelo);
				cargarPanel(moduloAlmacenVista);
			} else if ("Ventas".equals(nombre)) {
				boton.setIcon(new ImageIcon(Principal.class.getResource("/Iconos/venta.png")));

				CorteCajaDAO corteDAO = new CorteCajaDAO();
				// 1. Buscamos si el usuario YA tiene una caja abierta HOY
				CorteCaja corteAbierto = corteDAO.buscarCorteAbiertoHoy(this.usuarioActual.getid());

				if (corteAbierto == null) {
					// 2. NO hay caja abierta. Forzamos la apertura.
					double montoInicial = -1;
					String input = null;

					// Pedimos el monto inicial hasta que sea válido o cancele
					while (montoInicial < 0) {
						input = JOptionPane.showInputDialog(this,
								"No tienes una caja abierta para hoy.\nPor favor, ingresa el monto inicial (fondo de caja):",
								"Apertura de Caja", JOptionPane.QUESTION_MESSAGE);

						if (input == null) {
							// El usuario presionó "Cancelar"
							boton.setSelected(false); // Deseleccionamos el botón del menú
							return; // Salimos, no abrimos el panel de ventas
						}

						try {
							montoInicial = Double.parseDouble(input);
							if (montoInicial < 0) {
								JOptionPane.showMessageDialog(this, "El monto no puede ser negativo.", "Error",
										JOptionPane.ERROR_MESSAGE);
							}
						} catch (NumberFormatException ex) {
							JOptionPane.showMessageDialog(this, "Por favor, ingresa un número válido.", "Error",
									JOptionPane.ERROR_MESSAGE);
							montoInicial = -1; // Para que el bucle continúe
						}
					}

					// 3. Si el monto es válido, creamos y guardamos el corte
					CorteCaja nuevoCorte = new CorteCaja(this.usuarioActual.getid(), montoInicial);
					int nuevoCorteID = corteDAO.agregar(nuevoCorte);

					if (nuevoCorteID != -1) {
						JOptionPane.showMessageDialog(this,
								String.format("Caja abierta con éxito con un fondo de $%.2f", montoInicial),
								"Caja Abierta", JOptionPane.INFORMATION_MESSAGE);
						nuevoCorte.setCorteID(nuevoCorteID);
						corteAbierto = nuevoCorte; // Asignamos el nuevo corte para usarlo
					} else {
						JOptionPane.showMessageDialog(this, "Error fatal al abrir la caja. No se puede continuar.",
								"Error", JOptionPane.ERROR_MESSAGE);
						return; // No continuamos
					}

				} else {
					// 4. SÍ hay una caja abierta. Solo informamos.
					JOptionPane
							.showMessageDialog(this,
									String.format("Ya tienes una caja abierta con un fondo de $%.2f",
											corteAbierto.getMontoInicial()),
									"Caja Abierta", JOptionPane.INFORMATION_MESSAGE);
				}

				VentaDAO ventaModelo = new VentaDAO();
				AlmacenProductosDAO productoModelo = new AlmacenProductosDAO();
				ClienteDAO clienteModelo = new ClienteDAO();
				EmpleadoDAO empleadoModelo = new EmpleadoDAO();
				PanelVenta ventaVista = new PanelVenta();

				// Pasamos el ID del corte actual al controlador de ventas
				new ControladorVenta(ventaModelo, productoModelo, clienteModelo, empleadoModelo, ventaVista,
						corteAbierto.getCorteID());

				cargarPanel(ventaVista);

			} else if ("Reportes".equals(nombre)) {
				boton.setIcon(new ImageIcon(Principal.class.getResource("/Iconos/reportes.png")));
				PanelReportes vistaReportes = new PanelReportes();
				VentaDAO ventaDAO = new VentaDAO();
				VentaDetalleDAO detalleDAO = new VentaDetalleDAO();
				AlmacenProductosDAO productoDAO = new AlmacenProductosDAO();
				EntradaInventarioDAO entradaDAO = new EntradaInventarioDAO();
				// Pasa el usuarioActual al ControladorReportes
				new ControladorReportes(vistaReportes, ventaDAO, detalleDAO, productoDAO, entradaDAO,
						this.usuarioActual);
				cargarPanel(vistaReportes);
			} else if ("Cierre de Caja".equals(nombre)) {
				boton.setIcon(new ImageIcon(Principal.class.getResource("/Iconos/reportes.png"))); // (Puedes usar otro
																									// ícono)

				// 1. Verificamos si hay una caja abierta para este usuario HOY
				CorteCajaDAO corteDAO = new CorteCajaDAO();
				CorteCaja corteAbierto = corteDAO.buscarCorteAbiertoHoy(this.usuarioActual.getid());

				if (corteAbierto == null) {
					// 2. Si no hay, mostramos un error y no cargamos nada
					JOptionPane.showMessageDialog(this, "No tienes ninguna caja abierta para cerrar el día de hoy.",
							"Cierre de Caja", JOptionPane.WARNING_MESSAGE);
					boton.setSelected(false); // Deseleccionamos el botón
				} else {
					// 3. Si encontramos una caja, creamos la vista y el controlador
					PanelCierreCaja vistaCierre = new PanelCierreCaja();
					VentaDAO ventaDAO = new VentaDAO(); // El controlador lo necesitará

					// Creamos el nuevo controlador (¡QUE AÚN NO HEMOS HECHO!)
					new ControladorCierreCaja(vistaCierre, corteDAO, ventaDAO, this.usuarioActual, corteAbierto);

					cargarPanel(vistaCierre);
				}
			} else if ("Movimientos de Caja".equals(nombre)) {
				boton.setIcon(new ImageIcon(Principal.class.getResource("/Iconos/almacen.png"))); // Cambia el ícono si
																									// quieres

				// 1. Verificar si hay caja abierta (Igual que en Ventas y Cierre)
				CorteCajaDAO corteDAO = new CorteCajaDAO();
				// Reusamos el método buscarCorteAbiertoHoy
				modelo.CorteCaja corteAbierto = corteDAO.buscarCorteAbiertoHoy(this.usuarioActual.getid());

				if (corteAbierto == null) {
					JOptionPane.showMessageDialog(this,
							"Debes tener una CAJA ABIERTA para registrar movimientos.\nVe a 'Ventas' para abrir caja.",
							"Caja Cerrada", JOptionPane.WARNING_MESSAGE);
					boton.setSelected(false);
				} else {
					// 2. Si hay caja, abrimos el panel de movimientos
					persistencia.MovimientoCajaDAO movDAO = new persistencia.MovimientoCajaDAO();
					vista.PanelMovimientosCaja movVista = new vista.PanelMovimientosCaja();

					new controlador.ControladorMovimientosCaja(movDAO, movVista, this.usuarioActual, corteAbierto);

					cargarPanel(movVista);
				}
			}
		});
		return boton;
	}

	public void cargarPanel(JPanel panel) {
		panelContenido.removeAll();
		panelContenido.add(panel, BorderLayout.CENTER);
		panelContenido.revalidate();
		panelContenido.repaint();
	}
}