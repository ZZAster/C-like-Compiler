package Parse;

import Lexical.Token;
import Lexical.Type;

import java.util.LinkedList;
import java.util.ListIterator;

public class Parser {
    //private LinkedList<Node> TreeList = new LinkedList<>();
    private ListIterator<Token> tokenIterator;
    private Token current;
    private int point = 0;


    //以词法分析结果为参数构造语法分析器
    public Parser(LinkedList<Token> tokens)
    {
        tokenIterator = tokens.listIterator();
    }

    //输入token序列，输出算术表达式的语法树
    public Node test() throws ParseException {
        Node node = getExp();
        if (tokenIterator.hasNext())
            throw new ParseException(current, "Empty");
        return node;
    }

    //识别程序，这种方法以函数为单位构造树，并将程序视为树的列表
    private LinkedList<Node> getProgram() throws ParseException
    {
        LinkedList<Node> NodeList = new LinkedList<>();
        while (tokenIterator.hasNext())
            NodeList.add(getFuncDeclare());
        return NodeList;
    }

    /*这种方法以程序为单位构造语法树
    //识别程序
    private Node getProgram() throws ParseException
    {
        Node node = new Inner(NodeType.PROGRAM);
        ((Inner) node).setLeft(getFuncDeclare());
        if (tokenIterator.hasNext())
            ((Inner) node).setMiddle(getFuncClosure());
        return node;
    }

    //识别函数声明闭包
    private Node getFuncClosure() throws ParseException
    {
        if (isMatch(Type.INT, Type.DOUBLE, Type.VOID))
        {
            Node node = new Inner(NodeType.FUNC_CLOSURE);
            ((Inner) node).setLeft(getFuncDeclare());
            if (tokenIterator.hasNext())
                ((Inner) node).setMiddle(getFuncClosure());
            return node;
        }
        else
            throw new ParseException(current, "function declare");
    }
    */

    //识别函数声明
    private Node getFuncDeclare() throws ParseException
    {
        Node node = new Inner(NodeType.FUNC_DECLARE);
        ((Inner) node).setLeft(getFuncSign());
        if (isMatch(Type.SEMICOLON))
        {
            ((Inner) node).setMiddle(getEmpty());
            consume(Type.SEMICOLON);
        }
        else if (isMatch(Type.LEFT_BRACE))
        {
            consume(Type.LEFT_BRACE);
            ((Inner) node).setMiddle(getStmtSeq());
            consume(Type.RIGHT_BRACE);
        }
        else
            throw new ParseException(current, "function define");
        return node;
    }

    //识别函数签名
    private Node getFuncSign() throws ParseException
    {
        Node node = new Inner(NodeType.FUNC_SIGNATURE);
        if (isMatch(Type.VOID))
            ((Inner) node).setLeft(getVoid());
        else
            ((Inner) node).setLeft(getType());
        ((Inner) node).setMiddle(getID());
        consume(Type.LEFT_PARENT);
        ((Inner) node).setRight(getArgDecList());
        consume(Type.RIGHT_PARENT);
        return node;
    }

    //识别参数声明列表
    private Node getArgDecList() throws ParseException
    {
        if (isMatch(Type.VOID))
            return getVoid();
        Node node = new Inner(NodeType.ARG_DEC_LIST);
        ((Inner) node).setLeft(getArgDeclare());
        ((Inner) node).setMiddle(getArgClosure());
        return node;
    }

    //识别参数声明闭包
    private Node getArgClosure() throws ParseException
    {
        consume(Type.COMMA);
        Node left = getArgDeclare();
        if (isMatch(Type.COMMA))
        {
            Node node = new Inner(NodeType.ARG_CLOSURE);
            ((Inner) node).setLeft(left);
            ((Inner) node).setMiddle(getArgClosure());
            return node;
        }
        return left;
    }

    //识别参数声明
    private Node getArgDeclare() throws ParseException
    {
        Node node = new Inner(NodeType.ARG_DECLARE);
        ((Inner) node).setLeft(getType());
        ((Inner) node).setMiddle(getID());
        return node;
    }

    //识别基本语句
    private Node getStmt() throws ParseException
    {
        Node node = new Node();
        switch (nextType())
        {
            // if-stmt, while-stmt, for-stmt, declare-stmt
            // assign-stmt, write-stmt, read-stmt, return-stmt, empty-stmt( ;)
            case IF:
                return getIfStmt();
            case DO:
            case WHILE:
                return getWhileStmt();
            case FOR:
                return getForStmt();
            case INT:
            case DOUBLE:
                node =  getDeclareStmt();
                break;
            case IDENTIFIER:
                node = getAssignStmt();
                break;
            case PRINT:
                node = getWriteStmt();
                break;
            case SCAN:
                node = getReadStmt();
                break;
            case RETURN:
                node = getReturnStmt();
                break;
            case SEMICOLON:
                node = getEmpty();
                break;
            default:
                //todo 这里是不是应该进行语义报错？
        }
        consume(Type.SEMICOLON);
        return node;
    }

