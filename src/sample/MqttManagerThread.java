package sample;

public class MqttManagerThread extends Thread {
    MqttManager manager;

    public MqttManagerThread(){}

    private String threadName;

    MqttManagerThread(String threadName) {
        super(threadName);
        this.threadName = threadName;
    }

    @Override
    public void run() {
        System.out.println ("Connecting to mqtt client");
        manager = new MqttManager();
    }

    void disconnect() {
        manager.disconnect();
    }

    void reconnectIfNecessary() {
        /*
         * MqttManager.reconnectIfNecessary will check if
         * any of the MQTT client's settings have been changed.
         * If not, it will update the object's current settings (thresholds)
         * If they have, it will try to reconnect
         */
        manager.reconnectIfNecessary();
    }
}
