package modelInterface;

import com.alibaba.fastjson.JSON;
import com.github.javaparser.ast.expr.Expression;
import models.dcrGraph.DCRGraph;
import models.jsonDCR.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import models.jsonDCR.timeRelationship.TimeCondition;
import models.jsonDCR.timeRelationship.TimeRelationship;
import models.jsonDCR.timeRelationship.TimeResponse;
import models.parser.ExpParser;
import org.apache.commons.io.FileUtils;

public class ModelImp implements ModelInterface {
    @Override
    public JsonDCR parseJsonToObject(String filename) throws IOException {
        String absolutePath = System.getProperty("user.dir") + filename;
        File file = new File(absolutePath);
        String jsonString = FileUtils.readFileToString(file);

        JsonDCR jsonObject = JSON.parseObject(jsonString, JsonDCR.class);
        return jsonObject;
    }

    @Override
    public DCRGraph transferToDCRGraph(JsonDCR jsonDCR)
            throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException, InstantiationException {
        HashSet<String> groupNames = new HashSet<>();
        // Initiate all the group names.
        for (Group group: jsonDCR.getGroups()){
            groupNames.add(group.getIdentity());
        }

        DCRGraph dcrGraph = new DCRGraph();

        // add all the participants: their relationships and states.
        for (Event event: jsonDCR.getEvents()){
            // add it to events.
            dcrGraph.getEvents().add(event.getIdentity());

            // set the initiators and receivers.
            dcrGraph.getEventsInitiator().put(event.getIdentity(), event.getInitiator());
            dcrGraph.getEventsReceivers().put(event.getIdentity(), new HashSet<>(event.getReceivers()));

            // add the 5 relationships.

            addRelationship(groupNames, dcrGraph.getTimeExclusions(), event, "TimeExclusion", jsonDCR);
            addRelationship(groupNames, dcrGraph.getTimeInclusions(), event, "TimeInclusion", jsonDCR);
            addRelationship(groupNames, dcrGraph.getTimeMilestones(), event, "TimeMilestone", jsonDCR);
            addRelationship(groupNames, dcrGraph.getTimeConditions(), event, "TimeCondition", jsonDCR);
            addRelationship(groupNames, dcrGraph.getTimeResponses(), event, "TimeResponse", jsonDCR);

            // initiate the state.
            if (event.getMarking().isExecuted()){
                dcrGraph.getDcrMarking().executed.add(event.getIdentity());
            }
            if (event.getMarking().isIncluded()){
                dcrGraph.getDcrMarking().included.add(event.getIdentity());
            }
            if (event.getMarking().isPending()){
                dcrGraph.getDcrMarking().pending.add(event.getIdentity());
            }

            // initiate the data.
            dcrGraph.getDataLogicMap().put(event.getIdentity(), new EventData(event.getEventData()));
        }

        // add all the groups' relationships.
        for (Group group: jsonDCR.getGroups()){
            HashSet<String> eventsFrom = getEventsByGroupName(group.getIdentity(), jsonDCR);
            addGroupRelationship(dcrGraph.getTimeInclusions(), "TimeInclusion", group,
                    eventsFrom, groupNames, jsonDCR);
            addGroupRelationship(dcrGraph.getTimeMilestones(), "TimeMilestone", group,
                    eventsFrom, groupNames, jsonDCR);
            addGroupRelationship(dcrGraph.getTimeExclusions(), "TimeExclusion", group,
                    eventsFrom, groupNames, jsonDCR);
            addGroupRelationship(dcrGraph.getTimeConditions(), "TimeCondition", group,
                    eventsFrom, groupNames, jsonDCR);
            addGroupRelationship(dcrGraph.getTimeResponses(), "TimeResponse", group,
                    eventsFrom, groupNames, jsonDCR);
        }
        return dcrGraph;
    }

    @Override
    public boolean projectable(DCRGraph dcrGraph, String role)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        // first, get all the events whose initiator is the role.
        HashSet<String> initiateEvents = getARolesInteractions(dcrGraph,role);
        for (String event: initiateEvents){
            HashSet<String> dependEvents = getOnesDependency(dcrGraph, event);
            if(!dcrGraph.label(dependEvents, role)){
                return false;
            }
        }

        // if an event is a decision event,
        // its initiator should be included in the data logic part.
        for (String event: dcrGraph.getDataLogicMap().keySet()){
            EventData eventData = dcrGraph.getDataLogicMap().get(event);
            // if this event is a decision event.
            if ((!eventData.getType().equals(""))&&(!eventData.getType().equals("?"))){
                // get the data logic and transfer it to Expression.
                String exp = eventData.getLogic();
                Expression expression = ExpParser.parseExp(exp);
                // get all the events mentioned in the Expression.
                HashSet<String> eventsInExpression = ExpParser.getEventsInExpression(expression);
                // all the mentioned events should include the initiator
                for (String eventInExpression: eventsInExpression){
                    String initiator = dcrGraph.getEventsInitiator().get(event);
                    if ((!dcrGraph.getEventsInitiator().get(eventInExpression).equals(initiator))
                            &&(!dcrGraph.getEventsReceivers().get(eventInExpression).contains(initiator))){
                        return false;
                    }
                }
            }
        }

