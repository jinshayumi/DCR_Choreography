package dcrGraph.traceReplay;

import modelInterface.ModelImp;
import models.dcrGraph.DCRGraph;
import models.jsonDCR.JsonDCR;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;

public class TestTraceReplay {
    private final static String testTraceReplayPath = "/src/main/resources/test/dcrGraph/traceReplay/";


    // condition.
    @Test
    public void testTraceReplay0()
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, InstantiationException, IOException {
        ModelImp modelImp = new ModelImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testTraceReplayPath + "traceReplay0.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertEquals(dcrGraph.findPaddingEvents("C", 0).size(), 0);
        assertEquals(dcrGraph.findPaddingEvents("C", 1).size(), 0);
        assertEquals(dcrGraph.findPaddingEvents("C", 2).size(), 1);
        assertEquals(dcrGraph.findPaddingEvents("C", 3).size(), 5);
    }

    // response.
    @Test
    public void testTraceReplay1()
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, InstantiationException, IOException {
        ModelImp modelImp = new ModelImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testTraceReplayPath + "traceReplay1.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertEquals(dcrGraph.findPaddingEvents("C", 0).size(), 1);
        assertEquals(dcrGraph.findPaddingEvents("C", 1).size(), 3);
    }

    // milestone.
    @Test
    public void testTraceReplay2()
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, InstantiationException, IOException {
        ModelImp modelImp = new ModelImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testTraceReplayPath + "traceReplay2.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertEquals(dcrGraph.findPaddingEvents("C", 0).size(), 0);
        assertEquals(dcrGraph.findPaddingEvents("C", 1).size(), 1);
    }

    // inclusion.
    @Test
    public void testTraceReplay3()
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, InstantiationException, IOException {
        ModelImp modelImp = new ModelImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testTraceReplayPath + "traceReplay3.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertEquals(dcrGraph.findPaddingEvents("C", 0).size(), 0);
        assertEquals(dcrGraph.findPaddingEvents("C", 1).size(), 0);
        assertEquals(dcrGraph.findPaddingEvents("C", 2).size(), 1);
        assertEquals(dcrGraph.findPaddingEvents("C", 3).size(), 5);
    }

    // exclusion.
    @Test
    public void testTraceReplay4()
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, InstantiationException, IOException {
        ModelImp modelImp = new ModelImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testTraceReplayPath + "traceReplay4.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertEquals(dcrGraph.findPaddingEvents("C", 0).size(), 0);
        assertEquals(dcrGraph.findPaddingEvents("C", 1).size(), 2);
        assertEquals(dcrGraph.findPaddingEvents("C", 2).size(), 7);
    }

    // condition, response and milestone.
    @Test
    public void testTraceReplay5()
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, InstantiationException, IOException {
        ModelImp modelImp = new ModelImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testTraceReplayPath + "traceReplay5.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertEquals(dcrGraph.findPaddingEvents("C", 0).size(), 0);
        assertEquals(dcrGraph.findPaddingEvents("C", 1).size(), 0);
        assertEquals(dcrGraph.findPaddingEvents("C", 2).size(), 1);
        assertEquals(dcrGraph.findPaddingEvents("C", 3).size(), 3);

    }

    // inclusion, exclusion and condition.
    @Test
    public void testTraceReplay6()
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, InstantiationException, IOException {
        ModelImp modelImp = new ModelImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testTraceReplayPath + "traceReplay6.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertEquals(dcrGraph.findPaddingEvents("D", 0).size(), 0);
        assertEquals(dcrGraph.findPaddingEvents("D", 1).size(), 2);
        assertEquals(dcrGraph.findPaddingEvents("D", 2).size(), 10);
        System.out.println(dcrGraph.findPaddingEvents("D",2));
    }

    // deadlock: condition loop
    @Test
    public void testTraceReplay7()
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, InstantiationException, IOException {
        ModelImp modelImp = new ModelImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testTraceReplayPath + "traceReplay7.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertEquals(dcrGraph.findPaddingEvents("C", 0).size(), 0);
        assertEquals(dcrGraph.findPaddingEvents("C", 1).size(), 0);
        assertEquals(dcrGraph.findPaddingEvents("C", 2).size(), 0);
    }

    // deadlock: milestone
    @Test
    public void testTraceReplay8()
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, InstantiationException, IOException {
        ModelImp modelImp = new ModelImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testTraceReplayPath + "traceReplay8.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertEquals(dcrGraph.findPaddingEvents("C", 0).size(), 0);
        assertEquals(dcrGraph.findPaddingEvents("C", 1).size(), 0);
        assertEquals(dcrGraph.findPaddingEvents("C", 2).size(), 0);
    }




}
