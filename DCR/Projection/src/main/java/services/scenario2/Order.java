package services.scenario2;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import services.AsychroService;

import java.util.HashSet;

public class Order extends AsychroService {

    private SubscribeThread subscribeThread;

    public Order(String role, HashSet<String> subscribeTopics) {
        super(role);
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
                        String interaction = topic;
                        if (interaction.startsWith("CheckOut")){
                            HashSet<String> aims = new HashSet<>();
                            aims.add("Catalog");
                            sendMessage("CheckQuantity" + interaction.charAt(interaction.length()-1), aims, new String(message.getPayload()));
                        }
                        else if (interaction.startsWith("Not Enough")){
                            System.out.println(role + ": " + "Not Enough:"+ new String(message.getPayload()));
                        }
                        else if (interaction.startsWith("Fail")){
                            System.out.println(role + ": " + "Fail:"+ new String(message.getPayload()));
                        }
                        else if (interaction.startsWith("Details")){
                            System.out.println(role + ": " + "Details:"+ new String(message.getPayload()));
                        }
                        else {
                            System.out.println(role + ": " + "Invalid Message to Me:"+ new String(message.getPayload()));
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
