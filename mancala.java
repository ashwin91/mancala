/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mancala;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 *
 * @author AshwinKumar
 */
public class mancala {
BufferedWriter writer,logwriter ;
PrintWriter pw,lw ;
static int upperb[],lowerb[],task,player,cutoff,leftmancala,rightmancala,max,depth=-1;

    
    public void readInput(String fileName) throws IOException{
         
       
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(fileName);
            writer = new BufferedWriter(new FileWriter("next_state.txt"));
            pw = new PrintWriter(writer);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            task = Integer.parseInt(bufferedReader.readLine());
            player = Integer.parseInt(bufferedReader.readLine());
            cutoff = Integer.parseInt(bufferedReader.readLine());
            String upper = bufferedReader.readLine();
            String lower = bufferedReader.readLine();
            String upperS[]=upper.split(" ");
            String lowerS[]=lower.split(" ");
            upperb = new int[upperS.length];
            lowerb = new int[upperS.length];
            for(int i=0;i<upperS.length;i++){
                upperb[i] = Integer.parseInt(upperS[i]);
                lowerb[i] = Integer.parseInt(lowerS[i]);
            }
            leftmancala = Integer.parseInt(bufferedReader.readLine());
            rightmancala = Integer.parseInt(bufferedReader.readLine());
            BoardState bs = new BoardState();
            bs.lb = lowerb.clone();
            bs.ub = upperb.clone();
            bs.lm = leftmancala;
            bs.rm = rightmancala;
                    
            if(task ==1 ){
       
               BoardState bs1 =  dogreedy(bs);
               
               upper="";lower="";
               for(int i =0 ;i<bs1.lb.length;i++){
                   upper+=bs1.ub[i]+" ";
                   lower+=bs1.lb[i]+ " " ;
               }
               pw.println(upper.trim());
               pw.println(lower.trim());
               pw.println(bs1.lm);
               pw.println(bs1.rm);
            }
            else if(task==2){
                logwriter = new BufferedWriter(new FileWriter("traverse_log.txt"));
                lw = new PrintWriter(logwriter);
                lw.println("Node,Depth,Value");
                bs.name="root";
                BoardState bs1 =maxValue(bs, 0,1);
               upper="";lower="";
               for(int i =0 ;i<bs1.lb.length;i++){
                   upper+=bs1.ub[i]+" ";
                   lower+=bs1.lb[i]+ " " ;
               }
               pw.println(upper.trim());
               pw.println(lower.trim());
               pw.println(bs1.lm);
               pw.println(bs1.rm);
               
                lw.close();
                logwriter.close();
            }
            else if(task==3){
                logwriter = new BufferedWriter(new FileWriter("traverse_log.txt"));
                lw = new PrintWriter(logwriter);
                lw.println("Node,Depth,Value,Alpha,Beta");
                bs.name="root";
                BoardState bs1 =maxValueAB(bs, 0,1,Integer.MIN_VALUE,Integer.MAX_VALUE);
               upper="";lower="";
               for(int i =0 ;i<bs1.lb.length;i++){
                   upper+=bs1.ub[i]+" ";
                   lower+=bs1.lb[i]+ " " ;
               }
               pw.println(upper.trim());
               pw.println(lower.trim());
               pw.println(bs1.lm);
               pw.println(bs1.rm);
               
                lw.close();
                logwriter.close();
            
            }
           
            fileReader.close();
            pw.close();
            writer.close();
           
        }
        catch(Exception E){
            E.printStackTrace();
        }
    }

    BoardState dogreedy(BoardState obs){
        BoardState bs = new BoardState(obs);
        BoardState bestState,nextState=null;
        bestState = bs;
        
        for(int i=0;i<bs.lb.length;i++){
            if((bs.ub[i]!=0&&player==2)||(bs.lb[i]!=0&&player==1)){
                if(player==2){
                    nextState = doMovePlayer2(bs, i);
                    if(((bestState.lm-bestState.rm)<(nextState.lm-nextState.rm))||bestState==bs){
                        bestState = nextState;
                    }
                }
                else if(player==1){
                    nextState = doMove(bs, i);
                    if(((bestState.rm-bestState.lm)<(nextState.rm-nextState.lm))||bestState==bs){
                        bestState = nextState;
                    }
                }
            }
        }
     
        return bestState;
    }
    BoardState doEval(BoardState bs){
        int sumupper=0,sumlower=0;
        
        for(int i=0;i<bs.lb.length;i++){
            sumupper+=bs.ub[i];
            sumlower+=bs.lb[i];
        }
        if(sumlower==0||sumupper==0){
            bs.lm += sumupper;
            Arrays.fill(bs.ub, 0);
            bs.rm += sumlower;
            Arrays.fill(bs.lb, 0);
        }
        return bs;
    }
      boolean doEval1(BoardState bs){
        int sumupper=0,sumlower=0;
        
        for(int i=0;i<bs.lb.length;i++){
            sumupper+=bs.ub[i];
            sumlower+=bs.lb[i];
        }
        if(sumlower==0||sumupper==0){
            bs.lm += sumupper;
            Arrays.fill(bs.ub, 0);
            bs.rm += sumlower;
            Arrays.fill(bs.lb, 0);
            return true;
        }
        return false;
    }
    BoardState doMove(BoardState obs,int move){
        
        BoardState bs = new BoardState(obs);
        int traverse=move+1;
        int stone = bs.lb[move];
        bs.lb[move] = 0;
        while (stone!=0){
            stone--;
            if(stone==0&&traverse<bs.lb.length&&bs.lb[traverse]==0){
                bs.rm+=bs.ub[traverse]+1;
                bs.ub[traverse]=0;   
            }
            else if(stone==0&&(traverse==bs.lb.length)){
                bs.rm++;
                return(doEval(dogreedy(bs)));
            }
            else if(traverse<bs.lb.length){
                bs.lb[traverse]++;
            }
            else if(traverse==bs.lb.length){
                bs.rm++;
            }
            else if(traverse>bs.lb.length){
                int index = bs.lb.length-(traverse%(bs.lb.length+1))-1;
                bs.ub[index]++;
            }
            traverse = (traverse+1)%(2*bs.lb.length+1);
        }
        return doEval(bs);
     
    }
    
    BoardState doMovePlayer2(BoardState obs,int move){
        
        BoardState bs = new BoardState(obs);
        int traverseup=move-1;
        int traversedown = 0;
        int stone = bs.ub[move];
        bs.ub[move] = 0;
        while (stone!=0){
            stone--;
            if(stone==0&&traverseup>-1&&bs.ub[traverseup]==0){
               bs.lm= bs.lb[traverseup]+1;
               bs.lb[traverseup]=0;
            }
            else if(stone==0&&traversedown==bs.ub.length&&bs.ub[bs.ub.length-1]==0){
                traversedown=0;
                traverseup = bs.ub.length-1;
                bs.lm+=bs.lb[traverseup]+1;
                 bs.lb[traverseup]=0;
            }
            else if(traverseup > -1){
                bs.ub[traverseup] ++;
            }
            else if(traversedown==bs.ub.length){
                traversedown=0;
                traverseup = bs.ub.length-1;
                bs.ub[traverseup]++;
            }
            else if(traverseup<-1){
                bs.lb[traversedown]++;
                traversedown++;
            }
            
            else if(traverseup==-1&&stone==0){
                bs.lm++;
                return(doEval(dogreedy(bs)));
            }
            else if(traverseup==-1){
                bs.lm++;
            }
           traverseup --;
        }
        return doEval(bs);
    }
    BoardState doMoveMinMax(BoardState obs,int move,int depth){
        
        BoardState bs = new BoardState(obs);
        bs.depth = depth;
        int traverse=move+1;
        int stone = bs.lb[move];
        bs.lb[move] = 0;
        while (stone!=0){
            stone--;
            if(stone==0&&traverse<bs.lb.length&&bs.lb[traverse]==0){
                bs.rm+=bs.ub[traverse]+1;
                bs.ub[traverse]=0;   
            }
            else if(stone==0&&(traverse==bs.lb.length)){
                bs.rm++;
                if(player==1){
                    bs.parent = bs.name;
                    bs.name = "B"+(move+2);
                    BoardState playedState = maxValue(bs, depth-1,1);
                    playedState.moveplayed =true;
                    return doEval(playedState);
                }
                else{
                    bs.parent = bs.name;
                    bs.name = "B"+(move+2);
                    BoardState playedState = minValue(bs, depth-1,1);
                    playedState.moveplayed =true;
                    return doEval(playedState);
                    
                }
                //return(doEval(dogreedy(bs)));
            }
            else if(traverse<bs.lb.length){
                bs.lb[traverse]++;
            }
            else if(traverse==bs.lb.length){
                bs.rm++;
            }
            else if(traverse>bs.lb.length){
                int index = bs.lb.length-(traverse%(bs.lb.length+1))-1;
                bs.ub[index]++;
            }
            traverse = (traverse+1)%(2*bs.lb.length+1);
        }
        return doEval(bs);
        
     
    }
    
    BoardState doMovePlayer2MinMax(BoardState obs,int move,int depth){
        
        BoardState bs = new BoardState(obs);
        bs.depth =depth;
        int traverseup=move-1;
        int traversedown = 0;
        int stone = bs.ub[move];
        bs.ub[move] = 0;
        while (stone!=0){
            stone--;
            if(stone==0&&traverseup>-1&&bs.ub[traverseup]==0){
               bs.lm+= bs.lb[traverseup]+1;
               bs.lb[traverseup]=0;
            }
            else if(stone==0&&traversedown==bs.ub.length&&bs.ub[bs.ub.length-1]==0){
                traversedown=0;
                traverseup = bs.ub.length-1;
                bs.lm+=bs.lb[traverseup]+1;
                 bs.lb[traverseup]=0;
            }
            else if(traverseup > -1){
                bs.ub[traverseup] ++;
            }
            else if(traversedown==bs.ub.length){
                traversedown=0;
                traverseup = bs.ub.length-1;
                bs.ub[traverseup]++;
            }
            else if(traverseup<-1){
                bs.lb[traversedown]++;
                traversedown++;
            }
            
            else if(traverseup==-1&&stone==0){
                bs.lm++;
             
                if(player==2){
                    bs.parent = bs.name;
                    bs.name = "A"+(move+2);
                    BoardState playedState = maxValue(bs, depth-1,1);
                    playedState.moveplayed=true;
                    return doEval(playedState);
                    
                }
                else{
                    bs.parent = bs.name;
                    bs.name = "A"+(move+2);
                    BoardState playedState = minValue(bs, depth-1,1);
                    playedState.moveplayed=true;
                    return doEval(playedState);
                   
                }
                //return(doEval(dogreedy(bs)));
            }
            else if(traverseup==-1){
                bs.lm++;
            }
           traverseup --;
        }
       return doEval(bs);
    }
    
    
