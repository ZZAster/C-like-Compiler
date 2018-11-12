package Parse;

public enum NodeType {
    //空节点
    NULL,
    //非终结符节点
    EXPRESSION,
    ADD_EXPR, //加法表达式（算术表达式的开始符号）
    MUL_EXPR, //乘法表达式
    OPERAND, //操作数
    VARIABLE, //变量名
    Assign_Stmt,
    //终结符节点
    LOG_OP,
    ADD_OP, //加法符号，+、-
    MUL_OP, //乘法符号，*、/、%
    Assign_OP, //赋值符号，=
    UNARY_OP, //一元符号，+，-
    NUMBER, //数字
    IDENTIFIER, //标识符
}
