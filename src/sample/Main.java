package sample;

public class Main {

    private static MqttManagerThread mqttManager;

    public static void main(String[] args) {
        WindowThread mainWindow = new WindowThread("Window Thread");
        mainWindow.start();

        mqttManager = new MqttManagerThread("Mqtt Thread");
        mqttManager.start();
    }

    static void settingsChanged() {
        mqttManager.reconnectIfNecessary();
    }
}
