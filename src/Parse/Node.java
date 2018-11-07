package Parse;

import Lexical.*;

public class Node{
    private NodeType nodeType; //节点类型

    public Node(NodeType nodeType)
    {
        this.nodeType = nodeType;
    }

    public Node()
    {
        this(NodeType.ROOT);
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }
}

class Inner extends Node {
    private Node left, middle, right; // 子节点，用于存放部分语义信息

    public Inner(NodeType nodeType)
    {
        super(nodeType);
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getMiddle() {
        return middle;
    }

    public void setMiddle(Node middle) {
        this.middle = middle;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }
}

class Leaf extends Node{
    private Token token; //存放终结符的具体信息

    public Leaf(NodeType nodeType)
    {
        super(nodeType);
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
