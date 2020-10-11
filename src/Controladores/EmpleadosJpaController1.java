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
import AccesoDatos.Cargos;
import AccesoDatos.Empleados;
import AccesoDatos.GastoPersonal;
import Controladores.exceptions.IllegalOrphanException;
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
public class EmpleadosJpaController1 implements Serializable {

    public EmpleadosJpaController1(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Empleados empleados) throws PreexistingEntityException, Exception {
        if (empleados.getGastoPersonalList() == null) {
            empleados.setGastoPersonalList(new ArrayList<GastoPersonal>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cargos carId = empleados.getCarId();
            if (carId != null) {
                carId = em.getReference(carId.getClass(), carId.getCargosId());
                empleados.setCarId(carId);
            }
            List<GastoPersonal> attachedGastoPersonalList = new ArrayList<GastoPersonal>();
            for (GastoPersonal gastoPersonalListGastoPersonalToAttach : empleados.getGastoPersonalList()) {
                gastoPersonalListGastoPersonalToAttach = em.getReference(gastoPersonalListGastoPersonalToAttach.getClass(), gastoPersonalListGastoPersonalToAttach.getGpId());
                attachedGastoPersonalList.add(gastoPersonalListGastoPersonalToAttach);
            }
            empleados.setGastoPersonalList(attachedGastoPersonalList);
            em.persist(empleados);
            if (carId != null) {
                carId.getEmpleadosList().add(empleados);
                carId = em.merge(carId);
            }
            for (GastoPersonal gastoPersonalListGastoPersonal : empleados.getGastoPersonalList()) {
                Empleados oldEmpIdOfGastoPersonalListGastoPersonal = gastoPersonalListGastoPersonal.getEmpId();
                gastoPersonalListGastoPersonal.setEmpId(empleados);
                gastoPersonalListGastoPersonal = em.merge(gastoPersonalListGastoPersonal);
                if (oldEmpIdOfGastoPersonalListGastoPersonal != null) {
                    oldEmpIdOfGastoPersonalListGastoPersonal.getGastoPersonalList().remove(gastoPersonalListGastoPersonal);
                    oldEmpIdOfGastoPersonalListGastoPersonal = em.merge(oldEmpIdOfGastoPersonalListGastoPersonal);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findEmpleados(empleados.getEmpId()) != null) {
                throw new PreexistingEntityException("Empleados " + empleados + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Empleados empleados) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Empleados persistentEmpleados = em.find(Empleados.class, empleados.getEmpId());
            Cargos carIdOld = persistentEmpleados.getCarId();
            Cargos carIdNew = empleados.getCarId();
            List<GastoPersonal> gastoPersonalListOld = persistentEmpleados.getGastoPersonalList();
            List<GastoPersonal> gastoPersonalListNew = empleados.getGastoPersonalList();
            List<String> illegalOrphanMessages = null;
            for (GastoPersonal gastoPersonalListOldGastoPersonal : gastoPersonalListOld) {
                if (!gastoPersonalListNew.contains(gastoPersonalListOldGastoPersonal)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain GastoPersonal " + gastoPersonalListOldGastoPersonal + " since its empId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (carIdNew != null) {
                carIdNew = em.getReference(carIdNew.getClass(), carIdNew.getCargosId());
                empleados.setCarId(carIdNew);
            }
            List<GastoPersonal> attachedGastoPersonalListNew = new ArrayList<GastoPersonal>();
            for (GastoPersonal gastoPersonalListNewGastoPersonalToAttach : gastoPersonalListNew) {
                gastoPersonalListNewGastoPersonalToAttach = em.getReference(gastoPersonalListNewGastoPersonalToAttach.getClass(), gastoPersonalListNewGastoPersonalToAttach.getGpId());
                attachedGastoPersonalListNew.add(gastoPersonalListNewGastoPersonalToAttach);
            }
            gastoPersonalListNew = attachedGastoPersonalListNew;
            empleados.setGastoPersonalList(gastoPersonalListNew);
            empleados = em.merge(empleados);
            if (carIdOld != null && !carIdOld.equals(carIdNew)) {
                carIdOld.getEmpleadosList().remove(empleados);
                carIdOld = em.merge(carIdOld);
            }
            if (carIdNew != null && !carIdNew.equals(carIdOld)) {
                carIdNew.getEmpleadosList().add(empleados);
                carIdNew = em.merge(carIdNew);
            }
            for (GastoPersonal gastoPersonalListNewGastoPersonal : gastoPersonalListNew) {
                if (!gastoPersonalListOld.contains(gastoPersonalListNewGastoPersonal)) {
                    Empleados oldEmpIdOfGastoPersonalListNewGastoPersonal = gastoPersonalListNewGastoPersonal.getEmpId();
                    gastoPersonalListNewGastoPersonal.setEmpId(empleados);
                    gastoPersonalListNewGastoPersonal = em.merge(gastoPersonalListNewGastoPersonal);
                    if (oldEmpIdOfGastoPersonalListNewGastoPersonal != null && !oldEmpIdOfGastoPersonalListNewGastoPersonal.equals(empleados)) {
                        oldEmpIdOfGastoPersonalListNewGastoPersonal.getGastoPersonalList().remove(gastoPersonalListNewGastoPersonal);
                        oldEmpIdOfGastoPersonalListNewGastoPersonal = em.merge(oldEmpIdOfGastoPersonalListNewGastoPersonal);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                BigDecimal id = empleados.getEmpId();
                if (findEmpleados(id) == null) {
                    throw new NonexistentEntityException("The empleados with id " + id + " no longer exists.");
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
            Empleados empleados;
            try {
                empleados = em.getReference(Empleados.class, id);
                empleados.getEmpId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The empleados with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<GastoPersonal> gastoPersonalListOrphanCheck = empleados.getGastoPersonalList();
            for (GastoPersonal gastoPersonalListOrphanCheckGastoPersonal : gastoPersonalListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Empleados (" + empleados + ") cannot be destroyed since the GastoPersonal " + gastoPersonalListOrphanCheckGastoPersonal + " in its gastoPersonalList field has a non-nullable empId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Cargos carId = empleados.getCarId();
            if (carId != null) {
                carId.getEmpleadosList().remove(empleados);
                carId = em.merge(carId);
            }
            em.remove(empleados);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Empleados> findEmpleadosEntities() {
        return findEmpleadosEntities(true, -1, -1);
    }

    public List<Empleados> findEmpleadosEntities(int maxResults, int firstResult) {
        return findEmpleadosEntities(false, maxResults, firstResult);
    }

    private List<Empleados> findEmpleadosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Empleados.class));
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

    public Empleados findEmpleados(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Empleados.class, id);
        } finally {
            em.close();
        }
    }

    public int getEmpleadosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Empleados> rt = cq.from(Empleados.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
