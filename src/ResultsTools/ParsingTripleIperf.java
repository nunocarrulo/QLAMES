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
public class ParsingTripleIperf {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        File f = new File("/home/nuno/Quintus Anno/Thesis/Controller/Results/LBTest/mayRepRateChosen");
        //File output = new File("/home/nuno/Quintus Anno/Thesis/Controller/Results/parsedIperf.txt");
        //File output2 = new File("/home/nuno/Quintus Anno/Thesis/Controller/Results/parsedIperf2.txt");
        PrintWriter writerBwA = new PrintWriter("/home/nuno/Quintus Anno/Thesis/Controller/Results/LBTest/RateparsedIperfAbw.txt", "UTF-8");
        PrintWriter writerTrA = new PrintWriter("/home/nuno/Quintus Anno/Thesis/Controller/Results/LBTest/RateparsedIperfAtransfer.txt", "UTF-8");
        PrintWriter writerBwB = new PrintWriter("/home/nuno/Quintus Anno/Thesis/Controller/Results/LBTest/RateparsedIperfBbw.txt", "UTF-8");
        PrintWriter writerTrB = new PrintWriter("/home/nuno/Quintus Anno/Thesis/Controller/Results/LBTest/RateparsedIperfBtransfer.txt", "UTF-8");
        PrintWriter writerBwC = new PrintWriter("/home/nuno/Quintus Anno/Thesis/Controller/Results/LBTest/RateparsedIperfCbw.txt", "UTF-8");
        PrintWriter writerTrC = new PrintWriter("/home/nuno/Quintus Anno/Thesis/Controller/Results/LBTest/RateparsedIperfCtransfer.txt", "UTF-8");
        
        String[][] infoA = new String[5][30];
        String[][] infoB = new String[5][30];
        String[][] infoC = new String[5][30];
        String[][] infoAt = new String[5][30];
        String[][] infoBt = new String[5][30];
        String[][] infoCt = new String[5][30];
        
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
        int a = 1;
        BufferedReader br = new BufferedReader(new FileReader(f));

        while ((line = br.readLine()) != null) {
           
            if (line.contains("local")) {
             a = 1;
             counter += 1;
             start = 0;

             continue;
             }

            transfer = line.substring(stTrans, endTrans);
            bw = line.substring(stBw, endBw);
            
            if (a == 1) {
                infoA[counter][start] = bw;    
                infoAt[counter][start] = transfer;
                a++;
            } else if(a==2) {
                infoB[counter][start] = bw;
                infoBt[counter][start] = transfer;
                a++;
                
            }else{
                infoC[counter][start] = bw;
                infoCt[counter][start] = transfer;
                start += 1;
                a = 1;
            }
            System.out.println("Measure Number= " + (counter + 1) + " Bw: " + bw + " Transfer: " + transfer);

        }

        System.out.println("-------------------------------------------------------------------------");
        String cat = new String();

        //write to files
        for (int i = 0; i < infoA[0].length; i++) {
            for (int j = 0; j < 5; j++) {
                //System.out.println("i= "+i+" j= "+j+" cat= "+cat);
                cat += (infoA[j][i] + " ");
            }
            System.out.println("i= " + i + " " + cat);
            writerBwA.println(cat);
            cat = "";
        }
        for (int i = 0; i < infoAt[0].length; i++) {
            for (int j = 0; j < 5; j++) {
                cat += (infoAt[j][i] + " ");
            }
            writerTrA.println(cat);
            cat = new String();
        }

        for (int i = 0; i < infoB[0].length; i++) {
            for (int j = 0; j < 5; j++) {
                cat += (infoB[j][i] + " ");
            }
            writerBwB.println(cat);
            cat = new String();
        }
        for (int i = 0; i < infoBt[0].length; i++) {
            for (int j = 0; j < 5; j++) {
                cat += (infoBt[j][i] + " ");
            }
            writerTrB.println(cat);
            cat = new String();
        }
        
        for (int i = 0; i < infoC[0].length; i++) {
            for (int j = 0; j < 5; j++) {
                cat += (infoC[j][i] + " ");
            }
            writerBwC.println(cat);
            cat = new String();
        }
        for (int i = 0; i < infoCt[0].length; i++) {
            for (int j = 0; j < 5; j++) {
                cat += (infoCt[j][i] + " ");
            }
            writerTrC.println(cat);
            cat = new String();
        }
        

        writerBwA.close();
        writerTrA.close();
        writerTrB.close();
        writerBwB.close();
        writerTrC.close();
        writerBwC.close();
        System.out.println("Done");
    }

}
