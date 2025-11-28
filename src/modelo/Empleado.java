package modelo;

import modelogenerico.Entidad;

/**
 * Entidad que representa a un operador o usuario del sistema (Cajero, Admin,
 * etc.).
 * <p>
 * Mapea la tabla <b>TablaEmpleados</b> de la base de datos. Esta clase es
 * crítica para el módulo de seguridad, ya que contiene las credenciales
 * ({@code nombre} y {@code contraseña}) utilizadas durante el inicio de sesión
 * y el {@code rol} que define los permisos de acceso en el menú principal.
 * </p>
 * 
 * @version 1.0
 */
public class Empleado implements Entidad {

	/** Identificador único del empleado (PK). */
	private int id;

	/** Nombre de usuario o nombre real del empleado (usado para Login). */
	private String nombre;

	/** Número de teléfono de contacto. */
	private String numTel;

	/**
	 * * Rol de seguridad asignado.
	 * <p>
	 * Valores esperados: "ADMIN", "GENERAL", "SUPERVISOR". Determina qué botones se
	 * habilitan.
	 * </p>
	 */
	private String rol;

	/** Contraseña de acceso al sistema. */
	private String contraseña;

	/**
	 * Constructor completo para instanciar un empleado.
	 * 
	 * @param id         Identificador único (o -1 si es nuevo).
	 * @param nombre     Nombre del empleado.
	 * @param numTel     Número de teléfono.
	 * @param rol        Rol de permisos.
	 * @param contraseña Clave de acceso.
	 */
	public Empleado(int id, String nombre, String numTel, String rol, String contraseña) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.numTel = numTel;
		this.rol = rol;
		this.contraseña = contraseña;
	}

	// --- Implementación de la Interfaz Entidad ---

	@Override
	public int getid() {
		return id;
	}

	@Override
	public void setid(int id) {
		this.id = id;
	}

	/**
	 * Genera un arreglo de objetos para representar al empleado en el panel de
	 * administración.
	 * <p>
	 * <b>Nota de Seguridad:</b> Este método devuelve [ID, Nombre, Teléfono, Rol].
	 * La contraseña se omite intencionalmente para que no sea visible en la JTable
	 * de la interfaz.
	 * </p>
	 * 
	 * @return Arreglo de datos para la vista.
	 */
	@Override
	public Object[] toTableRow() {
		return new Object[] { this.id, this.nombre, this.numTel, this.rol };
	}

	// --- Métodos de la clase Object ---

	/**
	 * Devuelve el nombre del empleado.
	 * <p>
	 * Utilizado por los {@code JComboBox} en el módulo de Ventas y Reportes para
	 * mostrar quién realizó la operación.
	 * </p>
	 * 
	 * @return Nombre del empleado.
	 */
	@Override
	public String toString() {
		return nombre;
	}

	// --- Getters y Setters Estándar ---

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getNumTel() {
		return numTel;
	}

	public void setNumTel(String numTel) {
		this.numTel = numTel;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	public String getContraseña() {
		return contraseña;
	}

	public void setContraseña(String contraseña) {
		this.contraseña = contraseña;
	}
}