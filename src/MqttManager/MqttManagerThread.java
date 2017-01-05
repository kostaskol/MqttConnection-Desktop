package MqttManager;

//TODO: Check incident's ID before ringing for danger

public class MqttManagerThread extends Thread {
    MqttManager manager;

    public MqttManagerThread(){}

    private String threadName;

    public MqttManagerThread(String threadName) {
        super(threadName);
        this.threadName = threadName;
    }

    @Override
    public void run() {
        System.out.println ("Connecting to mqtt client");
        manager = new MqttManager();
    }

    public void disconnect() {
        manager.disconnect();
    }

    public void reconnectIfNecessary() {
        /*
         * MqttManager.reconnectIfNecessary will check if
         * any of the MQTT client's settings have been changed.
         * If not, it will update the object's current settings (thresholds)
         * If they have, it will try to reconnect
         */
        manager.reconnectIfNecessary();
    }
}
