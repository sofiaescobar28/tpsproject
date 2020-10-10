/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AccesoDatos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
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
 * @author Manuel
 */
@Entity
@Table(name = "CATEGORIAS")
@NamedQueries({
    @NamedQuery(name = "Categorias.findAll", query = "SELECT c FROM Categorias c")})
public class Categorias implements Serializable {

    @OneToMany(mappedBy = "catId")
    private List<IngresoEgreso> ingresoEgresoList;

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "CAT_ID")
    private BigDecimal catId;
    @Column(name = "CAT_NOMBRE")
    private String catNombre;
    @Column(name = "CAT_TIPO")
    private BigInteger catTipo;

    public Categorias() {
    }

    public Categorias(BigDecimal catId) {
        this.catId = catId;
    }

    public BigDecimal getCatId() {
        return catId;
    }

    public void setCatId(BigDecimal catId) {
        this.catId = catId;
    }

    public String getCatNombre() {
        return catNombre;
    }

    public void setCatNombre(String catNombre) {
        this.catNombre = catNombre;
    }

    public BigInteger getCatTipo() {
        return catTipo;
    }

    public void setCatTipo(BigInteger catTipo) {
        this.catTipo = catTipo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (catId != null ? catId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Categorias)) {
            return false;
        }
        Categorias other = (Categorias) object;
        if ((this.catId == null && other.catId != null) || (this.catId != null && !this.catId.equals(other.catId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AccesoDatos.Categorias[ catId=" + catId + " ]";
    }

    public List<IngresoEgreso> getIngresoEgresoList() {
        return ingresoEgresoList;
    }

    public void setIngresoEgresoList(List<IngresoEgreso> ingresoEgresoList) {
        this.ingresoEgresoList = ingresoEgresoList;
    }
    
}
