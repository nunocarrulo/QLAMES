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
public class FlowmapJpaController implements Serializable {

    public FlowmapJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Flowmap flowmap) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Reservation resID = flowmap.getResID();
            if (resID != null) {
                resID = em.getReference(resID.getClass(), resID.getId());
                flowmap.setResID(resID);
            }
            em.persist(flowmap);
            if (resID != null) {
                resID.getFlowmapCollection().add(flowmap);
                resID = em.merge(resID);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Flowmap flowmap) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Flowmap persistentFlowmap = em.find(Flowmap.class, flowmap.getId());
            Reservation resIDOld = persistentFlowmap.getResID();
            Reservation resIDNew = flowmap.getResID();
            if (resIDNew != null) {
                resIDNew = em.getReference(resIDNew.getClass(), resIDNew.getId());
                flowmap.setResID(resIDNew);
            }
            flowmap = em.merge(flowmap);
            if (resIDOld != null && !resIDOld.equals(resIDNew)) {
                resIDOld.getFlowmapCollection().remove(flowmap);
                resIDOld = em.merge(resIDOld);
            }
            if (resIDNew != null && !resIDNew.equals(resIDOld)) {
                resIDNew.getFlowmapCollection().add(flowmap);
                resIDNew = em.merge(resIDNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = flowmap.getId();
                if (findFlowmap(id) == null) {
                    throw new NonexistentEntityException("The flowmap with id " + id + " no longer exists.");
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
            Flowmap flowmap;
            try {
                flowmap = em.getReference(Flowmap.class, id);
                flowmap.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The flowmap with id " + id + " no longer exists.", enfe);
            }
            Reservation resID = flowmap.getResID();
            if (resID != null) {
                resID.getFlowmapCollection().remove(flowmap);
                resID = em.merge(resID);
            }
            em.remove(flowmap);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Flowmap> findFlowmapEntities() {
        return findFlowmapEntities(true, -1, -1);
    }

    public List<Flowmap> findFlowmapEntities(int maxResults, int firstResult) {
        return findFlowmapEntities(false, maxResults, firstResult);
    }

    private List<Flowmap> findFlowmapEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Flowmap.class));
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

    public Flowmap findFlowmap(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Flowmap.class, id);
        } finally {
            em.close();
        }
    }

    public int getFlowmapCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Flowmap> rt = cq.from(Flowmap.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
