package vista;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * Panel de interfaz gráfica para realizar el Cierre de Caja (Corte de Turno).
 * <p>
 * Proporciona una vista resumida de las finanzas del turno actual (Fondo
 * inicial, Ventas en efectivo, Ventas con tarjeta) e implementa la
 * funcionalidad de <b>Arqueo Ciego</b>, donde el usuario debe ingresar el monto
 * físico contado para que el sistema calcule la diferencia (sobrante o
 * faltante).
 * </p>
 * 
 * @version 1.1
 */
public class PanelCierreCaja extends JPanel {

	private static final long serialVersionUID = 1L;

	// --- Componentes Visuales Informativos ---

	/** Etiqueta para mostrar el nombre del cajero responsable. */
	private JLabel LblUsuario;

	/** Etiqueta para mostrar la fecha y hora en que se abrió la caja. */
	private JLabel LblFechaApertura;

	/** Etiqueta para mostrar el fondo inicial de caja. */
	private JLabel LblMontoInicial;

	/** Etiqueta para mostrar el total de ventas cobradas en efectivo. */
	private JLabel LblVentasEfectivo;

	/**
	 * Etiqueta informativa para mostrar ventas con tarjeta (no afecta el arqueo de
	 * efectivo).
	 */
	private JLabel LblVentasTarjeta;

	/**
	 * Etiqueta que muestra la suma teórica que debería haber en caja (Inicial +
	 * Ventas Efectivo).
	 */
	private JLabel LblTotalSistema;

	/**
	 * Etiqueta dinámica que muestra el resultado del arqueo (Contado - Sistema).
	 */
	private JLabel LblDiferencia;

	// --- Componentes de Interacción ---

	/** Campo de texto donde el usuario ingresa el dinero físico contado. */
	private JTextField TxtMontoContado;

	/** Botón para confirmar el cierre y guardar el registro. */
	private JButton BtnCerrarCaja;

	/**
	 * Constructor.
	 * <p>
	 * Inicializa el panel, configura el diseño {@link GridBagLayout} y añade todos
	 * los componentes visuales (etiquetas y campos) en sus posiciones
	 * correspondientes.
	 * </p>
	 */
	public PanelCierreCaja() {
		setLayout(new BorderLayout(10, 10));
		setBorder(new TitledBorder("Cierre de Caja"));

		// Panel principal interno con GridBagLayout para alineación precisa
		JPanel panelFormulario = new JPanel(new GridBagLayout());

		Font fontTitulo = new Font("Tahoma", Font.BOLD, 14);
		Font fontValor = new Font("Tahoma", Font.PLAIN, 14);
		Font fontTotal = new Font("Tahoma", Font.BOLD, 16);

		// --- Configuración de filas del formulario ---

		// Fila 0: Usuario
		JLabel LblUsuarioTitulo = new JLabel("Usuario:");
		LblUsuarioTitulo.setFont(fontTitulo);
		GridBagConstraints gbc_LblUsuarioTitulo = new GridBagConstraints();
		gbc_LblUsuarioTitulo.insets = new Insets(10, 10, 10, 10);
		gbc_LblUsuarioTitulo.anchor = GridBagConstraints.WEST;
		gbc_LblUsuarioTitulo.gridx = 0;
		gbc_LblUsuarioTitulo.gridy = 0;
		panelFormulario.add(LblUsuarioTitulo, gbc_LblUsuarioTitulo);

		LblUsuario = new JLabel("---");
		LblUsuario.setFont(fontValor);
		GridBagConstraints gbc_LblUsuario = new GridBagConstraints();
		gbc_LblUsuario.insets = new Insets(10, 10, 10, 10);
		gbc_LblUsuario.anchor = GridBagConstraints.WEST;
		gbc_LblUsuario.gridx = 1;
		gbc_LblUsuario.gridy = 0;
		panelFormulario.add(LblUsuario, gbc_LblUsuario);

		// ... [Se omite la configuración repetitiva de Fecha, Monto Inicial, Ventas
		// Efectivo para brevedad] ...
		// (El código completo incluye la configuración de GridBagConstraints para cada
		// etiqueta)

		// Fila 1: Fecha Apertura
		JLabel LblFechaAperturaTitulo = new JLabel("Fecha Apertura:");
		LblFechaAperturaTitulo.setFont(fontTitulo);
		// ... configuración constraints ...
		panelFormulario.add(LblFechaAperturaTitulo, crearGBC(0, 1)); // Método auxiliar imaginario para docs

		LblFechaApertura = new JLabel("---");
		LblFechaApertura.setFont(fontValor);
		panelFormulario.add(LblFechaApertura, crearGBC(1, 1));

		// Fila 2: Monto Inicial
		JLabel LblMontoInicialTitulo = new JLabel("Monto Inicial (Fondo):");
		LblMontoInicialTitulo.setFont(fontTitulo);
		panelFormulario.add(LblMontoInicialTitulo, crearGBC(0, 2));

		LblMontoInicial = new JLabel("$0.00");
		LblMontoInicial.setFont(fontValor);
		panelFormulario.add(LblMontoInicial, crearGBC(1, 2));

		// Fila 3: Ventas Efectivo
		JLabel LblVentasEfectivoTitulo = new JLabel("Ventas en Efectivo:");
		LblVentasEfectivoTitulo.setFont(fontTitulo);
		panelFormulario.add(LblVentasEfectivoTitulo, crearGBC(0, 3));

		LblVentasEfectivo = new JLabel("$0.00");
		LblVentasEfectivo.setFont(fontValor);
		panelFormulario.add(LblVentasEfectivo, crearGBC(1, 3));

		// --- Fila 4: Total Esperado (Sistema) ---
		JLabel LblTotalSistemaTitulo = new JLabel("Total Esperado en Caja:");
		LblTotalSistemaTitulo.setFont(fontTotal);
		panelFormulario.add(LblTotalSistemaTitulo, crearGBC(0, 4));

		LblTotalSistema = new JLabel("$0.00");
		LblTotalSistema.setFont(fontTotal);
		panelFormulario.add(LblTotalSistema, crearGBC(1, 4));

		// --- Fila 5: Entrada de Monto Físico (Interacción Usuario) ---
		JLabel LblMontoContadoTitulo = new JLabel("Monto Físico Contado:");
		LblMontoContadoTitulo.setFont(fontTotal);
		panelFormulario.add(LblMontoContadoTitulo, crearGBC(0, 5));

		TxtMontoContado = new JTextField(10);
		TxtMontoContado.setFont(fontTotal);
		GridBagConstraints gbc_TxtMontoContado = new GridBagConstraints();
		gbc_TxtMontoContado.insets = new Insets(10, 10, 10, 10);
		gbc_TxtMontoContado.anchor = GridBagConstraints.WEST;
		gbc_TxtMontoContado.fill = GridBagConstraints.HORIZONTAL;
		gbc_TxtMontoContado.gridx = 1;
		gbc_TxtMontoContado.gridy = 5;
		panelFormulario.add(TxtMontoContado, gbc_TxtMontoContado);

		// --- Fila 6: Diferencia Calculada ---
		JLabel LblDiferenciaTitulo = new JLabel("Diferencia (Faltante/Sobrante):");
		LblDiferenciaTitulo.setFont(fontTotal);
		panelFormulario.add(LblDiferenciaTitulo, crearGBC(0, 6));

		LblDiferencia = new JLabel("$0.00");
		LblDiferencia.setFont(fontTotal);
		panelFormulario.add(LblDiferencia, crearGBC(1, 6));

		// Fila 7: Info Tarjeta
		JLabel LblVentasTarjetaTitulo = new JLabel("(Ventas con Tarjeta (Info):");
		LblVentasTarjetaTitulo.setFont(fontTitulo);
		panelFormulario.add(LblVentasTarjetaTitulo, crearGBC(0, 7));

		LblVentasTarjeta = new JLabel("$0.00");
		LblVentasTarjeta.setFont(fontValor);
		panelFormulario.add(LblVentasTarjeta, crearGBC(1, 7));

		// --- Fila 8: Botón de Acción ---
		BtnCerrarCaja = new JButton("Confirmar y Cerrar Caja");
		BtnCerrarCaja.setFont(fontTotal);
		GridBagConstraints gbc_BtnCerrarCaja = new GridBagConstraints();
		gbc_BtnCerrarCaja.insets = new Insets(10, 10, 10, 10);
		gbc_BtnCerrarCaja.gridx = 0;
		gbc_BtnCerrarCaja.gridy = 8;
		gbc_BtnCerrarCaja.gridwidth = 2;
		gbc_BtnCerrarCaja.anchor = GridBagConstraints.CENTER;
		panelFormulario.add(BtnCerrarCaja, gbc_BtnCerrarCaja);

		add(panelFormulario, BorderLayout.CENTER);
	}

