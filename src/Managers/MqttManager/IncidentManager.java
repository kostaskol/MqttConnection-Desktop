package Managers.MqttManager;

import BundleClasses.*;
import Managers.DTManager.DateAndTimeManager;
import Managers.DataBaseManager.DataBaseManager;
import Managers.DataBaseManager.DataBaseManagerThread;

/**
 * Class that checks the given values, publishes different levels of warnings
 * and saves incidents as needed
 */
public class IncidentManager extends Thread {
    private String id;
    private String lightVal;
    private String proxVal;
    private String latitude;
    private String longitude;
    private SettingsBundle bundle;
    private IncidentTime otherIncTime;

    public IncidentManager() {
    }

    IncidentManager(String threadName, String id, String lightVal, String proxVal,
                    String latitude, String longitude, SettingsBundle bundle) {
        super(threadName);

        this.id = id;
        this.lightVal = lightVal;
        this.proxVal = proxVal;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bundle = bundle;

    }

    @Override
    public void run() {
        String lightCheck = checkValueLight();
        String proxCheck = checkValueProx();

        /*
         * If the newly acquired message should ring out a warning
         */
        if (lightCheck.equals(Constants.MESSAGE_WARNING)
                || proxCheck.equals(Constants.MESSAGE_WARNING)) {
            String date = DateAndTimeManager.getDate();
            String time = DateAndTimeManager.getTime();
            DataBaseManager dbManager = new DataBaseManager();
            ClientAverage client = dbManager.getClientAverage(id);
            dbManager.closeConnection();
            boolean isRinging = client.isRinging();


            if (!checkIncidentTime()) {
                if (!isRinging) {
                    client.setIsRinging(true);
                    DataBaseManagerThread dbManagerThread = new DataBaseManagerThread(
                            "UPDATE CLIENT", Constants.UPDATE_CLIENT_AVERAGE, client);
                    dbManagerThread.start();
                    try {
                        dbManagerThread.join();
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                        return;
                    }
                    /*
                    * If the most recent incident did not happen within the same second
                    * as this one *and* the client is not already in a state of warning/danger
                    */
                    Incident inc = new Incident(this.id, 0, this.lightVal, this.proxVal
                            , this.latitude, this.longitude, date, time);
                    dbManagerThread = new DataBaseManagerThread(
                            "SAVE INCIDENT", Constants.SAVE_INCIDENT, inc);
                    dbManagerThread.start();

                    try {
                        dbManagerThread.join();
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                    MqttManager.publish(
                            Constants.CONNECTED_TOPIC + id + Constants.TOPIC_WARNING,
                            Constants.MESSAGE_WARNING);
                }
            } else {
                /*
                 * Otherwise, send out the danger signal to the associated clients
                 */
                Incident inc = new Incident(this.id, 1, this.lightVal, this.proxVal
                        , this.latitude, this.longitude, date, time);
                DataBaseManagerThread dbManagerThread = new DataBaseManagerThread(
                        "SAVE INCIDENT", Constants.SAVE_INCIDENT, inc);
                dbManagerThread.start();
                System.out.println("Notifying current user's ID");

                MqttManager.publish(
                        Constants.CONNECTED_TOPIC + id + Constants.TOPIC_DANGER,
                        Constants.MESSAGE_DANGER);
                try {
                    dbManagerThread.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dbManagerThread = new DataBaseManagerThread(
                        "SWITCH LoD", Constants.UPDATE_DANGER, this.otherIncTime);
                dbManagerThread.start();
                System.out.println("Notifying other user's ID");
                MqttManager.publish(
                        Constants.CONNECTED_TOPIC + otherIncTime.getId() + Constants.TOPIC_DANGER,
                        Constants.MESSAGE_DANGER);
            }
        } else if ((lightCheck.equals(Constants.MESSAGE_STOP_WARNING)
                || lightCheck.equals(Constants.MESSAGE_NO_WARNING))
                && (proxCheck.equals(Constants.MESSAGE_STOP_WARNING)
                || proxCheck.equals(Constants.MESSAGE_NO_WARNING))) {

            /*
             * If either check produces a stop warning signal and
             * the other has not produced a warning signal, stop
             * We always send a stop warning signal if the values
             * are within the thresholds
             * and the android client checks if it's already playing a sound
             */
            DataBaseManager dbManager = new DataBaseManager();
            ClientAverage client = dbManager.getClientAverage(id);
            dbManager.closeConnection();
            client.setIsRinging(false);
            DataBaseManagerThread dbManagerThread = new DataBaseManagerThread(
                    "UPDATE CLIENT", Constants.UPDATE_CLIENT_AVERAGE, client);
            dbManagerThread.start();
            MqttManager.publish(
                    Constants.CONNECTED_TOPIC + id + Constants.TOPIC_STOP_WARNING,
                    Constants.MESSAGE_STOP_WARNING);
        }
    }

    private String checkValueLight() {
        int lightThres = bundle.getLightThres();

        if (this.lightVal.equals("null")) {
            return Constants.MESSAGE_NO_WARNING;
        }
        float lightValF = Float.valueOf(this.lightVal);

        DataBaseManager dbManager = new DataBaseManager();
        ClientAverage client = dbManager.getClientAverage(id);
        dbManager.closeConnection();
        if (client == null) {
            return Constants.MESSAGE_NO_WARNING;
        }
        dbManager.closeConnection();
        if (client.getTimes() < Constants.AVERAGE_TIMES) {
            client.setTimes(client.getTimes() + 1);
            client.setLightSum(client.getLightSum() + lightValF);
            DataBaseManagerThread dbManagerThread = new DataBaseManagerThread(
                    "UPDATE CLIENT", Constants.UPDATE_CLIENT_AVERAGE, client);
            dbManagerThread.start();
            try {
                dbManagerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Constants.MESSAGE_NO_WARNING;
        } else {

            float average;
            if (client.getLightAverage() == 0) {
                /*
                * On the 5th time a client has given us values
                * the average will still be 0, so we need to calculate
                * and update it
                */
                average = client.getLightSum() / client.getTimes();
                client.setLightAverage(average);
                DataBaseManagerThread dbManagerThread = new DataBaseManagerThread(
                        "UPDATE CLIENT", Constants.UPDATE_CLIENT_AVERAGE, client);
                dbManagerThread.start();
            } else {
                /*
                 * Otherwise, we get the saved average
                 */
                average = client.getLightAverage();
            }
            float floorAvg = (100f - lightThres) / 100f;
            float floor = average * floorAvg;
            if (lightValF > average + floor) {
                /*
                 * This client is not as "smart" as the android client
                 * in the sense that, if the current light sensor value
                 * is greater than the average, it will reset and
                 * calculate a new average immediately (in the next AVERAGE_TIMES
                 * times this client will send a message)
                 * whereas the android client (in offline mode)
                 * will wait for 3 seconds in case the light sensor value
                 * falls back to normal
                 */
                client = reset(client);
                DataBaseManagerThread dbManagerThread = new DataBaseManagerThread(
                        "UPDATE CLIENT", Constants.UPDATE_CLIENT_AVERAGE, client);
                dbManagerThread.start();
                return Constants.MESSAGE_STOP_WARNING;
            } else if (lightValF <= average - floor) {
                /*
                 * If, at any point, the light sensor value
                 * falls beneath average - floor, we send
                 * out the warning/danger signal
                 * (the run function checks whether it should send
                 * out a warning or danger signal)
                 */
                return Constants.MESSAGE_WARNING;
            } else {
                /*
                 * If the light sensor value is within the average +- floor
                 * stop warning (if there is one. The android client checks this)
                 */
                return Constants.MESSAGE_STOP_WARNING;
            }
        }
    }

    private String checkValueProx() {
        /*
         * If the proximity value is above
         * the threshold, stop the warning (if any)
         * Else, ring the warning
         */
        int proxThres = bundle.getProxThres();
        float proxVal = Float.valueOf(this.proxVal);

        if (proxVal <= proxThres) {
            DataBaseManager dbManager = new DataBaseManager();
            ClientAverage client = dbManager.getClientAverage(id);
            dbManager.closeConnection();
            client.setIsRinging(true);
            DataBaseManagerThread dbManagerThread = new DataBaseManagerThread(
                    "UPDATE CLIENT", Constants.UPDATE_CLIENT_AVERAGE, client);
            dbManagerThread.start();
            return Constants.MESSAGE_WARNING;
        }

        return Constants.MESSAGE_STOP_WARNING;
    }

    private ClientAverage reset(ClientAverage client) {
        /*
         * Resets the client's average, sum and times sent
         * (A new average will be calculated the next AVERAGE_TIMES times
         * the client sends a message
         */
        client.setLightAverage(0f);
        client.setLightSum(0f);
        client.setTimes(0);
        client.setIsRinging(false);
        return client;
    }

    private boolean checkIncidentTime() {
        MqttManager.publish(Constants.LOG_TOPIC,
                "MQTT | Checking incident time");

        /*
         * Get the last incident's time and check it against the current time
         * If both incidents occurred within the same hour, minute and second,
         * we send out a warning/danger signal (the run function checks this)
         */
        DataBaseManager dbManager = new DataBaseManager();

        String timeString = DateAndTimeManager.getTime();
        int[] currentTime = DateAndTimeManager.timeToParts(timeString);
        /*
         * Returns the most recent incident FOR TODAY.
         * If there were no incidents today or an error occurred, it returns null
         */
        IncidentTime lastIncidentTime = dbManager.getLastIncidentTime();

        dbManager.closeConnection();

        if (lastIncidentTime == null) {
            MqttManager.publish(Constants.LOG_TOPIC,
                    "MQTT | Last Incident is null");
            return false;
        }

        /*
         * If the same client was the last one to be in a state of
         * warning, we return false
         */
        if (this.id.equals(lastIncidentTime.getId())) {
            MqttManager.publish(Constants.LOG_TOPIC,
                    "MQTT | Checking incident time");
            return false;
        }

        timeString = lastIncidentTime.getTime()[0] + ":" +
                lastIncidentTime.getTime()[1] + ":" + lastIncidentTime.getTime()[2];
        MqttManager.publish(Constants.LOG_TOPIC,
                "MQTT | Last Incident's time is: " + timeString);
        int[] time = lastIncidentTime.getTime();

        timeString = time[0] + ":" + time[1] + ":" + time[2];
        boolean sameHour = currentTime[0] == time[0];
        if (!sameHour) {
            return false;
        }

        boolean sameMinute = currentTime[1] == time[1];
        if (!sameMinute) {
            return false;
        }
        /*
         * We allow a one second difference between the two incidents
         */
        boolean sameSecond = currentTime[2] == time[2] || currentTime[2] == time[2] + 1;

        /*
         * If the two incidents did happen at the same time
         * we return true
         */
        if (sameSecond) {
            this.otherIncTime = lastIncidentTime;
            return true;
        } else {
            return false;
        }
    }
}
