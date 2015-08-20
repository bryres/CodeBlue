package codeblue;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.ImageIcon;
import java.util.*;
import java.io.*;
import javax.swing.filechooser.FileFilter;
/*
 * CodeBlue.java
 *
 * Created on August 6, 2007, 1:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author Dennis.Schweitzer
 */
public class CodeBlue implements ActionListener{

    public static JTextArea CodePane;
    MemoryPanel MemoryPane;
    public static BattlePanel BattlePane;
    public static BattleInfo BinfoPane;
    public static Vector<CodeBlueInstruction> instructions = new Vector<CodeBlueInstruction>();
    public static int CurrCmdPos = -1;
    public static FontMetrics fm; 
    public static int currX, currY;
    public static JFrame frame;
    public static JPanel GraphPanel;
    public static JPanel InfoPane;
    public static boolean ExecutionMode = false;
    public JButton StepBtn,BackBtn,ResetBtn,QuitBtn,UpBtn,DownBtn;
    JDialog tmp;
    JTextArea output;
    JSplitPane splitPane;
    JScrollPane scrollPane;
    public static String newline = System.getProperty("line.separator");
    public static JLabel msgLabel;
    private int CodePos = 0;
    public JMenuItem LMitem, Exitem;

    public JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu menu, submenu;
        JMenuItem menuItem;
        JRadioButtonMenuItem rbMenuItem;
        JCheckBoxMenuItem cbMenuItem;

        //Create the menu bar.
        menuBar = new JMenuBar();

        //Build the first menu.
        menu = new JMenu("File");
        menuBar.add(menu);

        //a group of JMenuItems
        menuItem = new JMenuItem("Load");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("Save");
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menuItem = new JMenuItem("Clear All");
        menuItem.addActionListener(this);
        menu.add(menuItem);

       //Build second menu in the menu bar.
        menu = new JMenu("Commands");
        menuBar.add(menu);
        menuItem = new JMenuItem("Edit/Test Code");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        LMitem = new JMenuItem("Load Memory");
        LMitem.addActionListener(this);
        menu.add(LMitem);
        Exitem = new JMenuItem("Execute");
        Exitem.addActionListener(this);
        menu.add(Exitem);
        menuItem = new JMenuItem("Run Battle");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("Clear Battle Memory");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        //Build settings menu
        menu = new JMenu("Settings");
        menuBar.add(menu);

        //Build help  menu in the menu bar.
        menu = new JMenu("Help");
        menuBar.add(menu);
        JPanel ExecuteBtns = new JPanel();
        ExecuteBtns.setLayout(new BoxLayout(ExecuteBtns,BoxLayout.LINE_AXIS));
        JLabel blank = new JLabel("                                                 ");
        ExecuteBtns.add(blank);
        StepBtn = new JButton("Step");
        StepBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){ stepFunc();}
        });
        StepBtn.setEnabled(false);
        ExecuteBtns.add(StepBtn);
        BackBtn = new JButton("Back");
        BackBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){ backFunc();}
        });
        BackBtn.setEnabled(false);
        ExecuteBtns.add(BackBtn);
        ResetBtn = new JButton("Reset");
        ResetBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){ resetFunc();}
        });
        ResetBtn.setEnabled(false);
        ExecuteBtns.add(ResetBtn);
        QuitBtn = new JButton("Quit");
        QuitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){ quitFunc();}
        });
        QuitBtn.setEnabled(false);
        ExecuteBtns.add(QuitBtn);
        menuBar.add(ExecuteBtns);
        return menuBar;
    }

    public Container createContentPane() {
        //Create the  panes
        InfoPane = new JPanel();
        InfoPane.setMinimumSize(new Dimension(300,500) );
        LayoutManager overlay1 = new OverlayLayout(InfoPane);
        InfoPane.setLayout(overlay1);
        CodePane = new JTextArea();
        CodePane.setMinimumSize(new Dimension(300,500) );
        InfoPane.add(CodePane);
        BinfoPane = new BattleInfo();
        BinfoPane.setMinimumSize(new Dimension(300,500));
        InfoPane.add(BinfoPane);
        BinfoPane.setVisible(false);
        MemoryPane = new MemoryPanel();
        BattlePane = new BattlePanel();
        BattlePane.setMinimumSize(new Dimension(250,500));

        GraphPanel = new JPanel();
        LayoutManager overlay = new OverlayLayout(GraphPanel);
        GraphPanel.setLayout(overlay);
        GraphPanel.add(MemoryPane);
        GraphPanel.add(BattlePane);
        BattlePane.setVisible(false);
        //Create a scrolled text area.
        scrollPane = new JScrollPane(InfoPane);
        //Add the text and graph area to the content pane.
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,scrollPane,GraphPanel);
        splitPane.setPreferredSize(new Dimension(1000,500));
        splitPane.setDividerLocation(300);
        return splitPane;
    }

 
