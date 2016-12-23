package sample;

import java.sql.*;
import org.eclipse.paho.client.mqttv3.*;

public class MqttManager implements MqttCallback {
    private MqttClient client;
    /*
     * If we consider the topics as a tree,
     * The ID of each client will always be on depth 2 (connections/connected/<ID>)
     */
    private final String logTopic = "log";
    private final String connectionTopic = "connections";
    private final String newConnectionTopic = connectionTopic + "/newConnections";
    private final String connectedTopic = connectionTopic + "/connected";

    MqttManager() {
        try {
            client = new MqttClient("tcp://localhost:1883", "Main Client");
            MqttConnectOptions connOpt = new MqttConnectOptions();
            connOpt.setCleanSession(true);
            client.setCallback(this);

            client.connect();

            MqttMessage message = new MqttMessage("Main Client Conneced".getBytes());

            client.publish(logTopic, message);

            int subQoS = 2;
            System.out.println("Subscribing to topic " + newConnectionTopic);
            client.subscribe(newConnectionTopic, subQoS);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    void disconnect() {
        try {
            System.out.println("Disconnecting");
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        String[] topicParts = topic.split("/");
        
        if (topicParts[0].equals(connectionTopic)) {
            //Sort operation by topic:
            //newConnections
            if (topicParts[1].equals(newConnectionTopic)) {
                client.subscribe(connectedTopic + mqttMessage);
            } else if (topicParts[1].equals(connectedTopic)) {
                String subscribedId = topicParts[2];
                String message = new String(mqttMessage.getPayload());
                String[] information = message.split("/");
                String id = information[Constants.ID];
                if (!id.equals(subscribedId)) {
                    System.out.println ("ID mismatch");
                }

                String latitude = information[Constants.LATITUDE];
                double actualLat = Double.valueOf(latitude);
                String longitude = information[Constants.LONGITUDE];
                double actualLng = Double.valueOf(longitude);
                String lightVal = information[Constants.LIGHT];
                String proxVal = information[Constants.PROX];

            }



        }


    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
