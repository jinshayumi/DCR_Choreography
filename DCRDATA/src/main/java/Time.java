import modelInterface.ModelImp;
import models.dcrGraph.DCRGraph;
import models.jsonDCR.JsonDCR;
import projectionInterface.ProjectionImp;

import java.util.HashMap;
import java.util.HashSet;

public class Time {
    public static void main(String[] args) throws Exception {
        ModelImp modelImp = new ModelImp();
        ProjectionImp projectionImp = new ProjectionImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject("/src/main/resources/LogicTimeData.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        HashMap<String, HashSet<String>> timeCondition = dcrGraph.getOneMap("TimeCondition");

        System.out.println("dead lock approximation: " +dcrGraph.checkDeadLock());

        HashSet<String> roles = new HashSet<>();
        roles.add("Buyer");
        roles.add("Seller1");
        roles.add("Seller2");

        HashMap<String, DCRGraph> dcrGraphHashMap = new HashMap<>();
        for (String role: roles){
            // if is projectable?
            if (modelImp.projectable(dcrGraph, role)){
                System.out.println("Role " + role + " is projectable");

                DCRGraph endUpProjection = projectionImp.Process(dcrGraph, role);
                dcrGraphHashMap.put(role, endUpProjection);
            }
            else {
                System.out.println("Role " + role +" is not projectable");
//                return;
            }
        }

        System.out.println("finish");
    }
}
