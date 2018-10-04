package Lexical;

import java.io.IOException;
import java.util.LinkedList;

public class Analyzer {
    public static void main(String[] args) throws IOException
    {
        Scan scan = new Scan("src\\source.cmm");
        printToken(scan.getTokens());
    }

    public static void printToken(LinkedList<Token> tokens)
    {
        for (Token token : tokens)
            System.out.println(token);
    }
}
