import models.dcrGraph.DCRGraph;
import models.jsonDCR.JsonDCR;
import modelInterface.ModelImp;
import projectionInterface.ProjectionImp;
import services.AsychroService;
import services.IService;
import services.InteractServiceImp;
import services.MQTTListener;
import services.roles.Buyer;
import services.roles.Seller;
import services.roles.Shipper;
import services.utilities.Message;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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

                // register function call using rmi.
//                InteractServiceImp interactServiceImp = new InteractServiceImp(role, endUpProjection);
//                registry.rebind(role, interactServiceImp);
//                roleServiceMap.put(role, interactServiceImp);

                HashSet<String> subscribeTopics = endUpProjection.getSubscribe(role);

                if(role.equals("Buyer")){
                    Buyer buyer = new Buyer(role, subscribeTopics);
                    asychroServiceHashMap.put(role, buyer);
                }
                if (role.startsWith("Seller")){
                    Seller seller = new Seller(role, subscribeTopics);
                    asychroServiceHashMap.put(role, seller);
                }
                if (role.equals("Shipper")){
                    Shipper shipper = new Shipper(role, subscribeTopics);
                    asychroServiceHashMap.put(role, shipper);
                }
            }
            else {
                System.out.println("Role " + role +" is not projectable");
                return;
            }
        }

        MQTTListener mqttListener = new MQTTListener(dcrGraphHashMap, dcrGraph);

        HashSet<String> receivers = new HashSet<>();
        receivers.add("Seller1");
        receivers.add("Seller2");
        Buyer buyer = (Buyer) asychroServiceHashMap.get("Buyer");
        buyer.execute("interactionAsk", receivers);


// RMI
//        String[] list = registry.list();
//        for(String s : list){
//            System.out.println("service is: "+ s);
//        }
//
//        InteractServiceImp buyer = roleServiceMap.get("Buyer");
//        List<String> receivers = new ArrayList<>();
//        receivers.add("Seller1");
//        receivers.add("Seller2");
//        buyer.execute(new Message("interactionAsk", "Buyer", receivers));


    }
}
