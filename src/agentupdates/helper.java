package agentupdates;

import java.util.ArrayList;
import java.util.HashMap;
//import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class helper {

    

    /**
     * 
     * @param agents
     * @return
     * test sample
     */

    static ArrayList<String> agents = new ArrayList<>(); 
    static HashMap<String, Integer> arrivesCost = new HashMap<>();
    static HashMap<String, Integer> fleeCost = new HashMap<>();
    static HashMap<String, Integer> leavesCost = new HashMap<>();
    static HashMap<String, Integer> eatsCost = new HashMap<>();

    

public static String arrivesInit(ArrayList<String> agents)
    {
        String res = "";

        /*
         * try for one agent "m" and observers "f,o"
         * [actor]_arrives__[{obs}]
         * EVENTS = {
         * 1 = -at_[actor] AND POSSIBLE [actor] (TRUE) AND inloop at_{o} AND POSSIBLE [ob] (TRUE)
         * 2 =
         * }
         */

        for(String actor : agents) { 

        //first consider a completely public action - it will have only one event
        ArrayList<String> obs = new ArrayList<>();
        obs.addAll(agents);
        obs.remove(actor);
        String ob_set_name = "_".join("_", obs);

        System.out.println(actor);
        System.out.println(obs);
        System.out.println(ob_set_name);
         

        res += "CAPABILITY = (( "+actor+"_arrives__"+ob_set_name+" = { \nEVENTS = { \n1 = ( PRE = -at_"+actor+" AND POSSIBLE ["+actor+"] (TRUE) "; 

        //for each ob
            for(String ob : obs)
            {
                res +=   " AND at_"+ob+" AND POSSIBLE ["+ob+"] (TRUE)";
            }
            res += ", POST = {at_"+actor+"}) \n }, \n";

        // edges for actor
            res += "EDGES = { \n("+actor+",{-(1,1)})";
        // edges for each ob
        for(String ob : obs)
        {        
            res += ",\n("+ob+",{-(1,1)})";
        }
        res += "\n},\nDES = {1}}), \n"+ actor+", c"+arrivesCost.get(actor)+"),";
 


        //generate observer set name it: ob1_ob2 but keep a corresponding set
    
         int n = obs.size();
    
        for (int i = 0; i < n; i++) {
                for (int j = i + 1; j <= n; j++) {
                    ArrayList<String> subObs = new ArrayList<>(obs.subList(i, j));
                    ArrayList<String> obliv = new ArrayList<>(obs);
                    obliv.removeAll(subObs);
                    ob_set_name = "_".join("_", subObs);
                    System.out.println(subObs);
                    System.out.println(ob_set_name);

                    //starts here
                    res += "\nCAPABILITY = (( "+actor+"_arrives__"+ob_set_name+" = { \nEVENTS = { \n1 = ( PRE = -at_"+actor+" AND POSSIBLE ["+actor+"] (TRUE) "; 

                    //for each ob
                        for(String ob : subObs)
                        {
                            res +=   " AND at_"+ob+" AND POSSIBLE ["+ob+"] (TRUE)";
                        }
                        res += ", POST = {at_"+actor+"}), \n2 = ( PRE = (TRUE) , POST = {nil} ) \n}, \n";
            
                    // edges for actor
                        res += "EDGES = { \n("+actor+",{-(1,1),-(2,2)})";
                    // edges for each ob
                    for(String ob : subObs)
                    {        
                        res += ",\n("+ob+",{-(1,1),-(2,2)})";
                    }
                    for(String obl : obliv)
                    {        
                        res += ",\n("+obl+",{-(1,2),-(2,2)})";
                    }
                    res += "\n},\nDES = {1}}), \n"+ actor+", c"+arrivesCost.get(actor)+"),";    

                    //ends here
                }
        }
        // all oblivious
        res += "\nCAPABILITY = (( "+actor+"_arrives__ = { \nEVENTS = { \n1 = ( PRE = -at_"+actor+" AND POSSIBLE ["+actor+"] (TRUE) "; 

        ArrayList<String> subObs = new ArrayList<>();
        ArrayList<String> obliv = new ArrayList<>(obs);
        obliv.removeAll(subObs);

        //for each ob
            for(String ob : subObs)
            {
                res +=   " AND at_"+ob+" AND POSSIBLE ["+ob+"] (TRUE)";
            }
            res += ", POST = {at_"+actor+"}), \n2 = ( PRE = (TRUE) , POST = {nil} ) \n}, \n";

        // edges for actor
            res += "EDGES = { \n("+actor+",{-(1,1),-(2,2)})";
        // edges for each ob
        for(String ob : subObs)
        {        
            res += ",\n("+ob+",{-(1,1),-(2,2)})";
        }
        for(String obl : obliv)
        {        
            res += ",\n("+obl+",{-(1,2),-(2,2)})";
        }
        res += "\n},\nDES = {1}}), \n"+ actor+", c"+arrivesCost.get(actor)+"),"; 

}
return res;
}


