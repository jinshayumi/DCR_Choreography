package dcrGraph.conformanceCheck;

import conformanceCheckInterface.Conformance;
import conformanceCheckInterface.Violation;
import org.junit.Test;
import static org.junit.Assert.*;


public class TestConformanceCheck {
    private static final String testConformancePath = "/src/main/resources/test/dcrGraph/conformanceCheck/";

    @Test
    public void TestConformanceCheck0_0() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck0.json",
                testConformancePath + "log0_0.txt");
        assertFalse(violation.assertion);
    }
    @Test
    public void TestConformanceCheck0_1() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck0.json",
                testConformancePath + "log0_1.txt");
        assertTrue(violation.assertion);
    }

    @Test
    public void TestConformanceCheck1_0() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck1.json",
                testConformancePath + "log1_0.txt");
        assertFalse(violation.assertion);
    }

    @Test
    public void TestConformanceCheck1_1() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck1.json",
                testConformancePath + "log1_1.txt");
        assertTrue(violation.assertion);
    }

    @Test
    public void TestConformanceCheck2_0() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck2.json",
                testConformancePath + "log2_0.txt");
        assertFalse(violation.assertion);
    }

    @Test
    public void TestConformanceCheck2_1() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck2.json",
                testConformancePath + "log2_1.txt");
        assertTrue(violation.assertion);
    }

    @Test
    public void TestConformanceCheck3_0() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck3.json",
                testConformancePath + "log3_0.txt");
        assertFalse(violation.assertion);
    }

    @Test
    public void TestConformanceCheck3_1() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck3.json",
                testConformancePath + "log3_1.txt");
        assertTrue(violation.assertion);
    }

    @Test
    public void TestConformanceCheck4_0() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck4.json",
                testConformancePath + "log4_0.txt");
        assertFalse(violation.assertion);
    }

    @Test
    public void TestConformanceCheck4_1() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck4.json",
                testConformancePath + "log4_1.txt");
        assertTrue(violation.assertion);
    }

    @Test
    public void TestConformanceCheck5_0() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck5.json",
                testConformancePath + "log5_0.txt");
        assertFalse(violation.assertion);
    }

    @Test
    public void TestConformanceCheck5_1() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck5.json",
                testConformancePath + "log5_1.txt");
        assertTrue(violation.assertion);
    }

    @Test
    public void TestConformanceCheck6_0() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck6.json",
                testConformancePath + "log6_0.txt");
        assertFalse(violation.assertion);
    }

    @Test
    public void TestConformanceCheck6_1() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck6.json",
                testConformancePath + "log6_1.txt");
        assertTrue(violation.assertion);
    }

    @Test
    public void TestConformanceCheck7_0() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck7_0.json",
                testConformancePath + "log7_0.txt");
        assertFalse(violation.assertion);
    }

    @Test
    public void TestConformanceCheck7_1() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck7_0.json",
                testConformancePath + "log7_1.txt");
        assertTrue(violation.assertion);
    }

    @Test
    public void TestConformanceCheck7_2() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck7_0.json",
                testConformancePath + "log7_1.txt");
        assertTrue(violation.assertion);
    }

    @Test
    public void TestConformanceCheck7_3() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck7_1.json",
                testConformancePath + "log7_3.txt");
        assertFalse(violation.assertion);
    }

    @Test
    public void TestConformanceCheck7_4() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck7_1.json",
                testConformancePath + "log7_4.txt");
        assertTrue(violation.assertion);
    }

    @Test
    public void TestConformanceCheck7_5() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck7_1.json",
                testConformancePath + "log7_5.txt");
        assertTrue(violation.assertion);
    }
}
