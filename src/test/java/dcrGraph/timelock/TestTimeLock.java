package dcrGraph.timelock;


import modelInterface.ModelImp;
import models.dcrGraph.DCRGraph;
import models.jsonDCR.JsonDCR;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestTimeLock {
    private String testDeadlockPath = "/src/main/resources/test/dcrGraph/deadlock/";
    private String testTimelockPath = "/src/main/resources/test/dcrGraph/timelock/";

    // 0: not time lock free.
    // not deadlock free using approximation.
    @Test
    public void testTimeLock0() throws Exception {
        ModelImp modelImp = new ModelImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testDeadlockPath+ "deadlock2.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);
        assertFalse(dcrGraph.checkTimeLock());
    }

    // 1: not time lock free.
    // not deadlock free using approximation.
    @Test
    public void TestTimeLock1() throws Exception{
        ModelImp modelImp = new ModelImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testDeadlockPath+ "deadlock4.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);
        assertFalse(dcrGraph.checkDeadLock());
    }

    // 2: Time lock free.
    // Have responses in inhibitor graph and there are paths
    @Test
    public void TestTimeLock2() throws Exception{
        ModelImp modelImp = new ModelImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testTimelockPath+ "timelock2.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);
        assertTrue(dcrGraph.checkTimeLock());
    }

    // 3: Not time lock free.
    // have response in inhibitor graph, but there is no path.
    @Test
    public void TestTimeLock3() throws Exception{
        ModelImp modelImp = new ModelImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testTimelockPath+ "timelock3.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);
        assertFalse(dcrGraph.checkTimeLock());
    }

    // 4: Time lock free.
    // Have inclusions in inhibitor graph and there are paths
    @Test
    public void TestTimeLock4() throws Exception{
        ModelImp modelImp = new ModelImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testTimelockPath+ "timelock4.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);
        assertTrue(dcrGraph.checkTimeLock());
    }

    // 5: NOT Time lock free.
    // Have inclusions in inhibitor graph and there is no path
    @Test
    public void TestTimeLock5() throws Exception{
        ModelImp modelImp = new ModelImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testTimelockPath+ "timelock5.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);
        assertFalse(dcrGraph.checkTimeLock());
    }

    // 6. Time lock free.
    // time constraints of conditions in inhibitor graph is all 0.
    @Test
    public void TestTimeLock6() throws Exception{
        ModelImp modelImp = new ModelImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testTimelockPath+ "timelock6.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);
        assertTrue(dcrGraph.checkTimeLock());
    }

    // 7. Time lock free.
    // time constraints of conditions in inhibitor graph is all 0.
    @Test
    public void TestTimeLock7() throws Exception{
        ModelImp modelImp = new ModelImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testTimelockPath+ "timelock7.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);
        assertFalse(dcrGraph.checkTimeLock());
    }

}
