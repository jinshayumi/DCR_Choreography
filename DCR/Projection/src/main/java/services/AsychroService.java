package services;

import models.dcrGraph.DCRGraph;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import services.roles.Buyer;

import java.util.HashMap;
import java.util.Set;

public class AsychroService {
    public String HOST = "tcp://127.0.0.1:61613";
    public int qos = 1;
    public String userName = "admin";
    public String passWord = "password";
    public MemoryPersistence persistence = new MemoryPersistence();

    public String role;

    public AsychroService(String role){
        this.role = role;
    }

    public void sendMessage(String interaction, Set<String> aims, String payload){
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
            // message.
            MqttMessage message = new MqttMessage(payload.getBytes());
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
}
