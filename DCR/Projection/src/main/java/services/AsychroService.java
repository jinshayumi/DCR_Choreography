package services;

import models.dcrGraph.DCRGraph;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.HashMap;

public class AsychroService {
    private String HOST = "tcp://127.0.0.1:61613";
    private int qos = 1;
    private String userName = "admin";
    private String passWord = "password";
    private MemoryPersistence persistence = new MemoryPersistence();

    private String role;
    private DCRGraph dcrGraph;

    private HashMap<String, SubscribeThread> subscribeThreadHashMap;

    public AsychroService(String role, DCRGraph endUpProjection){
        this.role = role;
        this.dcrGraph = endUpProjection;
        subscribeThreadHashMap = new HashMap<>();
        subscribeTopics(endUpProjection);
    }

    public void execute(String interaction, String aim){
        try {
            // 创建客户端
            MqttClient sampleClient = new MqttClient(HOST, "pub"+role, persistence);
            // 创建链接参数
            MqttConnectOptions connOpts = new MqttConnectOptions();
            // 在重新启动和重新连接时记住状态
            connOpts.setCleanSession(false);
            // 设置连接的用户名
            connOpts.setUserName(userName);
            connOpts.setPassword(passWord.toCharArray());
            // 建立连接
            sampleClient.connect(connOpts);
            // 创建消息
            MqttMessage message = new MqttMessage((role + " " + interaction + " " + aim).getBytes());
            // 设置消息的服务质量
            message.setQos(qos);
            // 发布消息
            sampleClient.publish((role + " " + interaction + " " + aim), message);
            // 断开连接
            sampleClient.disconnect();
            // 关闭客户端
            sampleClient.close();
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }

    }

    private void subscribeTopics(DCRGraph endUpProjection) {
        for(String event: endUpProjection.getEventsReceivers().keySet()){
            if (endUpProjection.getEventsReceivers().get(event).contains(role)){
                SubscribeThread subscribeThread = new SubscribeThread(endUpProjection.getEventsInitiator().get(event)+" " + event + " " + role);
                subscribeThread.start();
                subscribeThreadHashMap.put(endUpProjection.getEventsInitiator().get(event)+" " + event + " " + role, subscribeThread);
            }
        }
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
                MqttClient client = new MqttClient(HOST, "sub" + topic, new MemoryPersistence());
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
                        System.out.println(role + ": " + "message content:"+new String(message.getPayload()));
                        synchronized (dcrGraph){
                            dcrGraph.execute(message.getPayload().toString().split(" ")[1]);
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
