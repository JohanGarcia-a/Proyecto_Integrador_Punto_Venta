package vista;

import javax.swing.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.SystemColor;
import java.awt.Color;

public class VistaLogin extends JFrame {
	private JTextField txtUsuario;
	private JPasswordField txtPassword;
	private JButton btnIngresar;

	public VistaLogin() {
		setTitle("Iniciar Sesión - POS");
		setSize(400, 200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null); // Centrar en la pantalla

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(SystemColor.activeCaption);
		getContentPane().add(panel);

		// --- Componente 1: Etiqueta "Usuario:" ---
		JLabel lblUsuario = new JLabel("Usuario:");
		GridBagConstraints gbc_lblUsuario = new GridBagConstraints();
		gbc_lblUsuario.insets = new Insets(5, 5, 5, 5);
		gbc_lblUsuario.anchor = GridBagConstraints.EAST; // Alineado a la derecha de su celda
		gbc_lblUsuario.gridx = 0;
		gbc_lblUsuario.gridy = 0;
		panel.add(lblUsuario, gbc_lblUsuario);

		// --- Componente 2: Campo de Texto para Usuario ---
		txtUsuario = new JTextField(20);
		GridBagConstraints gbc_txtUsuario = new GridBagConstraints();
		gbc_txtUsuario.insets = new Insets(5, 5, 5, 5);
		gbc_txtUsuario.fill = GridBagConstraints.HORIZONTAL; // Se expande horizontalmente
		gbc_txtUsuario.weightx = 1.0; // Permite que la columna crezca
		gbc_txtUsuario.gridx = 1;
		gbc_txtUsuario.gridy = 0;
		panel.add(txtUsuario, gbc_txtUsuario);

		// --- Componente 3: Etiqueta "Contraseña:" ---
		JLabel lblPassword = new JLabel("Contraseña:");
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.insets = new Insets(5, 5, 5, 5);
		gbc_lblPassword.anchor = GridBagConstraints.EAST;
		gbc_lblPassword.gridx = 0;
		gbc_lblPassword.gridy = 1;
		panel.add(lblPassword, gbc_lblPassword);

		// --- Componente 4: Campo de Texto para Contraseña ---
		txtPassword = new JPasswordField(20);
		GridBagConstraints gbc_txtPassword = new GridBagConstraints();
		gbc_txtPassword.insets = new Insets(5, 5, 5, 5);
		gbc_txtPassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPassword.weightx = 1.0;
		gbc_txtPassword.gridx = 1;
		gbc_txtPassword.gridy = 1;
		panel.add(txtPassword, gbc_txtPassword);

		// --- Componente 5: Botón "Ingresar" ---
		btnIngresar = new JButton("Ingresar");
		btnIngresar.setBackground(new Color(0, 255, 0));
		GridBagConstraints gbc_btnIngresar = new GridBagConstraints();
		gbc_btnIngresar.insets = new Insets(5, 5, 5, 5);
		gbc_btnIngresar.anchor = GridBagConstraints.EAST; // Alineado a la derecha de su celda
		gbc_btnIngresar.gridx = 1;
		gbc_btnIngresar.gridy = 2;
		panel.add(btnIngresar, gbc_btnIngresar);

		

		
		txtUsuario.addActionListener(e -> txtPassword.requestFocusInWindow());

		// 2. Al presionar "Enter" en la contraseña, "presiona" el botón de ingresar
		txtPassword.addActionListener(e -> btnIngresar.doClick());

	
	}

	public String getUsuario() {
		return txtUsuario.getText().trim();
	}

	public String getPassword() {
		return new String(txtPassword.getPassword());
	}

	public void addLoginListener(ActionListener listener) {
		btnIngresar.addActionListener(listener);
	}

	public void mostrarError(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
	}
}