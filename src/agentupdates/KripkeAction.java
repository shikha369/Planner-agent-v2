/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentupdates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author shikha
 */
public class KripkeAction {

    
    public Integer ActionId;
    public String ActionName;
    public ArrayList<KripkeEvent> eventlist;
    public ArrayList<Edge> observelist;
    public ArrayList<Edge> sumEdgelist;
    public ArrayList<Edge> delEdgelist;
    //public Set agents;
    public ArrayList<Integer> designated;
    public Set<String> agents;
    

    public KripkeAction(Integer actionid, String Actionname, ArrayList<KripkeEvent> eventlist, 
            ArrayList<Edge> observelist, ArrayList<Edge> sumEdgelist, 
            ArrayList<Edge> delEdgelist, ArrayList<Integer> designated, Set<String> agents) {
        
        this.ActionId = actionid;
        this.ActionName = Actionname;
        this.eventlist = eventlist;
        this.observelist = observelist;
        this.sumEdgelist = sumEdgelist;
        this.delEdgelist = delEdgelist;
        this.designated = designated;
        this.agents = agents;
    }
    
    
    
    
}
