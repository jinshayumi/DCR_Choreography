package model.entities;

import model.DCRMarking;
import model.entities.Group;
import model.entities.Participant;

import java.io.Serializable;
import java.util.*;

public class JSONDCR implements Serializable {
    private List<Participant> participants;
    private List<Group> groups;

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
}