BoardState maxValue (BoardState bs,int depth,int state) {
    
    
    if((depth>=cutoff)||doEval1(bs)){
        
        if(depth<cutoff&&doEval1(bs)){
            //System.out.println(bs.name+","+(bs.depth)+","+"-Infinity");
            lw.println(bs.name+","+(bs.depth)+","+"-Infinity");
        }
        if(player==1)
        bs.utility = bs.rm-bs.lm;
        else
        bs.utility = bs.lm-bs.rm;    
        //System.out.println(bs.name+","+(bs.depth)+","+bs.utility);
        lw.println(bs.name+","+(bs.depth)+","+bs.utility);
        return bs;
    }
    
    //System.out.println(bs.name+","+(bs.depth)+","+"-Infinity");
    lw.println(bs.name+","+(bs.depth)+","+"-Infinity");
    BoardState bestState = new BoardState();
    bestState.utility = Integer.MIN_VALUE;
    BoardState maxState = new BoardState();
    for(int i =0 ;i< bs.ub.length;i++){
    
        if((bs.lb[i]!=0&&player==1)||(bs.ub[i]!=0&&player==2)){
        BoardState nextState;
        if(player==1)    
        nextState = doMoveMinMax(bs,i,depth+1);
        else
        nextState = doMovePlayer2MinMax(bs,i,depth+1);   
         if(!nextState.moveplayed){
        nextState.parent = bs.name;
        if(player==1)
        nextState.name = "B"+(2+i);
        else
        nextState.name = "A"+(2+i);    
            maxState = new BoardState(minValue(nextState,depth+1,0));
            if(bestState.utility<maxState.utility){
                bestState = new BoardState(maxState);
            }
         }
         else {
             if(bestState.utility<nextState.utility){
                bestState = new BoardState(nextState);
            }
         }
           //System.out.println(bs.name+","+bs.depth+","+bestState.utility);
           lw.println(bs.name+","+bs.depth+","+bestState.utility);
        
        } 
   }  
    //printState(bestState);
    if(state!=0)
          return bestState;
    bs.utility = bestState.utility;
    return bs;
    //return bestState;
}
BoardState minValue (BoardState bs,int depth,int state) {
   
    if((depth>=cutoff)||doEval1(bs)){
         if(depth<cutoff&&doEval1(bs)){
            //System.out.println(bs.name+","+(bs.depth)+","+"Infinity");
            lw.println(bs.name+","+(bs.depth)+","+"Infinity");
        }

        
        if(player==1)
        bs.utility = bs.rm-bs.lm;
        else
        bs.utility = bs.lm-bs.rm; 
        //System.out.println(bs.name+","+(bs.depth)+","+bs.utility);
        lw.println(bs.name+","+(bs.depth)+","+bs.utility);
        return bs;
    }
          
    //System.out.println(bs.name+","+(bs.depth)+","+"Infinity");
    lw.println(bs.name+","+(bs.depth)+","+"Infinity");
    BoardState bestState = new BoardState();
    bestState.utility=Integer.MAX_VALUE;
    BoardState maxState = new BoardState();
    for(int i =0 ;i< bs.ub.length;i++){
        if((bs.lb[i]!=0&&player==2)||(bs.ub[i]!=0&&player==1)){
        BoardState nextState;
        if(player==1)
        nextState = new BoardState(doMovePlayer2MinMax(bs,i,depth+1));
        else
        nextState = new BoardState(doMoveMinMax(bs,i,depth+1));
        if(!nextState.moveplayed){
        nextState.parent = bs.name;
        if(player==1)
        nextState.name = "A"+(i+2);
        else 
        nextState.name = "B"+(i+2);        
            maxState = new BoardState(maxValue(nextState,depth+1,0));
            
            if(bestState.utility>maxState.utility){
                bestState = new BoardState(maxState);
            }
        }   
        else {
             if(bestState.utility>nextState.utility){
                bestState = new BoardState(nextState);
            }
         }  
            //System.out.println(bs.name+","+bs.depth+","+bestState.utility);
            lw.println(bs.name+","+bs.depth+","+bestState.utility);
        } 
   }
    
    if(state!=0)
          return bestState;
    bs.utility = bestState.utility;
    return bs;
    //return bestState;
}

