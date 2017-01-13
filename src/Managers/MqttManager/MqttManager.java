package Managers.MqttManager;

import BundleClasses.Constants;
import BundleClasses.SettingsBundle;
import Managers.DataBaseManager.DataBaseManager;
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
        publish(Constants.LOG_TOPIC,
                "MQTT | Connecting to MQTT client");
        client = getClient();
        assert client != null;
        client.setCallback(this);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(currentSettings.getCleanSess());
        connOpts.setWill(Constants.LAST_WILL_TOPIC,
                Constants.MAIN_CLIENT_DISCONNECTING.getBytes(), 2, false);
        connOpts.setMaxInflight(1000);
        connect(connOpts);
        publish(Constants.LOG_TOPIC,
                "MQTT | " + currentSettings.getClientName() + " connected");
        subscribe(Constants.NEW_CONNECTION_TOPIC);
        subscribe(Constants.REQUEST_ACKNOWLEDGEMENT_TOPIC);
    }

    public static void publish(String topic, String message) {
        if (client != null) {
            if (client.isConnected()) {
                try {
                    client.publish(topic, message.getBytes(), 2, false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void subscribe(String topic) {
        if (client != null) {
            if (client.isConnected()) {
                publish(Constants.LOG_TOPIC,
                        "MQTT | Subscribing to topic " + topic);
                try {
                    client.subscribe(topic, 2);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void connectionLost(Throwable throwable) {
        DataBaseManager dbManager = new DataBaseManager();
        dbManager.clearClients();
        dbManager.closeConnection();
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        publish(Constants.LOG_TOPIC, "Got new message: " + mqttMessage.toString() +
                " @ " + topic);
        String[] topicParts = topic.split("/");
        if (topicParts[0].equals(Constants.CONNECTION_TOPIC)) {
            //Sort operation by topic:
            switch (topicParts[1]) {
                case Constants.NEW_CONNECTION_TOPIC_SIMPLE:
                    subscribe(Constants.CONNECTED_TOPIC + mqttMessage);
                    publish(Constants.LOG_TOPIC,
                            "MQTT | Got new ID: " + mqttMessage);
                    break;
                case Constants.CONNECTED_TOPIC_SIMPLE: {
                    String subscribedId = topicParts[2];
                    String message = new String(mqttMessage.getPayload());
                    String[] information = message.split("/");
                    String id = information[Constants.ID];

                    if (!id.equals(subscribedId)) {
                        publish(Constants.LOG_TOPIC,
                                "ERROR: MQTT | ID Mismatch");
                        return;
                    }

                    String latitude = information[Constants.LATITUDE];
                    String longitude = information[Constants.LONGITUDE];
                    String lightVal = information[Constants.LIGHT];
                    String proxVal = information[Constants.PROX];

                    IncidentManager checkAndPublish = new IncidentManager(
                            "Check and publish", id, lightVal, proxVal,
                            latitude, longitude, currentSettings);

                    checkAndPublish.start();
                    break;
                }
                case Constants.REQUEST_ACKNOWLEDGEMENT_TOPIC_SIMPLE: {
                    String id = mqttMessage.toString();
                    /*
                     * Whenever a client requests an acknowledge response,
                     * we supply them with the frequency with which they should
                     * send the data
                     */
                    publish(Constants.LOG_TOPIC,
                            "MQTT | Acknowledgement request by ID " + id);
                    publish(Constants.CONNECTED_TOPIC + id + Constants.CONNECTED_ACKNOWLEDGE_TOPIC,
                            String.valueOf(currentSettings.getFrequency()));
                    break;
                }
            }
        }
    }

    void updateThresholds(SettingsBundle bundle) {
        this.currentSettings = bundle;
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
