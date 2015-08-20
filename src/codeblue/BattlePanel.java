package codeblue;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;
/*
 * BattlePanel.java
 *
 * Created on August 21, 2007, 3:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author Dennis.Schweitzer
 */
public class BattlePanel extends JPanel{
    public static boolean legalInstruction;
    public int[] Owner = new int[1000];
    public static int PC1, PC2;
    public CodeBlueInstruction[] memory = new CodeBlueInstruction[1000];
    public static Color instruct1 = new Color(255,255,0);
    public static Color data1 = new Color(200,200,0);
    public static Color instruct2 = new Color(0,255,0);
    public static Color data2 = new Color(0,200,0);
    public static Color PC1Color = new Color(0,0,0);
    public static Color PC2Color = new Color(255,255,255);
    int PC;
    /** Creates a new instance of BattlePanel */
    public BattlePanel() {
        for(int i=0;i<1000;i++){
            Owner[i] = 0;
            memory[i] = new CodeBlueInstruction();
            PC1 = 0;
            PC2 = 0;
        }
    }
    public void loadInstructions(int PgmNum,int start){
        if(PgmNum ==1) PC1 = start;
        else PC2 = start;
         for(int i=0;i<BattleInfo.instructions.size();i++){
             memory[start].copyInst(BattleInfo.instructions.elementAt(i));
             Owner[start] = PgmNum;
             start++;
         }
       repaint(); 
    }
    public void ClearMemory(){
        CodeBlueInstruction blank = new CodeBlueInstruction();
        for(int i=0;i<1000;i++){
            Owner[i] = 0;
            memory[i].copyInst(blank);
        }
        repaint();
        PC1 = 0;
        PC2 = 0;
    }
    public void ExecuteInstruction(int PgmNum){
      int dest,val,val1,val2;
      legalInstruction = false;
      CodeBlueInstruction Inst = new CodeBlueInstruction();
      if(PgmNum == 1) PC = PC1%1000;
      else PC = PC2%1000;
      Inst.copyInst(memory[PC]);
      switch (Inst.OpCode) {
             case 0:  break;
             case 1: //COPY
                 CodeBlueInstruction src = getParam1Inst(Inst);
                 if(src.OpCode != CodeBlueInstruction.ILLEGAL_OP){
                   dest = getParam2Dest(Inst);
                   if(dest != -1){
                     memory[dest].copyInst(src);
                     Owner[dest] = PgmNum;
                     PC = (PC+1)%1000;
                     legalInstruction = true;
                 }
              }
                 break;
             case 2: //COMPARE
                 val1 = getParam1Value(Inst);
                 if(val1 != -99999) {
                     val2 = getParam2Value(Inst);
                     if(val2 != -99999) {
                        legalInstruction = true;
                          if(val1 == val2)
                             PC = (PC+1)%1000;
                             else PC =(PC+2)%1000;
                     }
                 }
                 break;
             case 3: //JUMP
                 dest = getParam1Dest(Inst);
                 if(dest != -1){
                     legalInstruction = true;
                     PC = dest;
                 }
                 break;
             case 4: //JUMPZ
                 dest = getParam1Dest(Inst);
                 if(dest != -1){
                    val = getParam2Value(Inst);
                 legalInstruction = true;
                 if(val == 0)
                     PC = dest;
                 else
                     PC = PC + 1;
                 }
                 break;
             case 5: //JUMPNZ
                 dest = getParam1Dest(Inst);
                 if(dest != -1){
                    val = getParam2Value(Inst);
                    legalInstruction = true;
                    if(val != 0)
                        PC = dest;
                    else
                        PC = PC + 1;
                 }
                 break;
             case 6: //ADD
                 val1 = getParam1Value(Inst);
                 if(val1 !=99999){
                     dest = getParam2Dest(Inst);
                     if(dest != -1){
                         if(memory[dest].OpCode == CodeBlueInstruction.DATA){
                             legalInstruction = true;
                             Owner[dest] = PgmNum;
                             memory[dest].param1 = memory[dest].param1+val1;
                             PC = (PC+1)%1000;
                         }
                     }
                 }
                 break;
             case 7: //SUBTRACT
                val1 = getParam1Value(Inst);
                 if(val1 !=99999){
                     dest = getParam2Dest(Inst);
                     if(dest != -1){
                         if(memory[dest].OpCode == CodeBlueInstruction.DATA){
                             legalInstruction = true;
                             Owner[dest] = PgmNum;
                             memory[dest].param1 = memory[dest].param1-val1;
                             PC = (PC+1)%1000;
                         }
                     }
                 }
                 break;
             default: break;
         }
         if(legalInstruction){
            if(PgmNum==1) PC1 = PC;
            else PC2 = PC;
         }
        repaint();
    }
     public int getParam1Value(CodeBlueInstruction Inst){
        int loc;
        int tmp = -99999;
        if(Inst.type1 == CodeBlueInstruction.LITERAL){
            tmp = Inst.param1;
        } else if(Inst.type1 == CodeBlueInstruction.RELATIVE){
            loc = (PC+Inst.param1+1000)%1000;
            if(memory[loc].OpCode == CodeBlueInstruction.DATA)
                tmp = memory[loc].param1;
            } else if(Inst.type1 == CodeBlueInstruction.INDIRECT){
                loc = (PC + Inst.param1+100)%1000;
                if(memory[loc].OpCode == CodeBlueInstruction.DATA){
                    loc = (loc+memory[loc].param1+1000)%1000;
                    if(memory[loc].OpCode == CodeBlueInstruction.DATA){
                        tmp = memory[loc].param1;
                    }
                }
        }
        return tmp;
    }    
    public int getParam2Value(CodeBlueInstruction Inst){
        int loc;
        int tmp = -99999;
        if(Inst.type2 == CodeBlueInstruction.LITERAL){
            tmp = Inst.param2;
        } else if(Inst.type2 == CodeBlueInstruction.RELATIVE){
            loc = (PC+Inst.param2+1000)%1000;
            if(memory[loc].OpCode == CodeBlueInstruction.DATA)
                tmp = memory[loc].param1;
            } else if(Inst.type2 == CodeBlueInstruction.INDIRECT){
                loc = (PC + Inst.param2+1000)%1000;
                if(memory[loc].OpCode == CodeBlueInstruction.DATA){
                    loc = (loc+memory[loc].param1+1000)%1000;
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
              int loc = (PC + Inst.param1 + 1000)%1000; 
              tmp.copyInst(memory[loc]);
            } else{
              int loc = (PC + Inst.param1 + 1000)%1000;
              if(memory[loc].OpCode != CodeBlueInstruction.DATA){
                 tmp.OpCode = CodeBlueInstruction.ILLEGAL_OP;
              }
              else
                  tmp.copyInst(memory[(loc+memory[loc].param1+1000)%1000]);
            }
        return tmp;
    }
    public int getParam2Dest(CodeBlueInstruction Inst){
        int loc = -1;
        if(Inst.type2 == CodeBlueInstruction.RELATIVE){
            loc = (PC + Inst.param2 + 1000)%1000;
        } else if(Inst.type2 == CodeBlueInstruction.INDIRECT){
            int tmploc = (PC +Inst.param2 + 1000)%1000;
            if(memory[tmploc].OpCode == CodeBlueInstruction.DATA){
                loc = (tmploc+memory[tmploc].param1+1000)%1000;
            }
        }
        return loc;
    }
   public int getParam1Dest(CodeBlueInstruction Inst){
        int loc = -1;
        if(Inst.type1 == CodeBlueInstruction.RELATIVE){
            loc = (PC + Inst.param1 + 1000)%1000;
        } else if(Inst.type1 == CodeBlueInstruction.INDIRECT){
            int tmploc = (PC +Inst.param1 + 1000)%1000;
            if(memory[tmploc].OpCode == CodeBlueInstruction.DATA){
                loc = (tmploc+memory[tmploc].param1+1000)%1000;
            }
        }
        return loc;
    }
   
     public void paintComponent(Graphics canvas){
         int i;
        super.paintComponent(canvas);
        for(i=0;i<1000;i++){
             canvas.setColor(Color.BLACK);
             if(memory[i].OpCode == CodeBlueInstruction.DATA){
                if(Owner[i]==1) canvas.setColor(data1);
                else if(Owner[i]==2) canvas.setColor(data2);
            } else {
                if(Owner[i]==1) canvas.setColor(instruct1);
                else if(Owner[i]==2) canvas.setColor(instruct2);
            }
         canvas.fillRect(20*(i%25),10*(i/25),19,9);
         canvas.setColor(PC1Color);
         canvas.fillOval(20*(PC1%25)+5,10*(PC1/25)+2,10,5);
         canvas.setColor(PC2Color);
         canvas.fillOval(20*(PC2%25)+5,10*(PC2/25)+2,10,5);

                
        }
     }
}
