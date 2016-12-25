package sample;

public class Main {

    private static MqttManagerThread mqttManager;
    private static WindowThread mainWindow;

    public static void main(String[] args) {
        mainWindow = new WindowThread("Window Thread");
        mainWindow.start();

        mqttManager = new MqttManagerThread("Mqtt Thread");
        mqttManager.start();
    }

    /*
     * When the MQTT client's settings have been changed,
     * the client must reconnect
     */
    static void settingsChanged() {
        mqttManager.reconnectIfNecessary();
    }

    /*
     * This gets called when the main window is closed
     * and is only called by the Window class
     */
    static void close() {
        try {
            mainWindow.interrupt();
        } catch (Exception e) {
            System.out.println("Main Window exited");
        }

        try {
            mqttManager.interrupt();
        } catch (Exception e) {
            System.out.println("MQTT Manager exited");
        }
        System.out.println("Application shutting down");
        System.exit(0);
    }
}
