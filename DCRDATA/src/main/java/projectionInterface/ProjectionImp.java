package projectionInterface;

import modelInterface.ModelImp;
import models.dcrGraph.DCRGraph;
import models.dcrGraph.DCRMarking;
import models.jsonDCR.timeRelationship.*;
import projectionInterface.IProjection;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;

public class ProjectionImp implements IProjection {

    @Override
    public HashSet<String> getARolesSigma(DCRGraph dcrGraph, String role) {
        return ModelImp.getARolesInteractions(dcrGraph, role);
    }

    @Override
    public DCRGraph sigmaProjection(DCRGraph dcrGraph, HashSet<String> sigmaSet) throws Exception {
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
        for (String s: dcrGraph.getDcrMarking().executed){
            if (res.getEvents().contains(s)){
                projectionExecuted.add(s);
            }
        }
        projectionMarkings.executed = projectionExecuted;

        // Re|_{sigma}.
        HashSet<String> projectionPending = new HashSet<>();
        for (String s: dcrGraph.getDcrMarking().pending){
            if (res.getEvents().contains(s)){
                projectionPending.add(s);
            }
        }
        projectionMarkings.pending = projectionPending;

        // In|_{sigma}.
        HashSet<String> projectionIncluded = new HashSet<>();
        HashSet<String> conditionToSigma = getARelationship(dcrGraph, "TimeCondition", sigmaSet);
        HashSet<String> mileStoneToSigma = getARelationship(dcrGraph, "TimeMilestone", sigmaSet);
        HashSet<String> lhs1 = new HashSet<>(conditionToSigma);
        lhs1.addAll(mileStoneToSigma);
        lhs1.addAll(sigmaSet);
        HashSet<String> lhs = new HashSet<>();
        lhs.addAll(lhs1);
        lhs.retainAll(dcrGraph.getDcrMarking().included);
        HashSet<String> rhs = new HashSet<>(res.getEvents());
        rhs.removeAll(lhs1);

        projectionIncluded.addAll(lhs);
        projectionIncluded.addAll(rhs);
        projectionMarkings.included = projectionIncluded;
        res.setDcrMarking(projectionMarkings);

        // projection of condition relationship.
        HashMap<String, HashSet<String>> conditionProjectionRHS = multipleSets(conditionToSigma, sigmaSet);
        HashMap<String, HashSet<String>> conditionProjection = intersectionMap(dcrGraph.getOneMap("TimeCondition"), conditionProjectionRHS);
        HashMap<String, HashSet<TimeCondition>> timeConditionProjection = new HashMap<>();
        for (String from: conditionProjection.keySet()){
            HashSet<TimeCondition> temp = new HashSet<>();
            for (TimeCondition timeCondition: dcrGraph.getTimeConditions().get(from)){
                if (conditionProjection.get(from).contains(timeCondition.getTo())){
                    temp.add(new TimeCondition(timeCondition));
                }
            }
            timeConditionProjection.put(from, temp);
        }
        res.setTimeConditions(timeConditionProjection);

        // projection of milestone relationship.
        HashMap<String, HashSet<String>> milestoneProjectionRHS = multipleSets(mileStoneToSigma, sigmaSet);
        HashMap<String, HashSet<String>> milestoneProjection =
                intersectionMap(dcrGraph.getOneMap("TimeMilestone"), milestoneProjectionRHS);

        HashMap<String, HashSet<TimeMilestone>> timeMilestoneProjection = new HashMap<>();
        for (String from: milestoneProjection.keySet()){
            HashSet<TimeMilestone> temp = new HashSet<>();
            for (TimeMilestone timeMilestone: dcrGraph.getTimeMilestones().get(from)){
                if (milestoneProjection.get(from).contains(timeMilestone.getTo())){
                    temp.add(new TimeMilestone(timeMilestone));
                }
            }
            timeMilestoneProjection.put(from, temp);
        }
        res.setTimeMilestones(timeMilestoneProjection);

        // projection of response relationship.
        HashSet<String> responseToSigma = getARelationship(dcrGraph, "TimeResponse", sigmaSet);
        // •→→⋄δ
        HashSet<String> responseMile = getARelationship(dcrGraph, "TimeResponse", mileStoneToSigma);
        // (•→→⋄δ)×(→⋄δ)
        HashMap<String, HashSet<String>> multiResponseMile = multipleSets(responseMile, mileStoneToSigma);
        // (•→δ)×δ)
        HashMap<String, HashSet<String>> multiResponseSigma = multipleSets(responseToSigma, sigmaSet);
        HashMap<String, HashSet<String>> union = unionMap(multiResponseMile, multiResponseSigma);
        HashMap<String, HashSet<String>> responseProjection = intersectionMap(dcrGraph.getOneMap("TimeResponse"), union);
        HashMap<String, HashSet<TimeResponse>> timeResponseProjection = new HashMap<>();
        for (String from: responseProjection.keySet()){
            HashSet<TimeResponse> temp = new HashSet<>();
            for (TimeResponse timeResponse: dcrGraph.getTimeResponses().get(from)){
                if (responseProjection.get(from).contains(timeResponse.getTo())){
                    temp.add(new TimeResponse(timeResponse));
                }
            }
            timeResponseProjection.put(from, temp);
        }
        res.setTimeResponses(timeResponseProjection);
//        res.setResponsesTo(responseProjection);

        // projection of inclusion relationship.
        HashSet<String> includeToSigma = getARelationship(dcrGraph, "TimeInclusion", sigmaSet);
        HashSet<String> includeCondition = getARelationship(dcrGraph, "TimeInclusion", conditionToSigma);
        HashMap<String, HashSet<String>> multi1 = multipleSets(includeCondition, conditionToSigma);
        HashSet<String> includeMile = getARelationship(dcrGraph, "TimeInclusion", mileStoneToSigma);
        HashMap<String, HashSet<String>> multi2 = multipleSets(includeMile, mileStoneToSigma);
        HashMap<String, HashSet<String>> multi3 = multipleSets(includeToSigma, sigmaSet);
        HashMap<String, HashSet<String>> unions = unionMap(unionMap(multi1, multi2), multi3);
        HashMap<String, HashSet<String>> inclusionProjection =
                intersectionMap(dcrGraph.getOneMap("TimeInclusion"), unions);

        HashMap<String, HashSet<TimeInclusion>> timeInclusionProjection = new HashMap<>();
        for (String from: inclusionProjection.keySet()){
            HashSet<TimeInclusion> temp = new HashSet<>();
            for (TimeInclusion timeInclusion: dcrGraph.getTimeInclusions().get(from)){
                if (inclusionProjection.get(from).contains(timeInclusion.getTo())){
                    temp.add(new TimeInclusion(timeInclusion));
                }
            }
            timeInclusionProjection.put(from, temp);
        }
        res.setTimeInclusions(timeInclusionProjection);

        // projection of exclusion relationship.
        HashSet<String> excludeToSigma = getARelationship(dcrGraph, "TimeExclusion", sigmaSet);
        HashSet<String> excludeCondition = getARelationship(dcrGraph, "TimeExclusion", conditionToSigma);
        HashMap<String, HashSet<String>> emulti1 = multipleSets(excludeCondition, conditionToSigma);
        HashSet<String> excludeMile = getARelationship(dcrGraph, "TimeExclusion", mileStoneToSigma);
        HashMap<String, HashSet<String>> emulti2 = multipleSets(excludeMile, mileStoneToSigma);
        HashMap<String, HashSet<String>> emulti3 = multipleSets(excludeToSigma, sigmaSet);
        HashMap<String, HashSet<String>> eunions = unionMap(unionMap(emulti1, emulti2), emulti3);
        HashMap<String, HashSet<String>> excludeProjection = intersectionMap(dcrGraph.getOneMap("TimeExclusion"), eunions);

        HashMap<String, HashSet<TimeExclusion>> timeExclusionProjection = new HashMap<>();
        for (String from: excludeProjection.keySet()){
            HashSet<TimeExclusion> temp = new HashSet<>();
            for (TimeExclusion timeExclusion: dcrGraph.getTimeExclusions().get(from)){
                if (excludeProjection.get(from).contains(timeExclusion.getTo())){
                    temp.add(new TimeExclusion(timeExclusion));
                }
            }
            timeExclusionProjection.put(from, temp);
        }
        res.setTimeExclusions(timeExclusionProjection);

        return res;
    }

