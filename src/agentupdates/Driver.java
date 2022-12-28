/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentupdates;

import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author shikha
 */
public class Driver {
    
    //public static final KripkeStructure initial_model = new KripkeStructure();
    /*
        initial_model = models[0]
    */
    public static final ArrayList<KripkeStructure> models = new ArrayList<>();
    public static final ArrayList<String> fluentlist =new ArrayList<>();
    public static final ArrayList<String> agentlist =new ArrayList<>();
    public static final ArrayList<KripkeAction> sequence =new ArrayList<>();
    public static Formula goal = new Formula();
    
    public static void main(String[] args)
    {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter testcase directory:");
        String testcase_dir = input.nextLine();
        BuildXML.build(testcase_dir);
        BuildProblem.build(testcase_dir);
        System.out.println("agentupdates.Driver.main()");
        System.out.println("Truth of the goal formula in the given model : "+ 
                KripkeStructure.pull_a_state_by_id(models.get(0).statelist, models.get(0).designated.get(0)).isEntailed(goal));
        
        boolean next = true;
        KripkeStructure updated_model = new KripkeStructure();
                
        for(int i = 0; i < sequence.size(); i++)
        {
            if(models.get(i).isApplicable(sequence.get(i))){
                updated_model = models.get(i).update(sequence.get(i));
                models.add(updated_model);
            }
            else{
                next = false;
                System.out.println("action" + (i-1) + "is not applicable. Breaking...");
                break;
            }     
        }
        if(next){
                System.out.println("Truth of the goal formula in the updated model : "+
                        KripkeStructure.pull_a_state_by_id(updated_model.statelist,updated_model.designated.get(0)).isEntailed(goal));
                    }

    }
    
}
