package codeblue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
/*
 * MemoryPanel.java
 *
 * Created on August 6, 2007, 1:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author Dennis.Schweitzer
 */
public class MemoryPanel extends JPanel{
    String[] columnNames = {"Addr","Value","Addr","Value","Addr","Value","Addr","Value"};
    public CodeBlueInstruction[] memory = new CodeBlueInstruction[99];
    public Object[][] data = new Object[25][9];
    public int[][] cellColor = new int[25][9];
    public Color[] Colors = new Color[7];
    JTable table;
    public Vector<ExecuteState> ExecutionStack = new Vector<ExecuteState>();
    public int PC;
    
    /** Creates a new instance of MemoryPanel */
    public MemoryPanel() {
        Colors[0] = Color.WHITE;
        Colors[1] = Color.GREEN;
        Colors[2] = new Color(200,200,0);
        Colors[3] = new Color(0,200,200);
        Colors[4] = new Color(250,250,0);
        Colors[5] = Color.RED;
        Colors[6] = new Color(0,250,250);
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        table = new JTable(new MyTableModel());
        TableCellRenderer renderer = new CustomTableCellRenderer();
 	table.setColumnSelectionAllowed( false );
	table.setRowSelectionAllowed( false );
        for(int i=0;i<25;i++){
            table.setValueAt(""+i,i,0);
            table.setValueAt("000000000",i,1);
            table.setValueAt("",i,2);
            table.setValueAt(""+(25+i),i,3);
            table.setValueAt("000000000",i,4);
            table.setValueAt("",i,5);
            table.setValueAt(""+(50+i),i,6);
            table.setValueAt("000000000",i,7);
            table.setValueAt("",i,8);
        }
        for(int i=0;i<75;i++){
            memory[i] = new CodeBlueInstruction();
            memory[i].OpCode = 0;
            memory[i].param1 = 0;
            memory[i].type1 = 0;
        }
        for(int r = 0;r<25;r++)
            for(int c = 0;c<9;c++)
                cellColor[r][c] = 0;
        table.setFont(new Font("Trebuchet",Font.PLAIN, 10));
        
        TableColumn column = null;
        for(int i=0;i<9;i++) {
            column = table.getColumnModel().getColumn(i);
            column.setCellRenderer(renderer);
            if((i % 3) ==0) 
                column.setPreferredWidth(20);
            else column.setPreferredWidth(50);
        }
        JScrollPane scrollPane = new JScrollPane(table);
        topPanel.add(scrollPane,BorderLayout.CENTER);
        this.add(topPanel);
    }
    public void reset(){
         for(int i=0;i<25;i++){
            table.setValueAt(""+i,i,0);
            table.setValueAt("000000000",i,1);
            table.setValueAt("",i,2);
            table.setValueAt(""+(25+i),i,3);
            table.setValueAt("000000000",i,4);
            table.setValueAt("",i,5);
            table.setValueAt(""+(50+i),i,6);
            table.setValueAt("000000000",i,7);
            table.setValueAt("",i,8);
       }
         for(int i=0;i<75;i++){
            memory[i].OpCode = 0;
            memory[i].param1 = 0;
            memory[i].type1 = 0;
        }
         ExecutionStack.removeAllElements();
   }
    public void loadInstructions(){
        for(int i=0;i<CodeBlue.instructions.size();i++){
            setInst(CodeBlue.instructions.elementAt(i),i);
        }
    }
    public void ExecuteInstruction(){
        int dest,val,val1,val2;
      CodeBlueInstruction Inst = new CodeBlueInstruction();
      Inst.copyInst(memory[PC]);
        switch (Inst.OpCode) {
             case 0:  break;
             case 1: //COPY
                 CodeBlueInstruction src = getParam1Inst(Inst);
                 if(src.OpCode != CodeBlueInstruction.ILLEGAL_OP){
                   dest = getParam2Dest(Inst);
                   if(dest != -1){
                     ExecuteState es = new ExecuteState();
                     es.PrevPC = PC;
                     es.ChangedLoc = dest;
                     es.OldInst.copyInst(memory[dest]);
                     ExecutionStack.addElement(es);
                     setInst(src,dest);
                     setPC((PC+1)%75);
                 }
              }
                 break;
             case 2: //COMPARE
                 val1 = getParam1Value(Inst);
                 if(val1 != -999) {
                     val2 = getParam2Value(Inst);
                     if(val2 != -999) {
                       ExecuteState es = new ExecuteState();
                       es.PrevPC = PC;
                       ExecutionStack.addElement(es);
                       if(val1 == val2)
                             setPC((PC+1)%75);
                         else setPC((PC+2)%75);
                     }
                 }
                 break;
             case 3: //JUMP
                 dest = getParam1Dest(Inst);
                 if(dest != -1){
                     ExecuteState es = new ExecuteState();
                     es.PrevPC = PC;
                     ExecutionStack.addElement(es);
                     setPC(dest);
                 }
                 break;
             case 4: //JUMPZ
                 dest = getParam1Dest(Inst);
                 if(dest != -1){
                    val = getParam2Value(Inst);
                 ExecuteState es = new ExecuteState();
                 es.PrevPC = PC;
                 ExecutionStack.addElement(es);
                 if(val == 0)
                     setPC(dest);
                 else
                     setPC(PC+1);
                 }
                 break;
             case 5: //JUMPNZ
                 dest = getParam1Dest(Inst);
                 if(dest != -1){
                    val = getParam2Value(Inst);
                    ExecuteState es = new ExecuteState();
                    es.PrevPC = PC;
                    ExecutionStack.addElement(es);
                    if(val != 0)
                        setPC(dest);
                    else
                        setPC(PC+1);
                 }
                 break;
             case 6: //ADD
                 val1 = getParam1Value(Inst);
                 if(val1 !=999){
                     dest = getParam2Dest(Inst);
                     if(dest != -1){
                         if(memory[dest].OpCode == CodeBlueInstruction.DATA){
                             ExecuteState es = new ExecuteState();
                             es.PrevPC = PC;
                             es.ChangedLoc = dest;
                             es.OldInst.copyInst(memory[dest]);
                             ExecutionStack.addElement(es);
                             memory[dest].param1 = memory[dest].param1+val1;
                             setInst(memory[dest],dest);
                             setPC((PC+1)%75);
                         }
                     }
                 }
                 break;
             case 7: //SUBTRACT
                val1 = getParam1Value(Inst);
                 if(val1 !=999){
                     dest = getParam2Dest(Inst);
                     if(dest != -1){
                         if(memory[dest].OpCode == CodeBlueInstruction.DATA){
                             ExecuteState es = new ExecuteState();
                             es.PrevPC = PC;
                             es.ChangedLoc = dest;
                             es.OldInst.copyInst(memory[dest]);
                             ExecutionStack.addElement(es);
                             memory[dest].param1 = memory[dest].param1-val1;
                             setInst(memory[dest],dest);
                             setPC((PC+1)%75);
                         }
                     }
                 }
                 break;
             default: break;
                 
         }
        repaint();
    }
    public int getParam1Value(CodeBlueInstruction Inst){
        int loc;
        int tmp = -999;
        if(Inst.type1 == CodeBlueInstruction.LITERAL){
            tmp = Inst.param1;
        } else if(Inst.type1 == CodeBlueInstruction.RELATIVE){
            loc = (PC+Inst.param1+75)%75;
            if(memory[loc].OpCode == CodeBlueInstruction.DATA)
                tmp = memory[loc].param1;
            } else if(Inst.type1 == CodeBlueInstruction.INDIRECT){
                loc = (PC + Inst.param1+75)%75;
                if(memory[loc].OpCode == CodeBlueInstruction.DATA){
                    loc = (loc+memory[loc].param1+75)%75;
                    if(memory[loc].OpCode == CodeBlueInstruction.DATA){
                        tmp = memory[loc].param1;
                    }
                }
            }
        return tmp;
    }    
    public int getParam2Value(CodeBlueInstruction Inst){
        int loc;
        int tmp = -999;
        if(Inst.type2 == CodeBlueInstruction.LITERAL){
            tmp = Inst.param2;
        } else if(Inst.type2 == CodeBlueInstruction.RELATIVE){
            loc = (PC+Inst.param2+75)%75;
            if(memory[loc].OpCode == CodeBlueInstruction.DATA)
                tmp = memory[loc].param1;
            } else if(Inst.type2 == CodeBlueInstruction.INDIRECT){
                loc = (PC + Inst.param2+75)%75;
                if(memory[loc].OpCode == CodeBlueInstruction.DATA){
                    loc = (loc+memory[loc].param1+75)%75;
                    if(memory[loc].OpCode == CodeBlueInstruction.DATA){
                        tmp = memory[loc].param1;
                    }
                }
            }
        return tmp;
    }    
    public CodeBlueInstruction getParam1Inst(CodeBlueInstruction Inst){
        CodeBlueInstruction tmp = new CodeBlueInstruction();
        if(Inst.type1 == CodeBlueInstruction.LITERAL){
            tmp.param1 = Inst.param1;
        } else
            if(Inst.type1 == CodeBlueInstruction.RELATIVE){
              int loc = (PC + Inst.param1 + 75)%75; 
              tmp.copyInst(memory[loc]);
            } else{
              int loc = (PC + Inst.param1 + 75)%75;
              if(memory[loc].OpCode != CodeBlueInstruction.DATA){
                 tmp.OpCode = CodeBlueInstruction.ILLEGAL_OP;
              }
              else
                  tmp.copyInst(memory[(loc+memory[loc].param1+75)%75]);
            }
        return tmp;
    }
    public int getParam2Dest(CodeBlueInstruction Inst){
        int loc = -1;
        if(Inst.type2 == CodeBlueInstruction.RELATIVE){
            loc = (PC + Inst.param2 + 75)%75;
        } else if(Inst.type2 == CodeBlueInstruction.INDIRECT){
            int tmploc = (PC +Inst.param2 + 75)%75;
            if(memory[tmploc].OpCode == CodeBlueInstruction.DATA){
                loc = (tmploc+memory[tmploc].param1+75)%75;
            }
        }
        return loc;
    }
   public int getParam1Dest(CodeBlueInstruction Inst){
        int loc = -1;
        if(Inst.type1 == CodeBlueInstruction.RELATIVE){
            loc = (PC + Inst.param1 + 75)%75;
        } else if(Inst.type1 == CodeBlueInstruction.INDIRECT){
            int tmploc = (PC +Inst.param1 + 75)%75;
            if(memory[tmploc].OpCode == CodeBlueInstruction.DATA){
                loc = (tmploc+memory[tmploc].param1+75)%75;
            }
        }
        return loc;
    }
    public void unExecuteInstruction(){
        if(ExecutionStack.size()>0){
            if(ExecutionStack.lastElement().ChangedLoc >= 0){
                setInst(ExecutionStack.lastElement().OldInst,ExecutionStack.lastElement().ChangedLoc);
            }
            setPC(ExecutionStack.lastElement().PrevPC);
            ExecutionStack.removeElementAt(ExecutionStack.size()-1);
        }
    }
    
