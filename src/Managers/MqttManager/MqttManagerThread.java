package Managers.MqttManager;

import BundleClasses.SettingsBundle;

/**
 * Manager class that begins the MqttManager within its own thread
 */
public class MqttManagerThread extends Thread {
    MqttManager manager;

    public MqttManagerThread() {
    }

    private String threadName;

    public MqttManagerThread(String threadName) {
        super(threadName);
        this.threadName = threadName;
    }

    @Override
    public void run() {
        manager = new MqttManager();
    }

    public void updateThresholds(SettingsBundle bundle) {
        this.manager.updateThresholds(bundle);
    }
}
