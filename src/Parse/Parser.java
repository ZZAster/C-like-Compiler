package Parse;

import Lexical.Token;
import Lexical.Type;

import java.util.LinkedList;
import java.util.ListIterator;

public class Parser {
    private LinkedList<Node> TreeList = new LinkedList<>();
    private ListIterator<Token> tokenIterator;
    private Token current;

    //输入词法分析得到的序列，输出语法树
    public LinkedList<Node> getTreeList(LinkedList<Token> tokens)
    {
        tokenIterator = tokens.listIterator();
        while(tokenIterator.hasNext())
        {
            TreeList.add(getTree());
        }
        return TreeList;
    }

    //输入token序列，输出算术表达式的语法树
       public Node test(LinkedList<Token> tokens) throws ParseException{
           tokenIterator = tokens.listIterator();
           Node node = getAddExpr();
           if (tokenIterator.hasNext())
               throw new ParseException(current, "Empty");
           return node;
       }

    //每次循环中语法分析的起点
    private Node getTree()
    {
        switch (nextType())
        {
            case IF:
            //TODO 整个模块的语法分析
        }
        return null;
    }

    //识别加法表达式
    private Node getAddExpr() throws ParseException
    {
        /* 右递归实现方法，由于实际运算需要采用左递归，故改变实现方式为左递归
        if (isMatch(Type.ADD, Type.SUB))
        {
            //((Inner) node).setLeft(left);
            //((Inner) node).setMiddle(getAddOp());
            //((Inner) node).setRight(getAddExpr());
            Node new_left = new Inner(NodeType.ADD_EXPR);
            ((Inner) new_left).setLeft(left);
            ((Inner) new_left).setMiddle(getAddOp());
            ((Inner) new_left).setRight(getMulExpr());
            if (isMatch(Type.ADD, Type.SUB))
            {
                ((Inner) node).setLeft(new_left);
                ((Inner) node).setMiddle(getAddOp());
                ((Inner) node).setRight(getAddExpr());
            }
            else
                return new_left;
        }
        else
            return left;//((Inner) node).setLeft(left);
        return node;
        */
        //左递归实现
        Node left = getMulExpr();
        return getF(left);
    }

    //加法表达式的辅助函数
    private Node getF(Node left) throws ParseException
    {
        if (isMatch(Type.ADD, Type.SUB))
        {
            Node new_left = new Inner(NodeType.ADD_EXPR);
            ((Inner) new_left).setLeft(left);
            ((Inner) new_left).setMiddle(getAddOp());
            ((Inner) new_left).setRight(getMulExpr());
            return getF(new_left);
        }
        else
            return left;
    }

    //识别赋值语句
    private Node getAssignStmt() throws ParseException
    {
        if (isMatch(Type.NUMBER))
            return getNumber();
        Node node = new Inner(NodeType.Assign_Stmt);
        Node left = getVariable();
        if (isMatch(Type.ASSIGN))
        {
            ((Inner) node).setLeft(left);
            ((Inner) node).setMiddle(getAssignOp());
        }
        else
            return left;
        if (isMatch(Type.LEFT_PARENT))
        {
            consume(Type.LEFT_PARENT);
            ((Inner) node).setRight(getAddExpr());
            consume(Type.RIGHT_PARENT);
        }
        else
            ((Inner) node).setRight(getAssignStmt());
        return node;
    }

    //识别乘法表达式
    private Node getMulExpr() throws ParseException
    {
        /* 右递归实现
        if (isMatch(Type.MUL, Type.DIV, Type.MOD))
        {
            ((Inner) node).setLeft(left);
            ((Inner) node).setMiddle(getMulOp());
            ((Inner) node).setRight(getMulExpr());
        }
        else
            return left;//((Inner) node).setLeft(left);
        return node;
        */
        Node left = getOperand();
        return getT(left);
    }

    //乘法表达式的辅助函数
    private Node getT(Node left) throws ParseException
    {
        if (isMatch(Type.MUL, Type.DIV, Type.MOD))
        {
            Node new_left = new Inner(NodeType.MUL_EXPR);
            ((Inner) new_left).setLeft(left);
            ((Inner) new_left).setMiddle(getMulOp());
            ((Inner) new_left).setRight(getOperand());
            return getT(new_left);
        }
        else
            return left;
    }

