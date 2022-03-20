package services;

import models.dcrGraph.DCRGraph;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import services.entities.Data;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * This is the base class that each end point should extend.
 * The states included in this base class:
 *      1. Information to connect to the MQTT server.
 *      2. End point's End up projection.
 *      3. Each event's data.
 *      4. Relationships with guards' events information.*/
public abstract class AsychroService {
    public String HOST = "tcp://127.0.0.1:61613";
    public int qos = 1;
    public String userName = "admin";
    public String passWord = "password";
    public MemoryPersistence persistence = new MemoryPersistence();

    public String role;

    private DCRGraph dcrGraph;

    // A hash map to store the data in events.
    public HashMap<String, Data> eventData = new HashMap<>();

    // events included in the guard relationships.
    // Now it is set to empty.
    HashMap<String, HashSet<String>> guardRelationships = new HashMap<>();

    // calculate some event's value.
    /**
     * This is the interface that the programmer should implement
     * for each role in each end point.
     * @Input: An event's name
     * @Output: The result data that event calculate*/
    public abstract Data calculate(String event);

    /**
     * This is the interface that the programmer should implement
     * for each role in each end point.
     * The result of this function should initiate the
     * Hash map @guardRelationships
     * which stores the events that relationships' guards' include.*/
    public abstract void initiateGuardEvents();

    public AsychroService(String role, DCRGraph dcrGraph){
        this.role = role;
        this.dcrGraph = dcrGraph;
        initiateGuardEvents();
    }

    // When a role sends or receives  some data,
    // it will execute the event and update according relationships
    // according to the guard BExp.
    public void handleMessage(String event, Data data){
        HashSet<String> unsatisfyRelationships = getEventUnsatisfyRelationSet(event);
        if (!dcrGraph.enabled(event, unsatisfyRelationships)){
            System.out.println(role +": " + event +" is not enabled");
        }
        else {
            eventData.put(event, data);
            dcrGraph.execute(event, getEventUnsatisfyRelationSet(event));
        }
    }

    public void sendMessage(String interaction){
        try {
            // create client.
            MqttClient sampleClient = new MqttClient(HOST, "pub"+role, persistence);
            // create connection options
            MqttConnectOptions connOpts = new MqttConnectOptions();
            // remember state when restarting.
            connOpts.setCleanSession(false);
            // username and password.
            connOpts.setUserName(userName);
            connOpts.setPassword(passWord.toCharArray());
            // connect.
            sampleClient.connect(connOpts);

            // message serialization.
            byte[] bytes = serialize(calculate(interaction));
            handleMessage(interaction, calculate(interaction));
            MqttMessage message = new MqttMessage(bytes);


            // qos.
            message.setQos(qos);
            // publish.
            sampleClient.publish(interaction, message);
            // disconnect.
            sampleClient.disconnect();
            // close client.
            sampleClient.close();
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

    public static byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            // 序列化
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception e) {

        }
        return null;
    }

    public static Object unserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;
        try {
            // 反序列化
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {

        }
        return null;
    }

    // This function go through all the relationships
    // and find the unsatisfied guards.
    public HashSet<String> getEventUnsatisfyRelationSet(String event){
        HashSet<String> res = new HashSet<>();
        if (dcrGraph.getConditionsFor().containsKey(event)){
            for(String toEvent: dcrGraph.getConditionsFor().get(event)){
                if (!satisfyRelationship(event, toEvent, "condition")){
                    res.add(event + " " + toEvent +" " + "condition");
                }
            }
        }
        if (dcrGraph.getExcludesTo().containsKey(event)){
            for(String toEvent: dcrGraph.getExcludesTo().get(event)){
                if (!satisfyRelationship(event, toEvent, "exclusion")){
                    res.add(event + " " + toEvent +" " + "exclusion");
                }
            }
        }
        if (dcrGraph.getResponsesTo().containsKey(event)){
            for(String toEvent: dcrGraph.getResponsesTo().get(event)){
                if (!satisfyRelationship(event, toEvent, "response")){
                    res.add(event + " " + toEvent +" " + "response");
                }
            }
        }
        if (dcrGraph.getIncludesTo().containsKey(event)){
            for(String toEvent: dcrGraph.getIncludesTo().get(event)){
                if (!satisfyRelationship(event, toEvent, "inclusion")){
                    res.add(event + " " + toEvent +" " + "inclusion");
                }
            }
        }
        if (dcrGraph.getMilestonesFor().containsKey(event)){
            for(String toEvent: dcrGraph.getMilestonesFor().get(event)){
                if (!satisfyRelationship(event, toEvent, "milestone")){
                    res.add(event + " " + toEvent +" " + "milestone");
                }
            }
        }
        return res;
    }

    public boolean satisfyRelationship(String fromEvent, String toEvent, String relationship){
        HashSet<String> guard = new HashSet<>(getGuardEvents(fromEvent, toEvent, relationship));
        guard.retainAll(dcrGraph.getEvents());
        if (!guard.isEmpty()){
            System.out.println();
            return false;
        }
        else return true;
    }

    // find the needed event information for this guard.
    private HashSet<String> getGuardEvents(String fromEvent, String toEvent, String relationship) {
        if (guardRelationships.containsKey(fromEvent+ " " + toEvent + " " + relationship)){
            return guardRelationships.get(fromEvent+ " " + toEvent + " " + relationship);
        }
        else return new HashSet<>();
    }
}
