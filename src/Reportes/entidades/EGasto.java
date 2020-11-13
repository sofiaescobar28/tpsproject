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
public class EGasto {
    private String Nombre;
    private String CargoO;
    private String CargoT;
    private String Fecha;
    private double pago;
    private String Comentario;

    public EGasto() {
    }

    public String getNombre() {
        return Nombre;
    }

    public String getCargoO() {
        return CargoO;
    }

    public String getCargoT() {
        return CargoT;
    }
    
    public String getFecha() {
        return Fecha;
    }
    
    public double getPago() {
        return pago;
    }

    public String getComentario() {
        return Comentario;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public void setCargoO(String CargoO) {
        this.CargoO = CargoO;
    }

    public void setCargoT(String CargoT) {
        this.CargoT = CargoT;
    }
    
    public void setFecha(String Fecha) {
        this.Fecha = Fecha;
    }

    public void setPago(double pago) {
        this.pago = pago;
    }

    public void setComentario(String Comentario) {
        this.Comentario = Comentario;
    }

    @Override
    public String toString() {
        return "EGasto{" + "Nombre=" + Nombre + ", CargoO=" + CargoO + ", CargoT=" + CargoT + ", pago=" + pago + ", Comentario=" + Comentario + '}';
    }
    
}
