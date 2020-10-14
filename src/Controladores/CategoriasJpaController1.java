/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import AccesoDatos.Categorias;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import AccesoDatos.IngresoEgreso;
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
public class CategoriasJpaController1 implements Serializable {

    public CategoriasJpaController1(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Categorias categorias) throws PreexistingEntityException, Exception {
        if (categorias.getIngresoEgresoList() == null) {
            categorias.setIngresoEgresoList(new ArrayList<IngresoEgreso>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<IngresoEgreso> attachedIngresoEgresoList = new ArrayList<IngresoEgreso>();
            for (IngresoEgreso ingresoEgresoListIngresoEgresoToAttach : categorias.getIngresoEgresoList()) {
                ingresoEgresoListIngresoEgresoToAttach = em.getReference(ingresoEgresoListIngresoEgresoToAttach.getClass(), ingresoEgresoListIngresoEgresoToAttach.getIeId());
                attachedIngresoEgresoList.add(ingresoEgresoListIngresoEgresoToAttach);
            }
            categorias.setIngresoEgresoList(attachedIngresoEgresoList);
            em.persist(categorias);
            for (IngresoEgreso ingresoEgresoListIngresoEgreso : categorias.getIngresoEgresoList()) {
                Categorias oldCatIdOfIngresoEgresoListIngresoEgreso = ingresoEgresoListIngresoEgreso.getCatId();
                ingresoEgresoListIngresoEgreso.setCatId(categorias);
                ingresoEgresoListIngresoEgreso = em.merge(ingresoEgresoListIngresoEgreso);
                if (oldCatIdOfIngresoEgresoListIngresoEgreso != null) {
                    oldCatIdOfIngresoEgresoListIngresoEgreso.getIngresoEgresoList().remove(ingresoEgresoListIngresoEgreso);
                    oldCatIdOfIngresoEgresoListIngresoEgreso = em.merge(oldCatIdOfIngresoEgresoListIngresoEgreso);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCategorias(categorias.getCatId()) != null) {
                throw new PreexistingEntityException("Categorias " + categorias + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Categorias categorias) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Categorias persistentCategorias = em.find(Categorias.class, categorias.getCatId());
            List<IngresoEgreso> ingresoEgresoListOld = persistentCategorias.getIngresoEgresoList();
            List<IngresoEgreso> ingresoEgresoListNew = categorias.getIngresoEgresoList();
            List<IngresoEgreso> attachedIngresoEgresoListNew = new ArrayList<IngresoEgreso>();
            for (IngresoEgreso ingresoEgresoListNewIngresoEgresoToAttach : ingresoEgresoListNew) {
                ingresoEgresoListNewIngresoEgresoToAttach = em.getReference(ingresoEgresoListNewIngresoEgresoToAttach.getClass(), ingresoEgresoListNewIngresoEgresoToAttach.getIeId());
                attachedIngresoEgresoListNew.add(ingresoEgresoListNewIngresoEgresoToAttach);
            }
            ingresoEgresoListNew = attachedIngresoEgresoListNew;
            categorias.setIngresoEgresoList(ingresoEgresoListNew);
            categorias = em.merge(categorias);
            for (IngresoEgreso ingresoEgresoListOldIngresoEgreso : ingresoEgresoListOld) {
                if (!ingresoEgresoListNew.contains(ingresoEgresoListOldIngresoEgreso)) {
                    ingresoEgresoListOldIngresoEgreso.setCatId(null);
                    ingresoEgresoListOldIngresoEgreso = em.merge(ingresoEgresoListOldIngresoEgreso);
                }
            }
            for (IngresoEgreso ingresoEgresoListNewIngresoEgreso : ingresoEgresoListNew) {
                if (!ingresoEgresoListOld.contains(ingresoEgresoListNewIngresoEgreso)) {
                    Categorias oldCatIdOfIngresoEgresoListNewIngresoEgreso = ingresoEgresoListNewIngresoEgreso.getCatId();
                    ingresoEgresoListNewIngresoEgreso.setCatId(categorias);
                    ingresoEgresoListNewIngresoEgreso = em.merge(ingresoEgresoListNewIngresoEgreso);
                    if (oldCatIdOfIngresoEgresoListNewIngresoEgreso != null && !oldCatIdOfIngresoEgresoListNewIngresoEgreso.equals(categorias)) {
                        oldCatIdOfIngresoEgresoListNewIngresoEgreso.getIngresoEgresoList().remove(ingresoEgresoListNewIngresoEgreso);
                        oldCatIdOfIngresoEgresoListNewIngresoEgreso = em.merge(oldCatIdOfIngresoEgresoListNewIngresoEgreso);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                BigDecimal id = categorias.getCatId();
                if (findCategorias(id) == null) {
                    throw new NonexistentEntityException("The categorias with id " + id + " no longer exists.");
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
            Categorias categorias;
            try {
                categorias = em.getReference(Categorias.class, id);
                categorias.getCatId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The categorias with id " + id + " no longer exists.", enfe);
            }
            List<IngresoEgreso> ingresoEgresoList = categorias.getIngresoEgresoList();
            for (IngresoEgreso ingresoEgresoListIngresoEgreso : ingresoEgresoList) {
                ingresoEgresoListIngresoEgreso.setCatId(null);
                ingresoEgresoListIngresoEgreso = em.merge(ingresoEgresoListIngresoEgreso);
            }
            em.remove(categorias);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Categorias> findCategoriasEntities() {
        return findCategoriasEntities(true, -1, -1);
    }

    public List<Categorias> findCategoriasEntities(int maxResults, int firstResult) {
        return findCategoriasEntities(false, maxResults, firstResult);
    }

    private List<Categorias> findCategoriasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Categorias.class));
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

    public Categorias findCategorias(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Categorias.class, id);
        } finally {
            em.close();
        }
    }

    public int getCategoriasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Categorias> rt = cq.from(Categorias.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
