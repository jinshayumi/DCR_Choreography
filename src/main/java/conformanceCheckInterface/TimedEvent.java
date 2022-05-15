package conformanceCheckInterface;

import services.entities.data.Data;

import java.io.Serializable;

public class TimedEvent implements Serializable, Comparable<TimedEvent> {
    private String identity;
    private Long time;
    private String type;
    private Data data;

    public TimedEvent(String identity, Long time, String type, Data data){
        this.identity = identity;
        this.time = time;
        this.type = type;
        this.data = data;
    }

    public TimedEvent(){
        identity = "";
        time = -1L;
        type = "";
        data = null;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public int compareTo(TimedEvent o){
        return this.time.compareTo(o.time);
    }
}
