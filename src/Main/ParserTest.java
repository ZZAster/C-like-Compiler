package Main;

import Lexical.LexicalException;
import Lexical.Scan;
import Parse.Node;
import Parse.Parser;
import Parse.ParseException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

public class ParserTest {
    public static void main(String[] args) throws LexicalException, IOException, ParseException
    {
        Scan scan = new Scan("source.cmm");
        Parser parser = new Parser(scan.getTokens());
        LinkedList<Node> NodeList = parser.getProgram();

        File file = new File("Parse.xml");
        try(PrintWriter output = new PrintWriter(file))
        {
            for (Node node : NodeList)
            {
                output.print(node.toString());
                output.print("\n");
            }
        }

    }
}
