package agentupdates;

import java.util.ArrayList;
//import java.util.PriorityQueue;
import java.util.Stack;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.IOException;

public class BranchAndBoundSearch {

    // Define a Node class to store information about each node in the search tree
    private static class Node implements Comparable<Node> {
        KripkeStructure state;
        int cost;
        int level;
        String path; 
        Node par;
        KripkeAction action;

        public Node(KripkeStructure state, Node par, int cost, int level, String path, KripkeAction action) {
            this.state = state;
            this.par = par;
            this.cost = cost;
            this.level = level;
            this.path = path;
            this.action = action;
        }

        // Implement the compareTo method to allow Nodes to be compared in a PriorityQueue
        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.cost, other.cost);
        }
    }

    // Define a heuristic function to estimate the cost of reaching the goal state
    private static int heuristic(KripkeStructure state) {
        // TODO: Implement your heuristic function here
        return 0;
    }

    // Define the main search function
    public static void branchAndBoundSearch(KripkeStructure initialState, String testcase_dir) {
        Stack<Node> nodestack = new Stack<>();
        nodestack.push(new Node(initialState, null, 0, 0, "", null));

        /*
         * Stop exploring the space when the nodestack is empty
         */

        int minCost = Integer.MAX_VALUE;
        int maxLevel = 7;
        //String canEditLogs = "Y";
        String logs = "";

        boolean firstnode = true;
        while (!nodestack.isEmpty()) {
            Node currentNode = nodestack.pop();
            KripkeStructure currentState = currentNode.state;

            /* Stop exploring the current branch currentState is the goal state */

            if (goal_satisfied(currentState)) {
                // Print the solution and the cost, save the minCost and explore further
                if(minCost >= currentNode.cost)
                    {
                        minCost = Integer.min(minCost, currentNode.cost);
                        //logs = print_plan(currentNode,logs);
                        logs = print_plan(currentNode,"");
                    }
                continue ;
            }

            /*
             * Stop exploring the current branch when you have reached the maximum level
             */
            else if(currentNode.level == maxLevel){
                // explore the next successor to the parent
                continue;
            }

            else if(currentNode.cost > minCost){
                // stop exploring the current branch
                // explore the sibling
                continue;           
            }

            // Generate all possible successor states using actions in the sequence
            // For each successor compute the cost
            // create successor nodes and push onto the node stack

            else
            {
            
            for(int a = 0; a < Planner.sequence.size(); a++)
            {
                //has to explore all sucessors
                KripkeAction action = Planner.sequence.get(a);
                //System.out.println("checking: "+action.ActionName +" on "+ curr.ks.modelId);

                if(firstnode && !Planner.action_to_agent.get(action.ActionName).equals(Planner.self))
                    continue;
                
                if(currentNode.state.isApplicable(action))
                {
                    KripkeStructure next = currentNode.state.update(action);
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

                    Node child = new Node(next, currentNode, currentNode.cost + Planner.action_to_cost.get(action.ActionName), currentNode.level+1, "", action);
                    nodestack.add(child);
                }
            }
        }
            firstnode = false;
        }
        
        /* dump the plans and their costs*/
        Path filepath = Paths.get("./testcases/" + testcase_dir + "/resultsBB.txt");
 
        try {
            Files.writeString(filepath, logs, StandardCharsets.UTF_8);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    

    public static String print_plan(Node curr, String logs) { 
        ArrayList<String> plan = new ArrayList<>();
        //System.out.println("Number of models evaluated: " + curr.ks.modelId);
        logs = logs + "\t cost: " + curr.cost;
        
        while(curr.par != null)
        {
            plan.add(curr.action.ActionName);
            curr = curr.par;
        }  

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


