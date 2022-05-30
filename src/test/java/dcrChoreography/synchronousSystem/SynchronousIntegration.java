package dcrChoreography.synchronousSystem;

import modelInterface.ModelImp;
import models.dcrGraph.DCRGraph;
import models.jsonDCR.JsonDCR;
import projectionInterface.ProjectionImp;
import services.SynchronousCoordinator;
import services.SynchronousService;
import services.roles.SynchronousRole;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class SynchronousIntegration {
    public static void main(String[] args) throws Exception{
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

        HashSet<String> roles = new HashSet<>();
        roles.add("Buyer");
        roles.add("Seller1");
        roles.add("Seller2");

        HashMap<String, DCRGraph> dcrGraphHashMap = new HashMap<>();
        HashMap<String, DCRGraph> monitorMap = new HashMap<>();
        HashMap<String, SynchronousService> synchronousServiceHashMap = new HashMap<>();
        for (String role: roles){
            // if is projectable?
            if (modelImp.projectable(dcrGraph, role)){
                System.out.println("Role " + role + " is projectable");

                DCRGraph endUpProjection = projectionImp.Process(dcrGraph, role);
                dcrGraphHashMap.put(role, endUpProjection);
                monitorMap.put(role, projectionImp.Process(dcrGraph, role));

                SynchronousRole synchronousRole = new SynchronousRole(role, endUpProjection);
                synchronousServiceHashMap.put(role, synchronousRole);
            }
            else {
                System.out.println("Role " + role +" is not projectable");
            }
        }

        // Centralized listener.
//        MQTTListener mqttListener = new MQTTListener(monitorMap, dcrGraph);

        SynchronousCoordinator synchronousCoordinator = new SynchronousCoordinator(monitorMap, dcrGraph);

        FileReader fr = new FileReader(System.getProperty("user.dir")+ "/src/main/resources/input.txt");
        BufferedReader br = new BufferedReader(fr);
        String line = "";
        String arrs[] = null;
        while ((line = br.readLine())!= null){
            Scanner sc = new Scanner(System.in);
            System.out.println("Input!:::");
            sc.nextLine();
            arrs = line.split(" ");
            synchronousServiceHashMap.get(arrs[0]).requestExecuteEvent(arrs[1]);
        }
        br.close();
        fr.close();
    }
}
