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

        // the result DCR graph.
        DCRGraph res = new DCRGraph();

        // The projection to the events: E|_{sigma}.
        HashSet<String> projectionEvents = new HashSet<>();
        for (String s: sigmaSet){
            HashSet<String> dependencies = dcrGraph.getOnesDependency(s);
            projectionEvents.addAll(dependencies);
        }
        res.setEvents(projectionEvents);

        // The projection to the marking states: M|_{sigma}.
        DCRMarking projectionMarkings = new DCRMarking();

        // Ex|_{sigma}.
        HashSet<String> projectionExecuted = new HashSet<>();
        for (String s: dcrGraph.dcrMarking.executed){
            if (res.getEvents().contains(s)){
                projectionExecuted.add(s);
            }
        }
        projectionMarkings.executed = projectionExecuted;

        // Re|_{sigma}.
        HashSet<String> projectionPending = new HashSet<>();
        for (String s: dcrGraph.dcrMarking.pending){
            if (res.getEvents().contains(s)){
                projectionPending.add(s);
            }
        }
        projectionMarkings.pending = projectionPending;

        // In|_{sigma}.
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
        res.dcrMarking = projectionMarkings;

        // projection of condition relationship.
        HashMap<String, HashSet<String>> conditionProjectionRHS = multipleSets(conditionToSigma, sigmaSet);
        HashMap<String, HashSet<String>> conditionProjection = intersectionMap(dcrGraph.getConditionsFor(), conditionProjectionRHS);
        res.setConditionsFor(conditionProjection);

        // projection of milestone relationship.
        HashMap<String, HashSet<String>> milestoneProjectionRHS = multipleSets(mileStoneToSigma, sigmaSet);
        HashMap<String, HashSet<String>> milestoneProjection = intersectionMap(dcrGraph.getMilestonesFor(), milestoneProjectionRHS);
        res.setMilestonesFor(milestoneProjection);

        // projection of response relationship.
        HashSet<String> responseToSigma = getARelationship(dcrGraph, "getResponsesTo", sigmaSet);
        // •→→⋄δ
        HashSet<String> responseMile = getARelationship(dcrGraph, "getResponsesTo", mileStoneToSigma);
        // (•→→⋄δ)×(→⋄δ)
        HashMap<String, HashSet<String>> multiResponseMile = multipleSets(responseMile, mileStoneToSigma);
        // (•→δ)×δ)
        HashMap<String, HashSet<String>> multiResponseSigma = multipleSets(responseToSigma, sigmaSet);
        HashMap<String, HashSet<String>> union = unionMap(multiResponseMile, multiResponseSigma);
        HashMap<String, HashSet<String>> responseProjection = intersectionMap(dcrGraph.getResponsesTo(), union);
        res.setResponsesTo(responseProjection);

        // projection of inclusion relationship.
        HashSet<String> includeToSigma = getARelationship(dcrGraph, "getIncludesTo", sigmaSet);
        HashSet<String> includeCondition = getARelationship(dcrGraph, "getIncludesTo", conditionToSigma);
        HashMap<String, HashSet<String>> multi1 = multipleSets(includeCondition, conditionToSigma);
        HashSet<String> includeMile = getARelationship(dcrGraph, "getIncludesTo", mileStoneToSigma);
        HashMap<String, HashSet<String>> multi2 = multipleSets(includeMile, mileStoneToSigma);
        HashMap<String, HashSet<String>> multi3 = multipleSets(includeToSigma, sigmaSet);
        HashMap<String, HashSet<String>> unions = unionMap(unionMap(multi1, multi2), multi3);
        HashMap<String, HashSet<String>> inclusionProjection = intersectionMap(dcrGraph.getIncludesTo(), unions);
        res.setIncludesTo(inclusionProjection);

        // projection of exclusion relationship.
        HashSet<String> excludeToSigma = getARelationship(dcrGraph, "getExcludesTo", sigmaSet);
        HashSet<String> excludeCondition = getARelationship(dcrGraph, "getExcludesTo", conditionToSigma);
        HashMap<String, HashSet<String>> emulti1 = multipleSets(excludeCondition, conditionToSigma);
        HashSet<String> excludeMile = getARelationship(dcrGraph, "getExcludesTo", mileStoneToSigma);
        HashMap<String, HashSet<String>> emulti2 = multipleSets(excludeMile, mileStoneToSigma);
        HashMap<String, HashSet<String>> emulti3 = multipleSets(excludeToSigma, sigmaSet);
        HashMap<String, HashSet<String>> eunions = unionMap(unionMap(emulti1, emulti2), emulti3);
        HashMap<String, HashSet<String>> excludeProjection = intersectionMap(dcrGraph.getExcludesTo(), eunions);
        res.setIncludesTo(excludeProjection);

        return res;
    }

    public void addAPair(HashMap<String, HashSet<String>> map, String key, String value){
        if (!map.containsKey(key)){
            HashSet<String> temp = new HashSet<>();
            temp.add(value);
            map.put(key, temp);
        }
        else{
            map.get(key).add(value);
        }
    }

    // calculate the intersection of two maps.
    private HashMap<String, HashSet<String>> intersectionMap(HashMap<String, HashSet<String>> map1, HashMap<String, HashSet<String>> map2){
        HashMap<String, HashSet<String>> res = new HashMap<>();
        for (String key: map1.keySet()){
            for (String value: map1.get(key)){
                if(map2.containsKey(key)&&map2.get(key).contains(value)){
                    addAPair(res, key, value);
                }
            }
        }
        return res;
    }

    // calculate the union of two maps.
    private HashMap<String, HashSet<String>> unionMap(HashMap<String, HashSet<String>> map1, HashMap<String, HashSet<String>> map2){
        HashMap<String, HashSet<String>> res = new HashMap<>();
        for (String key: map1.keySet()){
            for (String value: map1.get(key)){
                addAPair(res, key, value);
            }
        }
        for (String key: map2.keySet()){
            for (String value: map2.get(key)){
                addAPair(res, key, value);
            }
        }
        return res;
    }

    // calculate the multiple of 2 sets as relationship.
    private HashMap<String, HashSet<String>> multipleSets(HashSet<String> s1, HashSet<String> s2){
        HashMap<String, HashSet<String>> res = new HashMap<>();
        for(String ele1: s1){
            for (String ele2:s2){
                addAPair(res, ele1, ele2);
            }
        }
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
