package models.jsonDCR;

import java.io.Serializable;

public class EventData implements Serializable {
    private String type = "";
    private String logic = "";

    public EventData(){
        type = "";
        logic = "";
    }

    public EventData(EventData other){
        type = other.type;
        logic = other.logic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLogic() {
        return logic;
    }

    public void setLogic(String logic) {
        this.logic = logic;
    }
}
