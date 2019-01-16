package de.phoenixstaffel.decodetools.gui.util;

import de.phoenixstaffel.decodetools.res.ResPayload;
import de.phoenixstaffel.decodetools.res.ResPayload.Payload;
import de.phoenixstaffel.decodetools.res.kcap.AbstractKCAP;
import de.phoenixstaffel.decodetools.res.payload.KCAPPayload;

public class ResPayloadTreeNodeFactory {
    
    private ResPayloadTreeNodeFactory() {}
    
    /**
     * Creates a new TreeNode from a given {@link ResPayload}, depending on the type of the ResPayload.
     * 
     * @param payload the payload to create a node from
     * @return the created node
     */
    public static ResPayloadTreeNode craft(ResPayload payload) {
        // might be worth to adjust to switch/case if there are more node types
        
        if(payload.getType() == Payload.KCAP) {
            if(payload instanceof KCAPPayload)
                return new KCAPTreeNode((KCAPPayload) payload);
            else if (payload instanceof AbstractKCAP)
                return new AbstractKCAPTreeNode((AbstractKCAP) payload);
            
        }

        return new ResPayloadTreeNode(payload); //default, a strict leaf node
    }
}