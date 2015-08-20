package codeblue;

import javax.swing.*;
import javax.swing.table.*;
import java.util.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
/*
 * BattleInfo.java
 *
 * Created on August 22, 2007, 8:17 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author Dennis.Schweitzer
 */
public class BattleInfo extends JPanel implements ActionListener{
    
 public static JLabel FileName1, FileName2, StartAddr1, StartAddr2,
         PC1lbl,PC2lbl,NumIters,Winner;
 public static JTextField MaxIters;
 public static int memStart1 = 0,memStart2 = 0;
 public static JComboBox Order;
 javax.swing.Timer timer;
 public static Vector<CodeBlueInstruction> instructions = new Vector<CodeBlueInstruction>();
 public static Vector<CodeBlueInstruction> instructions1 = new Vector<CodeBlueInstruction>();
 public static Vector<CodeBlueInstruction> instructions2 = new Vector<CodeBlueInstruction>();
 public static String newline = System.getProperty("line.separator");
 public static int maxIterations = 5000, whoFirst = 1, turn,numSteps;
 public static String WinString;
    /** Creates a new instance of BattleInfo */
    public BattleInfo() {   
        JButton Load1 = new JButton("Load Pgm 1:");
        Load1.addActionListener(this);
        JButton Load2 = new JButton("Load Pgm 2:");
        Load2.addActionListener(this);
        FileName1 = new JLabel("<none loaded>");
        FileName2 = new JLabel("<none loaded>");
        JLabel SA1 = new JLabel("Start Addr 1: ");
        JLabel SA2 = new JLabel("Start Addr 2: ");
        StartAddr1 = new JLabel("0");
        StartAddr2 = new JLabel("0");
        JLabel PC1label = new JLabel("Pgm Ctr 1:");
        JLabel PC2label = new JLabel("Pgm Ctr 2:");
        PC1lbl = new JLabel("0");
        PC2lbl = new JLabel("0");
        this.setLayout(new SpringLayout());
        add(Load1);
        add(FileName1);
        add(SA1);
        add(StartAddr1);
        add(PC1label);
        add(PC1lbl);
        add(Load2);
        add(FileName2);
        add(SA2);
        add(StartAddr2);
        add(PC2label);
        add(PC2lbl);
        JLabel MaxItersLbl = new JLabel("Max Iterations: ");
        add(MaxItersLbl);
        MaxIters = new JTextField("5000");
        MaxIters.setMaximumSize(new Dimension(50,20));
        add(MaxIters);
        JLabel ExOrder = new JLabel("Execution Order:");
        add(ExOrder);
        String[] orderStr = {"1 then 2","2 then 1"};
        Order = new JComboBox(orderStr);
        Order.setMaximumSize(new Dimension(50,20));
        Order.setSelectedIndex(0);
        Order.addActionListener(this);
        add(Order);
        JButton Run = new JButton("Run Single Battle");
        Run.addActionListener(this);
        add(Run);
        JButton Run100 = new JButton("Run 100 Battles");
        Run100.addActionListener(this);
        add(Run100);
        JLabel Rslts = new JLabel("*** RESULTS *** ");
        JLabel blank2 = new JLabel();
        add(Rslts);
        add(blank2);
        JLabel NumItersLbl = new JLabel("# Iterations: ");
        add(NumItersLbl);
        NumIters = new JLabel("0");
        add(NumIters);
        JLabel WinLbl = new JLabel("Winner: ");
        add(WinLbl);
        Winner = new JLabel("");
        add(Winner);
        JButton Stop = new JButton("Stop Battle");
        Stop.addActionListener(this);
        JButton Reset = new JButton("Reset Programs");
        Reset.addActionListener(this);
        add(Stop);
        add(Reset);
        SpringUtilities.makeCompactGrid(this,13,2,10,10,10,10);
    }
public void actionPerformed(ActionEvent e){
        if (e.getActionCommand().equals("Load Pgm 1:")) {
            loadFile(1);
        }
        if (e.getActionCommand().equals("Load Pgm 2:")) {
            loadFile(2);
        }
        if (e.getActionCommand().equals("Run 100 Battles")) {
            int wins1 = 0, wins2 = 0, ties = 0;
            maxIterations = Integer.parseInt(MaxIters.getText());
            for(int i=0;i<100;i++){
                CodeBlue.BattlePane.ClearMemory();
                reloadBM(1);
                reloadBM(2);
                int numsteps = 0;
                if(i%2==0) turn = whoFirst;
                else { if(whoFirst==1) turn = 2; else turn = 1;}
                do{
                  CodeBlue.BattlePane.ExecuteInstruction(turn);
                  if(CodeBlue.BattlePane.legalInstruction){
                    numsteps++;
                    if(turn==1) turn = 2; else turn = 1;
                  }
                }
                while(CodeBlue.BattlePane.legalInstruction && 
                        (numsteps <= maxIterations));
                if(CodeBlue.BattlePane.legalInstruction) ties++;
                else {if(turn==1) wins2++; else wins1++;}
            }
           Winner.setText("Wins: 1-"+wins1+" 2-"+wins2+ " Ties: "+ties);
           repaint();
        }
        if (e.getActionCommand().equals("Run Single Battle")) {
            if((memStart1 !=0)&&(memStart2 !=0)){ //make sure pgms loaded
               turn = whoFirst;
               WinString = "TIE";
               Winner.setText("");
               numSteps = 0;
               maxIterations = Integer.parseInt(MaxIters.getText());
               timer = new javax.swing.Timer(2,new ActionListener() {
                    public void actionPerformed(ActionEvent e){ Step(); }});
               timer.start();
            }
        }
        if (e.getActionCommand().equals("Stop Battle")) {
            timer.stop();
        }
        if (e.getActionCommand().equals("Reset Programs")) {
          CodeBlue.BattlePane.ClearMemory();
          PC1lbl.setText("0");
          PC2lbl.setText("0");
          memStart1 = 0;
          memStart2 = 0;
          StartAddr1.setText("0");
          StartAddr2.setText("0");
          Winner.setText("");
          NumIters.setText("0");
          reloadBM(1);
          reloadBM(2);
        }
        
}
public void reloadBM(int PgmNum){
          instructions.removeAllElements();
          if(PgmNum ==1){
            for(int i=0;i<instructions1.size();i++){
              CodeBlueInstruction tmp = new CodeBlueInstruction();
              tmp.copyInst(instructions1.elementAt(i));
              instructions.addElement(tmp);
            }
            LoadBattleMemory(1);
          } else {
             for(int i=0;i<instructions2.size();i++){
              CodeBlueInstruction tmp = new CodeBlueInstruction();
              tmp.copyInst(instructions2.elementAt(i));
              instructions.addElement(tmp);
            }
            LoadBattleMemory(2);
          }   
}
    public void Step(){
        CodeBlue.BattlePane.ExecuteInstruction(turn);
        if(CodeBlue.BattlePane.legalInstruction){
           numSteps++;
           NumIters.setText(""+numSteps/2);
           if(turn==1)
               PC1lbl.setText(""+CodeBlue.BattlePane.PC1);
           else  PC2lbl.setText(""+CodeBlue.BattlePane.PC2);
           if(turn==1) turn = 2; else turn = 1;
           repaint();
        }
        else {
            if(turn ==1) WinString = "PROGRAM 2";
            else WinString = "PROGRAM 1";
            Winner.setText(WinString);
            timer.stop();
        }
        if(numSteps >= 2*maxIterations){
            Winner.setText(WinString);
            timer.stop();
        }
        
        
    }
    public void loadFile(int PgmNum){
        String fname, InText = "";
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        int r = chooser.showOpenDialog(CodeBlue.frame);
        if (r == JFileChooser.APPROVE_OPTION) {
           fname = chooser.getSelectedFile().getPath();
        int CodePos = 0;
        instructions.removeAllElements();
       try {
            BufferedReader inputStream = new BufferedReader(new FileReader(fname));
            String line = null;
            while ((line = inputStream.readLine()) != null){
                if(line.length()>0){
                    CodeBlueInstruction Inst = new CodeBlueInstruction();
                    Inst.ParseLine(line);
                    if(!Inst.ignore) {
                        instructions.add(Inst);
                    }
                }
            }
        }
         catch(FileNotFoundException e) {
          System.out.println("Error opening data file");
        }
        catch(IOException e){
            System.out.println("Error reading data file");
          }
           String shortName = fname.substring(fname.lastIndexOf("\\")+1);
          resolveLabels();
          if(PgmNum==1){
              FileName1.setText(shortName);
              instructions1.removeAllElements();
              for(int i=0;i<instructions.size();i++){
                CodeBlueInstruction tmp = new CodeBlueInstruction();
                tmp.copyInst(instructions.elementAt(i));
                instructions1.addElement(tmp);
              }
          } else {
                FileName2.setText(shortName);
                instructions2.removeAllElements();
                for(int i=0;i<instructions.size();i++){
                  CodeBlueInstruction tmp = new CodeBlueInstruction();
                  tmp.copyInst(instructions.elementAt(i));
                  instructions2.addElement(tmp);
              }
          }
          LoadBattleMemory(PgmNum);
        }
    }
    public void LoadBattleMemory(int PgmNum){
          Random rn = new Random();
          int otherstart, RandomStart;
          if(PgmNum == 1) otherstart = memStart2;
          else otherstart = memStart1;
          do {
             RandomStart = Math.abs(rn.nextInt()) % 900;
          } while((RandomStart> otherstart-100)&&(RandomStart < otherstart+100));
          CodeBlue.BattlePane.loadInstructions(PgmNum,RandomStart);  
           if(PgmNum == 1){
               memStart1 = RandomStart;
               PC1lbl.setText(""+RandomStart);
               StartAddr1.setText(""+RandomStart);
           } else {
               memStart2 = RandomStart;
               PC2lbl.setText(""+RandomStart);
               StartAddr2.setText(""+RandomStart);
           }
           
        }
            
