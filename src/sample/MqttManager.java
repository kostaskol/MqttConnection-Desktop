package sample;

import org.eclipse.paho.client.mqttv3.*;

public class MqttManager implements MqttCallback {
    static MqttClient client;
    /*
     * If we consider the topics as a tree,
     * The ID of each client will always be on depth 2 (connections/connected/<ID>)
     */

    private SettingsBundle currentSettings;

    MqttManager() {
        DataBaseManager dbManager = new DataBaseManager();
        int prof = dbManager.getSelectedProfile();
        currentSettings = dbManager.getProfile(prof);
        dbManager.closeConnection();
        connectClient();
    }

    private MqttClient getClient() {
        try {
            System.out.println("Creating new client with settings: " +
                    currentSettings.getConnUrl() + ":" + currentSettings.getPort());
            return new MqttClient(currentSettings.getConnUrl() + ":" + currentSettings.getPort(),
                    currentSettings.getClientName());
        } catch (MqttException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void connect(MqttConnectOptions connOpts) {
        try {
            client.connect(connOpts);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void connectClient() {
        client = getClient();
        assert client != null;
        client.setCallback(this);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(currentSettings.getCleanSess());

        connect(connOpts);
        publish(Constants.LOG_TOPIC, currentSettings.getClientName() + " Connected");
        subscribe(2, Constants.NEW_CONNECTION_TOPIC);
    }

    static void publish(String topic, String message) {
        MqttMessage m = new MqttMessage(message.getBytes());
        try {
            client.publish(topic, m);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void subscribe(int QoS, String topic) {
        try {
            client.subscribe(topic, QoS);
            System.out.println("Subscibed to topic: " + topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    void reconnectIfNecessary() {
        DataBaseManager dbManager = new DataBaseManager();
        SettingsBundle tmpBundle = dbManager.getProfile(dbManager.getSelectedProfile());
        if (currentSettings != null &&
                (!tmpBundle.getConnUrl().equals(currentSettings.getConnUrl())
                        || tmpBundle.getCleanSess() != currentSettings.getCleanSess()
                        || !tmpBundle.getPort().equals(currentSettings.getPort())
                        || !tmpBundle.getClientName().equals(currentSettings.getClientName()))) {
            disconnect();
            connectClient();
        } else {
            this.currentSettings = tmpBundle;
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
        System.out.println("Message arrived on topic: " + topic + "(" + mqttMessage + ")");

        if (topicParts[0].equals(Constants.CONNECTION_TOPIC)) {
            //Sort operation by topic:
            //newConnections
            if (topicParts[1].equals(Constants.NEW_CONNECTION_TOPIC_SIMPLE)) {
                System.out.println("Subscribing to topic: " + topicParts[1]);
                client.subscribe(Constants.CONNECTED_TOPIC + "/" + mqttMessage);
            } else if (topicParts[1].equals(Constants.CONNECTED_TOPIC_SIMPLE)) {
                System.out.println("Just got new message!");
                String subscribedId = topicParts[2];
                String message = new String(mqttMessage.getPayload());
                String[] information = message.split("/");
                String id = information[Constants.ID];
                if (!id.equals(subscribedId)) {
                    System.out.println("subscribed id: " + subscribedId
                            + " received id: " + id);
                }

                String latitude = information[Constants.LATITUDE];
                String longitude = information[Constants.LONGITUDE];
                String lightVal = information[Constants.LIGHT];
                String proxVal = information[Constants.PROX];

                CheckPublishAndSaveThread checkAndPublish = new CheckPublishAndSaveThread(
                        "Check and publish", id, lightVal, proxVal,
                        latitude, longitude, currentSettings);

                checkAndPublish.start();
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
