package Parse;

import Lexical.Token;

public class ParseException extends Exception {
    public ParseException()
    {
        super();
    }

    public ParseException(Token token, String type)
    {
        super(String.format("Wrong Token %s At <Line:%3d, Pos:%3d>: Token here should be %s",
                token.getName(), token.getLines(), token.getPos(), type));
    }
}
