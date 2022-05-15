import modelInterface.ModelImp;
import models.dcrGraph.DCRGraph;
import models.jsonDCR.JsonDCR;
import projectionInterface.ProjectionImp;
import services.AsynchroService;
import services.MQTTListener;
import services.scenario.Buyer;
import services.scenario.Seller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Time {
    public static void main(String[] args) throws Exception {
        ModelImp modelImp = new ModelImp();
        ProjectionImp projectionImp = new ProjectionImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject("/src/main/resources/LogicTimeData.json");
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);
        System.out.println("equal events: " + dcrGraph.findAlternativePairs());

        System.out.println("dead lock free using approximation? " +dcrGraph.checkDeadLock());
        if (dcrGraph.checkDeadLock()){
            System.out.println("time lock free using approximation? " + dcrGraph.checkTimeLock());
        }
        System.out.println("have path: "+ dcrGraph.havePath("Input_decision", "Quote"));

        HashSet<String> roles = new HashSet<>();
        roles.add("Buyer");
        roles.add("Seller1");
        roles.add("Seller2");

        HashMap<String, DCRGraph> dcrGraphHashMap = new HashMap<>();
        HashMap<String, DCRGraph> monitorMap = new HashMap<>();
        HashMap<String, AsynchroService> asychroServiceHashMap = new HashMap<>();
        for (String role: roles){
            // if is projectable?
            if (modelImp.projectable(dcrGraph, role)){
                System.out.println("Role " + role + " is projectable");

                DCRGraph endUpProjection = projectionImp.Process(dcrGraph, role);
                dcrGraphHashMap.put(role, endUpProjection);
                monitorMap.put(role, projectionImp.Process(dcrGraph, role));

                if(role.equals("Buyer")){
                    Buyer buyer = new Buyer(role, endUpProjection);
                    asychroServiceHashMap.put(role, buyer);
                }
                if (role.startsWith("Seller")){
                    Seller seller = new Seller(role, endUpProjection);
                    asychroServiceHashMap.put(role, seller);
                }
            }
            else {
                System.out.println("Role " + role +" is not projectable");
//                return;
            }
        }

        // Centralized listener.
        MQTTListener mqttListener = new MQTTListener(monitorMap, dcrGraph);


        FileReader fr = new FileReader(System.getProperty("user.dir")+ "/src/main/resources/input.txt");
        BufferedReader br = new BufferedReader(fr);
        String line = "";
        String arrs[] = null;
        while ((line = br.readLine())!= null){
            Scanner sc = new Scanner(System.in);
            System.out.println("Input!:::");
            sc.nextLine();
            arrs = line.split(" ");
            asychroServiceHashMap.get(arrs[0]).execute(arrs[1]);
        }
        br.close();
        fr.close();

//        asychroServiceHashMap.get("Buyer").execute("Ask_for_Quote");
//        asychroServiceHashMap.get("Seller1").execute("Quote1");
//        asychroServiceHashMap.get("Seller2").execute("Quote2");
//        asychroServiceHashMap.get("Buyer").execute("Output_replies");
//        asychroServiceHashMap.get("Buyer").execute("Input_decision");
////        asychroServiceHashMap.get("Buyer").execute("Ask_for_Quote");
////        asychroServiceHashMap.get("Seller1").execute("Quote1");
////        asychroServiceHashMap.get("Seller2").execute("Quote2");
////        asychroServiceHashMap.get("Buyer").execute("Output_replies");
//
////        asychroServiceHashMap.get("Buyer").execute("Timeout");
////        asychroServiceHashMap.get("Buyer").execute("Accept2");
//        Scanner sc = new Scanner(System.in);
//        while (true){
//            System.out.println("Next role and the interaction");
//            String input = sc.nextLine();
//            if (input.equals("quit")){
//                break;
//            }
//            else {
//                System.out.println(input);
//                String role = input.split(" ")[0];
//                String action = input.split(" ")[1];
//                asychroServiceHashMap.get(role).execute(action);
//            }
//        }
        System.out.println("finish");
    }
}