public static String fleeInit(ArrayList<String> agents)
{
    String res = "";

    /*
     * try for one agent "m" and observers "f,o"
     * [actor]_arrives__[{obs}]
     * EVENTS = {
     * 1 = -at_[actor] AND POSSIBLE [actor] (TRUE) AND inloop at_{o} AND POSSIBLE [ob] (TRUE)
     * 2 =
     * }
     */

    for(String actor : agents) { 

    //first consider a completely public action - it will have only one event
    ArrayList<String> obs = new ArrayList<>();
    obs.addAll(agents);
    obs.remove(actor);
    String ob_set_name = "_".join("_", obs);

    System.out.println(actor);
    System.out.println(obs);
    System.out.println(ob_set_name);
     

    res += "CAPABILITY = (( "+actor+"_flee__"+ob_set_name+" = { \nEVENTS = { \n1 = ( PRE = (BELIEVES ["+actor+"] (th_"+actor+")) AND at_"+actor+" AND POSSIBLE ["+actor+"] (TRUE) "; 

    //for each ob
        for(String ob : obs)
        {
            res +=   " AND at_"+ob+" AND POSSIBLE ["+ob+"] (TRUE)";
        }
        res += ", POST = {-at_"+actor+"}) \n }, \n";

    // edges for actor
        res += "EDGES = { \n("+actor+",{-(1,1)})";
    // edges for each ob
    for(String ob : obs)
    {        
        res += ",\n("+ob+",{-(1,1)})";
    }
    res += "\n},\nDES = {1}}), \n"+ actor+", c"+fleeCost.get(actor)+"),";



    //generate observer set name it: ob1_ob2 but keep a corresponding set

     int n = obs.size();

    for (int i = 0; i < n; i++) {
            for (int j = i + 1; j <= n; j++) {
                ArrayList<String> subObs = new ArrayList<>(obs.subList(i, j));
                ArrayList<String> obliv = new ArrayList<>(obs);
                obliv.removeAll(subObs);
                ob_set_name = "_".join("_", subObs);
                System.out.println(subObs);
                System.out.println(ob_set_name);

                //starts here
                res += "\nCAPABILITY = (( "+actor+"_flee__"+ob_set_name+" = { \nEVENTS = { \n1 = ( PRE = (BELIEVES ["+actor+"] (th_"+actor+")) AND at_"+actor+" AND POSSIBLE ["+actor+"] (TRUE) "; 

                //for each ob
                    for(String ob : subObs)
                    {
                        res +=   " AND at_"+ob+" AND POSSIBLE ["+ob+"] (TRUE)";
                    }
                    res += ", POST = {-at_"+actor+"}), \n2 = ( PRE = (TRUE) , POST = {nil} ) \n}, \n";
        
                // edges for actor
                    res += "EDGES = { \n("+actor+",{-(1,1),-(2,2)})";
                // edges for each ob
                for(String ob : subObs)
                {        
                    res += ",\n("+ob+",{-(1,1),-(2,2)})";
                }
                for(String obl : obliv)
                {        
                    res += ",\n("+obl+",{-(1,2),-(2,2)})";
                }
                res += "\n},\nDES = {1}}), \n"+ actor+", c"+fleeCost.get(actor)+"),";    

                //ends here
            }
    }
    // all oblivious
    res += "\nCAPABILITY = (( "+actor+"_flee__ = { \nEVENTS = { \n1 = ( PRE = (BELIEVES ["+actor+"] (th_"+actor+")) AND at_"+actor+" AND POSSIBLE ["+actor+"] (TRUE) "; 

    ArrayList<String> subObs = new ArrayList<>();
    ArrayList<String> obliv = new ArrayList<>(obs);
    obliv.removeAll(subObs);

    //for each ob
        for(String ob : subObs)
        {
            res +=   " AND at_"+ob+" AND POSSIBLE ["+ob+"] (TRUE)";
        }
        res += ", POST = {-at_"+actor+"}), \n2 = ( PRE = (TRUE) , POST = {nil} ) \n}, \n";

    // edges for actor
        res += "EDGES = { \n("+actor+",{-(1,1),-(2,2)})";
    // edges for each ob
    for(String ob : subObs)
    {        
        res += ",\n("+ob+",{-(1,1),-(2,2)})";
    }
    for(String obl : obliv)
    {        
        res += ",\n("+obl+",{-(1,2),-(2,2)})";
    }
    res += "\n},\nDES = {1}}), \n"+ actor+", c"+fleeCost.get(actor)+"),"; 

}
return res;
}


