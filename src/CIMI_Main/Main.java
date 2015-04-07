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
import OVS.StatisticsThread;
import REST_Requests.Constants;
import static REST_Requests.Constants.applyLoadBalance;
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

        //testDBOrder();
        //testDBScal();
        //testDB();
        
        /* Variables and Data structures initialization */
        /*FlowConfig fc = new FlowConfig();
        
        Reservation r;
        */
        /* Reservation parameters */
        /*String srcIP, dstIP;
        int priority;
        int minBW, maxBW;
        Calendar calendar;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        Date start, end;
        */
        
        QosConfig qc = new QosConfig();
        List<Reservation> resList;
                
        /* Credentials set to Rest requests */
        MyXML.setCredentials("admin", "admin");     // xml
        MyJson.setCredentials("admin", "admin");    // json
        System.out.println("Controller credentials set.");

        /* Getting Topology */
        System.out.println("Obtaining network topology...");
        MyXML.sendGet(Constants.topo, null); // requesting topology to controller
        System.exit(0);
        
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
        
        //start statistics collector thread
        if(applyLoadBalance){
            StatisticsThread st = new StatisticsThread();
            st.start();
        }
        /* Poll database entries to check new reservations */
        //long time = System.nanoTime();
        while (true) {
            // Get all reservations
            resList = DB_Manager.getReservations();
            // Verify what needs to be done
            ReservationHandler.process(resList, pw);
            
            try {
                Thread.sleep(5000);                 //1000 milliseconds is one second.
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            
            //break;
        }
        //long timeEnd = System.nanoTime();
        //double runTime = (timeEnd - time) / 1000000000.0;
        //System.out.println("Done Once!\nRuntime: "+runTime+" sec");

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
        dstIP = "10.0.0.2";
        priority = 5;
        minBW = 40000;
        maxBW = 50000;
        calendar = new GregorianCalendar(2015, 2, 24, 11, 0, 0);
        System.out.println(sdf.format(calendar.getTime()));
        start = calendar.getTime();
        calendar = new GregorianCalendar(2015, 2, 24, 19, 20, 0);
        end = calendar.getTime();

        //DB_Manager.deleteAllReservations();
        // Add reservation to db 
        r = new Reservation(srcIP, dstIP, priority, minBW, maxBW, start, end);
        DB_Manager.addReservation(r);
        System.out.println("Reservation added!");
        //DB_Manager.getReservations();

        System.exit(0);
    }

    public static void testDBScal() {
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
        String srcIPs[] = {"10.0.0.1", "10.0.0.2", "10.0.0.3", "10.0.0.4", "10.0.0.5"};
        //String srcIPs[] = {"10.0.0.1"};
        //String dstIPs[] = {"10.0.0.3"};
        //srcIP = "10.0.0.1";
        //dstIP = "10.0.0.2";
        priority = 5;
        minBW = 40000;
        maxBW = 50000;

        for (int a = 0; a < 1; a++) {
            for (int i = 0; i < srcIPs.length; i++) {
                for (int j = i; j < srcIPs.length; j++) {
                    if (i == j) {
                        continue;
                    }
                    switch (i) {
                        case 0:
                            minBW = 10000;
                            maxBW = 20000;
                            break;
                        case 1:
                            minBW = 20000;
                            maxBW = 30000;
                            break;
                        case 2:
                            minBW = 30000;
                            maxBW = 40000;
                            break;
                        case 3:
                            minBW = 40000;
                            maxBW = 50000;
                            break;
                        case 4:
                            minBW = 50000;
                            maxBW = 60000;
                            break;
                        default:
                            System.out.println("DEU MERDA");
                            break;
                    }
                    calendar = new GregorianCalendar(2015, 2, 24, 11, 0, 0);
                    System.out.println(sdf.format(calendar.getTime()));
                    start = calendar.getTime();
                    calendar = new GregorianCalendar(2015, 3, 1, 19, 00, 0);
                    end = calendar.getTime();
                    r = new Reservation(srcIPs[i], srcIPs[j], priority, minBW, maxBW, start, end);
                    DB_Manager.addReservation(r);
                    System.out.println("Reservation added!");
                }
            }
        }

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
