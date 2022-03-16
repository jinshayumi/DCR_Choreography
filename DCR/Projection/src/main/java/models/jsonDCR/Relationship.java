package models.jsonDCR;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Relationship implements Serializable {
    private List<String> inclusion = new ArrayList<>();
//    private List<String> condition = new ArrayList<>();
//    private List<String> response = new ArrayList<>();
    private List<String> milstone = new ArrayList<>();
    private List<String> exclusion = new ArrayList<>();

    private List<TimeCondition> timeCondition = new ArrayList<>();
    private List<TimeResponse> timeResponse = new ArrayList<>();

    public List<String> getInclusion() {
        return inclusion;
    }

    public void setInclusion(List<String> inclusion) {
        this.inclusion = inclusion;
    }

    public List<String> getCondition() {
        List<String> res = new ArrayList<>();
        for (TimeCondition timeCond: timeCondition){
            res.add(timeCond.getTo());
        }
        return res;
    }
//
//    public void setCondition(List<String> condition) {
//        this.condition = condition;
//    }
//
    public List<String> getResponse() {
        List<String> res = new ArrayList<>();
        for (TimeResponse timeRes: timeResponse){
            res.add(timeRes.getTo());
        }
        return res;
    }
//
//    public void setResponse(List<String> response) {
//        this.response = response;
//    }

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

    public List<TimeCondition> getTimeCondition() {
        return timeCondition;
    }

    public void setTimeCondition(List<TimeCondition> timeCondition) {
        this.timeCondition = timeCondition;
    }

    public List<TimeResponse> getTimeResponse() {
        return timeResponse;
    }

    public void setTimeResponse(List<TimeResponse> timeResponse) {
        this.timeResponse = timeResponse;
    }
}
