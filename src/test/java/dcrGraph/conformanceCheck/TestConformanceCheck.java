package dcrGraph.conformanceCheck;

import conformanceCheckInterface.Conformance;
import conformanceCheckInterface.Violation;
import org.junit.Test;
import static org.junit.Assert.*;


public class TestConformanceCheck {
    private static final String testConformancePath = "/src/main/resources/test/dcrGraph/conformanceCheck/";

    // condition: no violation
    @Test
    public void TestConformanceCheck0_0() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck0.json",
                testConformancePath + "log0_0.txt");
        assertFalse(violation.assertion);
    }

    // condition: violation
    @Test
    public void TestConformanceCheck0_1() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck0.json",
                testConformancePath + "log0_1.txt");
        assertTrue(violation.assertion);
    }

    // response: no violation
    @Test
    public void TestConformanceCheck1_0() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck1.json",
                testConformancePath + "log1_0.txt");
        assertFalse(violation.assertion);
    }

    // response: violation
    @Test
    public void TestConformanceCheck1_1() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck1.json",
                testConformancePath + "log1_1.txt");
        assertTrue(violation.assertion);
    }

    // inclusion: no violation
    @Test
    public void TestConformanceCheck2_0() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck2.json",
                testConformancePath + "log2_0.txt");
        assertFalse(violation.assertion);
    }

    // inclusion: violation
    @Test
    public void TestConformanceCheck2_1() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck2.json",
                testConformancePath + "log2_1.txt");
        assertTrue(violation.assertion);
    }

    // exclusion: no violation
    @Test
    public void TestConformanceCheck3_0() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck3.json",
                testConformancePath + "log3_0.txt");
        assertFalse(violation.assertion);
    }

    // exclusion: violation
    @Test
    public void TestConformanceCheck3_1() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck3.json",
                testConformancePath + "log3_1.txt");
        assertTrue(violation.assertion);
    }

    // milestone: no violation
    @Test
    public void TestConformanceCheck4_0() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck4.json",
                testConformancePath + "log4_0.txt");
        assertFalse(violation.assertion);
    }

    // milestone: violation
    @Test
    public void TestConformanceCheck4_1() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck4.json",
                testConformancePath + "log4_1.txt");
        assertTrue(violation.assertion);
    }

    // time delay: no violation
    @Test
    public void TestConformanceCheck5_0() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck5.json",
                testConformancePath + "log5_0.txt");
        assertFalse(violation.assertion);
    }

    // time delay: violation
    @Test
    public void TestConformanceCheck5_1() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck5.json",
                testConformancePath + "log5_1.txt");
        assertTrue(violation.assertion);
    }

    // time deadline: no violation
    @Test
    public void TestConformanceCheck6_0() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck6.json",
                testConformancePath + "log6_0.txt");
        assertFalse(violation.assertion);
    }

    // time deadline: violation
    @Test
    public void TestConformanceCheck6_1() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck6.json",
                testConformancePath + "log6_1.txt");
        assertTrue(violation.assertion);
    }

    // delay and deadline: no violation
    @Test
    public void TestConformanceCheck7_0() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck7_0.json",
                testConformancePath + "log7_0.txt");
        assertFalse(violation.assertion);
    }

    // delay and deadline: violation of delay
    @Test
    public void TestConformanceCheck7_1() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck7_0.json",
                testConformancePath + "log7_1.txt");
        assertTrue(violation.assertion);
    }

    // delay and deadline: violation of deadline
    @Test
    public void TestConformanceCheck7_2() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck7_0.json",
                testConformancePath + "log7_1.txt");
        assertTrue(violation.assertion);
    }

    // delay chain and deadline: no violation
    @Test
    public void TestConformanceCheck7_3() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck7_1.json",
                testConformancePath + "log7_3.txt");
        assertFalse(violation.assertion);
    }

    // delay chain and deadline: violation of delay
    @Test
    public void TestConformanceCheck7_4() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck7_1.json",
                testConformancePath + "log7_4.txt");
        assertTrue(violation.assertion);
    }

    // delay chain and deadline: violation of deadline
    @Test
    public void TestConformanceCheck7_5() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck7_1.json",
                testConformancePath + "log7_5.txt");
        assertTrue(violation.assertion);
    }

    // constant data on event: no violation
    @Test
    public void TestConformanceCheck8_0() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck8_0.json",
                testConformancePath + "log8_0.txt");
        assertFalse(violation.assertion);
    }

    // constant data on event: violation
    @Test
    public void TestConformanceCheck8_1() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck8_0.json",
                testConformancePath + "log8_1.txt");
        assertTrue(violation.assertion);
    }

    // data including other data's value on event: no violation
    @Test
    public void TestConformanceCheck8_2() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck8_1.json",
                testConformancePath + "log8_2.txt");
        assertFalse(violation.assertion);
    }

    // data including other data's value on event: violation
    @Test
    public void TestConformanceCheck8_3() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck8_1.json",
                testConformancePath + "log8_3.txt");
        assertTrue(violation.assertion);
    }

    // input data on event: no violation
    @Test
    public void TestConformanceCheck8_4() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck8_2.json",
                testConformancePath + "log8_4.txt");
        assertFalse(violation.assertion);
    }

    // input data on event: violation
    @Test
    public void TestConformanceCheck8_5() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck8_2.json",
                testConformancePath + "log8_5.txt");
        assertTrue(violation.assertion);
    }

    // incorrect data type in sequence: violation
    @Test
    public void TestConformanceCheck8_6() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck8_1.json",
                testConformancePath + "log8_6.txt");
        assertTrue(violation.assertion);
    }

    // incorrect data type in sequence: violation
    @Test
    public void TestConformanceCheck8_7() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck8_1.json",
                testConformancePath + "log8_7.txt");
        assertTrue(violation.assertion);
    }

    // Tests of events on guards(condition/milestone): no violation
    @Test
    public void TestConformanceCheck8_8() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck8_3.json",
                testConformancePath + "log8_8.txt");
        assertFalse(violation.assertion);
    }

    // Tests of events on guards(condition/milestone): violation
    @Test
    public void TestConformanceCheck8_9() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck8_4.json",
                testConformancePath + "log8_9.txt");
        assertTrue(violation.assertion);
    }

    // Tests of events on guards(inclusion/exclusion/response): no violation
    @Test
    public void TestConformanceCheck8_10() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck8_5.json",
                testConformancePath + "log8_10.txt");
        assertFalse(violation.assertion);
    }

    // Tests of events on guards(inclusion/exclusion/response): violation
    @Test
    public void TestConformanceCheck8_11() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(testConformancePath + "conformanceCheck8_6.json",
                testConformancePath + "log8_11.txt");
        assertTrue(violation.assertion);
    }

}