    @Override
    public DCRGraph endUpProjection(final DCRGraph choreography, final DCRGraph sigmaProjection, String role) {
        DCRGraph res = new DCRGraph();
        // E'.
        HashSet<String> eventsPrime = getARolesInteractionAsReceiver(choreography, role);

        // M'.
        DCRMarking mPrime = new DCRMarking();
        HashSet<String> eExceptIn = new HashSet<>(sigmaProjection.getEvents());
        eExceptIn.removeAll(sigmaProjection.getDcrMarking().included);
        mPrime.included = new HashSet<>(eventsPrime);
        mPrime.included.removeAll(eExceptIn);

        // E|_{sigma} U E'.
        HashSet <String> events = new HashSet<>(eventsPrime);
        events.addAll(sigmaProjection.getEvents());
        res.setEvents(events);

        // M U M'
        DCRMarking markings = new DCRMarking();
        markings.included = new HashSet<>(sigmaProjection.getDcrMarking().included);
        markings.included.addAll(mPrime.included);
        markings.executed = new HashSet<>(sigmaProjection.getDcrMarking().executed);
        markings.pending = new HashSet<>(sigmaProjection.getDcrMarking().pending);
        res.setDcrMarking(markings);

        // Relationships.
        res.setTimeConditions(new HashMap<>(sigmaProjection.getTimeConditions()));
        res.setTimeResponses(new HashMap<>(sigmaProjection.getTimeResponses()));
        res.setTimeMilestones(new HashMap<>(sigmaProjection.getTimeMilestones()));
        res.setTimeInclusions(new HashMap<>(sigmaProjection.getTimeInclusions()));
        res.setTimeExclusions(new HashMap<>(sigmaProjection.getTimeExclusions()));

        return res;
    }

