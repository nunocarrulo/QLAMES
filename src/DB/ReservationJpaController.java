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
import javax.persistence.TypedQuery;

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
        if (reservation.getQosmapCollection() == null) {
            reservation.setQosmapCollection(new ArrayList<Qosmap>());
        }
        if (reservation.getFlowmapCollection() == null) {
            reservation.setFlowmapCollection(new ArrayList<Flowmap>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Qosmap> attachedQosmapCollection = new ArrayList<Qosmap>();
            for (Qosmap qosmapCollectionQosmapToAttach : reservation.getQosmapCollection()) {
                qosmapCollectionQosmapToAttach = em.getReference(qosmapCollectionQosmapToAttach.getClass(), qosmapCollectionQosmapToAttach.getId());
                attachedQosmapCollection.add(qosmapCollectionQosmapToAttach);
            }
            reservation.setQosmapCollection(attachedQosmapCollection);
            Collection<Flowmap> attachedFlowmapCollection = new ArrayList<Flowmap>();
            for (Flowmap flowmapCollectionFlowmapToAttach : reservation.getFlowmapCollection()) {
                flowmapCollectionFlowmapToAttach = em.getReference(flowmapCollectionFlowmapToAttach.getClass(), flowmapCollectionFlowmapToAttach.getId());
                attachedFlowmapCollection.add(flowmapCollectionFlowmapToAttach);
            }
            reservation.setFlowmapCollection(attachedFlowmapCollection);
            em.persist(reservation);
            for (Qosmap qosmapCollectionQosmap : reservation.getQosmapCollection()) {
                Reservation oldResIDOfQosmapCollectionQosmap = qosmapCollectionQosmap.getResID();
                qosmapCollectionQosmap.setResID(reservation);
                qosmapCollectionQosmap = em.merge(qosmapCollectionQosmap);
                if (oldResIDOfQosmapCollectionQosmap != null) {
                    oldResIDOfQosmapCollectionQosmap.getQosmapCollection().remove(qosmapCollectionQosmap);
                    oldResIDOfQosmapCollectionQosmap = em.merge(oldResIDOfQosmapCollectionQosmap);
                }
            }
            for (Flowmap flowmapCollectionFlowmap : reservation.getFlowmapCollection()) {
                Reservation oldResIDOfFlowmapCollectionFlowmap = flowmapCollectionFlowmap.getResID();
                flowmapCollectionFlowmap.setResID(reservation);
                flowmapCollectionFlowmap = em.merge(flowmapCollectionFlowmap);
                if (oldResIDOfFlowmapCollectionFlowmap != null) {
                    oldResIDOfFlowmapCollectionFlowmap.getFlowmapCollection().remove(flowmapCollectionFlowmap);
                    oldResIDOfFlowmapCollectionFlowmap = em.merge(oldResIDOfFlowmapCollectionFlowmap);
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
            Collection<Qosmap> qosmapCollectionOld = persistentReservation.getQosmapCollection();
            Collection<Qosmap> qosmapCollectionNew = reservation.getQosmapCollection();
            Collection<Flowmap> flowmapCollectionOld = persistentReservation.getFlowmapCollection();
            Collection<Flowmap> flowmapCollectionNew = reservation.getFlowmapCollection();
            List<String> illegalOrphanMessages = null;
            for (Qosmap qosmapCollectionOldQosmap : qosmapCollectionOld) {
                if (!qosmapCollectionNew.contains(qosmapCollectionOldQosmap)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Qosmap " + qosmapCollectionOldQosmap + " since its resID field is not nullable.");
                }
            }
            for (Flowmap flowmapCollectionOldFlowmap : flowmapCollectionOld) {
                if (!flowmapCollectionNew.contains(flowmapCollectionOldFlowmap)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Flowmap " + flowmapCollectionOldFlowmap + " since its resID field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Qosmap> attachedQosmapCollectionNew = new ArrayList<Qosmap>();
            for (Qosmap qosmapCollectionNewQosmapToAttach : qosmapCollectionNew) {
                qosmapCollectionNewQosmapToAttach = em.getReference(qosmapCollectionNewQosmapToAttach.getClass(), qosmapCollectionNewQosmapToAttach.getId());
                attachedQosmapCollectionNew.add(qosmapCollectionNewQosmapToAttach);
            }
            qosmapCollectionNew = attachedQosmapCollectionNew;
            reservation.setQosmapCollection(qosmapCollectionNew);
            Collection<Flowmap> attachedFlowmapCollectionNew = new ArrayList<Flowmap>();
            for (Flowmap flowmapCollectionNewFlowmapToAttach : flowmapCollectionNew) {
                flowmapCollectionNewFlowmapToAttach = em.getReference(flowmapCollectionNewFlowmapToAttach.getClass(), flowmapCollectionNewFlowmapToAttach.getId());
                attachedFlowmapCollectionNew.add(flowmapCollectionNewFlowmapToAttach);
            }
            flowmapCollectionNew = attachedFlowmapCollectionNew;
            reservation.setFlowmapCollection(flowmapCollectionNew);
            reservation = em.merge(reservation);
            for (Qosmap qosmapCollectionNewQosmap : qosmapCollectionNew) {
                if (!qosmapCollectionOld.contains(qosmapCollectionNewQosmap)) {
                    Reservation oldResIDOfQosmapCollectionNewQosmap = qosmapCollectionNewQosmap.getResID();
                    qosmapCollectionNewQosmap.setResID(reservation);
                    qosmapCollectionNewQosmap = em.merge(qosmapCollectionNewQosmap);
                    if (oldResIDOfQosmapCollectionNewQosmap != null && !oldResIDOfQosmapCollectionNewQosmap.equals(reservation)) {
                        oldResIDOfQosmapCollectionNewQosmap.getQosmapCollection().remove(qosmapCollectionNewQosmap);
                        oldResIDOfQosmapCollectionNewQosmap = em.merge(oldResIDOfQosmapCollectionNewQosmap);
                    }
                }
            }
            for (Flowmap flowmapCollectionNewFlowmap : flowmapCollectionNew) {
                if (!flowmapCollectionOld.contains(flowmapCollectionNewFlowmap)) {
                    Reservation oldResIDOfFlowmapCollectionNewFlowmap = flowmapCollectionNewFlowmap.getResID();
                    flowmapCollectionNewFlowmap.setResID(reservation);
                    flowmapCollectionNewFlowmap = em.merge(flowmapCollectionNewFlowmap);
                    if (oldResIDOfFlowmapCollectionNewFlowmap != null && !oldResIDOfFlowmapCollectionNewFlowmap.equals(reservation)) {
                        oldResIDOfFlowmapCollectionNewFlowmap.getFlowmapCollection().remove(flowmapCollectionNewFlowmap);
                        oldResIDOfFlowmapCollectionNewFlowmap = em.merge(oldResIDOfFlowmapCollectionNewFlowmap);
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
            Collection<Qosmap> qosmapCollectionOrphanCheck = reservation.getQosmapCollection();
            for (Qosmap qosmapCollectionOrphanCheckQosmap : qosmapCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Reservation (" + reservation + ") cannot be destroyed since the Qosmap " + qosmapCollectionOrphanCheckQosmap + " in its qosmapCollection field has a non-nullable resID field.");
            }
            Collection<Flowmap> flowmapCollectionOrphanCheck = reservation.getFlowmapCollection();
            for (Flowmap flowmapCollectionOrphanCheckFlowmap : flowmapCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Reservation (" + reservation + ") cannot be destroyed since the Flowmap " + flowmapCollectionOrphanCheckFlowmap + " in its flowmapCollection field has a non-nullable resID field.");
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
        TypedQuery<Reservation> query = em.createNamedQuery("Reservation.findAllOrderedByStartDate", Reservation.class);
        List<Reservation> results = query.getResultList();
        em.close();
        return results;
        
        /*try {
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
        }*/
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
