import models.dcrGraph.DCRGraph;
import models.jsonDCR.JsonDCR;
import modelInterface.ModelImp;
import projectionInterface.ProjectionImp;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;

public class DCR {
    public static void main(String[] args) throws Exception {
        ModelImp modelImp = new ModelImp();
        ProjectionImp projectionImp = new ProjectionImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject("/Projection/src/main/resources/DCR.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);
        HashSet<String> roles = new HashSet<>();
        roles.add("Seller1");
//        roles.add("Shipper");
//        roles.add("Seller2");

        for (String role: roles){
            if (modelImp.projectable(dcrGraph, role)){
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
