/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DB;

import java.util.List;

/**
 *
 * @author nuno
 */
public interface IDB_Manager {
    /**
     * Returns all Reservations on the database
     * @return List of reservation objects
     */
    public List<Reservation> getReservations();
    
    /**
     * Returns all FlowMap entries with the specified reservation id
     * @param resId Reservation ID
     * @return 
     */
    public List<FlowMap> getFlowMap(int resId);
    
    /**
     * Returns all QosMap entries with the specified reservation id
     * @param resId Reservation ID
     * @return 
     */
    
    public List<QosMap> getQosMap(int resId);
    
    /**
     * Inserts the flow parameters from a specific reservation
     * @param flow FlowMap to be inserted on database
     */
    public void addQueue(FlowMap flow);
    
    /**
     * Inserts the qos (queue) parameters from a specific reservation
     * @param qos 
     */
    public void addQos(QosMap qos);
    
    /**
     * Inserts a reservation on database
     * @param r Reservation object to be inserted on database
     */
    public void addReservation(Reservation r);
    
    /**
     * Deletes the reservation with id "resId" from database
     * @param resId Reservation ID (PK) to be deleted from database
     */
    public void deleteReservation(int resId);
    
    /**
     * Deletes all reservations from the database
     */
    public void deleteAllReservations();
}
