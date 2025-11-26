// En el paquete "modelo"
package modelo;

import java.util.Date;

public class EntradaInventario {
	private int id;
	private int productoId;
	private String nombreProducto;
	private int cantidadAgregada;
	private String productoDescripcion;
	private Date fechaEntrada;
	private int usuarioId;
	private String nombreUsuario;

	public EntradaInventario(int productoId, int cantidadAgregada, Date fechaEntrada, int usuarioId,
			String productoDescripcion) {
		this.productoId = productoId;
		this.cantidadAgregada = cantidadAgregada;
		this.fechaEntrada = fechaEntrada;
		this.usuarioId = usuarioId;
		this.productoDescripcion = productoDescripcion;
	}

	public EntradaInventario(int id, String nombreProducto, int cantidadAgregada, Date fechaEntrada,
			String nombreUsuario, String productoDescripcion) {
		this.id = id;
		this.nombreProducto = nombreProducto;
		this.cantidadAgregada = cantidadAgregada;
		this.fechaEntrada = fechaEntrada;
		this.nombreUsuario = nombreUsuario;
		this.productoDescripcion = productoDescripcion; // <-- AÑADIR
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProductoId() {
		return productoId;
	}

	public void setProductoId(int productoId) {
		this.productoId = productoId;
	}

	public String getNombreProducto() {
		return nombreProducto;
	}

	public void setNombreProducto(String nombreProducto) {
		this.nombreProducto = nombreProducto;
	}

	public String getProductoDescripcion() {
		return productoDescripcion;
	}

	public void setProductoDescripcion(String productoDescripcion) {
		this.productoDescripcion = productoDescripcion;
	}

	public int getCantidadAgregada() {
		return cantidadAgregada;
	}

	public void setCantidadAgregada(int cantidadAgregada) {
		this.cantidadAgregada = cantidadAgregada;
	}

	public Date getFechaEntrada() {
		return fechaEntrada;
	}

	public void setFechaEntrada(Date fechaEntrada) {
		this.fechaEntrada = fechaEntrada;
	}

	public int getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(int usuarioId) {
		this.usuarioId = usuarioId;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

}