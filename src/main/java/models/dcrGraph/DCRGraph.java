package models.dcrGraph;

import com.github.javaparser.ast.expr.Expression;
import models.jsonDCR.EventData;
import models.jsonDCR.timeRelationship.*;
import models.parser.ExpParser;
import services.entities.data.BoolData;
import services.entities.data.Data;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
public class DCRGraph implements Serializable {
    // E: events
    protected HashSet<String> events = new HashSet<>();

    // maps to record the initiator and the receivers for an interaction.
    private HashMap<String, String> eventsInitiator = new HashMap<>();
    private HashMap<String, HashSet<String>> eventsReceivers = new HashMap<>();

    // L: Labels.
    private HashSet<String> labels = new HashSet<>();

    // l: labelling function
    private HashMap<String, String> labelFunc = new HashMap<>();

    // 5 relationships' map
    private HashMap<String, HashSet<TimeCondition>> timeConditions = new HashMap<>();
    private HashMap<String, HashSet<TimeResponse>> timeResponses = new HashMap<>();
    private HashMap<String, HashSet<TimeExclusion>> timeExclusions = new HashMap<>();
    private HashMap<String, HashSet<TimeInclusion>> timeInclusions = new HashMap<>();
    private HashMap<String, HashSet<TimeMilestone>> timeMilestones = new HashMap<>();

    // A map to store the data calculation logic for each event.
    private HashMap<String, EventData> dataLogicMap = new HashMap<>();

    // states in a timed DCR Choreography
    // A map to record the earliest executing time and deadlines. (State)
    // M: marking. (State)
    private DCRMarking dcrMarking = new DCRMarking();

    // A map to record the condition relationships
    // and response relationships with time.
    private  HashMap<String, Long> runTimeConditionMap = new HashMap<>();
    private HashMap<String, Long> runTimeResponseMap = new HashMap<>();

    // A map to store the data in each event.
    private HashMap<String, Data> dataMap = new HashMap<>();



    public DCRGraph deepClone() throws IOException, ClassNotFoundException {
        // write the object to stream.
        ByteArrayOutputStream bo=new ByteArrayOutputStream();
        ObjectOutputStream oo=new ObjectOutputStream(bo);
        oo.writeObject(this);
        // read the object from stream.
        ByteArrayInputStream bi=new ByteArrayInputStream(bo.toByteArray());
        ObjectInputStream oi=new ObjectInputStream(bi);
        return (DCRGraph) (oi.readObject());
    }


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
    public HashSet<String> getOnesDependency(String eventName)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        HashSet<String> res = new HashSet<>();
        // 1. itself
        res.add(eventName);

        // 2. e' -> e.
        for (String e: events){
            if(FiveRelation(e, eventName)){
                res.add(e);
            }
        }

        // 3. e' (include/exclude) e'' (condition/milestone) e
        HashSet<String> condition3 = findCondition3(eventName);
        res.addAll(condition3);

        // 4. e' (response) e'' (milestone) e
        HashSet<String> condition4 = findCondition4(eventName);
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
     * return if a role is one of the participant in an interaction .*/
    public boolean isParticipant(HashSet<String> dependEvents, String role) {
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
        if (runTimeConditionMap.containsKey(event) && System.currentTimeMillis()<runTimeConditionMap.get(event)){
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

    public Boolean enabled(final String event, Long time)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (!events.contains(event)){
            return true;
        }

        if (!dcrMarking.included.contains(event)){
            return false;
        }

        // check all the deadlines are less than the expected executing time.
        for (String deadlineEvent: runTimeResponseMap.keySet()){
            if (dcrMarking.included.contains(deadlineEvent)&&runTimeResponseMap.get(deadlineEvent)<time){
                System.out.println("Time advancing is not allowed.");
                return false;
            }
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
        if (runTimeConditionMap.containsKey(event) && time<runTimeConditionMap.get(event)){
            System.out.println("run time Condition not enabled! (Too Early)");
            return false;
        }

        // no later than the deadline.
        if (runTimeResponseMap.containsKey(event)&& time> runTimeResponseMap.get(event)){
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
    public void execute(final String event, Long time)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if(!events.contains(event)){
            return;
        }

        // add executed, remove pending.
        dcrMarking.executed.add(event);
        dcrMarking.pending.remove(event);
        // remove from deadline map.
        runTimeResponseMap.remove(event);
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
                    Long condTime = time+timeCondition.getTime();
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
                    Long responseTime = time+timeResponse.getTime();
                    // update the deadline directly.
                    runTimeResponseMap.put(timeResponse.getTo(), responseTime);

//                    if (!runTimeResponseMap.containsKey(timeResponse.getTo()))
//                        runTimeResponseMap.put(timeResponse.getTo(), responseTime);
//                    else {
//                        runTimeResponseMap.put(timeResponse.getTo(), Math.min(responseTime, timeResponse.getTime()));
//                    }
                }
            }
        }
    }

