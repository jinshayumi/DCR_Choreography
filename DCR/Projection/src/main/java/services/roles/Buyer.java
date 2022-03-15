package services.roles;

import models.dcrGraph.DCRGraph;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import services.AsychroService;

import java.util.HashSet;
import java.util.Set;

public class Buyer extends AsychroService {
    private SubscribeThread subscribeThread;

    private int price1=-1;
    private int price2=-1;

    public Buyer(String role, HashSet<String> subscribeTopics){
        super(role);
        subscribeThread = new SubscribeThread(subscribeTopics);
        subscribeThread.run();
    }

    public void execute(String interaction, HashSet<String> aim){
        sendMessage(interaction, aim, "Ask");
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
                        System.out.println(role + ": " + "Qos:"+message.getQos());
                        System.out.println(role + ": " + "message content:"+ new String(message.getPayload()));

                        String interaction = topic;
                        if(interaction.startsWith("Quote")){
                            if (interaction.equals("Quote1")){
                                price1 =  Integer.parseInt(new String(message.getPayload()));
                            }
                            else{
                                price2 = Integer.parseInt(new String(message.getPayload()));
                            }
                            if(price1>=0 && price2 >=0){
                                HashSet<String> receiveRoles = new HashSet<>();
                                receiveRoles.add("Seller1");
                                receiveRoles.add("Seller2");
                                if(price1 >20 && price2>20){
                                    sendMessage("Reject", receiveRoles, "Reject");
                                }
                                else if (price1<price2){
                                    sendMessage("Accept1", receiveRoles, "Accept1");
                                }
                                else {
                                    sendMessage("Accept2", receiveRoles, "Accept2");
                                }
                            }
                        }
                        else if(interaction.equals("Details")){
                            System.out.println(role + ": " + "details:"+ new String(message.getPayload()));
                        }
                    }

                    public void deliveryComplete(IMqttDeliveryToken token) {
                        System.out.println(role + ": " + "deliveryComplete---------"+ token.isComplete());
                    }

                });
                client.connect(options);
                // subscribe.
//                client.subscribe(topic, qos);
                for (String topic : subscribeTopics){
                    client.subscribe(topic, qos);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
