package conformanceCheckInterface;

import modelInterface.ModelImp;
import models.dcrGraph.DCRGraph;
import models.jsonDCR.JsonDCR;
import services.entities.data.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Conformance implements IConformance{
    @Override
    public Violation conformanceCheck(String dcrPath, String logPath)
            throws IOException, ClassNotFoundException, InvocationTargetException,
            NoSuchMethodException, IllegalAccessException, InstantiationException {
        ModelImp modelImp = new ModelImp();
        // Load from Json
        JsonDCR jsonDCR = modelImp.parseJsonToObject(dcrPath);
        // Transfer to DCR graph
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);
        List<TimedEvent> sequence = parseLog(logPath);
        for (TimedEvent activity: sequence){
            String identity = activity.getIdentity();
            if (dcrGraph.enabled(identity, activity.getTime())){
                // if input event: set the data.
                if (dcrGraph.getDataLogicMap().get(identity).getType().equals("?")){
                    dcrGraph.updateEventData(identity, activity.getData());
                }
                // if decision event: calculate the data and compare with the log.
                else if (!dcrGraph.getDataLogicMap().get(identity).getType().equals("")){
                    dcrGraph.calculateAnEvent(identity);
                    if (!dcrGraph.getDataMap().get(identity).equals(activity.getData())){
                        return new Violation(true, activity);
                    }
                }
                // execute the event.
                dcrGraph.execute(identity, activity.getTime());
            }
            // not enabled
            else {
                return new Violation(true, activity);
            }
        }
        // Finally, if this graph is accepted.
        if (dcrGraph.isAccepting()){
            return new Violation(false, new TimedEvent());
        }
        else return new Violation(true, new TimedEvent());
    }

    public static List<TimedEvent> parseLog(String logPath) throws IOException {
        List<TimedEvent> res = new ArrayList<>();
        FileReader fr = new FileReader(System.getProperty("user.dir")+ logPath);
        BufferedReader br = new BufferedReader(fr);
        String line = "";
        String arrs[] = null;
        while ((line = br.readLine())!= null){
            arrs = line.split(",");
            String name = arrs[0];
            Long time = Long.parseLong(arrs[1]);
            String type = arrs[2];
            Data data;
            if (type.equals("int")){
                data = new IntData(Integer.parseInt(arrs[3]));
            }
            else if (type.equals("bool")){
                data = new BoolData(Boolean.parseBoolean(arrs[3]));
            }
            else if (type.equals("void")){
                data = new VoidData("");
            }
            else data = new StringData(arrs[3]);
            res.add(new TimedEvent(name, time, type, data));
        }
        br.close();
        fr.close();
        Collections.sort(res);
        return res;
    }
}
