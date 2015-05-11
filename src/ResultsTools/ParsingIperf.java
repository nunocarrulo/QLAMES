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
import java.io.PrintWriter;

/**
 *
 * @author nuno
 */
public class ParsingIperf {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        File f = new File("/home/nuno/Quintus Anno/Thesis/Controller/Results/QoSTest/logBoth_WS_1.25MBChosen");
        //File output = new File("/home/nuno/Quintus Anno/Thesis/Controller/Results/parsedIperf.txt");
        //File output2 = new File("/home/nuno/Quintus Anno/Thesis/Controller/Results/parsedIperf2.txt");
        PrintWriter writerBwA = new PrintWriter("/home/nuno/Quintus Anno/Thesis/Controller/Results/QoSTest/parsedIperfAbw.txt", "UTF-8");
        PrintWriter writerTrA = new PrintWriter("/home/nuno/Quintus Anno/Thesis/Controller/Results/QoSTest/parsedIperfAtransfer.txt", "UTF-8");
        PrintWriter writerBwB = new PrintWriter("/home/nuno/Quintus Anno/Thesis/Controller/Results/QoSTest/parsedIperfBbw.txt", "UTF-8");
        PrintWriter writerTrB = new PrintWriter("/home/nuno/Quintus Anno/Thesis/Controller/Results/QoSTest/parsedIperfBtransfer.txt", "UTF-8");

        String[][] infoA = new String[10][60];
        String[][] infoB = new String[10][60];
        String[][] infoAt = new String[10][60];
        String[][] infoBt = new String[10][60];
        int start = 0;
        int end = 1;
        String line;
        String comp = start + ".0- " + end + ".0 sec";
        String comp2 = start + ".0-" + end + ".0 sec";
        String transfer = "";
        String bw = "";
        int stTrans = 20;
        int endTrans = 25;
        int stBw = 33;
        int endBw = 38;
        int counter = 0;
        int lol = 0;
        boolean a = true;
        BufferedReader br = new BufferedReader(new FileReader(f));

        while ((line = br.readLine()) != null) {
            if (line.contains("local")) {
             a = true;
             counter += 1;
             start = 0;
             //end = 1;
             //comp = start + ".0- " + end + ".0 sec";
             //comp2 = start + ".0-" + end + ".0 sec";
             continue;
             }
            //if (!line.contains(comp) && !line.contains(comp2)) {
            //    continue;
            //}
            transfer = line.substring(stTrans, endTrans);
            bw = line.substring(stBw, endBw);
            if (a) {
                infoA[counter][start] = bw;
                
                if((counter+1)==4 && start == 32){
                    System.out.println("HEY bw= "+bw+" transfer "+transfer);
                }
                    
                infoAt[counter][start] = transfer;
            } else {
                infoB[counter][start] = bw;
                infoBt[counter][start] = transfer;
                start += 1;
                //end += 1;
                //comp = start + ".0- " + end + ".0 sec";
                //comp2 = start + ".0-" + end + ".0 sec";

            }
            System.out.println("Measure Number= " + (counter + 1) + " Bw: " + bw + " Transfer: " + transfer);
            a = !a;

        }

        System.out.println("-------------------------------------------------------------------------");
        String cat = new String();

        //write to files
        for (int i = 0; i < infoA[0].length; i++) {
            for (int j = 0; j < 10; j++) {
                //System.out.println("i= "+i+" j= "+j+" cat= "+cat);
                cat += (infoA[j][i] + " ");
            }
            System.out.println("i= " + i + " " + cat);
            writerBwA.println(cat);
            cat = "";
        }
        for (int i = 0; i < infoAt[0].length; i++) {
            for (int j = 0; j < 10; j++) {
                cat += (infoAt[j][i] + " ");
            }
            writerTrA.println(cat);
            cat = new String();
        }

        for (int i = 0; i < infoB[0].length; i++) {
            for (int j = 0; j < 10; j++) {
                cat += (infoB[j][i] + " ");
            }
            writerBwB.println(cat);
            cat = new String();
        }
        for (int i = 0; i < infoBt[0].length; i++) {
            for (int j = 0; j < 10; j++) {
                cat += (infoBt[j][i] + " ");
            }
            writerTrB.println(cat);
            cat = new String();
        }

        writerBwA.close();
        writerTrA.close();
        writerTrB.close();
        writerBwB.close();
        System.out.println("Done");
    }

}
