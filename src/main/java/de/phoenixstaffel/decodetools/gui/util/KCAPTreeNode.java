package de.phoenixstaffel.decodetools.gui.util;

import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import de.phoenixstaffel.decodetools.res.ResPayload;
import de.phoenixstaffel.decodetools.res.payload.KCAPPayload;

/**
 * Represents a {@link KCAPPayload} as TreeNode.
 */
public class KCAPTreeNode extends ResPayloadTreeNode {

    /**
     * Creates a new TreeNode with the given {@link KCAPPayload} as root.
     * 
     * @param root the {@link KCAPPayload} to use a root
     */
    protected KCAPTreeNode(KCAPPayload root) {
        super(root);
    }
    
    @Override
    public TreeNode getChildAt(int childIndex) {
        if(childIndex > getChildCount())
            return null;
        
        return ResPayloadTreeNodeFactory.craft(getPayload().get(childIndex));
    }

    @Override
    public int getChildCount() {
        return getPayload().getNumEntries();
    }

    @Override
    public TreeNode getParent() {
        return new KCAPTreeNode(getPayload().getParent());
    }

    @Override
    public int getIndex(TreeNode node) {
        if(!(node instanceof ResPayloadTreeNode))
            return -1;
        
        int i = 0;
        Enumeration<ResPayload> iter = children();
        
        while(iter.hasMoreElements()) {
            if(iter.nextElement().equals(((ResPayloadTreeNode) node).getPayload()))
                return i;
            
            i++;
        }

        return -1;
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    // TODO update to Enumeration<? extends TreeNode>
    @Override
    public Enumeration<ResPayload> children() {
        return new IteratorEnumeration<>(getPayload().getEntries().iterator());
    }        
    
    @Override
    public KCAPPayload getPayload() {
        return (KCAPPayload) super.getPayload();
    }
}