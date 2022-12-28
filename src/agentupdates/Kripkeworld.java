/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentupdates;

import static agentupdates.BuildProblem.buildFormulaIn;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author shikha
 */
public class Kripkeworld {
    
    // DO NOT TRY TO ACCESS STATELISTS AND EDGELISTS by using arraylist indexes as the respective ids...
    // the edges get shuffled after the update happens
    //Solution1: Either maintain a reverse hash from edges to ids and states to ids and vice versa DONE
    //Solution2: Insert the edges at indexes same as its edge ids 
    
    /**
     * each kripkeworld has:
     * id
     * associated interpretation
     * kripkeedge KEoutlist
     * kripkeedge KEinlist
     */
    public Integer kripkeworldId; 
    public Integer reverseId;
    public Set<String> literals;
    public HashMap<String, ArrayList<Integer>> agent_to_outlistMap;
    public HashMap<String, ArrayList<Integer>> agent_to_inlistMap;
    
    /**
     * getters and setters
     */
    public void setKwid(int i) {
        this.kripkeworldId = i;
    }
   
          
    
    /**
     * methods to check whether a formula holds true in this world
     */
    
    public boolean isEntailed(Formula formula) {   
        boolean isEnt = false;
        
        switch(formula.type)
        {
            case TRUE:
                isEnt = true;
            break;
            
            case LITERAL:
                /**
                 * input is a string
                 * 
                 */
                
                isEnt = isLitEntailed(formula.fluents.get(0));
                break;
                
            case AND:
                isEnt = isEntailed(formula.leftFormula) && isEntailed(formula.rightFormula);
                break;
                
            case OR:
                isEnt = isEntailed(formula.leftFormula) || isEntailed(formula.rightFormula);                
                break;
                
            case NOT:
                /**
                 * rightmost character '-'
                 */
                isEnt = !(isEntailed(formula.rightFormula));
                break;
                
            case B:

                ArrayList<String> agents = formula.leftFormula.fluents;
                
                for (String ag : agents)
                {
                    if(!this.agent_to_outlistMap.containsKey(ag))
                        continue;
                    ArrayList<Integer> to_world_edge_ids = this.agent_to_outlistMap.get(ag);
                    
                    /**
                     * to_world_edges contain the ids of the outgoing edges for ag from this world
                     * of the reverseId-th kripke model
                     * these edges have ids of the worlds they are going into
                     */
                    for (Integer e : to_world_edge_ids)
                    {
                        
                        //int to_world_edge_id = Planner.models.get(this.reverseId).kedgelist.get(e).toKripkeWorldId;
                        int to_world_id = 
                                KripkeStructure.pull_an_edge_by_id(Planner.models.get(this.reverseId).kedgelist, e).toKripkeWorldId;
                        
                        Kripkeworld to_world = 
                                KripkeStructure.pull_a_state_by_id(Planner.models.get(this.reverseId).statelist, to_world_id);
                        isEnt = to_world.isEntailed(formula.rightFormula);
                        
                        if(!isEnt)
                            break;
                    }
                    
                    if(!isEnt)
                        break;
                    //assume only one agent in the agentlist
                }

                
                
//                isEnt = !(isEntailed(formula.rightFormula));
                break;                

            case P:
    
                /**
                 * 
                 * enumerate all the worlds which are reachable from this world 
                 * for the corresponding agents in formula.leftformula
                 * and check whether formula.rightformula holds or not
                 * If yes return true, else return false
                 * 
                 */ 
                ArrayList<String> agentss = formula.leftFormula.fluents;
                
                for (String ag : agentss)
                {
                    if(!this.agent_to_outlistMap.containsKey(ag))
                        continue;
                    ArrayList<Integer> to_world_edge_ids = this.agent_to_outlistMap.get(ag);
                    
                    /**
                     * to_world_edges contain the ids of the outgoing edges for ag from this world
                     * of the reverseId-th kripke model
                     * these edges have ids of the worlds they are going into
                     */
                    for (Integer e : to_world_edge_ids)
                    {
                        
                        //int to_world_edge_id = Planner.models.get(this.reverseId).kedgelist.get(e).toKripkeWorldId;
                        int to_world_id = 
                                KripkeStructure.pull_an_edge_by_id(Planner.models.get(this.reverseId).kedgelist, e).toKripkeWorldId;
                        
                        Kripkeworld to_world = 
                                KripkeStructure.pull_a_state_by_id(Planner.models.get(this.reverseId).statelist, to_world_id);
                        isEnt = to_world.isEntailed(formula.rightFormula);
                        
                        if(isEnt)
                            break;
                        //if(!isEnt)
                        //    break;
                    }
                    
//                    if(!isEnt)
//                        break;
                    //assume only one agent in the agentlist
                }
                
                break;
            default:
                System.err.println("can't decide the type of the formula");
                break;
        
        }
        
        if(isEnt)
            return true;
        else
            return false;
    }
    
    public boolean isLitEntailed(String fluent){
        if (this.literals.contains(fluent))
            return true;
        else 
            return false;
    }
    
    
    
    public Kripkeworld deep_copy(){
        
        Kripkeworld destination = new Kripkeworld();
        
        destination.kripkeworldId = this.kripkeworldId;
        destination.reverseId = this.reverseId;
        
        Set<String> lit = new HashSet<>();
        lit.addAll(this.literals);
        destination.literals = lit;
        
        HashMap<String, ArrayList<Integer>> agent_to_outlistMap = new HashMap<>();
        agent_to_outlistMap = (HashMap<String, ArrayList<Integer>>) this.agent_to_outlistMap.clone();
        destination.agent_to_outlistMap = agent_to_outlistMap;
        
        HashMap<String, ArrayList<Integer>> agent_to_inlistMap = new HashMap<>();
        agent_to_inlistMap = (HashMap<String, ArrayList<Integer>>) this.agent_to_inlistMap.clone();
        destination.agent_to_inlistMap = agent_to_inlistMap;
        
        
        return destination;
    }
   

}
