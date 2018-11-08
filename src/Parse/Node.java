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

class Inner extends Node {
    private Node left, middle, right; // 子节点，用于存放部分语义信息

    public Inner(NodeType nodeType)
    {
        super(nodeType);
        left = middle = right = new Node();
        innerOrLeaf = INNER;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
        increaseDepth(this.left);
    }

    public Node getMiddle() {
        return middle;
    }

    public void setMiddle(Node middle) {
        this.middle = middle;
        increaseDepth(this.middle);
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
        increaseDepth(this.right);
    }

    private void increaseDepth(Node node)
    {
        if (node.innerOrLeaf == LEAF)
            node.setDepth(node.getDepth() + 1);
        else if (node.innerOrLeaf == INNER)
        {
            node.setDepth(node.getDepth() + 1);
            increaseDepth(((Inner) node).getLeft());
            increaseDepth(((Inner) node).getMiddle());
            increaseDepth(((Inner) node).getRight());
        }
    }

    public String toString()
    {
        String s = "";
        String space = "";
        for (int i = 0; i < this.getDepth(); i++)
            space +=  "\t";
        s += space + "<" + this.getNodeType() + ">\n";
        s += left.toString() + middle.toString() + right.toString();
        s += space + "</" + this.getNodeType() + ">\n";
        return s;
    }
}

class Leaf extends Node{
    private Token token; //存放终结符的具体信息

    public Leaf(NodeType nodeType)
    {
        super(nodeType);
        innerOrLeaf = LEAF;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public String toString()
    {
        String s = "";
        String space = "";
        for (int i = 0; i < this.getDepth(); i++)
            space +=  "\t";
        s += space + "<" + this.getNodeType() + ">\n";
        s += space + "\t" + this.getToken().getValue() + "\n";
        s += space + "</" + this.getNodeType() + ">\n";
        return s;
    }
}
