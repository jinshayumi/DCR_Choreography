package models.parser;
import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.Expression;
import services.entities.BoolData;
import services.entities.Data;
import services.entities.IntData;
import services.entities.StringData;

import java.util.HashMap;
import java.util.List;

public class ParseExample {
    static Data calculate(HashMap<String, Data> map, Expression expression){
        // variable.
        if (expression.isNameExpr()){
            return map.get(expression.toString());
        }
        // boolean.
        else if (expression.isBooleanLiteralExpr()){
            return new BoolData(expression.asBooleanLiteralExpr().getValue());
        }
        // int.
        else if (expression.isIntegerLiteralExpr()){
            return new IntData(expression.asIntegerLiteralExpr().asInt());
        }
        // string.
        else if (expression.isStringLiteralExpr()){
            return new StringData(expression.asStringLiteralExpr().getValue());
        }
        // unary: not.
        else if (expression.isUnaryExpr()){
            BoolData boolData = (BoolData) calculate(map, expression.asUnaryExpr().getExpression());
            assert boolData != null;
            return new BoolData(!boolData.getData());
        }

        // bracket: ().
        else if (expression.isEnclosedExpr()){
            return calculate(map, expression.asEnclosedExpr().getInner());
        }
        else if (expression.isBinaryExpr()){
            String op = expression.asBinaryExpr().getOperator().asString();
            Expression left = expression.asBinaryExpr().getLeft();
            Expression right = expression.asBinaryExpr().getRight();
            if (op.equals("==")){
                Data leftData = calculate(map, left);
                // int == int
                if(leftData.getClass().getSimpleName().equals("IntDta")){
                    Integer leftVal = ((IntData) calculate(map, left)).getData();
                    Integer rightVal = ((IntData) calculate(map, right)).getData();
                    return new BoolData(leftVal==rightVal);
                }
                // string == string.
                else {
                    String leftVal = ((StringData) calculate(map, left)).getData();
                    String rightVal = ((StringData) calculate(map, right)).getData();
                    return new BoolData(leftVal.equals(rightVal));
                }
            }
            else if (op.equals("<")){
                Integer leftVal = ((IntData) calculate(map, left)).getData();
                Integer rightVal = ((IntData) calculate(map, right)).getData();
                return new BoolData(leftVal<rightVal);
            }
            else if (op.equals(">")){
                Integer leftVal = ((IntData) calculate(map, left)).getData();
                Integer rightVal = ((IntData) calculate(map, right)).getData();
                return new BoolData(leftVal>rightVal);
            }
            else if (op.equals("<=")){
                Integer leftVal = ((IntData) calculate(map, left)).getData();
                Integer rightVal = ((IntData) calculate(map, right)).getData();
                return new BoolData(leftVal<=rightVal);
            }
            else if (op.equals(">=")){
                Integer leftVal = ((IntData) calculate(map, left)).getData();
                Integer rightVal = ((IntData) calculate(map, right)).getData();
                return new BoolData(leftVal>=rightVal);
            }
            else if (op.equals("+")){
                Data leftData = calculate(map, left);
                // int + int
                if(leftData.getClass().getSimpleName().equals("IntDta")){
                    Integer leftVal = ((IntData) calculate(map, left)).getData();
                    Integer rightVal = ((IntData) calculate(map, right)).getData();
                    return new IntData(leftVal+rightVal);
                }
                // string + string.
                else {
                    String leftVal = ((StringData) calculate(map, left)).getData();
                    String rightVal = ((StringData) calculate(map, right)).getData();
                    return new StringData(leftVal+rightVal);
                }
            }
            else if (op.equals("-")){
                Integer leftVal = ((IntData) calculate(map, left)).getData();
                Integer rightVal = ((IntData) calculate(map, right)).getData();
                return new IntData(leftVal-rightVal);
            }
            else if (op.equals("*")){
                Integer leftVal = ((IntData) calculate(map, left)).getData();
                Integer rightVal = ((IntData) calculate(map, right)).getData();
                return new IntData(leftVal*rightVal);
            }
            else if (op.equals("&&")){
                Boolean leftVal = ((BoolData) calculate(map, left)).getData();
                Boolean rightVal = ((BoolData) calculate(map, right)).getData();
                return new BoolData(leftVal&&rightVal);
            }
            else if (op.equals("||")){
                Boolean leftVal = ((BoolData) calculate(map, left)).getData();
                Boolean rightVal = ((BoolData) calculate(map, right)).getData();
                return new BoolData(leftVal||rightVal);
            }
        }

        else if (expression.isConditionalExpr()){
            Boolean condition = ((BoolData) calculate(map, expression.asConditionalExpr().getCondition())).getData();
            if (condition){
                return calculate(map, expression.asConditionalExpr().getThenExpr());
            }
            else return calculate(map, expression.asConditionalExpr().getElseExpr());
        }
        return null;
    }

    public static void main(String[] args) {
        HashMap<String, Data> map = new HashMap<>();
        map.put("Input_decision", new StringData("AskAgain"));
        map.put("b", new IntData(2));
        Expression expression = StaticJavaParser.parseExpression("!(Input_decision==\"AskAgain\")");
        System.out.println(expression.getClass().getSimpleName());
        System.out.println(expression.toString());
        System.out.println(calculate(map, expression).getData());

    }
}