    public void setPC(int loc){
        for(int i=0;i<25;i++)
            for(int j=0;j<9;j++)
                cellColor[i][j] = 0;
       if(loc == -1){
            this.repaint();
            return;
       }
       PC = loc;
       cellColor[loc%25][loc/25*3] = 1;
       if(memory[loc].OpCode == CodeBlueInstruction.ILLEGAL_OP){
           cellColor[loc%25][3*(loc/25)+2] = 5;
           return;
       } 
       if(memory[loc].OpCode == CodeBlueInstruction.DATA){
           cellColor[loc%25][3*(loc/25)+2] = 5;
           return;
       }
       else {
       if(memory[loc].type1 == CodeBlueInstruction.RELATIVE){
           int newloc = (loc+memory[loc].param1+100)%100;
           cellColor[newloc%25][3*(newloc/25)+2] = 4;
       } else
       if(memory[loc].type1 == CodeBlueInstruction.INDIRECT) {
           int newloc = (loc+memory[loc].param1+75)%75;
           cellColor[newloc%25][3*(newloc/25)+2] = 2;
           if (memory[newloc].OpCode != CodeBlueInstruction.DATA) {
             cellColor[newloc%25][3*(newloc/25)+2] = 5;
           } else {
               newloc = (newloc + memory[newloc].param1+75)%75;
               cellColor[newloc%25][3*(newloc/25)+2] = 4;
           }
       }
       if(memory[loc].numParams ==2) {
           if(memory[loc].type2 == CodeBlueInstruction.RELATIVE){
               int newloc = (loc+memory[loc].param2+75)%75;
               cellColor[newloc%25][3*(newloc/25)+2] = 6;
           }
       if(memory[loc].type2 == CodeBlueInstruction.INDIRECT) {
           int newloc = (loc+memory[loc].param2+75)%75;
           cellColor[newloc%25][3*(newloc/25)+2] = 3;
           if (memory[newloc].OpCode != CodeBlueInstruction.DATA) {
             cellColor[newloc%25][3*(newloc/25)+2] = 5;
           } else {
               newloc = (newloc + memory[newloc].param1+75)%75;
               cellColor[newloc%25][3*(newloc/25)+2] = 6;
           }
       }
       }
       }
       this.repaint();
    }
    
