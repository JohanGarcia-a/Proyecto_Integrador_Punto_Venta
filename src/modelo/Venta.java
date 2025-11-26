package modelo;

import java.util.Date;
import java.util.List;
import modelogenerico.Entidad;
import java.util.ArrayList;

public class Venta implements Entidad {
	private int id;
	private int clienteId;
	private int empleadoId;
	private Date fecha;
	private int corteID;

	// --- INICIO DE LA MODIFICACIÓN ---
	private double subtotal; // Suma de productos
	private double descuento; // Monto descontado
	private double impuestos; // Monto de IVA
	private double total; // Total final (subtotal - descuento + impuestos)
	// --- FIN DE LA MODIFICACIÓN ---

	private String nombreCliente;
	private String nombreEmpleado;
	private String metodoPago;

	private List<VentaDetalle> detalles;

	public Venta() {
		this.detalles = new ArrayList<>();
		this.total = 0.0;
		this.subtotal = 0.0; // --- AÑADIDO ---
		this.descuento = 0.0; // --- AÑADIDO ---
		this.impuestos = 0.0; // --- AÑADIDO ---
	}

	// --- ESTE MÉTODO AHORA CALCULA EL SUBTOTAL ---

	public double recalcularSubtotal() {
		double nuevoSubtotal = 0.0;
		for (VentaDetalle detalle : this.detalles) {
			nuevoSubtotal += detalle.getSubtotal();
		}
		this.subtotal = nuevoSubtotal; // Actualizamos el campo
		return this.subtotal;
	}

	public void agregarDetalle(VentaDetalle detalle) {
		this.detalles.add(detalle);
		// La lógica de recalcular el total se mueve al controlador
	}

	public void quitarDetalle(int indice) {
		if (indice >= 0 && indice < detalles.size()) {
			detalles.remove(indice);
			// La lógica de recalcular el total se mueve al controlador
		}
	}

	public void setDetalles(List<VentaDetalle> detalles) {
		this.detalles = detalles;
		// La lógica de recalcular el total se mueve al controlador
	}

	public List<VentaDetalle> getDetalles() {
		return detalles;
	}

	@Override
	public Object[] toTableRow() {
		// Este método se usa en PanelHistorialVentas, el total final es correcto
		return new Object[] { this.id, this.fecha, this.nombreCliente, this.nombreEmpleado, this.total };
	}

	public int getClienteId() {
		return clienteId;
	}

	public void setClienteId(int clienteId) {
		this.clienteId = clienteId;
	}

	public int getEmpleadoId() {
		return empleadoId;
	}

	public void setEmpleadoId(int empleadoId) {
		this.empleadoId = empleadoId;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public double getTotal() {
		return this.total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public String getNombreCliente() {
		return nombreCliente;
	}

	public void setNombreCliente(String nombreCliente) {
		this.nombreCliente = nombreCliente;
	}

	public String getNombreEmpleado() {
		return nombreEmpleado;
	}

	public void setNombreEmpleado(String nombreEmpleado) {
		this.nombreEmpleado = nombreEmpleado;
	}

	public String getMetodoPago() {
		return metodoPago;
	}

	public void setMetodoPago(String metodoPago) {
		this.metodoPago = metodoPago;
	}

	public int getCorteID() {
		return corteID;
	}

	public void setCorteID(int corteID) {
		this.corteID = corteID;
	}

	@Override
	public int getid() {
		return this.id;
	}

	@Override
	public void setid(int id) {
		this.id = id;
	}

	// --- GETTERS Y SETTERS NUEVOS ---
	public double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}

	public double getDescuento() {
		return descuento;
	}

	public void setDescuento(double descuento) {
		this.descuento = descuento;
	}

	public double getImpuestos() {
		return impuestos;
	}

	public void setImpuestos(double impuestos) {
		this.impuestos = impuestos;
	}

}