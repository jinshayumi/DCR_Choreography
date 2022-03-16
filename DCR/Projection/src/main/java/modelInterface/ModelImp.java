package modelInterface;

import com.alibaba.fastjson.JSON;
import models.dcrGraph.DCRGraph;
import models.jsonDCR.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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
    public DCRGraph transferToDCRGraph(JsonDCR jsonDCR) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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

            addRelationship(groupNames, dcrGraph.getExcludesTo(), event, "getExclusion", jsonDCR);
            addRelationship(groupNames, dcrGraph.getIncludesTo(), event, "getInclusion", jsonDCR);
            addRelationship(groupNames, dcrGraph.getMilestonesFor(), event, "getMilstone", jsonDCR);
            addCondRelationship(groupNames, dcrGraph.getTimeConditions(), event, "getTimeCondition", jsonDCR);
            addResponseRelationship(groupNames, dcrGraph.getTimeResponses(), event, "getTimeResponse", jsonDCR);

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
        }

        // add all the groups' relationships.
        for (Group group: jsonDCR.getGroups()){
            HashSet<String> eventsFrom = getEventsByGroupName(group.getIdentity(), jsonDCR);
            addGroupRelationship(dcrGraph.getIncludesTo(), "getInclusion", group, eventsFrom, groupNames, jsonDCR);
            addGroupRelationship(dcrGraph.getMilestonesFor(), "getMilstone", group, eventsFrom, groupNames, jsonDCR);
            addGroupRelationship(dcrGraph.getExcludesTo(), "getExclusion", group, eventsFrom, groupNames, jsonDCR);
            addGroupCond(dcrGraph.getTimeConditions(), "getTimeCondition", group, eventsFrom, groupNames, jsonDCR);
            addGroupResponse(dcrGraph.getTimeResponses(), "getTimeResponse", group, eventsFrom, groupNames, jsonDCR);
        }
        return dcrGraph;
    }

    @Override
    public boolean projectable(DCRGraph dcrGraph, String role) {
        // first, get all the events whose initiator is the role.
        HashSet<String> initiateEvents = getARolesInteractions(dcrGraph,role);
        for (String event: initiateEvents){
            HashSet<String> dependEvents = getOnesDependency(dcrGraph, event);
            if(!dcrGraph.label(dependEvents, role)){
                return false;
            }
        }
        return true;
    }

    private HashSet<String> getOnesDependency(DCRGraph dcrGraph, String event) {
        return dcrGraph.getOnesDependency(event);
    }


    private void addGroupRelationship(HashMap<String, HashSet<String>> relationMap, String aimRelation, Group group, HashSet<String> eventsFrom, HashSet<String> groupNames, JsonDCR jsonDCR) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> clazz = Class.forName("models.jsonDCR.Relationship");
        Method method = clazz.getMethod(aimRelation);
        ArrayList<String> eventList = (ArrayList<String>) method.invoke(group.getRelationship());
        HashSet<String> events = new HashSet<>(eventList);
        for (String e: events){
            HashSet<String> eventsTo = getEventsByIdentity(e,groupNames,jsonDCR);
            for (String s1: eventsFrom){
                if (relationMap.containsKey(s1)){
                    relationMap.get(s1).addAll(eventsTo);
                }
                else relationMap.put(s1, eventsTo);
            }
        }
    }

    private void addGroupCond(HashMap<String, HashSet<TimeCondition>> relationMap, String aimRelation, Group group, HashSet<String> eventsFrom, HashSet<String> groupNames, JsonDCR jsonDCR) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> clazz = Class.forName("models.jsonDCR.Relationship");
        Method method = clazz.getMethod(aimRelation);
        ArrayList<TimeCondition> eventList = (ArrayList<TimeCondition>) method.invoke(group.getRelationship());
        for (String from: eventsFrom){
            HashSet<TimeCondition> res = new HashSet<>();
            for (TimeCondition timeCondition: eventList){
                long time = timeCondition.getTime();
                if (groupNames.contains(timeCondition.getTo())){
                    for(String s: getEventsByGroupName(timeCondition.getTo(), jsonDCR)){
                        res.add(new TimeCondition(s, time));
                    }
                }
                else res.add(new TimeCondition(timeCondition.getTo(), time));
            }
            if (relationMap.containsKey(from)){
                relationMap.get(from).addAll(res);
            }
            else{
                relationMap.put(from, res);
            }
        }
    }

    private void addGroupResponse(HashMap<String, HashSet<TimeResponse>> relationMap, String aimRelation, Group group, HashSet<String> eventsFrom, HashSet<String> groupNames, JsonDCR jsonDCR) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> clazz = Class.forName("models.jsonDCR.Relationship");
        Method method = clazz.getMethod(aimRelation);
        ArrayList<TimeResponse> eventList = (ArrayList<TimeResponse>) method.invoke(group.getRelationship());
        for (String from: eventsFrom){
            HashSet<TimeResponse> res = new HashSet<>();
            for (TimeResponse timeResponse: eventList){
                long time = timeResponse.getTime();
                if (groupNames.contains(timeResponse.getTo())){
                    for(String s: getEventsByGroupName(timeResponse.getTo(), jsonDCR)){
                        res.add(new TimeResponse(s, time));
                    }
                }
                else res.add(new TimeResponse(timeResponse.getTo(), time));
            }
            if (relationMap.containsKey(from)){
                relationMap.get(from).addAll(res);
            }
            else{
                relationMap.put(from, res);
            }
        }
    }


    private void addRelationship(HashSet<String> groupNames, HashMap<String, HashSet<String>> relationMap, Event event, String aimRelation, JsonDCR jsonDCR) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> clazz = Class.forName("models.jsonDCR.Relationship");
        Method method = clazz.getMethod(aimRelation);
        ArrayList<String> eventList = (ArrayList<String>) method.invoke(event.getRelationship());
        HashSet<String> eventSet = new HashSet<>(eventList);
        HashSet<String> events = new HashSet<>();
        for (String e: eventSet){
            events.addAll(getEventsByIdentity(e, groupNames, jsonDCR));
        }
        relationMap.put(event.getIdentity(), events);
    }

    private void addCondRelationship(HashSet<String> groupNames, HashMap<String, HashSet<TimeCondition>> relationMap, Event event, String aimRelation, JsonDCR jsonDCR) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> clazz = Class.forName("models.jsonDCR.Relationship");
        Method method = clazz.getMethod(aimRelation);
        ArrayList<TimeCondition> eventList = (ArrayList<TimeCondition>) method.invoke(event.getRelationship());
        HashSet<TimeCondition> res = new HashSet<>();
        for (TimeCondition timeCondition: eventList){
            long time = timeCondition.getTime();
            if (groupNames.contains(timeCondition.getTo())){
                for(String s: getEventsByGroupName(timeCondition.getTo(), jsonDCR)){
                    res.add(new TimeCondition(s, time));
                }
            }
            else res.add(new TimeCondition(timeCondition.getTo(), time));
        }
        relationMap.put(event.getIdentity(), res);
    }

    private void addResponseRelationship(HashSet<String> groupNames, HashMap<String, HashSet<TimeResponse>> relationMap, Event event, String aimRelation, JsonDCR jsonDCR) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> clazz = Class.forName("models.jsonDCR.Relationship");
        Method method = clazz.getMethod(aimRelation);
        ArrayList<TimeResponse> eventList = (ArrayList<TimeResponse>) method.invoke(event.getRelationship());
        HashSet<TimeResponse> res = new HashSet<>();
        for (TimeResponse timeResponse: eventList){
            long time = timeResponse.getTime();
            if (groupNames.contains(timeResponse.getTo())){
                for(String s: getEventsByGroupName(timeResponse.getTo(), jsonDCR)){
                    res.add(new TimeResponse(s, time));
                }
            }
            else res.add(new TimeResponse(timeResponse.getTo(), time));
        }
        relationMap.put(event.getIdentity(), res);
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
