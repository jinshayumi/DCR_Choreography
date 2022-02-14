import models.dcrGraph.DCRGraph;
import models.jsonDCR.JsonDCR;
import modelInterface.ModelImp;
import projectionInterface.ProjectionImp;
import services.IService;
import services.InteractServiceImp;
import services.utilities.Message;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DCR {
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

        Registry registry = LocateRegistry.createRegistry(8088);
        HashMap<String, InteractServiceImp> roleServiceMap = new HashMap<>();
        for (String role: roles){
            // if is projectable?
            if (modelImp.projectable(dcrGraph, role)){
                System.out.println("Role " + role +" is projectable");
                // generate the end up projection.
                DCRGraph endUpProjection = projectionImp.Process(dcrGraph, role);

                // register function call using rmi.
                InteractServiceImp interactServiceImp = new InteractServiceImp(role, endUpProjection);
                registry.rebind(role, interactServiceImp);
                roleServiceMap.put(role, interactServiceImp);
            }
            else {
                System.out.println("Role " + role +" is not projectable");
                return;
            }
        }

        String[] list = registry.list();
        for(String s : list){
            System.out.println("service is: "+ s);
        }

        InteractServiceImp buyer = roleServiceMap.get("Buyer");
        List<String> receivers = new ArrayList<>();
        receivers.add("Seller1");
        receivers.add("Seller2");
        buyer.execute(new Message("interactionAsk", "Buyer", receivers));

        System.out.println("finish...");
    }
}
