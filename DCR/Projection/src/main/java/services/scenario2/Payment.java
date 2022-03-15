package services.scenario2;

import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import services.AsychroService;
import sun.jvm.hotspot.ui.tree.FloatTreeNodeAdapter;

import java.util.HashMap;
import java.util.HashSet;

public class Payment extends AsychroService {
    private HashMap<String, Float> accountInf;
    private HashMap<String, Float> priceInf;

    private SubscribeThread subscribeThread;
    public Payment(String role,  HashSet<String> subscribeTopics) {
        super(role);
        accountInf = new HashMap<>();
        accountInf.put("Basket1", 10f);
        accountInf.put("Basket2", 10f);
        priceInf = new HashMap<>();
        priceInf.put("Book", 10f);
        subscribeThread = new SubscribeThread(subscribeTopics);
        subscribeThread.run();
    }

    private class SubscribeThread extends Thread{
        private  HashSet<String> subscribeTopics;

        public SubscribeThread( HashSet<String> subscribeTopics){
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
                        String inf = new String(message.getPayload());
                        String item = inf.split(" ")[0];
                        int quantity = Integer.parseInt(inf.split(" ")[1]);
                        if (interaction.startsWith("Enough")){
                            float remaining = accountInf.get("Basket" + interaction.charAt(interaction.length()-1));
                            float require = quantity * priceInf.get(item);
                            if (remaining >= require){
                                HashSet<String > aims = new HashSet<>();
                                aims.add("Catalog");
                                accountInf.put("Basket" + interaction.charAt(interaction.length()-1), remaining-require);
                                Thread.sleep(2000);
                                sendMessage("Successful" + interaction.charAt(interaction.length()-1), aims, inf);
                            }
                            else {
                                HashSet<String > aims = new HashSet<>();
                                aims.add("Order");
                                aims.add("Basket" + interaction.charAt(interaction.length()-1));
                                aims.add("Catalog");
                                sendMessage("Fail" + interaction.charAt(interaction.length()-1), aims, inf);
                            }
                        }
                        else if (interaction.startsWith("Not Enough")){
                            System.out.println(role + ": " + "Not Enough:"+ new String(message.getPayload()));
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