        // 3rd condition.
        return dcrGraph.checkGuardEvents();
//        return true;
    }

    private HashSet<String> getOnesDependency(DCRGraph dcrGraph, String event)
            throws ClassNotFoundException, InvocationTargetException,
            NoSuchMethodException, IllegalAccessException {
        return dcrGraph.getOnesDependency(event);
    }


    private <T> void addGroupRelationship
            (HashMap<String, HashSet<T>> relationMap, String aimRelation,
             Group group, HashSet<String> eventsFrom, HashSet<String> groupNames, JsonDCR jsonDCR)
            throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException, InstantiationException {
        Class<?> clazz = Class.forName("models.jsonDCR.Relationship");
        Method method = clazz.getMethod("get"+aimRelation);
        ArrayList<T> eventList = (ArrayList<T>) method.invoke(group.getRelationship());

        for (T t: eventList){
            Class<?> clazzRelation = Class.forName("models.jsonDCR.timeRelationship." + aimRelation);
            Method methodTo = clazzRelation.getMethod("getTo");
            Method methodCondition = clazzRelation.getMethod("getCondition");
            String to = (String) methodTo.invoke(t);
            String condition = (String) methodCondition.invoke(t);

            HashSet<String> aims = getEventsByIdentity(to, groupNames, jsonDCR);
            for (String from: eventsFrom){
                for (String aim: aims){
                    if (aimRelation.equals("TimeResponse")||aimRelation.equals("TimeCondition")){
                        Method methodTime = clazzRelation.getMethod("getTime");
                        Long time = (Long) methodTime.invoke(t);
                        Constructor constructor = Class.forName("models.jsonDCR.timeRelationship."+ aimRelation).
                                getDeclaredConstructor(String.class, long.class, String.class);
                        T t2 = (T) constructor.newInstance(aim, time, condition);
                        relationMap.get(from).add(t2);
                    }
                    else {
                        Constructor constructor = Class.forName("models.jsonDCR.timeRelationship."+ aimRelation).
                                getDeclaredConstructor(String.class, String.class);
                        T t2 = (T) constructor.newInstance(aim, condition);
                        relationMap.get(from).add(t2);
                    }
                }
            }
        }

    }

    private <T> void addRelationship
            (HashSet<String> groupNames, HashMap<String, HashSet<T>> relationMap,
             Event event, String aimRelation, JsonDCR jsonDCR)
            throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException, InstantiationException {
        relationMap.put(event.getIdentity(), new HashSet<>());
        Class<?> clazz = Class.forName("models.jsonDCR.Relationship");
        // getTimeInclusion.
        Method method = clazz.getMethod("get" + aimRelation);

        // ArrayList<TimeInclusion>.
        ArrayList<T> eventList = (ArrayList<T>) method.invoke(event.getRelationship());
        for (T t: eventList){
            Class<?> clazzRelation = Class.forName("models.jsonDCR.timeRelationship." + aimRelation);
            Method methodTo = clazzRelation.getMethod("getTo");
            Method methodCondition = clazzRelation.getMethod("getCondition");
            String to = (String) methodTo.invoke(t);
            String condition = (String) methodCondition.invoke(t);

            HashSet<String> aims = getEventsByIdentity(to, groupNames, jsonDCR);
            for (String aim: aims){
                if (aimRelation.equals("TimeResponse")||aimRelation.equals("TimeCondition")){
                    Method methodTime = clazzRelation.getMethod("getTime");
                    Long time = (Long) methodTime.invoke(t);
                    Constructor constructor = Class.forName("models.jsonDCR.timeRelationship."+ aimRelation).
                            getDeclaredConstructor(String.class, long.class, String.class);
                    T t2 = (T) constructor.newInstance(aim, time, condition);
                    relationMap.get(event.getIdentity()).add(t2);
                }
                else {
                    Constructor constructor = Class.forName("models.jsonDCR.timeRelationship."+ aimRelation).
                            getDeclaredConstructor(String.class, String.class);
                    T t2 = (T) constructor.newInstance(aim, condition);
                    relationMap.get(event.getIdentity()).add(t2);
                }
            }
        }
    }

    private HashSet<String> getEventsByIdentity(String e, HashSet<String> groupNames, JsonDCR jsonDCR) {
        if (groupNames.contains(e)){
            return getEventsByGroupName(e, jsonDCR);
        }
        else {
            HashSet<String> res = new HashSet<>();
            res.add(e);
            return res;
        }
    }

    public static HashSet<String> getEventsByGroupName(String e, JsonDCR jsonDCR) {
        HashSet<String> res = new HashSet<>();
        for (Event event: jsonDCR.getEvents()){
            if (event.getBelongGroups().contains(e)){
                res.add(event.getIdentity());
            }
        }
        return res;
    }

    // input: a role.
    // output: the participants that the role initiates.
    public static HashSet<String> getARolesInteractions(DCRGraph dcrGraph, String role){
        HashSet<String> res = new HashSet<>();
        for (String event: dcrGraph.getEventsInitiator().keySet()){
            if (dcrGraph.getEventsInitiator().get(event).equals(role)){
                res.add(event);
            }
        }
        return res;
    }
}
