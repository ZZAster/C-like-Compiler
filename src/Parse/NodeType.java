package Parse;

public enum NodeType {
    //空节点
    NULL,
    //非终结符节点
    STMT_SEQ, //语句序列
    EXPRESSION, //到时候可能要给表达式节点打个统一的标签，先留着
    LOG_EXPR, //逻辑表达式
    JUD_EXPR, //判等表达式
    COM_EXPR, //比较表达式
    ADD_EXPR, //加法表达式（算术表达式的开始符号）
    MUL_EXPR, //乘法表达式
    OPERAND, //操作数
    VARIABLE, //变量名
    L_VALUE, //左值,实际上NodeType为L_VALUE的节点一定是数组字面值，如{1，2，3}
    EXP_CLOSURE, //表达式闭包，用来识别多个表达式组成的数组字面值
    CALL_STMT, //函数调用
    ARG_LIST, //参数调用列表
    WRITE_STMT, // print语句
    READ_STMT, // scan语句
    RETURN_STMT, //return语句
    ASSIGN_STMT, //ASSIGN_STMT是赋值语句（可以赋值数组）
    NUM_ASSIGN, //NUM_ASSIGN只能赋值数值并返回这个值
    DECLARE_STMT, //声明语句
    COMPLEX_TYPE, //复合类型，int[], double[]
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
    WRITE, //print
    READ, //scan
    STRING, //字符串
    PRIME_TYPE, //int 和 double
    EMPTY, //和number一起表示数组声明时的下标，对应的Token Type 为NULL
}
