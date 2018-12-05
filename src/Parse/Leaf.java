package Parse;

import Lexical.Token;

public class Leaf extends Node{
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