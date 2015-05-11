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
import java.io.UnsupportedEncodingException;

/**
 *
 * @author nuno
 */
public class ParsingOneIperf {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        String fileName = "logH1_WS_1.25MBChosen";
        File f = new File("/home/nuno/Quintus Anno/Thesis/Controller/Results/QoSTest/" + fileName);
        String pw = "/home/nuno/Quintus Anno/Thesis/Controller/Results/QoSTest/" + fileName + "BwParsed.txt";
        String pw2 = "/home/nuno/Quintus Anno/Thesis/Controller/Results/QoSTest/" + fileName + "TransferParsed.txt";
        PrintWriter writerBwA = new PrintWriter(pw, "UTF-8");
        PrintWriter writerTrA = new PrintWriter(pw2, "UTF-8");

        String[][] infoA = new String[10][60];
        String[][] infoAt = new String[10][60];

        int start = 0;
        String line;
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
                continue;
            }

            transfer = line.substring(stTrans, endTrans);
            bw = line.substring(stBw, endBw);
            infoA[counter][start] = bw;
            infoAt[counter][start] = transfer;
            start += 1;
            System.out.println("Measure Number= " + (counter + 1) + " Bw: " + bw + " Transfer: " + transfer);
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

        writerBwA.close();
        writerTrA.close();
        System.out.println("Done");
    }

}
