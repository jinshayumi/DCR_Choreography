package model;

import com.alibaba.fastjson.JSON;
import model.entities.Group;
import model.entities.JSONDCR;
import model.entities.Participant;

import java.io.IOException;
import java.util.*;

public class DCRGraph {
    protected HashSet<String> events = new HashSet<>();

    private JSONDCR jsondcr;
    private HashSet<String> groupNames = new HashSet<>();

    private HashMap<String, HashSet<String>> conditionsFor = new HashMap<>();
    private HashMap<String, HashSet<String>> milestonesFor = new HashMap<>();
    private HashMap<String, HashSet<String>> responsesTo = new HashMap<>();
    private HashMap<String, HashSet<String>> excludesTo = new HashMap<>();
    private HashMap<String, HashSet<String>> includesTo = new HashMap<>();

    private DCRMarking dcrMarking = new DCRMarking();

    public DCRGraph(final String path){
        try {
            jsondcr = DCRLoader.LoadFromJsonFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initiate all the group names.
        for (Group group: jsondcr.getGroups()){
            groupNames.add(group.getIdentity());
        }

        // participants.
        for (Participant participant: jsondcr.getParticipants()){
            // Add the participant to event list.
            events.add(participant.getIdentity());

            // Add the participant's relationships.
            for (String e: participant.getRelationship().getCondition()){
                HashSet<String> events = getEventsByIdentity(e);
                conditionsFor.put(participant.getIdentity(), events);
            }
            for (String e: participant.getRelationship().getExclusion()){
                HashSet<String> events = getEventsByIdentity(e);
                excludesTo.put(participant.getIdentity(), events);
            }
            for (String e: participant.getRelationship().getResponse()){
                HashSet<String> events = getEventsByIdentity(e);
                responsesTo.put(participant.getIdentity(), events);
            }
            for (String e: participant.getRelationship().getInclusion()){
                HashSet<String> events = getEventsByIdentity(e);
                includesTo.put(participant.getIdentity(), events);
            }
            for (String e: participant.getRelationship().getMilstone()){
                HashSet<String> events = getEventsByIdentity(e);
                milestonesFor.put(participant.getIdentity(), events);
            }

            // initiate the state.
            if (participant.getMarking().isExecuted()){
                dcrMarking.executed.add(participant.getIdentity());
            }
            if (participant.getMarking().isIncluded()){
                dcrMarking.included.add(participant.getIdentity());
            }
            if (participant.getMarking().isPending()){
                dcrMarking.pending.add(participant.getIdentity());
            }
        }

        // groups.
        for (Group group: jsondcr.getGroups()){
            HashSet<String> eventsFrom = getEventsByGroupName(group.getIdentity());
            for (String e: group.getRelationship().getCondition()){
                HashSet<String> eventsTo = getEventsByIdentity(e);
                for (String s1: eventsFrom){
                    if (conditionsFor.containsKey(s1)){
                        conditionsFor.get(s1).addAll(eventsTo);
                    }
                    else conditionsFor.put(s1, eventsTo);
                }
            }
            for (String e: group.getRelationship().getInclusion()){
                HashSet<String> eventsTo = getEventsByIdentity(e);
                for (String s1: eventsFrom){
                    if (includesTo.containsKey(s1)){
                        includesTo.get(s1).addAll(eventsTo);
                    }
                    else includesTo.put(s1, eventsTo);
                }
            }
            for (String e: group.getRelationship().getResponse()){
                HashSet<String> eventsTo = getEventsByIdentity(e);
                for (String s1: eventsFrom){
                    if (responsesTo.containsKey(s1)){
                        responsesTo.get(s1).addAll(eventsTo);
                    }
                    else responsesTo.put(s1, eventsTo);
                }
            }
            for (String e: group.getRelationship().getExclusion()){
                HashSet<String> eventsTo = getEventsByIdentity(e);
                for (String s1: eventsFrom){
                    if (excludesTo.containsKey(s1)){
                        excludesTo.get(s1).addAll(eventsTo);
                    }
                    else excludesTo.put(s1, eventsTo);
                }
            }
            for (String e: group.getRelationship().getMilstone()){
                HashSet<String> eventsTo = getEventsByIdentity(e);
                for (String s1: eventsFrom){
                    if (milestonesFor.containsKey(s1)){
                        milestonesFor.get(s1).addAll(eventsTo);
                    }
                    else milestonesFor.put(s1, eventsTo);
                }
            }
        }
    }

    private HashSet<String> getEventsByIdentity(String e) {
        if (groupNames.contains(e)){
            return getEventsByGroupName(e);
        }
        else {
            HashSet<String> res = new HashSet<>();
            res.add(e);
            return res;
        }
    }

    private HashSet<String> getEventsByGroupName(String e) {
        HashSet<String> res = new HashSet<>();
        for (Participant participant: jsondcr.getParticipants()){
            if (participant.getBelongGroups().contains(e)){
                res.add(participant.getIdentity());
            }
        }
        return res;
    }


    public Boolean enabled(final DCRMarking marking, final String event){
        if (!events.contains(event)){
            return true;
        }

        if (!marking.included.contains(event)){
            return false;
        }

        try {
            final Set<String> inccon = new HashSet<>(conditionsFor.get(event));
            inccon.retainAll(marking.included);
            if(!marking.executed.containsAll(inccon)){
                return false;
            }
        }
        catch (Exception e){

        }

        try {
            final Set<String> incmil = new HashSet<>(milestonesFor.get(event));
            incmil.retainAll(marking.included);
            for (final String p:marking.pending){
                if (incmil.contains(p)){
                    return false;
                }
            }
        }catch (Exception e){

        }
        return true;
    }

    public DCRMarking execute(final DCRMarking marking, final String event){
        if(!events.contains(event)){
            return marking;
        }

        if (!this.enabled(marking, event)){
            return marking;
        }

        DCRMarking result = new DCRMarking();
        result.executed = new HashSet<>(marking.executed);
        result.included = new HashSet<>(marking.included);
        result.pending = new HashSet<>(marking.pending);

        result.executed.add(event);

        result.pending.remove(event);
        try {
            result.pending.addAll(responsesTo.get(event));
        }catch (Exception e){

        }
        try {
            result.included.removeAll(excludesTo.get(event));
        }catch (Exception e){}
        try {
            result.included.addAll(includesTo.get(event));
        }catch (Exception e){}

        return result;
    }

    public boolean endPointProjectable(String role){
        List<Participant> roleParticipant = getARolesInteractions(role);
        for (Participant participant: roleParticipant){
            HashSet<String> dependParticipants = getOnesDependency(participant.getIdentity());
            if(!label(dependParticipants, role)){
                return false;
            }
        }
        return true;
    }

    private boolean label(HashSet<String> participantDepend, String role) {
        for (Participant participant: jsondcr.getParticipants()){
            if (participantDepend.contains(participant.getIdentity())&&(!participant.getInitiator().equals(role)&&!participant.getReceivers().contains(role))){
                return false;
            }
        }
        return true;
    }

    private HashSet<String> getOnesDependency(String participant) {
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

    private HashSet<String> findResponse(String e) {
        HashSet<String> res = new HashSet<>();
        for (String event:events){
            if (responsesTo.containsKey(event)&&responsesTo.get(event).contains(e)){
                res.add(event);
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

    private boolean ConditionOrMilestone(String e, String identity) {
        if (conditionsFor.containsKey(e)&&conditionsFor.get(e).contains(identity)){
            return true;
        }
        if (milestonesFor.containsKey(e)&&milestonesFor.get(e).contains(identity)){
            return true;
        }
        return false;
    }

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

    // input: a role. output: the participants that the role initiates.
    private List<Participant> getARolesInteractions(String role){
        List<Participant> res = new ArrayList<>();
        for (Participant p: jsondcr.getParticipants()){
            if (p.getInitiator().equals(role)){
                res.add(p);
            }
        }
        return res;
    }
}