// Routines to handle execute buttons
      
      
    public void stepFunc(){
        MemoryPane.ExecuteInstruction();
    }

    public void backFunc(){
      MemoryPane.unExecuteInstruction();
  }
    public void resetFunc(){
        MemoryPane.reset();
        MemoryPane.loadInstructions();
        MemoryPane.setPC(0);
        MemoryPane.repaint();
    }
 
    public void quitFunc(){
       MemoryPane.setPC(-1);
       StepBtn.setEnabled(false); 
       BackBtn.setEnabled(false); 
       ResetBtn.setEnabled(false); 
       QuitBtn.setEnabled(false); 
       ExecutionMode = false;
    }
    
    public void actionPerformed(ActionEvent e) {
        String tmpLabel,fname;
        JMenuItem source = (JMenuItem)(e.getSource());
        String s = source.getText();
        int tmpN, tmpA, reply,n;
       if(s=="Execute"){
                ExecutionMode = true;
                StepBtn.setEnabled(true);
                BackBtn.setEnabled(true);
                ResetBtn.setEnabled(true);
                QuitBtn.setEnabled(true);
                CurrCmdPos = 0;
                MemoryPane.setPC(0);
        }
        if(s=="Load"){
            n = 0;
            if(instructions.size()>0) {
              n=JOptionPane.showConfirmDialog(frame,"Are you sure you want to replace the current program?","Load Program",
                    JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
            }
            if(n == 0){
              JFileChooser chooser = new JFileChooser();
              chooser.setCurrentDirectory(new File("."));

              int r = chooser.showOpenDialog(frame);
              if (r == JFileChooser.APPROVE_OPTION) {
                fname = chooser.getSelectedFile().getPath();
                loadData(fname);  
              }
            }
        }
        if(s=="Save"){
          JFileChooser chooser = new JFileChooser();
          chooser.setCurrentDirectory(new File("."));
           int r = chooser.showSaveDialog(frame);
            if (r == JFileChooser.APPROVE_OPTION) {
                fname = chooser.getSelectedFile().getPath();
               saveData(fname);  
            }
        }
        if(s=="Clear All"){
            n=JOptionPane.showConfirmDialog(frame,"Are you sure you want to clear the program/memory?","Clear Graph",
                    JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
            if(n == 0){
                instructions.removeAllElements();
                MemoryPane.reset();
                CodePane.setText("");
            }
        }
        if(s=="Load Memory"){
            quitFunc(); //in case execution was in operation
            CodePos = 0;
            instructions.removeAllElements();
            MemoryPane.reset();
            do {
                String line = getLine(CodePane.getText());
                if(line.length()>0){
                    CodeBlueInstruction Inst = new CodeBlueInstruction();
                    Inst.ParseLine(line);
                    if(!Inst.ignore) {
                        instructions.add(Inst);
                    }
                }
            } while(CodePos < CodePane.getText().length());
          MemoryPane.resolveLabels();
          MemoryPane.loadInstructions();  
          MemoryPane.repaint();
        }        
        if(s=="Edit/Test Code"){
            BattlePane.setVisible(false);
            MemoryPane.setVisible(true);
            BinfoPane.setVisible(false);
            CodePane.setVisible(true);
            LMitem.setEnabled(true);
            Exitem.setEnabled(true);
        }
        if(s=="Run Battle"){
            quitFunc(); // in case in execution mode
            BattlePane.setVisible(true);
            BinfoPane.setVisible(true);
            CodePane.setVisible(false);
            MemoryPane.setVisible(false);
            LMitem.setEnabled(false);
            Exitem.setEnabled(false);
        }
        if(s=="Clear Battle Memory"){
            BattlePane.ClearMemory();
            BinfoPane.ClearInfo();
        }        
     }
    private String getLine(String s){
        String rtnS = "";
        int endofline = s.indexOf("\n",CodePos);
        if (endofline < 0) {
            rtnS = s.substring(CodePos);
            CodePos = s.length();
        }
        else {
            rtnS = s.substring(CodePos,endofline);
            CodePos = endofline+1;
        }
        return rtnS;
    }


    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        frame = new JFrame("CodeBlue Wars");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create/set menu bar and content pane.
        CodeBlue demo = new CodeBlue();
        frame.setJMenuBar(demo.createMenuBar());
        frame.setContentPane(demo.createContentPane());


        //Display the window.
        frame.setSize(800,500);
        frame.setVisible(true);

    }

    public void loadData(String fname){
        String InText = "";
      try {
            BufferedReader inputStream = new BufferedReader(new FileReader(fname));
            String line = null;
            while ((line = inputStream.readLine()) != null){
                InText = InText + line + newline;
            }
      }
      catch(FileNotFoundException e) {
          System.out.println("Error opening data file");
      }
      catch(IOException e){
          System.out.println("Error reading data file");
      }
        CodePane.setText(InText);
    }    
    
    public void saveData(String fname){
      Writer output = null;
      try {
            output = new BufferedWriter(new FileWriter(fname));
            output.write(CodePane.getText());
            output.close();
      }
      catch(FileNotFoundException e) {
          System.out.println("Error opening data file");
      }
      catch(IOException e){
          System.out.println("Error writing data file");
      }
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
    

}

