import models.dcrGraph.DCRGraph;
import models.jsonDCR.JsonDCR;
import modelInterface.ModelImp;
import projectionInterface.ProjectionImp;

import java.util.HashSet;

public class DCR {
    public static void main(String[] args) throws Exception {
        ModelImp modelImp = new ModelImp();
        ProjectionImp projectionImp = new ProjectionImp();

        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject("/Projection/src/main/resources/DCR.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);
        HashSet<String> roles = new HashSet<>();
//        roles.add("Buyer");
        roles.add("Shipper");
//        roles.add("Seller1");
        char[][] a = new char[][]{};

        for (String role: roles){
            // if is projectable?
            if (modelImp.projectable(dcrGraph, role)){
                // generate the end up projection.
                DCRGraph endUpProjection = projectionImp.Process(dcrGraph, role);
                System.out.println("Role " + role +" is projectable");
            }
            else {
                System.out.println("Role " + role +" is not projectable");
            }
        }
        System.out.println("finish...");
    }
}
