package services.scenario1;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import services.AsychroService;

import java.util.HashSet;

public class Seller extends AsychroService {
    private SubscribeThread subscribeThread;

    private int price;

    public Seller(String role, HashSet<String> subscribeTopics){
        super(role);
        if(role.charAt(role.length()-1)=='1'){
            price = 25;
        }
        else {
            price = 15;
        }
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

                        if(interaction.startsWith("Accept")){
                            if (interaction.charAt(interaction.length()-1)==role.charAt(role.length()-1)){
                                HashSet<String> receivers = new HashSet<>();
                                receivers.add("Shipper");
                                String orderInf = interaction.split("/")[0] + ": " + price;
                                sendMessage("Order" + role.charAt(role.length()-1), receivers, orderInf);
                            }
                        }
                        else if(interaction.equals("interactionAsk")){
                            HashSet<String> receivers = new HashSet<>();
                            receivers.add("Buyer");
                            sendMessage("Quote" + role.charAt(role.length()-1), receivers, String.valueOf(price));
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
