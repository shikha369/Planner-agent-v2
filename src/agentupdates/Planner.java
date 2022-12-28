/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package agentupdates;

import java.util.ArrayList;
import java.util.Scanner;

import javax.lang.model.util.ElementScanner14;
import javax.naming.ldap.ExtendedRequest;

/**
 *
 * @author shikha
 */
public class Planner {

    public static final ArrayList<KripkeStructure> models = new ArrayList<>();
    public static final ArrayList<String> fluentlist =new ArrayList<>();
    public static final ArrayList<String> agentlist =new ArrayList<>();
    public static final ArrayList<KripkeAction> sequence =new ArrayList<>();
    public static final ArrayList<KripkeAction> inferencing_actions =new ArrayList<>();
    public static Formula goal = new Formula();
    
    public static void main(String[] args)
    {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter testcase directory:");
        String testcase_dir = input.nextLine();
        BuildXML.build(testcase_dir);
        BuildProblem.build(testcase_dir);

        SearchNode curr = new SearchNode(null, null, Planner.models.get(0), null);
        KripkeStructure next;
        //ArrayList<SearchNode> closed = new ArrayList<>();
        ArrayList<SearchNode> open = new ArrayList<>();
        
        open.add(curr);
        
        double start = System.nanoTime();
        double end = System.nanoTime();
        double elapsedTime = (end - start)/1000000000;
        boolean breakfromsearch = false;
        boolean to_close = false;
        String result;
        // = input.nextLine();
        
        while (!open.isEmpty() && !breakfromsearch)
        //while (!open.isEmpty() && elapsedTime < 5 && !plan_found)
        //while (!open.isEmpty() && !plan_found)
        {
            curr = open.remove(0);
            if(goal_satisfied(curr.ks))
            {
                print_plan(curr);
                System.out.println("Do you want to continue: press Y || y for Yes");
                result = input.nextLine();
                if(result.equals("Y") || result.equals("y")){
                    start = System.nanoTime();
                   // continue;
                }
                else
                    {
                    breakfromsearch=true;
                    break;
                    }
                }
            
                for(int a = 0; a < Planner.sequence.size(); a++)
                {
                    //has to explore all sucessors
                    KripkeAction action = Planner.sequence.get(a);
                    System.out.println("checking: "+action.ActionName +" on "+ curr.ks.modelId);
                    
                    if(curr.ks.isApplicable(action))
                    {
                        next = curr.ks.update(action);
                        Planner.models.add(next);
                        //DEBUG
                        System.out.println(next.modelId +" = "+": "+action.ActionName +" on "+ curr.ks.modelId);

                        //execute inference cycle on next, store the applicable inferences in a list, add the final to the search node
                        ArrayList<String> inferences = new ArrayList<>();

                        for (int i = 0;  i < Planner.inferencing_actions.size(); i++){
                            KripkeAction iaction = Planner.inferencing_actions.get(i);
                            System.out.println("checking: "+iaction.ActionName +" on "+ next.modelId);

                            if(next.isApplicable(iaction)){
                                System.out.println("applying: "+iaction.ActionName +" on "+ next.modelId);
                                next = next.update(iaction);
                                Planner.models.add(next);
                                inferences.add(iaction.ActionName);
                            }
                        } 

                        SearchNode child = new SearchNode(curr, action, next, inferences);
                        
                        if(goal_satisfied(next)){
                        print_plan(child);
                        to_close = true;
                        System.out.println("Do you want to continue: press Y || y for Yes");
                        result = input.nextLine();
                        if(result.equals("Y") || result.equals("y")){
                            start = System.nanoTime();
                            //continue;
                        }
                        else
                        {
                            breakfromsearch=true;
                            break;
                        }

                        }

                
                    // while(!(child.ParentNode == null))
                    // {
                    // //first add inferences made in this search node and then the action ... following the reverse order
                    // // if(!child.listIactions.isEmpty()){
                    // //     for(String inf : inferences)
                    // //     act_plan.add(inf);
                    // // }
                    
                    // // act_plan.add(child.Action.ActionName);
                    // // child = child.ParentNode;
                    // }
                    if(breakfromsearch)
                        break;
                    else{
                        if(!to_close)
                            open.add(child);
                        else
                            to_close = false;
                        
                    }  
                }       
            }
            
            //closed.add(curr);
            end = System.nanoTime();
            elapsedTime = (end - start)/1000000000;
            
        }
        
        System.out.println("Total time: "+ elapsedTime);
    

        // if(!plan_found)
        //     System.out.println("timeout");
        // else
        //     System.out.println("printed"); 

    }

    
    public static void print_plan(SearchNode curr) { 
        ArrayList<String> plan = new ArrayList<>();
        System.out.println("Number of models evaluated: " + curr.ks.modelId);
        
        while(curr.ParentNode != null)
        {
            plan.add(curr.Action.ActionName);
            curr = curr.ParentNode;
        }  

        System.out.print("\nPlan found is : [");
        for(int i = plan.size()-1; i > -1; i--){
            System.out.print(plan.get(i));
            if(i !=0)
                System.out.print(", ");
        }
        System.out.println("]\n");  
    }
    
        public static boolean goal_satisfied(KripkeStructure model) {
            boolean satisfied = true;
            for (int i = 0; i < model.designated.size(); i++){
                if(!KripkeStructure.pull_a_state_by_id(model.statelist, model.designated.get(i)).isEntailed(Planner.goal))
                    return false;
            }
                return satisfied;       
        }   
}
    

    
    

