package Interpret;

public class Value {
    public static final int DOUBLE = 1;
    public static final int INT = 0;

    private int type;
    private double value;

    public Value(int type, double value)
    {
        this.type = type;
        this.value = value;
    }

    public Value()
    {
        this(DOUBLE, 0);
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public double getValue()
    {
        return value;
    }

    public void setValue(double value)
    {
        this.value = value;
    }
}
