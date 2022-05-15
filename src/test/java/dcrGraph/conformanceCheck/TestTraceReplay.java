package dcrGraph.conformanceCheck;

import modelInterface.ModelImp;
import models.dcrGraph.DCRGraph;
import models.jsonDCR.JsonDCR;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;

public class TestTraceReplay {
    private String testConformancePath = "/src/main/resources/test/dcrGraph/conformanceCheck/";
    @Test
    public void testTraceReplay0()
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, InstantiationException, IOException {
        ModelImp modelImp = new ModelImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testConformancePath + "conformanceCheck0.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

//        System.out.println(dcrGraph.findPaddingEvents("C", 3));
        assertEquals(dcrGraph.findPaddingEvents("C", 0).size(), 0);
        assertEquals(dcrGraph.findPaddingEvents("C", 1).size(), 0);
        assertEquals(dcrGraph.findPaddingEvents("C", 2).size(), 1);
        assertEquals(dcrGraph.findPaddingEvents("C", 3).size(), 8);
    }
}
