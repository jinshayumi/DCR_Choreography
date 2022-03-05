package services.roles;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import services.AsychroService;

import java.util.HashSet;

public class Shipper extends AsychroService {
    private SubscribeThread subscribeThread;

    public Shipper(String role) {
        super(role);
        subscribeThread = new SubscribeThread("+/+/" + role);
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

                        String interaction = topic.split("/")[1];

                        if(interaction.startsWith("Order")){
                            HashSet<String> receivers = new HashSet<>();
                            receivers.add("Buyer");
                            sendMessage("ShipDetails", receivers, new String(message.getPayload()));
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
