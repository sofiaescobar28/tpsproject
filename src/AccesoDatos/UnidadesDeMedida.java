/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AccesoDatos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Usuario
 */
@Entity
@Table(name = "UNIDADES_DE_MEDIDA")
@NamedQueries({
    @NamedQuery(name = "UnidadesDeMedida.findAll", query = "SELECT u FROM UnidadesDeMedida u")})
public class UnidadesDeMedida implements Serializable {

    @OneToMany(mappedBy = "umId")
    private List<IngresoEgreso> ingresoEgresoList;

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "UM_ID")
    private BigDecimal umId;
    @Basic(optional = false)
    @Column(name = "UM_NOMBRE")
    private String umNombre;

    public UnidadesDeMedida() {
    }

    public UnidadesDeMedida(BigDecimal umId) {
        this.umId = umId;
    }

    public UnidadesDeMedida(BigDecimal umId, String umNombre) {
        this.umId = umId;
        this.umNombre = umNombre;
    }

    public BigDecimal getUmId() {
        return umId;
    }

    public void setUmId(BigDecimal umId) {
        this.umId = umId;
    }

    public String getUmNombre() {
        return umNombre;
    }

    public void setUmNombre(String umNombre) {
        this.umNombre = umNombre;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (umId != null ? umId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UnidadesDeMedida)) {
            return false;
        }
        UnidadesDeMedida other = (UnidadesDeMedida) object;
        if ((this.umId == null && other.umId != null) || (this.umId != null && !this.umId.equals(other.umId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AccesoDatos.UnidadesDeMedida[ umId=" + umId + " ]";
    }

    public List<IngresoEgreso> getIngresoEgresoList() {
        return ingresoEgresoList;
    }

    public void setIngresoEgresoList(List<IngresoEgreso> ingresoEgresoList) {
        this.ingresoEgresoList = ingresoEgresoList;
    }
    
}
