package Main;

import BundleClasses.Constants;
import BundleClasses.SettingsBundle;
import Managers.DataBaseManager.DataBaseManager;
import Managers.MqttManager.MqttManager;
import Managers.MqttManager.MqttManagerThread;
import Windows.WindowThread;

public class Main {

    private static MqttManagerThread mqttManager;
    private static WindowThread mainWindow;

    public static void main(String[] args) {
        /*
         * Start the window thread
         */
        mainWindow = new WindowThread("Window Thread");
        mainWindow.start();

        /*
         * Start the Managers.MqttManager thread
         */
        mqttManager = new MqttManagerThread("Mqtt Thread");
        mqttManager.start();
    }

    /*
     * If the user has changed the threshold settings,
     * we update the Managers.MqttManager's thresholds
     * Note: Any MQTT broker related setting changes,
     * take effect the next time you start the application
     */
    public static void updateThresholds(SettingsBundle bundle) {
        mqttManager.updateThresholds(bundle);
    }

    /*
     * This gets called when the main window is closed
     * and is only called by the Window class
     */
    public static void close() {

        /*
         * Interrupt both threads and close the programme
         */
        mainWindow.interrupt();

        mqttManager.interrupt();

        DataBaseManager dbManager = new DataBaseManager();
        dbManager.clearClients();


        MqttManager.publish(Constants.LOG_TOPIC,
                "APPLICATION | Shutting down");
        System.exit(0);
    }
}
