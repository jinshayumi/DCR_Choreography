package models.dcrGraph;

import com.github.javaparser.ast.expr.Expression;
import models.jsonDCR.EventData;
import models.jsonDCR.timeRelationship.*;
import models.parser.ExpParser;
import services.entities.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Time;
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

    // maps to record the initiator and the receivers for an interaction.
    private HashMap<String, String> eventsInitiator = new HashMap<>();
    private HashMap<String, HashSet<String>> eventsReceivers = new HashMap<>();

    // M: marking.
    private DCRMarking dcrMarking = new DCRMarking();

    // L: Labels.
    private HashSet<String> labels = new HashSet<>();

    // l: labelling function
    private HashMap<String, String> labelFunc = new HashMap<>();

    // A map to record the condition relationships
    // and response relationships with time.
    private HashMap<String, HashSet<TimeCondition>> timeConditions = new HashMap<>();
    private HashMap<String, HashSet<TimeResponse>> timeResponses = new HashMap<>();
    private HashMap<String, HashSet<TimeExclusion>> timeExclusions = new HashMap<>();
    private HashMap<String, HashSet<TimeInclusion>> timeInclusions = new HashMap<>();
    private HashMap<String, HashSet<TimeMilestone>> timeMilestones = new HashMap<>();

    // A map to record the earliest executing time and deadlines.
    private  HashMap<String, Long> runTimeConditionMap = new HashMap<>();
    private HashMap<String, Long> runTimeResponseMap = new HashMap<>();

    // A map to store the data in each event.
    private HashMap<String, Data> dataMap = new HashMap<>();

    // A map to store the data calculation logic for each event.
    private HashMap<String, EventData> dataLogicMap = new HashMap<>();

