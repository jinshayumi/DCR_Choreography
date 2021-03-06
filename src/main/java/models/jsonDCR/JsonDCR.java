package models.jsonDCR;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JsonDCR implements Serializable {
    private List<Event> events;
    private List<Group> groups = new ArrayList<>();

    public List<Event> getEvents() {
        return events;
    }

    public void getEvents(List<Event> participants) {
        this.events = participants;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
}
