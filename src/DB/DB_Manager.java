/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DB;

import DB.exceptions.IllegalOrphanException;
import DB.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author nuno
 */
public class DB_Manager {

    private static final boolean debug = true;
    private static List<Reservation> resList = new ArrayList<>();
    private static List<Flowmap> flow = new ArrayList<>();
    private static List<Qosmap> qos = new ArrayList<>();
    private static ReservationJpaController resCtl;
    private static FlowmapJpaController fmCtl;
    private static QosmapJpaController qmCtl;
    private static EntityManagerFactory emf;
    private static EntityManager em;
    
    
    public static Reservation getReservation(int resID) {
        return resCtl.findReservation(resID);
    }
    
    public static List<Reservation> getReservations() {
        
        resList = resCtl.findReservationEntities();
        
        if (false) {
            System.out.println("Printing existing Reservations ...");
            for (Reservation r : resList) {
                System.out.println("ID = " + r.getId() + " sourceIP = " + r.getSrcIP() + " dstIP = " + r.getDstIP());
            }
        }
        return resList;
    }
    
    public static List<Flowmap> getFlowMap(int resId){
        flow.clear();
        for(Flowmap lol : fmCtl.findFlowmapEntities()){
            if(lol.getResID().getId() == resId)
                flow.add(lol);
        }
        return flow;
    }
    
    public static List<Qosmap> getQosMap(int resId){
        qos.clear();
        for(Qosmap lol : qmCtl.findQosmapEntities()){
            if(lol.getResID().getId() == resId)
                qos.add(lol);
        }
        return qos;
    }
    
    public static void editReservation(Reservation r){
        try {
            resCtl.edit(r);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(DB_Manager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DB_Manager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void addFlow(Flowmap flow){
        fmCtl.create(flow);
    }
    
    public static void addQos(Qosmap qos){
        qmCtl.create(qos);
    }
   
    public static void addReservation(Reservation r){
        resCtl.create(r);
    }
    
    public static void deleteQosMapEntries(int resID){

        qos = qmCtl.findQosmapEntities();
        for(Qosmap q : qos){
            if(q.getResID().getId() == resID)
                try {
                    qmCtl.destroy(q.getId());
            } catch (NonexistentEntityException ex) {
                Logger.getLogger(DB_Manager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void deleteFlowMapEntries(int resID){

        flow = fmCtl.findFlowmapEntities();
        for(Flowmap f : flow){
            if(f.getResID().getId() == resID)
                try {
                    fmCtl.destroy(f.getId());
            } catch (NonexistentEntityException ex) {
                Logger.getLogger(DB_Manager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void deleteReservation(int resId){
        try {
            resCtl.destroy(resId);
        } catch (IllegalOrphanException | NonexistentEntityException ex) {
            Logger.getLogger(DB_Manager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static void deleteAllReservations(){
        for(Reservation r : getReservations()){
            try {
                resCtl.destroy(r.getId());
            } catch (IllegalOrphanException | NonexistentEntityException ex) {
                Logger.getLogger(DB_Manager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(debug)
            System.out.println("All Reservations were deleted from database!");
    }

    public static void prepareDB() {
        emf = javax.persistence.Persistence.createEntityManagerFactory("hi");
        em = emf.createEntityManager();
        resCtl = new ReservationJpaController(emf);
        fmCtl = new FlowmapJpaController(emf);
        qmCtl = new QosmapJpaController(emf);
    }
    public static void refreshDB(){
        
        for(Reservation a : resList){
            em.merge(a);
            em.refresh(a);
        }
    }
    
    public static void closeDB(){
        em.close();
        emf.close();
        
    }

}