    @Override
    public DCRGraph Process(DCRGraph choreography, String role) throws Exception {
        HashSet<String> sigmaSet = getARolesSigma(choreography, role);
        DCRGraph sigmaProjection = sigmaProjection(choreography, sigmaSet);
        DCRGraph roleEndUpProjection = endUpProjection(choreography, sigmaProjection, role);
        // receivers.
        for (String event: choreography.getEventsReceivers().keySet()){
            if (choreography.getEventsReceivers().get(event).contains(role)){
                HashSet<String> temp = new HashSet<>();
                temp.add(role);
                roleEndUpProjection.getEventsInitiator().put(event, choreography.getEventsInitiator().get(event));
                roleEndUpProjection.getEventsReceivers().put(event, temp);
            }
        }
        // initiators.
        for (String event: choreography.getEventsInitiator().keySet()){
            if (choreography.getEventsInitiator().get(event).equals(role)){
                roleEndUpProjection.getEventsInitiator().put(event, event);
                roleEndUpProjection.getEventsReceivers()
                        .put(event, new HashSet<>(choreography.getEventsReceivers().get(event)));
            }
        }
        return roleEndUpProjection;
    }


    //functions for get a role's end-up projection.
    private HashSet<String> getARolesInteractionAsReceiver(DCRGraph dcrGraph, String role){
        HashSet<String> res = new HashSet<>();
        for (String key: dcrGraph.getEventsReceivers().keySet()){
            if (dcrGraph.getEventsReceivers().get(key).contains(role)){
                res.add(key);
            }
        }
        return res;
    }


    // functions for get a role's sigma projection.
    private void addAPair(HashMap<String, HashSet<String>> map, String key, String value){
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
    private HashMap<String, HashSet<String>> intersectionMap
    (HashMap<String, HashSet<String>> map1, HashMap<String, HashSet<String>> map2){
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


    // function for getARolesInteraction.
    private HashSet<String> getARelationship(DCRGraph dcrGraph, String relationship, HashSet<String> set)
            throws Exception {
        Class<?> clazz = Class.forName("models.dcrGraph.DCRGraph");
        Method method = clazz.getMethod("getOneMap", String.class);
        HashMap<String, HashSet<String>> relations =
                (HashMap<String, HashSet<String>>) method.invoke(dcrGraph, relationship);
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
