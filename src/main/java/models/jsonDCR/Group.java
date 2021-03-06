package models.jsonDCR;

import java.io.Serializable;

public class Group implements Serializable {
    private String identity;
    private Relationship relationship = new Relationship();

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }
}
