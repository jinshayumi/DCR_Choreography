package services.scenario1;

import models.dcrGraph.DCRGraph;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import services.AsychroService;
import services.entities.Data;
import services.entities.IntData;

import java.util.HashSet;

public class Buyer extends AsychroService {
    private SubscribeThread subscribeThread;

    private int price1=-1;
    private int price2=-1;

    public Buyer(String role, HashSet<String> subscribeTopics, DCRGraph dcrGraph){
        super(role, dcrGraph);
        subscribeThread = new SubscribeThread(subscribeTopics);
        subscribeThread.run();
    }

    public void execute(String interaction){
        sendMessage(interaction);
    }

    @Override
    public Data calculate(String event) {
        if(event.equals("interactionAsk")){
            return new IntData(0);
        }
        else if (event.equals("Accept1")){
            return eventData.get("Quote1");
        }
        else if (event.equals("Accept2")){
            return eventData.get("Quote2");
        }
        else return new IntData(0);
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
                        System.out.println(cause.getMessage());
                        System.out.println(role + " connectionLost");
                    }

                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        System.out.println(role + ": " + "topic:"+topic);
                        System.out.println(role + ": " + "Qos:"+message.getQos());
                        System.out.println(role + ": " + "message content:"+ new String(message.getPayload()));

                        String interaction = topic;

                        handleMessage(interaction, (IntData) unserialize(message.getPayload()));
                        eventData.put(interaction, (IntData) unserialize(message.getPayload()));

                        if(interaction.startsWith("Quote")){
                            if (interaction.equals("Quote1")){
                                price1 = ((IntData) unserialize(message.getPayload())).getData();
                            }
                            else{
                                price2 = ((IntData) unserialize(message.getPayload())).getData();
                            }
                            if(price1>=0 && price2 >=0){
                                if(price1 >20 && price2>20){
                                    sendMessage("Reject");
                                }
                                else if (price1<price2){
                                    sendMessage("Accept1");
                                }
                                else {
                                    sendMessage("Accept2");
                                }
                            }
                        }
                        else if(interaction.equals("ShipDetails")){
                            System.out.println(role + ": " + "details:"+ new String(message.getPayload()));
                        }
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