    //识别语句序列
    private Node getStmtSeq() throws ParseException
    {
        Node node = new Inner(NodeType.STMT_SEQ);
        Node left = getStmt();
        if (isMatch(Type.RIGHT_BRACE))
            return left;
        ((Inner) node).setLeft(left);
        ((Inner) node).setMiddle(getStmtSeq());
        return node;
    }

    //识别语句块
    private Node getStmtBlock() throws ParseException
    {
        if (isMatch(Type.LEFT_BRACE))
        {
            consume(Type.LEFT_BRACE);
            Node node = getStmtSeq();
            consume(Type.RIGHT_BRACE);
            return node;
        }
        else
            return getStmt();
    }

    //识别if语句
    private Node getIfStmt() throws ParseException
    {
        consume(Type.IF);
        consume(Type.LEFT_PARENT);
        Node node = new Inner(NodeType.IF_STMT);
        ((Inner) node).setLeft(getExp());
        consume(Type.RIGHT_PARENT);
        ((Inner) node).setMiddle(getStmtBlock());
        if (isMatch(Type.ELSE))
        {
            consume(Type.ELSE);
            ((Inner) node).setRight(getStmtBlock());
        }
        return node;
    }

    //识别while语句
    private Node getWhileStmt() throws ParseException
    {
        Node node = new Inner(NodeType.WHILE_STMT);
        if (isMatch(Type.WHILE))
        {
            consume(Type.WHILE);
            consume(Type.LEFT_PARENT);
            ((Inner) node).setLeft(getExp());
            consume(Type.RIGHT_PARENT);
            ((Inner) node).setMiddle(getStmtBlock());
        }
        else if (isMatch(Type.DO))
        {
            consume(Type.DO);
            ((Inner) node).setMiddle(getStmtBlock());
            consume(Type.WHILE);
            consume(Type.LEFT_PARENT);
            ((Inner) node).setLeft(getExp());
            consume(Type.RIGHT_PARENT);
            consume(Type.SEMICOLON);
        }
        return node;
    }

    //识别for语句
    private Node getForStmt() throws ParseException
    {
        consume(Type.FOR);
        consume(Type.LEFT_PARENT);
        Node node = new Inner(NodeType.FOR_STMT);
        ((Inner) node).setLeft(getForList());
        consume(Type.RIGHT_PARENT);
        ((Inner) node).setMiddle(getStmtBlock());
        return node;
    }

    //识别for-list
    private Node getForList() throws ParseException
    {
        Node node = new Inner(NodeType.FOR_LIST);
        if (isMatch(Type.INT, Type.DOUBLE))
            ((Inner) node).setLeft(getDeclareStmt());
        else
            ((Inner) node).setLeft(getAssignStmt());
        consume(Type.SEMICOLON);
        ((Inner) node).setMiddle(getExp());
        consume(Type.SEMICOLON);
        ((Inner) node).setRight(getAssignStmt());
        return node;
    }