    public void updatePC(int PgmNum,int NewPC){
        if(PgmNum == 1){
            PC1lbl.setText(""+NewPC);
        } else {
            PC2lbl.setText(""+NewPC);
        }
    }
    public void ClearInfo(){
        FileName1.setText("<none loaded>");
        FileName2.setText("<none loaded>");
        PC1lbl.setText("0");
        PC2lbl.setText("0");
        memStart1 = 0;
        memStart2 = 0;
        StartAddr1.setText("0");
        StartAddr2.setText("0");
        Winner.setText("");
        NumIters.setText("0");
    }
    public void resolveLabels(){
        for(int i=0;i<instructions.size();i++){
          CodeBlueInstruction inst = instructions.elementAt(i);
          if(inst.param1Label.length()>0) { //resolve param1 label
              boolean found = false;
              for(int j=0;j<instructions.size();j++){
                  CodeBlueInstruction tmp = instructions.elementAt(j);
                  if(tmp.Label.equals(inst.param1Label)){
                      inst.param1 = j-i;
                      found = true;
                  }
              }
              if(!found) inst.OpCode = -1;
          }
          if(inst.param2Label.length()>0) { //resolve param1 label
              boolean found = false;
              for(int j=0;j<instructions.size();j++){
                  CodeBlueInstruction tmp = instructions.elementAt(j);
                  if(tmp.Label.equals(inst.param2Label)){
                      inst.param2 = j-i;
                      found = true;
                  }
              }
              if(!found) inst.OpCode = -1;
          }
        }
    }
}
