package de.phoenixstaffel.decodetools.gui.util;

import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import de.phoenixstaffel.decodetools.res.ResPayload;

/**
 * Represents a {@link ResPayload} as a TreeNode. This will always be a leaf node.
 */
public class ResPayloadTreeNode implements TreeNode {
    private ResPayload root;

    /**
     * Creates a new TreeNode with the given {@link ResPayload} as root.
     * 
     * @param root the {@link ResPayload} to use a root
     */
    protected ResPayloadTreeNode(ResPayload root) {
        this.root = root;
    }
    
    @Override
    public TreeNode getChildAt(int childIndex) {
        return null;
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public TreeNode getParent() {
        return new AbstractKCAPTreeNode(root.getParent()); // KCAPTreeNode(root.getParent());
    }

    @Override
    public int getIndex(TreeNode node) {
        return -1;
    }

    @Override
    public boolean getAllowsChildren() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public Enumeration<ResPayloadTreeNode> children() {
        return null;
    }
    
    @Override
    public String toString() {
        return root.toString(); //the ResPayload should decide the display name, for now
    }

    /**
     * Get the payload associated with this node.
     * 
     * @return the payload associated with this node
     */
    public ResPayload getPayload() {
        return root;
    }
}