/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import AccesoDatos.Cargos;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import AccesoDatos.Empleados;
import Controladores.exceptions.NonexistentEntityException;
import Controladores.exceptions.PreexistingEntityException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Usuario
 */
public class CargosJpaController1 implements Serializable {

    public CargosJpaController1(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cargos cargos) throws PreexistingEntityException, Exception {
        if (cargos.getEmpleadosList() == null) {
            cargos.setEmpleadosList(new ArrayList<Empleados>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Empleados> attachedEmpleadosList = new ArrayList<Empleados>();
            for (Empleados empleadosListEmpleadosToAttach : cargos.getEmpleadosList()) {
                empleadosListEmpleadosToAttach = em.getReference(empleadosListEmpleadosToAttach.getClass(), empleadosListEmpleadosToAttach.getEmpId());
                attachedEmpleadosList.add(empleadosListEmpleadosToAttach);
            }
            cargos.setEmpleadosList(attachedEmpleadosList);
            em.persist(cargos);
            for (Empleados empleadosListEmpleados : cargos.getEmpleadosList()) {
                Cargos oldCarIdOfEmpleadosListEmpleados = empleadosListEmpleados.getCarId();
                empleadosListEmpleados.setCarId(cargos);
                empleadosListEmpleados = em.merge(empleadosListEmpleados);
                if (oldCarIdOfEmpleadosListEmpleados != null) {
                    oldCarIdOfEmpleadosListEmpleados.getEmpleadosList().remove(empleadosListEmpleados);
                    oldCarIdOfEmpleadosListEmpleados = em.merge(oldCarIdOfEmpleadosListEmpleados);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCargos(cargos.getCargosId()) != null) {
                throw new PreexistingEntityException("Cargos " + cargos + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cargos cargos) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cargos persistentCargos = em.find(Cargos.class, cargos.getCargosId());
            List<Empleados> empleadosListOld = persistentCargos.getEmpleadosList();
            List<Empleados> empleadosListNew = cargos.getEmpleadosList();
            List<Empleados> attachedEmpleadosListNew = new ArrayList<Empleados>();
            for (Empleados empleadosListNewEmpleadosToAttach : empleadosListNew) {
                empleadosListNewEmpleadosToAttach = em.getReference(empleadosListNewEmpleadosToAttach.getClass(), empleadosListNewEmpleadosToAttach.getEmpId());
                attachedEmpleadosListNew.add(empleadosListNewEmpleadosToAttach);
            }
            empleadosListNew = attachedEmpleadosListNew;
            cargos.setEmpleadosList(empleadosListNew);
            cargos = em.merge(cargos);
            for (Empleados empleadosListOldEmpleados : empleadosListOld) {
                if (!empleadosListNew.contains(empleadosListOldEmpleados)) {
                    empleadosListOldEmpleados.setCarId(null);
                    empleadosListOldEmpleados = em.merge(empleadosListOldEmpleados);
                }
            }
            for (Empleados empleadosListNewEmpleados : empleadosListNew) {
                if (!empleadosListOld.contains(empleadosListNewEmpleados)) {
                    Cargos oldCarIdOfEmpleadosListNewEmpleados = empleadosListNewEmpleados.getCarId();
                    empleadosListNewEmpleados.setCarId(cargos);
                    empleadosListNewEmpleados = em.merge(empleadosListNewEmpleados);
                    if (oldCarIdOfEmpleadosListNewEmpleados != null && !oldCarIdOfEmpleadosListNewEmpleados.equals(cargos)) {
                        oldCarIdOfEmpleadosListNewEmpleados.getEmpleadosList().remove(empleadosListNewEmpleados);
                        oldCarIdOfEmpleadosListNewEmpleados = em.merge(oldCarIdOfEmpleadosListNewEmpleados);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                BigDecimal id = cargos.getCargosId();
                if (findCargos(id) == null) {
                    throw new NonexistentEntityException("The cargos with id " + id + " no longer exists.");
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
            Cargos cargos;
            try {
                cargos = em.getReference(Cargos.class, id);
                cargos.getCargosId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cargos with id " + id + " no longer exists.", enfe);
            }
            List<Empleados> empleadosList = cargos.getEmpleadosList();
            for (Empleados empleadosListEmpleados : empleadosList) {
                empleadosListEmpleados.setCarId(null);
                empleadosListEmpleados = em.merge(empleadosListEmpleados);
            }
            em.remove(cargos);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cargos> findCargosEntities() {
        return findCargosEntities(true, -1, -1);
    }

    public List<Cargos> findCargosEntities(int maxResults, int firstResult) {
        return findCargosEntities(false, maxResults, firstResult);
    }

    private List<Cargos> findCargosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cargos.class));
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

    public Cargos findCargos(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cargos.class, id);
        } finally {
            em.close();
        }
    }

    public int getCargosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cargos> rt = cq.from(Cargos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
