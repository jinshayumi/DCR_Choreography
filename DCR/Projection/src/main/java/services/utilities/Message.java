package services.utilities;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
    String from;
    String data;
    String event;
    List<String> to;
    public Message(String event, String from, List<String> to){
        this.event = event;
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }
}
