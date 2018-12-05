package Interpret;

import Lexical.Token;
import Lexical.Type;
import Parse.Inner;
import Parse.Leaf;
import Parse.Node;
import Parse.NodeType;

public class Calculate {
    public Value getNodeValue(Node node)
    {
        Value value = new Value();
        switch (node.getNodeType())
        {
            case LOG_EXPR:
                Type operation = ((Leaf) ((Inner) node).getMiddle()).getToken().getType();
                boolean a = getNodeValue(((Inner) node).getLeft()).getValue() != 0.0;
                boolean b = getNodeValue(((Inner) node).getRight()).getValue() != 0.0;
                if (operation == Type.AND)
                {
                    if (a && b)
                        value.setValue(1);
                    else
                        value.setValue(0);
                }
                else if (operation == Type.OR)
                {
                    if (a||b)
                        value.setValue(1);
                    else
                        value.setValue(0);
                }
                value.setType(Value.INT);
                break;
            case JUD_EXPR:
            case COM_EXPR:
                double c = getNodeValue(((Inner) node).getLeft()).getValue();
                double d = getNodeValue(((Inner) node).getRight()).getValue();
                boolean result = false;
                switch (((Leaf) ((Inner) node).getMiddle()).getToken().getType())
                {
                    case NOT_EQUAL:
                        result = c != d;
                        break;
                    case EQUAL:
                        result = c == d;
                        break;
                    case LEFT_EQUAL:
                        result = c <= d;
                        break;
                    case LEFT_THAN:
                        result = c < d;
                        break;
                    case RIGHT_EQUAL:
                        result = c >= d;
                        break;
                    case RIGHT_THAN:
                        result = c > d;
                        break;
                    //todo 这里应该有一个default用于报错，但是检查后觉得没有报错的必要，因此用初始值替代default
                }
                if (result)
                    value.setValue(1);
                else
                    value.setValue(0);
                value.setType(Value.INT);
                break;
            case ADD_EXPR:
            case MUL_EXPR:
                Value e = getNodeValue(((Inner) node).getLeft());
                Value f = getNodeValue(((Inner) node).getRight());
                double number = 0;
                switch (((Leaf) ((Inner) node).getMiddle()).getToken().getType())
                {
                    case ADD:
                        number = e.getValue() + f.getValue();
                        break;
                    case SUB:
                        number = e.getValue() - f.getValue();
                        break;
                    case DIV:
                        number = e.getValue() / f.getValue();
                        break;
                    case MUL:
                        number = e.getValue() * f.getValue();
                        break;
                    case MOD:
                        if (e.getType() == Value.INT && f.getType() == Value.INT)
                            number = e.getValue() % f.getValue();
                        else
                            //todo 这里进行语义报错（%运算符不能用于double）
                        break;
                    // todo 同上，这里应该报错
                }
                if (e.getType() == Value.INT && f.getType() == Value.INT)
                {
                    number = (double) ((int) number);
                    value.setType(Value.INT);
                    value.setValue(number);
                }
                else
                {
                    value.setType(Value.DOUBLE);
                    value.setValue(number);
                }
                break;
            case OPERAND:
                Value g = getNodeValue(((Inner) node).getLeft());
                double operand = 0;
                int type = g.getType();
                switch (((Leaf) ((Inner) node).getMiddle()).getToken().getType())
                {
                    case NOT:
                        if (g.getValue() != 0.0)
                            operand = 0;
                        else
                            operand = 1;
                        type = Value.INT;
                        break;
                    case POSITIVE:
                        operand = g.getValue();
                        break;
                    case NEGATIVE:
                        operand = (-1) * g.getValue();
                        break;
                    // todo 同上，这里应该报错
                }
                value.setType(type);
                value.setValue(operand);
                break;
            case NUMBER:
                Token token = ((Leaf) node).getToken();
                String literal = token.getValue();
                if (literal.contains("."))
                {
                    value.setType(Value.DOUBLE);
                    value.setValue(Double.parseDouble(literal));
                }
                else
                {
                    value.setType(Value.INT);
                    value.setValue(Integer.parseInt(literal));
                }
                break;
            //todo 这三个结点都涉及了变量，无法在单独的表达式中计算
            case VARIABLE:
            case IDENTIFIER:
            case NUM_ASSIGN:
        }
        return value;
    }
}
