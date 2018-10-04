# C-like-Compiler
# C-like 语言
## 语言结构
- 顺序结构：赋值（=），输入（scan()），输出（print()）<br>
- 选择语句：if-else (允许使用级联形式)<br>
- 循环结构：for, while<br>

## 表达式
- 关系表达式：<, >, ==, !=, <=, >=<br>
- 算术表达式: +, -, *, /, %, ()<br>
- 逻辑表达式：!, ||, &&<br>
- 不支持进行位运算（|, &, ~）<br>
- 不支持三元运算符（? :）<br>

## 程序结构
- 语句以;作为结束，{}表示一个程序块<br>
- 支持单行和多行注释 /*, //<br>
- 变量使用前必须定义<br>
- 函数使用前必须声明<br>
- 支持数组运算，不支持指针和取地址运算，[]表示数组下标<br>

## 数据类型
- int 整型 （只支持十进制数）<br>
- double 浮点型（支持科学计数法）<br>
- bool   布尔型<br>
- string 字符串<br>
- 不支持char类型<br>

## 其他
- Keyword：if, else, while, for, print, scan, int, double, bool, string, void, return
- Operator：+, -, *, /, %, =, ==, <, >, <=, >=, !=, !, ||, &&, (, ), [, ]
- Separator：{, }, ;, ,
- Other：", '
- Command：//, /*, */
- Identifier： 由数字、字母和下划线组成，但必须以字母开头
- Value: NUMBER, BoolValue, String_value

---
# 词法规则
- Integer<br>
  digit ::= [0-9]<br>
  integer ::= [1-9][0-9]*<br>
- Real<br>
  real ::= integer.digit+<br>
- Boolean<br>
  boolean ::= true|false<br>
- String<br>
  char ::= ASCII码能表示的所有字符<br>
  literal ::= char*<br>
- Identifier<br>
  alphabet ::= [a-zA-Z]<br>
  id ::= alphabet(alphabet|digit|_)*<br>
- Keyword

---
# 语法规则
