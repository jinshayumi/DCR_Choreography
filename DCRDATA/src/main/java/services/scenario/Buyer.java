package services.scenario;

import models.dcrGraph.DCRGraph;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import services.AsynchroService;
import services.entities.Data;
import services.entities.IntData;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;

public class Buyer extends AsynchroService {
    private SubscribeThread subscribeThread;

    public Buyer(String role, DCRGraph dcrGraph)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        super(role, dcrGraph);
        HashSet<String> subscribeTopics = dcrGraph.getSubscribe(role);
        subscribeThread = new SubscribeThread(subscribeTopics);
        subscribeThread.run();
    }

    private class SubscribeThread extends Thread{
        private HashSet<String> subscribeTopics;

        public SubscribeThread(HashSet<String> subscribeTopics){
            this.subscribeTopics = subscribeTopics;
        }

        @Override
        public void run(){
            try {
                // MemoryPersistence set clientId's save model.
                MqttClient client = new MqttClient(HOST, "sub" + role, new MemoryPersistence());
                // MQTT connection configuration.
                MqttConnectOptions options = new MqttConnectOptions();
                // if clear session: false: keep the connection, true: everytime connect to server, use a new id.
                options.setCleanSession(true);
                // username.
                options.setUserName(userName);
                // password.
                options.setPassword(passWord.toCharArray());
                // timeout. s.
                options.setConnectionTimeout(10);
                // heartbeat, no reconnection
                options.setKeepAliveInterval(20);
                // callback fun.
                client.setCallback(new MqttCallback() {

                    public void connectionLost(Throwable cause) {
                        System.out.println(cause.getMessage());
                        System.out.println(role + " connectionLost");
                    }

                    public void messageArrived(String topic, MqttMessage message) throws Exception {
//                        System.out.println(role + ": " + "topic:"+topic);
//                        System.out.println(role + ": " + "Qos:"+message.getQos());
//                        System.out.println(role + ": " + "message content:"+ new String(message.getPayload()));

                        String interaction = topic;

                        handleMessage(interaction, (Data) unserialize(message.getPayload()));
                    }

                    public void deliveryComplete(IMqttDeliveryToken token) {
                        System.out.println(role + ": " + "deliveryComplete---------"+ token.isComplete());
                    }

                });
                client.connect(options);
                for (String topic : subscribeTopics){
                    client.subscribe(topic, qos);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
