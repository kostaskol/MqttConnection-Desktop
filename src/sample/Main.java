package sample;

public class Main {

    public static void main(String[] args) {
        WindowThread mainWindow = new WindowThread("Window Thread");
        mainWindow.start();

        MqttManagerThread mqttManager = new MqttManagerThread("Mqtt Thread");
        mqttManager.start();


    }
}
