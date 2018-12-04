package Parse;

public enum NodeType {
    //空节点
    NULL,
    //非终结符节点
    EXPRESSION, //到时候可能要给表达式节点打个统一的标签，先留着
    LOG_EXPR, //逻辑表达式
    JUD_EXPR, //判等表达式
    COM_EXPR, //比较表达式
    ADD_EXPR, //加法表达式（算术表达式的开始符号）
    MUL_EXPR, //乘法表达式
    OPERAND, //操作数
    VARIABLE, //变量名
    Assign_Stmt, //ASSIGN_STMT是赋值语句（可以赋值数组）
    NUM_ASSIGN, //NUM_ASSIGN只能赋值数值并返回这个值
    //终结符节点
    LOG_OP, //逻辑符号，||，&&（and的优先级大于or）
    JUD_OP, //判等符号，!=, ==
    COM_OP, //比较符号，>, <, >=, <=
    ADD_OP, //加法符号，+、-
    MUL_OP, //乘法符号，*、/、%
    Assign_OP, //赋值符号，=
    UNARY_OP, //一元符号，+，-，！
    NUMBER, //数字
    IDENTIFIER, //标识符
}
