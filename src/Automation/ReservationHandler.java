/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Automation;

import CIMI_Main.GeneralStatistics;
import DB.DB_Manager;
import DB.FlowMap;
import DB.QosMap;
import DB.Reservation;
import Dijkstra.DijkstraOps;
import Dijkstra.ParsedPath;
import Dijkstra.ReservPath;
import OVS.Queue;
import REST_Requests.Constants;
import static REST_Requests.Constants.applyLoadBalance;
import static REST_Requests.Constants.lol;
import REST_Requests.MyJson;
import REST_Requests.MyXML;
import TopologyManagerImpl.FlowConfig;
import TopologyManagerImpl.Port;
import TopologyManagerImpl.QosConfig;
import TopologyManagerImpl.TopoNode;
import TopologyManagerUtils.Utils;
import static TopologyManagerUtils.Utils.LINK_OVL;
import static TopologyManagerUtils.Utils.ovledLinks;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
    private static ReservPath rp;
    private static Queue q;
    private static QosMap qm;
    private static FlowMap fm;
    private static final QosConfig qc = new QosConfig();
    private static final FlowConfig fc = new FlowConfig();
    private static String srcHostId, dstHostId;
    private static boolean replacePath = false;
    private static ReservPath resPath = new ReservPath();
    /* Time variables */
    private static long startTime;
    private static long endTime;

    public static void process(List<Reservation> resList, PrintWriter pw) throws FileNotFoundException {
        //PrintWriter pw = new PrintWriter("queueUUID.txt");
        /* Retrieving actual time */
        now = new Date();
        //Clearing config data structure just in case
        fc.clearFlowConfig();
        qc.clear();

        for (Reservation r : resList) {
            System.out.println("Reservation " + r.getId());
            int resID = r.getId();
            boolean applied = r.getApplied();
            if (applyLoadBalance) {
                startTime = System.nanoTime();
                loadBalance(pw);
            }


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
                    if (!applied) {
                        break;
                    }
                    /* Delete all info (db and network) associated with the reservation */
                    deleteResInfo(resID);

                    break;
                case Within:
                    System.out.println("------------------------------------------------------------------------------");
                    System.out.println("WITHIN");

                    /* if reservation already "applied" go to next one */
                    if (applied) {
                        System.out.println("RH: Policy already applied!");
                        break;
                    }

                    /* Apply Dijkstra to find paths between hosts  */
                    DijkstraOps.getExistingPath(r.getSrcIP(), r.getDstIP());
                    //DijkstraOps.findPath(r.getSrcIP(), r.getDstIP());

                    /* Indicate that reservation was already processed */
                    r.setApplied(true);
                    DB_Manager.editReservation(r);

                    upPathDefinition(r, pw);

                    downPathDefinition(r, pw);

                    System.out.println("Reservation processed successfully!");

                    //Insert the paths associated with a resID into a internal data structure
                    startTime = System.nanoTime();
                    
                    rp = new ReservPath(r.getId());
                    rp.setPaths(PP, revPP);
                    rp.setIPs(r.getSrcIP(), r.getDstIP());
                    Utils.ReservPathList.add(rp);
                    
                    endTime = System.nanoTime();
                    GeneralStatistics.lbDuration += (endTime - startTime) / 1000000.0;
                    
                    if (true) {
                        System.out.println("---------------------Printing Reserved Paths:-----------------------");
                        for (ReservPath x : Utils.ReservPathList) {
                            x.toString();
                        }
                        System.out.println("--------------------------------------------------------------------");
                    }
                    checkPortQueues();

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

    private static void upPathDefinition(Reservation r, PrintWriter pw) {
        /* Getting Up Path */
        PP = DijkstraOps.getParsedPath();
        System.out.println("Up Path");
        //create queues and flows to UP path
        for (ParsedPath p : PP) {
            int prio = r.getPriority();
            System.out.println("At Switch " + p.getSwID() + " port Number " + p.getPortNumber());
            
            startTime = System.nanoTime();
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
                System.out.println("RH: Post sent successfully!");
            } else {
                System.out.println("RH: Post failed!");
            }

            //Save queue in port data structure
            q = new Queue(trainPort.getNextNumberCounter(), Utils.queueUUID, qc.getMinRateQ(), qc.getMaxRateQ(), qc.getPriorityQ());
            trainPort.addQueue(q);

            //send Request to map Queues in Qos rows of each port & switch
            if (MyJson.sendPut(Constants.qos, qc, trainPort)) {
                System.out.println("RH: Put sent successfully!");
            } else {
                System.out.println("RH: Put failed!");
            }
            endTime = System.nanoTime();
            GeneralStatistics.queueDuration += (endTime - startTime) / 1000000.0;
            
            //save in text file
            pw.println(Utils.queueUUID);
            pw.flush();

            startTime = System.nanoTime();
            
            //Save Queue in db
            qm = new QosMap(p.getSwID(), trainPort.getPortID(), trainPort.getPortUUID(), qosUUID, q.getUuid());
            qm.setResID(r); //set reservation as FK
            DB_Manager.addQos(qm);

            endTime = System.nanoTime();
            GeneralStatistics.databaseDuration += (endTime - startTime) / 1000000.0;
            
            /* Flows */
            startTime = System.nanoTime();
            // create flow config
            fc.setFlowConfig(p.getSwID(), 0, Utils.topo.getNode(p.getSwID()).getAndIncFlowID(), (r.getPriority() + 10), r.getSrcIP(), r.getDstIP(),
                    p.getPortNumber(), Integer.toString(trainPort.getNumberCounter()));
            
            // send flow to controller and switch
            MyXML.sendPut(true, false, fc);

            endTime = System.nanoTime();
            GeneralStatistics.flowDuration += (endTime - startTime) / 1000000.0;
            
            startTime = System.nanoTime();
            
            //save Flow in db 
            fm = new FlowMap(fc.getNodeID(), trainPort.getPortID(), 0, fc.getFlowID());

            fm.setResID(r); //set reservation as FK
            DB_Manager.addFlow(fm);
            
            endTime = System.nanoTime();
            GeneralStatistics.databaseDuration += (endTime - startTime) / 1000000.0;

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

            
            startTime = System.nanoTime();
            
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

            //send Request to map Queues in Qos rows of each port & switch
            if (MyJson.sendPut(Constants.qos, qc, trainPort)) {
                System.out.println("RH: Put sent successfully!");
            } else {
                System.out.println("RH: Put failed!");
            }

            endTime = System.nanoTime();
            GeneralStatistics.queueDuration += (endTime - startTime) / 1000000.0;
            
            //save in text file
            pw.println(Utils.queueUUID);
            pw.flush();

            startTime = System.nanoTime();
            
            //Save Queue in db (add the queue-id mapped on flow to the db)
            qm = new QosMap(p.getSwID(), trainPort.getPortID(), trainPort.getPortUUID(), qosUUID, q.getUuid());
            qm.setResID(r); //set reservation as FK
            DB_Manager.addQos(qm);
            
            endTime = System.nanoTime();
            GeneralStatistics.databaseDuration += (endTime - startTime) / 1000000.0;
            
            /* Flows */
            startTime = System.nanoTime();
            // create flow config
            fc.setFlowConfig(p.getSwID(), 0, Utils.topo.getNode(p.getSwID()).getAndIncFlowID(), r.getPriority(), r.getDstIP(), r.getSrcIP(),
                    p.getPortNumber(), Integer.toString(trainPort.getNumberCounter()));
            
            // send flow to controller and switch
            MyXML.sendPut(true, false, fc);

            endTime = System.nanoTime();
            GeneralStatistics.flowDuration += (endTime - startTime) / 1000000.0;
            
            startTime = System.nanoTime();
            
            //save Flow in db 
            fm = new FlowMap(fc.getNodeID(), trainPort.getPortID(), 0, fc.getFlowID());

            fm.setResID(r); //set reservation as FK

            DB_Manager.addFlow(fm);
            
            endTime = System.nanoTime();
            GeneralStatistics.databaseDuration += (endTime - startTime) / 1000000.0;

        }
    }

    public static void checkPortQueues() {
        for (TopoNode n : Utils.topo.getAllSwitches()) {
            for (Port p : n.getAllPorts()) {
                System.out.println("Node: " + n.getId() + " Port: " + p.getPortID() + "\t" + p.getQueues().toString());
            }
        }
    }

    private static void deleteResInfo(int resID) {

        //find all entries on table QosMap associated with this reservationID
        qosList = DB_Manager.getQosMap(resID);

        //define qos config
        qc.setOvsid(Constants.ovsID);

        //delete all queues on the switches
        for (QosMap q : qosList) {
            qc.setQueueuuid(q.getQueueUUID());
            qc.setQosuuid(q.getQosUUID());
            //deleting queue from port
            Port trainPort = Utils.topo.getNode(q.getSwID()).getPort(q.getPortID());
            trainPort.delQueue(qc.getQueueuuid());
            //updating Qos row
            //send Request to map Queues in Qos rows of each port & switch
            if (MyJson.sendPut(Constants.qos, qc, trainPort)) {
                System.out.println("RH: Put sent successfully!");
            } else {
                System.out.println("RH: Put failed!");
            }
            //safely delete Queue
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

        // delete paths from internal data structure associated with the resID
        Iterator<ReservPath> iter = Utils.ReservPathList.iterator();
        while (iter.hasNext()) {
            ReservPath rp = iter.next();
            if (rp.getResID() == resID) {
                iter.remove();
            }
        }

    }

    private static void loadBalance(PrintWriter pw) {
        boolean up = false;
        synchronized (lol) {
            long startTime = System.nanoTime();
            /* Apply Load Balancing if link is overloaded */
            if (LINK_OVL) {
                System.out.println("----------------------------LOAD BALANCE----------------------------------");
                /* Check Path */
                System.out.println("Overloaded link: "+ovledLinks.get(0));
                System.out.println("ReservPaths:");
                reservLoop:
                for (ReservPath a : Utils.ReservPathList) {
                    System.out.println("\t"+a.toString());
                    pathloop:
                    for (ParsedPath pp : a.getPath()) {
                        String concatPortID = pp.getSwID()+":"+pp.getPortNumber();
                        System.out.println("Port Number: "+concatPortID);
                        //if a reservation contains a path using the ovlded link find another path
                        if (concatPortID.equals(ovledLinks.get(0))) {
                            System.out.println("Reservation " + a.getResID() + " has the link overloaded on up path");
                            // Change that port weight to high metric
                            DijkstraOps.updateEdge(pp.getSwID(), concatPortID, 10);
                            // Recalculate the path between hosts
                            System.out.println("Changing edge value. Finding another path");
                            DijkstraOps.findPath(a.getSrcIP(), a.getDstIP());
                            PP = DijkstraOps.getParsedPath();
                            // Verify if the path is valid
                            if (!PP.isEmpty()) {
                                for (ParsedPath lol : PP) {
                                    //verify if the overloaded port is in new path
                                    concatPortID = lol.getSwID()+":"+lol.getPortNumber();
                                    if (concatPortID.equals(ovledLinks.get(0))) {
                                        System.out.println("NOT VALID! Breaking pathloop");
                                        break pathloop;
                                    }
                                }
                                // Replace metric to normal SPF
                                DijkstraOps.updateEdge(pp.getSwID(), pp.getPortNumber(), 1);
                                replacePath = true;
                                resPath = a;
                                revPP = DijkstraOps.getRevParsedPath();
                                System.out.println("Found a new path " + resPath.toString());
                                break reservLoop;
                            } else {
                                break;  //go to next reservation, this has no redundancy
                            }
                        }
                    }

                    /* Check Reverse Path */
                    revpathloop:
                    for (ParsedPath pp : a.getRevPath()) {
                        //if a reservation contains a path using the ovldedlink find another path
                        if (pp.getPortNumber().equals(ovledLinks.get(0))) {
                            System.out.println("Reservation " + a.getResID() + " has the link overloaded on down path");
                            // Change that port weight to high metric
                            DijkstraOps.updateEdge(pp.getSwID(), pp.getPortNumber(), Integer.MAX_VALUE);
                            // Recalculate the path between hosts
                            DijkstraOps.findPath(a.getSrcIP(), a.getDstIP());
                            PP = DijkstraOps.getParsedPath();
                            // Verify if the path is valid
                            if (!PP.isEmpty()) {
                                for (ParsedPath lol : PP) {
                                    //verify if the overloaded port is in new path
                                    if (lol.getPortNumber().equals(ovledLinks.get(0))) {
                                        break revpathloop;
                                    }
                                }
                                //replace metric
                                DijkstraOps.updateEdge(pp.getSwID(), pp.getPortNumber(), 1);
                                replacePath = true;
                                resPath = a;
                                System.out.println("Found a new path " + resPath.toString());
                                break reservLoop;
                            } else {
                                break;  //go to next reservation, this has no redundancy
                            }
                        }
                    }

                }
                //remove processed overloaded link
                System.out.println("Removing ovled link");
                ovledLinks.remove(0);
                if (Utils.ovledLinks.isEmpty()) {
                    LINK_OVL = false;
                }
            }
            long endTime = System.nanoTime();
            GeneralStatistics.lbDuration += (endTime - startTime) / 1000000.0;
        }
        if (replacePath) {
            System.out.println("++++++++++++++++++++++++++++++++++++++++Replacing path...+++++++++++++++++++++++++++++++++++++");
            
            startTime = System.nanoTime();
            /* Get old reservation */
            Reservation oldReserv = DB_Manager.getReservation(resPath.getResID());
            int prio = oldReserv.getPriority() + 1;

            /* Create new reservation equal but with new path and higher prio so there's no packet loss */
            Reservation newReserv = new Reservation(oldReserv.getSrcIP(), oldReserv.getDstIP(), prio,
                    oldReserv.getMinBW(), oldReserv.getMaxBW(), oldReserv.getStartDate(), oldReserv.getEndDate());
            newReserv.setApplied(true);
            DB_Manager.addReservation(newReserv);
            
            endTime = System.nanoTime();
            GeneralStatistics.databaseDuration += (endTime - startTime) / 1000000.0;
            System.out.println("New Reservation created");

            /* Path definition Queues and Flows */
            upPathDefinition(newReserv, pw);
            downPathDefinition(newReserv, pw);
            System.out.println("New path successfully created");

            /* Remove Reservation and all associated queues and flows */
            int oldResID = oldReserv.getId();
            deleteResInfo(oldResID);
            System.out.println("Old path successfully deleted");
            replacePath = false;
        }
    }

}
