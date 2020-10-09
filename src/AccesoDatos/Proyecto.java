/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AccesoDatos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author josse
 */
@Entity
@Table(name = "PROYECTO")
@NamedQueries({
    @NamedQuery(name = "Proyecto.findAll", query = "SELECT p FROM Proyecto p")})
public class Proyecto implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "PROY_ID")
    private BigDecimal proyId;
    @Column(name = "PROY_NOMBRE")
    private String proyNombre;
    @Column(name = "PROY_FECHA")
    @Temporal(TemporalType.TIMESTAMP)
    private Date proyFecha;
    @Column(name = "PROY_ESTADO")
    private BigInteger proyEstado;

    public Proyecto() {
    }
    
    

    public Proyecto(BigDecimal proyId) {
        this.proyId = proyId;
    }

    public BigDecimal getProyId() {
        return proyId;
    }

    public void setProyId(BigDecimal proyId) {
        this.proyId = proyId;
    }

    public String getProyNombre() {
        return proyNombre;
    }

    public void setProyNombre(String proyNombre) {
        this.proyNombre = proyNombre;
    }

    public Date getProyFecha() {
        return proyFecha;
    }

    public void setProyFecha(Date proyFecha) {
        this.proyFecha = proyFecha;
    }

    public BigInteger getProyEstado() {
        return proyEstado;
    }

    public void setProyEstado(BigInteger proyEstado) {
        this.proyEstado = proyEstado;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (proyId != null ? proyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Proyecto)) {
            return false;
        }
        Proyecto other = (Proyecto) object;
        if ((this.proyId == null && other.proyId != null) || (this.proyId != null && !this.proyId.equals(other.proyId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AccesoDatos.Proyecto[ proyId=" + proyId + " ]";
    }
    
}