void printState(BoardState bestState){
       System.out.println("-------------------------------------------------");
    String ub="",lb="";
    for(int i=0;i<bestState.lb.length;i++){
       ub+= bestState.ub[i]+" ";
       lb+=bestState.lb[i]+" ";
    }
    System.out.println(ub);
    System.out.println(lb);
    System.out.println("Utility "+bestState.utility);
    System.out.println("-------------------------------------------------");
}  
    BoardState doMoveMinMaxAB(BoardState obs,int move,int depth,int alpha,int beta){
        
        BoardState bs = new BoardState(obs);
        bs.depth = depth;
        int traverse=move+1;
        int stone = bs.lb[move];
        bs.lb[move] = 0;
        while (stone!=0){
            stone--;
            if(stone==0&&traverse<bs.lb.length&&bs.lb[traverse]==0){
                bs.rm+=bs.ub[traverse]+1;
                bs.ub[traverse]=0;   
            }
            else if(stone==0&&(traverse==bs.lb.length)){
                bs.rm++;
                if(player==1){
                    bs.parent = bs.name;
                    bs.name = "B"+(move+2);
                    BoardState playedState = maxValueAB(bs, depth-1,1,alpha,beta);
                    playedState.moveplayed =true;
                    return doEval(playedState);
                }
                else{
                    bs.parent = bs.name;
                    bs.name = "B"+(move+2);
                    BoardState playedState = minValueAB(bs, depth-1,1,alpha,beta);
                    playedState.moveplayed =true;
                    return doEval(playedState);
                    
                }
                //return(doEval(dogreedy(bs)));
            }
            else if(traverse<bs.lb.length){
                bs.lb[traverse]++;
            }
            else if(traverse==bs.lb.length){
                bs.rm++;
            }
            else if(traverse>bs.lb.length){
                int index = bs.lb.length-(traverse%(bs.lb.length+1))-1;
                bs.ub[index]++;
            }
            traverse = (traverse+1)%(2*bs.lb.length+1);
        }
        return doEval(bs);
        
     
    }
    
    BoardState doMovePlayer2MinMaxAB(BoardState obs,int move,int depth,int alpha,int beta){
        
        BoardState bs = new BoardState(obs);
        bs.depth =depth;
        int traverseup=move-1;
        int traversedown = 0;
        int stone = bs.ub[move];
        bs.ub[move] = 0;
        while (stone!=0){
            stone--;
            if(stone==0&&traverseup>-1&&bs.ub[traverseup]==0){
               bs.lm+= bs.lb[traverseup]+1;
               bs.lb[traverseup]=0;
            }
            else if(stone==0&&traversedown==bs.ub.length&&bs.ub[bs.ub.length-1]==0){
                traversedown=0;
                traverseup = bs.ub.length-1;
                bs.lm+=bs.lb[traverseup]+1;
                 bs.lb[traverseup]=0;
            }
            else if(traverseup > -1){
                bs.ub[traverseup] ++;
            }
            else if(traversedown==bs.ub.length){
                traversedown=0;
                traverseup = bs.ub.length-1;
                bs.ub[traverseup]++;
            }
            else if(traverseup<-1){
                bs.lb[traversedown]++;
                traversedown++;
            }
            
            else if(traverseup==-1&&stone==0){
                bs.lm++;
             
                if(player==2){
                    bs.parent = bs.name;
                    bs.name = "A"+(move+2);
                    BoardState playedState = maxValueAB(bs, depth-1,1,alpha,beta);
                    playedState.moveplayed=true;
                    return doEval(playedState);
                    
                }
                else{
                    bs.parent = bs.name;
                    bs.name = "A"+(move+2);
                    BoardState playedState = minValueAB(bs, depth-1,1,alpha,beta);
                    playedState.moveplayed=true;
                    return doEval(playedState);
                   
                }
                //return(doEval(dogreedy(bs)));
            }
            else if(traverseup==-1){
                bs.lm++;
            }
           traverseup --;
        }
       return doEval(bs);
    }
    
  
