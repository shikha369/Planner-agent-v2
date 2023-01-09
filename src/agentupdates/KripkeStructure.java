/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentupdates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 *
 * @author shikha
 */
public class KripkeStructure {
    
    /**
     * id
     * statelist 
     * edgelist
     * agents
     * designated
     */
    
    public Integer modelId;
    public ArrayList<Kripkeworld> statelist;  //can be unordered
    public ArrayList<KEdge> kedgelist;        //can be unordered
    //public Set<String> agents;
    public ArrayList<Integer> designated; //unordered list of ids of kripkeworlds
    
    public KripkeStructure(){}

    public KripkeStructure(Integer modelId, ArrayList<Kripkeworld> statelist, ArrayList<KEdge> kedgelist, ArrayList<Integer> designated) {
        this.modelId = modelId;
        this.statelist = statelist; 
        this.kedgelist = kedgelist;
        //this.agents = agents;
        this.designated = designated;
    }
    
    
    
 /***   
    public boolean entails (Formula formula){
        boolean goal_condition_met = false;
        
        for( int d : this.designated ){
            //designated is the list of kripkeworldIds...kripkeworldIds may not align with the arraylist indexing
            //goal_condition_met = this.statelist.get(d).isEntailed(formula);
            goal_condition_met = UpdateStore.pull_a_state_by_id(this.statelist, d).isEntailed(formula);
            }
        return goal_condition_met;
    }
    ***/
    
