package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Ventana de inicio de sesión (Login) del sistema POS.
 * <p>
 * Esta clase representa la interfaz gráfica inicial que permite a los usuarios
 * autenticarse. Extiende de {@link JFrame} y utiliza un diseño centrado
 * ({@link GridBagLayout}) para presentar los campos de credenciales de forma
 * limpia.
 * </p>
 * <p>
 * <b>Responsabilidad:</b> Capturar el usuario y contraseña y notificar al
 * {@code ControladorLogin} cuando el usuario intenta ingresar.
 * </p>
 * 
 * @version 1.1
 */
public class VistaLogin extends JFrame {
	/**
	 * Serial Version UID para la serialización de componentes Swing.
	 */
	private static final long serialVersionUID = 1L;

	/** Campo de texto para ingresar el nombre de usuario. */
	private JTextField txtUsuario;

	/** Campo de contraseña (oculta los caracteres) para seguridad. */
	private JPasswordField txtPassword;

	/** Botón para disparar la acción de validación. */
	private JButton btnIngresar;

	/**
	 * Constructor.
	 * <p>
	 * Configura las propiedades de la ventana (tamaño, posición, cierre),
	 * inicializa los componentes visuales y define el diseño.
	 * </p>
	 * <p>
	 * Incluye mejoras de UX (Experiencia de Usuario):
	 * <ul>
	 * <li>Presionar <b>Enter</b> en el campo de Usuario mueve el foco a la
	 * Contraseña.</li>
	 * <li>Presionar <b>Enter</b> en el campo de Contraseña simula el clic en
	 * "Ingresar".</li>
	 * </ul>
	 * </p>
	 */
	public VistaLogin() {
		setTitle("Iniciar Sesión - POS");
		setSize(400, 450);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null); // Centrar en la pantalla
		setResizable(false);

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(Color.WHITE);
		getContentPane().add(panel);

		// --- Título ---
		JLabel lblTitulo = new JLabel("Bienvenido");
		lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
		lblTitulo.setForeground(new Color(50, 50, 50));

		GridBagConstraints gbcTitulo = new GridBagConstraints();
		gbcTitulo.gridx = 0;
		gbcTitulo.gridy = 0;
		gbcTitulo.gridwidth = 2;
		gbcTitulo.anchor = GridBagConstraints.CENTER;
		gbcTitulo.insets = new Insets(10, 10, 10, 10);
		panel.add(lblTitulo, gbcTitulo);

		// --- Icono (Opcional) ---
		try {
			ImageIcon icon = new ImageIcon(getClass().getResource("/Iconos/login.png"));
			Image img = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
			JLabel lblIcono = new JLabel(new ImageIcon(img));

			GridBagConstraints gbcIcono = new GridBagConstraints();
			gbcIcono.gridx = 0;
			gbcIcono.gridy = 1;
			gbcIcono.gridwidth = 2;
			gbcIcono.anchor = GridBagConstraints.CENTER;
			gbcIcono.insets = new Insets(0, 10, 20, 10);
			panel.add(lblIcono, gbcIcono);
		} catch (Exception e) {
			// Ignorar si no se encuentra la imagen para evitar errores en tiempo de
			// ejecución
		}

		// --- Etiqueta Usuario ---
		JLabel lblUsuario = new JLabel("Usuario:");
		lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblUsuario.setForeground(Color.DARK_GRAY);

		GridBagConstraints gbcLblUsuario = new GridBagConstraints();
		gbcLblUsuario.gridx = 0;
		gbcLblUsuario.gridy = 2;
		gbcLblUsuario.anchor = GridBagConstraints.WEST;
		gbcLblUsuario.insets = new Insets(5, 30, 5, 5);
		panel.add(lblUsuario, gbcLblUsuario);

		// --- Campo Usuario ---
		txtUsuario = new JTextField(20);
		txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));

		GridBagConstraints gbcTxtUsuario = new GridBagConstraints();
		gbcTxtUsuario.gridx = 0;
		gbcTxtUsuario.gridy = 3;
		gbcTxtUsuario.gridwidth = 2;
		gbcTxtUsuario.fill = GridBagConstraints.HORIZONTAL;
		gbcTxtUsuario.insets = new Insets(0, 30, 10, 30);
		panel.add(txtUsuario, gbcTxtUsuario);

		// --- Etiqueta Contraseña ---
		JLabel lblPassword = new JLabel("Contraseña:");
		lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblPassword.setForeground(Color.DARK_GRAY);

		GridBagConstraints gbcLblPassword = new GridBagConstraints();
		gbcLblPassword.gridx = 0;
		gbcLblPassword.gridy = 4;
		gbcLblPassword.anchor = GridBagConstraints.WEST;
		gbcLblPassword.insets = new Insets(5, 30, 5, 5);
		panel.add(lblPassword, gbcLblPassword);

		txtPassword = new JPasswordField(20);
		txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));

		GridBagConstraints gbcTxtPassword = new GridBagConstraints();
		gbcTxtPassword.gridx = 0;
		gbcTxtPassword.gridy = 5;
		gbcTxtPassword.gridwidth = 2;
		gbcTxtPassword.fill = GridBagConstraints.HORIZONTAL;
		gbcTxtPassword.insets = new Insets(0, 30, 20, 30);
		panel.add(txtPassword, gbcTxtPassword);

		// --- Botón Ingresar ---
		btnIngresar = new JButton("Ingresar");
		btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnIngresar.setBackground(new Color(0, 123, 255));
		btnIngresar.setForeground(Color.WHITE);
		btnIngresar.setFocusPainted(false);
		btnIngresar.setBorderPainted(false);
		btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));

		GridBagConstraints gbcBtnIngresar = new GridBagConstraints();
		gbcBtnIngresar.gridx = 0;
		gbcBtnIngresar.gridy = 6;
		gbcBtnIngresar.gridwidth = 2;
		gbcBtnIngresar.fill = GridBagConstraints.HORIZONTAL;
		gbcBtnIngresar.insets = new Insets(10, 30, 30, 30);
		panel.add(btnIngresar, gbcBtnIngresar);

		// --- Listeners para mejorar la usabilidad (Teclado) ---
		txtUsuario.addActionListener(e -> txtPassword.requestFocusInWindow());
		txtPassword.addActionListener(e -> btnIngresar.doClick());
	}

	/**
	 * Obtiene el texto ingresado en el campo de usuario.
	 * 
	 * @return El nombre de usuario sin espacios al inicio o final.
	 */
	public String getUsuario() {
		return txtUsuario.getText().trim();
	}

	/**
	 * Obtiene la contraseña ingresada.
	 * 
	 * @return La contraseña como String plano.
	 */
	public String getPassword() {
		return new String(txtPassword.getPassword());
	}

	/**
	 * Asigna el controlador que manejará el evento de clic en "Ingresar".
	 * 
	 * @param listener El ActionListener del {@code ControladorLogin}.
	 */
	public void addLoginListener(ActionListener listener) {
		btnIngresar.addActionListener(listener);
	}

	/**
	 * Muestra un cuadro de diálogo modal con un mensaje de error.
	 * <p>
	 * Utilizado por el controlador cuando la autenticación falla (datos
	 * incorrectos).
	 * </p>
	 * 
	 * @param mensaje Texto del error a mostrar.
	 */
	public void mostrarError(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
	}
}