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
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        //checkInsertionRefresh();
        //testDBScal();
        //testDB();
        
        QosConfig qc = new QosConfig();
        List<Reservation> resList;
                
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
        StatisticsThread st;
        if(applyLoadBalance){
            st = new StatisticsThread();
            st.start();
        }
        System.out.println("QoS row time: "+GeneralStatistics.odlQosDuration);
        GeneralStatistics.queueDuration = GeneralStatistics.odlQosDuration = 0.0;
        boolean lol = true;
        /* Poll database entries to check new reservations */
        long time = System.nanoTime();
        GeneralStatistics.absTime = time;
        
        while (lol) {
            
            // Get all reservations
            long startTime = System.nanoTime();
            
            resList = DB_Manager.getReservations();
            
            long endTimew = System.nanoTime();
            GeneralStatistics.databaseDuration += (endTimew - startTime) / 1000000.0;
            
            // Verify what needs to be done
            ReservationHandler.process(resList, pw);
            /*
            try {
                Thread.sleep(5000);                 //1000 milliseconds is one second.
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            */
            //break;
        }
        
        long endTime = System.nanoTime();
        GeneralStatistics.runtime = (endTime - time) / 1000000.0; // in msec
        
        if(applyLoadBalance){
            st.stop();
        }
        
        GeneralStatistics.totalSignOverhead = GeneralStatistics.flowSignOverhead + GeneralStatistics.queueSignOverhead;
        
        System.out.println("-----------------------------------GENERAL STATISTICS-----------------------------------");
        System.out.println("DURATION:");
        System.out.printf("\tQoS : \t%.1f msec\n\tFlow : \t%.1f msec \n\tDB : \t%.1f msec \n\tLoadBalance : \t%.1f msec\n\tDijkstra : \t%.1f msec\n\tODL Flow : \t%.1f msec\n\tODL QoS : \t%.1f msec\n\tTeardown Flow : %.1f msec\n\tTeardown QoS : %.1f msec\n\t Teardown Other : %.1f msec \n", 
                (GeneralStatistics.queueDuration-GeneralStatistics.odlQosDuration), GeneralStatistics.flowDuration, GeneralStatistics.databaseDuration, GeneralStatistics.lbDuration, GeneralStatistics.dijkstraDuration, 
                GeneralStatistics.odlFlowDuration, GeneralStatistics.odlQosDuration, GeneralStatistics.teardownFlowDuration, GeneralStatistics.teardownQoSDuration, 
                GeneralStatistics.teardownOtherDuration);
        System.out.println("OVERHEAD");
        System.out.printf("\tQoS : \t%d bytes\n\tFlow : \t%d bytes \n\tTotal: \t%d bytes \n", GeneralStatistics.queueSignOverhead, 
                    GeneralStatistics.flowSignOverhead, GeneralStatistics.totalSignOverhead);
        
        System.out.printf("Done Once!\nRuntime: %.1f msec", GeneralStatistics.runtime);
        
        System.out.println("-----------------------------------------------------------");
        System.out.println("Changing dates to delete");
        /*Calendar calendar = new GregorianCalendar(2015, 2, 12, 19, 20, 0);
        Date end = calendar.getTime();
        for(Reservation a : resList){
            a.setEndDate(end);
            
        }
        for(Reservation a : resList){
            DB_Manager.editReservation(a);
        }*/
        System.out.println("Press any key after change endDate");
        try {
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        /* Prepare database to write and read */
        DB_Manager.closeDB();
        DB_Manager.prepareDB();
        
        //reset times
        GeneralStatistics.databaseDuration = GeneralStatistics.queueDuration = GeneralStatistics.odlQosDuration = 0.0;
        GeneralStatistics.lbDuration = GeneralStatistics.flowDuration = GeneralStatistics.odlFlowDuration = 0.0;
        GeneralStatistics.queueSignOverhead = GeneralStatistics.flowSignOverhead = 0;
        
        time = 0;
        time = System.nanoTime();
        System.out.println("Starting teardown...");
        while (true) {
            
            // Get all reservations
            long startTime2 = System.nanoTime();
            
            resList = DB_Manager.getReservations();
            
            long endTime2 = System.nanoTime();
            GeneralStatistics.databaseDuration += (endTime2 - startTime2) / 1000000.0;
            
            // Verify what needs to be done
            ReservationHandler.process(resList, pw);

            break;
        }
        endTime = System.nanoTime();
        GeneralStatistics.runtime = (endTime - time) / 1000000.0; // in msec
        
        GeneralStatistics.totalSignOverhead = GeneralStatistics.flowSignOverhead + GeneralStatistics.queueSignOverhead;
        
        System.out.println("-----------------------------------GENERAL STATISTICS-----------------------------------");
        System.out.println("DURATION:");
        System.out.printf("\tQoS : \t%.1f msec\n\tFlow : \t%.1f msec \n\tDB : \t%.1f msec \n\tLoadBalance : \t%.1f msec\n\tDijkstra : \t%.1f msec\n\tODL Flow : \t%.1f msec\n\tODL QoS : \t%.1f msec\n\tTeardown Flow : %.1f msec\n\tTeardown QoS : %.1f msec\n\t Teardown Other : %.1f msec \n", 
                (GeneralStatistics.queueDuration-GeneralStatistics.odlQosDuration), GeneralStatistics.flowDuration, GeneralStatistics.databaseDuration, GeneralStatistics.lbDuration, GeneralStatistics.dijkstraDuration, 
                GeneralStatistics.odlFlowDuration, GeneralStatistics.odlQosDuration, GeneralStatistics.teardownFlowDuration, GeneralStatistics.teardownQoSDuration, 
                GeneralStatistics.teardownOtherDuration);
        System.out.println("OVERHEAD");
        System.out.printf("\tQoS : \t%d bytes\n\tFlow : \t%d bytes \n\tTotal: \t%d bytes \n", GeneralStatistics.queueSignOverhead, 
                    GeneralStatistics.flowSignOverhead, GeneralStatistics.totalSignOverhead);
        
        System.out.printf("Done Once Teardown!\nRuntime: %.1f msec", GeneralStatistics.runtime);
        
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

    
    public static void checkInsertionRefresh(){
        System.out.println("Testing DB Refresh...");
        List<Reservation> resList;
        DB_Manager.prepareDB();
        int cont = 0;
        while(true){
            resList = DB_Manager.getReservations();
            System.out.println("Printing existing Reservations ...");
            System.out.println("Size: "+resList.size()+" cont = "+cont);
            for (Reservation res : resList){ 
                //System.out.println("ID = " + res.getId() + " sourceIP = " + res.getSrcIP() + " dstIP = " + res.getDstIP() +" startDate = "+res.getStartDate());
                System.out.println("ID = " + res.getId() + " applied = "+res.getApplied());
            }
            if(cont > 5000){
                System.exit(0);
            }
            if(cont == 5000){
                System.out.println("Editing Reservation");
                Reservation r = resList.get(1);
                r.setApplied(true);
                DB_Manager.editReservation(r);
            }
            cont++;
        }
        
        
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
        //String srcIPs[] = {"10.0.0.1", "10.0.0.2", "10.0.0.3", "10.0.0.4", "10.0.0.5"};
        String srcIPs[][] = {{"10.0.0.1", "10.0.0.2", "10.0.0.3"}, 
                            {"10.0.0.11", "10.0.0.12", "10.0.0.13"}, 
                            {"10.0.0.21", "10.0.0.22", "10.0.0.23"},
                            {"10.0.0.31", "10.0.0.32", "10.0.0.33"},
                            {"10.0.0.41", "10.0.0.42", "10.0.0.43"}};
        //String dstIPs[] = {"10.0.0.3"};
        //srcIP = "10.0.0.1";
        //dstIP = "10.0.0.2";
        priority = 5;
        minBW = 40000;
        maxBW = 50000;
        Random rnd = new Random();

        for (int a = 0; a < 1; a++) {
            for (int i = 0; i < srcIPs.length; i++) {
                for (int j = 0; j < srcIPs[0].length; j++) {

                    switch (rnd.nextInt(5)) {
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
                    calendar = new GregorianCalendar(2015, 3, 10, 11, 0, 0);
                    System.out.println(sdf.format(calendar.getTime()));
                    start = calendar.getTime();
                    calendar = new GregorianCalendar(2015, 3, 27, 23, 00, 0);
                    end = calendar.getTime();

                    for(int x = i+1; x < srcIPs.length; x++)
                        for(int y = 0; y < srcIPs[0].length; y++){
                            int startDay = rnd.nextInt(25)+1;
                            calendar = new GregorianCalendar(2015, 3, startDay, 23, 00, 0);
                            start = calendar.getTime();
                            r = new Reservation(srcIPs[i][j], srcIPs[x][y], priority, minBW, maxBW, start, end);
                            DB_Manager.addReservation(r);
                            System.out.println("Reservation added!");
                        }
                }
            }
        }

        resList = DB_Manager.getReservations();
        System.out.println("Printing existing Reservations ...");
        System.out.println("Size: "+resList.size());
        for (Reservation res : resList){ 
            System.out.println("ID = " + res.getId() + " sourceIP = " + res.getSrcIP() + " dstIP = " + res.getDstIP() +" startDate = "+res.getStartDate());
        }
        
        /*for (int a = 0; a < 1; a++) {
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
        }*/

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
