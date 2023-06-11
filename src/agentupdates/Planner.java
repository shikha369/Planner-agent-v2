/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package agentupdates;

import java.util.ArrayList;
import java.util.Scanner;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 *
 * @author shikha
 */
public class Planner {

    public static final ArrayList<KripkeStructure> models = new ArrayList<>();
    public static final ArrayList<String> fluentlist =new ArrayList<>();
    public static final ArrayList<String> agentlist =new ArrayList<>();
    public static final ArrayList<String> extended_agentlist =new ArrayList<>();
    public static final ArrayList<KripkeAction> sequence =new ArrayList<>();
    public static final ArrayList<KripkeAction> inferencing_actions =new ArrayList<>();
    public static String self = new String();
    public static Formula goal = new Formula();
    public static final HashMap<String, String> action_to_agent = new HashMap<>();
    public static final HashMap<String, Integer> action_to_cost = new HashMap<>();
    public static int model_ctr = 0;

    
    public static void main(String[] args)
    {
        /*Run the planner for a minute, Store all logs in a file and dump the logs in the end 
         * domain build time
         * plan found and time taken
        */
        Scanner input = new Scanner(System.in);
        System.out.println("Enter testcase directory:");
        String testcase_dir = input.nextLine();
        BuildXML.build(testcase_dir,0);
        BuildProblem.build(testcase_dir);
        System.out.println(sequence.size() + inferencing_actions.size());
        String logs = "Domain size:" + (sequence.size() + inferencing_actions.size())+ "\n";
        boolean firstnode = true;
        boolean plan_found = false;

        //System.out.println("cost of actions:");
        //for(int a = 0; a < Planner.sequence.size(); a++){
        //    String act_name = Planner.sequence.get(a).ActionName;
        //    System.out.println(action_to_cost.get(act_name));
        //}

         /*   * temp fix below      */

        /*
         * set bnb to true for cost based planning
         * 
         * 
         */
        boolean bnb = true;   
        if(bnb){
            //cost 25 gives flee_m
            System.out.println("working with the initial domain ");
            plan_found = BranchAndBoundSearch.branchAndBoundSearch(Planner.models.get(0), testcase_dir, 20);

            int i = 0;
            int maxOrderCombinations = 4;
            while(!plan_found && i < maxOrderCombinations)
            {
                System.out.println("expanding to domain " + (i+1));
                BuildXML.build(testcase_dir, ++i);

                Planner.models.clear();
                Planner.sequence.clear();
                Planner.inferencing_actions.clear();
                //new clears
                Planner.action_to_agent.clear();
                Planner.action_to_cost.clear();
                Planner.fluentlist.clear();
                Planner.agentlist.clear();
                
                BuildProblem.build(testcase_dir); //resets init too
                System.out.println(sequence.size() + inferencing_actions.size());
                logs = "Domain size:" + (sequence.size() + inferencing_actions.size())+ "\n";
                plan_found = BranchAndBoundSearch.branchAndBoundSearch(Planner.models.get(0), testcase_dir, 20);
            }

            //     /*OTHERWISE for a general script
            //      * New information that needs to be parsed for the changes
            //      * 1. extended agent list
            //      * 2. for each extended agent, each possible order, we maintain a list of init and actionlist
            //      * 3. current order and the corresponding domain in terms of init and actionlist
            //      * 4. Where do we parse it? maintain different files --- build domain again with init and actionlist.
            //      */

            // }
        }
        
        else
        {   
        SearchNode curr = new SearchNode(null, null, Planner.models.get(0), null, "[");
        //model_ctr++;
        KripkeStructure next;
        //ArrayList<SearchNode> closed = new ArrayList<>();
        ArrayList<SearchNode> open = new ArrayList<>();
        
        open.add(curr);
        
        double start = System.nanoTime();
        double end = System.nanoTime();
        //double elapsedTime = 0;
        double totalTime = System.nanoTime();
        //System.out.println((end-start)/1000000000);
        // = (end - start)/1000000000;
        boolean breakfromsearch = false;
        boolean to_close = false;
        String result;
        // = input.nextLine();

        result = "Y";
        
        while (!open.isEmpty() && !breakfromsearch && ((end-start)/1000000000) < 3)
        //while (!open.isEmpty() && elapsedTime < 5 && !plan_found)
        //while (!open.isEmpty() && !plan_found)
        {
            curr = open.remove(0);
            if(goal_satisfied(curr.ks))
            {
                logs = print_plan(curr,logs);
                end = System.nanoTime();
                //System.out.println(logs);
                //System.out.println("elapsed time: " + (end - start)/1000000000);
                logs = logs + "\t elapsed time:" + (end - start)/1000000000 + "\n";
                //System.out.println("Do you want to continue: press Y || y for Yes");
                //result = input.nextLine();
                if(result.equals("Y") || result.equals("y")){
                    //start = System.nanoTime();
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
                    //System.out.println("checking: "+action.ActionName +" on "+ curr.ks.modelId);

                    if(firstnode && !Planner.action_to_agent.get(action.ActionName).equals(Planner.self))
                        continue;
                    
                    if(curr.ks.isApplicable(action))
                    {
                        next = curr.ks.update(action);
                        //Planner.models.add(next);

                        //DEBUG
                        //System.out.println(next.modelId +" = "+": "+action.ActionName +" on "+ curr.ks.modelId);

                        //execute inference cycle on next, store the applicable inferences in a list, add the final to the search node
                        ArrayList<String> inferences = new ArrayList<>();

                        for (int i = 0;  i < Planner.inferencing_actions.size(); i++){
                            KripkeAction iaction = Planner.inferencing_actions.get(i);
                            //System.out.println("checking: "+iaction.ActionName +" on "+ next.modelId);

                            if(next.isApplicable(iaction)){
                                //System.out.println("applying: "+iaction.ActionName +" on "+ next.modelId);
                                next = next.update(iaction);
                                //Planner.models.add(next);

                                inferences.add(iaction.ActionName);
                            }
                        } 

                        SearchNode child = new SearchNode(curr, action, next, inferences, "");
                        
                        if(goal_satisfied(next)){
                        logs = print_plan(child,logs);
                        end = System.nanoTime();
                        //System.out.println(logs);
                        //System.out.println("elapsed time: " + (end - start)/1000000000);
                        logs = logs + "\t elapsed time:" + (end - start)/1000000000 + "\n";
                        
                        to_close = true;
                        //System.out.println("Do you want to continue: press Y || y for Yes");
                        //result = input.nextLine();
                        if(result.equals("Y") || result.equals("y")){
                            //start = System.nanoTime();
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
            firstnode = false;
            
            //closed.add(curr);
            end = System.nanoTime();
            //elapsedTime = (end - start)/1000000000;
            
        }
        //System.out.println(logs);
        Path filepath = Paths.get("./testcases/" + testcase_dir + "/results.txt");
 
        try {
            Files.writeString(filepath, logs, StandardCharsets.UTF_8);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //System.out.println("Total time: "+ elapsedTime);
    

        // if(!plan_found)
        //     System.out.println("timeout");
        // else
        //     System.out.println("printed"); 

    }
    }
    
    public static String print_plan(SearchNode curr, String logs) { 
        ArrayList<String> plan = new ArrayList<>();
        //System.out.println("Number of models evaluated: " + curr.ks.modelId);
        logs = logs + "\t Number of models evaluated: " + curr.ks.modelId;
        
        while(curr.ParentNode != null)
        {
            plan.add(curr.Action.ActionName);
            curr = curr.ParentNode;
        }  

        /*** 
        System.out.print("\nPlan found is : [");
        for(int i = plan.size()-1; i > -1; i--){
            System.out.print(plan.get(i));
            if(i !=0)
                System.out.print(", ");
        }
        System.out.println("]\n");  
        */

        logs = logs  +"\t Plan found is : [";
        for(int i = plan.size()-1; i > -1; i--){
            logs = logs + (plan.get(i));
            if(i !=0)
                logs = logs + ", ";
        }
        logs = logs + "]\n"; 

        return logs;
    }
    
        public static boolean goal_satisfied(KripkeStructure model) {
            boolean satisfied = true;
            for (int i = 0; i < model.designated.size(); i++){
                if(!KripkeStructure.pull_a_state_by_id(model.statelist, model.designated.get(i)).isEntailed(model, Planner.goal))
                    return false;
            }
                return satisfied;       
        }   
}
    

    
    

