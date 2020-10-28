/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Reportes.entidades;

/**
 *
 * @author Usuario
 */
public class EIEResumen {
    private String Fecha;
    private String Tipo;
    private double Monto;

    public EIEResumen() {
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String Fecha) {
        this.Fecha = Fecha;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String Tipo) {
        this.Tipo = Tipo;
    }

    public double getMonto() {
        return Monto;
    }

    public void setMonto(double Monto) {
        this.Monto = Monto;
    }

    @Override
    public String toString() {
        return "EResumen{" + "Fecha=" + Fecha + ", Tipo=" + Tipo + ", Monto=" + Monto + '}';
    }
    
}
