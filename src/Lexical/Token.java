package Lexical;

public class Token {
    private String name;
    private Type type;
    private int lines;
    private String value;

    public Token(String name, Type type, int lines)
    {
        this.name = name;
        this.type = type;
        this.lines = lines;
        value = name;
    }

    public Token(String name, Type type, int lines, String value)
    {
        this(name, type, lines);
        this.value = value;
    }

    public int getLines() {
        return lines;
    }

    public void setLines(int lines) {
        this.lines = lines;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString()
    {
        return String.format("Line %2d: <Name: %s, Type: %s, Value: %s>", lines, name, type.toString(), value);
    }
}
