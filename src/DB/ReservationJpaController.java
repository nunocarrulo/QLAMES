/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DB;

import DB.exceptions.IllegalOrphanException;
import DB.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author nuno
 */
public class ReservationJpaController implements Serializable {

    public ReservationJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Reservation reservation) {
        if (reservation.getQosMapCollection() == null) {
            reservation.setQosMapCollection(new ArrayList<QosMap>());
        }
        if (reservation.getFlowMapCollection() == null) {
            reservation.setFlowMapCollection(new ArrayList<FlowMap>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<QosMap> attachedQosMapCollection = new ArrayList<QosMap>();
            for (QosMap qosMapCollectionQosMapToAttach : reservation.getQosMapCollection()) {
                qosMapCollectionQosMapToAttach = em.getReference(qosMapCollectionQosMapToAttach.getClass(), qosMapCollectionQosMapToAttach.getId());
                attachedQosMapCollection.add(qosMapCollectionQosMapToAttach);
            }
            reservation.setQosMapCollection(attachedQosMapCollection);
            Collection<FlowMap> attachedFlowMapCollection = new ArrayList<FlowMap>();
            for (FlowMap flowMapCollectionFlowMapToAttach : reservation.getFlowMapCollection()) {
                flowMapCollectionFlowMapToAttach = em.getReference(flowMapCollectionFlowMapToAttach.getClass(), flowMapCollectionFlowMapToAttach.getId());
                attachedFlowMapCollection.add(flowMapCollectionFlowMapToAttach);
            }
            reservation.setFlowMapCollection(attachedFlowMapCollection);
            em.persist(reservation);
            for (QosMap qosMapCollectionQosMap : reservation.getQosMapCollection()) {
                Reservation oldResIDOfQosMapCollectionQosMap = qosMapCollectionQosMap.getResID();
                qosMapCollectionQosMap.setResID(reservation);
                qosMapCollectionQosMap = em.merge(qosMapCollectionQosMap);
                if (oldResIDOfQosMapCollectionQosMap != null) {
                    oldResIDOfQosMapCollectionQosMap.getQosMapCollection().remove(qosMapCollectionQosMap);
                    oldResIDOfQosMapCollectionQosMap = em.merge(oldResIDOfQosMapCollectionQosMap);
                }
            }
            for (FlowMap flowMapCollectionFlowMap : reservation.getFlowMapCollection()) {
                Reservation oldResIDOfFlowMapCollectionFlowMap = flowMapCollectionFlowMap.getResID();
                flowMapCollectionFlowMap.setResID(reservation);
                flowMapCollectionFlowMap = em.merge(flowMapCollectionFlowMap);
                if (oldResIDOfFlowMapCollectionFlowMap != null) {
                    oldResIDOfFlowMapCollectionFlowMap.getFlowMapCollection().remove(flowMapCollectionFlowMap);
                    oldResIDOfFlowMapCollectionFlowMap = em.merge(oldResIDOfFlowMapCollectionFlowMap);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Reservation reservation) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Reservation persistentReservation = em.find(Reservation.class, reservation.getId());
            Collection<QosMap> qosMapCollectionOld = persistentReservation.getQosMapCollection();
            Collection<QosMap> qosMapCollectionNew = reservation.getQosMapCollection();
            Collection<FlowMap> flowMapCollectionOld = persistentReservation.getFlowMapCollection();
            Collection<FlowMap> flowMapCollectionNew = reservation.getFlowMapCollection();
            List<String> illegalOrphanMessages = null;
            for (QosMap qosMapCollectionOldQosMap : qosMapCollectionOld) {
                if (!qosMapCollectionNew.contains(qosMapCollectionOldQosMap)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain QosMap " + qosMapCollectionOldQosMap + " since its resID field is not nullable.");
                }
            }
            for (FlowMap flowMapCollectionOldFlowMap : flowMapCollectionOld) {
                if (!flowMapCollectionNew.contains(flowMapCollectionOldFlowMap)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain FlowMap " + flowMapCollectionOldFlowMap + " since its resID field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<QosMap> attachedQosMapCollectionNew = new ArrayList<QosMap>();
            for (QosMap qosMapCollectionNewQosMapToAttach : qosMapCollectionNew) {
                qosMapCollectionNewQosMapToAttach = em.getReference(qosMapCollectionNewQosMapToAttach.getClass(), qosMapCollectionNewQosMapToAttach.getId());
                attachedQosMapCollectionNew.add(qosMapCollectionNewQosMapToAttach);
            }
            qosMapCollectionNew = attachedQosMapCollectionNew;
            reservation.setQosMapCollection(qosMapCollectionNew);
            Collection<FlowMap> attachedFlowMapCollectionNew = new ArrayList<FlowMap>();
            for (FlowMap flowMapCollectionNewFlowMapToAttach : flowMapCollectionNew) {
                flowMapCollectionNewFlowMapToAttach = em.getReference(flowMapCollectionNewFlowMapToAttach.getClass(), flowMapCollectionNewFlowMapToAttach.getId());
                attachedFlowMapCollectionNew.add(flowMapCollectionNewFlowMapToAttach);
            }
            flowMapCollectionNew = attachedFlowMapCollectionNew;
            reservation.setFlowMapCollection(flowMapCollectionNew);
            reservation = em.merge(reservation);
            for (QosMap qosMapCollectionNewQosMap : qosMapCollectionNew) {
                if (!qosMapCollectionOld.contains(qosMapCollectionNewQosMap)) {
                    Reservation oldResIDOfQosMapCollectionNewQosMap = qosMapCollectionNewQosMap.getResID();
                    qosMapCollectionNewQosMap.setResID(reservation);
                    qosMapCollectionNewQosMap = em.merge(qosMapCollectionNewQosMap);
                    if (oldResIDOfQosMapCollectionNewQosMap != null && !oldResIDOfQosMapCollectionNewQosMap.equals(reservation)) {
                        oldResIDOfQosMapCollectionNewQosMap.getQosMapCollection().remove(qosMapCollectionNewQosMap);
                        oldResIDOfQosMapCollectionNewQosMap = em.merge(oldResIDOfQosMapCollectionNewQosMap);
                    }
                }
            }
            for (FlowMap flowMapCollectionNewFlowMap : flowMapCollectionNew) {
                if (!flowMapCollectionOld.contains(flowMapCollectionNewFlowMap)) {
                    Reservation oldResIDOfFlowMapCollectionNewFlowMap = flowMapCollectionNewFlowMap.getResID();
                    flowMapCollectionNewFlowMap.setResID(reservation);
                    flowMapCollectionNewFlowMap = em.merge(flowMapCollectionNewFlowMap);
                    if (oldResIDOfFlowMapCollectionNewFlowMap != null && !oldResIDOfFlowMapCollectionNewFlowMap.equals(reservation)) {
                        oldResIDOfFlowMapCollectionNewFlowMap.getFlowMapCollection().remove(flowMapCollectionNewFlowMap);
                        oldResIDOfFlowMapCollectionNewFlowMap = em.merge(oldResIDOfFlowMapCollectionNewFlowMap);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = reservation.getId();
                if (findReservation(id) == null) {
                    throw new NonexistentEntityException("The reservation with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Reservation reservation;
            try {
                reservation = em.getReference(Reservation.class, id);
                reservation.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The reservation with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<QosMap> qosMapCollectionOrphanCheck = reservation.getQosMapCollection();
            for (QosMap qosMapCollectionOrphanCheckQosMap : qosMapCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Reservation (" + reservation + ") cannot be destroyed since the QosMap " + qosMapCollectionOrphanCheckQosMap + " in its qosMapCollection field has a non-nullable resID field.");
            }
            Collection<FlowMap> flowMapCollectionOrphanCheck = reservation.getFlowMapCollection();
            for (FlowMap flowMapCollectionOrphanCheckFlowMap : flowMapCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Reservation (" + reservation + ") cannot be destroyed since the FlowMap " + flowMapCollectionOrphanCheckFlowMap + " in its flowMapCollection field has a non-nullable resID field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(reservation);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Reservation> findReservationEntities() {
        return findReservationEntities(true, -1, -1);
    }

    public List<Reservation> findReservationEntities(int maxResults, int firstResult) {
        return findReservationEntities(false, maxResults, firstResult);
    }

    private List<Reservation> findReservationEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Reservation.class));
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

    public Reservation findReservation(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Reservation.class, id);
        } finally {
            em.close();
        }
    }

    public int getReservationCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Reservation> rt = cq.from(Reservation.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
