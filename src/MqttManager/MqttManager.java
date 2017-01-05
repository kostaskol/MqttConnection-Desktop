package MqttManager;

import BundleClasses.Constants;
import BundleClasses.SettingsBundle;
import DataBaseManager.DataBaseManager;
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
        connOpts.setWill(Constants.LAST_WILL_TOPIC,
                Constants.MAIN_CLIENT_DISCONNECTING.getBytes(), 2, false);
        connOpts.setMaxInflight(1000);
        connect(connOpts);
        publish(Constants.LOG_TOPIC, currentSettings.getClientName() + " Connected");
        subscribe(2, Constants.NEW_CONNECTION_TOPIC);
        subscribe(2, Constants.REQUEST_ACKNOWLEDGEMENT_TOPIC);
    }

    static void publish(String topic, String message) {
        System.out.println("Publishing " + message + " @ " + topic);
        try {
            client.publish(topic, message.getBytes(), 2, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void subscribe(int QoS, String topic) {
        System.out.println("Subscribing to topic: " + topic);
        try {
            client.subscribe(topic, QoS);
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
            publish(Constants.MAIN_CLIENT_DISCONNECTING, Constants.MAIN_CLIENT_DISCONNECTING);
            client.disconnect();
            DataBaseManager dbManager = new DataBaseManager();
            dbManager.clearClients();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void connectionLost(Throwable throwable) {
        DataBaseManager dbManager = new DataBaseManager();
        dbManager.clearClients();
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        String[] topicParts = topic.split("/");
        System.out.println("Message arrived on topic: " + topic + "(" + mqttMessage + ")");
        if (topicParts[0].equals(Constants.CONNECTION_TOPIC)) {
            //Sort operation by topic:
            switch (topicParts[1]) {
                case Constants.NEW_CONNECTION_TOPIC_SIMPLE:
                    subscribe(2, Constants.CONNECTED_TOPIC + mqttMessage);
                    publish(Constants.CONNECTED_TOPIC +
                            mqttMessage + Constants.CONNECTED_ACKNOWLEDGE_TOPIC, Constants.MESSAGE_ACKNOWLEDGED);
                    break;
                case Constants.CONNECTED_TOPIC_SIMPLE: {
                    String subscribedId = topicParts[2];
                    String message = new String(mqttMessage.getPayload());
                    String[] information = message.split("/");
                    String id = information[Constants.ID];
                    System.out.println("Got new id: " + id);
                    if (!id.equals(subscribedId)) {
                        System.out.println("ID Mismatch");
                        return;
                    }

                    String latitude = information[Constants.LATITUDE];
                    String longitude = information[Constants.LONGITUDE];
                    String lightVal = information[Constants.LIGHT];
                    String proxVal = information[Constants.PROX];

                    CheckPublishAndSaveThread checkAndPublish = new CheckPublishAndSaveThread(
                            "Check and publish", id, lightVal, proxVal,
                            latitude, longitude, currentSettings);

                    checkAndPublish.start();
                    break;
                }
                case Constants.REQUEST_ACKNOWLEDGEMENT_TOPIC_SIMPLE: {
                    String id = mqttMessage.toString();
                    System.out.println("Acknowledgement requested by id: " + id);
                    publish(Constants.CONNECTED_TOPIC + id + Constants.CONNECTED_ACKNOWLEDGE_TOPIC,
                            Constants.MESSAGE_ACKNOWLEDGED);
                    break;
                }
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
