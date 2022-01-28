package models.jsonDCR;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Relationship implements Serializable {
    private List<String> inclusion = new ArrayList<>();
    private List<String> condition = new ArrayList<>();
    private List<String> response = new ArrayList<>();
    private List<String> milstone = new ArrayList<>();
    private List<String> exclusion = new ArrayList<>();

    public List<String> getInclusion() {
        return inclusion;
    }

    public void setInclusion(List<String> inclusion) {
        this.inclusion = inclusion;
    }

    public List<String> getCondition() {
        return condition;
    }

    public void setCondition(List<String> condition) {
        this.condition = condition;
    }

    public List<String> getResponse() {
        return response;
    }

    public void setResponse(List<String> response) {
        this.response = response;
    }

    public List<String> getMilstone() {
        return milstone;
    }

    public void setMilstone(List<String> milstone) {
        this.milstone = milstone;
    }

    public List<String> getExclusion() {
        return exclusion;
    }

    public void setExclusion(List<String> exclusion) {
        this.exclusion = exclusion;
    }
}
