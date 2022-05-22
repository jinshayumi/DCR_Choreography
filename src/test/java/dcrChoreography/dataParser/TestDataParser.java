package dcrChoreography.dataParser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.Expression;
import models.parser.ExpParser;
import org.junit.Test;
import services.entities.data.BoolData;
import services.entities.data.Data;
import services.entities.data.IntData;
import services.entities.data.StringData;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class TestDataParser {
    // parse and calculate const values.
    @Test
    public void testDataParser0(){
        Data intConst = ExpParser.calculate(new HashMap<>(), ExpParser.parseExp("30"));
        Data stringConst = ExpParser.calculate(new HashMap<>(), ExpParser.parseExp("\"30\""));
        Data boolConst = ExpParser.calculate(new HashMap<>(), ExpParser.parseExp("true"));

        assertEquals(intConst, new IntData(30));
        assertEquals(stringConst, new StringData("30"));
        assertEquals(boolConst, new BoolData(true));
    }

    // parse and calculate variables.
    @Test
    public void testDataParser1(){
        HashMap<String, Data> map = new HashMap<>();
        map.put("A", new IntData(25));
        map.put("B", new StringData("hello"));
        map.put("C", new BoolData(false));

        Data intVar = ExpParser.calculate(map, ExpParser.parseExp("A"));
        Data stringVar = ExpParser.calculate(map, ExpParser.parseExp("B"));
        Data boolVar = ExpParser.calculate(map, ExpParser.parseExp("C"));

        assertEquals(intVar, new IntData(25));
        assertEquals(stringVar, new StringData("hello"));
        assertEquals(boolVar, new BoolData(false));
    }

    // parse and calculate integer operators.
    @Test
    public void testDataParser2(){
        HashMap<String, Data> map = new HashMap<>();
        map.put("a", new IntData(1));
        map.put("b", new IntData(2));
        map.put("c", new IntData(3));

        String exp1 = "a==c";
        String exp2 = "a<b";
        String exp3 = "b >=c";
        String exp4 = "(a+b)*c +1";
        Data boolVar1 = ExpParser.calculate(map, ExpParser.parseExp(exp1));
        Data boolVar2 = ExpParser.calculate(map, ExpParser.parseExp(exp2));
        Data boolVar3 = ExpParser.calculate(map, ExpParser.parseExp(exp3));
        Data intVar4 = ExpParser.calculate(map, ExpParser.parseExp(exp4));

        assertEquals(boolVar1, new BoolData(false));
        assertEquals(boolVar2, new BoolData(true));
        assertEquals(boolVar3, new BoolData(false));
        assertEquals(intVar4, new IntData(10));

    }

    // parse and calculate string operators.
    @Test
    public void testDataParser3(){
        HashMap<String, Data> map = new HashMap<>();
        map.put("a", new StringData("hello"));
        map.put("b", new StringData(", "));
        map.put("c", new StringData("world"));

        String exp1 = "a==c";
        String exp2 = "a+b+c+ \"!\"";
        Data boolVar1 = ExpParser.calculate(map, ExpParser.parseExp(exp1));
        Data stringVar2 = ExpParser.calculate(map, ExpParser.parseExp(exp2));

        assertEquals(boolVar1, new BoolData(false));
        assertEquals(stringVar2, new StringData("hello, world!"));
    }

    // parse and calculate string operators.
    @Test
    public void testDataParser4(){
        HashMap<String, Data> map = new HashMap<>();
        map.put("a", new StringData("hello"));
        map.put("b", new StringData(", "));
        map.put("c", new IntData(3));

        String exp1 = "(a==b)&&true";
        String exp2 = "(c==3)||false";
        Data boolVar1 = ExpParser.calculate(map, ExpParser.parseExp(exp1));
        Data boolVar2 = ExpParser.calculate(map, ExpParser.parseExp(exp2));

        assertEquals(boolVar1, new BoolData(false));
        assertEquals(boolVar2, new BoolData(true));
    }

    // test NOT and BRANCH
    @Test
    public void testDataParser5(){
        HashMap<String, Data> map = new HashMap<>();
        map.put("a", new StringData("hello"));
        map.put("b", new StringData(", "));
        map.put("c", new IntData(3));

        String exp1 = "(a==b)?true:3";
        String exp2 = "!(c==3)||false";
        Data boolVar1 = ExpParser.calculate(map, ExpParser.parseExp(exp1));
        Data boolVar2 = ExpParser.calculate(map, ExpParser.parseExp(exp2));

        assertEquals(boolVar1, new IntData(3));
        assertEquals(boolVar2, new BoolData(false));
    }

    // test mismatched type 0
    @Test(expected = ClassCastException.class)
    public void testDataParser6(){
        HashMap<String, Data> map = new HashMap<>();
        map.put("a", new StringData("hello"));
        map.put("b", new StringData(", "));
        map.put("c", new IntData(3));

        String exp1 = "(a==c)";

        ExpParser.calculate(map, ExpParser.parseExp(exp1));
    }

    // test mismatched type 1
    @Test(expected = ClassCastException.class)
    public void testDataParser7(){
        HashMap<String, Data> map = new HashMap<>();
        map.put("a", new StringData("hello"));
        map.put("b", new StringData(", "));
        map.put("c", new IntData(3));

        String exp2 = "b?c:a";

        ExpParser.calculate(map, ExpParser.parseExp(exp2));
    }

    // test mismatched type 2
    @Test(expected = ClassCastException.class)
    public void testDataParser8(){
        HashMap<String, Data> map = new HashMap<>();
        map.put("a", new StringData("hello"));
        map.put("b", new BoolData(true));
        map.put("c", new IntData(3));

        String exp2 = "!b?a+c:a-c";

        ExpParser.calculate(map, ExpParser.parseExp(exp2));
    }

    // Combination test 0
    @Test
    public void testDataParser9(){
        HashMap<String, Data> map = new HashMap<>();
        map.put("Quote1", new IntData(25));
        map.put("Quote2", new IntData(15));
        Expression expression = StaticJavaParser.parseExpression(
                "(Quote1<20&&Quote2<20)?((Quote1<Quote2)?(\"accept1\"):" +
                        "(\"accept1\")):((Quote1<20)?(\"accept1\"):" +
                        "((Quote2<20)?(\"accept2\"):(\"reject\")))");

        Data calculated = ExpParser.calculate(map, expression);
        assertEquals(calculated, new StringData("accept2"));
    }

    // Combination test 1
    @Test
    public void testDataParser10(){
        HashMap<String, Data> map = new HashMap<>();
        map.put("Quote1", new IntData(25));
        map.put("Quote2", new IntData(15));
        map.put("c", new StringData("c"));
        map.put("d", new BoolData(true));
        Expression expression = StaticJavaParser.parseExpression(
                "!(Quote1<20&&Quote2<20)?c:d");

        Data calculated = ExpParser.calculate(map, expression);
        assertEquals(calculated, new StringData("c"));
    }

    // Combination test 1
    @Test(expected = ClassCastException.class)
    public void testDataParser11(){
        HashMap<String, Data> map = new HashMap<>();
        map.put("Quote1", new IntData(25));
        map.put("Quote2", new IntData(15));
        map.put("c", new StringData("c"));
        map.put("d", new BoolData(true));
        Expression expression = StaticJavaParser.parseExpression(
                "!(Quote1<20&&Quote2<=20)?c-d:d+a");

        Data calculated = ExpParser.calculate(map, expression);
        assertEquals(calculated, new StringData("c"));
    }
}
