package models.jsonDCR.timeRelationship;

import java.io.Serializable;
import java.util.Objects;

public class TimeMilestone extends TimeRelationship implements Serializable {
    String to;
    String condition = "true";

    public TimeMilestone(TimeMilestone other){
        to = other.to;
        condition = other.condition;
    }

    public TimeMilestone(String to, String condition){
        this.to = to;
        this.condition = condition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeMilestone timeMilestone = (TimeMilestone) o;
        return Objects.equals(to, timeMilestone.to) &&
                Objects.equals(condition, timeMilestone.condition);
    }

    //重写hashCode详见Objects.hash()方法
    @Override
    public int hashCode() {
        return Objects.hash(to, condition);
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getTo() {
        return to;
    }

    public String getCondition() {
        return condition;
    }
}