public static String leavesInit(ArrayList<String> agents)
{
    String res = "";

    for(String actor : agents) { 

    //first consider a completely public action - it will have only one event
    ArrayList<String> obs = new ArrayList<>();
    obs.addAll(agents);
    obs.remove(actor);
    String ob_set_name = "_".join("_", obs);

    System.out.println(actor);
    System.out.println(obs);
    System.out.println(ob_set_name);
     

    res += "CAPABILITY = (( "+actor+"_leaves__"+ob_set_name+" = { \nEVENTS = { \n1 = ( PRE = at_"+actor+" AND POSSIBLE ["+actor+"] (TRUE) "; 

    //for each ob
        for(String ob : obs)
        {
            res +=   " AND at_"+ob+" AND POSSIBLE ["+ob+"] (TRUE)";
        }
        res += ", POST = {-at_"+actor+"}) \n }, \n";

    // edges for actor
        res += "EDGES = { \n("+actor+",{-(1,1)})";
    // edges for each ob
    for(String ob : obs)
    {        
        res += ",\n("+ob+",{-(1,1)})";
    }
    res += "\n},\nDES = {1}}), \n"+ actor+", c"+leavesCost.get(actor)+"),";



    //generate observer set name it: ob1_ob2 but keep a corresponding set

     int n = obs.size();

    for (int i = 0; i < n; i++) {
            for (int j = i + 1; j <= n; j++) {
                ArrayList<String> subObs = new ArrayList<>(obs.subList(i, j));
                ArrayList<String> obliv = new ArrayList<>(obs);
                obliv.removeAll(subObs);
                ob_set_name = "_".join("_", subObs);
                System.out.println(subObs);
                System.out.println(ob_set_name);

                //starts here
                res += "\nCAPABILITY = (( "+actor+"_leaves__"+ob_set_name+" = { \nEVENTS = { \n1 = ( PRE = at_"+actor+" AND POSSIBLE ["+actor+"] (TRUE) "; 

                //for each ob
                    for(String ob : subObs)
                    {
                        res +=   " AND at_"+ob+" AND POSSIBLE ["+ob+"] (TRUE)";
                    }
                    res += ", POST = {-at_"+actor+"}), \n2 = ( PRE = (TRUE) , POST = {nil} ) \n}, \n";
        
                // edges for actor
                    res += "EDGES = { \n("+actor+",{-(1,1),-(2,2)})";
                // edges for each ob
                for(String ob : subObs)
                {        
                    res += ",\n("+ob+",{-(1,1),-(2,2)})";
                }
                for(String obl : obliv)
                {        
                    res += ",\n("+obl+",{-(1,2),-(2,2)})";
                }
                res += "\n},\nDES = {1}}), \n"+ actor+", c"+leavesCost.get(actor)+"),";    

                //ends here
            }
    }
    // all oblivious
    res += "\nCAPABILITY = (( "+actor+"_leaves__ = { \nEVENTS = { \n1 = at_"+actor+" AND POSSIBLE ["+actor+"] (TRUE) "; 

    ArrayList<String> subObs = new ArrayList<>();
    ArrayList<String> obliv = new ArrayList<>(obs);
    obliv.removeAll(subObs);

    //for each ob
        for(String ob : subObs)
        {
            res +=   " AND at_"+ob+" AND POSSIBLE ["+ob+"] (TRUE)";
        }
        res += ", POST = {-at_"+actor+"}), \n2 = ( PRE = (TRUE) , POST = {nil} ) \n}, \n";

    // edges for actor
        res += "EDGES = { \n("+actor+",{-(1,1),-(2,2)})";
    // edges for each ob
    for(String ob : subObs)
    {        
        res += ",\n("+ob+",{-(1,1),-(2,2)})";
    }
    for(String obl : obliv)
    {        
        res += ",\n("+obl+",{-(1,2),-(2,2)})";
    }
    res += "\n},\nDES = {1}}), \n"+ actor+", c"+leavesCost.get(actor)+"),"; 

}
return res;
}


