package main;

import org.mindrot.jbcrypt.BCrypt;

public class GeneradorHash {
	public static void main(String[] args) {
		String contraseñaPlana = "1234";

		String contraseñaHasheada = BCrypt.hashpw(contraseñaPlana, BCrypt.gensalt());

		System.out.println("La contraseña '" + contraseñaPlana + "' en hash es:");
		System.out.println(contraseñaHasheada);
	}
}