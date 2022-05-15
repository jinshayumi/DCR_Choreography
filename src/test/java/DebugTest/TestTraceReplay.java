package DebugTest;

import modelInterface.ModelImp;
import models.dcrGraph.DCRGraph;
import models.jsonDCR.JsonDCR;
import projectionInterface.ProjectionImp;

public class TestTraceReplay {
    public static void main(String[] args) throws Exception {
        ModelImp modelImp = new ModelImp();
        ProjectionImp projectionImp = new ProjectionImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject("/src/main/resources/simpleGraph.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);
        System.out.println("equal events: " + dcrGraph.findAlternativePairs());

        System.out.println("dead lock free using approximation? " +dcrGraph.checkDeadLock());
        if (dcrGraph.checkDeadLock()){
            System.out.println("time lock free using approximation? " + dcrGraph.checkTimeLock());
        }
        System.out.println(dcrGraph.findPaddingEvents("3", 1));
    }
}
