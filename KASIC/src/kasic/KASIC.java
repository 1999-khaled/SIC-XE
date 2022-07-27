/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kasic;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.HashMap;

public class KASIC {
static HashMap<String, Integer>instructionset = new HashMap<>();

    static int locounter=0;
    static String startAdrs = "";
    static HashMap<String, Integer>symTable = new HashMap<>();
    static int pLength = 0;
    static HashMap<Integer, Integer>lineLocTab = new HashMap<>();
    
    public static void main(String[] args) throws FileNotFoundException {
   instructionset.put("ADD", Integer.parseInt("18", 16));
        instructionset.put("AND", Integer.parseInt("40", 16));
        instructionset.put("COMP", Integer.parseInt("28", 16));
        instructionset.put("DIV", Integer.parseInt("24", 16));
        instructionset.put("J", Integer.parseInt("3C", 16));
        instructionset.put("JEQ", Integer.parseInt("30", 16));
        instructionset.put("JGT", Integer.parseInt("34", 16));
        instructionset.put("JLT", Integer.parseInt("38", 16));
        instructionset.put("JSUB", Integer.parseInt("48", 16));
        instructionset.put("LDA", Integer.parseInt("00", 16));
        instructionset.put("LDCH", Integer.parseInt("50", 16));
        instructionset.put("LDL", Integer.parseInt("08", 16));
        instructionset.put("LDX", Integer.parseInt("04", 16));
        instructionset.put("MUL", Integer.parseInt("20", 16));
        instructionset.put("OR", Integer.parseInt("44", 16));
        instructionset.put("RD", Integer.parseInt("D8", 16));
        instructionset.put("RSUB", Integer.parseInt("4C", 16));
        instructionset.put("STA", Integer.parseInt("0C", 16));
        instructionset.put("STCH", Integer.parseInt("54", 16));
        instructionset.put("STL", Integer.parseInt("14", 16));
        instructionset.put("STSW", Integer.parseInt("E8", 16));
        instructionset.put("STX", Integer.parseInt("10", 16));
        instructionset.put("SUB", Integer.parseInt("1C", 16));
        instructionset.put("TD", Integer.parseInt("E0", 16));
        instructionset.put("TIX", Integer.parseInt("2C", 16));
        instructionset.put("WD", Integer.parseInt("DC", 16));
        System.out.println(instructionset);
    try{
          File myfile = new File("inputfile.txt");
          File symTabFile = new File("symTabFile.txt");
          File HTErecord = new File("HTErecord.txt");
        
            Scanner input = new Scanner(myfile);
            PrintWriter symwriter = new PrintWriter(symTabFile);
            Scanner pass2 = new Scanner(myfile);
            PrintWriter HTE =new PrintWriter(HTErecord);
            int lineindx=0;
            while(input.hasNext()) {
                
            String line = input.nextLine();
             System.out.println(String.format("%04X", locounter) + " " +line);
                String[] codeWords = line.split(" ");
                  lineLocTab.put(lineindx, locounter);
                 if(codeWords.length == 2) {
                    if(instructionset.containsKey(codeWords[0])) {
                        locounter += 3;
                    } else if(instructionset.containsKey(codeWords[1])) {
                        if(symTable.containsKey(codeWords[0])) {
                            System.out.println("Duplication Error");
                        } else {
                            symTable.put(codeWords[0], locounter);
                            symwriter.println(codeWords[0] + " " + String.format("%04X", Integer.parseInt(Integer.toHexString(locounter), 16)));
                            locounter += 3;
                        }
                    } 
                    else if(codeWords[0].equals("END")) {
                        break;
                    } 
                }
                else if (codeWords.length == 3) {
                 if (codeWords[1].equals("START")) {
                        startAdrs = codeWords[2];
                        locounter = Integer.parseInt(startAdrs, 16);
                    } 
                 else{
                        
                     if (symTable.containsKey(codeWords[0])) {
                            System.out.println("Error, Duplicated Symbol");
                        } 
                     else {
                            symTable.put(codeWords[0], locounter);
                            symwriter.println(codeWords[0] + " " + String.format("%04X", Integer.parseInt(Integer.toHexString(locounter), 16)));
                        }
                        if(instructionset.containsKey(codeWords[1])) {
                            locounter += 3;
                        }
                        else if(codeWords[1].equals("WORD")) {
                            
                                    locounter += 3;
                            
                        }
                        else if(codeWords[1].equals("RESW")) {
                            locounter += (3 * Integer.parseInt(codeWords[2]));
                        } 
                        else if(codeWords[1].equals("RESB")) {
                            locounter += Integer.parseInt(codeWords[2]);
                        }
                        else if(codeWords[1].equals("BYTE")) {
                            if(codeWords[2].contains(",")) {
                                String[] arrayWords = codeWords[2].split(",");
                                for(int i = 0; i < arrayWords.length; i++) {
                                    CX(arrayWords[i]);
                                }
                            } else {
                                CX(codeWords[2]);
                            }
                        }
                 }
                 
                
            }else if(codeWords.length == 1) {
                    locounter += 3;
                }
                  lineindx++;
            
          
          
     
        }
            input.close();
            symwriter.close();
           
            pLength = locounter - Integer.parseInt(startAdrs, 16);
            System.out.println("Program Length: " + pLength + ", In Hex: " + String.format("%04X", Integer.parseInt(Integer.toHexString(pLength), 16)));
//pass 2:
            String tRec = "";
            int lineIndex_PASS2 = 0;
            int tStartAddr = 0;
       while(pass2.hasNext()){
                String line = pass2.nextLine();
                String[] codeWords = line.split(" ");
                String objectCode = "";
                
                    if(codeWords.length == 2) {
                    if(instructionset.containsKey(codeWords[0])) {
                      
                        objectCode = String.format("%02X", instructionset.get(codeWords[0]));
                        int opCode = 0x0000;
                        if(codeWords[1].contains(",")){
                            opCode = 0x8000;
                            codeWords[1] = codeWords[1].split(",")[0];
                        }
                        if(symTable.containsKey(codeWords[1])) {
                            opCode += symTable.get(codeWords[1]);
                        }
                        objectCode += String.format("%04X", opCode);
                    }
                }
                    else if(codeWords.length == 3){
                    if(codeWords[1].equals("START")) {
                        String progName = codeWords[0];
                         if(progName.length() > 6) {
                            progName = progName.substring(0, 6);
                        } else if(progName.length() < 6) {
                            for(int i = 0; progName.length() < 6; i++) {
                            progName = progName + " ";
                            }
                        }
                        HTE.println("H " + progName + String.format("%06X", Integer.parseInt(startAdrs, 16)) + " " + String.format("%06X", pLength));
                        tStartAddr = Integer.parseInt(startAdrs, 16);
                        
                        
                    }else {
                        if (instructionset.containsKey(codeWords[1])) {
                            objectCode = String.format("%X", instructionset.get(codeWords[1]));
                            objectCode += String.format("%X", symTable.get(codeWords[2]));
                        }
                        else if (codeWords[1].equals("WORD")) {
                            objectCode = String.format("%06X", Integer.parseInt(codeWords[2]));
                
                        
                    }
                    else if (codeWords[1].equals("BYTE")) {
                            if(codeWords[2].contains("C")) {
                                codeWords[2] = codeWords[2].substring(2, codeWords[2].length()-1);
                                for(char c : codeWords[2].toCharArray()) {
                                    objectCode += String.format("%02X", (int) c);
                                }
                            } else if (codeWords[2].contains("X")) {
                                codeWords[2] = codeWords[2].substring(2, codeWords[2].length()-1);
                                objectCode = String.format("%02X", Integer.parseInt(codeWords[2], 16));
                            } 
                        }else if(codeWords.length == 1) {
                    objectCode = String.format("%02X", instructionset.get(codeWords[0]));
                    objectCode += "0000";
                } 
                        
                   if(((tRec + " " + objectCode).replace(" ", "").length() > 60) || (codeWords[1].equals("RESB")) || (codeWords[1].equals("RESW"))) {
                    int tLen = tRec.replace(" ", "").length();
                    tLen /= 2;
                    HTE.println("T" + String.format("%06X", tStartAddr) + " " + String.format("%02X", tLen) + tRec);
                    tRec = objectCode;
                    tStartAddr = lineLocTab.get(lineIndex_PASS2);
                } else {
                    tRec += " " + objectCode;
                }
                lineIndex_PASS2++;
       
       
       }
                     int tLen = tRec.replace(" ", "").length();
            tLen /= 2;
            HTE.println("T " + String.format("%06X", tStartAddr) + " " + String.format("%02X", tLen) + tRec);
            HTE.print("E " + String.format("%06X", Integer.parseInt(startAdrs, 16)));
            HTE.close();
            
       
       }
       }
       pass2.close();
      
       

    }catch (FileNotFoundException ex) {
        File myfile = new File("inputfile.txt");     
    
    }
}
     static void CX(String word) {
        if(word.contains("C")) {
            word = word.substring(2, word.length()-1);
            locounter += word.length();
        } else if (word.contains("X")) {
            word = word.substring(2, word.length()-1);
            double numBytes = Math.ceil(word.length() / 2);
            locounter += numBytes;
        } else {
            locounter++;
        }    }
    
}
