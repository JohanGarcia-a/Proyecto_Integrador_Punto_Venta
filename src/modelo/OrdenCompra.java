package modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import modelogenerico.Entidad;

// Esta clase es la "Maestra", similar a Venta.java
public class OrdenCompra implements Entidad {

    private int id;
    private int proveedorId;
    private Date fecha;
    private String status;

    // Campos extra para mostrar datos de JOINs (igual que en Venta)
    private String nombreProveedor;

    // Lista para guardar los detalles (igual que en Venta)
    private List<OrdenCompraDetalle> detalles;

    // Constructor para cuando creamos un pedido nuevo
    public OrdenCompra() {
        this.detalles = new ArrayList<>();
        this.fecha = new Date();
        this.status = "Pendiente";
    }

    // Constructor para cuando leemos un pedido de la BD
    public OrdenCompra(int id, int proveedorId, Date fecha, String status, String nombreProveedor) {
        this.id = id;
        this.proveedorId = proveedorId;
        this.fecha = fecha;
        this.status = status;
        this.nombreProveedor = nombreProveedor;
        this.detalles = new ArrayList<>(); // Los detalles se cargan por separado
    }

    // --- Métodos para manejar los detalles (igual que en Venta) ---
    
    public void agregarDetalle(OrdenCompraDetalle detalle) {
        this.detalles.add(detalle);
        detalle.setOrdenCompraId(this.id); // Asignamos el ID de esta orden al detalle
    }

    public void quitarDetalle(int indice) {
        if (indice >= 0 && indice < detalles.size()) {
            detalles.remove(indice);
        }
    }

    public List<OrdenCompraDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<OrdenCompraDetalle> detalles) {
        this.detalles = detalles;
    }

    // --- Métodos de la interfaz Entidad ---

    @Override
    public int getid() {
        return this.id;
    }

    @Override
    public void setid(int id) {
        this.id = id;
    }

    // Este toTableRow() es para el panel de "Gestionar Pedidos"
    @Override
    public Object[] toTableRow() {
        return new Object[] {
            this.id,
            this.nombreProveedor,
            this.fecha,
            this.status
        };
    }

    // --- Getters y Setters ---

    public int getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(int proveedorId) {
        this.proveedorId = proveedorId;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }
}