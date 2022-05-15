package models.parser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.Expression;
import services.entities.BoolData;
import services.entities.Data;
import services.entities.IntData;
import services.entities.StringData;

import java.util.HashMap;
import java.util.HashSet;

public class ExpParser {
    public static Data calculate(HashMap<String, Data> map, Expression expression){
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
                if(leftData.getClass().getSimpleName().equals("IntData")){
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
                if(leftData.getClass().getSimpleName().equals("IntData")){
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

        // conditional expression.
        else if (expression.isConditionalExpr()){
            Boolean condition = ((BoolData) calculate(map, expression.asConditionalExpr().getCondition())).getData();
            if (condition){
                return calculate(map, expression.asConditionalExpr().getThenExpr());
            }
            else return calculate(map, expression.asConditionalExpr().getElseExpr());
        }
        return null;
    }

    public static Expression parseExp(String exp){
        return StaticJavaParser.parseExpression(exp);
    }

    // get all the events mentioned in an expression.
    public static HashSet<String> getEventsInExpression(Expression expression) {
        if (expression.isNameExpr()){
            HashSet<String> res = new HashSet<>();
            res.add(expression.toString());
            return res;
        }
        else if (expression.isBooleanLiteralExpr()
                ||expression.isIntegerLiteralExpr()
                ||expression.isStringLiteralExpr()){
            return new HashSet<>();
        }
        else if (expression.isUnaryExpr()){
            return getEventsInExpression(expression.asUnaryExpr().getExpression());
        }
        else if (expression.isEnclosedExpr()){
            return getEventsInExpression(expression.asEnclosedExpr().getInner());
        }
        else if (expression.isBinaryExpr()){
            Expression left = expression.asBinaryExpr().getLeft();
            Expression right = expression.asBinaryExpr().getRight();
            HashSet<String> res = new HashSet<>();
            HashSet<String> leftRes = getEventsInExpression(left);
            HashSet<String> rightRes = getEventsInExpression(right);
            res.addAll(leftRes);
            res.addAll(rightRes);
            return res;
        }
        else if (expression.isConditionalExpr()){
            Expression condition = expression.asConditionalExpr().getCondition();
            Expression thenExp = expression.asConditionalExpr().getThenExpr();
            Expression elseExp = expression.asConditionalExpr().getElseExpr();
            HashSet<String> res = new HashSet<>();
            HashSet<String> conditionRes = getEventsInExpression(condition);
            HashSet<String> thenRes = getEventsInExpression(thenExp);
            HashSet<String> elseRes = getEventsInExpression(elseExp);
            res.addAll(conditionRes);
            res.addAll(thenRes);
            res.addAll(elseRes);
            return res;
        }
        else return new HashSet<>();
    }

    public static void main(String[] args) throws ClassNotFoundException {
        HashMap<String, Data> map = new HashMap<>();
        map.put("Quote1", new IntData(25));
        map.put("Quote2", new IntData(15));
        Expression expression = StaticJavaParser.parseExpression(
                "(Quote1<20&&Quote2<20)?((Quote1<Quote2)?(\"accept1\"):" +
                        "(\"accept1\")):((Quote1<20)?(\"accept1\"):" +
                        "((Quote2<20)?(\"accept2\"):(\"reject\")))");
        System.out.println(expression.getClass().getSimpleName());
        System.out.println(expression.toString());
        System.out.println(calculate(map, expression).getData());

    }
}
