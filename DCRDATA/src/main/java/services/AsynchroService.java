package services;

import models.dcrGraph.DCRGraph;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;

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

    public AsynchroService(String role, DCRGraph dcrGraph){
        this.role = role;
        this.dcrGraph = dcrGraph;
    }

    public void handleMessage(String event)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (!dcrGraph.enabled(event)){
            System.out.println(role +": " + event + " is not enabled");
        }
        else {
            dcrGraph.execute(event);
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
//            byte[] bytes = serialize(calculate(interaction));
//            handleMessage(interaction, calculate(interaction));
            MqttMessage message = new MqttMessage(new byte[5]);

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

}
