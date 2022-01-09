package projections.endUp;

import model.DCRGraph;
import model.DCRMarking;
import model.entities.Participant;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static projections.sigma.SigmaSet.getSigmaSet;

public class EndUpProjection {
    public DCRGraph getARolesProjection(DCRGraph dcrGraph, String role) throws Exception {
        HashSet<String> sigmaSet = getSigmaSet(dcrGraph, role);

        DCRGraph res = new DCRGraph();
        HashSet<String> projectionEvents = new HashSet<>();
        for (String s: sigmaSet){
            HashSet<String> dependencies = dcrGraph.getOnesDependency(s);
            projectionEvents.addAll(dependencies);
        }
        res.setEvents(projectionEvents);

        DCRMarking projectionMarkings = new DCRMarking();
        HashSet<String> projectionExecuted = new HashSet<>();
        for (String s: dcrGraph.dcrMarking.executed){
            if (res.getEvents().contains(s)){
                projectionExecuted.add(s);
            }
        }
        projectionMarkings.executed = projectionExecuted;

        HashSet<String> projectionPending = new HashSet<>();
        for (String s: dcrGraph.dcrMarking.pending){
            if (res.getEvents().contains(s)){
                projectionPending.add(s);
            }
        }
        projectionMarkings.pending = projectionPending;

        HashSet<String> projectionIncluded = new HashSet<>();
        HashSet<String> conditionToSigma = getARelationship(dcrGraph, "getConditionsFor", sigmaSet);
        HashSet<String> mileStoneToSigma = getARelationship(dcrGraph, "getMilestonesFor", sigmaSet);
        conditionToSigma.addAll(mileStoneToSigma);
        conditionToSigma.addAll(sigmaSet);
        HashSet<String> lhs = new HashSet<>();
        lhs.addAll(conditionToSigma);
        lhs.retainAll(dcrGraph.dcrMarking.included);
        HashSet<String> rhs = new HashSet<>(res.getEvents());
        rhs.removeAll(conditionToSigma);
        projectionIncluded.addAll(lhs);
        projectionIncluded.addAll(rhs);
        projectionMarkings.included = projectionIncluded;


        return res;
    }

    public HashSet<String> getARelationship(DCRGraph dcrGraph, String relationship, HashSet<String> set) throws Exception {
        Class<?> clazz = Class.forName("model.DCRGraph");
        Method method = clazz.getMethod(relationship);
        HashMap<String, HashSet<String>> relations = (HashMap<String, HashSet<String>>) method.invoke(dcrGraph);
        HashSet<String> res = new HashSet<>();
        for (String s : relations.keySet()){
            HashSet<String> result = new HashSet<>();
            result.addAll(relations.get(s));
            result.retainAll(set);
            if (result.size()>0){
                res.add(s);
            }
        }
        return res;
    }
}
