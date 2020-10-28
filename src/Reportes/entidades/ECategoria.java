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
public class ECategoria {
    private String categoria;
    private String tipo;

    public ECategoria() {
    }
    
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "ECategoria{" + "categoria=" + categoria + ", tipo=" + tipo + '}';
    }
    
}
