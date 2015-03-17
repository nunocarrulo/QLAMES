/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Automation;

import DB.DB_Manager;
import DB.FlowMap;
import DB.QosMap;
import DB.Reservation;
import Dijkstra.DijkstraOps;
import REST_Requests.Constants;
import REST_Requests.MyJson;
import REST_Requests.MyXML;
import TopologyManagerImpl.FlowConfig;
import TopologyManagerImpl.QosConfig;
import TopologyManagerUtils.Utils;
import java.util.Date;
import java.util.List;

/**
 *
 * @author nuno
 */
public class ReservationHandler {

    private static Date now;
    private static final int Prev = 1;
    private static final int After = 2;
    private static final int Within = 3;
    private static List<QosMap> qosList;
    private static List<FlowMap> flowList;
    private static final QosConfig qc = new QosConfig();
    private static final FlowConfig fc = new FlowConfig();
    private static String srcHostId, dstHostId;
    
    public static void process(List<Reservation> resList){
        
        /* Retrieving actual time */
        now = new Date();
        for(Reservation r : resList){
            
            /* Apply actions according to the dates */
            switch(checkDate(now, r.getStartDate(), r.getEndDate())){
                case Prev:
                        // do nothing
                        break;
                case After:
                        int resID = r.getId();
                        //find all entries on table QosMap associated with this reservationID
                        qosList = DB_Manager.getQosMap(resID);
                        
                        //define qos config
                        qc.setOvsid(Constants.ovsID);
                        
                        //delete all queues on the switches
                        for(QosMap q : qosList){
                            qc.setQueueuuid(q.getQueueUUID());
                            MyJson.sendDelete(Constants.queue, qc);
                        }
                        
                        //find all entries on table FlowMap associated with this reservationID 
                        flowList = DB_Manager.getFlowMap(resID);
                        
                        //delete all the flows on the switches
                        for(FlowMap f : flowList){
                            //define flow config
                            fc.clearFlowConfig();
                            fc.setNodeID(f.getSwID());
                            fc.setTableID(f.getTableID());
                            fc.setFlowID(f.getFlowID());
                            MyXML.sendDelete(Constants.flow, fc);
                        }
                        
                        //delete the entry reservation (and hopefully all other entries will be deleted)
                        DB_Manager.deleteReservation(resID);
                        
                        break;
                case Within:
                        // if flag "applied" is true break
                        if(r.getApplied())
                            break;
                        
                        // Apply Dijkstra to find paths between hosts 
                        // get Hosts ID
                        srcHostId = Utils.topo.getHostByIP(r.getSrcIP());
                        dstHostId = Utils.topo.getHostByIP(r.getDstIP());
                        
                        if( srcHostId.isEmpty() || dstHostId.isEmpty()){
                            System.out.println("Host "+srcHostId+" or "+dstHostId+" not found! Nothing will be added");
                            break;
                        }
                        //Finding path between source and dest
                        System.out.println("Finding path between source "+srcHostId+" and dest "+dstHostId);
                        DijkstraOps.findPath(srcHostId, dstHostId);
                        //create queues
                        //create flows
                        //edit reservation and set flag "applied" true
                        //insert into database with the correspondent reservation id
                        break;
                default:
                    System.out.println("Error processing dates!");
                    break;
            }
        }
        
    }
    
    private static int checkDate(Date now, Date start, Date after){
        if(now.before(start))
            return Prev;
        else if(now.after(after))
            return After;
        else 
            return Within; 
    }
    
}
