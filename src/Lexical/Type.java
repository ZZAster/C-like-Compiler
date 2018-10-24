package Lexical;

public enum Type {
    //关键字
    IF,
    ELSE,
    WHILE,
    FOR,
    PRINT,
    SCAN,
    INT,
    DOUBLE,
    BOOL,
    STRING,
    VOID,
    RETURN,
    //操作符
    //一元
    POSITIVE,
    NEGATIVE,
    //二元
    ADD,
    SUB,
    MUL,
    DIV,
    MOD,
    ASSIGN,
    EQUAL,
    NOT_EQUAL,
    LEFT_THAN,
    RIGHT_THAN,
    LEFT_EQUAL,
    RIGHT_EQUAL,
    NOT,
    AND,
    OR,
    LEFT_PARENT,
    RIGHT_PARENT,
    LEFT_BRACKET,
    RIGHT_BRACKET,
    //分隔符
    LEFT_BRACE,
    RIGHT_BRACE,
    COMMA,
    SEMICOLON,
    //其他
    DOUBLE_QUOTATION,
    //SINGLE_QUOTATION, （目前不支持char类型）
    IDENTIFIER,
    //Values
    NUMBER,
    BOOLEAN,
    STRING_VALUE
}
