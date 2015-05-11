/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ResultsTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author nuno
 */
public class RTTDelay {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
    
        String fileName = "RTTH1H7";
        File f = new File("/home/nuno/Quintus Anno/Thesis/Controller/Results/QoSTest/" + fileName);
        
        String line;
        BufferedReader br = new BufferedReader(new FileReader(f));
        int counter = 0;
        double sum = 0.0;
        
        while ((line = br.readLine()) != null) {
            if (line.contains("time=")) {
                String aux[] = line.split("time=");
                String time = aux[1].split("ms")[0];
                sum +=Double.parseDouble(time);
                counter++;
            }
        }
        
        System.out.println("RTT= "+(sum/counter));
        
    }
    
}
