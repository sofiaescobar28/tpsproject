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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author josse
 */
@Entity
@Table(name = "USUARIOS")
@NamedQueries({
    @NamedQuery(name = "Usuarios.findAll", query = "SELECT u FROM Usuarios u")})
public class Usuarios implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "USER_ID")
    private BigDecimal userId;
    @Basic(optional = false)
    @Column(name = "USER_NOMBRES")
    private String userNombres;
    @Basic(optional = false)
    @Column(name = "USER_CLAVE")
    private String userClave;
    @Basic(optional = false)
    @Column(name = "USER_CORREO")
    private String userCorreo;
    @Column(name = "USER_ESTADO")
    private BigInteger userEstado;
    @Basic(optional = false)
    @Column(name = "USER_CONTRASENA")
    private String userContrasena;

    public Usuarios() {
    }

    public Usuarios(BigDecimal userId) {
        this.userId = userId;
    }

    public Usuarios(BigDecimal userId, String userNombres, String userClave, String userCorreo, String userContrasena) {
        this.userId = userId;
        this.userNombres = userNombres;
        this.userClave = userClave;
        this.userCorreo = userCorreo;
        this.userContrasena = userContrasena;
    }

    public BigDecimal getUserId() {
        return userId;
    }

    public void setUserId(BigDecimal userId) {
        this.userId = userId;
    }

    public String getUserNombres() {
        return userNombres;
    }

    public void setUserNombres(String userNombres) {
        this.userNombres = userNombres;
    }

    public String getUserClave() {
        return userClave;
    }

    public void setUserClave(String userClave) {
        this.userClave = userClave;
    }

    public String getUserCorreo() {
        return userCorreo;
    }

    public void setUserCorreo(String userCorreo) {
        this.userCorreo = userCorreo;
    }

    public BigInteger getUserEstado() {
        return userEstado;
    }

    public void setUserEstado(BigInteger userEstado) {
        this.userEstado = userEstado;
    }

    public String getUserContrasena() {
        return userContrasena;
    }

    public void setUserContrasena(String userContrasena) {
        this.userContrasena = userContrasena;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userId != null ? userId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Usuarios)) {
            return false;
        }
        Usuarios other = (Usuarios) object;
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AccesoDatos.Usuarios[ userId=" + userId + " ]";
    }
    
}
