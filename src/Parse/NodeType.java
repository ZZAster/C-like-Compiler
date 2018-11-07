package Parse;

public enum NodeType {
    //根节点，用作空节点
    ROOT,
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
