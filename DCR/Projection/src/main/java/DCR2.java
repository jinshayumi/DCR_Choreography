//import modelInterface.ModelImp;
//import models.dcrGraph.DCRGraph;
//import models.jsonDCR.JsonDCR;
//import projectionInterface.ProjectionImp;
//import services.AsychroService;
//import services.MQTTListener;
//import services.scenario2.Basket;
//import services.scenario2.Catalog;
//import services.scenario2.Order;
//import services.scenario2.Payment;
//
//import java.util.HashMap;
//import java.util.HashSet;
//
//public class DCR2 {
//    public static void main(String[] args) throws Exception {
//        ModelImp modelImp = new ModelImp();
//        ProjectionImp projectionImp = new ProjectionImp();
//
//        // Load from Json
//        JsonDCR jsonDCR = modelImp.parseJsonToObject("/Projection/src/main/resources/DCR_Spawning.json");
//        // Transfer to DCR graph
//        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);
//        HashSet<String> roles = new HashSet<>();
//        roles.add("Basket1");
//        roles.add("Basket2");
//        roles.add("Catalog");
//        roles.add("Order");
//        roles.add("Payment");
//
//        HashMap<String, AsychroService> asychroServiceHashMap = new HashMap<>();
//        HashMap<String, DCRGraph> dcrGraphHashMap = new HashMap<>();
//        for (String role: roles){
//            // if is projectable?
//            if (modelImp.projectable(dcrGraph, role)){
//                System.out.println("Role " + role +" is projectable");
//
//                // generate the end up projection.
//                DCRGraph endUpProjection = projectionImp.Process(dcrGraph, role);
//                dcrGraphHashMap.put(role, endUpProjection);
//
//                HashSet<String> subscribeTopics = endUpProjection.getSubscribe(role);
//
//                if(role.startsWith("Basket")){
//                    Basket basket = new Basket(role, subscribeTopics, endUpProjection);
//                    asychroServiceHashMap.put(role, basket);
//                }
//                if (role.startsWith("Catalog")){
//                    Catalog catalog = new Catalog(role,subscribeTopics, endUpProjection);
//                    asychroServiceHashMap.put(role, catalog);
//                }
//                if (role.equals("Order")){
//                    Order order = new Order(role, subscribeTopics, endUpProjection);
//                    asychroServiceHashMap.put(role, order);
//                }
//                if (role.equals("Payment")){
//                    Payment payment = new Payment(role, subscribeTopics, endUpProjection);
//                    asychroServiceHashMap.put(role, payment);
//                }
//            }
//            else {
//                System.out.println("Role " + role +" is not projectable");
//                return;
//            }
//        }
//
//        MQTTListener mqttListener = new MQTTListener(dcrGraphHashMap, dcrGraph);
//
//        Basket Basket1 = (Basket) asychroServiceHashMap.get("Basket1");
//        Basket Basket2 = (Basket) asychroServiceHashMap.get("Basket2");
//        Basket1.execute();
//        Basket2.execute();
//
//    }
//}
