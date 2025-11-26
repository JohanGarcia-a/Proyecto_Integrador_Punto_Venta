package modelo;

import java.util.Date;

public class CorteCaja {

    private int corteID;
    private int usuarioID;
    private Date fechaApertura;
    private double montoInicial;
    private Date fechaCierre;       // Será null si la caja está abierta
    private double montoFinalSistema;
    private double montoFinalContado;
    private double diferencia;
    private String status;         
  
    public CorteCaja() {
    }

   
    public CorteCaja(int usuarioID, double montoInicial) {
        this.usuarioID = usuarioID;
        this.montoInicial = montoInicial;
        this.fechaApertura = new Date(); 
        this.status = "Abierto";
        
        // Los campos de cierre se quedan nulos o en 0.0
        this.fechaCierre = null;
        this.montoFinalSistema = 0.0;
        this.montoFinalContado = 0.0;
        this.diferencia = 0.0;
    }

  
    public CorteCaja(int corteID, int usuarioID, Date fechaApertura, double montoInicial, 
                     Date fechaCierre, double montoFinalSistema, double montoFinalContado, 
                     double diferencia, String status) {
        this.corteID = corteID;
        this.usuarioID = usuarioID;
        this.fechaApertura = fechaApertura;
        this.montoInicial = montoInicial;
        this.fechaCierre = fechaCierre;
        this.montoFinalSistema = montoFinalSistema;
        this.montoFinalContado = montoFinalContado;
        this.diferencia = diferencia;
        this.status = status;
    }

    // --- Getters y Setters ---

    public int getCorteID() {
        return corteID;
    }

    public void setCorteID(int corteID) {
        this.corteID = corteID;
    }

    public int getUsuarioID() {
        return usuarioID;
    }

    public void setUsuarioID(int usuarioID) {
        this.usuarioID = usuarioID;
    }

    public Date getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(Date fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public double getMontoInicial() {
        return montoInicial;
    }

    public void setMontoInicial(double montoInicial) {
        this.montoInicial = montoInicial;
    }

    public Date getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(Date fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public double getMontoFinalSistema() {
        return montoFinalSistema;
    }

    public void setMontoFinalSistema(double montoFinalSistema) {
        this.montoFinalSistema = montoFinalSistema;
    }

    public double getMontoFinalContado() {
        return montoFinalContado;
    }

    public void setMontoFinalContado(double montoFinalContado) {
        this.montoFinalContado = montoFinalContado;
    }

    public double getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(double diferencia) {
        this.diferencia = diferencia;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}