    public void resolveLabels(){
        for(int i=0;i<CodeBlue.instructions.size();i++){
          CodeBlueInstruction inst = CodeBlue.instructions.elementAt(i);
          if(inst.param1Label.length()>0) { //resolve param1 label
              boolean found = false;
              for(int j=0;j<CodeBlue.instructions.size();j++){
                  CodeBlueInstruction tmp = CodeBlue.instructions.elementAt(j);
                  if(tmp.Label.equals(inst.param1Label)){
                      inst.param1 = j-i;
                      found = true;
                  }
              }
              if(!found) inst.OpCode = -1;
          }
          if(inst.param2Label.length()>0) { //resolve param1 label
              boolean found = false;
              for(int j=0;j<CodeBlue.instructions.size();j++){
                  CodeBlueInstruction tmp = CodeBlue.instructions.elementAt(j);
                  if(tmp.Label.equals(inst.param2Label)){
                      inst.param2 = j-i;
                      found = true;
                  }
              }
              if(!found) inst.OpCode = -1;
          }
        }
    }
      public void setInst(CodeBlueInstruction Inst,int loc){
          String InstStr = "";
         memory[loc].copyInst(Inst);
         memory[loc].makeValue();
         switch (Inst.OpCode) {
             case 0: InstStr = ""+Inst.param1;break;
             case 1: InstStr = "CP "+paramStr(Inst,2); break;
             case 2: InstStr = "CMP"+paramStr(Inst,2); break;
             case 3: InstStr = "JMP"+paramStr(Inst,1); break;
             case 4: InstStr = "JZ"+paramStr(Inst,2);  break;
             case 5: InstStr = "JNZ"+paramStr(Inst,2); break;
             case 6: InstStr = "ADD"+paramStr(Inst,2); break;
             case 7: InstStr = "SUB"+paramStr(Inst,2); break;
             default: InstStr = "**ERR**";break;
                 
         }
         table.setValueAt(memory[loc].value,loc%25,1+loc/25*3);
         table.setValueAt(InstStr,loc%25,2+loc/25*3);
      } 
      private String paramStr(CodeBlueInstruction inst,int numParams){
          String rtnS= "";
          if(inst.type1 == CodeBlueInstruction.LITERAL) rtnS = rtnS + "#";
          if(inst.type1 == CodeBlueInstruction.INDIRECT) rtnS = rtnS + "@";
          rtnS = rtnS + inst.param1;
          if(numParams > 1){
              rtnS = rtnS + ", ";
              if(inst.type2 == CodeBlueInstruction.LITERAL) rtnS = rtnS + "#";
              if(inst.type2 == CodeBlueInstruction.INDIRECT) rtnS = rtnS + "@";
              rtnS = rtnS + inst.param2;
          }
          return rtnS;
      }
    class MyTableModel extends AbstractTableModel {
        private String[] columnNames = {"Addr",
                                        "Value",
                                        "Instr",
                                        "Addr",
                                        "Value",
                                        "Instr",
                                        "Addr",
                                        "Value",
                                        "Instr"};
        private Object[][] data = new Object[25][9];

        public boolean isCellEditable(int rowIndex, int vColIndex){
          return false;
        }
        
        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
        public void setValueAt(Object value, int row, int col) {

            data[row][col] = value;
            fireTableCellUpdated(row, col);

        }
    }
public class CustomTableCellRenderer extends DefaultTableCellRenderer 
{
    public Component getTableCellRendererComponent
       (JTable table, Object value, boolean isSelected,
       boolean hasFocus, int row, int column) 
    {
        Component cell = super.getTableCellRendererComponent
           (table, value, isSelected, hasFocus, row, column);
             cell.setBackground( Colors[cellColor[row][column]] );
        return cell;
    }
}

}