    /**
     * Execute the event with the unsatisfied relationships.*/
    public void execute(final String event)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if(!events.contains(event)){
            return;
        }

        // add executed, remove pending.
        dcrMarking.executed.add(event);
        dcrMarking.pending.remove(event);
        // remove from deadline map.
        runTimeResponseMap.remove(event);
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

    // in asynchronous: return the events whose receivers contain this role.
    public HashSet<String> getSubscribe(String role) {
        HashSet<String > res = new HashSet<>();
        for (String event: eventsReceivers.keySet()){
            if (eventsReceivers.get(event).contains(role)){
                res.add(event);
            }
        }
        return res;
    }

    // in synchronous: return the topics that a role should subscribe.
    public HashSet<String> getSyncSubscribe(String role) {
        HashSet<String > res = new HashSet<>();
        for (String event: eventsInitiator.keySet()){
            if (eventsInitiator.get(event).equals(role)){
                res.add("reject@"+event);
                res.add("execute@"+event);
            }
        }
        for (String event: eventsReceivers.keySet()){
            if (eventsReceivers.get(event).contains(role)){
                res.add("execute@"+event);
            }
        }
        return res;
    }



    // functions below generate the approximation for locks.
    private HashSet<String> getBusyEvents()
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        HashSet<String> res = new HashSet<>();

