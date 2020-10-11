/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import AccesoDatos.Empleados;
import AccesoDatos.GastoPersonal;
import AccesoDatos.Proyecto;
import Controladores.exceptions.NonexistentEntityException;
import Controladores.exceptions.PreexistingEntityException;
import gestor_de_proyectos.interfaces.viewPagar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.swing.JOptionPane;

/**
 *
 * @author Usuario
 */
public class GastoPersonalJpaController implements Serializable {

    public GastoPersonalJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(GastoPersonal gastoPersonal) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Empleados empId = gastoPersonal.getEmpId();            
            if (empId != null) {
                empId = em.getReference(empId.getClass(), empId.getEmpId());
                gastoPersonal.setEmpId(empId);
            }
            Proyecto proyId = gastoPersonal.getProyId();
            if (proyId != null) {
                proyId = em.getReference(proyId.getClass(), proyId.getProyId());
                gastoPersonal.setProyId(proyId);
            }
            em.persist(gastoPersonal);
            if (empId != null) {
                empId.getGastoPersonalList().add(gastoPersonal);
                empId = em.merge(empId);
            }
            if (proyId != null) {
                proyId.getGastoPersonalList().add(gastoPersonal);
                proyId = em.merge(proyId);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findGastoPersonal(gastoPersonal.getGpId()) != null) {
                throw new PreexistingEntityException("GastoPersonal " + gastoPersonal + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(GastoPersonal gastoPersonal) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            GastoPersonal persistentGastoPersonal = em.find(GastoPersonal.class, gastoPersonal.getGpId());
            Empleados empIdOld = persistentGastoPersonal.getEmpId();
            Empleados empIdNew = gastoPersonal.getEmpId();
            Proyecto proyIdOld = persistentGastoPersonal.getProyId();
            Proyecto proyIdNew = gastoPersonal.getProyId();
            if (empIdNew != null) {
                empIdNew = em.getReference(empIdNew.getClass(), empIdNew.getEmpId());
                gastoPersonal.setEmpId(empIdNew);
            }
            if (proyIdNew != null) {
                proyIdNew = em.getReference(proyIdNew.getClass(), proyIdNew.getProyId());
                gastoPersonal.setProyId(proyIdNew);
            }
            gastoPersonal = em.merge(gastoPersonal);
            if (empIdOld != null && !empIdOld.equals(empIdNew)) {
                empIdOld.getGastoPersonalList().remove(gastoPersonal);
                empIdOld = em.merge(empIdOld);
            }
            if (empIdNew != null && !empIdNew.equals(empIdOld)) {
                empIdNew.getGastoPersonalList().add(gastoPersonal);
                empIdNew = em.merge(empIdNew);
            }
            if (proyIdOld != null && !proyIdOld.equals(proyIdNew)) {
                proyIdOld.getGastoPersonalList().remove(gastoPersonal);
                proyIdOld = em.merge(proyIdOld);
            }
            if (proyIdNew != null && !proyIdNew.equals(proyIdOld)) {
                proyIdNew.getGastoPersonalList().add(gastoPersonal);
                proyIdNew = em.merge(proyIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                BigDecimal id = gastoPersonal.getGpId();
                if (findGastoPersonal(id) == null) {
                    throw new NonexistentEntityException("The gastoPersonal with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(BigDecimal id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            GastoPersonal gastoPersonal;
            try {
                gastoPersonal = em.getReference(GastoPersonal.class, id);
                gastoPersonal.getGpId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The gastoPersonal with id " + id + " no longer exists.", enfe);
            }
            Empleados empId = gastoPersonal.getEmpId();
            if (empId != null) {
                empId.getGastoPersonalList().remove(gastoPersonal);
                empId = em.merge(empId);
            }
            Proyecto proyId = gastoPersonal.getProyId();
            if (proyId != null) {
                proyId.getGastoPersonalList().remove(gastoPersonal);
                proyId = em.merge(proyId);
            }
            em.remove(gastoPersonal);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<GastoPersonal> findGastoPersonalEntities() {
        return findGastoPersonalEntities(true, -1, -1);
    }

    public List<GastoPersonal> findGastoPersonalEntities(int maxResults, int firstResult) {
        return findGastoPersonalEntities(false, maxResults, firstResult);
    }

    private List<GastoPersonal> findGastoPersonalEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(GastoPersonal.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public GastoPersonal findGastoPersonal(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(GastoPersonal.class, id);
        } finally {
            em.close();
        }
    }

    public int getGastoPersonalCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<GastoPersonal> rt = cq.from(GastoPersonal.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    
    
    
}
