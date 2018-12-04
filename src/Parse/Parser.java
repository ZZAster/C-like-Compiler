package Parse;

import Lexical.Token;
import Lexical.Type;

import java.util.LinkedList;
import java.util.ListIterator;

public class Parser {
    private LinkedList<Node> TreeList = new LinkedList<>();
    private ListIterator<Token> tokenIterator;
    private Token current;
    private int point = 0;

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
           Node node = getExp();
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

    //识别表达式
    private Node getExp() throws ParseException
    {
        if (isMatch(Type.IDENTIFIER))
        {
            int begin = point;
            Node left = getVariable();
            int end = point;
            if (isMatch(Type.ASSIGN))
            {
                Node temp = new Inner(NodeType.NUM_ASSIGN);
                ((Inner) temp).setLeft(left);
                ((Inner) temp).setMiddle(getAssignOp());
                ((Inner) temp).setRight(getNumAssign());
                return temp;
            }
            else
                for (int i = end - begin; i > 0; i--)
                    previous();
        }
        return getOrExpr();
    }

    //识别逻辑或表达式
    private Node getOrExpr() throws ParseException
    {
        Node left = getAndExpr();
        return getOrExpr(left);
    }

    private Node getOrExpr(Node left) throws ParseException
    {
        if (isMatch(Type.OR))
        {
            Node new_left = new Inner(NodeType.LOG_EXPR);
            ((Inner) new_left).setLeft(left);
            ((Inner) new_left).setMiddle(getLogOp());
            ((Inner) new_left).setRight(getAndExpr());
            return getOrExpr(new_left);
        }
        else
            return left;
    }

    //识别逻辑与表达式
    private Node getAndExpr() throws ParseException
    {
        Node left = getComExpr();
        return getAndExpr(left);
    }

    private Node getAndExpr(Node left) throws ParseException
    {
        if (isMatch(Type.AND))
        {
            Node new_left = new Inner(NodeType.LOG_EXPR);
            ((Inner) new_left).setLeft(left);
            ((Inner) new_left).setMiddle(getLogOp());
            ((Inner) new_left).setRight(getJudExpr());
            return getAndExpr(new_left);
        }
        else
            return left;
    }

    //识别判等表达式
    private Node getJudExpr() throws ParseException
    {
        Node left = getComExpr();
        return getJudExpr(left);
    }

    private Node getJudExpr(Node left) throws ParseException
    {
        if (isMatch(Type.EQUAL, Type.NOT_EQUAL))
        {
            Node new_left = new Inner(NodeType.JUD_EXPR);
            ((Inner) new_left).setLeft(left);
            ((Inner) new_left).setMiddle(getJudOp());
            ((Inner) new_left).setRight(getComExpr());
            return getJudExpr(new_left);
        }
        else
            return left;
    }

    //识别比较表达式
    private Node getComExpr() throws ParseException
    {
        Node left = getAddExpr();
        return getComExpr(left);
    }

    private Node getComExpr(Node left) throws ParseException
    {
        if (isMatch(Type.LEFT_THAN, Type.LEFT_EQUAL, Type.RIGHT_EQUAL, Type.RIGHT_THAN))
        {
            Node new_left = new Inner(NodeType.COM_EXPR);
            ((Inner) new_left).setLeft(left);
            ((Inner) new_left).setMiddle(getComOp());
            ((Inner) new_left).setRight(getAddExpr());
            return getComExpr(new_left);
        }
        else
            return left;
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
        return getAddExpr(left);
    }

    //加法表达式的辅助函数
    private Node getAddExpr(Node left) throws ParseException
    {
        if (isMatch(Type.ADD, Type.SUB))
        {
            Node new_left = new Inner(NodeType.ADD_EXPR);
            ((Inner) new_left).setLeft(left);
            ((Inner) new_left).setMiddle(getAddOp());
            ((Inner) new_left).setRight(getMulExpr());
            return getAddExpr(new_left);
        }
        else
            return left;
    }

    /* 初始版getNumAssign
    private Node getNumAssign() throws ParseException
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
            ((Inner) node).setRight(getExp());
            consume(Type.RIGHT_PARENT);
        }
        else
            ((Inner) node).setRight(getNumAssign());
        return node;
    }
    */

