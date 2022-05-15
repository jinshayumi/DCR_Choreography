package services;

import models.dcrGraph.DCRGraph;
import models.parser.ExpParser;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import services.entities.SynchronousReply;
import services.entities.data.Data;
import services.entities.data.VoidData;
import visualization.DCRVisual;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class SynchronousService {
    public String HOST = "tcp://127.0.0.1:61613";
    public int qos = 1;
    public String userName = "admin";
    public String passWord = "password";
    public MemoryPersistence persistence = new MemoryPersistence();

    public String role;
    private DCRGraph dcrGraph;
    private DCRVisual dcrVisual;

    // fields to govern the synchronous DCR choreography.
    List<String> eventsToBeReplied;
    HashMap<Integer, SynchronousReply> executionQueue;
    int executeId;

    public SynchronousService(String role, DCRGraph dcrGraph)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        this.role = role;
        this.dcrGraph = dcrGraph;
        eventsToBeReplied = new ArrayList<>();
        executionQueue = new HashMap<>();
        executeId = 0;
        dcrVisual = new DCRVisual(dcrGraph, role);
        dcrVisual.display();
    }

    public void requestExecuteEvent(String interaction)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, MqttException {
        if (!dcrGraph.enabled(interaction)){
            System.out.println(role +": " + interaction + " is not enabled when request the execution.");
            return;
        }

        // if this is an input event, the input should be sent to the coordinator.
        Data requestData;
        if (dcrGraph.getDataLogicMap().get(interaction).getType().equals("?")){
            System.out.println("Input for event " + interaction);
            Scanner sc = new Scanner( System.in );
            String input = sc.nextLine();
//            dcrGraph.updateEventData(interaction,
//                    ExpParser.calculate(dcrGraph.getDataMap(), ExpParser.parseExp(input)));
            requestData = ExpParser.calculate(dcrGraph.getDataMap(), ExpParser.parseExp(input));
        } else requestData = new VoidData("void");
        String topic = interaction;
        publishMessage(topic, requestData);
    }

    private void publishMessage(String topic, Data requestData) throws MqttException {
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

        //serialize the data.
        byte[] serializedData;
        serializedData = serialize(requestData);

        // send the data.
        // message serialization.
        MqttMessage message = new MqttMessage(serializedData);

        // qos.
        message.setQos(qos);
        // publish.
        sampleClient.publish(topic, message);
        // disconnect.
        sampleClient.disconnect();
        // close client.
        sampleClient.close();
    }

    public void handleMessage(String topic, SynchronousReply reply)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (topic.startsWith("reject")){
            System.out.println
                    ("Role " + role +": event " + topic.split("@")[1] + " is rejected by the coordinator." );
        }
        else if(topic.startsWith("execute")){
            System.out.println
                    ("Role " + role +": receive execute event " + reply.getSequenceIdMap().get(role) + " " + topic.split("@")[1]);
            // put it into the execution queue.
            executionQueue.put(reply.getSequenceIdMap().get(role), reply);

            //do execution.
            doExecutions();
        }

    }

    private void doExecutions()
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (executionQueue.containsKey(executeId)){
            String interaction = executionQueue.get(executeId).getInteraction();
            Data data = executionQueue.get(executeId).getData();
            dcrGraph.updateEventData(interaction, data);
            System.out.println("role " + role + " executes event " + interaction);
            dcrGraph.execute(interaction, executionQueue.get(executeId).getTime());
            dcrVisual.updateMarkings();
            executionQueue.remove(executeId);
            executeId++;
            doExecutions();
        }
        else return;
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
