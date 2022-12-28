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
public class SumEdge extends Edge{

    public SumEdge(int id, int fromEventId, int toEventId, String agent) {
        
        this.id = id;
        this.fromEventId = fromEventId;
        this.toEventId = toEventId;
        this.agent = agent;
        
    }
}
