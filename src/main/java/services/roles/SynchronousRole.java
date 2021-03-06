package services.roles;

import models.dcrGraph.DCRGraph;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import services.SynchronousService;
import services.entities.SynchronousReply;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;

public class SynchronousRole extends SynchronousService {
    private SubscribeThread subscribeThread;

    public SynchronousRole(String role, DCRGraph dcrGraph)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        super(role, dcrGraph);
        HashSet<String> subscribeTopics = dcrGraph.getSyncSubscribe(role);
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

                        String interaction = topic;

                        handleMessage(interaction, (SynchronousReply) unserialize(message.getPayload()));
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
