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
public class ECargos {
    private String Cargo;
    private int Num_Emp;

    public ECargos() {
    }    

    public String getCargo() {
        return Cargo;
    }

    public void setCargo(String Cargo) {
        this.Cargo = Cargo;
    }

    public int getNum_Emp() {
        return Num_Emp;
    }

    public void setNum_Emp(int Num_Emp) {
        this.Num_Emp = Num_Emp;
    }

    @Override
    public String toString() {
        return "ECargos{" + "Cargo=" + Cargo + ", Num_Emp=" + Num_Emp + '}';
    }
    
}
