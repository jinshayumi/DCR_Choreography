package models.dcrGraph;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TraceReplay {
    HashSet<List<String>> res = new HashSet<>();


    public HashSet<List<String>> replayRes(DCRGraph dcrGraph, int depth, String aim)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, IOException {
        List<String> temp = new ArrayList<>();
        backtrace(dcrGraph, depth, aim, temp);
        return res;
    }

    private void backtrace(DCRGraph dcrGraph, int depth, String aim, List<String> temp)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, IOException {
        if (depth<0) {
            return;
        }
        else {
            if (dcrGraph.enabled(aim)){
                res.add(new ArrayList<>(temp));
            }
//            else {
                HashSet<String> events = dcrGraph.enabledEvents();
                for (String event: events){
                    DCRGraph newOne = dcrGraph.deepClone();
                    newOne.execute(event);
                    List<String> newTemp = new ArrayList<>(temp);
                    newTemp.add(event);
                    backtrace(newOne, depth-1, aim, newTemp);
                }
//            }
        }
    }

}
