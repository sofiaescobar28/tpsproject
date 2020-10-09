/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AccesoDatos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Manuel
 */
@Entity
@Table(name = "EMPLEADOS")
@NamedQueries({
    @NamedQuery(name = "Empleados.findAll", query = "SELECT e FROM Empleados e")})
public class Empleados implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "EMP_ID")
    private BigDecimal empId;
    @Column(name = "EMP_NOMBRE")
    private String empNombre;
    @Column(name = "EMP_SALARIO")
    private Double empSalario;
    @Column(name = "EMP_ESTADO")
    private BigInteger empEstado;
    @Column(name = "EMP_TELEFONO")
    private String empTelefono;
    @JoinColumn(name = "CAR_ID", referencedColumnName = "CARGOS_ID")
    @ManyToOne
    private Cargos carId;

    public Empleados() {
    }

    public Empleados(BigDecimal empId) {
        this.empId = empId;
    }

    public BigDecimal getEmpId() {
        return empId;
    }

    public void setEmpId(BigDecimal empId) {
        this.empId = empId;
    }

    public String getEmpNombre() {
        return empNombre;
    }

    public void setEmpNombre(String empNombre) {
        this.empNombre = empNombre;
    }

    public Double getEmpSalario() {
        return empSalario;
    }

    public void setEmpSalario(Double empSalario) {
        this.empSalario = empSalario;
    }

    public BigInteger getEmpEstado() {
        return empEstado;
    }

    public void setEmpEstado(BigInteger empEstado) {
        this.empEstado = empEstado;
    }

    public String getEmpTelefono() {
        return empTelefono;
    }

    public void setEmpTelefono(String empTelefono) {
        this.empTelefono = empTelefono;
    }

    public Cargos getCarId() {
        return carId;
    }

    public void setCarId(Cargos carId) {
        this.carId = carId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (empId != null ? empId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Empleados)) {
            return false;
        }
        Empleados other = (Empleados) object;
        if ((this.empId == null && other.empId != null) || (this.empId != null && !this.empId.equals(other.empId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AccesoDatos.Empleados[ empId=" + empId + " ]";
    }
    
}
