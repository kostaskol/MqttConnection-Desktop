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
        manager = new MqttManager();
        System.out.println ("Connecting to mqtt client");
    }

    void disconnect() {
        manager.disconnect();
    }
}
