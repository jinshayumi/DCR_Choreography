import modelInterface.ModelImp;
import models.dcrGraph.DCRGraph;
import models.jsonDCR.JsonDCR;
import projectionInterface.ProjectionImp;
import services.AsychroService;
import services.scenario1.Buyer;
import services.scenario1.Seller;
import services.scenario1.Shipper;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class Time {
    public static void main(String[] args) throws Exception {
        ModelImp modelImp = new ModelImp();
        ProjectionImp projectionImp = new ProjectionImp();

        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject("/Projection/src/main/resources/DCR.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        HashSet<String> roles = new HashSet<>();
        roles.add("Buyer");
        roles.add("Shipper");
        roles.add("Seller1");
        roles.add("Seller2");

        HashMap<String, AsychroService> asychroServiceHashMap = new HashMap<>();
        HashMap<String, DCRGraph> dcrGraphHashMap = new HashMap<>();
        for (String role: roles){
            // if is projectable?
            if (modelImp.projectable(dcrGraph, role)){
                System.out.println("Role " + role + " is projectable");

                DCRGraph endUpProjection = projectionImp.Process(dcrGraph, role);
                dcrGraphHashMap.put(role, endUpProjection);

                HashSet<String> subscribeTopics = endUpProjection.getSubscribe(role);
            }
            else {
                System.out.println("Role " + role +" is not projectable");
                return;
            }
        }

        System.out.println("finish");
    }
}