        // the events whose initial states are included and pending.
        for (String s: dcrMarking.pending){
            if (dcrMarking.included.contains(s)){
                res.add(s);
            }
        }
        // events response to.
        for (String from: getOneMap("TimeResponse").keySet()){
            res.addAll(getOneMap("TimeResponse").get(from));
        }
        return res;
    }


    // deadlock checking.
    private boolean deadlockfree = true;
    public boolean checkDeadLock()
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        HashSet<String> possiblePending = getBusyEvents();

        for (String possible : possiblePending){
            HashSet<String> traversed = new HashSet<>();
            dfsCheckDeadLock(possible, traversed);
        }
        return deadlockfree;
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
        if (deadlockfree){
            deadlockfree = false;
        }
    }
    // end of deadlock part.

    private HashSet<String> inhibitors = new HashSet<>();
    public boolean checkTimeLock()
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        HashSet<String> busyEvents = getBusyEvents();
        // 1. acyclic.
        if (!checkDeadLock()){
            return false;
        }

        for (String busy: busyEvents){
            dfsInhibitors(busy);
        }
        // 2. if e o-> f or e ->+ f, there is a path from e to f.
        for (String key: getOneMap("TimeResponse").keySet()){
            for (String val: getOneMap("TimeResponse").get(key)){
                if (inhibitors.contains(key)&&inhibitors.contains(val)){
                    if (!havePath(key, val)){
                        return false;
                    }
                }
            }
        }

        for (String key: getOneMap("TimeInclusion").keySet()){
            for (String val: getOneMap("TimeInclusion").get(key)){
                if (inhibitors.contains(key)&&inhibitors.contains(val)){
                    if (!havePath(key, val)){
                        return false;
                    }
                }
            }
        }

        // 3. if e ->o f with time k, then k = 0.
        for (String key: getTimeConditions().keySet()){
            for (TimeCondition tc: getTimeConditions().get(key)){
                if (inhibitors.contains(key)&&inhibitors.contains(tc.getTo())){
                    if (tc.getTime()!= 0){
                        return false;
                    }
                }
            }
        }

        return true;
    }

    // There is a path from source to sink in the inhibotor graph
    private boolean havePath(String from, String to)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        HashSet<String> reachable = new HashSet<>();
        dfsOneReachable(from, reachable);
        if (reachable.contains(to)){
            return true;
        }
        else return false;
    }

    private void dfsOneReachable(String from, HashSet<String> reachable)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (reachable.contains(from)){
            return;
        }
        reachable.add(from);
        if (getOneMap("TimeCondition").containsKey(from)){
            for (String val: getOneMap("TimeCondition").get(from)){
                dfsOneReachable(val, reachable);
            }
        }
        if (getOneMap("TimeMilestone").containsKey(from)){
            for (String val: getOneMap("TimeMilestone").get(from)){
                dfsOneReachable(val, reachable);
            }
        }
    }

    // dfs to generate the inhibitor graph from a busy event.
    private void dfsInhibitors(String busy)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (inhibitors.contains(busy)){
            return;
        }
        inhibitors.add(busy);
        HashSet<String> temp = new HashSet<>();
        for (String key: getOneMap("TimeCondition").keySet()){
            if(getOneMap("TimeCondition").get(key).contains(busy)){
                temp.add(key);
            }
        }
        for (String key: getOneMap("TimeMilestone").keySet()){
            if(getOneMap("TimeMilestone").get(key).contains(busy)){
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

    // below three functions find the alternative pairs in a DCR graph.
    public HashSet<HashSet<String>> findAlternativePairs()
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        HashSet<HashSet<String>> allPairs = getAllPairs();
        HashSet<HashSet<String>> res = new HashSet<>();
        for(HashSet<String> pair: allPairs){
            List<String> arrayPair = new ArrayList<>(pair);
            if (isSymmetric(arrayPair)){
                res.add(pair);
            }
        }
        return res;
    }

    private boolean isSymmetric(List<String> arrayPair)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        // condition
        String first = arrayPair.get(0);
        String second = arrayPair.get(1);
        HashSet<String> relationships = new HashSet<>();
        relationships.add("TimeCondition");
        relationships.add("TimeResponse");
        relationships.add("TimeMilestone");
        relationships.add("TimeInclusion");
        relationships.add("TimeExclusion");
        for (String relationship : relationships){
            HashMap<String, HashSet<String>> binaryMap = getOneMap(relationship);
            if ((!binaryMap.containsKey(first))&&(!binaryMap.containsKey(second))){
            }
            else if (binaryMap.containsKey(first)&&binaryMap.containsKey(second)){
                HashSet<String> firstMap = binaryMap.get(first);
                HashSet<String> secondMap = binaryMap.get(second);
                // self.
                boolean self_first = firstMap.remove(first);
                boolean self_second = secondMap.remove(second);
                if (self_first!=self_second){
                    return false;
                }
                // to each other.
                boolean other_first = firstMap.remove(second);
                boolean other_second = secondMap.remove(first);
                if (other_second!=other_first){
                    return false;
                }
                // others are the same.
                if (!binaryMap.get(first).equals(binaryMap.get(second))){
                    return false;
                }
            }
            else return false;
        }

        for (String relationship : relationships){
            HashMap<String, HashSet<String>> binaryMap = getOneMap(relationship);
            for (String key: binaryMap.keySet()){
                if((!key.equals(first))&&(!key.equals(second))){
                    boolean contain_first = binaryMap.get(key).contains(first);
                    boolean contain_second = binaryMap.get(key).contains(second);
                    if (contain_first!=contain_second) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private HashSet<HashSet<String>> getAllPairs(){
        HashSet<HashSet<String>> res = new HashSet<>();
        for (String event :events){
            for (String event1: events){
                if (!event.equals(event1)){
                    HashSet<String> temp = new HashSet<>();
                    temp.add(event);
                    temp.add(event1);
                    res.add(temp);
                }
            }
        }
        return res;
    }

    public HashSet<List<String>> findPaddingEvents(String interaction, int depth)
            throws IOException, ClassNotFoundException, InvocationTargetException,
            NoSuchMethodException, IllegalAccessException {
        TraceReplay tr = new TraceReplay();
        return tr.replayRes(deepClone(), depth, interaction);
    }

    public HashSet<String> enabledEvents()
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        HashSet<String> res = new HashSet<>();
        for (String event: events){
            if (enabled(event)){
                res.add(event);
            }
        }
        return res;
    }

}
