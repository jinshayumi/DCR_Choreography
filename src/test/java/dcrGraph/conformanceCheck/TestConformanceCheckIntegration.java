package dcrGraph.conformanceCheck;
import conformanceCheckInterface.Conformance;
import conformanceCheckInterface.Violation;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestConformanceCheckIntegration {
    private static final String choreographyPath =
            "/src/main/resources/test/dcrChoreography/synchronousSystem/LogicTimeData.json";

    private static final String logPath = "/src/main/resources/test/dcrGraph/conformanceCheck/integration/";

    @Test
    public void testConformanceIntegration0() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(choreographyPath,
                logPath+"log0.txt");
        assertFalse(violation.assertion);
    }

    @Test
    public void testConformanceIntegration1() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(choreographyPath,
                logPath+"log1.txt");
        assertTrue(violation.assertion);
    }

    @Test
    public void testConformanceIntegration2() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(choreographyPath,
                logPath+"log2.txt");
        assertTrue(violation.assertion);
    }

    @Test
    public void testConformanceIntegration3() throws Exception{
        Conformance conformance = new Conformance();
        Violation violation = conformance.conformanceCheck(choreographyPath,
                logPath+"log3.txt");
        assertTrue(violation.assertion);
    }
}
