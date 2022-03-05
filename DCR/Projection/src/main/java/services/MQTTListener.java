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


    public MQTTListener(HashMap<String, DCRGraph> dcrGraphHashMap){

        this.dcrGraphHashSet = dcrGraphHashMap;
        subscribeThread = new SubscribeThread("+/+/+");
        subscribeThread.run();
    }

    private class SubscribeThread extends Thread{
        private String topic;

        public SubscribeThread(String topic){
            this.topic = topic;
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
                        System.out.println(role + ": " + "Qos:"+message.getQos());
                        System.out.println(role + ": " + "message content:"+ new String(message.getPayload()));

                        String sender = topic.split("/")[0];
                        String interaction = topic.split("/")[1];
                        String receiver = topic.split("/")[2];

                        if (!dcrGraphHashSet.get(sender).enabled(interaction)){
                            System.out.println(interaction + " is not enabled by role " + sender);
                        }
                        else if (!dcrGraphHashSet.get(receiver).enabled(interaction)){
                            System.out.println(interaction + " is not enabled by role " + sender);
                        }
                        else {
                            dcrGraphHashSet.get(sender).execute(interaction);
                            dcrGraphHashSet.get(receiver).execute(interaction);
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
                client.subscribe(topic, qos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
