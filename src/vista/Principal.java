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
import modelo.Empleado;
import modelo.Categorias;
import controlador.ControladorAlmacen;
import controlador.ControladorCierreCaja;
import controlador.ControladorGestionPedidos;
import controlador.ControladorReportes;
import controlador.ControladorVenta;
import controlador.ControladorGenerico;
import javax.swing.ImageIcon;

/**
 * Ventana Principal (Dashboard) del Sistema de Punto de Venta.
 * <p>
 * Esta clase actúa como el contenedor maestro de la aplicación. Sus
 * responsabilidades son:
 * <ul>
 * <li><b>Gestión de Sesión:</b> Mantiene la referencia al {@link Empleado}
 * logueado.</li>
 * <li><b>Navegación:</b> Gestiona el menú lateral para cambiar entre módulos
 * (Ventas, Inventario, etc.).</li>
 * <li><b>Seguridad (RBAC):</b> Habilita o bloquea módulos basándose en el rol
 * del usuario (ADMIN vs otros).</li>
 * <li><b>Orquestación MVC:</b> Instancia dinámicamente los Modelos, Vistas y
 * Controladores de cada módulo al ser solicitados.</li>
 * <li><b>Reglas de Negocio Globales:</b> Valida la apertura de caja antes de
 * permitir ventas.</li>
 * </ul>
 * </p>
 * 
 * @version 1.3
 */
public class Principal extends JFrame {

	private static final long serialVersionUID = 1L;

	/** Panel central dinámico donde se cargan las vistas de cada módulo. */
	private JPanel panelContenido;

	/**
	 * Grupo de botones para asegurar que solo un módulo del menú esté activo a la
	 * vez.
	 */
	private ButtonGroup buttonGroup = new ButtonGroup();

	/**
	 * * Usuario que ha iniciado sesión. Crítico para auditoría y permisos en todos
	 * los controladores hijos.
	 */
	private Empleado usuarioActual;

	/**
	 * Constructor principal.
	 * <p>
	 * Configura la ventana (maximizada), el título personalizado con el nombre del
	 * usuario y construye el menú lateral.
	 * </p>
	 * <p>
	 * <b>Seguridad:</b> Verifica {@code usuarioActual.getRol()}. Si no es "ADMIN",
	 * deshabilita los botones de "Empleados" y "Reportes".
	 * </p>
	 * 
	 * @param usuarioLogueado Objeto empleado recibido desde el Login.
	 */
	public Principal(Empleado usuarioLogueado) {
		this.usuarioActual = usuarioLogueado; // Guardamos la sesión

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 700);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setTitle("POS-02 - Usuario: " + this.usuarioActual.getNombre());

		JPanel contentPane = new JPanel(new BorderLayout(5, 5));
		setContentPane(contentPane);

		// Configuración del Menú Lateral
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

