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
import java.util.ArrayList;
import java.util.List;
import AccesoDatos.GastoPersonal;
import AccesoDatos.Proyecto;
import Controladores.exceptions.IllegalOrphanException;
import Controladores.exceptions.NonexistentEntityException;
import Controladores.exceptions.PreexistingEntityException;
import java.math.BigDecimal;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Usuario
 */
public class ProyectoJpaController2 implements Serializable {

    public ProyectoJpaController2(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Proyecto proyecto) throws PreexistingEntityException, Exception {
        if (proyecto.getIngresoEgresoList() == null) {
            proyecto.setIngresoEgresoList(new ArrayList<IngresoEgreso>());
        }
        if (proyecto.getGastoPersonalList() == null) {
            proyecto.setGastoPersonalList(new ArrayList<GastoPersonal>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<IngresoEgreso> attachedIngresoEgresoList = new ArrayList<IngresoEgreso>();
            for (IngresoEgreso ingresoEgresoListIngresoEgresoToAttach : proyecto.getIngresoEgresoList()) {
                ingresoEgresoListIngresoEgresoToAttach = em.getReference(ingresoEgresoListIngresoEgresoToAttach.getClass(), ingresoEgresoListIngresoEgresoToAttach.getIeId());
                attachedIngresoEgresoList.add(ingresoEgresoListIngresoEgresoToAttach);
            }
            proyecto.setIngresoEgresoList(attachedIngresoEgresoList);
            List<GastoPersonal> attachedGastoPersonalList = new ArrayList<GastoPersonal>();
            for (GastoPersonal gastoPersonalListGastoPersonalToAttach : proyecto.getGastoPersonalList()) {
                gastoPersonalListGastoPersonalToAttach = em.getReference(gastoPersonalListGastoPersonalToAttach.getClass(), gastoPersonalListGastoPersonalToAttach.getGpId());
                attachedGastoPersonalList.add(gastoPersonalListGastoPersonalToAttach);
            }
            proyecto.setGastoPersonalList(attachedGastoPersonalList);
            em.persist(proyecto);
            for (IngresoEgreso ingresoEgresoListIngresoEgreso : proyecto.getIngresoEgresoList()) {
                Proyecto oldProyIdOfIngresoEgresoListIngresoEgreso = ingresoEgresoListIngresoEgreso.getProyId();
                ingresoEgresoListIngresoEgreso.setProyId(proyecto);
                ingresoEgresoListIngresoEgreso = em.merge(ingresoEgresoListIngresoEgreso);
                if (oldProyIdOfIngresoEgresoListIngresoEgreso != null) {
                    oldProyIdOfIngresoEgresoListIngresoEgreso.getIngresoEgresoList().remove(ingresoEgresoListIngresoEgreso);
                    oldProyIdOfIngresoEgresoListIngresoEgreso = em.merge(oldProyIdOfIngresoEgresoListIngresoEgreso);
                }
            }
            for (GastoPersonal gastoPersonalListGastoPersonal : proyecto.getGastoPersonalList()) {
                Proyecto oldProyIdOfGastoPersonalListGastoPersonal = gastoPersonalListGastoPersonal.getProyId();
                gastoPersonalListGastoPersonal.setProyId(proyecto);
                gastoPersonalListGastoPersonal = em.merge(gastoPersonalListGastoPersonal);
                if (oldProyIdOfGastoPersonalListGastoPersonal != null) {
                    oldProyIdOfGastoPersonalListGastoPersonal.getGastoPersonalList().remove(gastoPersonalListGastoPersonal);
                    oldProyIdOfGastoPersonalListGastoPersonal = em.merge(oldProyIdOfGastoPersonalListGastoPersonal);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findProyecto(proyecto.getProyId()) != null) {
                throw new PreexistingEntityException("Proyecto " + proyecto + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Proyecto proyecto) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Proyecto persistentProyecto = em.find(Proyecto.class, proyecto.getProyId());
            List<IngresoEgreso> ingresoEgresoListOld = persistentProyecto.getIngresoEgresoList();
            List<IngresoEgreso> ingresoEgresoListNew = proyecto.getIngresoEgresoList();
            List<GastoPersonal> gastoPersonalListOld = persistentProyecto.getGastoPersonalList();
            List<GastoPersonal> gastoPersonalListNew = proyecto.getGastoPersonalList();
            List<String> illegalOrphanMessages = null;
            for (GastoPersonal gastoPersonalListOldGastoPersonal : gastoPersonalListOld) {
                if (!gastoPersonalListNew.contains(gastoPersonalListOldGastoPersonal)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain GastoPersonal " + gastoPersonalListOldGastoPersonal + " since its proyId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<IngresoEgreso> attachedIngresoEgresoListNew = new ArrayList<IngresoEgreso>();
            for (IngresoEgreso ingresoEgresoListNewIngresoEgresoToAttach : ingresoEgresoListNew) {
                ingresoEgresoListNewIngresoEgresoToAttach = em.getReference(ingresoEgresoListNewIngresoEgresoToAttach.getClass(), ingresoEgresoListNewIngresoEgresoToAttach.getIeId());
                attachedIngresoEgresoListNew.add(ingresoEgresoListNewIngresoEgresoToAttach);
            }
            ingresoEgresoListNew = attachedIngresoEgresoListNew;
            proyecto.setIngresoEgresoList(ingresoEgresoListNew);
            List<GastoPersonal> attachedGastoPersonalListNew = new ArrayList<GastoPersonal>();
            for (GastoPersonal gastoPersonalListNewGastoPersonalToAttach : gastoPersonalListNew) {
                gastoPersonalListNewGastoPersonalToAttach = em.getReference(gastoPersonalListNewGastoPersonalToAttach.getClass(), gastoPersonalListNewGastoPersonalToAttach.getGpId());
                attachedGastoPersonalListNew.add(gastoPersonalListNewGastoPersonalToAttach);
            }
            gastoPersonalListNew = attachedGastoPersonalListNew;
            proyecto.setGastoPersonalList(gastoPersonalListNew);
            proyecto = em.merge(proyecto);
            for (IngresoEgreso ingresoEgresoListOldIngresoEgreso : ingresoEgresoListOld) {
                if (!ingresoEgresoListNew.contains(ingresoEgresoListOldIngresoEgreso)) {
                    ingresoEgresoListOldIngresoEgreso.setProyId(null);
                    ingresoEgresoListOldIngresoEgreso = em.merge(ingresoEgresoListOldIngresoEgreso);
                }
            }
            for (IngresoEgreso ingresoEgresoListNewIngresoEgreso : ingresoEgresoListNew) {
                if (!ingresoEgresoListOld.contains(ingresoEgresoListNewIngresoEgreso)) {
                    Proyecto oldProyIdOfIngresoEgresoListNewIngresoEgreso = ingresoEgresoListNewIngresoEgreso.getProyId();
                    ingresoEgresoListNewIngresoEgreso.setProyId(proyecto);
                    ingresoEgresoListNewIngresoEgreso = em.merge(ingresoEgresoListNewIngresoEgreso);
                    if (oldProyIdOfIngresoEgresoListNewIngresoEgreso != null && !oldProyIdOfIngresoEgresoListNewIngresoEgreso.equals(proyecto)) {
                        oldProyIdOfIngresoEgresoListNewIngresoEgreso.getIngresoEgresoList().remove(ingresoEgresoListNewIngresoEgreso);
                        oldProyIdOfIngresoEgresoListNewIngresoEgreso = em.merge(oldProyIdOfIngresoEgresoListNewIngresoEgreso);
                    }
                }
            }
            for (GastoPersonal gastoPersonalListNewGastoPersonal : gastoPersonalListNew) {
                if (!gastoPersonalListOld.contains(gastoPersonalListNewGastoPersonal)) {
                    Proyecto oldProyIdOfGastoPersonalListNewGastoPersonal = gastoPersonalListNewGastoPersonal.getProyId();
                    gastoPersonalListNewGastoPersonal.setProyId(proyecto);
                    gastoPersonalListNewGastoPersonal = em.merge(gastoPersonalListNewGastoPersonal);
                    if (oldProyIdOfGastoPersonalListNewGastoPersonal != null && !oldProyIdOfGastoPersonalListNewGastoPersonal.equals(proyecto)) {
                        oldProyIdOfGastoPersonalListNewGastoPersonal.getGastoPersonalList().remove(gastoPersonalListNewGastoPersonal);
                        oldProyIdOfGastoPersonalListNewGastoPersonal = em.merge(oldProyIdOfGastoPersonalListNewGastoPersonal);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                BigDecimal id = proyecto.getProyId();
                if (findProyecto(id) == null) {
                    throw new NonexistentEntityException("The proyecto with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(BigDecimal id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Proyecto proyecto;
            try {
                proyecto = em.getReference(Proyecto.class, id);
                proyecto.getProyId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The proyecto with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<GastoPersonal> gastoPersonalListOrphanCheck = proyecto.getGastoPersonalList();
            for (GastoPersonal gastoPersonalListOrphanCheckGastoPersonal : gastoPersonalListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Proyecto (" + proyecto + ") cannot be destroyed since the GastoPersonal " + gastoPersonalListOrphanCheckGastoPersonal + " in its gastoPersonalList field has a non-nullable proyId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<IngresoEgreso> ingresoEgresoList = proyecto.getIngresoEgresoList();
            for (IngresoEgreso ingresoEgresoListIngresoEgreso : ingresoEgresoList) {
                ingresoEgresoListIngresoEgreso.setProyId(null);
                ingresoEgresoListIngresoEgreso = em.merge(ingresoEgresoListIngresoEgreso);
            }
            em.remove(proyecto);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Proyecto> findProyectoEntities() {
        return findProyectoEntities(true, -1, -1);
    }

    public List<Proyecto> findProyectoEntities(int maxResults, int firstResult) {
        return findProyectoEntities(false, maxResults, firstResult);
    }

    private List<Proyecto> findProyectoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Proyecto.class));
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

    public Proyecto findProyecto(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Proyecto.class, id);
        } finally {
            em.close();
        }
    }

    public int getProyectoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Proyecto> rt = cq.from(Proyecto.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
