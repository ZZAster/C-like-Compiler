package Parse;

public class Inner extends Node {
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
