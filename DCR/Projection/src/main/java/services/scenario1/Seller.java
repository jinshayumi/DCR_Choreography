package services.scenario1;

import models.dcrGraph.DCRGraph;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import services.AsychroService;
import services.entities.Data;
import services.entities.IntData;

import java.util.HashSet;

public class Seller extends AsychroService {
    private SubscribeThread subscribeThread;

    private int price;

    public Seller(String role, HashSet<String> subscribeTopics, DCRGraph dcrGraph){
        super(role, dcrGraph);
        if(role.charAt(role.length()-1)=='1'){
            price = 25;
        }
        else {
            price = 15;
        }
        subscribeThread = new SubscribeThread(subscribeTopics);
        subscribeThread.run();
    }

    @Override
    public Data calculate(String event) {
        if (event.startsWith("Quote")){
            return new IntData(price);
        }
        else return eventData.get("Accept" + event.charAt(event.length()-1));
    }

    @Override
    public void initiateGuardEvents() {

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

                        handleMessage(interaction, (IntData) unserialize(message.getPayload()));

                        if(interaction.startsWith("Accept")){
                            if (interaction.charAt(interaction.length()-1)==role.charAt(role.length()-1)){
                                String orderInf = interaction.split("/")[0] + ": " + price;
                                sendMessage("Order" + role.charAt(role.length()-1));
                            }
                        }
                        else if(interaction.equals("interactionAsk")){
                            sendMessage("Quote" + role.charAt(role.length()-1));
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
