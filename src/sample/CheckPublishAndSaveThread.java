package sample;

public class CheckPublishAndSaveThread extends Thread {
    private String threadName;
    private String id;
    private String lightVal;
    private String proxVal;
    private String latitude;
    private String longitude;
    private SettingsBundle bundle;

    private boolean isWarned;

    public CheckPublishAndSaveThread() {
    }

    CheckPublishAndSaveThread(String threadName, String id, String lightVal, String proxVal,
                              String latitude, String longitude, SettingsBundle bundle) {
        this.threadName = threadName;
        this.id = id;
        this.lightVal = lightVal;
        this.proxVal = proxVal;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bundle = bundle;
        isWarned = false;
    }

    @Override
    public void run() {
        String lightCheck = checkValueLight();
        String proxCheck = checkValueProx();
        if (lightCheck.equals(Constants.WARNING)
                || proxCheck.equals(Constants.WARNING)) {
            MqttManager.publish(
                    Constants.CONNECTED_TOPIC + "/" + id + "/warning",
                    Constants.WARNING);
            this.isWarned = true;
            saveIncident();
        } else if (lightCheck.equals(Constants.STOP_WARNING)) {
            MqttManager.publish(
                    Constants.CONNECTED_TOPIC + "/" + id + "/warning",
                    Constants.STOP_WARNING);
            this.isWarned = false;
        }
    }

    boolean hasBeenWarned() {
        return this.isWarned;
    }

    private String checkValueLight() {
        int lightThres = bundle.getLightThres();
        float lightVal = Float.valueOf(this.lightVal);

        DataBaseManager dbManager = new DataBaseManager();
        ClientAverage client = dbManager.getClientAverage(id);
        if (client.getTimes() < 10) {
            client.setTimes(client.getTimes() + 1);
            client.setLightSum(client.getLightSum() + lightVal);
            dbManager.updateClientAverage(client);
            return Constants.NO_WARNING;
        } else {
            float average = client.getLightSum() / client.getTimes();
            client.setLightAverage(average);
            if (lightVal > average + lightThres) {
                client = reset(client);
                dbManager.updateClientAverage(client);
                dbManager.closeConnection();
                return Constants.NO_WARNING;
            } else if (lightVal <= average - lightThres) {
                return Constants.WARNING;
            } else {
                return Constants.STOP_WARNING;
            }
        }
    }

    private String checkValueProx() {
        int proxThres = bundle.getProxThres();
        int proxVal = Integer.parseInt(this.proxVal);
        if (proxVal < proxThres) {
            return Constants.WARNING;
        }
        return Constants.STOP_WARNING;
    }

    private ClientAverage reset(ClientAverage client) {
        client.setLightAverage(0f);
        client.setLightSum(0f);
        client.setTimes(0);
        return client;
    }

    private void saveIncident() {
        DataBaseManager dbManager = new DataBaseManager();
        int[] currentTime = HelpFunc.timeToParts(HelpFunc.getTime());
        IncidentTime lastIncidentTime = dbManager.getLastIncidentTime();
        int[] time = lastIncidentTime.getTime();
        boolean sameHour = currentTime[0] == time[0];
        boolean sameMinute = currentTime[1] == time[1];
        boolean sameSecond = currentTime[2] == time[2];

        boolean sameTime = sameHour && sameMinute && sameSecond;
        if (sameTime) {
            SaveIncidentThread saveIncident = new SaveIncidentThread(
                    "Save Incident", id, 1, lightVal,
                    proxVal, latitude, longitude);

            saveIncident.start();

            dbManager.updateDanger(lastIncidentTime.getId());
        } else {

            SaveIncidentThread saveIncident = new SaveIncidentThread(
                    "Save Incident", id, 0, lightVal, proxVal, latitude, longitude);
            saveIncident.start();
        }
    }
}
