package model.entities;

import java.io.Serializable;

public class Participant implements Serializable {
    private String identity;
    private String initiator;
    private String action;
    private String receivers;
    private String belongGroups;
    private Relationship relationship = new Relationship();
    private Marking marking;

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getReceivers() {
        return receivers;
    }

    public void setReceivers(String receivers) {
        this.receivers = receivers;
    }

    public String getBelongGroups() {
        return belongGroups;
    }

    public void setBelongGroups(String belongGroups) {
        this.belongGroups = belongGroups;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    public Marking getMarking() {
        return marking;
    }

    public void setMarking(Marking marking) {
        this.marking = marking;
    }
}
