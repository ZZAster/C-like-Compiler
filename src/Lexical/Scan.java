package Lexical;

import java.io.*;
import java.util.LinkedList;

public class Scan {
    private InputStream input;
    private int lines;
    private int fline;
    private int pos;
    private int fpos;
    private int trans;
    private char ch;
    private char fch;
    private String filename;
    private LinkedList<Token> tokens = new LinkedList<>();

    private String[] keywords = {"if", "else", "while", "for", "print", "scan", "int", "double",
            "bool", "string", "void", "return"};

    public Scan(String filename) throws IOException {
        input = new BufferedInputStream(new FileInputStream(filename));
        lines = 1;
        this.filename = new File(filename).getName();
    }

    public LinkedList<Token> getTokens() throws IOException {
        StringBuilder temp = new StringBuilder();
        readChar();
        while (trans != -1) {
            if (ch == ' ' || ch == '\n' || ch == '\t' || ch == '\r' || ch == '\f') {
                readChar();
                continue;
            }
            setFirst();
            //扫描是否是操作符、分割符
            switch (ch) {
                case '+':
                    Type plus = tokens.getLast().getType();
                    if (isOperand(plus))
                        plus = Type.ADD;
                    else
                        plus = Type.POSITIVE;
                    tokens.add(new Token("+", plus, lines, pos));
                    readChar();
                    continue;
                case '-':
                    Type minus = tokens.getLast().getType();
                    if (isOperand(minus))
                        minus = Type.SUB;
                    else
                        minus = Type.NEGATIVE;
                    tokens.add(new Token("-", minus, lines, pos));
                    readChar();
                    continue;
                case '*':
                    tokens.add(new Token("*", Type.MUL, lines, pos));
                    readChar();
                    continue;
                case '%':
                    tokens.add(new Token("%", Type.MOD, lines, pos));
                    readChar();
                    continue;
                case '(':
                    tokens.add(new Token("(", Type.LEFT_PARENT, lines, pos));
                    readChar();
                    continue;
                case ')':
                    tokens.add(new Token(")", Type.RIGHT_PARENT, lines, pos));
                    readChar();
                    continue;
                case '{':
                    tokens.add(new Token("{", Type.LEFT_BRACE, lines, pos));
                    readChar();
                    continue;
                case '}':
                    tokens.add(new Token("}", Type.RIGHT_BRACE, lines, pos));
                    readChar();
                    continue;
                case '[':
                    tokens.add(new Token("[", Type.LEFT_BRACKET, lines, pos));
                    readChar();
                    continue;
                case ']':
                    tokens.add(new Token("]", Type.RIGHT_BRACKET, lines, pos));
                    readChar();
                    continue;
                case ';':
                    tokens.add(new Token(";", Type.SEMICOLON, lines, pos));
                    readChar();
                    continue;
                case ',':
                    tokens.add(new Token(",", Type.COMMA, lines, pos));
                    readChar();
                    continue;
                case '=':
                    readChar();
                    if (ch == '=')
                        tokens.add(new Token("==", Type.EQUAL, lines, pos));
                    else
                        tokens.add(new Token("=", Type.ASSIGN, lines, pos));
                    continue;
                case '<':
                    readChar();
                    if (ch == '=')
                        tokens.add(new Token("<=", Type.LEFT_EQUAL, lines, pos));
                    else
                        tokens.add(new Token("<", Type.LEFT_THAN, lines, pos));
                    continue;
                case '>':
                    readChar();
                    if (ch == '=')
                        tokens.add(new Token(">=", Type.RIGHT_EQUAL, lines, pos));
                    else
                        tokens.add(new Token(">", Type.RIGHT_THAN, lines, pos));
                    continue;
                case '!':
                    readChar();
                    if (ch == '=')
                        tokens.add(new Token("!=", Type.NOT_EQUAL, lines, pos));
                    else
                        tokens.add(new Token("!", Type.NOT, lines, pos));
                    continue;
                case '|':
                    setFirst();
                    readChar();
                    if (ch == '|')
                        tokens.add(new Token("||", Type.OR, lines, pos));
                    else
                        lexicalException();
                    continue;
                case '&':
                    setFirst();
                    readChar();
                    if (ch == '&')
                        tokens.add(new Token("&&", Type.AND, lines, pos));
                    else
                        lexicalException();
                    continue;
            }
            if (ch == '/') {
                readChar();
                if (ch == '/') {
                    readLine();
                    continue;
                } else if (ch == '*') {
                    setFirst();
                    readChar();
                    while (true) {
                        if (trans == -1)
                            lexicalException();
                        if (ch == '*') {
                            readChar();
                            if (ch == '/') {
                                readChar();
                                break;
                            }
                        } else
                            readChar();
                    }
                    continue;
                } else {
                    tokens.add(new Token("/", Type.DIV, lines, pos));
                    continue;
                }
            }
            //扫描是否是关键字, 标识符, true, false
            if (Character.isAlphabetic(ch)) {
                while (Character.isDigit(ch) || Character.isAlphabetic(ch) || ch == '_') {
                    temp.append(ch);
                    readChar();
                }
                int i;
                String s = temp.toString();
                if (s.equals("true") || s.equals("false"))
                    tokens.add(new Token(s, Type.BOOLEAN, lines, pos));
                else if ((i = isKeyword(s)) >= 0)
                    tokens.add(new Token(s, Type.values()[i], lines, pos));
                else
                    tokens.add(new Token(s, Type.IDENTIFIER, lines, pos));
                temp.delete(0, temp.length());//清空临时字符串
                continue;
            }
            //扫描是否是字符串
            if (ch == '\"') {
                setFirst();
                tokens.add(new Token("\"", Type.DOUBLE_QUOTATION, lines, pos));
                readChar();
                boolean flag = false; // 记录是否出现转义符'\'
                while (ch != '\"' || flag) {
                    if (trans == -1 || ch == '\n')
                        lexicalException();
                    if (ch == '\\' && !flag)
                        flag = true;
                    else
                        flag = false;
                    temp.append(ch);
                    readChar();
                }
                tokens.add(new Token(temp.toString(), Type.STRING_VALUE, lines, pos, escape_trans(temp)));
                tokens.add(new Token("\"", Type.DOUBLE_QUOTATION, lines, pos));
                temp.delete(0, temp.length());
                readChar();
                continue;
            }
            //扫描是否是数值
            if (Character.isDigit(ch)) {
                setFirst();
                while (Character.isDigit(ch)) {
                    temp.append(ch);
                    readChar();
                }
                if (temp.charAt(0) == '0' && temp.length() > 1)
                    lexicalException();
                if (ch == '.') {
                    temp.append(ch);
                    readChar();
                    if (!Character.isDigit(ch)) {
                        setFirst();
                        lexicalException();
                    }
                    while (Character.isDigit(ch)) {
                        temp.append(ch);
                        readChar();
                    }
                    setFirst();
                    if (Character.isAlphabetic(ch) && Character.toLowerCase(ch) != 'e')
                        lexicalException();
                    if (Character.toLowerCase(ch) != 'e')
                    {
                        tokens.add(new Token(temp.toString(), Type.NUMBER, lines, pos));
                        temp.delete(0, temp.length());
                        continue;
                    }
                }
                if (ch == 'e' || ch == 'E') {
                    setFirst();
                    readChar();
                    if (Character.isAlphabetic(ch))
                        lexicalException();
                    if (Character.isDigit(ch) || ch == '-' || ch == '+') {
                        temp.append(fch);
                        temp.append(ch);
                        readChar();
                        while (Character.isDigit(ch)) {
                            temp.append(ch);
                            readChar();
                        }
                        if (Character.isAlphabetic(ch)) {
                            setFirst();
                            lexicalException();
                        }
                        String value = getRealValue(temp);
                        tokens.add(new Token(temp.toString(), Type.NUMBER, lines, pos, value));
                    } else
                        lexicalException();
                } else {
                    setFirst();
                    if (Character.isAlphabetic(ch))
                        lexicalException();
                    tokens.add(new Token(temp.toString(), Type.NUMBER, lines, pos));
                }
                temp.delete(0, temp.length());
                continue;
            }
            lexicalException();
        }
        input.close();
        return tokens;
    }

