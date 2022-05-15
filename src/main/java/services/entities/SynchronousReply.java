package services.entities;

import services.entities.data.Data;

import java.io.Serializable;
import java.util.HashMap;

public class SynchronousReply implements Serializable {
    Data data;
    Long time;
    HashMap<String, Integer> sequenceIdMap;
    String interaction;

    public SynchronousReply(Data data, Long time, HashMap<String, Integer> sequenceIdMap, String interaction){
        this.data = data;
        this.time = time;
        this.sequenceIdMap = sequenceIdMap;
        this.interaction = interaction;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }


    public HashMap<String, Integer> getSequenceIdMap() {
        return sequenceIdMap;
    }

    public void setSequenceIdMap(HashMap<String, Integer> sequenceIdMap) {
        this.sequenceIdMap = sequenceIdMap;
    }

    public String getInteraction() {
        return interaction;
    }

    public void setInteraction(String interaction) {
        this.interaction = interaction;
    }
}
