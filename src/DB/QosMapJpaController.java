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
public class QosMapJpaController implements Serializable {

    public QosMapJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(QosMap qosMap) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Reservation resID = qosMap.getResID();
            if (resID != null) {
                resID = em.getReference(resID.getClass(), resID.getId());
                qosMap.setResID(resID);
            }
            em.persist(qosMap);
            if (resID != null) {
                resID.getQosMapCollection().add(qosMap);
                resID = em.merge(resID);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(QosMap qosMap) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            QosMap persistentQosMap = em.find(QosMap.class, qosMap.getId());
            Reservation resIDOld = persistentQosMap.getResID();
            Reservation resIDNew = qosMap.getResID();
            if (resIDNew != null) {
                resIDNew = em.getReference(resIDNew.getClass(), resIDNew.getId());
                qosMap.setResID(resIDNew);
            }
            qosMap = em.merge(qosMap);
            if (resIDOld != null && !resIDOld.equals(resIDNew)) {
                resIDOld.getQosMapCollection().remove(qosMap);
                resIDOld = em.merge(resIDOld);
            }
            if (resIDNew != null && !resIDNew.equals(resIDOld)) {
                resIDNew.getQosMapCollection().add(qosMap);
                resIDNew = em.merge(resIDNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = qosMap.getId();
                if (findQosMap(id) == null) {
                    throw new NonexistentEntityException("The qosMap with id " + id + " no longer exists.");
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
            QosMap qosMap;
            try {
                qosMap = em.getReference(QosMap.class, id);
                qosMap.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The qosMap with id " + id + " no longer exists.", enfe);
            }
            Reservation resID = qosMap.getResID();
            if (resID != null) {
                resID.getQosMapCollection().remove(qosMap);
                resID = em.merge(resID);
            }
            em.remove(qosMap);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<QosMap> findQosMapEntities() {
        return findQosMapEntities(true, -1, -1);
    }

    public List<QosMap> findQosMapEntities(int maxResults, int firstResult) {
        return findQosMapEntities(false, maxResults, firstResult);
    }

    private List<QosMap> findQosMapEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(QosMap.class));
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

    public QosMap findQosMap(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(QosMap.class, id);
        } finally {
            em.close();
        }
    }

    public int getQosMapCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<QosMap> rt = cq.from(QosMap.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
