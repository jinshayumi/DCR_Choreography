package models.jsonDCR;

public class TimeResponse {
    String to;
    long time = -1;

    public TimeResponse(TimeResponse other){
        to = other.to;
        time = other.time;
    }

    public TimeResponse(String to, long time){
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
