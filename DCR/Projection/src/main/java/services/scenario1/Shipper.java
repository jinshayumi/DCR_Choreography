package services.scenario1;

import models.dcrGraph.DCRGraph;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import services.AsychroService;
import services.entities.Data;
import services.entities.IntData;

import java.util.HashSet;

public class Shipper extends AsychroService {
    private SubscribeThread subscribeThread;

    public Shipper(String role, HashSet<String> subscribeTopics, DCRGraph dcrGraph) {
        super(role, dcrGraph);
        subscribeThread = new SubscribeThread(subscribeTopics);
        subscribeThread.run();
    }

    @Override
    public Data calculate(String event) {
        return eventData.get(event);
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

                        // time.
                        handleMessage(interaction, (IntData) unserialize(message.getPayload()));

                        if(interaction.startsWith("Order")){
                            eventData.put("ShipDetails", (IntData) unserialize(message.getPayload()));
                            sendMessage("ShipDetails");
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
