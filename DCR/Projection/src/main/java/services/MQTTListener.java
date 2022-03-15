package services;

import models.dcrGraph.DCRGraph;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.HashMap;
import java.util.HashSet;

public class MQTTListener {
    HashMap<String, DCRGraph> dcrGraphHashSet;
    private String role = "MQTTListener";

    public String HOST = "tcp://127.0.0.1:61613";
    public int qos = 1;
    public String userName = "admin";
    public String passWord = "password";
    public MemoryPersistence persistence = new MemoryPersistence();

    private SubscribeThread subscribeThread;
    private DCRGraph dcrGraph;


    public MQTTListener(HashMap<String, DCRGraph> dcrGraphHashMap, DCRGraph dcrGraph){

        this.dcrGraphHashSet = dcrGraphHashMap;
        this.dcrGraph = dcrGraph;

        subscribeThread = new SubscribeThread(new HashSet<>(dcrGraph.getEvents()));
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
                // MemoryPersistence set clientid's save model.
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
                        System.out.println(role + " connectionLost");
                    }

                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        System.out.println(role + ": " + "topic:"+topic);
                        System.out.println(role + ": " + "message content:"+ new String(message.getPayload()));

                        String interaction = topic;
                        String sender = dcrGraph.getEventsInitiator().get(topic);

                        HashSet<String> receivers = dcrGraph.getEventsReceivers().get(topic);

                        boolean enabled = true;
                        if (!dcrGraphHashSet.get(sender).enabled(interaction)){
                            System.out.println(interaction + " is not enabled by role " + sender);
                            enabled = false;
                        }
                        else {
                            for (String receiver: receivers){
                                if (!dcrGraphHashSet.get(receiver).enabled(interaction)){
                                    System.out.println(interaction + " is not enabled by role " + sender);
                                    enabled = false;
                                }
                            }
                        }
                        if (enabled){
                            dcrGraphHashSet.get(sender).execute(interaction);
                            for (String receiver: receivers){
                                dcrGraphHashSet.get(receiver).execute(interaction);
                            }
                            System.out.println("after executing interaction " + interaction + ", following end up projection is not accepted:");
                            for(String role : dcrGraphHashSet.keySet()){
                                if(!dcrGraphHashSet.get(role).isAccepting()){
                                    System.out.println(role);
                                }
                            }
                        }
                    }

                    public void deliveryComplete(IMqttDeliveryToken token) {
                        System.out.println(role + ": " + "deliveryComplete---------"+ token.isComplete());
                    }

                });
                client.connect(options);
                // subscribe.
                for (String topic : subscribeTopics){
                    client.subscribe(topic, qos);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
