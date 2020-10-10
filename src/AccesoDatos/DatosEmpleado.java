/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AccesoDatos;

import java.math.BigDecimal;

/**
 *
 * @author Usuario
 */
public class DatosEmpleado {    
    
    private BigDecimal Sueldo;
    private String cargo;

    public DatosEmpleado() {
    }

    public DatosEmpleado(BigDecimal Sueldo, String cargo) {
        this.Sueldo = Sueldo;
        this.cargo = cargo;
    }

    public BigDecimal getSueldo() {
        return Sueldo;
    }

    public String getCargo() {
        return cargo;
    }

    public void setSueldo(BigDecimal Sueldo) {
        this.Sueldo = Sueldo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
}
