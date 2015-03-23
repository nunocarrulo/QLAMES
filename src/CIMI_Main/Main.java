/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CIMI_Main;

import Automation.ReservationHandler;
import DB.DB_Manager;
import DB.Reservation;
import Dijkstra.DijkstraOps;
import REST_Requests.Constants;
import REST_Requests.MyJson;
import REST_Requests.MyXML;
import TopologyManagerImpl.FlowConfig;
import TopologyManagerImpl.Port;
import TopologyManagerImpl.QosConfig;
import TopologyManagerImpl.TopoNode;
import TopologyManagerUtils.Utils;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 *
 * @author nuno
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    static boolean debug = false;
    static boolean queue = false;

    public static void main(String[] args) throws FileNotFoundException {

        //testDB();
        /* Variables and Data structures initialization */
        FlowConfig fc = new FlowConfig();
        QosConfig qc = new QosConfig();
        List<Reservation> resList;
        Reservation r;

        /* Reservation parameters */
        String srcIP, dstIP;
        int priority;
        int minBW, maxBW;
        Calendar calendar;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        Date start, end;

        /* Credentials set to Rest requests */
        MyXML.setCredentials("admin", "admin");     // xml
        MyJson.setCredentials("admin", "admin");    // json
        System.out.println("Controller credentials set.");

        /* Getting Topology */
        System.out.println("Obtaining network topology...");
        MyXML.sendGet(Constants.topo, null); // requesting topology to controller

        /* Finding paths using Dijkstra algorithm */
        System.out.println("Running Dijkstra algorithm...\n\tConstructing graph...");
        DijkstraOps.createGraph();
        System.out.println("Graph ready. Ready to find paths.");

        //srcIP = "10.0.0.1"; dstIP = "10.0.0.5";
        //DijkstraOps.findPath(srcIP, dstIP);
        //System.exit(0);

        /* Getting Ovs Node Id info */
        MyJson.sendGet(Constants.node, null);
        qc.setOvsid(Constants.ovsID);
        System.out.println("OvsID: " + Constants.ovsID);

        /* Getting Ovs Port Uuid info */
        qc.setOvsid(Constants.ovsID);
        MyJson.sendGet(Constants.port, qc);

        /* Add qos rows in every port of every switch */
        addQoSUUID(qc);

        /* Prepare database to write and read */
        DB_Manager.prepareDB();

        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Processing Reservations Loop...");
        PrintWriter pw = new PrintWriter("queueUUID.txt");
        /* Poll database entries to check new reservations */
        while (true) {
            // Get all reservations
            resList = DB_Manager.getReservations();
            // Verify what needs to be done
            ReservationHandler.process(resList, pw);
            //break;
            try {
                Thread.sleep(10000);                 //1000 milliseconds is one second.
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

        }
        //System.out.println("Done Once!");

    }

    public static void addQoSUUID(QosConfig qc) throws FileNotFoundException {

        System.out.println("Adding qosUUID to all ports...");
        PrintWriter out = new PrintWriter("qosUUID.txt");
        for (TopoNode tn : Utils.topo.getAllSwitches()) {
            for (Port p : tn.getAllPorts()) {
                qc.setPortuuid(p.getPortUUID());
                MyJson.sendPost(queue, Constants.qos, qc);
                p.setQosUUID(Utils.qosUUID);
                System.out.println("Added qosUUID: " + p.getQosUUID() + " to port: " + p.getPortID());

                out.println(Utils.qosUUID);
                out.flush();

            }
        }
        out.close();

    }

    public static void testDB() {
        System.out.println("Testing DB stuff...");
        List<Reservation> resList;
        Reservation r;
        String srcIP, dstIP;
        int priority;
        int minBW, maxBW;
        Calendar calendar;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        Date start, end;
        DB_Manager.prepareDB();

        /* Initialize reservation parameters */
        srcIP = "10.0.0.1";
        dstIP = "10.0.0.5";
        priority = 5;
        minBW = 20000;
        maxBW = 30000;
        calendar = new GregorianCalendar(2015, 2, 23, 11, 0, 0);
        System.out.println(sdf.format(calendar.getTime()));
        start = calendar.getTime();
        calendar = new GregorianCalendar(2015, 2, 23, 15, 20, 0);
        end = calendar.getTime();

        //DB_Manager.deleteAllReservations();
        // Add reservation to db 
        r = new Reservation(srcIP, dstIP, priority, minBW, maxBW, start, end);
        DB_Manager.addReservation(r);
        System.out.println("Reservation added!");
        //DB_Manager.getReservations();

        // Get all reservations
        /*resList = DB_Manager.getReservations();
         for (Reservation r : resList) {
         r.setApplied(false);
         DB_Manager.editReservation(r);
         System.out.println("Reservation processed successfully!");
         }*/
        //int resID = 1;
        //delete the entries associated with reservation
        //DB_Manager.deleteQosMapEntries(resID);
        //delete the entries associated with reservation 
        //DB_Manager.deleteFlowMapEntries(resID);
        //delete the entry reservation 
        //DB_Manager.deleteReservation(resID);
        System.exit(0);
    }

    public static void addARPToAll() {
        /* Installing ARP flows */
        FlowConfig fc = new FlowConfig();
        System.out.println("Installing ARP flow in all switches!");

        for (TopoNode tn : Utils.topo.getAllNodes()) {
            if (tn.isIsHost()) {
                continue;
            }

            fc.setFlowConfig(tn.getId(), 0, 4, 4, "", "", Constants.OFLogicalPorts.ALL.name());
            MyXML.sendPut(false, true, fc);

        }
        //removeARPFromAll();*/
    }

    public static void removeARPFromAll() {
        FlowConfig fc = new FlowConfig();

        for (TopoNode tn : Utils.topo.getAllNodes()) {
            if (tn.isIsHost()) {
                continue;
            }
            fc.setFlowConfig(tn.getId(), 0, 4, 4, "", "", Constants.OFLogicalPorts.ALL.name());
            MyXML.sendDelete(Constants.flow, fc);
        }
    }

    public static void removeQosRowsFromAll(QosConfig qc) {

        for (TopoNode tn : Utils.topo.getAllSwitches()) {
            for (Port p : tn.getAllPorts()) {
                qc.setQosuuid(p.getQosUUID());
                System.out.println("Deleting " + qc.getQosuuid());
                MyJson.sendDelete(Constants.qos, qc);
                p.setQosUUID("");
                System.out.println("Removed qosUUID: " + qc.getQosuuid() + " from port: " + p.getPortID());
            }
            break;
        }

    }

    public static void checkPortUUID() {
        System.out.println("--------------------Printing Port ID and UUID------------------------");

        for (TopoNode tn : Utils.topo.getAllNodes()) {
            for (int i = 0; i < tn.getAllPorts().size(); i++) {
                System.out.println("portID= " + tn.getAllPorts().get(i).getPortID()
                        + "\tportUUID= " + tn.getAllPorts().get(i).getPortUUID());
            }
        }
    }
}
