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
    
    /* Cumulative values */
    /* Duration in msec */
    public static double queueDuration = 0.0;
    public static double flowDuration = 0.0;
    public static double databaseDuration = 0.0;
    public static double dijkstraDuration = 0.0;
    public static double lbDuration = 0.0;  //retrieving statistics + acting if needed
    public static double odlDuration = 0.0;
    
    /* Overhead in bytes */
    public static int totalSignOverhead = 0;
    public static int flowSignOverhead = 0;
    public static int queueSignOverhead = 0;
    
    /* Time variables */
    public static long startTime;
    public static long endTime;
}