static String eatsInit(ArrayList<String> agents){

    String res = "";

// min 2 agents are required. 
// actor and predator are same, but there is a prey and rest could be either observers or oblivious

for(String predator : agents) { 

    ArrayList<String> preys = new ArrayList<>(agents);
    preys.remove(predator);
    for(String prey : preys){
        ArrayList<String> obs = new ArrayList<>(preys);
        obs.remove(prey);
        String ob_set_name = "_".join("_", obs);
        //System.out.println(predator+" preys on "+ prey);
        //System.out.println(obs);
        //System.out.println(ob_set_name);
        res += "CAPABILITY = (( "+predator+"_eats_"+prey+"__"+ob_set_name+" = { \nEVENTS = { \n1 = ( PRE = predator_"+predator+"_"+prey+" AND -friends_"+prey+"_"+predator+" AND at_"+predator+" AND POSSIBLE ["+predator+"] (TRUE) AND at_"+prey+" AND POSSIBLE ["+prey+"] (TRUE) AND hungry_"+predator; 

        //for each ob
            for(String ob : obs)
            {
                res +=   " AND at_"+ob+" AND POSSIBLE ["+ob+"] (TRUE)";
            }
            res += ", POST = {-hungry_"+predator+"}) \n }, \n";
    
        // edges for predator
            res += "EDGES = { \n("+predator+",{-(1,1)})";
        // edges for predator
            res += ",\n("+prey+",{-(1,1)})";
        // edges for each ob
        for(String ob : obs)
        {        
            res += ",\n("+ob+",{-(1,1)})";
        }

        //DEL edges


        res += "\n} \n DELEDGES = {\n ("+prey+",{-(1,1)})\n} ,\nDES = {1}}), \n"+ predator+", c"+eatsCost.get(predator)+"),";
    
    
    
        //generate observer set name it: ob1_ob2 but keep a corresponding set
    
         int n = obs.size();
    
        for (int i = 0; i < n; i++) {
                for (int j = i + 1; j <= n; j++) {
                    ArrayList<String> subObs = new ArrayList<>(obs.subList(i, j));
                    ArrayList<String> obliv = new ArrayList<>(obs);
                    obliv.removeAll(subObs);
                    ob_set_name = "_".join("_", subObs);
                    System.out.println(subObs);
                    System.out.println(ob_set_name);
    
                    //starts here
                    res += "\nCAPABILITY = (( "+predator+"_eats_"+prey+"__"+ob_set_name+" = { \nEVENTS = { \n1 = ( PRE = predator_"+predator+"_"+prey+" AND -friends_"+prey+"_"+predator+" AND at_"+predator+" AND POSSIBLE ["+predator+"] (TRUE) AND at_"+prey+" AND POSSIBLE ["+prey+"] (TRUE) AND hungry_"+predator; 
    
                    //for each ob
                        for(String ob : subObs)
                        {
                            res +=   " AND at_"+ob+" AND POSSIBLE ["+ob+"] (TRUE)";
                        }
                        res += ", POST = {-hungry_"+predator+"}), \n2 = ( PRE = (TRUE) , POST = {nil} ) \n}, \n";
            
                    // edges for actor
                        res += "EDGES = { \n("+predator+",{-(1,1),-(2,2)})";
                        res += ",\n("+prey+",{-(1,1),-(2,2)})";
                    // edges for each ob
                    for(String ob : subObs)
                    {        
                        res += ",\n("+ob+",{-(1,1),-(2,2)})";
                    }
                    for(String obl : obliv)
                    {        
                        res += ",\n("+obl+",{-(1,2),-(2,2)})";
                    }
                    res += "\n} \n DELEDGES = {\n ("+prey+",{-(1,1)})\n} ,\nDES = {1}}), \n"+ predator+", c"+eatsCost.get(predator)+"),";  
    
                    //ends here
                }
        }
        // all oblivious
        res += "\nCAPABILITY = (( "+predator+"_eats_"+prey+"__"+ob_set_name+" = { \nEVENTS = { \n1 = ( PRE = predator_"+predator+"_"+prey+" AND -friends_"+prey+"_"+predator+" AND at_"+predator+" AND POSSIBLE ["+predator+"] (TRUE) AND at_"+prey+" AND POSSIBLE ["+prey+"] (TRUE) AND hungry_"+predator;  
    
        ArrayList<String> subObs = new ArrayList<>();
        ArrayList<String> obliv = new ArrayList<>(obs);
        obliv.removeAll(subObs);
    
        //for each ob
            for(String ob : subObs)
            {
                res +=   " AND at_"+ob+" AND POSSIBLE ["+ob+"] (TRUE)";
            }
            res += ", POST = {-hungry_"+predator+"}), \n2 = ( PRE = (TRUE) , POST = {nil} ) \n}, \n";
    
        // edges for actor
            res += "EDGES = { \n("+predator+",{-(1,1),-(2,2)})";
            res += ",\n("+prey+",{-(1,1),-(2,2)})";

        // edges for each ob
        for(String ob : subObs)
        {        
            res += ",\n("+ob+",{-(1,1),-(2,2)})";
        }
        for(String obl : obliv)
        {        
            res += ",\n("+obl+",{-(1,2),-(2,2)})";
        }
        res += "\n} \n DELEDGES = {\n ("+prey+",{-(1,1)})\n} ,\nDES = {1}}), \n"+ predator+", c"+eatsCost.get(predator)+"),"; 
    
    }
    
    
    break;
    }
    return res;
}
    
