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
public class EEgresoIngreso {
        private String Descripcion;
        private String Tipo;
        private String Fecha;
        private String Categoria;
        private String Cantidad;
        private String Medida;
        private double Monto;

    public EEgresoIngreso() {
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String Tipo) {
        this.Tipo = Tipo;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String Fecha) {
        this.Fecha = Fecha;
    }

    public String getCategoria() {
        return Categoria;
    }

    public void setCategoria(String Categoria) {
        this.Categoria = Categoria;
    }

    public String getCantidad() {
        return Cantidad;
    }

    public void setCantidad(String Cantidad) {
        this.Cantidad = Cantidad;
    }

    public String getMedida() {
        return Medida;
    }

    public void setMedida(String Medida) {
        this.Medida = Medida;
    }

    public double getMonto() {
        return Monto;
    }

    public void setMonto(double Monto) {
        this.Monto = Monto;
    }

    @Override
    public String toString() {
        return "EEgresoIngreso{" + "Descripcion=" + Descripcion + ", Tipo=" + Tipo + ", Fecha=" + Fecha + ", Categoria=" + Categoria + ", Cantidad=" + Cantidad + ", Medida=" + Medida + ", Monto=" + Monto + '}';
    }        
}
