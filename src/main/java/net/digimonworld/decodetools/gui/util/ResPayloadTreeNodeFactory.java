package net.digimonworld.decodetools.gui.util;

import net.digimonworld.decodetools.res.ResPayload;
import net.digimonworld.decodetools.res.ResPayload.Payload;
import net.digimonworld.decodetools.res.kcap.AbstractKCAP;

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
        
        if(payload.getType() == Payload.KCAP)
            return new AbstractKCAPTreeNode((AbstractKCAP) payload);

        return new ResPayloadTreeNode(payload); //default, a strict leaf node
    }
}