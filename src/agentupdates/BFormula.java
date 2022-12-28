/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentupdates;

import java.util.ArrayList;

/**
 *
 * @author shikha
 */
public class BFormula extends Formula{
    
    public  BFormula(){
        this.num_operands = 2;
        this.leftFormula.type = form_type.AGENTNAME;
        //this.rightFormula.type  will be set dynamically
        //leftformula will have list of agents in the fluents section
    }
    
    
    /**
     * Formula f1 = new Formula();
     * 
     * Case f1.type : AND/OR
     * If one of them is a Belief formula then the other can be either atomic or belief formula 
     * Do not allow both to be atomic
     * 
     * Case f1.type : NOT
     * No need to have leftFormula
     * RightFormula can be either atomic or Belief
     * 
     * Case f1.type : B
     * Left formula will be a list [[]] of agents
     * Right formula will be a Belief formula
     * 
     * apply all the checks before
     * 
     */
}


