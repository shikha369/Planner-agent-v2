/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentupdates;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 *
 * @author shikha
 */
public class Formula {
    
    enum form_type
    {
        AND, OR, NOT, P, B, ATOMIC, AGENTNAME, LITERAL, NIL, TRUE
    }
    
    //, C, P
    //note that when type is agentname, it can be string of agents stored in fluents
    //when the type is literal, it will be a single string
        
    //No need of atomic it seems
    
    public form_type type;
    public Integer num_operands;
    public Formula leftFormula;
    public Formula rightFormula;
    public ArrayList<String> fluents;
    
}