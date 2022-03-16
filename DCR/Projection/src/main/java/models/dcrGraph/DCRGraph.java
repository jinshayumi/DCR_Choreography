package models.dcrGraph;

import models.jsonDCR.Event;
import models.jsonDCR.TimeCondition;
import models.jsonDCR.TimeResponse;

import java.util.*;

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

    private HashMap<String, HashSet<TimeCondition>> timeConditions = new HashMap<>();
    private HashMap<String, HashSet<TimeResponse>> timeResponses = new HashMap<>();

    private  HashMap<String, Long> runTimeConditionMap = new HashMap<>();
    private HashMap<String, Long> runTimeResponseMap = new HashMap<>();

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

    // return if the role is pro
    public boolean label(HashSet<String> dependEvents, String role) {
        for (String event: dependEvents){
            if ((!eventsInitiator.get(event).equals(role))&&(!eventsReceivers.get(event).contains(role))){
                return false;
            }
        }
        return true;
    }

    // if the event is enabled.
    public Boolean enabled(final String event){
        if (!events.contains(event)){
            return true;
        }

        if (!dcrMarking.included.contains(event)){
            return false;
        }

        try {
            // conditions that are in
            final Set<String> inccon = new HashSet<>();
            HashMap<String, HashSet<String>> conditionsFor = conditionMap();
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

        try {
            final Set<String> incmil = new HashSet<>();
            for (String con: milestonesFor.keySet()){
                if (milestonesFor.get(con).contains(event)){
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

        // be enabled if now the time is greater than the condition time.
        if (runTimeConditionMap.containsKey(event) && System.currentTimeMillis()/1000<=runTimeConditionMap.get(event)){
            return false;
        }

        // no later than the deadline.
        if (runTimeResponseMap.containsKey(event)&&System.currentTimeMillis()/1000> runTimeResponseMap.get(event)){
            return false;
        }

        return true;
    }

    // execute the event
    public void execute(final String event){
        if(!events.contains(event)){
            return;
        }

        if (!this.enabled(event)){
            return;
        }
        dcrMarking.executed.add(event);
        dcrMarking.pending.remove(event);
        HashMap<String, HashSet<String>> responsesTo = responseMap();

        if (responsesTo.containsKey(event)){
            dcrMarking.pending.addAll(responsesTo.get(event));
        }
        if (excludesTo.containsKey(event)){
            dcrMarking.included.removeAll(excludesTo.get(event));
        }
        if (includesTo.containsKey(event)){
            dcrMarking.included.addAll(includesTo.get(event));
        }

        // condition Time.
        if (timeConditions.containsKey(event)){
            for (TimeCondition timeCondition: timeConditions.get(event)){
                if (timeCondition.getTime()!=0){
                    Long condTime = System.currentTimeMillis()/1000+timeCondition.getTime();
                    if (!runTimeConditionMap.containsKey(timeCondition.getTo())) runTimeConditionMap.
                            put(timeCondition.getTo(), condTime);
                    else {
                        runTimeConditionMap.put(timeCondition.getTo(), Math.max(condTime, timeCondition.getTime()));
                    }
                }
            }
        }

        // deadline.
        if (timeResponses.containsKey(event)){
            for (TimeResponse timeResponse: timeResponses.get(event)){
                if (timeResponse.getTime()!=-1){
                    Long condTime = System.currentTimeMillis()/1000+timeResponse.getTime();
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
