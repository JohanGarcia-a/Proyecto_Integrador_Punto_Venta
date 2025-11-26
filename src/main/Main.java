package main;

import java.awt.EventQueue;
import javax.swing.UIManager;

import controlador.ControladorLogin;
import persistencia.EmpleadoDAO;
import vista.VistaLogin;

public class Main {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		EventQueue.invokeLater(() -> {
			
		
			VistaLogin vistaLogin = new VistaLogin();
			EmpleadoDAO empleadoDAO = new EmpleadoDAO();

			
			new ControladorLogin(vistaLogin, empleadoDAO);

			
			vistaLogin.setVisible(true);
		});
	}
}