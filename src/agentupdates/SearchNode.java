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
public class SearchNode {
    
    SearchNode ParentNode;
    KripkeAction Action;
    ArrayList<String> listIactions;
    KripkeStructure ks;
    
    public SearchNode(SearchNode parentNode, KripkeAction Action, KripkeStructure self, ArrayList<String> iactionslist)
    {
        this.ParentNode = parentNode;
        this.Action = Action;
        this.ks = self;
        this.listIactions = iactionslist;
    }
    
    
}
