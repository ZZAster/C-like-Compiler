# C-like-Complier
# C-like 语言
## 语言结构
- 顺序结构：赋值（=），输入（scan()），输出（print()）<br>
- 选择语句：if-else (允许使用级联形式)<br>
- 循环结构：for, while<br>

## 表达式
- 关系表达式：<, >, ==, !=, <=, >=<br>
- 算术表达式: +, -, *, /, %, ()<br>
- 逻辑表达式：||, &&<br>
- 一元表达式：!, +, -<br>
- 不支持进行位运算（|, &, ~）<br>
- 不支持三元运算符（? :）<br>

## 程序结构
- 程序以函数为单位，main函数为程序入口<br>
- 不支持在函数块外定义语句<br>
- 语句以;作为结束，{}表示一个程序块<br>
- 支持单行和多行注释 /*, //<br>
- 变量使用前必须定义<br>
- 函数使用前必须声明<br>
- 支持数组运算，不支持指针和取地址运算，[]表示数组下标<br>

## 数据类型
- int 整型 （只支持十进制数）<br>
- double 浮点型（支持科学计数法）<br>
- 不支持char类型<br>

---
# 词法
- Integer<br>
  digit ::= [0-9]<br>
  integer ::= [1-9][0-9]*<br>
- Real<br>
  real ::= integer.digit+<br>
- String<br>
  char ::= ASCII码能表示的所有字符<br>
  literal ::= char*<br>
- Identifier<br>
  alphabet ::= [a-zA-Z]<br>
  id ::= alphabet(alphabet|digit|_)*<br>

- Keyword：if, else, while, for, print, scan, int, double, void, return, do
- Operator：+, -, *, /, %, =, ==, <, >, <=, >=, !=, !, ||, &&, (, ), [, ]
- Separator：{, }, ;, ", ,
- Command：//, /*, */
- Identifier： 由数字、字母和下划线组成，但必须以字母开头
- Value: number, string_literal

---
# 语法
## 程序
- program -> (func-declare)+

##函数
- func-declare -> func-sign func-define
- func-define -> { stmt-sequence } | ;
- func-sign -> type identifier ( arg-dec-list )
- arg-dec-list -> arg-declare arg-dec-clos | void | ε
- arg-dec-clos -> , arg-declare arg-dec-clos | ε
- arg-declare -> type identifier
- declare-stmt -> type identifier [initialize]
- type -> prim-type | prim-type [ sub ]
- sub -> number | ε
- prim-type -> int | double
- initialize -> = lvalue
- lvalue -> exp | { exps }
- exps -> exp exp-clos
- exp-clos -> , exp exp-clos | ε

##语句
- stmt-sequence -> statement stmt-sequence | ε
- statement -> if-stmt | while-stmt | assign-stmt ; | read-stmt ; | write-stmt ;<br>
			   declare-stmt ; | return-stmt ; | call-stmt ; | for-stmt ; | ;
- assign-stmt -> variable = assign-stmt | lvalue
- write-stmt -> print ( string | exp )
- read-stmt -> scan ( variable )
- call-stmt -> identifier ( arg-list )
- arg-list -> lvalue arg-list | ε
- return-stmt -> return exp
- if-stmt -> if ( exp ) if-block
- if-block -> stmt-block [else stmt-block]
- stmt-block -> statement | { stmt-sequence }
- while-stmt -> while ( exp ) stmt-block | do stmt-block while ( exp ) ;
- for-stmt -> for ( for-list ) stm-block
- for-list -> for-init ; condition ; change
- for-init -> declare-stmt | assign-stmt
- condition -> exp
- change -> identifier = exp

##表达式
- exp -> log-expr | add-expr
- log-expr -> log-expr logical com-expr | com-expr
- com-expr -> com-expr compare jud-expr | jud-expr
- jud-expr -> jud-expr islog-exprqual add-expr | add-expr
- add-expr -> add-expr addative mul-expr | mul-expr
- mul-expr -> mul-expr multiple operand | operand
- operand -> ( add-expr ) | number | variable | unary operand | ( num-assign ) | call-stmt

##运算符
- logical -> || | &&
- islog-exprqual -> != | ==
- compare -> > | < | >= | <=
- multiple -> * | / | %
- addative -> + | -
- unary -> ! | + | -

##说明
- 函数应支持先声明后定义的形式
- 参数列表以int,double以及数组为参数
- 参数列表中的数组下标可以为空或数字
- 参数列表为空时可以用(void)或者()表示
- if语句，支持级联形式
- while语句，支持do-while形式
- switch, break和continue暂时还未加入
- 声明数组时形式为type[] identifier, 暂不支持类似于int a[]的形式
- 数组赋值时支持表达式和变量形式，如int[] b = {a, 1+2}
- 暂定所有变量声明时都有默认初始值0
- 不支持将逗号作为运算符，如int a, b;以及a = b=2, c=3;等形式
- 支持逻辑表达式与算术表达式结果之间的转换（0代表true, 非0代表false，true代表1，flase代表0）