    //识别赋值语句
    private Node getNumAssign() throws ParseException
    {
        if (isMatch(Type.IDENTIFIER))
        {
            Node node = new Inner(NodeType.NUM_ASSIGN);
            int begin = point;
            ((Inner) node).setLeft(getVariable());
            int end = point;
            if (isMatch(Type.ASSIGN))
            {
                ((Inner) node).setMiddle(getAssignOp());
                ((Inner) node).setRight(getNumAssign());
            }
            else
            {
                for (int i = end - begin; i > 0; i--)
                    previous();
                ((Inner) node).setRight(getExp());
            }
            return node;
        }
        return getExp();
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
        return getMulExpr(left);
    }

    //乘法表达式的辅助函数
    private Node getMulExpr(Node left) throws ParseException
    {
        if (isMatch(Type.MUL, Type.DIV, Type.MOD))
        {
            Node new_left = new Inner(NodeType.MUL_EXPR);
            ((Inner) new_left).setLeft(left);
            ((Inner) new_left).setMiddle(getMulOp());
            ((Inner) new_left).setRight(getOperand());
            return getMulExpr(new_left);
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
                node = getExp();
                if ((node.getNodeType() == NodeType.VARIABLE || node.getNodeType() == NodeType.IDENTIFIER)
                        && isMatch(Type.ASSIGN))
                {
                    Node temp = new Inner(NodeType.NUM_ASSIGN);
                    ((Inner) temp).setLeft(node);
                    ((Inner) temp).setMiddle(getAssignOp());
                    ((Inner) temp).setRight(getNumAssign());
                    node = temp;
                }
                consume(Type.RIGHT_PARENT);
                break;
            case NUMBER:
                node = getNumber();//((Inner) node).setLeft(getNumber());
                break;
            case IDENTIFIER:
                //todo 这里的操作数还可能是函数的返回值
                node = getVariable();//((Inner) node).setLeft(getVariable());
                break;
            case POSITIVE:
            case NEGATIVE:
            //加入 ! 操作符
            case NOT:
                ((Inner) node).setMiddle(getUnaryOp());
                ((Inner) node).setLeft(getOperand());
                break;
            default:
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
            ((Inner) node).setMiddle(getExp());
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
            ((Leaf) node).setToken(next());
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
            ((Leaf) node).setToken(next());
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
            ((Leaf) node).setToken(next());
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
            ((Leaf) node).setToken(next());
            return node;
        }
        else
            throw new ParseException(current, "Mul Operation");
    }

    //识别正负号
    private Node getUnaryOp() throws ParseException
    {
        if (isMatch(Type.POSITIVE, Type.NEGATIVE, Type.NOT))
        {
            Node node = new Leaf(NodeType.UNARY_OP);
            ((Leaf) node).setToken(next());
            return node;
        }
        else
            throw new ParseException(current, "Unary Operation");
    }

    //识别逻辑符号
    // || 和 && 需要区分，所以好像不需要函数来识别
    private Node getLogOp() throws ParseException
    {
        if (isMatch(Type.AND, Type.OR))
        {
            Node node = new Leaf(NodeType.LOG_OP);
            ((Leaf) node).setToken(next());
            return node;
        }
        else
            throw new ParseException(current, "Logical Operation");
    }

    //识别判等符号
    private Node getJudOp() throws ParseException
    {
        if (isMatch(Type.NOT_EQUAL, Type.EQUAL))
        {
            Node node = new Leaf(NodeType.JUD_OP);
            ((Leaf) node).setToken(next());
            return node;
        }
        else
            throw new ParseException(current, "Judge Operation");
    }
    //识别比较符号
    private Node getComOp() throws ParseException
    {
        if (isMatch(Type.LEFT_THAN, Type.LEFT_EQUAL, Type.RIGHT_EQUAL, Type.RIGHT_THAN))
        {
            Node node = new Leaf(NodeType.COM_OP);
            ((Leaf) node).setToken(next());
            return node;
        }
        else
            throw new ParseException(current, "Compare Operation");
    }

    //识别赋值符号
    private Node getAssignOp() throws ParseException
    {
        if (isMatch(Type.ASSIGN))
        {
            Node node = new Leaf(NodeType.Assign_OP);
            ((Leaf) node).setToken(next());
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
            current = next();
            Type type = current.getType();
            previous(); // 还原迭代器的指针位置
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
            current = next();
            if (current.getType() == type)
                return;
        }
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

    /**
     * 将iterator的next()函数进行二次封装，使其能够记录迭代次数
     * @return iterator.next()
     */
    private Token next()
    {
        point++;
        return tokenIterator.next();
    }

    /**
     * 将iterator的previous()函数进行二次封装，使其能够记录迭代次数
     * 返回值暂时用不上，为了形式和next()统一
     * @return iterator.previous()
     */
    private Token previous()
    {
        point--;
        return tokenIterator.previous();
    }
}
