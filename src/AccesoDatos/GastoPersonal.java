/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AccesoDatos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Usuario
 */
@Entity
@Table(name = "GASTO_PERSONAL")
@NamedQueries({
    @NamedQuery(name = "GastoPersonal.findAll", query = "SELECT g FROM GastoPersonal g")})
public class GastoPersonal implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "GP_ID")
    private BigDecimal gpId;
    @Column(name = "GP_CARGO")
    private Double gpCargo;
    @Column(name = "GP_FECHA")
    @Temporal(TemporalType.TIMESTAMP)
    private Date gpFecha;
    @Column(name = "GP_COMENTARIO")
    private String gpComentario;
    @Basic(optional = false)
    @Column(name = "GP_PAGO")
    private double gpPago;
    @JoinColumn(name = "EMP_ID", referencedColumnName = "EMP_ID")
    @ManyToOne(optional = false)
    private Empleados empId;
    @JoinColumn(name = "PROY_ID", referencedColumnName = "PROY_ID")
    @ManyToOne(optional = false)
    private Proyecto proyId;

    public GastoPersonal() {
    }

    public GastoPersonal(BigDecimal gpId) {
        this.gpId = gpId;
    }

    public GastoPersonal(BigDecimal gpId, double gpPago) {
        this.gpId = gpId;
        this.gpPago = gpPago;
    }

    public BigDecimal getGpId() {
        return gpId;
    }

    public void setGpId(BigDecimal gpId) {
        this.gpId = gpId;
    }

    public Double getGpCargo() {
        return gpCargo;
    }

    public void setGpCargo(Double gpCargo) {
        this.gpCargo = gpCargo;
    }

    public Date getGpFecha() {
        return gpFecha;
    }

    public void setGpFecha(Date gpFecha) {
        this.gpFecha = gpFecha;
    }

    public String getGpComentario() {
        return gpComentario;
    }

    public void setGpComentario(String gpComentario) {
        this.gpComentario = gpComentario;
    }

    public double getGpPago() {
        return gpPago;
    }

    public void setGpPago(double gpPago) {
        this.gpPago = gpPago;
    }

    public Empleados getEmpId() {
        return empId;
    }

    public void setEmpId(Empleados empId) {
        this.empId = empId;
    }

    public Proyecto getProyId() {
        return proyId;
    }

    public void setProyId(Proyecto proyId) {
        this.proyId = proyId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (gpId != null ? gpId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GastoPersonal)) {
            return false;
        }
        GastoPersonal other = (GastoPersonal) object;
        if ((this.gpId == null && other.gpId != null) || (this.gpId != null && !this.gpId.equals(other.gpId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AccesoDatos.GastoPersonal[ gpId=" + gpId + " ]";
    }
    
}
