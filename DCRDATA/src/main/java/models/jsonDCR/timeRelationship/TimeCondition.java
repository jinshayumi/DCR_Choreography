package models.jsonDCR.timeRelationship;

import java.io.Serializable;
import java.util.Objects;

public class TimeCondition implements Serializable{
    String to;
    long time = 0;
    String condition = "true";

    public TimeCondition (TimeCondition other){
        to = other.to;
        time = other.time;
        condition = other.condition;
    }

    public TimeCondition (String to, long time, String condition){
        this.to = to;
        this.time = time;
        this.condition = condition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeCondition timeCondition = (TimeCondition) o;
        return Objects.equals(to, timeCondition.to) &&
                Objects.equals(time, timeCondition.time)&&
                Objects.equals(condition, timeCondition.condition);
    }

    //重写hashCode详见Objects.hash()方法
    @Override
    public int hashCode() {
        return Objects.hash(to, time, condition);
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
