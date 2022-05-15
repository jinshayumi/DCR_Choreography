package services;

import models.dcrGraph.DCRGraph;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.ui.swing.SwingGraphRenderer;
import org.graphstream.ui.swing_viewer.DefaultView;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.view.Viewer;
import services.entities.SynchronousReply;
import services.entities.data.Data;
import services.entities.data.VoidData;
import visualization.DCRVisual;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;

public class SynchronousCoordinator {

    private String role = "MQTTListener";

    public String HOST = "tcp://127.0.0.1:61613";
    public int qos = 1;
    public String userName = "admin";
    public String passWord = "password";
    public MemoryPersistence persistence = new MemoryPersistence();

    private SubscribeThread subscribeThread;
    private DCRGraph dcrGraph;
    HashMap<String, DCRGraph> dcrGraphHashSet;

    // Visualization Map.
    private HashMap<String, DCRVisual> visualMap;

    // Each Role's event number.
    private HashMap<String, Integer> nextEventNumberMap;

    public SynchronousCoordinator(HashMap<String, DCRGraph> dcrGraphHashMap, DCRGraph dcrGraph)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        this.dcrGraphHashSet = dcrGraphHashMap;
        this.dcrGraph = dcrGraph;
        visualMap = new HashMap<>();
        nextEventNumberMap = new HashMap<>();
        for (String role: dcrGraphHashMap.keySet()){
            nextEventNumberMap.put(role, 0);
        }

        subscribeThread = new SubscribeThread(new HashSet<>(dcrGraph.getEvents()));
        subscribeThread.run();
        for (String role: dcrGraphHashMap.keySet()){
            DCRVisual dcrVisual = new DCRVisual(dcrGraphHashMap.get(role), role);
            visualMap.put(role, dcrVisual);
        }

        SwingUtilities.invokeLater(new MultipleViews());
    }

    private class MultipleViews implements Runnable {

        @Override
        public void run() {
            JFrame frame = new JFrame("Multiple Views");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel panelBuyer = createPanel("Buyer");
            JPanel panelSeller1 = createPanel("Seller1");
            JPanel panelSeller2 = createPanel("Seller2");

            frame.add(panelSeller1, BorderLayout.BEFORE_LINE_BEGINS);
            frame.add(panelSeller2, BorderLayout.AFTER_LINE_ENDS);
            frame.add(panelBuyer, BorderLayout.AFTER_LAST_LINE);

            frame.pack();
            frame.setLocationByPlatform(true);
            frame.setVisible(true);
        }

        private JPanel createPanel(String role){
            MultiGraph graph = visualMap.get(role).visualGraph;
            ThreadProxyPipe pipe = new ThreadProxyPipe() ;
            pipe.init(graph);
            Viewer viewer = new SwingViewer(pipe);
            DefaultView view = new DefaultView(viewer, role, new SwingGraphRenderer());
            viewer.addView(view);
            viewer.enableAutoLayout();

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(view, BorderLayout.CENTER);
            panel.setBackground(Color.white);
            panel.setPreferredSize(new Dimension(750, 350));
            panel.add(new JLabel(role), BorderLayout.BEFORE_FIRST_LINE);

            JPanel panel1 = new JPanel(new BorderLayout(5, 5));

            panel1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            panel1.add(panel, BorderLayout.CENTER);
            return panel1;
        }
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
                        cause.printStackTrace();
                        System.out.println(role + " connectionLost");
                    }

                    public void messageArrived(String topic, MqttMessage message) throws Exception {
//                        System.out.println(role + ": " + "topic:"+topic);
//                        System.out.println(role + ": " + "message content:"+ new String(message.getPayload()));

                        handleMessage(topic, message);
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

    private void publishMessage(String topic, Object dataToSend) throws MqttException {
        // create client.
        MqttClient sampleClient = new MqttClient(HOST, "pub"+role, persistence);
        // create connection options
        MqttConnectOptions connOpts = new MqttConnectOptions();
        // remember state when restarting.
        connOpts.setCleanSession(false);
        // username and password.
        connOpts.setUserName(userName);
        connOpts.setPassword(passWord.toCharArray());
        // connect.
        sampleClient.connect(connOpts);

        //serialize the data.
        byte[] serializedData;
        serializedData = serialize(dataToSend);

        // send the data.
        // message serialization.
        MqttMessage message = new MqttMessage(serializedData);

        // qos.
        message.setQos(qos);
        // publish.
        sampleClient.publish(topic, message);
        // disconnect.
        sampleClient.disconnect();
        // close client.
        sampleClient.close();
    }

    public void handleMessage(String topic, MqttMessage message)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException,
            IllegalAccessException, MqttException {
        String interaction = topic;
        String sender = dcrGraph.getEventsInitiator().get(topic);
        HashSet<String> receivers = dcrGraph.getEventsReceivers().get(topic);

        Data payload = (Data) unserialize(message.getPayload());

        Long time = System.currentTimeMillis();

        boolean enabled = true;
        if (!dcrGraphHashSet.get(sender).enabled(interaction, time)){
            System.out.println("**Coordinator: " + interaction + " is not enabled");

            // only send to the sender.
            SynchronousReply reply = new SynchronousReply(new VoidData("void"), -1L, new HashMap<>(), interaction);
            publishMessage("reject@" + interaction, reply);

            enabled = false;
        }
        if (enabled){
            // update the event.
            if (dcrGraph.getDataLogicMap().get(interaction).getType().equals("?")){
                dcrGraph.updateEventData(interaction, payload);
                dcrGraphHashSet.get(sender).updateEventData(interaction, payload);
                for (String receiver: receivers){
                    dcrGraphHashSet.get(receiver).updateEventData(interaction, payload);
                }
            }
            // have data calculation logic
            else if(!dcrGraph.getDataLogicMap().get(interaction).getType().equals("")){
                // calculate and update.
                dcrGraph.calculateAnEvent(interaction);
            }
            dcrGraph.execute(interaction, time);
            dcrGraphHashSet.get(sender).execute(interaction, time);
            visualMap.get(sender).updateMarkings();
            for (String receiver: receivers){
                dcrGraphHashSet.get(receiver).execute(interaction, time);
                visualMap.get(receiver).updateMarkings();
            }

            // send execute to sender and all the receivers.
            Data data = dcrGraph.getDataMap().get(interaction);
            System.out.println("coordinator publish " + "execute@"+interaction);
            HashMap<String, Integer> sentMap = new HashMap<>();
            int senderSequence = nextEventNumberMap.get(sender);
            sentMap.put(sender, senderSequence);
            nextEventNumberMap.put(sender, senderSequence+1);
            for (String receiver: receivers){
                int receiverSequence = nextEventNumberMap.get(receiver);
                sentMap.put(receiver, receiverSequence);
                nextEventNumberMap.put(receiver, receiverSequence+1);
            }
            publishMessage("execute@"+interaction,
                    new SynchronousReply(data, time, sentMap, interaction));
            System.out.println("sequenceId map now is: " + nextEventNumberMap);
        }
    }

    // help function to serialize an object to byte[].
    public static byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            // 序列化
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception e) {

        }
        return null;
    }

    public static Object unserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;
        try {
            // 反序列化
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {

        }
        return null;
    }
}