    /**识别表达式
     * 下面一系列函数用于识别各子类表达式的节点
     * 每个函数都有一个同名的辅助函数用于完成对左递归形式的识别
     * @return 返回最低优先级操作符对应的表达式（exp || exp）结点，或者赋值节点（variable = exp）
     * @throws ParseException 抛出语法异常
     */
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
        Node left = getMulExpr();
        return getAddExpr(left);
    }

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

    //识别乘法表达式
    private Node getMulExpr() throws ParseException
    {
        Node left = getOperand();
        return getMulExpr(left);
    }

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

    //识别print语句
    private Node getWriteStmt() throws ParseException
    {
        Node node = new Inner(NodeType.WRITE_STMT);
        ((Inner) node).setLeft(getPrint());
        consume(Type.LEFT_PARENT);
        if (isMatch(Type.DOUBLE_QUOTATION))
        {
            consume(Type.DOUBLE_QUOTATION);
            ((Inner) node).setMiddle(getString());
            consume(Type.DOUBLE_QUOTATION);
        }
        else
            ((Inner) node).setMiddle(getExp());
        consume(Type.RIGHT_PARENT);
        return node;
    }

    //识别scan语句
    private Node getReadStmt() throws ParseException
    {
        Node node = new Inner(NodeType.READ_STMT);
        ((Inner) node).setLeft(getScan());
        consume(Type.LEFT_PARENT);
        ((Inner) node).setMiddle(getVariable());
        consume(Type.RIGHT_PARENT);
        return node;
    }

    //识别return语句
    private Node getReturnStmt() throws ParseException
    {
        Node node = new Inner(NodeType.RETURN_STMT);
        consume(Type.RETURN);
        ((Inner) node).setLeft(getLvalue());
        return node;
    }

    //识别声明语句
    private Node getDeclareStmt() throws ParseException
    {
        Node node = new Inner(NodeType.DECLARE_STMT);
        ((Inner) node).setLeft(getType());
        ((Inner) node).setMiddle(getID());
        if (isMatch(Type.ASSIGN))
        {
            consume(Type.ASSIGN);
            ((Inner) node).setRight(getLvalue());
        }
        return node;
    }

    //识别赋值语句
    private Node getAssignStmt() throws ParseException
    {
        if (isMatch(Type.IDENTIFIER))
        {
            int begin = point;
            Node left = getVariable();
            int end = point;
            if (isMatch(Type.ASSIGN))
            {
                Node node = new Inner(NodeType.ASSIGN_STMT);
                ((Inner) node).setLeft(left);
                ((Inner) node).setMiddle(getAssignOp());
                ((Inner) node).setRight(getAssignStmt());
                return node;
            }
            else
            {
                for (int i = end - begin; i > 0; i--)
                    previous();
                return getLvalue();
            }
        }
        else
            return getLvalue();
    }

    //识别函数调用
    private Node getCallStmt() throws ParseException
    {
        Node node = new Inner(NodeType.CALL_STMT);
        ((Inner) node).setLeft(getID());
        consume(Type.LEFT_PARENT);
        if (!isMatch(Type.RIGHT_PARENT))
        {
            ((Inner) node).setMiddle(getArgList());
        }
        consume(Type.RIGHT_PARENT);
        return node;
    }

    //识别参数调用列表
    private Node getArgList() throws ParseException
    {
        Node left = getLvalue();
        if (isMatch(Type.COMMA))
        {
            Node node = new Inner(NodeType.ARG_LIST);
            ((Inner) node).setLeft(left);
            consume(Type.COMMA);
            ((Inner) node).setMiddle(getArgList());
            return node;
        }
        return left;
    }

    //识别左值（不支持{1, 2, 3,}这种最后遗留逗号的形式）
    private Node getLvalue() throws ParseException
    {
        if (isMatch(Type.LEFT_BRACE))
        {
            consume(Type.LEFT_BRACE);
            Node node = new Inner(NodeType.L_VALUE);
            ((Inner) node).setLeft(getExp());
            if (isMatch(Type.COMMA))
            {
                consume(Type.COMMA);
                ((Inner) node).setMiddle(getExpClosure());
            }
            consume(Type.RIGHT_BRACE);
            return node;
        }
        else
            return getExp();
    }

    //识别表达式闭包
    private Node getExpClosure() throws ParseException
    {
        Node left = getExp();
        if (isMatch(Type.COMMA))
        {
            Node node = new Inner(NodeType.EXP_CLOSURE);
            ((Inner) node).setLeft(left);
            ((Inner) node).setMiddle(getExpClosure());
            return node;
        }
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
                node = getNumber();
                break;
            case IDENTIFIER:
                node = getVariable();
                if ((node.getNodeType() == NodeType.IDENTIFIER) && isMatch(Type.LEFT_PARENT))
                {
                    //IDENTIFIER一定只包含一个Token
                    previous();
                    node = getCallStmt();
                }
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

    //识别print关键字
    private Node getPrint() throws ParseException
    {
        if (isMatch(Type.PRINT))
        {
            Node node = new Leaf(NodeType.WRITE);
            ((Leaf) node).setToken(next());
            return node;
        }
        else
            throw new ParseException(current, "print()");
    }

    //识别write关键字
    private Node getScan() throws ParseException
    {
        if (isMatch(Type.SCAN))
        {
            Node node = new Leaf(NodeType.READ);
            ((Leaf) node).setToken(next());
            return node;
        }
        else
            throw new ParseException(current, "scan()");
    }

    //识别数据类型
    private Node getType() throws ParseException
    {
        if (isMatch(Type.INT, Type.DOUBLE))
        {
            Node left = new Leaf(NodeType.PRIME_TYPE);
            ((Leaf) left).setToken(next());
            if (isMatch(Type.LEFT_BRACKET))
            {
                Node node = new Inner(NodeType.COMPLEX_TYPE);
                consume(Type.LEFT_BRACKET);
                if (isMatch(Type.RIGHT_BRACKET))
                    ((Inner) node).setMiddle(getEmpty());
                else
                    ((Inner) node).setMiddle(getNumber());
                consume(Type.RIGHT_BRACKET);
                return node;
            }
            return left;
        }
        else
            throw new ParseException(current, "int or double");
    }

    //识别void
    private Node getVoid() throws ParseException
    {
        if (isMatch(Type.VOID))
        {
            Node node = new Leaf(NodeType.VOID);
            ((Leaf) node).setToken(next());
            return node;
        }
        else
            throw new ParseException(current, "void");
    }

    //识别空
    private Node getEmpty()
    {
        Node node = new Leaf(NodeType.EMPTY);
        // 这个Token并不存在于词法识别返回的token序列中
        ((Leaf) node).setToken(new Token("ε", Type.NULL, -1, -1));
        return node;
    }

    //识别字符串
    private Node getString() throws ParseException
    {
        if (isMatch(Type.STRING_VALUE))
        {
            Node node = new Leaf(NodeType.STRING);
            ((Leaf) node).setToken(next());
            return node;
        }
        else
            throw new ParseException(current, "String literal");
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
            return left;
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