public static void main(String[] args){
        
        agents.add("m"); arrivesCost.put("m", 25); fleeCost.put("m", 25); leavesCost.put("m", 25); eatsCost.put("m", 20); 
        agents.add("g"); arrivesCost.put("g", 3);  fleeCost.put("g", 5);  leavesCost.put("g", 3);  eatsCost.put("g", 2);
        agents.add("c"); arrivesCost.put("c", 3);  fleeCost.put("c", 5);  leavesCost.put("c", 3);  eatsCost.put("c", 2);
        agents.add("s"); arrivesCost.put("s", 2);  fleeCost.put("s", 3);  leavesCost.put("s", 2);  eatsCost.put("s", 1);
        //agents.add("c");  arrivesCost.put("c", 3); fleeCost.put("c", 2); leavesCost.put("c", 3);
        //agents.add("M");  arrivesCost.put("M", 1); fleeCost.put("M", 1); leavesCost.put("M", 1);

        //unary actions: arrives, flee, leaves
        /*
         * uncomment below for final run
         */
        String arrivesLib = arrivesInit(agents);
        String fleeLib = fleeInit(agents);
        String leavesLib = leavesInit(agents);
        String eatsLib = eatsInit(agents);
        String all = arrivesLib + fleeLib + leavesLib + eatsLib;


        //binary actions: eats -- agent deleting too

        
        // System.out.println(eatsLib);

        Path filepath = Paths.get("./genhelp.txt");
 
        try {
            Files.writeString(filepath, all, StandardCharsets.UTF_8);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // n-ary actions: introduce, fake-create, fake-disappear


        
        


    }
}