	// Método auxiliar para reducir código en docs (no existe en la clase real, es
	// solo para limpiar el ejemplo)
	private GridBagConstraints crearGBC(int x, int y) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = x;
		gbc.gridy = y;
		return gbc;
	}

	// --- Métodos de Interfaz para el Controlador ---

	public void setDatosApertura(String usuario, String fechaApertura, double montoInicial) {
		LblUsuario.setText(usuario);
		LblFechaApertura.setText(fechaApertura);
		LblMontoInicial.setText(String.format("$%.2f", montoInicial));
	}

	public void setTotalesSistema(double ventasEfectivo, double ventasTarjeta, double totalEsperado) {
		LblVentasEfectivo.setText(String.format("$%.2f", ventasEfectivo));
		LblVentasTarjeta.setText(String.format("$%.2f", ventasTarjeta));
		LblTotalSistema.setText(String.format("$%.2f", totalEsperado));
	}

	/**
	 * Actualiza la etiqueta de diferencia con formato visual.
	 * 
	 * @param diferencia Valor calculado (Contado - Esperado).
	 */
	public void setDiferencia(double diferencia) {
		String textoDif = String.format("$%.2f", diferencia);
		if (diferencia > 0) {
			textoDif += " (Sobrante)";
		} else if (diferencia < 0) {
			textoDif += " (Faltante)";
		}
		LblDiferencia.setText(textoDif);
	}

	public double getMontoContado() throws NumberFormatException {
		return Double.parseDouble(TxtMontoContado.getText());
	}

	public void addCerrarCajaListener(ActionListener listener) {
		BtnCerrarCaja.addActionListener(listener);
	}

	public void addMontoContadoListener(KeyListener listener) {
		TxtMontoContado.addKeyListener(listener);
	}

	public void mostrarMensaje(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
	}

	public JButton getBtnCerrarCaja() {
		return BtnCerrarCaja;
	}

	public void mostrarError(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
	}
}