//    public DCRGraph(){
//        for (String event:events){
//            dataMap.put(event, new Data())
//        }
//    }

    public <T> HashMap<String, HashSet<String>> getOneMap(String relation)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        HashMap<String, HashSet<String>> res = new HashMap<>();
        Class clazz = this.getClass();
        HashMap<String, HashSet<T>> t =
                (HashMap<String, HashSet<T>>) clazz.getMethod("get"+relation +"s").invoke(this);
        for (String key: t.keySet()){
            res.put(key, new HashSet<>());
            for (T ele: t.get(key)){
                Class<?> clazzRelation = Class.forName("models.jsonDCR.timeRelationship." + relation);
                Method methodTo = clazzRelation.getMethod("getTo");
                String to = (String) methodTo.invoke(ele);
                res.get(key).add(to);
            }
        }
        return res;
    }


    /**
     * @Input: an event name
     * @Output: the events that depend on the input event.*/
    public HashSet<String> getOnesDependency(String participant)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
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
    private boolean FiveRelation(String identity, String identity1)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (getOneMap("TimeInclusion").containsKey(identity)&&
                getOneMap("TimeInclusion").get(identity).contains(identity1)){
            return true;
        }
        if (getOneMap("TimeExclusion").containsKey(identity)&&
                getOneMap("TimeExclusion").get(identity).contains(identity1)){
            return true;
        }
        if (getOneMap("TimeMilestone").containsKey(identity)&&
                getOneMap("TimeMilestone").get(identity).contains(identity1)){
            return true;
        }

        if (getOneMap("TimeCondition").containsKey(identity)
                &&getOneMap("TimeCondition").get(identity).contains(identity1)){
            return true;
        }
        if (getOneMap("TimeResponse").containsKey(identity)
                &&getOneMap("TimeResponse").get(identity).contains(identity1)){
            return true;
        }
        return false;
    }

    // condition 3.
    private HashSet<String> findCondition3(String identity)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        HashSet<String> res = new HashSet<>();
        for (String e: events){
            if (ConditionOrMilestone(e, identity)){
                HashSet<String> inOrEx = findIncludeOrExclude(e);
                res.addAll(inOrEx);
            }
        }
        return res;
    }

    private boolean ConditionOrMilestone(String e, String identity)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (getOneMap("TimeCondition").containsKey(e)
                &&getOneMap("TimeCondition").get(e).contains(identity)){
            return true;
        }
        if (getOneMap("TimeMilestone").containsKey(e)
                &&getOneMap("TimeMilestone").get(e).contains(identity)){
            return true;
        }
        return false;
    }

    private HashSet<String> findIncludeOrExclude(String e)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        HashSet<String> res = new HashSet<>();
        for (String event: events){
            if (IncludeOrExclude(event, e)){
                res.add(event);
            }
        }
        return res;
    }

    private boolean IncludeOrExclude(String e, String event)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (getOneMap("TimeInclusion").containsKey(e)
                &&getOneMap("TimeInclusion").get(e).contains(event)){
            return true;
        }
        if (getOneMap("TimeExclusion").containsKey(e)
                &&getOneMap("TimeExclusion").get(e).contains(event)){
            return true;
        }
        return false;
    }

    // condition 4.
    private HashSet<String> findCondition4(String identity)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        HashSet<String> res = new HashSet<>();
        for(String e: events){
            if (MileStone(e, identity)){
                HashSet<String> response = findResponse(e);
                res.addAll(response);
            }
        }
        return res;
    }

    private boolean MileStone(String e, String identity)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (getOneMap("TimeMilestone").containsKey(e)
                &&(getOneMap("TimeMilestone").get(e).contains(identity))){
            return true;
        }
        return false;
    }

    private HashSet<String> findResponse(String e)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        HashSet<String> res = new HashSet<>();
        for (String event:events){
            HashMap<String, HashSet<String>> responsesTo = getOneMap("TimeResponse");
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
    public Boolean enabled(final String event)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (!events.contains(event)){
            return true;
        }

        if (!dcrMarking.included.contains(event)){
            // find some events chain to make this event included.
//            for (String key: getOneMap("TimeInclusion").keySet()){
//                if (getOneMap("TimeInclusion").get(key).contains(event)){
//
//                }
//            }
            return false;
        }

        try {
            // conditions: events which should be executed.
            final HashSet<String> inccon = findSatisfyCondition(event);

            inccon.retainAll(dcrMarking.included);
            if(!dcrMarking.executed.containsAll(inccon)){
                // find some events chain to make the condition event executed.
                return false;
            }
        }
        catch (Exception e){
            throw e;
        }

        // milestones: events cannot be pending.
        try {
            final Set<String> incmil = findSatisfyMilestone(event);

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
            System.out.println("run time Condition not enabled! (Too Early)");
            return false;
        }

        // no later than the deadline.
        if (runTimeResponseMap.containsKey(event)&&System.currentTimeMillis()> runTimeResponseMap.get(event)){
            System.out.println("run time Response not enabled! (Too Late)");
            return false;
        }

        return true;
    }

    private Set<String> findSatisfyMilestone(String event) {
        HashSet<String> res = new HashSet<>();
        for (String key: timeMilestones.keySet()){
            for (TimeMilestone timeMilestone: timeMilestones.get(key)){
                if (timeMilestone.getTo().equals(event)){
                    if (satisfy(timeMilestone.getCondition())){
                        res.add(key);
                    }
                }
            }
        }
        return res;
    }

    private HashSet<String> findSatisfyCondition(String event) {
        HashSet<String> res = new HashSet<>();
        for (String key: timeConditions.keySet()){
            for (TimeCondition timeCondition: timeConditions.get(key)){
                if (timeCondition.getTo().equals(event)){
                    if (satisfy(timeCondition.getCondition())){
                        res.add(key);
                    }
                }
            }
        }
        return res;
    }

    private boolean satisfy(String condition) {
        Expression expression = ExpParser.parseExp(condition);
        BoolData res = (BoolData) ExpParser.calculate(dataMap, expression);
        return res.getData();
    }

    /**
     * Execute the event with the unsatisfied relationships.*/
    public void execute(final String event) {
        if(!events.contains(event)){
            return;
        }

        // add executed, remove pending.
        dcrMarking.executed.add(event);
        dcrMarking.pending.remove(event);
        if(timeResponses.containsKey(event)){
            for (TimeResponse timeResponse: timeResponses.get(event)){
                if(satisfy(timeResponse.getCondition())){
                    dcrMarking.pending.add(timeResponse.getTo());
                }
            }
        }

        if (timeExclusions.containsKey(event)){
            for (TimeExclusion timeExclusion: timeExclusions.get(event)){
                if (satisfy(timeExclusion.getCondition())){
                    dcrMarking.included.remove(timeExclusion.getTo());
                }
            }
        }

        if (timeInclusions.containsKey(event)){
            for (TimeInclusion timeInclusion: timeInclusions.get(event)){
                if (satisfy(timeInclusion.getCondition())){
                    dcrMarking.included.add(timeInclusion.getTo());
                }
            }
        }

        // add condition Time.
        if (timeConditions.containsKey(event)){
            for (TimeCondition timeCondition: timeConditions.get(event)){
                if (satisfy(timeCondition.getCondition())&&timeCondition.getTime()!=0){
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
                if (satisfy(timeResponse.getCondition()) && timeResponse.getTime()!=0){
                    Long responseTime = System.currentTimeMillis()+timeResponse.getTime();
                    if (!runTimeResponseMap.containsKey(timeResponse.getTo()))
                        runTimeResponseMap.put(timeResponse.getTo(), responseTime);
                    else {
                        runTimeResponseMap.put(timeResponse.getTo(), Math.min(responseTime, timeResponse.getTime()));
                    }
                }
            }
        }
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

    // functions below generate the approximation for locks.
    private HashSet<String> getBusyEvents()
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        HashSet<String> res = new HashSet<>();

        // the events whose initial states are pending.
        for (String s: dcrMarking.pending){
            res.add(s);
        }
        // events response to.
        for (String from: getOneMap("TimeResponse").keySet()){
            res.addAll(getOneMap("TimeResponse").get(from));
        }
        return res;
    }


    // deadlock checking.
    private boolean deadlock = false;
    public boolean checkDeadLock()
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        HashSet<String> possiblePending = getBusyEvents();

        for (String possible : possiblePending){
            HashSet<String> traversed = new HashSet<>();
            dfsCheckDeadLock(possible, traversed);
        }
        return deadlock;
    }

    private void dfsCheckDeadLock(String temp, HashSet<String> headEvent)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (headEvent.contains(temp)){
            changeDeadLock();
            return;
        }
        else{
            HashSet<String> possibles = new HashSet<>();
            possibles.addAll(getOneMap("TimeCondition").get(temp));
            possibles.addAll(getOneMap("TimeMilestone").get(temp));
            for (String possible: possibles){
                headEvent.add(temp);
                dfsCheckDeadLock(possible, headEvent);
                headEvent.remove(temp);
            }
        }
    }

    private void changeDeadLock(){
        if (!deadlock){
            deadlock = true;
        }
    }
    // end of deadlock part.

    private HashSet<String> inhibitors = new HashSet<>();
    public boolean checkTimeLock()
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        HashSet<String> busyEvents = getBusyEvents();
        // 1. acyclic.
        if (checkDeadLock()){
            return true;
        }

        // 2. if e o-> f or e ->+ f, there is a path from e to f.
        for (String busy : busyEvents){
            //
            dfsInhibitors(busy);
            for (String key: getOneMap("TimeResponse").keySet()){
                for (String val: getOneMap("TimeResponse").get(key)){
                    if (inhibitors.contains(key)&&inhibitors.contains(val)){
                        if (!havePath(key, val)){
                            return true;
                        }
                    }
                }
            }

            for (String key: getOneMap("TimeInclusion").keySet()){
                for (String val: getOneMap("TimeResponse").get(key)){
                    if (inhibitors.contains(key)&&inhibitors.contains(val)){
                        if (!havePath(key, val)){
                            return true;
                        }
                    }
                }
            }
        }

        // 3. if e ->o f with time k, then k = 0.
        for (String key: getTimeConditions().keySet()){
            for (TimeCondition tc: getTimeConditions().get(key)){
                if (inhibitors.contains(key)&&inhibitors.contains(tc.getTo())){
                    if (tc.getTime()!= 0){
                        return true;
                    }
                }
            }
        }

        return false;
    }


    private boolean havePath(String from, String to)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        HashSet<String> reachable = new HashSet<>();
        HashSet<String> oneReachable =  dfsOneReachable(from, reachable);
        if (oneReachable.contains(to)){
            return true;
        }
        else return false;
    }

    private HashSet<String> dfsOneReachable(String from, HashSet<String> reachable)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        reachable.add(from);
        HashSet<String> temp = new HashSet<>();
        if (getOneMap("TimeCondition").containsKey(from)){
            for (String val: getOneMap("TimeCondition").get(from)){
                dfsOneReachable(val, reachable);
            }
        }
        return reachable;
    }

    private void dfsInhibitors(String busy)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        inhibitors.add(busy);
        HashSet<String> temp = new HashSet<>();
        for (String key: getOneMap("TimeCondition").keySet()){
            if(getOneMap("TimeCondition").get(key).contains(busy)){
                temp.add(key);
            }
        }
        for (String key: getOneMap("TimeMilestone").keySet()){
            if(getOneMap("TimeCondition").get(key).contains(busy)){
                temp.add(key);
            }
        }
        for (String oneInhibitor: temp){
            dfsInhibitors(oneInhibitor);
        }
    }

    // calculate and update.
    public void calculateAnEvent(String event){
        Data data = ExpParser.calculate(dataMap, ExpParser.parseExp(dataLogicMap.get(event).getLogic()));
        updateEventData(event, data);
    }

    // only update.
    public void updateEventData(String event, Data data){
        dataMap.put(event, data);
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

    public HashMap<String, HashSet<TimeExclusion>> getTimeExclusions() {
        return timeExclusions;
    }

    public void setTimeExclusions(HashMap<String, HashSet<TimeExclusion>> timeExclusions) {
        this.timeExclusions = timeExclusions;
    }

    public HashMap<String, HashSet<TimeInclusion>> getTimeInclusions() {
        return timeInclusions;
    }

    public void setTimeInclusions(HashMap<String, HashSet<TimeInclusion>> timeInclusions) {
        this.timeInclusions = timeInclusions;
    }

    public HashMap<String, HashSet<TimeMilestone>> getTimeMilestones() {
        return timeMilestones;
    }

    public void setTimeMilestones(HashMap<String, HashSet<TimeMilestone>> timeMilestones) {
        this.timeMilestones = timeMilestones;
    }

    public HashMap<String, Data> getDataMap() {
        return dataMap;
    }

    public void setDataMap(HashMap<String, Data> dataMap) {
        this.dataMap = dataMap;
    }

    public HashMap<String, EventData> getDataLogicMap() {
        return dataLogicMap;
    }

    public void setDataLogicMap(HashMap<String, EventData> dataLogicMap) {
        this.dataLogicMap = dataLogicMap;
    }


    /**
     * this function checks that
     *      if all the events in the guards contains the initiator.
     *      According to the third property in Definition 15.
     * ToDo: the implementation now contains
     * duplicate code for five relationships.*/
    public boolean checkGuardEvents() {
        for (String key: timeMilestones.keySet()){
            HashSet<TimeMilestone> onesTimeMilestone = timeMilestones.get(key);
            for (TimeMilestone timeMilestone: onesTimeMilestone){
                String guard = timeMilestone.getCondition();
                Expression expression = ExpParser.parseExp(guard);
                HashSet<String> eventsInExpression = ExpParser.getEventsInExpression(expression);
                for (String eventInExpression: eventsInExpression){
                    String initiator = getEventsInitiator().get(timeMilestone.getTo());
                    if ((!this.getEventsInitiator().get(eventInExpression).equals(initiator))
                            &&(!this.getEventsReceivers().get(eventInExpression).contains(initiator))){
                        return false;
                    }
                }

            }
        }
        for (String key: timeConditions.keySet()){
            HashSet<TimeCondition> onesTimeCondition = timeConditions.get(key);
            for (TimeCondition timeCondition: onesTimeCondition){
                String guard = timeCondition.getCondition();
                Expression expression = ExpParser.parseExp(guard);
                HashSet<String> eventsInExpression = ExpParser.getEventsInExpression(expression);
                for (String eventInExpression: eventsInExpression){
                    String initiator = getEventsInitiator().get(timeCondition.getTo());
                    if ((!this.getEventsInitiator().get(eventInExpression).equals(initiator))
                            &&(!this.getEventsReceivers().get(eventInExpression).contains(initiator))){
                        return false;
                    }
                }

            }
        }
        for (String key: timeResponses.keySet()){
            HashSet<TimeResponse> onesTimeResponse = timeResponses.get(key);
            for (TimeResponse timeResponse: onesTimeResponse){
                String guard = timeResponse.getCondition();
                Expression expression = ExpParser.parseExp(guard);
                HashSet<String> eventsInExpression = ExpParser.getEventsInExpression(expression);
                for (String eventInExpression: eventsInExpression){
                    String initiator = getEventsInitiator().get(timeResponse.getTo());
                    if ((!this.getEventsInitiator().get(eventInExpression).equals(initiator))
                            &&(!this.getEventsReceivers().get(eventInExpression).contains(initiator))){
                        return false;
                    }
                }

            }
        }
        for (String key: timeInclusions.keySet()){
            HashSet<TimeInclusion> onesTimeInclusion = timeInclusions.get(key);
            for (TimeInclusion timeInclusion: onesTimeInclusion){
                String guard = timeInclusion.getCondition();
                Expression expression = ExpParser.parseExp(guard);
                HashSet<String> eventsInExpression = ExpParser.getEventsInExpression(expression);
                for (String eventInExpression: eventsInExpression){
                    String initiator = getEventsInitiator().get(timeInclusion.getTo());
                    if ((!this.getEventsInitiator().get(eventInExpression).equals(initiator))
                            &&(!this.getEventsReceivers().get(eventInExpression).contains(initiator))){
                        return false;
                    }
                }

            }
        }
        for (String key: timeExclusions.keySet()){
            HashSet<TimeExclusion> onesTimeExclusion = timeExclusions.get(key);
            for (TimeExclusion timeExclusion: onesTimeExclusion){
                String guard = timeExclusion.getCondition();
                Expression expression = ExpParser.parseExp(guard);
                HashSet<String> eventsInExpression = ExpParser.getEventsInExpression(expression);
                for (String eventInExpression: eventsInExpression){
                    String initiator = getEventsInitiator().get(timeExclusion.getTo());
                    if ((!this.getEventsInitiator().get(eventInExpression).equals(initiator))
                            &&(!this.getEventsReceivers().get(eventInExpression).contains(initiator))){
                        return false;
                    }
                }

            }
        }
        return true;
    }
}
