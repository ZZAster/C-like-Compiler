package Lexical;

public class LexicalException extends Exception {
    public LexicalException()
    {
        super();
    }

    public LexicalException(String filename, int pos, int line, char ch)
    {
        super(String.format("File <%s>, Line %-2d Pos %-2d:\n\tLexical Error: Invalid characters '%c'.",
                filename, line, pos, ch));
    }
}
