package models.jsonDCR.timeRelationship;

import java.io.Serializable;
import java.util.Objects;

public class TimeExclusion extends TimeRelationship implements Serializable {
    String to;
    String condition = "true";

    public TimeExclusion(TimeExclusion other){
        to = other.to;
        condition = other.condition;
    }

    public TimeExclusion(String to, String condition){
        this.to = to;
        this.condition = condition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeExclusion timeExclusion = (TimeExclusion) o;
        return Objects.equals(to, timeExclusion.to) &&
                Objects.equals(condition, timeExclusion.condition);
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
