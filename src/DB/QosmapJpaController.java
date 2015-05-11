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
public class QosmapJpaController implements Serializable {

    public QosmapJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Qosmap qosmap) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Reservation resID = qosmap.getResID();
            if (resID != null) {
                resID = em.getReference(resID.getClass(), resID.getId());
                qosmap.setResID(resID);
            }
            em.persist(qosmap);
            if (resID != null) {
                resID.getQosmapCollection().add(qosmap);
                resID = em.merge(resID);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Qosmap qosmap) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Qosmap persistentQosmap = em.find(Qosmap.class, qosmap.getId());
            Reservation resIDOld = persistentQosmap.getResID();
            Reservation resIDNew = qosmap.getResID();
            if (resIDNew != null) {
                resIDNew = em.getReference(resIDNew.getClass(), resIDNew.getId());
                qosmap.setResID(resIDNew);
            }
            qosmap = em.merge(qosmap);
            if (resIDOld != null && !resIDOld.equals(resIDNew)) {
                resIDOld.getQosmapCollection().remove(qosmap);
                resIDOld = em.merge(resIDOld);
            }
            if (resIDNew != null && !resIDNew.equals(resIDOld)) {
                resIDNew.getQosmapCollection().add(qosmap);
                resIDNew = em.merge(resIDNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = qosmap.getId();
                if (findQosmap(id) == null) {
                    throw new NonexistentEntityException("The qosmap with id " + id + " no longer exists.");
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
            Qosmap qosmap;
            try {
                qosmap = em.getReference(Qosmap.class, id);
                qosmap.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The qosmap with id " + id + " no longer exists.", enfe);
            }
            Reservation resID = qosmap.getResID();
            if (resID != null) {
                resID.getQosmapCollection().remove(qosmap);
                resID = em.merge(resID);
            }
            em.remove(qosmap);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Qosmap> findQosmapEntities() {
        return findQosmapEntities(true, -1, -1);
    }

    public List<Qosmap> findQosmapEntities(int maxResults, int firstResult) {
        return findQosmapEntities(false, maxResults, firstResult);
    }

    private List<Qosmap> findQosmapEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Qosmap.class));
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

    public Qosmap findQosmap(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Qosmap.class, id);
        } finally {
            em.close();
        }
    }

    public int getQosmapCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Qosmap> rt = cq.from(Qosmap.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
