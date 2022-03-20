package models.jsonDCR;

import java.io.Serializable;

public class TimeCondition implements Serializable {
    String to;
    long time = 0;

    public TimeCondition (TimeCondition other){
        to = other.to;
        time = other.time;
    }

    public TimeCondition (String to, long time){
        this.to = to;
        this.time = time;
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
}
