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

public class PanelCierreCaja extends JPanel {

	private static final long serialVersionUID = 1L;

	// --- Etiquetas para mostrar datos ---
	private JLabel LblUsuario;
	private JLabel LblFechaApertura;
	private JLabel LblMontoInicial;
	private JLabel LblVentasEfectivo;
	private JLabel LblVentasTarjeta;
	private JLabel LblTotalSistema; // (MontoInicial + VentasEfectivo)
	private JLabel LblDiferencia;

	// --- Campo de entrada para el usuario ---
	private JTextField TxtMontoContado;

	// --- Botón de acción ---
	private JButton BtnCerrarCaja;

	public PanelCierreCaja() {
		setLayout(new BorderLayout(10, 10));
		setBorder(new TitledBorder("Cierre de Caja"));

		// Panel principal con GridBagLayout
		JPanel panelFormulario = new JPanel(new GridBagLayout());

		Font fontTitulo = new Font("Tahoma", Font.BOLD, 14);
		Font fontValor = new Font("Tahoma", Font.PLAIN, 14);
		Font fontTotal = new Font("Tahoma", Font.BOLD, 16);

		// --- Fila 0: Usuario ---
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

		// --- Fila 1: Fecha de Apertura ---
		JLabel LblFechaAperturaTitulo = new JLabel("Fecha Apertura:");
		LblFechaAperturaTitulo.setFont(fontTitulo);
		GridBagConstraints gbc_LblFechaAperturaTitulo = new GridBagConstraints();
		gbc_LblFechaAperturaTitulo.insets = new Insets(10, 10, 10, 10);
		gbc_LblFechaAperturaTitulo.anchor = GridBagConstraints.WEST;
		gbc_LblFechaAperturaTitulo.gridx = 0;
		gbc_LblFechaAperturaTitulo.gridy = 1;
		panelFormulario.add(LblFechaAperturaTitulo, gbc_LblFechaAperturaTitulo);

		LblFechaApertura = new JLabel("---");
		LblFechaApertura.setFont(fontValor);
		GridBagConstraints gbc_LblFechaApertura = new GridBagConstraints();
		gbc_LblFechaApertura.insets = new Insets(10, 10, 10, 10);
		gbc_LblFechaApertura.anchor = GridBagConstraints.WEST;
		gbc_LblFechaApertura.gridx = 1;
		gbc_LblFechaApertura.gridy = 1;
		panelFormulario.add(LblFechaApertura, gbc_LblFechaApertura);

		// --- Fila 2: Monto Inicial (Fondo) ---
		JLabel LblMontoInicialTitulo = new JLabel("Monto Inicial (Fondo):");
		LblMontoInicialTitulo.setFont(fontTitulo);
		GridBagConstraints gbc_LblMontoInicialTitulo = new GridBagConstraints();
		gbc_LblMontoInicialTitulo.insets = new Insets(10, 10, 10, 10);
		gbc_LblMontoInicialTitulo.anchor = GridBagConstraints.WEST;
		gbc_LblMontoInicialTitulo.gridx = 0;
		gbc_LblMontoInicialTitulo.gridy = 2;
		panelFormulario.add(LblMontoInicialTitulo, gbc_LblMontoInicialTitulo);

		LblMontoInicial = new JLabel("$0.00");
		LblMontoInicial.setFont(fontValor);
		GridBagConstraints gbc_LblMontoInicial = new GridBagConstraints();
		gbc_LblMontoInicial.insets = new Insets(10, 10, 10, 10);
		gbc_LblMontoInicial.anchor = GridBagConstraints.WEST;
		gbc_LblMontoInicial.gridx = 1;
		gbc_LblMontoInicial.gridy = 2;
		panelFormulario.add(LblMontoInicial, gbc_LblMontoInicial);

		// --- Fila 3: Ventas en Efectivo ---
		JLabel LblVentasEfectivoTitulo = new JLabel("Ventas en Efectivo:");
		LblVentasEfectivoTitulo.setFont(fontTitulo);
		GridBagConstraints gbc_LblVentasEfectivoTitulo = new GridBagConstraints();
		gbc_LblVentasEfectivoTitulo.insets = new Insets(10, 10, 10, 10);
		gbc_LblVentasEfectivoTitulo.anchor = GridBagConstraints.WEST;
		gbc_LblVentasEfectivoTitulo.gridx = 0;
		gbc_LblVentasEfectivoTitulo.gridy = 3;
		panelFormulario.add(LblVentasEfectivoTitulo, gbc_LblVentasEfectivoTitulo);

		LblVentasEfectivo = new JLabel("$0.00");
		LblVentasEfectivo.setFont(fontValor);
		GridBagConstraints gbc_LblVentasEfectivo = new GridBagConstraints();
		gbc_LblVentasEfectivo.insets = new Insets(10, 10, 10, 10);
		gbc_LblVentasEfectivo.anchor = GridBagConstraints.WEST;
		gbc_LblVentasEfectivo.gridx = 1;
		gbc_LblVentasEfectivo.gridy = 3;
		panelFormulario.add(LblVentasEfectivo, gbc_LblVentasEfectivo);

		// --- Fila 4: Total en Sistema ---
		JLabel LblTotalSistemaTitulo = new JLabel("Total Esperado en Caja:");
		LblTotalSistemaTitulo.setFont(fontTotal);
		GridBagConstraints gbc_LblTotalSistemaTitulo = new GridBagConstraints();
		gbc_LblTotalSistemaTitulo.insets = new Insets(10, 10, 10, 10);
		gbc_LblTotalSistemaTitulo.anchor = GridBagConstraints.WEST;
		gbc_LblTotalSistemaTitulo.gridx = 0;
		gbc_LblTotalSistemaTitulo.gridy = 4;
		panelFormulario.add(LblTotalSistemaTitulo, gbc_LblTotalSistemaTitulo);

		LblTotalSistema = new JLabel("$0.00");
		LblTotalSistema.setFont(fontTotal);
		GridBagConstraints gbc_LblTotalSistema = new GridBagConstraints();
		gbc_LblTotalSistema.insets = new Insets(10, 10, 10, 10);
		gbc_LblTotalSistema.anchor = GridBagConstraints.WEST;
		gbc_LblTotalSistema.gridx = 1;
		gbc_LblTotalSistema.gridy = 4;
		panelFormulario.add(LblTotalSistema, gbc_LblTotalSistema);

		// --- Fila 5: Monto Físico Contado ---
		JLabel LblMontoContadoTitulo = new JLabel("Monto Físico Contado:");
		LblMontoContadoTitulo.setFont(fontTotal);
		GridBagConstraints gbc_LblMontoContadoTitulo = new GridBagConstraints();
		gbc_LblMontoContadoTitulo.insets = new Insets(10, 10, 10, 10);
		gbc_LblMontoContadoTitulo.anchor = GridBagConstraints.WEST;
		gbc_LblMontoContadoTitulo.gridx = 0;
		gbc_LblMontoContadoTitulo.gridy = 5;
		panelFormulario.add(LblMontoContadoTitulo, gbc_LblMontoContadoTitulo);

		TxtMontoContado = new JTextField(10);
		TxtMontoContado.setFont(fontTotal);
		GridBagConstraints gbc_TxtMontoContado = new GridBagConstraints();
		gbc_TxtMontoContado.insets = new Insets(10, 10, 10, 10);
		gbc_TxtMontoContado.anchor = GridBagConstraints.WEST;
		gbc_TxtMontoContado.fill = GridBagConstraints.HORIZONTAL;
		gbc_TxtMontoContado.gridx = 1;
		gbc_TxtMontoContado.gridy = 5;
		panelFormulario.add(TxtMontoContado, gbc_TxtMontoContado);

		// --- Fila 6: Diferencia ---
		JLabel LblDiferenciaTitulo = new JLabel("Diferencia (Faltante/Sobrante):");
		LblDiferenciaTitulo.setFont(fontTotal);
		GridBagConstraints gbc_LblDiferenciaTitulo = new GridBagConstraints();
		gbc_LblDiferenciaTitulo.insets = new Insets(10, 10, 10, 10);
		gbc_LblDiferenciaTitulo.anchor = GridBagConstraints.WEST;
		gbc_LblDiferenciaTitulo.gridx = 0;
		gbc_LblDiferenciaTitulo.gridy = 6;
		panelFormulario.add(LblDiferenciaTitulo, gbc_LblDiferenciaTitulo);

		LblDiferencia = new JLabel("$0.00");
		LblDiferencia.setFont(fontTotal);
		GridBagConstraints gbc_LblDiferencia = new GridBagConstraints();
		gbc_LblDiferencia.insets = new Insets(10, 10, 10, 10);
		gbc_LblDiferencia.anchor = GridBagConstraints.WEST;
		gbc_LblDiferencia.gridx = 1;
		gbc_LblDiferencia.gridy = 6;
		panelFormulario.add(LblDiferencia, gbc_LblDiferencia);

		// --- Fila 7: Ventas con Tarjeta (Informativo) ---
		JLabel LblVentasTarjetaTitulo = new JLabel("(Ventas con Tarjeta (Info):");
		LblVentasTarjetaTitulo.setFont(fontTitulo);
		GridBagConstraints gbc_LblVentasTarjetaTitulo = new GridBagConstraints();
		gbc_LblVentasTarjetaTitulo.insets = new Insets(10, 10, 10, 10);
		gbc_LblVentasTarjetaTitulo.anchor = GridBagConstraints.WEST;
		gbc_LblVentasTarjetaTitulo.gridx = 0;
		gbc_LblVentasTarjetaTitulo.gridy = 7;
		panelFormulario.add(LblVentasTarjetaTitulo, gbc_LblVentasTarjetaTitulo);

		LblVentasTarjeta = new JLabel("$0.00");
		LblVentasTarjeta.setFont(fontValor);
		GridBagConstraints gbc_LblVentasTarjeta = new GridBagConstraints();
		gbc_LblVentasTarjeta.insets = new Insets(10, 10, 10, 10);
		gbc_LblVentasTarjeta.anchor = GridBagConstraints.WEST;
		gbc_LblVentasTarjeta.gridx = 1;
		gbc_LblVentasTarjeta.gridy = 7;
		panelFormulario.add(LblVentasTarjeta, gbc_LblVentasTarjeta);

		// --- Fila 8: Botón ---
		BtnCerrarCaja = new JButton("Confirmar y Cerrar Caja");
		BtnCerrarCaja.setFont(fontTotal);
		GridBagConstraints gbc_BtnCerrarCaja = new GridBagConstraints();
		gbc_BtnCerrarCaja.insets = new Insets(10, 10, 10, 10);
		gbc_BtnCerrarCaja.gridx = 0;
		gbc_BtnCerrarCaja.gridy = 8;
		gbc_BtnCerrarCaja.gridwidth = 2; // Ocupa 2 columnas
		gbc_BtnCerrarCaja.anchor = GridBagConstraints.CENTER;
		panelFormulario.add(BtnCerrarCaja, gbc_BtnCerrarCaja);

		add(panelFormulario, BorderLayout.CENTER);
	}

	// --- Métodos para que el Controlador interactúe ---

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