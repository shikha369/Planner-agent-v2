/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentupdates;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author shikha
 */
public class KripkeEvent {
    /**
     * It has the following attributes:
     * 0- id
     * 1- a set of preconditions
     * 2- a set of literals whose truth value need to be changed
     * 3- 
     */
    public Integer eventId;
    public Formula preconditions;
    public ArrayList<String> posteffects;
    public HashMap<String, ArrayList<Integer>> exist_agent_to_outEventsMap;
    public HashMap<String, ArrayList<Integer>> exist_agent_to_inEventsMap; 
    public HashMap<String, ArrayList<Integer>> add_agent_to_outEventsMap;
    public HashMap<String, ArrayList<Integer>> del_agent_to_outEventsMap;
    public ArrayList<String> observers;
}
