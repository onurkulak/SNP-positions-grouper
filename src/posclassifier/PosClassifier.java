/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package posclassifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author onur
 */
public class PosClassifier {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    /*
    args[0] the list file of close positions, 
    in each line first and third columns are chr numbers
    2nd 4th tokens are the positions of the SNP's
    args[1] is resolution size
    args[2] is the map file of SNP's from the dbSNP 
    args[3] is the folder of BED files from the dbSNP 
    */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String folderName = "Positions";
        HashMap<String, String> positions = createPositionFiles(args[0], folderName);
        int resolution = Integer.parseInt(args[1]);
        String bedFolder = args[3];
        HashSet<String> SNPDict = createHapMapDict(args[2]);
        System.out.println("Created hapMap dictionary");
        
        for (int chrCount = 1; chrCount < 25; chrCount++) {
            String fileName;
            if(chrCount<23)
                fileName = bedFolder + "/bed_chr_" + chrCount + ".bed";
            else if (chrCount==23) fileName = bedFolder + "/bed_chr_" + "X" + ".bed";
            else fileName = bedFolder + "/bed_chr_" + "Y" + ".bed";
            Scanner scan = new Scanner(new File(fileName));
            scan.nextLine();

            while (scan.hasNextLine()) {
                String positionName = scan.next() + "_";
                positionName += ((scan.nextInt() + scan.nextInt()) / 2)
                        / resolution * resolution + resolution / 2;
                String rsName = scan.next();
                if (SNPDict.contains(rsName) && positions.containsKey(positionName)) 
                    positions.put(positionName, positions.get(positionName) + (rsName + "\n"));
                scan.nextLine();
            }
            scan.close();
            System.out.println("finished dealing with chr: " + chrCount);
        }
        String[] temp = {};
        String[] keyset = positions.keySet().toArray(temp);
        String[] valueCollection = positions.values().toArray(temp);
        for (int i = 0; i < keyset.length; i++) {
            FileWriter fw = new FileWriter(folderName + "/" + keyset[i] + ".txt");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(valueCollection[i]);
            bw.close();
            fw.close();

        }
    }

    private static HashMap<String, String> createPositionFiles(String arg, String folderName) throws FileNotFoundException {

        File posFile = new File(arg);
        Scanner scan = new Scanner(posFile);
        int numberOfLines = countLines(posFile);
        HashMap<String, String> positions = new HashMap<>(numberOfLines / 2 * 5, 1);
        new File(folderName).mkdir();
        scan.nextLine();
        while (scan.hasNextLine()) {
            String chr = scan.next();
            String pos1 = chr + "_" + scan.next();
            scan.next(); //this line is changes whether HiC or Taha file
            String pos2 = chr + "_" + scan.next();
            scan.nextLine();
            if (null == positions.put(pos1, "")) {
                createFile(pos1, folderName);
            }
            if (null == positions.put(pos2, "")) {
                createFile(pos1, folderName);
            }
        }
        scan.close();
        return positions;
    }

    private static void createFile(String pos, String folderName) {
        File f = new File(folderName + "/" + pos + ".txt");
    }

    private static int countLines(File posFile) throws FileNotFoundException {
        int numberOfLines = 0;
        try (Scanner scan = new Scanner(posFile)) {
            while (scan.hasNextLine()) {
                scan.nextLine();
                numberOfLines++;
            }
        }
        return numberOfLines;
    }

    private static HashMap<String, Integer> createSNPDict(String arg, int resolution) throws FileNotFoundException {

        File posFile = new File(arg);
        HashMap<String, Integer> positions;
        try (Scanner scan = new Scanner(posFile)) {
            int numberOfLines = countLines(posFile);
            positions = new HashMap<>(numberOfLines / 4 * 5, 1);
            scan.nextLine();
            //assuming resolution is equal to difference between two closest position centers
            while (scan.hasNextLine()) {
                String name = scan.next();
                int position = (scan.nextInt() + scan.nextInt() / 2)
                        / resolution * resolution + resolution / 2;
                name += "_" + scan.next();
                positions.put(name, position);
                scan.nextLine();
            }
        }
        return positions;
    }

    private static HashSet<String> createHapMapDict(String arg) throws FileNotFoundException {
        File mapFile = new File(arg);
        Scanner scan = new Scanner(mapFile);
        int numberOfLines = countLines(mapFile);
        HashSet<String> SNPDict = new HashSet<>(numberOfLines / 4 * 5, 1);
        while (scan.hasNextLine()){
            scan.next();
            SNPDict.add(scan.next());
            scan.nextLine();
        }
        scan.close();
        return SNPDict;
    }

}