    private String getRealValue(StringBuilder stringBuilder) {
        int i;
        if ((i = stringBuilder.indexOf("e")) == -1)
            i = stringBuilder.indexOf("E");
        String double_num = stringBuilder.substring(0, i);
        String little = stringBuilder.substring(i + 1);
        double p = Double.parseDouble(double_num);
        double q = Double.parseDouble(little);
        double value = p * Math.pow(10, q);
        return "" + value;
    }

    private int isKeyword(String s) {
        for (int i = 0; i < keywords.length; i++)
            if (s.equals(keywords[i]))
                return i;
        return -1;
    }

    private boolean isOperand(Type type)
    {
        return (type == Type.NUMBER || type == Type.RIGHT_PARENT || type == Type.IDENTIFIER);
    }

    private String escape_trans(StringBuilder origin) throws IOException
    {
        StringBuilder output = new StringBuilder();
        // 对\", \t, \n, \\进行转化
        for (int i = 0; i < origin.length(); i++)
        {
            if (origin.charAt(i) == '\\')
            {
                char curr = origin.charAt(++i);
                switch (curr){
                    case '"':
                        output.append('\"');
                        break;
                    case 't':
                        output.append('\t');
                        break;
                    case 'n':
                        output.append('\n');
                        break;
                    case '\\':
                        output.append('\\');
                        break;
                    default:
                        fch = curr;
                        fpos += i+1;
                        lexicalException();
                }
            }
            else
                output.append(origin.charAt(i));
        }
        return output.toString();
    }

    private void setFirst()
    {
        fpos = pos;
        fch = ch;
        fline = lines;
    }

    private void readChar() throws IOException
    {
        ch = (char) (trans = input.read());
        if(ch != '\r')
            pos++;
        if (ch == '\n')
        {
            lines++;
            pos = 0;
        }
    }

    private void readLine() throws IOException
    {
        while(ch != '\n')
            readChar();
    }

    private void lexicalException() throws IOException
    {
        System.err.printf("File <%s>, Line %-2d Pos %-2d:\n\tLexical Error: Invalid characters '%c'.",
                filename, fline, fpos, fch);
        input.close();
        System.exit(1);
    }
}
