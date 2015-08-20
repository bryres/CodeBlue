package codeblue;

/*
 * ExecuteState.java
 *
 * Created on August 8, 2007, 4:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author Dennis.Schweitzer
 */
public class ExecuteState {
    
    public int PrevPC;
    public int ChangedLoc;
    public CodeBlueInstruction OldInst;
    
    /** Creates a new instance of ExecuteState */
    public ExecuteState() {
        PrevPC = 0;
        ChangedLoc = -1;
        OldInst = new CodeBlueInstruction();
    }
    
}
