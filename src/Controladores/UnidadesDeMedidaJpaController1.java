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
import AccesoDatos.IngresoEgreso;
import AccesoDatos.UnidadesDeMedida;
import Controladores.exceptions.NonexistentEntityException;
import Controladores.exceptions.PreexistingEntityException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author josse
 */
public class UnidadesDeMedidaJpaController1 implements Serializable {

    public UnidadesDeMedidaJpaController1(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(UnidadesDeMedida unidadesDeMedida) throws PreexistingEntityException, Exception {
        if (unidadesDeMedida.getIngresoEgresoList() == null) {
            unidadesDeMedida.setIngresoEgresoList(new ArrayList<IngresoEgreso>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<IngresoEgreso> attachedIngresoEgresoList = new ArrayList<IngresoEgreso>();
            for (IngresoEgreso ingresoEgresoListIngresoEgresoToAttach : unidadesDeMedida.getIngresoEgresoList()) {
                ingresoEgresoListIngresoEgresoToAttach = em.getReference(ingresoEgresoListIngresoEgresoToAttach.getClass(), ingresoEgresoListIngresoEgresoToAttach.getIeId());
                attachedIngresoEgresoList.add(ingresoEgresoListIngresoEgresoToAttach);
            }
            unidadesDeMedida.setIngresoEgresoList(attachedIngresoEgresoList);
            em.persist(unidadesDeMedida);
            for (IngresoEgreso ingresoEgresoListIngresoEgreso : unidadesDeMedida.getIngresoEgresoList()) {
                UnidadesDeMedida oldUmIdOfIngresoEgresoListIngresoEgreso = ingresoEgresoListIngresoEgreso.getUmId();
                ingresoEgresoListIngresoEgreso.setUmId(unidadesDeMedida);
                ingresoEgresoListIngresoEgreso = em.merge(ingresoEgresoListIngresoEgreso);
                if (oldUmIdOfIngresoEgresoListIngresoEgreso != null) {
                    oldUmIdOfIngresoEgresoListIngresoEgreso.getIngresoEgresoList().remove(ingresoEgresoListIngresoEgreso);
                    oldUmIdOfIngresoEgresoListIngresoEgreso = em.merge(oldUmIdOfIngresoEgresoListIngresoEgreso);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUnidadesDeMedida(unidadesDeMedida.getUmId()) != null) {
                throw new PreexistingEntityException("UnidadesDeMedida " + unidadesDeMedida + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(UnidadesDeMedida unidadesDeMedida) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UnidadesDeMedida persistentUnidadesDeMedida = em.find(UnidadesDeMedida.class, unidadesDeMedida.getUmId());
            List<IngresoEgreso> ingresoEgresoListOld = persistentUnidadesDeMedida.getIngresoEgresoList();
            List<IngresoEgreso> ingresoEgresoListNew = unidadesDeMedida.getIngresoEgresoList();
            List<IngresoEgreso> attachedIngresoEgresoListNew = new ArrayList<IngresoEgreso>();
            for (IngresoEgreso ingresoEgresoListNewIngresoEgresoToAttach : ingresoEgresoListNew) {
                ingresoEgresoListNewIngresoEgresoToAttach = em.getReference(ingresoEgresoListNewIngresoEgresoToAttach.getClass(), ingresoEgresoListNewIngresoEgresoToAttach.getIeId());
                attachedIngresoEgresoListNew.add(ingresoEgresoListNewIngresoEgresoToAttach);
            }
            ingresoEgresoListNew = attachedIngresoEgresoListNew;
            unidadesDeMedida.setIngresoEgresoList(ingresoEgresoListNew);
            unidadesDeMedida = em.merge(unidadesDeMedida);
            for (IngresoEgreso ingresoEgresoListOldIngresoEgreso : ingresoEgresoListOld) {
                if (!ingresoEgresoListNew.contains(ingresoEgresoListOldIngresoEgreso)) {
                    ingresoEgresoListOldIngresoEgreso.setUmId(null);
                    ingresoEgresoListOldIngresoEgreso = em.merge(ingresoEgresoListOldIngresoEgreso);
                }
            }
            for (IngresoEgreso ingresoEgresoListNewIngresoEgreso : ingresoEgresoListNew) {
                if (!ingresoEgresoListOld.contains(ingresoEgresoListNewIngresoEgreso)) {
                    UnidadesDeMedida oldUmIdOfIngresoEgresoListNewIngresoEgreso = ingresoEgresoListNewIngresoEgreso.getUmId();
                    ingresoEgresoListNewIngresoEgreso.setUmId(unidadesDeMedida);
                    ingresoEgresoListNewIngresoEgreso = em.merge(ingresoEgresoListNewIngresoEgreso);
                    if (oldUmIdOfIngresoEgresoListNewIngresoEgreso != null && !oldUmIdOfIngresoEgresoListNewIngresoEgreso.equals(unidadesDeMedida)) {
                        oldUmIdOfIngresoEgresoListNewIngresoEgreso.getIngresoEgresoList().remove(ingresoEgresoListNewIngresoEgreso);
                        oldUmIdOfIngresoEgresoListNewIngresoEgreso = em.merge(oldUmIdOfIngresoEgresoListNewIngresoEgreso);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                BigDecimal id = unidadesDeMedida.getUmId();
                if (findUnidadesDeMedida(id) == null) {
                    throw new NonexistentEntityException("The unidadesDeMedida with id " + id + " no longer exists.");
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
            UnidadesDeMedida unidadesDeMedida;
            try {
                unidadesDeMedida = em.getReference(UnidadesDeMedida.class, id);
                unidadesDeMedida.getUmId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The unidadesDeMedida with id " + id + " no longer exists.", enfe);
            }
            List<IngresoEgreso> ingresoEgresoList = unidadesDeMedida.getIngresoEgresoList();
            for (IngresoEgreso ingresoEgresoListIngresoEgreso : ingresoEgresoList) {
                ingresoEgresoListIngresoEgreso.setUmId(null);
                ingresoEgresoListIngresoEgreso = em.merge(ingresoEgresoListIngresoEgreso);
            }
            em.remove(unidadesDeMedida);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<UnidadesDeMedida> findUnidadesDeMedidaEntities() {
        return findUnidadesDeMedidaEntities(true, -1, -1);
    }

    public List<UnidadesDeMedida> findUnidadesDeMedidaEntities(int maxResults, int firstResult) {
        return findUnidadesDeMedidaEntities(false, maxResults, firstResult);
    }

    private List<UnidadesDeMedida> findUnidadesDeMedidaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(UnidadesDeMedida.class));
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

    public UnidadesDeMedida findUnidadesDeMedida(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(UnidadesDeMedida.class, id);
        } finally {
            em.close();
        }
    }

    public int getUnidadesDeMedidaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<UnidadesDeMedida> rt = cq.from(UnidadesDeMedida.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