    public KripkeStructure update(KripkeAction kaction)        
    {
        /***
         * 1. update is invoked only when action is applicable2
         * 2. Get the designated worlds from Kripke structure 
         * as well as Kripke action
         */
//        ArrayList<Kripkeworld> designated_worlds = new ArrayList<>();
//        for(int w : this.designated){
//            designated_worlds.add(pull_a_state_by_id(this.statelist, w));
//        }
//        
//        ArrayList<KripkeEvent> designated_events = new ArrayList<>();
//        for(int e : kaction.designated){
//            designated_events.add(pull_an_event_by_id(kaction.eventlist, e));
//        }       
        
        /***
         * 3. Sum-Product update
         */
        
        KripkeStructure updated_model = null;
               
        ArrayList<Kripkeworld> curr_worlds = this.statelist;
        ArrayList<KripkeEvent> curr_events = kaction.eventlist;

        ArrayList<Kripkeworld> new_statelist;
        ArrayList<KEdge> new_edgelist;
        ArrayList<Integer> new_des = new ArrayList<>();
        
        //Initialise identifiers
        //int numM = Planner.models.size();
        int numM = Planner.model_ctr++;
        int numW = 0;
        int numE = 0;
        
        ArrayList<Kripke_state_event_tuple> kse_tuples = new ArrayList<>();
        
        ArrayList<KEdge> temp_new_kedges = new ArrayList<>();


        for(int w_itr = 0; w_itr < curr_worlds.size(); w_itr++)
        {
            Kripkeworld curr_world = this.statelist.get(w_itr);
            
            for(int e_itr = 0; e_itr < curr_events.size(); e_itr++)
            {
                KripkeEvent curr_event = kaction.eventlist.get(e_itr);

                if(curr_world.isEntailed(this, curr_event.preconditions))
                {
                    /**
                     * 1- create a new state with updated valuation and add the w-id and e-id pair to twmp list
                     */
                    Kripkeworld k = new Kripkeworld();
                    k.kripkeworldId = numW;
                    k.reverseId = numM;
                    
                    /**
                     * Check if both kw and ke are designated then make k too designated
                     */
                    
                    if(this.designated.contains(curr_world.kripkeworldId) && 
                            kaction.designated.contains(curr_event.eventId))
                        new_des.add(numW);
                    
                    
                    Set<String> new_valuation = new HashSet<>();
                    new_valuation.addAll(curr_world.literals);
                    ArrayList<String> post =  new ArrayList<>();
                            post = curr_event.posteffects;
                    
                    for (String lit: post){
                        if(lit.charAt(0)=='-'){
                            new_valuation.remove(lit.substring(1, lit.length()));
                            new_valuation.add(lit);
                        }
                        else if (lit.equals("nil"))
                            continue;
                        else{
                            new_valuation.add(lit);
                            new_valuation.remove("-".concat(lit));
                        }
                            
                    }

                    k.literals = new_valuation;
                    
                    k.agent_to_outlistMap = new HashMap<>();
                    k.agent_to_inlistMap = new HashMap<>();
                    
                    
                    Kripke_state_event_tuple kse1 = new Kripke_state_event_tuple();
                    kse1.kworld = k;
                    kse1.sepair = new state_event_pair(curr_world.kripkeworldId, curr_event.eventId);
                    kse_tuples.add(kse1);
                    
                    numW++;
                    
                }
            }
        }
            
            for(int w1 = 0; w1 < kse_tuples.size(); w1++) /*from world*/
            {
                for(int w2 = 0; w2 < kse_tuples.size(); w2++)  /*to world*/
                {
                    state_event_pair p1 = kse_tuples.get(w1).sepair; //w1,e1
                    state_event_pair p2 = kse_tuples.get(w2).sepair; //w2,e2
                    
                    Kripkeworld s = pull_a_state_by_id(this.statelist, p1.state_id);
                    Kripkeworld t = pull_a_state_by_id(this.statelist, p2.state_id);
                    KripkeEvent u = pull_an_event_by_id(kaction.eventlist, p1.event_id);
                    KripkeEvent v = pull_an_event_by_id(kaction.eventlist, p2.event_id);
                    
                    for (String agent : kaction.agents)
                    {
                        //regardless of what s and t are
                        boolean sRat = check_sRat(agent, s,t);
                        
                        //s = t
                        boolean sISt = p1.state_id==p2.state_id;
                                
                        //exists u to v
                        boolean uOav = check_uOav(agent, kaction, u, v);
                         
                        //sum u to v
                        boolean uOsav = check_uOsav(agent, kaction, u, v);
                                
                        //del u to v
                        boolean uOdav = check_uOdav(agent, kaction, u, v);
                        
                        //u = v     (can arise in case of checking observers)   
                        boolean uISv = p1.event_id==p2.event_id;
                                
                        
                        Set<String> exist_u = new HashSet<>();
                        Set<String> add_u = new HashSet<>();
                        Set<String> del_u = new HashSet<>();
                        
                        exist_u = u.exist_agent_to_outEventsMap.keySet();
                        add_u = u.add_agent_to_outEventsMap.keySet();
                        del_u = u.del_agent_to_outEventsMap.keySet();
                        
                        //condition: exist but not delete
                        if(exist_u.contains(agent) && !del_u.contains(agent) && sRat && uOav){
                            
                        KEdge knew = new KEdge(numE, kse_tuples.get(w1).kworld.kripkeworldId, kse_tuples.get(w2).kworld.kripkeworldId, agent);
                                    temp_new_kedges.add(knew);

                        if(kse_tuples.get(w1).kworld.agent_to_outlistMap.containsKey(agent))
                            {
                                ArrayList<Integer> agent_out = kse_tuples.get(w1).kworld.agent_to_outlistMap.get(agent);
                                        agent_out.add(numE);
                                kse_tuples.get(w1).kworld.agent_to_outlistMap.put(agent, agent_out);
                                }
                        else
                            {
                                ArrayList<Integer> agent_out = new ArrayList<>();
                                        agent_out.add(numE);
                                kse_tuples.get(w1).kworld.agent_to_outlistMap.put(agent, agent_out);
                                        
                            }
                                    

                                    
                         if(kse_tuples.get(w2).kworld.agent_to_inlistMap.containsKey(agent))
                            {
                                ArrayList<Integer> agent_in = kse_tuples.get(w2).kworld.agent_to_inlistMap.get(agent);
                                        agent_in.add(numE);
                                kse_tuples.get(w2).kworld.agent_to_inlistMap.put(agent, agent_in);
                            }
                        else
                            {
                                ArrayList<Integer> agent_in = new ArrayList<>();
                                        agent_in.add(numE);
                                kse_tuples.get(w2).kworld.agent_to_inlistMap.put(agent, agent_in);
                            }
                                    
                        numE++;
                        }
                        
                        //condition: exist and delete but not the same delete
                        if(exist_u.contains(agent) && del_u.contains(agent) && sRat && uOav && !uOdav){
                            
                           // if(!u.del_agent_to_outEventsMap.get(agent).contains(v.eventId)){
                        KEdge knew = new KEdge(numE, kse_tuples.get(w1).kworld.kripkeworldId, kse_tuples.get(w2).kworld.kripkeworldId, agent);
                                    temp_new_kedges.add(knew);

                        if(kse_tuples.get(w1).kworld.agent_to_outlistMap.containsKey(agent))
                            {
                                ArrayList<Integer> agent_out = kse_tuples.get(w1).kworld.agent_to_outlistMap.get(agent);
                                        agent_out.add(numE);
                                kse_tuples.get(w1).kworld.agent_to_outlistMap.put(agent, agent_out);
                                }
                        else
                            {
                                ArrayList<Integer> agent_out = new ArrayList<>();
                                        agent_out.add(numE);
                                kse_tuples.get(w1).kworld.agent_to_outlistMap.put(agent, agent_out);
                                        
                            }
                                    

                                    
                         if(kse_tuples.get(w2).kworld.agent_to_inlistMap.containsKey(agent))
                            {
                                ArrayList<Integer> agent_in = kse_tuples.get(w2).kworld.agent_to_inlistMap.get(agent);
                                        agent_in.add(numE);
                                kse_tuples.get(w2).kworld.agent_to_inlistMap.put(agent, agent_in);
                            }
                        else
                            {
                                ArrayList<Integer> agent_in = new ArrayList<>();
                                        agent_in.add(numE);
                                kse_tuples.get(w2).kworld.agent_to_inlistMap.put(agent, agent_in);
                            }        
                        numE++;
                        }
                    //}
                        
                        //condition: add and not delete
                        if(add_u.contains(agent) && !del_u.contains(agent) && sISt && uOsav){
                        KEdge knew = new KEdge(numE, kse_tuples.get(w1).kworld.kripkeworldId, kse_tuples.get(w2).kworld.kripkeworldId, agent);
                                    temp_new_kedges.add(knew);

                        if(kse_tuples.get(w1).kworld.agent_to_outlistMap.containsKey(agent))
                            {
                                ArrayList<Integer> agent_out = kse_tuples.get(w1).kworld.agent_to_outlistMap.get(agent);
                                        agent_out.add(numE);
                                kse_tuples.get(w1).kworld.agent_to_outlistMap.put(agent, agent_out);
                                }
                        else
                            {
                                ArrayList<Integer> agent_out = new ArrayList<>();
                                        agent_out.add(numE);
                                kse_tuples.get(w1).kworld.agent_to_outlistMap.put(agent, agent_out);
                                        
                            }
                                    

                                    
                         if(kse_tuples.get(w2).kworld.agent_to_inlistMap.containsKey(agent))
                            {
                                ArrayList<Integer> agent_in = kse_tuples.get(w2).kworld.agent_to_inlistMap.get(agent);
                                        agent_in.add(numE);
                                kse_tuples.get(w2).kworld.agent_to_inlistMap.put(agent, agent_in);
                            }
                        else
                            {
                                ArrayList<Integer> agent_in = new ArrayList<>();
                                        agent_in.add(numE);
                                kse_tuples.get(w2).kworld.agent_to_inlistMap.put(agent, agent_in);
                            }
                                    
                        numE++;
                        
                        
                        //add arrows inherited from the observers
                        for (String obs : u.observers){
                            
                            if(check_sRat(obs,s,t) && uISv){
                                
                               KEdge knew_ob = new KEdge(numE, kse_tuples.get(w1).kworld.kripkeworldId, kse_tuples.get(w2).kworld.kripkeworldId, agent);
                                    temp_new_kedges.add(knew_ob);

                        if(kse_tuples.get(w1).kworld.agent_to_outlistMap.containsKey(agent))
                            {
                                ArrayList<Integer> agent_out = kse_tuples.get(w1).kworld.agent_to_outlistMap.get(agent);
                                        agent_out.add(numE);
                                kse_tuples.get(w1).kworld.agent_to_outlistMap.put(agent, agent_out);
                                }
                        else
                            {
                                ArrayList<Integer> agent_out = new ArrayList<>();
                                        agent_out.add(numE);
                                kse_tuples.get(w1).kworld.agent_to_outlistMap.put(agent, agent_out);
                                        
                            }
                                    

                                    
                         if(kse_tuples.get(w2).kworld.agent_to_inlistMap.containsKey(agent))
                            {
                                ArrayList<Integer> agent_in = kse_tuples.get(w2).kworld.agent_to_inlistMap.get(agent);
                                        agent_in.add(numE);
                                kse_tuples.get(w2).kworld.agent_to_inlistMap.put(agent, agent_in);
                            }
                        else
                            {
                                ArrayList<Integer> agent_in = new ArrayList<>();
                                        agent_in.add(numE);
                                kse_tuples.get(w2).kworld.agent_to_inlistMap.put(agent, agent_in);
                            }
                                    
                        numE++; 
                            }
                        }
                }
            }
        }
        
            }
            
        new_statelist = new ArrayList<>();
        for(int w = 0; w < kse_tuples.size(); w++)
        {
            new_statelist.add(kse_tuples.get(w).kworld);

        }

        updated_model = new KripkeStructure(numM, new_statelist , temp_new_kedges, new_des);
        return updated_model;
        }
            
            
public boolean check_sRat(String agent, Kripkeworld s, Kripkeworld t){
    boolean result = false;
    if(!s.agent_to_outlistMap.keySet().contains(agent))
        return result;
    ArrayList<Integer> outgoing_edge_ids_from_s = s.agent_to_outlistMap.get(agent);
    for (Integer eid : outgoing_edge_ids_from_s)
        if(pull_an_edge_by_id(this.kedgelist, eid).toKripkeWorldId.equals(t.kripkeworldId))
            result = true;
    return result;
}

public boolean check_uOav(String agent, KripkeAction kaction, KripkeEvent u, KripkeEvent v){
    boolean result = false;
    if(!u.exist_agent_to_outEventsMap.keySet().contains(agent))
        return result;
    ArrayList<Integer> outgoing_oedge_ids_from_u = u.exist_agent_to_outEventsMap.get(agent);
    for (Integer eid : outgoing_oedge_ids_from_u)
        if(pull_an_oedge_by_id(kaction.observelist, eid).toEventId.equals(v.eventId))
            result = true;
    return result;
}

public boolean check_uOsav(String agent, KripkeAction kaction, KripkeEvent u, KripkeEvent v){
    boolean result = false;
    if(!u.add_agent_to_outEventsMap.keySet().contains(agent))
        return result;
    ArrayList<Integer> outgoing_oedge_ids_from_u = u.add_agent_to_outEventsMap.get(agent);
    for (Integer eid : outgoing_oedge_ids_from_u)
        if(pull_an_oedge_by_id(kaction.sumEdgelist, eid).toEventId.equals(v.eventId))
            result = true;
    return result;
}

public boolean check_uOdav(String agent, KripkeAction kaction, KripkeEvent u, KripkeEvent v){
    boolean result = false;
    if(!u.del_agent_to_outEventsMap.keySet().contains(agent))
        return result;
    ArrayList<Integer> outgoing_oedge_ids_from_u = u.del_agent_to_outEventsMap.get(agent);
    for (Integer eid : outgoing_oedge_ids_from_u)
        if(pull_an_oedge_by_id(kaction.delEdgelist, eid).toEventId.equals(v.eventId))
            result = true;
    return result;
}
    

public boolean isApplicable(KripkeAction ka){
        ArrayList<Kripkeworld> designated_worlds = new ArrayList<>();
        for(int w:this.designated){
            designated_worlds.add(pull_a_state_by_id(this.statelist, w));
        }
        
        ArrayList<KripkeEvent> designated_events = new ArrayList<>();
        for(int e:ka.designated){
            designated_events.add(pull_an_event_by_id(ka.eventlist, e));
        }
        
        return checkIfApplicable(designated_worlds, designated_events);
    }

    
public boolean checkIfApplicable(ArrayList<Kripkeworld> designated_worlds, 
        ArrayList<KripkeEvent> designated_events){
        
        boolean isapplicable = false;
        
        for(int w_itr = 0; w_itr < designated_worlds.size(); w_itr++)
        {
            for(int e_itr = 0; e_itr < designated_events.size(); e_itr++)
            {
                if(designated_worlds.get(w_itr).isEntailed(this, designated_events.get(e_itr).preconditions))
                {
                  isapplicable = true;
                  break;
                }
            }
            if(isapplicable)
                break;
        }
        return isapplicable;
    }


    
    public void correctModelId(int retainId){
        this.modelId = retainId;
        for (Kripkeworld kw : this.statelist){
            kw.reverseId = modelId;
    }
    
}
    
    public boolean isEquivalentTo(KripkeStructure kmodel){
        boolean isEq = false;
        
        return isEq; 
    }
    
    public static KEdge pull_an_edge_by_id(ArrayList<KEdge> listedges, int edgeId) {
    for(KEdge e : listedges)
        if(e.edgeId == edgeId)
            return e;
    return null;
        
    }
    
    public static Kripkeworld pull_a_state_by_id(ArrayList<Kripkeworld> liststates, int stateId) {
        for(Kripkeworld s : liststates)
            if(s.kripkeworldId == stateId)
                return s;
        return null;
        
    }
    
    public static Edge pull_an_oedge_by_id(ArrayList<Edge> listedges, int edgeId) {
    for(Edge e : listedges)
        if(e.id == edgeId)
            return e;
    return null;
        
    }
    
    public static KripkeEvent pull_an_event_by_id(ArrayList<KripkeEvent> listevents, int eventId) {
        for(KripkeEvent e : listevents)
            if(e.eventId == eventId)
                return e;
        return null;
        
    }

    
}
    

