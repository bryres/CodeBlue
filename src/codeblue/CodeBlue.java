package codeblue;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.OverlayLayout;
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

    public static JTextArea codePane;
    MemoryPanel memoryPane;
    public static BattlePanel battlePane;
    public static BattleInfo battleInfoPane;
    public static Vector<CodeBlueInstruction> instructions = new Vector<CodeBlueInstruction>();
    public static int currCmdPos = -1;
    public static FontMetrics fm; 
    public static int currX, currY;
    public static JFrame frame;
    public static JPanel graphPanel;
    public static JPanel infoPane;
    public static boolean executionMode = false;
    public JButton stepBtn,backBtn,resetBtn,quitBtn,upBtn,downBtn;
    JDialog tmp;
    JTextArea output;
    JSplitPane splitPane;
    JScrollPane scrollPane;
    public static String newline = System.getProperty("line.separator");
    public static JLabel msgLabel;
    private int codePos = 0;
    public JMenuItem lMitem, exitem;

    public JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;

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
        lMitem = new JMenuItem("Load Memory");
        lMitem.addActionListener(this);
        menu.add(lMitem);
        exitem = new JMenuItem("Execute");
        exitem.addActionListener(this);
        menu.add(exitem);
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
        stepBtn = new JButton("Step");
        stepBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){ stepFunc();}
        });
        stepBtn.setEnabled(false);
        ExecuteBtns.add(stepBtn);
        backBtn = new JButton("Back");
        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){ backFunc();}
        });
        backBtn.setEnabled(false);
        ExecuteBtns.add(backBtn);
        resetBtn = new JButton("Reset");
        resetBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){ resetFunc();}
        });
        resetBtn.setEnabled(false);
        ExecuteBtns.add(resetBtn);
        quitBtn = new JButton("Quit");
        quitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){ quitFunc();}
        });
        quitBtn.setEnabled(false);
        ExecuteBtns.add(quitBtn);
        menuBar.add(ExecuteBtns);
        return menuBar;
    }

    public Container createContentPane() {
        //Create the  panes
        infoPane = new JPanel();
        infoPane.setMinimumSize(new Dimension(300,500) );
        LayoutManager overlay1 = new OverlayLayout(infoPane);
        infoPane.setLayout(overlay1);
        codePane = new JTextArea();
        codePane.setMinimumSize(new Dimension(300,500) );
        infoPane.add(codePane);
        battleInfoPane = new BattleInfo();
        battleInfoPane.setMinimumSize(new Dimension(300,500));
        infoPane.add(battleInfoPane);
        battleInfoPane.setVisible(false);
        memoryPane = new MemoryPanel();
        battlePane = new BattlePanel();
        battlePane.setMinimumSize(new Dimension(250,500));

        graphPanel = new JPanel();
        LayoutManager overlay = new OverlayLayout(graphPanel);
        graphPanel.setLayout(overlay);
        graphPanel.add(memoryPane);
        graphPanel.add(battlePane);
        battlePane.setVisible(false);
        //Create a scrolled text area.
        scrollPane = new JScrollPane(infoPane);
        //Add the text and graph area to the content pane.
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,scrollPane,graphPanel);
        splitPane.setPreferredSize(new Dimension(1000,500));
        splitPane.setDividerLocation(300);
        return splitPane;
    }

 
// Routines to handle execute buttons
      
      
    public void stepFunc(){
        memoryPane.ExecuteInstruction();
    }

    public void backFunc(){
      memoryPane.unExecuteInstruction();
  }
    public void resetFunc(){
        memoryPane.reset();
        memoryPane.loadInstructions();
        memoryPane.setPC(0);
        memoryPane.repaint();
    }
 
    public void quitFunc(){
       memoryPane.setPC(-1);
       stepBtn.setEnabled(false); 
       backBtn.setEnabled(false); 
       resetBtn.setEnabled(false); 
       quitBtn.setEnabled(false); 
       executionMode = false;
    }
    
    public void actionPerformed(ActionEvent e) {
        String fname;
        JMenuItem source = (JMenuItem)(e.getSource());
        String s = source.getText();
        int n;
       if(s=="Execute"){
                executionMode = true;
                stepBtn.setEnabled(true);
                backBtn.setEnabled(true);
                resetBtn.setEnabled(true);
                quitBtn.setEnabled(true);
                currCmdPos = 0;
                memoryPane.setPC(0);
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
                memoryPane.reset();
                codePane.setText("");
            }
        }
        if(s=="Load Memory"){
            quitFunc(); //in case execution was in operation
            codePos = 0;
            instructions.removeAllElements();
            memoryPane.reset();
            do {
                String line = getLine(codePane.getText());
                if(line.length()>0){
                    CodeBlueInstruction Inst = new CodeBlueInstruction();
                    Inst.ParseLine(line);
                    if(!Inst.ignore) {
                        instructions.add(Inst);
                    }
                }
            } while(codePos < codePane.getText().length());
          memoryPane.resolveLabels();
          memoryPane.loadInstructions();  
          memoryPane.repaint();
        }        
        if(s=="Edit/Test Code"){
            battlePane.setVisible(false);
            memoryPane.setVisible(true);
            battleInfoPane.setVisible(false);
            codePane.setVisible(true);
            lMitem.setEnabled(true);
            exitem.setEnabled(true);
        }
        if(s=="Run Battle"){
            quitFunc(); // in case in execution mode
            battlePane.setVisible(true);
            battleInfoPane.setVisible(true);
            codePane.setVisible(false);
            memoryPane.setVisible(false);
            lMitem.setEnabled(false);
            exitem.setEnabled(false);
        }
        if(s=="Clear Battle Memory"){
            battlePane.ClearMemory();
            battleInfoPane.ClearInfo();
        }        
     }
    private String getLine(String s){
        String rtnS = "";
        int endofline = s.indexOf("\n",codePos);
        if (endofline < 0) {
            rtnS = s.substring(codePos);
            codePos = s.length();
        }
        else {
            rtnS = s.substring(codePos,endofline);
            codePos = endofline+1;
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
            inputStream.close();
      }
      catch(FileNotFoundException e) {
          System.out.println("Error opening data file");
      }
      catch(IOException e){
          System.out.println("Error reading data file");
      }
        codePane.setText(InText);
    }    
    
    public void saveData(String fname){
      Writer output = null;
      try {
            output = new BufferedWriter(new FileWriter(fname));
            output.write(codePane.getText());
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

