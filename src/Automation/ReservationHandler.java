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
import Dijkstra.ParsedPath;
import OVS.Queue;
import REST_Requests.Constants;
import REST_Requests.MyJson;
import REST_Requests.MyXML;
import TopologyManagerImpl.FlowConfig;
import TopologyManagerImpl.Port;
import TopologyManagerImpl.QosConfig;
import TopologyManagerUtils.Utils;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
    private static List<ParsedPath> PP;
    private static List<ParsedPath> revPP;
    private static Queue q;
    private static QosMap qm;
    private static FlowMap fm;
    private static final QosConfig qc = new QosConfig();
    private static final FlowConfig fc = new FlowConfig();
    private static String srcHostId, dstHostId;
    private static int flowCounter = 10;
    private static int queueCounterID = 1;

    public static void process(List<Reservation> resList) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter("queueUUID.txt");
        /* Retrieving actual time */
        now = new Date();
        //Clearing config data structure just in case
        fc.clearFlowConfig();
        qc.clear();

        for (Reservation r : resList) {

            /* Apply actions according to the dates */
            switch (checkDate(now, r.getStartDate(), r.getEndDate())) {
                case Prev:
                    // do nothing
                    System.out.println("------------------------------------------------------------------------------");
                    System.out.println("PREV");
                    break;
                case After:
                    System.out.println("------------------------------------------------------------------------------");
                    System.out.println("AFTER");
                    int resID = r.getId();
                    //find all entries on table QosMap associated with this reservationID
                    qosList = DB_Manager.getQosMap(resID);

                    //define qos config
                    qc.setOvsid(Constants.ovsID);

                    //delete all queues on the switches
                    for (QosMap q : qosList) {
                        qc.setQueueuuid(q.getQueueUUID());
                        MyJson.sendDelete(Constants.queue, qc);
                    }

                    //find all entries on table FlowMap associated with this reservationID 
                    flowList = DB_Manager.getFlowMap(resID);

                    //delete all the flows on the switches
                    for (FlowMap f : flowList) {
                        //define flow config
                        fc.clearFlowConfig();
                        fc.setNodeID(f.getSwID());
                        fc.setTableID(f.getTableID());
                        fc.setFlowID(f.getFlowID());
                        MyXML.sendDelete(Constants.flow, fc);
                    }

                    //delete the entries associated with reservation
                    DB_Manager.deleteQosMapEntries(resID);

                    //delete the entries associated with reservation 
                    DB_Manager.deleteFlowMapEntries(resID);

                    //delete the entry reservation 
                    DB_Manager.deleteReservation(resID);

                    break;
                case Within:
                    System.out.println("------------------------------------------------------------------------------");
                    System.out.println("WITHIN");
                    // if flag "applied" is true break
                    if (r.getApplied()) {
                        System.out.println("RH: Policy already applied!");
                        break;
                    }

                    // Apply Dijkstra to find paths between hosts 
                    DijkstraOps.findPath(r.getSrcIP(), r.getDstIP());

                    //edit reservation and set flag "applied" true
                    r.setApplied(true);
                    DB_Manager.editReservation(r);
                    //System.exit(0);

                    upPathDefinition(r);

                    downPathDefinition(r, pw);

                    System.out.println("Reservation processed successfully!");
                    break;
                default:
                    System.out.println("Error processing dates!");
                    break;
            }
        }

    }

    private static int checkDate(Date now, Date start, Date after) {
        if (now.before(start)) {
            return Prev;
        } else if (now.after(after)) {
            return After;
        } else {
            return Within;
        }
    }

    private static void upPathDefinition(Reservation r) {
        /* Getting Up Path */
        PP = DijkstraOps.getParsedPath();
        System.out.println("Up Path");
        //create queues and flows to UP path
        for (ParsedPath p : PP) {
            int prio = r.getPriority();
            System.out.println("At Switch " + p.getSwID() + " port Number " + p.getPortNumber());
            //FUCKIN TRAIN
            Port trainPort = Utils.topo.getNode(p.getSwID()).getPortByNumber(Integer.parseInt(p.getPortNumber()));

            /* Queue */
            //get qosUUID
            System.out.println(trainPort);
            String qosUUID = trainPort.getQosUUID();
            System.out.println("Trainport qosUUID= " + qosUUID);

            //define qos config
            qc.setOvsid(Constants.ovsID);
            //create queue config
            qc.setQosConfig(qosUUID, r.getPriority(), r.getMinBW(), r.getMaxBW());

            //send queue config to ovsdb
            if (MyJson.sendPost(true, Constants.queue, qc)) {
                System.out.println("RH: Post send successfully!");
            } else {
                System.out.println("RH: Post failed!");
            }

            //Save queue in port data structure
            q = new Queue(trainPort.getNextNumberCounter(), Utils.queueUUID, qc.getMinRateQ(), qc.getMaxRateQ(), qc.getPriorityQ());
            trainPort.addQueue(q);

            //Save Queue in db
            qm = new QosMap(p.getSwID(), trainPort.getPortID(), trainPort.getPortUUID(), qosUUID, q.getUuid());
            qm.setResID(r); //set reservation as FK
            DB_Manager.addQos(qm);

            /* Flows */
            // create flow config
            fc.setFlowConfig(p.getSwID(), 0, flowCounter, r.getPriority(), r.getSrcIP(), r.getDstIP(),
                    p.getPortNumber(), Integer.toString(trainPort.getNumberCounter()));

            // send flow to controller and switch
            MyXML.sendPut(true, false, fc);

            //save Flow in db 
            fm = new FlowMap(fc.getNodeID(), trainPort.getPortID(), 0, flowCounter);
            flowCounter++;
            fm.setResID(r); //set reservation as FK
            DB_Manager.addFlow(fm);

        }
    }

    private static void downPathDefinition(Reservation r, PrintWriter pw) {
        System.out.println("Down Path");
        /* Getting Down Path */
        revPP = DijkstraOps.getRevParsedPath();

        //Clearing config data structure just in case
        fc.clearFlowConfig();
        qc.clear();

        //create queues and flow to DOWN path
        for (ParsedPath p : revPP) {
            int prio = r.getPriority();

            //FUCKIN TRAIN
            Port trainPort = Utils.topo.getNode(p.getSwID()).getPortByNumber(Integer.parseInt(p.getPortNumber()));

            /* Queue */
            //get qosUUID
            System.out.println(trainPort);
            String qosUUID = trainPort.getQosUUID();
            //define qos config
            qc.setOvsid(Constants.ovsID);
            //create queue config
            qc.setQosConfig(qosUUID, r.getPriority(), r.getMinBW(), r.getMaxBW());
            
            //send queue config to ovsdb
            if (MyJson.sendPost(true, Constants.queue, qc)) {
                System.out.println("RH: Post send successfully!");
            } else {
                System.out.println("RH: Post failed!");
            }

            //Save queue in port data structure (including the ID to be mapped in the flow)
            q = new Queue(trainPort.getNextNumberCounter(), Utils.queueUUID, qc.getMinRateQ(), qc.getMaxRateQ(), qc.getPriorityQ());
            trainPort.addQueue(q);

            //save in text file
            pw.println(Utils.queueUUID);
            pw.flush();

            //Save Queue in db (add the queue-id mapped on flow to the db)
            qm = new QosMap(p.getSwID(), trainPort.getPortID(), trainPort.getPortUUID(), qosUUID, q.getUuid());
            qm.setResID(r); //set reservation as FK
            DB_Manager.addQos(qm);

            /* Flows */
            // create flow config
            fc.setFlowConfig(p.getSwID(), 0, flowCounter, r.getPriority(), r.getDstIP(), r.getSrcIP(),
                    p.getPortNumber(), Integer.toString(trainPort.getNumberCounter()));

            // send flow to controller and switch
            MyXML.sendPut(true, false, fc);

            //save Flow in db 
            fm = new FlowMap(fc.getNodeID(), trainPort.getPortID(), 0, flowCounter);

            fm.setResID(r); //set reservation as FK
            flowCounter++;
            DB_Manager.addFlow(fm);

        }
    }

}
