package models.dcrGraph;

import models.jsonDCR.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DCRGraph {
    // E: events
    protected HashSet<String> events = new HashSet<>();

    private HashMap<String, String> eventsInitiator = new HashMap<>();
    private HashMap<String, HashSet<String>> eventsReceivers = new HashMap<>();

    // M: marking.
    private DCRMarking dcrMarking = new DCRMarking();

    // L: Labels.
    private HashSet<String> labels = new HashSet<>();

    // l: labelling function
    private HashMap<String, String> labelFunc = new HashMap<>();

    // 5 relationships.
    private HashMap<String, HashSet<String>> conditionsFor = new HashMap<>();
    private HashMap<String, HashSet<String>> milestonesFor = new HashMap<>();
    private HashMap<String, HashSet<String>> responsesTo = new HashMap<>();
    private HashMap<String, HashSet<String>> excludesTo = new HashMap<>();
    private HashMap<String, HashSet<String>> includesTo = new HashMap<>();

    public HashSet<String> getOnesDependency(String participant) {
        HashSet<String> res = new HashSet<>();
        // 1. itself
        res.add(participant);

        // 2. e' -> e.
        for (String e: events){
            if(FiveRelation(e, participant)){
                res.add(e);
            }
        }

        // 3.
        HashSet<String> condition3 = findCondition3(participant);
        res.addAll(condition3);

        // 4.
        HashSet<String> condition4 = findCondition4(participant);
        res.addAll(condition4);
        return res;
    }

    // condition 2.
    private boolean FiveRelation(String identity, String identity1) {
        if (includesTo.containsKey(identity)&&includesTo.get(identity).contains(identity1)){
            return true;
        }
        if (excludesTo.containsKey(identity)&&excludesTo.get(identity).contains(identity1)){
            return true;
        }
        if (milestonesFor.containsKey(identity)&&milestonesFor.get(identity).contains(identity1)){
            return true;
        }
        if (conditionsFor.containsKey(identity)&&conditionsFor.get(identity).contains(identity1)){
            return true;
        }
        if (responsesTo.containsKey(identity)&&responsesTo.get(identity).contains(identity1)){
            return true;
        }
        return false;
    }


    // condition 3.
    private HashSet<String> findCondition3(String identity) {
        HashSet<String> res = new HashSet<>();
        for (String e: events){
            if (ConditionOrMilestone(e, identity)){
                HashSet<String> inOrEx = findIncludeOrExclude(e);
                res.addAll(inOrEx);
            }
        }
        return res;
    }

    private boolean ConditionOrMilestone(String e, String identity) {
        if (conditionsFor.containsKey(e)&&conditionsFor.get(e).contains(identity)){
            return true;
        }
        if (milestonesFor.containsKey(e)&&milestonesFor.get(e).contains(identity)){
            return true;
        }
        return false;
    }

    private HashSet<String> findIncludeOrExclude(String e) {
        HashSet<String> res = new HashSet<>();
        for (String event: events){
            if (IncludeOrExclude(event, e)){
                res.add(event);
            }
        }
        return res;
    }

    private boolean IncludeOrExclude(String e, String event) {
        if (includesTo.containsKey(e)&&includesTo.get(e).contains(event)){
            return true;
        }
        if (excludesTo.containsKey(e)&&excludesTo.get(e).contains(event)){
            return true;
        }
        return false;
    }

    // condition 4.
    private HashSet<String> findCondition4(String identity){
        HashSet<String> res = new HashSet<>();
        for(String e: events){
            if (MileStone(e, identity)){
                HashSet<String> response = findResponse(e);
                res.addAll(response);
            }
        }
        return res;
    }

    private boolean MileStone(String e, String identity) {
        if (milestonesFor.containsKey(e)&&milestonesFor.get(e).contains(identity)){
            return true;
        }
        return false;
    }

    private HashSet<String> findResponse(String e) {
        HashSet<String> res = new HashSet<>();
        for (String event:events){
            if (responsesTo.containsKey(event)&&responsesTo.get(event).contains(e)){
                res.add(event);
            }
        }
        return res;
    }

    // return if the role is pro
    public boolean label(HashSet<String> dependEvents, String role) {
        for (String event: dependEvents){
            if ((!eventsInitiator.get(event).equals(role))&&(!eventsReceivers.get(event).contains(role))){
                return false;
            }
        }
        return true;
    }


    // getters and setters.
    public HashSet<String> getEvents() {
        return events;
    }

    public void setEvents(HashSet<String> events) {
        this.events = events;
    }

    public DCRMarking getDcrMarking() {
        return dcrMarking;
    }

    public void setDcrMarking(DCRMarking dcrMarking) {
        this.dcrMarking = dcrMarking;
    }

    public HashSet<String> getLabels() {
        return labels;
    }

    public void setLabels(HashSet<String> labels) {
        this.labels = labels;
    }

    public HashMap<String, String> getLabelFunc() {
        return labelFunc;
    }

    public void setLabelFunc(HashMap<String, String> labelFunc) {
        this.labelFunc = labelFunc;
    }

    public HashMap<String, HashSet<String>> getConditionsFor() {
        return conditionsFor;
    }

    public void setConditionsFor(HashMap<String, HashSet<String>> conditionsFor) {
        this.conditionsFor = conditionsFor;
    }

    public HashMap<String, HashSet<String>> getMilestonesFor() {
        return milestonesFor;
    }

    public void setMilestonesFor(HashMap<String, HashSet<String>> milestonesFor) {
        this.milestonesFor = milestonesFor;
    }

    public HashMap<String, HashSet<String>> getResponsesTo() {
        return responsesTo;
    }

    public void setResponsesTo(HashMap<String, HashSet<String>> responsesTo) {
        this.responsesTo = responsesTo;
    }

    public HashMap<String, HashSet<String>> getExcludesTo() {
        return excludesTo;
    }

    public void setExcludesTo(HashMap<String, HashSet<String>> excludesTo) {
        this.excludesTo = excludesTo;
    }

    public HashMap<String, HashSet<String>> getIncludesTo() {
        return includesTo;
    }

    public void setIncludesTo(HashMap<String, HashSet<String>> includesTo) {
        this.includesTo = includesTo;
    }

    public HashMap<String, String> getEventsInitiator() {
        return eventsInitiator;
    }

    public void setEventsInitiator(HashMap<String, String> eventsInitiator) {
        this.eventsInitiator = eventsInitiator;
    }

    public HashMap<String, HashSet<String>> getEventsReceivers() {
        return eventsReceivers;
    }

    public void setEventsReceivers(HashMap<String, HashSet<String>> eventsReceivers) {
        this.eventsReceivers = eventsReceivers;
    }
}
