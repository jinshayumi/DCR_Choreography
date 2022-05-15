package dcrGraph.deadlock;


import modelInterface.ModelImp;
import models.dcrGraph.DCRGraph;
import models.jsonDCR.JsonDCR;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestDeadLock {
    private String testDeadlockPath = "/src/main/resources/test/dcrGraph/deadlock/";
    // 0: deadlock free.
    // No conditino/milestone circle.
    @Test
    public void testDeadlock0() throws Exception {
        ModelImp modelImp = new ModelImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testDeadlockPath+ "deadlock0.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);
        assertTrue(dcrGraph.checkDeadLock());
    }

    // 1: deadlock free.
    // Have condition/milestone circle, but no events can be pending.
    @Test
    public void testDeadlock1() throws Exception{
        ModelImp modelImp = new ModelImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testDeadlockPath+ "deadlock1.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);
        assertTrue(dcrGraph.checkDeadLock());
    }

    // 2: NOT deadlock free.
    // Have condition/milestone circle, Some event is the aim of a response.
    @Test
    public void testDeadlock2() throws Exception{
        ModelImp modelImp = new ModelImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testDeadlockPath+ "deadlock2.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);
        assertFalse(dcrGraph.checkDeadLock());
    }

    // 3: deadlock free.
    // Have condition/milestone circle, Some event is pending, but not included.
    @Test
    public void testDeadlock3() throws Exception{
        ModelImp modelImp = new ModelImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testDeadlockPath+ "deadlock3.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);
        assertTrue(dcrGraph.checkDeadLock());
    }

    // 4: NOT deadlock free.
    // Have a milestone circle, Some event is pending.
    @Test
    public void testDeadlock4() throws Exception{
        ModelImp modelImp = new ModelImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testDeadlockPath+ "deadlock4.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);
        assertFalse(dcrGraph.checkDeadLock());
    }

}
