package Parse;

import Lexical.*;

public class Node{
    protected static final int INNER = 1;
    protected static final int LEAF = 0;
    protected int innerOrLeaf = -1; // 指示节点是内部节点还是叶子节点

    private NodeType nodeType; //节点类型
    private int depth;

    public Node(NodeType nodeType)
    {
        this.nodeType = nodeType;
        depth = 0;
    }

    public Node()
    {
        this(NodeType.NULL);
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String toString()
    {
        return "";
    }
}