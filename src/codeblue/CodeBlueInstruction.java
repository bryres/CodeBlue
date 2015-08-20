package codeblue;

/*
 * CodeBlueInstruction.java
 *
 * Created on August 2, 2007, 12:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author Dennis.Schweitzer
 */
public class CodeBlueInstruction {
    String Label;
    int OpCode;
    int numParams;
    int param1, param2;
    int type1, type2;
    String param1Label, param2Label;
    String value;
    boolean ignore;
    
    public static int DATA = 0;    
    public static int COPY = 1;    
    public static int COMPARE = 2;    
    public static int JUMP = 3;    
    public static int JUMPZ = 4;    
    public static int JUMPNZ = 5;    
    public static int ADD = 6;    
    public static int SUBTRACT = 7;  
    public static int ILLEGAL_OP = -1;
    public static int LITERAL = 0;    
    public static int RELATIVE = 1;    
    public static int INDIRECT = 2;    
    private int Spos;
    /** Creates a new instance of CodeBlueInstruction */
    public CodeBlueInstruction() {
        Label = "";
        OpCode = DATA;
        numParams = 1;
        param1 = 0;
        param2 = 0;
        type1 = LITERAL;
        type2 = LITERAL;
        param1Label = "";
        param2Label = "";
        ignore = false;
        value = "000000000";
    }
    public CodeBlueInstruction(String value){
        Label = "";
        OpCode = DATA;
        numParams = 1;
        param1 = 0;
        param2 = 0;
        type1 = LITERAL;
        type2 = LITERAL;
        param1Label = "";
        param2Label = "";
        ignore = false;       
        if(value != null && value.length()>0){
            if(value.charAt(0)=='0') param1 = Integer.parseInt(value.substring(1));
            else if(value.charAt(0)=='1') param1 = -Integer.parseInt(value.substring(1));
            else {
            if(value.charAt(0)=='2') OpCode = COPY;
            else if(value.charAt(0)=='3') OpCode = ADD;
            else if(value.charAt(0)=='4') OpCode = JUMP;
            else if(value.charAt(0)=='5') OpCode = JUMPZ;
            if(value.length()>4) {
                if(value.charAt(1)=='1') {
                    type1 = LITERAL;
                    param1 = Integer.parseInt(value.substring(2,5));
                }
                else if(value.charAt(1)=='2'){
                    type1 = LITERAL;
                    param1 = -Integer.parseInt(value.substring(2,5));
                }
                else if(value.charAt(1)=='3'){
                    type1 = RELATIVE;
                    param1 = Integer.parseInt(value.substring(2,5));
                }
                else if(value.charAt(1)=='4'){
                    type1 = RELATIVE;
                    param1 = -Integer.parseInt(value.substring(2,5));
                }
                else if(value.charAt(1)=='5'){
                    type1 = INDIRECT;
                    param1 = Integer.parseInt(value.substring(2,5));
                }
                else if(value.charAt(1)=='6'){
                    type1 = INDIRECT;
                    param1 = -Integer.parseInt(value.substring(2,5));
                }
            }
            if(value.length()>8) {
                if(value.charAt(5)=='1') {
                    type2 = LITERAL;
                    param2 = Integer.parseInt(value.substring(6,9));
                }
                else if(value.charAt(5)=='2'){
                    type2 = LITERAL;
                    param2 = -Integer.parseInt(value.substring(6,9));
                }
                else if(value.charAt(5)=='3'){
                    type2 = RELATIVE;
                    param2 = Integer.parseInt(value.substring(6,9));
                }
                else if(value.charAt(5)=='4'){
                    type2 = RELATIVE;
                    param2 = -Integer.parseInt(value.substring(6,9));
                }
                else if(value.charAt(5)=='5'){
                    type2 = INDIRECT;
                    param2 = Integer.parseInt(value.substring(6,9));
                }
                else if(value.charAt(5)=='6'){
                    type2 = INDIRECT;
                    param2 = -Integer.parseInt(value.substring(6,9));
                }
            }
            }
        }
    }
    public void copyInst(CodeBlueInstruction c1){
        Label = c1.Label;
        OpCode = c1.OpCode;
        numParams = c1.numParams;
        param1 = c1.param1;
        param2 = c1.param2;
        type1 = c1.type1;
        type2 = c1.type2;
        param1Label = c1.param1Label;
        param2Label = c1.param2Label;
        ignore = c1.ignore;
        value = c1.value;
     }
    public void makeValue(){
       value = "000000000";
       if(OpCode == DATA){
           if(param1>=0){
               value = "0"+leftZeroFill(Integer.toString(param1),8);
           }
           else value = "1"+leftZeroFill(Integer.toString(Math.abs(param1)),8);
       }
       else {
           if(OpCode == COPY) 
               value = "2";
           else if(OpCode == ADD) value = "3";
           else if(OpCode == JUMP) value = "4";
           else if(OpCode == JUMPZ) value = "5";
           if(type1==LITERAL && param1 >=0) value = value + "1";
           else if(type1==LITERAL && param1 <0) value = value + "2";
           else if(type1==RELATIVE && param1 >=0) value = value + "3";
           else if(type1==RELATIVE && param1 <0) value = value + "4";
           else if(type1==INDIRECT && param1 >=0) value = value + "5";
           else if(type1==INDIRECT && param1 <0) value = value + "6";
           else value = value + "0";
           value = value + leftZeroFill(Integer.toString(Math.abs(param1)),3);
           if(type2==LITERAL && param2 >=0) value = value + "1";
           else if(type2==LITERAL && param2 <0) value = value + "2";
           else if(type2==RELATIVE && param2 >=0) value = value + "3";
           else if(type2==RELATIVE && param2 <0) value = value + "4";
           else if(type2==INDIRECT && param2 >=0) value = value + "5";
           else if(type2==INDIRECT && param2 <0) value = value + "6";
           else value = value + "0";
           value = value + leftZeroFill(Integer.toString(Math.abs(param2)),3);
        }
    }
    private String leftZeroFill(String inS,int lngth){
        String rtnS = inS;
        for(int i=0;i<lngth-inS.length();i++)
            rtnS = '0'+rtnS;
        return rtnS;
    }
    
