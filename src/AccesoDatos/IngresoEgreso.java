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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "INGRESO_EGRESO")
@NamedQueries({
    @NamedQuery(name = "IngresoEgreso.findAll", query = "SELECT i FROM IngresoEgreso i")})
public class IngresoEgreso implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "IE_ID")
    private BigDecimal ieId;
    @Column(name = "IE_TIPO")
    private BigInteger ieTipo;
    @Column(name = "IE_FECHA")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ieFecha;
    @Column(name = "IE_DESCRIPCION")
    private String ieDescripcion;
    @Column(name = "IE_CALIDAD")
    private BigInteger ieCalidad;
    @Column(name = "IE_CANTIDAD")
    private Double ieCantidad;
    @Column(name = "IE_MONTO")
    private Double ieMonto;
    @JoinColumn(name = "CAT_ID", referencedColumnName = "CAT_ID")
    @ManyToOne
    private Categorias catId;
    @JoinColumn(name = "PROY_ID", referencedColumnName = "PROY_ID")
    @ManyToOne
    private Proyecto proyId;
    @JoinColumn(name = "UM_ID", referencedColumnName = "UM_ID")
    @ManyToOne
    private UnidadesDeMedida umId;

    public IngresoEgreso() {
    }

    public IngresoEgreso(BigDecimal ieId) {
        this.ieId = ieId;
    }

    public BigDecimal getIeId() {
        return ieId;
    }

    public void setIeId(BigDecimal ieId) {
        this.ieId = ieId;
    }

    public BigInteger getIeTipo() {
        return ieTipo;
    }

    public void setIeTipo(BigInteger ieTipo) {
        this.ieTipo = ieTipo;
    }

    public Date getIeFecha() {
        return ieFecha;
    }

    public void setIeFecha(Date ieFecha) {
        this.ieFecha = ieFecha;
    }

    public String getIeDescripcion() {
        return ieDescripcion;
    }

    public void setIeDescripcion(String ieDescripcion) {
        this.ieDescripcion = ieDescripcion;
    }

    public BigInteger getIeCalidad() {
        return ieCalidad;
    }

    public void setIeCalidad(BigInteger ieCalidad) {
        this.ieCalidad = ieCalidad;
    }

    public Double getIeCantidad() {
        return ieCantidad;
    }

    public void setIeCantidad(Double ieCantidad) {
        this.ieCantidad = ieCantidad;
    }

    public Double getIeMonto() {
        return ieMonto;
    }

    public void setIeMonto(Double ieMonto) {
        this.ieMonto = ieMonto;
    }

    public Categorias getCatId() {
        return catId;
    }

    public void setCatId(Categorias catId) {
        this.catId = catId;
    }

    public Proyecto getProyId() {
        return proyId;
    }

    public void setProyId(Proyecto proyId) {
        this.proyId = proyId;
    }

    public UnidadesDeMedida getUmId() {
        return umId;
    }

    public void setUmId(UnidadesDeMedida umId) {
        this.umId = umId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ieId != null ? ieId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof IngresoEgreso)) {
            return false;
        }
        IngresoEgreso other = (IngresoEgreso) object;
        if ((this.ieId == null && other.ieId != null) || (this.ieId != null && !this.ieId.equals(other.ieId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AccesoDatos.IngresoEgreso[ ieId=" + ieId + " ]";
    }
    
}
