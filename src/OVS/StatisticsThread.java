/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OVS;

import REST_Requests.Constants;
import REST_Requests.MyJson;
import TopologyManagerImpl.QosConfig;

/**
 *
 * @author nuno
 */
public class StatisticsThread extends Thread {

    private long time;
    private long lastTime;
    private QosConfig qc;

    public StatisticsThread() {
        time = lastTime = 0;
        qc = new QosConfig();
        qc.setOvsid(Constants.ovsID);
    }

    @Override
    public void run() {

        /*time = System.nanoTime();
         System.out.println(System.nanoTime());
         if ((time - lastTime) > 950000000) {    //0.95sec
         //get interface statistics
         MyJson.sendGet(Constants.iface, qc);
         }
         lastTime = time;*/
        try {
            Thread.sleep(10000);                 //1000 milliseconds is one second.
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        while (true) {
            try {
                Thread.sleep(950);                 //1000 milliseconds is one second.
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            //get interface statistics
            MyJson.sendGet(Constants.iface, qc);
        }

    }
}
