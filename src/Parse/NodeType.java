package Parse;

public enum NodeType {
    //空节点
    NULL,
    //非终结符节点
    EXPRESSION,
    ADD_EXPR,
    MUL_EXPR,
    OPERAND,
    VARIABLE,
    //终结符节点
    LOG_OP,
    ADD_OP,
    MUL_OP,
    UNARY_OP,
    NUMBER,
    IDENTIFIER,
}
