/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DB;

import DB.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author nuno
 */
public class FlowMapJpaController implements Serializable {

    public FlowMapJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(FlowMap flowMap) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Reservation resID = flowMap.getResID();
            if (resID != null) {
                resID = em.getReference(resID.getClass(), resID.getId());
                flowMap.setResID(resID);
            }
            em.persist(flowMap);
            if (resID != null) {
                resID.getFlowMapCollection().add(flowMap);
                resID = em.merge(resID);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(FlowMap flowMap) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            FlowMap persistentFlowMap = em.find(FlowMap.class, flowMap.getId());
            Reservation resIDOld = persistentFlowMap.getResID();
            Reservation resIDNew = flowMap.getResID();
            if (resIDNew != null) {
                resIDNew = em.getReference(resIDNew.getClass(), resIDNew.getId());
                flowMap.setResID(resIDNew);
            }
            flowMap = em.merge(flowMap);
            if (resIDOld != null && !resIDOld.equals(resIDNew)) {
                resIDOld.getFlowMapCollection().remove(flowMap);
                resIDOld = em.merge(resIDOld);
            }
            if (resIDNew != null && !resIDNew.equals(resIDOld)) {
                resIDNew.getFlowMapCollection().add(flowMap);
                resIDNew = em.merge(resIDNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = flowMap.getId();
                if (findFlowMap(id) == null) {
                    throw new NonexistentEntityException("The flowMap with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            FlowMap flowMap;
            try {
                flowMap = em.getReference(FlowMap.class, id);
                flowMap.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The flowMap with id " + id + " no longer exists.", enfe);
            }
            Reservation resID = flowMap.getResID();
            if (resID != null) {
                resID.getFlowMapCollection().remove(flowMap);
                resID = em.merge(resID);
            }
            em.remove(flowMap);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<FlowMap> findFlowMapEntities() {
        return findFlowMapEntities(true, -1, -1);
    }

    public List<FlowMap> findFlowMapEntities(int maxResults, int firstResult) {
        return findFlowMapEntities(false, maxResults, firstResult);
    }

    private List<FlowMap> findFlowMapEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(FlowMap.class));
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

    public FlowMap findFlowMap(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(FlowMap.class, id);
        } finally {
            em.close();
        }
    }

    public int getFlowMapCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<FlowMap> rt = cq.from(FlowMap.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