    public void ParseLine(String inLine){
      Spos = 0;
      String Token = GetToken(inLine);
      if(Token == "") { //blank line or comment
          ignore = true;
      }else {
          ignore = false;
          if((Spos <inLine.length())&&(inLine.charAt(Spos-1)==':')) { //process label first
              Label = Token;
              Token = GetToken(inLine); //get next token
          }
          if(Token.equalsIgnoreCase("DATA")) {
              OpCode = DATA;
              numParams = 1;
          }
          else if(Token.equalsIgnoreCase("COPY")) {
              OpCode = COPY;
              numParams = 2;
          }
          else if(Token.equalsIgnoreCase("COMPARE")){
              OpCode = COMPARE;
              numParams = 2;
          }
          else if(Token.equalsIgnoreCase("JUMP")) {
              OpCode = JUMP;
              numParams = 1;
          }
          else if(Token.equalsIgnoreCase("JUMPZ")){
              OpCode = JUMPZ;
              numParams = 2;
          }
          else if(Token.equalsIgnoreCase("JUMPNZ")) {
              OpCode = JUMPNZ;
              numParams = 2;
          }
          else if(Token.equalsIgnoreCase("ADD")) {
              OpCode = ADD;
              numParams = 2;
          }
          else if(Token.equalsIgnoreCase("SUBTRACT")) {
              OpCode = SUBTRACT;
              numParams = 2;
          } 
          else OpCode = ILLEGAL_OP;
          if(OpCode != ILLEGAL_OP){ 
             Token = GetToken(inLine);  // get first parameter
             if(Token.length()>0) { // got one
                 //first check if literal
                 if(OpCode == DATA){
                     type1 = LITERAL;
                     param1 = Integer.parseInt(Token);
                 } else
                 if(Token.charAt(0)=='#'){
                     type1 = LITERAL;
                     param1 = Integer.parseInt(Token.substring(1));
                 } else
                 //next check if indirect
                 if(Token.charAt(0)=='@'){
                     type1 = INDIRECT;
                     if((Token.charAt(1)>='0')&&(Token.charAt(1)<='9')) { //assume number
                         param1 = Integer.parseInt(Token.substring(1));
                     } else //must be label
                         param1Label = Token.substring(1);
                 } else{
                     type1 = RELATIVE;
                     if((Token.charAt(0)=='-')||((Token.charAt(0)>='0')&&(Token.charAt(0)<='9'))) { //assume number
                         param1 = Integer.parseInt(Token);
                     } else //must be label
                         param1Label = Token;
                 }
             }
             if(numParams == 2){
             Token = GetToken(inLine);  // get second parameter
             if(Token.length()>0) { // got one
                 //first check if literal
                 if(Token.charAt(0)=='#'){
                     type2 = LITERAL;
                     param2 = Integer.parseInt(Token.substring(1));
                 }
                 //next check if indirect
                 if(Token.charAt(0)=='@'){
                     type2 = INDIRECT;
                     if((Token.charAt(1)>='0')&&(Token.charAt(1)<='9')) { //assume number
                         param2 = Integer.parseInt(Token.substring(1));
                     } else //must be label
                         param2Label = Token.substring(1);
                 } else{
                     type2 = RELATIVE;
                     if((Token.charAt(0)>='0')&&(Token.charAt(0)<='9')) { //assume number
                         param2 = Integer.parseInt(Token);
                     } else //must be label
                         param2Label = Token;
                 }
             }
             }
          }
      }
    }
    
    public String GetToken(String s){
        String rtnS = "";
        Spos--;
        //skip blanks/tabs
        do{
            Spos++;
        } while ((s.length()>Spos) &&
                ((s.charAt(Spos) == ' ') ||(s.charAt(Spos) == '\t')));
        if((Spos < s.length())&&(s.charAt(Spos)==';')) {
            //comment, ignore rest of line
            Spos = s.length();
        }
        if(Spos <s.length()) { //collect characters for token until delimeter                
            while ((s.length()>Spos) &&
                    (s.charAt(Spos) != ':')&&
                    (s.charAt(Spos) != ';')&&
                    (s.charAt(Spos) != ' ')&&
                    (s.charAt(Spos) != ',')&&
                    (s.charAt(Spos) != '\r')&&
                    (s.charAt(Spos) != '\t')){
                rtnS = rtnS + s.charAt(Spos);
                Spos++;
            }
            Spos++;
        }
        return rtnS;
    }
}
