package models.dcrGraph;

import models.jsonDCR.Event;
import models.jsonDCR.TimeCondition;
import models.jsonDCR.TimeResponse;

import java.util.*;

/**
 * This class record the states and relationships for a timed DCR graph.
 * The states(that can be changed) include:
 *      1. markings.
 *      2. deadlines and conditional time.
 *      3. unsatisfied relationships. (When guards are computed)
 *
 * The state(will not be changed) include:
 *      1. relationships(with time information).
 *      2. */
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
    private HashMap<String, HashSet<String>> milestonesFor = new HashMap<>();
    private HashMap<String, HashSet<String>> excludesTo = new HashMap<>();
    private HashMap<String, HashSet<String>> includesTo = new HashMap<>();

    // A map to record the condition relationships
    // and response relationships with time.
    private HashMap<String, HashSet<TimeCondition>> timeConditions = new HashMap<>();
    private HashMap<String, HashSet<TimeResponse>> timeResponses = new HashMap<>();

    // A map to record the earliest executing time and deadlines.
    private  HashMap<String, Long> runTimeConditionMap = new HashMap<>();
    private HashMap<String, Long> runTimeResponseMap = new HashMap<>();

    // A map to record the unsatisfied relationships.
    private HashSet<String> unsatisfyRelationships = new HashSet<>();

    private HashMap<String, HashSet<String>> conditionMap(){
        HashMap<String, HashSet<String>> res = new HashMap<>();
        for (String event: timeConditions.keySet()){
            HashSet<String> temp = new HashSet<>();
            for (TimeCondition condition: timeConditions.get(event)){
                temp.add(condition.getTo());
            }
            res.put(event, temp);
        }
        return res;
    }

    private HashMap<String, HashSet<String>> responseMap(){
        HashMap<String, HashSet<String>> res = new HashMap<>();
        for (String event: timeResponses.keySet()){
            HashSet<String> temp = new HashSet<>();
            for (TimeResponse response: timeResponses.get(event)){
                temp.add(response.getTo());
            }
            res.put(event, temp);
        }
        return res;
    }

    /**
     * @Input: an event name
     * @Output: the events that depend on the input event.*/
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

        HashMap<String, HashSet<String>> conditionsFor = conditionMap();
        HashMap<String, HashSet<String>> responsesTo = responseMap();

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
        HashMap<String, HashSet<String>> conditionsFor = conditionMap();
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
            HashMap<String, HashSet<String>> responsesTo = responseMap();
            if (responsesTo.containsKey(event)&&responsesTo.get(event).contains(e)){
                res.add(event);
            }
        }
        return res;
    }

    /**
     * return if the DCR graph is projectable for a role.*/
    public boolean label(HashSet<String> dependEvents, String role) {
        for (String event: dependEvents){
            if ((!eventsInitiator.get(event).equals(role))&&(!eventsReceivers.get(event).contains(role))){
                return false;
            }
        }
        return true;
    }

    /**
     * @Input: event name
     * @Output: If the event is enabled by current DCR graph.*/
    public Boolean enabled(final String event, HashSet<String> satisfyRelationships){
        if (!events.contains(event)){
            return true;
        }

        if (!dcrMarking.included.contains(event)){
            return false;
        }

        try {
            // conditions: events which should be executed.
            final Set<String> inccon = new HashSet<>();
            HashMap<String, HashSet<String>> conditionsFor = conditionMap();
            for (String s: unsatisfyRelationships){
                if (s.split(" ")[2].equals("condition")&&s.split(" ")[1].equals(event)){
                    conditionsFor.get(s.split(" ")[0]).remove(event);
                }
            }
            for (String con: conditionsFor.keySet()){
                if (conditionsFor.get(con).contains(event)){
                    inccon.add(con);
                }
            }
            inccon.retainAll(dcrMarking.included);
            if(!dcrMarking.executed.containsAll(inccon)){
                return false;
            }

        }
        catch (Exception e){
            throw e;
        }

        // milestones: events cannot be pending.
        try {
            final Set<String> incmil = new HashSet<>();

            HashMap<String, HashSet<String>> milestones = new HashMap<>(milestonesFor);
            for (String s: unsatisfyRelationships){
                if (s.split(" ")[2].equals("milestone")&&s.split(" ")[1].equals(event)){
                    milestones.get(s.split(" ")[0]).remove(event);
                }
            }
            for (String con: milestones.keySet()){
                if (milestones.get(con).contains(event)){
                    incmil.add(con);
                }
            }
            incmil.retainAll(dcrMarking.included);
            for (final String p:dcrMarking.pending){
                if (incmil.contains(p)){
                    return false;
                }
            }
        }catch (Exception e){
            throw e;
        }

//         be enabled if now the time is greater than the condition time.
        if (runTimeConditionMap.containsKey(event) && System.currentTimeMillis()<=runTimeConditionMap.get(event)){
            System.out.println("run time condition not enabled!");
            return false;
        }

        // no later than the deadline.
        if (runTimeResponseMap.containsKey(event)&&System.currentTimeMillis()> runTimeResponseMap.get(event)){
            System.out.println("run time response not enabled!");
            return false;
        }

        return true;
    }

    /**
     * Execute the event with the unsatisfied relationships.*/
    public void execute(final String event, HashSet<String> unsatisfyRelationships){
        if(!events.contains(event)){
            return;
        }
//        if (!this.enabled(event)){
//            return;
//        }

        // reset all the relationships for this from event.
        HashSet<String> toBeRemove = new HashSet<>();
        for (String s: this.unsatisfyRelationships){
            if (s.split(" ")[0].equals(event)){
                toBeRemove.add(s);
            }
        }

        for (String s: toBeRemove){
            this.unsatisfyRelationships.remove(s);
        }
        this.unsatisfyRelationships.addAll(unsatisfyRelationships);

        // add executed, remove pending.
        dcrMarking.executed.add(event);
        dcrMarking.pending.remove(event);
        HashMap<String, HashSet<String>> responsesTo = responseMap();

        if (responsesTo.containsKey(event)){
            for (String s: unsatisfyRelationships){
                if (s.split(" ")[2].equals("response")){
                    responsesTo.get(event).remove(s.split(" ")[1]);
                }
            }
            dcrMarking.pending.addAll(responsesTo.get(event));
        }
        if (excludesTo.containsKey(event)){
            HashMap<String, HashSet<String>> excludes = new HashMap<>(excludesTo);
            for (String s: unsatisfyRelationships){
                if (s.split(" ")[2].equals("exclusion")){
                    excludes.get(event).remove(s.split(" ")[1]);
                }
            }
            dcrMarking.included.removeAll(excludes.get(event));
        }
        if (includesTo.containsKey(event)){
            HashMap<String, HashSet<String>> includes = new HashMap<>(includesTo);
            for (String s: unsatisfyRelationships){
                if (s.split(" ")[2].equals("inclusion")){
                    includes.get(event).remove(s.split(" ")[1]);
                }
            }
            dcrMarking.included.addAll(includes.get(event));
        }

        // add condition Time.
        if (timeConditions.containsKey(event)){
            for (TimeCondition timeCondition: timeConditions.get(event)){
                if (timeCondition.getTime()!=0){
                    Long condTime = System.currentTimeMillis()+timeCondition.getTime();
                    if (!runTimeConditionMap.containsKey(timeCondition.getTo()))
                        runTimeConditionMap.put(timeCondition.getTo(), condTime);
                    else {
                        runTimeConditionMap.put(timeCondition.getTo(), Math.max(condTime, timeCondition.getTime()));
                    }
                }
            }
        }

        // add deadlines.
        if (timeResponses.containsKey(event)){
            for (TimeResponse timeResponse: timeResponses.get(event)){
                if (timeResponse.getTime()!=0){
                    System.out.println("put: " +event);
                    Long condTime = System.currentTimeMillis()+timeResponse.getTime();
                    if (!runTimeResponseMap.containsKey(timeResponse.getTo()))
                        runTimeResponseMap.put(timeResponse.getTo(), condTime);
                    else {
                        runTimeResponseMap.put(timeResponse.getTo(), Math.min(condTime, timeResponse.getTime()));
                    }
                }
            }
        }
        return;
    }

    public Set<String> getIncludedPending(){
        HashSet<String> result = new HashSet<>(dcrMarking.included);

        result.retainAll(dcrMarking.pending);
        return result;
    }

    public boolean isAccepting(){
        return getIncludedPending().isEmpty();
    }

    // return the events whose receivers contain this role.
    public HashSet<String> getSubscribe(String role) {
        HashSet<String > res = new HashSet<>();
        for (String event: eventsReceivers.keySet()){
            if (eventsReceivers.get(event).contains(role)){
                res.add(event);
            }
        }
        return res;
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
        HashMap<String, HashSet<String>> conditionsFor = conditionMap();
        return conditionsFor;
    }


    public HashMap<String, HashSet<String>> getMilestonesFor() {
        return milestonesFor;
    }

    public void setMilestonesFor(HashMap<String, HashSet<String>> milestonesFor) {
        this.milestonesFor = milestonesFor;
    }

    public HashMap<String, HashSet<String>> getResponsesTo() {
        HashMap<String, HashSet<String>> responsesTo = responseMap();
        return responsesTo;
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

    public HashMap<String, HashSet<TimeCondition>> getTimeConditions() {
        return timeConditions;
    }

    public void setTimeConditions(HashMap<String, HashSet<TimeCondition>> timeConditions) {
        this.timeConditions = timeConditions;
    }

    public HashMap<String, HashSet<TimeResponse>> getTimeResponses() {
        return timeResponses;
    }

    public void setTimeResponses(HashMap<String, HashSet<TimeResponse>> timeResponses) {
        this.timeResponses = timeResponses;
    }
}
