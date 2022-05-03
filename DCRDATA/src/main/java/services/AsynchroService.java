package services;

import models.dcrGraph.DCRGraph;
import models.parser.ExpParser;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import services.entities.Data;
import services.entities.VoidData;
import visualization.DCRVisual;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

/**
 * This is the base class that each end point should inherit.
 * The states included in this base class:
 *      1. Information to connect to the MQTT server.
 *      2. End point's End up projection.
 *      3. Each event's data.
 *      4. Relationships with guards' events information.*/
public class AsynchroService {
    public String HOST = "tcp://127.0.0.1:61613";
    public int qos = 1;
    public String userName = "admin";
    public String passWord = "password";
    public MemoryPersistence persistence = new MemoryPersistence();

    public String role;

    private DCRGraph dcrGraph;

    private DCRVisual dcrVisual;

    public AsynchroService(String role, DCRGraph dcrGraph)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        this.role = role;
        this.dcrGraph = dcrGraph;
        dcrVisual = new DCRVisual(dcrGraph, role);
        dcrVisual.display();
    }

    public void execute(String event){
        sendMessage(event);
    }

    /**
     * When receiving a message, the event is always enabled in theory.
     * If the interaction carries some data(payload is not void),
     * the data should be updated in data map in DCR graph..*/
    public void handleMessage(String event, Data data)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (!dcrGraph.enabled(event)){
            System.out.println(role +": " + event + " is not enabled (receiving side)");
            return;
        }
        else {
            // if this event contains some data, update the data.
            if (!dcrGraph.getDataLogicMap().get(event).getType().equals("")){
                dcrGraph.updateEventData(event, data);
            }
            // execute the event in the dcr graph.
            dcrGraph.execute(event);
            dcrVisual.updateMarkings();
            System.out.println(role + ": " + "after handling "+ event
                    +", "
                    + "pending events are:"
                    + dcrGraph.getIncludedPending().toString());
        }
    }

    /**
     * When sending a message, it means that there is an interaction
     * and the sender is the initiator for the interaction.
     * The steps are:
     * 1. if it is enabled?
     * 2. execute the event in local EPP choreography.
     * 3. send the calculated data to the receivers via publishing topic(topic name is the interaction's name)*/
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

            if (!dcrGraph.enabled(interaction)){
                System.out.println(role +": " + interaction + " is not enabled (sending side)");
                return;
            }
            // if this event is not an input event or of type void.
            if((!dcrGraph.getDataLogicMap().get(interaction).getType().equals(""))
                    &&(!dcrGraph.getDataLogicMap().get(interaction).getType().equals("?"))){
                dcrGraph.calculateAnEvent(interaction);
            }
            if (dcrGraph.getDataLogicMap().get(interaction).getType().equals("?")){
                System.out.println("Input for event " + interaction);
                Scanner sc = new Scanner( System.in );
                String input = sc.nextLine();
                dcrGraph.updateEventData(interaction,
                        ExpParser.calculate(dcrGraph.getDataMap(), ExpParser.parseExp(input)));
            }
            dcrGraph.execute(interaction);
            dcrVisual.updateMarkings();
            System.out.println(role + ": after sending " + interaction + ", " + "pending events are: " +
                    dcrGraph.getIncludedPending().toString());

            byte[] serializedData;
            if ((!dcrGraph.getDataLogicMap().get(interaction).getType().equals(""))){
                Data dataToSend = dcrGraph.getDataMap().get(interaction);
                serializedData = serialize(dataToSend);
            }
            else {
                Data dataToSend = new VoidData("void");
                serializedData = serialize(dataToSend);
            }

            // message serialization.
            MqttMessage message = new MqttMessage(serializedData);

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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    // help function to serialize an object to byte[].
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

}