		// Bloqueo de seguridad por Rol
		if (!"ADMIN".equalsIgnoreCase(usuarioActual.getRol())) {
			botonEmpleados.setEnabled(false);
			botonReportes.setEnabled(false);
		}
	}

	/**
	 * Crea, configura y agrega un botón al menú lateral, definiendo su lógica de
	 * navegación.
	 * <p>
	 * Este método contiene el "Switch" principal de la aplicación. Dependiendo del
	 * nombre del botón presionado, instancia la tríada MVC correspondiente (Modelo,
	 * Vista, Controlador) y carga el panel.
	 * </p>
	 * <p>
	 * <b>Lógica Especial para Ventas:</b> Antes de cargar el módulo, consulta a la
	 * BD si existe un {@link CorteCaja} abierto para hoy. Si no existe, fuerza un
	 * flujo de "Apertura de Caja" solicitando el monto inicial.
	 * </p>
	 * 
	 * @param panel  El panel contenedor del menú.
	 * @param nombre Texto del botón.
	 * @return El botón creado (útil para aplicar lógica de
	 *         habilitado/deshabilitado).
	 */
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
				// Instanciación de múltiples DAOs necesarios
				AlmacenProductosDAO productoModelo = new AlmacenProductosDAO();
				ProveedorDAO proveedorModelo = new ProveedorDAO();
				CategoriaDAO categoriaModelo = new CategoriaDAO();
				OrdenCompraDAO ordenModelo = new OrdenCompraDAO();

				// Inicialización de sub-controladores
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
				// 1. Buscamos si el usuario YA tiene una caja abierta HOY (Lógica de Negocio)
				CorteCaja corteAbierto = corteDAO.buscarCorteAbiertoHoy(this.usuarioActual.getid());

				if (corteAbierto == null) {
					// 2. NO hay caja abierta. Forzamos la apertura.
					double montoInicial = -1;
					String input = null;

					// Bucle de validación para el fondo inicial
					while (montoInicial < 0) {
						input = JOptionPane.showInputDialog(this,
								"No tienes una caja abierta para hoy.\nPor favor, ingresa el monto inicial (fondo de caja):",
								"Apertura de Caja", JOptionPane.QUESTION_MESSAGE);

						if (input == null) {
							boton.setSelected(false);
							return; // Cancelar operación
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
							montoInicial = -1;
						}
					}

					// 3. Crear el registro de corte en BD
					CorteCaja nuevoCorte = new CorteCaja(this.usuarioActual.getid(), montoInicial);
					int nuevoCorteID = corteDAO.agregar(nuevoCorte);

					if (nuevoCorteID != -1) {
						JOptionPane.showMessageDialog(this,
								String.format("Caja abierta con éxito con un fondo de $%.2f", montoInicial),
								"Caja Abierta", JOptionPane.INFORMATION_MESSAGE);
						nuevoCorte.setCorteID(nuevoCorteID);
						corteAbierto = nuevoCorte;
					} else {
						return; // Error fatal
					}

				} else {
					JOptionPane
							.showMessageDialog(this,
									String.format("Ya tienes una caja abierta con un fondo de $%.2f",
											corteAbierto.getMontoInicial()),
									"Caja Abierta", JOptionPane.INFORMATION_MESSAGE);
				}

				// Inicializar Módulo de Ventas con el ID del corte verificado
				VentaDAO ventaModelo = new VentaDAO();
				AlmacenProductosDAO productoModelo = new AlmacenProductosDAO();
				ClienteDAO clienteModelo = new ClienteDAO();
				EmpleadoDAO empleadoModelo = new EmpleadoDAO();
				PanelVenta ventaVista = new PanelVenta();

				new ControladorVenta(ventaModelo, productoModelo, clienteModelo, empleadoModelo, ventaVista,
						corteAbierto.getCorteID());

				cargarPanel(ventaVista);

			} else if ("Reportes".equals(nombre)) {
				boton.setIcon(new ImageIcon(Principal.class.getResource("/Iconos/reportes.png")));
				PanelReportes vistaReportes = new PanelReportes();
				// Instanciar todos los DAOs necesarios para reportes
				VentaDAO ventaDAO = new VentaDAO();
				VentaDetalleDAO detalleDAO = new VentaDetalleDAO();
				AlmacenProductosDAO productoDAO = new AlmacenProductosDAO();
				EntradaInventarioDAO entradaDAO = new EntradaInventarioDAO();

				new ControladorReportes(vistaReportes, ventaDAO, detalleDAO, productoDAO, entradaDAO,
						this.usuarioActual);
				cargarPanel(vistaReportes);

			} else if ("Cierre de Caja".equals(nombre)) {
				boton.setIcon(new ImageIcon(Principal.class.getResource("/Iconos/reportes.png")));

				CorteCajaDAO corteDAO = new CorteCajaDAO();
				CorteCaja corteAbierto = corteDAO.buscarCorteAbiertoHoy(this.usuarioActual.getid());

				if (corteAbierto == null) {
					JOptionPane.showMessageDialog(this, "No tienes ninguna caja abierta para cerrar el día de hoy.",
							"Cierre de Caja", JOptionPane.WARNING_MESSAGE);
					boton.setSelected(false);
				} else {
					PanelCierreCaja vistaCierre = new PanelCierreCaja();
					VentaDAO ventaDAO = new VentaDAO();
					new ControladorCierreCaja(vistaCierre, corteDAO, ventaDAO, this.usuarioActual, corteAbierto);
					cargarPanel(vistaCierre);
				}

			} else if ("Movimientos de Caja".equals(nombre)) {
				boton.setIcon(new ImageIcon(Principal.class.getResource("/Iconos/almacen.png")));

				CorteCajaDAO corteDAO = new CorteCajaDAO();
				modelo.CorteCaja corteAbierto = corteDAO.buscarCorteAbiertoHoy(this.usuarioActual.getid());

				if (corteAbierto == null) {
					JOptionPane.showMessageDialog(this,
							"Debes tener una CAJA ABIERTA para registrar movimientos.\nVe a 'Ventas' para abrir caja.",
							"Caja Cerrada", JOptionPane.WARNING_MESSAGE);
					boton.setSelected(false);
				} else {
					persistencia.MovimientoCajaDAO movDAO = new persistencia.MovimientoCajaDAO();
					vista.PanelMovimientosCaja movVista = new vista.PanelMovimientosCaja();
					new controlador.ControladorMovimientosCaja(movDAO, movVista, this.usuarioActual, corteAbierto);
					cargarPanel(movVista);
				}
			}
		});
		return boton;
	}

	/**
	 * Utilidad para intercambiar la vista mostrada en el panel central.
	 * <p>
	 * Limpia el contenido actual, agrega el nuevo panel y refresca la interfaz.
	 * </p>
	 * 
	 * @param panel El nuevo {@link JPanel} a visualizar.
	 */
	public void cargarPanel(JPanel panel) {
		panelContenido.removeAll();
		panelContenido.add(panel, BorderLayout.CENTER);
		panelContenido.revalidate();
		panelContenido.repaint();
	}
}