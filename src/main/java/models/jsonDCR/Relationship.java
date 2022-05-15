package models.jsonDCR;

import models.jsonDCR.timeRelationship.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Relationship implements Serializable {
    private List<TimeCondition> timeCondition = new ArrayList<>();
    private List<TimeResponse> timeResponse = new ArrayList<>();
    private List<TimeInclusion> timeInclusion = new ArrayList<>();
    private List<TimeExclusion> timeExclusion = new ArrayList<>();
    private List<TimeMilestone> timeMilestone = new ArrayList<>();

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

    public List<TimeInclusion> getTimeInclusion() {
        return timeInclusion;
    }

    public void setTimeInclusion(List<TimeInclusion> timeInclusion) {
        this.timeInclusion = timeInclusion;
    }

    public List<TimeExclusion> getTimeExclusion() {
        return timeExclusion;
    }

    public void setTimeExclusion(List<TimeExclusion> timeExclusion) {
        this.timeExclusion = timeExclusion;
    }

    public List<TimeMilestone> getTimeMilestone() {
        return timeMilestone;
    }

    public void setTimeMilestone(List<TimeMilestone> timeMilestone) {
        this.timeMilestone = timeMilestone;
    }
}
