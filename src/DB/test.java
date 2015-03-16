/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DB;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author nuno
 */
public class test {

    /**
     * @param args the command line arguments
     */
    
    static ReservationJpaController res;
    public static void main(String[] args) {
        getReservations();
        System.exit(0);
        Reservation r = new Reservation();
        r.setPriority(25);
        r.setSrcIP("10.0.0.1");
        r.setDstIP("10.0.0.2");
        
        persist(r);
    }
    public static void getReservations(){
        List<Reservation> resList = new ArrayList<>();
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("lol");
        EntityManager em = emf.createEntityManager();
        res = new ReservationJpaController(emf);
        resList = res.findReservationEntities();
        System.out.println("Printing Reservations...");
        for(Reservation r : resList){
            System.out.println("ID="+r.getId()+" sourceIP= "+r.getSrcIP()+" dstIP= "+r.getDstIP());
        }
    }
    public static void persist(Object object) {
        
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("lol");
        
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {
            em.persist(object);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }
    
}
