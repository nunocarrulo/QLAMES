/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CIMI_Main;

/**
 *
 * @author nuno
 */
public class GeneralStatistics {
    
    public static double absTime = 0.0;
    public static double absNow = 0.0;
    
    /* Cumulative values */
    /* Duration in msec */
    public static double queueDuration = 0.0;
    public static double flowDuration = 0.0;
    public static double databaseDuration = 0.0;
    public static double dijkstraDuration = 0.0;
    public static double lbDuration = 0.0;  //retrieving statistics + acting if needed
    public static double lbDurationAct = 0.0; 
    public static double lbDurationTotal = 0.0; 
    public static double odlFlowDuration = 0.0;
    public static double odlQosDuration = 0.0;
    public static double teardownFlowDuration = 0.0;
    public static double teardownQoSDuration = 0.0;
    public static double teardownOtherDuration = 0.0;
    public static double runtime = 0.0;
    
    /* Overhead in bytes */
    public static int totalSignOverhead = 0;
    public static int flowSignOverhead = 0;
    public static int queueSignOverhead = 0;
    
    /* Time variables */
    public static long startTime;
    public static long endTime;
    
    public static void printStats(){
        System.out.println("-----------------------------------GENERAL STATISTICS-----------------------------------");
        System.out.println("DURATION:");
        System.out.printf("\tQoS : \t%.1f msec\n\tFlow : \t%.1f msec \n\tDB : \t%.1f msec \n\tLoadBalance : \t%.1f msec\n\tDijkstra : \t%.1f msec\n\tODL Flow : \t%.1f msec\n\tODL QoS : \t%.1f msec\n\tTeardown Flow : %.1f msec\n\tTeardown QoS : %.1f msec\n\t Teardown Other : %.1f msec \n", 
                (GeneralStatistics.queueDuration-GeneralStatistics.odlQosDuration), GeneralStatistics.flowDuration, GeneralStatistics.databaseDuration, GeneralStatistics.lbDuration, GeneralStatistics.dijkstraDuration, 
                GeneralStatistics.odlFlowDuration, GeneralStatistics.odlQosDuration, GeneralStatistics.teardownFlowDuration, GeneralStatistics.teardownQoSDuration, 
                GeneralStatistics.teardownOtherDuration);
        System.out.printf("LoadBalancing Act : \t%.1f msec\nLoadBalancing Total : \t%.1f msec\n", GeneralStatistics.lbDurationAct, GeneralStatistics.lbDurationTotal);
        System.out.println("OVERHEAD");
        System.out.printf("\tQoS : \t%d bytes\n\tFlow : \t%d bytes \n\tTotal: \t%d bytes \n", GeneralStatistics.queueSignOverhead, 
                    GeneralStatistics.flowSignOverhead, GeneralStatistics.totalSignOverhead);
        
        System.out.printf("Done Once!\nRuntime: %.1f msec", runtime);
    }
}
