package conformanceCheckInterface;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface IConformance {
    /**
     * conformance check a sequence of events according to DCR graph.
     * return false if there is no violation.
     * return (true, ee) if there is a violation.
     */
    public Violation conformanceCheck(String dcrPath, String logPath)
            throws IOException, ClassNotFoundException, InvocationTargetException,
            NoSuchMethodException, IllegalAccessException, InstantiationException;
}