BoardState maxValueAB (BoardState bs,int depth,int state,int alpha,int beta) {
    
    
    if((depth>=cutoff)||doEval1(bs)){
        
        if(depth<cutoff&&doEval1(bs)){
            //System.out.println(bs.name+","+(bs.depth)+","+"-Infinity"+","+ConvertStringAlphaBeta(alpha)+","+ConvertStringAlphaBeta(beta));
            lw.println(bs.name+","+(bs.depth)+","+"-Infinity"+","+ConvertStringAlphaBeta(alpha)+","+ConvertStringAlphaBeta(beta));
        }
        if(player==1)
        bs.utility = bs.rm-bs.lm;
        else
        bs.utility = bs.lm-bs.rm;    
        //System.out.println(bs.name+","+(bs.depth)+","+bs.utility+","+ConvertStringAlphaBeta(alpha)+","+ConvertStringAlphaBeta(beta));
        lw.println(bs.name+","+(bs.depth)+","+bs.utility+","+ConvertStringAlphaBeta(alpha)+","+ConvertStringAlphaBeta(beta));
        return bs;
    }
    
    //System.out.println(bs.name+","+(bs.depth)+","+"-Infinity"+","+ConvertStringAlphaBeta(alpha)+","+ConvertStringAlphaBeta(beta));
    lw.println(bs.name+","+(bs.depth)+","+"-Infinity"+","+ConvertStringAlphaBeta(alpha)+","+ConvertStringAlphaBeta(beta));
    BoardState bestState = new BoardState();
    bestState.utility = Integer.MIN_VALUE;
    BoardState maxState = new BoardState();
    for(int i =0 ;i< bs.ub.length;i++){
    
        if((bs.lb[i]!=0&&player==1)||(bs.ub[i]!=0&&player==2)){
        BoardState nextState;
        if(player==1)    
        nextState = doMoveMinMaxAB(bs,i,depth+1,alpha,beta);
        else
        nextState = doMovePlayer2MinMaxAB(bs,i,depth+1,alpha,beta);   
         if(!nextState.moveplayed){
        nextState.parent = bs.name;
        if(player==1)
        nextState.name = "B"+(2+i);
        else
        nextState.name = "A"+(2+i);    
            maxState = new BoardState(minValueAB(nextState,depth+1,0,alpha,beta));
            if(bestState.utility<maxState.utility){
                bestState = new BoardState(maxState);
                
            }
         }
         else {
             if(bestState.utility<nextState.utility){
                bestState = new BoardState(nextState);
            }
             
         }
            if(bestState.utility>=beta){
                 //System.out.println(bs.name+","+bs.depth+","+bestState.utility+","+ConvertStringAlphaBeta(alpha)+","+ConvertStringAlphaBeta(beta));
                lw.println(bs.name+","+bs.depth+","+bestState.utility+","+ConvertStringAlphaBeta(alpha)+","+ConvertStringAlphaBeta(beta));
          
                break;
            }
              
           alpha = Math.max(alpha,bestState.utility);
           //System.out.println(bs.name+","+bs.depth+","+bestState.utility+","+ConvertStringAlphaBeta(alpha)+","+ConvertStringAlphaBeta(beta));
           lw.println(bs.name+","+bs.depth+","+bestState.utility+","+ConvertStringAlphaBeta(alpha)+","+ConvertStringAlphaBeta(beta));
            
          
           
        } 
   }  
    //printState(bestState);
    if(state!=0)
          return bestState;
    bs.utility = bestState.utility;
    return bs;
    //return bestState;
}
BoardState minValueAB (BoardState bs,int depth,int state,int alpha,int beta) {
   
    if((depth>=cutoff)||doEval1(bs)){
         if(depth<cutoff&&doEval1(bs)){
            //System.out.println(bs.name+","+(bs.depth)+","+"Infinity"+","+ConvertStringAlphaBeta(alpha)+","+ConvertStringAlphaBeta(beta));
            lw.println(bs.name+","+(bs.depth)+","+"Infinity"+","+ConvertStringAlphaBeta(alpha)+","+ConvertStringAlphaBeta(beta));
        }

        
        if(player==1)
        bs.utility = bs.rm-bs.lm;
        else
        bs.utility = bs.lm-bs.rm; 
        //System.out.println(bs.name+","+(bs.depth)+","+bs.utility+","+ConvertStringAlphaBeta(alpha)+","+ConvertStringAlphaBeta(beta));
        lw.println(bs.name+","+(bs.depth)+","+bs.utility+","+ConvertStringAlphaBeta(alpha)+","+ConvertStringAlphaBeta(beta));
        return bs;
    }
          
    //System.out.println(bs.name+","+(bs.depth)+","+"Infinity"+","+ConvertStringAlphaBeta(alpha)+","+ConvertStringAlphaBeta(beta));
    lw.println(bs.name+","+(bs.depth)+","+"Infinity"+","+ConvertStringAlphaBeta(alpha)+","+ConvertStringAlphaBeta(beta));
    BoardState bestState = new BoardState();
    bestState.utility=Integer.MAX_VALUE;
    BoardState maxState = new BoardState();
    for(int i =0 ;i< bs.ub.length;i++){
        if((bs.lb[i]!=0&&player==2)||(bs.ub[i]!=0&&player==1)){
        BoardState nextState;
        if(player==1)
        nextState = new BoardState(doMovePlayer2MinMaxAB(bs,i,depth+1,alpha,beta));
        else
        nextState = new BoardState(doMoveMinMaxAB(bs,i,depth+1,alpha,beta));
        if(!nextState.moveplayed){
        nextState.parent = bs.name;
        if(player==1)
        nextState.name = "A"+(i+2);
        else 
        nextState.name = "B"+(i+2);        
            maxState = new BoardState(maxValueAB(nextState,depth+1,0,alpha,beta));
            
            if(bestState.utility>maxState.utility){
                bestState = new BoardState(maxState);
            }
        }   
        else {
             if(bestState.utility>nextState.utility){
                bestState = new BoardState(nextState);
            }
         }  
        
             if(bestState.utility<=alpha){
                 //System.out.println(bs.name+","+bs.depth+","+bestState.utility+","+ConvertStringAlphaBeta(alpha)+","+ConvertStringAlphaBeta(beta));
            lw.println(bs.name+","+bs.depth+","+bestState.utility+","+ConvertStringAlphaBeta(alpha)+","+ConvertStringAlphaBeta(beta));
                 break;
             }
           
            beta = Math.min(beta,bestState.utility);
            //System.out.println(bs.name+","+bs.depth+","+bestState.utility+","+ConvertStringAlphaBeta(alpha)+","+ConvertStringAlphaBeta(beta));
            lw.println(bs.name+","+bs.depth+","+bestState.utility+","+ConvertStringAlphaBeta(alpha)+","+ConvertStringAlphaBeta(beta));
             
           
           
        } 
   }
    
    

//    System.out.println("-------------------------------------------------");
//    String ub="",lb="";
//    for(int i=0;i<bestState.lb.length;i++){
//       ub+= bestState.ub[i]+" ";
//       lb+=bestState.lb[i]+" ";
//    }
//    System.out.println(ub);
//    System.out.println(lb);
//    System.out.println("Utility "+(bestState.rm-bestState.lm));
//    System.out.println("-------------------------------------------------");
    //printState(bestState);
    if(state!=0)
          return bestState;
    bs.utility = bestState.utility;
    return bs;
    //return bestState;
}
String ConvertStringAlphaBeta(int a){
    if(a==Integer.MIN_VALUE)
        return "-Infinity";
    else if(a==Integer.MAX_VALUE)
        return "Infinity";
    else
        return ""+a;
}
    public static void main(String[] args)throws IOException {
        // TODO code application logic here
        mancala m = new mancala();
        m.readInput(args[1]);
    }
    
    public class BoardState {
        int []ub;
        int []lb;
        int lm;
        int rm;
        int utility;
        int depth;
        String parent;
        String name;
        boolean moveplayed;
        public BoardState (BoardState bs){
            this.lb = bs.lb.clone();
            this.ub = bs.ub.clone();
            this.lm= bs.lm;
            this.rm = bs.rm;
            this.utility = bs.utility;
            this.parent = bs.parent;
            this.name = bs.name;
            this.depth = bs.depth;
            this.moveplayed = bs.moveplayed;
        }

        public BoardState() {
            lm=0;
            rm=0;
            depth=0;
            moveplayed=false;
        }
        
    }
    
}
