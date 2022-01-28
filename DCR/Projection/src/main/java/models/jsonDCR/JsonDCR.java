package models.jsonDCR;

import java.io.Serializable;
import java.util.*;

public class JsonDCR implements Serializable {
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
