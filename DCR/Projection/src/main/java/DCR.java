import models.dcrGraph.DCRGraph;
import models.jsonDCR.JsonDCR;
import modelInterface.ModelImp;
import projectionInterface.ProjectionImp;
import services.AsychroService;
import services.scenario1.Buyer;
import services.scenario1.Seller;
import services.scenario1.Shipper;

import java.util.*;

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

// RMI
//        Registry registry = LocateRegistry.createRegistry(8088);
//        HashMap<String, InteractServiceImp> roleServiceMap = new HashMap<>();
        HashMap<String, AsychroService> asychroServiceHashMap = new HashMap<>();
        HashMap<String, DCRGraph> dcrGraphHashMap = new HashMap<>();
        for (String role: roles){
            // if is projectable?
            if (modelImp.projectable(dcrGraph, role)){
                System.out.println("Role " + role + " is projectable");
                // generate the end up projection.
                DCRGraph endUpProjection = projectionImp.Process(dcrGraph, role);
                dcrGraphHashMap.put(role, endUpProjection);

                HashSet<String> subscribeTopics = endUpProjection.getSubscribe(role);

                if(role.equals("Buyer")){
                    Buyer buyer = new Buyer(role, subscribeTopics, endUpProjection);
                    asychroServiceHashMap.put(role, buyer);
                }
                if (role.startsWith("Seller")){
                    Seller seller = new Seller(role, subscribeTopics, endUpProjection);
                    asychroServiceHashMap.put(role, seller);
                }
                if (role.equals("Shipper")){
                    Shipper shipper = new Shipper(role, subscribeTopics, endUpProjection);
                    asychroServiceHashMap.put(role, shipper);
                }
            }
            else {
                System.out.println("Role " + role +" is not projectable");
                return;
            }
        }

//        MQTTListener mqttListener = new MQTTListener(dcrGraphHashMap, dcrGraph);
        Buyer buyer = (Buyer) asychroServiceHashMap.get("Buyer");
        buyer.execute("interactionAsk");
    }
}