    //识别操作数
    private Node getOperand() throws ParseException
    {
        Node node = new Inner(NodeType.OPERAND);
        switch (nextType())
        {
            case LEFT_PARENT:
                consume(Type.LEFT_PARENT);
                //((Inner) node).setLeft(getAddExpr());
                node = getAddExpr();
                if ((node.getNodeType() == NodeType.VARIABLE || node.getNodeType() == NodeType.IDENTIFIER)
                        && isMatch(Type.ASSIGN))
                {
                    Node temp = new Inner(NodeType.Assign_Stmt);
                    ((Inner) temp).setLeft(node);
                    ((Inner) temp).setMiddle(getAssignOp());
                    ((Inner) temp).setRight(getAssignStmt());
                    node = temp;
                }
                consume(Type.RIGHT_PARENT);
                break;
            case NUMBER:
                node = getNumber();//((Inner) node).setLeft(getNumber());
                break;
            case IDENTIFIER:
                node = getVariable();//((Inner) node).setLeft(getVariable());
                break;
            case POSITIVE:
            case NEGATIVE:
                ((Inner) node).setMiddle(getUnaryOp());
                ((Inner) node).setLeft(getOperand());
                break;
            default:
                //todo 报错
                throw new ParseException(current, "Operand");
        }
        return node;
    }

    //识别变量名
    private Node getVariable() throws ParseException
    {
        Node node = new Inner(NodeType.VARIABLE);
        Node left = getID();
        if (isMatch(Type.LEFT_BRACKET))
        {
            ((Inner) node).setLeft(left);
            consume(Type.LEFT_BRACKET);
            //todo 存在同样的问题，逻辑表达式是否能作为数组下标？
            ((Inner) node).setMiddle(getAddExpr());
            consume(Type.RIGHT_BRACKET);
        }
        else
            return left;//((Inner) node).setLeft(left);
        return node;
    }

    //识别标识符
    private Node getID() throws ParseException
    {
        if (isMatch(Type.IDENTIFIER))
        {
            Node node = new Leaf(NodeType.IDENTIFIER);
            ((Leaf) node).setToken(tokenIterator.next());
            return node;
        }
        else
            throw new ParseException(current, "Identifier");
    }

    //识别数值
    private Node getNumber() throws ParseException
    {
        if (isMatch(Type.NUMBER))
        {
            Node node = new Leaf(NodeType.NUMBER);
            ((Leaf) node).setToken(tokenIterator.next());
            return node;
        }
        else
            throw new ParseException(current, "Number");
    }

    //识别加法符号
    private Node getAddOp() throws ParseException
    {
        if (isMatch(Type.ADD, Type.SUB))
        {
            Node node = new Leaf(NodeType.ADD_OP);
            ((Leaf) node).setToken(tokenIterator.next());
            return node;
        }
        else
            throw new ParseException(current, "Add Operation");
    }

    //识别乘法符号
    private Node getMulOp() throws ParseException
    {
        if (isMatch(Type.MUL, Type.DIV, Type.MOD))
        {
            Node node = new Leaf(NodeType.MUL_OP);
            ((Leaf) node).setToken(tokenIterator.next());
            return node;
        }
        else
            throw new ParseException(current, "Mul Operation");
    }

    //识别正负号
    private Node getUnaryOp() throws ParseException
    {
        if (isMatch(Type.POSITIVE, Type.NEGATIVE))
        {
            Node node = new Leaf(NodeType.UNARY_OP);
            ((Leaf) node).setToken(tokenIterator.next());
            return node;
        }
        else
            throw new ParseException(current, "Unary Operation");
    }

    //识别赋值符号
    private Node getAssignOp() throws ParseException
    {
        if (isMatch(Type.ASSIGN))
        {
            Node node = new Leaf(NodeType.Assign_OP);
            ((Leaf) node).setToken(tokenIterator.next());
            return node;
        }
        else
            throw new ParseException(current, "Assign Operation");
    }

    //对Token序列的操作集
    /**
     * 返回下一个Token的Type值
     * 如果不存在下一个Token则返回NULL型Type
     * @return type or NULL
     */
    private Type nextType()
    {
        if (tokenIterator.hasNext()) {
            current = tokenIterator.next();
            Type type = current.getType();
            tokenIterator.previous(); // 还原迭代器的指针位置
            return type;
        }
        else
            return Type.NULL;
    }

    /**
     * 对下一个Token进行匹配并抵消
     * 若不匹配则进行报错
     * @param type  要求进行匹配的type
     */
    private void consume(Type type) throws ParseException
    {
        if (tokenIterator.hasNext())
        {
            current = tokenIterator.next();
            if (current.getType() == type)
                return;
        }
        //TODO 进行报错
        throw new ParseException(current, type.toString());
    }

    /**
     * 检查下一个Token的Type值是否为需要的type
     * @param types 需要的type序列
     * @return 是否匹配成功
     */
    private boolean isMatch(Type ...types)
    {
        Type type = nextType();
        for (Type t : types)
            if (type == t)
                return true;
        return false;
    }


}
