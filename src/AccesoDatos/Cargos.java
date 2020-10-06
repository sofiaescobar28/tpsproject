/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AccesoDatos;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Manuel
 */
@Entity
@Table(name = "CARGOS")
@NamedQueries({
    @NamedQuery(name = "Cargos.findAll", query = "SELECT c FROM Cargos c")})
public class Cargos implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "CARGOS_ID")
    private BigDecimal cargosId;
    @Basic(optional = false)
    @Column(name = "CARGOS")
    private String cargos;

    public Cargos() {
    }

    public Cargos(BigDecimal cargosId) {
        this.cargosId = cargosId;
    }

    public Cargos(BigDecimal cargosId, String cargos) {
        this.cargosId = cargosId;
        this.cargos = cargos;
    }

    public BigDecimal getCargosId() {
        return cargosId;
    }

    public void setCargosId(BigDecimal cargosId) {
        this.cargosId = cargosId;
    }

    public String getCargos() {
        return cargos;
    }

    public void setCargos(String cargos) {
        this.cargos = cargos;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cargosId != null ? cargosId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cargos)) {
            return false;
        }
        Cargos other = (Cargos) object;
        if ((this.cargosId == null && other.cargosId != null) || (this.cargosId != null && !this.cargosId.equals(other.cargosId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AccesoDatos.Cargos[ cargosId=" + cargosId + " ]";
    }
    
}
