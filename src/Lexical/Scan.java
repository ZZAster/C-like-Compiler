package Lexical;

import java.io.*;
import java.util.LinkedList;

public class Scan {
     private InputStream input;
     private int lines;
     private int pos;
     private int fpos;
     private int trans;
     private char ch;
     private char fch;
     private String filename;
     private LinkedList<Token> tokens = new LinkedList<>();

     private String[] keywords = {"if", "else", "while", "for", "print", "scan", "int", "double",
                                   "bool", "string", "void", "return"};

    public Scan(String filename) throws IOException
    {
        input = new BufferedInputStream(new FileInputStream(filename));
        lines = 1;
        this.filename = new File(filename).getName();
    }

    public LinkedList<Token> getTokens() throws IOException
    {
        StringBuilder temp = new StringBuilder();
        readChar();
        while(trans != -1)
        {
            if(ch == ' ' || ch == '\n' || ch == '\t'|| ch == '\r' || ch == '\f')
            {
                readChar();
                continue;
            }
            setFirst();
            //扫描是否是操作符、分割符
            switch (ch)
            {
                case '+':
                    tokens.add(new Token("+", Type.ADD, lines));
                    readChar();
                    continue;
                case '-':
                    tokens.add(new Token("-", Type.SUB, lines));
                    readChar();
                    continue;
                case '*':
                    tokens.add(new Token("*", Type.MUL, lines));
                    readChar();
                    continue;
                case '%':
                    tokens.add(new Token("%", Type.MOD, lines));
                    readChar();
                    continue;
                case '(':
                    tokens.add(new Token("(", Type.LEFT_PARENT, lines));
                    readChar();
                    continue;
                case ')':
                    tokens.add(new Token(")", Type.RIGHT_PARENT, lines));
                    readChar();
                    continue;
                case '{':
                    tokens.add(new Token("{", Type.LEFT_BRACE, lines));
                    readChar();
                    continue;
                case '}':
                    tokens.add(new Token("}", Type.RIGHT_BRACE, lines));
                    readChar();
                    continue;
                case '[':
                    tokens.add(new Token("[", Type.LEFT_BRACKET, lines));
                    readChar();
                    continue;
                case ']':
                    tokens.add(new Token("]", Type.RIGHT_BRACKET, lines));
                    readChar();
                    continue;
                case ';':
                    tokens.add(new Token(";", Type.SEMICOLON, lines));
                    readChar();
                    continue;
                case ',':
                    tokens.add(new Token(",", Type.COMMA, lines));
                    readChar();
                    continue;
                case '=':
                    readChar();
                    if (ch == '=')
                        tokens.add(new Token("==", Type.EQUAL, lines));
                    else
                        tokens.add(new Token("=", Type.ASSIGN, lines));
                    continue;
                case '<':
                    readChar();
                    if (ch == '=')
                        tokens.add(new Token("<=", Type.LEFT_EQUAL, lines));
                    else
                        tokens.add(new Token("<", Type.LEFT_THAN, lines));
                    continue;
                case '>':
                    readChar();
                    if (ch == '=')
                        tokens.add(new Token(">=", Type.RIGHT_EQUAL, lines));
                    else
                        tokens.add(new Token(">", Type.RIGHT_THAN, lines));
                    continue;
                case '!':
                    readChar();
                    if (ch == '=')
                        tokens.add(new Token("!=", Type.NOT_EQUAL, lines));
                    else
                        tokens.add(new Token("!", Type.NOT, lines));
                    continue;
                case '|':
                    setFirst();
                    readChar();
                    if (ch == '|')
                        tokens.add(new Token("||", Type.OR, lines));
                    else
                        lexicalException();
                    continue;
                case '&':
                    setFirst();
                    readChar();
                    if (ch == '&')
                        tokens.add(new Token("&&", Type.AND, lines));
                    else
                       lexicalException();
                    continue;
            }
            if (ch == '/')
            {
                readChar();
                if (ch == '/')
                {
                    readLine();
                    continue;
                }
                else if (ch == '*')
                {
                    setFirst();
                    readChar();
                    while(true)
                    {
                        if (trans == -1)
                            lexicalException();
                        if (ch == '*')
                        {
                            readChar();
                            if (ch == '/')
                            {
                                readChar();
                                break;
                            }
                        }
                        else
                            readChar();
                    }
                    continue;
                }
                else
                {
                    tokens.add(new Token("/", Type.DIV, lines));
                    continue;
                }
            }
            //扫描是否是关键字, 标识符, true, false
            if (Character.isAlphabetic(ch))
            {
                while(Character.isDigit(ch) || Character.isAlphabetic(ch) || ch == '_')
                {
                    temp.append(ch);
                    readChar();
                }
                int i;
                String s = temp.toString();
                if (s.equals("true") || s.equals("false"))
                    tokens.add(new Token(s, Type.BOOLEAN, lines));
                else if ((i = isKeyword(s)) >= 0)
                    tokens.add(new Token(s, Type.values()[i], lines));
                else
                    tokens.add(new Token(s, Type.IDENTIFIER, lines));
                temp.delete(0, temp.length());//清空临时字符串
                continue;
            }
            //扫描是否是字符串
            if (ch == '\"')
            {
                setFirst();
                tokens.add(new Token("\"", Type.DOUBLE_QUOTATION, lines));
                readChar();
                boolean flag = false; // 记录是否出现转义符'\'
                while(ch != '\"' || flag)
                {
                    if (trans == -1 || ch == '\n')
                        lexicalException();
                    if (ch == '\\' && !flag)
                        flag = true;
                    else
                        flag = false;
                    temp.append(ch);
                    readChar();
                }
                tokens.add(new Token(temp.toString(), Type.STRING_VALUE, lines));
                tokens.add(new Token("\"", Type.DOUBLE_QUOTATION, lines));
                temp.delete(0, temp.length());
                readChar();
                continue;
            }
            //扫描是否是数值
            if (Character.isDigit(ch))
            {
                setFirst();
                while(Character.isDigit(ch))
                {
                    temp.append(ch);
                    readChar();
                }
                if (temp.charAt(0) == '0' && temp.length() > 1)
                    lexicalException();
                if (ch == '.')
                {
                    temp.append(ch);
                    readChar();
                    if (!Character.isDigit(ch))
                    {
                        setFirst();
                        lexicalException();
                    }
                    while(Character.isDigit(ch))
                    {
                        temp.append(ch);
                        readChar();
                    }
                    setFirst();
                    if (Character.isAlphabetic(ch))
                        lexicalException();
                    tokens.add(new Token(temp.toString(), Type.NUMBER, lines));
                }
                else if (ch == 'e' || ch == 'E')
                {
                    setFirst();
                    readChar();
                    if (Character.isAlphabetic(ch))
                        lexicalException();
                    if (Character.isDigit(ch) || ch == '-' || ch == '+')
                    {
                        temp.append(fch);
                        temp.append(ch);
                        readChar();
                        while(Character.isDigit(ch))
                        {
                            temp.append(ch);
                            readChar();
                        }
                        if (Character.isAlphabetic(ch))
                        {
                            setFirst();
                            lexicalException();
                        }
                        String value = getRealValue(temp);
                        tokens.add(new Token(temp.toString(), Type.NUMBER, lines, value));
                    }
                    else
                        lexicalException();
                }
                else
                {
                    setFirst();
                    if (Character.isAlphabetic(ch))
                        lexicalException();
                    tokens.add(new Token(temp.toString(), Type.NUMBER, lines));
                }
                temp.delete(0, temp.length());
                continue;
            }
            lexicalException();
        }
        input.close();
        return tokens;
    }

    private String getRealValue(StringBuilder stringBuilder)
    {
        int i;
        if ((i = stringBuilder.indexOf("e")) == -1)
            i = stringBuilder.indexOf("E");
        String integer = stringBuilder.substring(0, i);
        String little  = stringBuilder.substring(i + 1);
        int p = Integer.parseInt(integer);
        int q = Integer.parseInt(little);
        double value = p * Math.pow(10, q);
        return "" + value;
    }

    private int isKeyword(String s)
    {
        for (int i = 0; i < keywords.length; i++)
            if (s.equals(keywords[i]))
                return i;
        return -1;
    }

    /* 貌似不需要这个函数
    private boolean isId(String s)
    {
        for (char c : s.toCharArray())
        {
            if (!Character.isAlphabetic(c) && !Character.isDigit(c) && c != '_')
                return false;
        }
        return true;
    }
    */

    private void setFirst()
    {
        fpos = pos;
        fch = ch;
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
                filename, lines, fpos, fch);
        input.close();
        System.exit(1);
    }
}
