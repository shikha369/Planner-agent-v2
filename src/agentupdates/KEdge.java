/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentupdates;

/**
 *
 * @author shikha
 */
public class KEdge {
    
    /**
     * 
     *A kripkeedge has an id
     *It has indices of from-kripke-world and to-kripke-world
     *It has agent id
     * 
     */
    public Integer edgeId;
    public Integer fromKripkeWorldId;
    public Integer toKripkeWorldId;
    public String agent;

    public KEdge(int edgeId, int fromKripkeWorldId, int toKripkeWorldId, String agent) {
        this.edgeId = edgeId;
        this.agent = agent;
        this.fromKripkeWorldId = fromKripkeWorldId;
        this.toKripkeWorldId = toKripkeWorldId;
    }
    
    public KEdge deep_copy(){
        return new KEdge(this.edgeId, this.fromKripkeWorldId, this.toKripkeWorldId, this.agent);
    }
    
   
    
}
