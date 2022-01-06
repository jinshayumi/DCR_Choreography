package checker;

import model.DCRGraph;

public class ProjectableChecker {
    public static boolean isProjectable(DCRGraph dcrGraph, String role){
        return dcrGraph.endPointProjectable(role);
    }
}
