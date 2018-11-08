package Main;

import Lexical.*;
import Parse.*;

import java.io.IOException;

public class MyRunner {
    public static void main(String[] args) throws IOException
    {
        Scan scan = new Scan("source.cmm");
        Parser parser = new Parser();
        System.out.println(parser.test(scan.getTokens()));
    }
}
