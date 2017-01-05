package MqttManager;

import BundleClasses.*;
import DataBaseManager.DataBaseManager;
import DataBaseManager.DataBaseManagerThread;
import HelpfulFunctions.HelpFunc;

public class CheckPublishAndSaveThread extends Thread {
    private String id;
    private String lightVal;
    private String proxVal;
    private String latitude;
    private String longitude;
    private SettingsBundle bundle;
    private String otherId;

    public CheckPublishAndSaveThread() {
    }

    CheckPublishAndSaveThread(String threadName, String id, String lightVal, String proxVal,
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
            String date = HelpFunc.getDate();
            String time = HelpFunc.getTime();
            DataBaseManager dbManager = new DataBaseManager();
            ClientAverage client = dbManager.getClientAverage(id);
            boolean isRinging = client.isRinging();
            /*
             * If the most recent incident did not happen within the same second
             * as this one
             */
            if (!checkIncidentTime()) {
                if (!isRinging) {
                    Incident inc = new Incident(this.id, 0, this.lightVal, this.proxVal
                            , this.latitude, this.longitude, date, time);
                    DataBaseManagerThread dbManagerThread = new DataBaseManagerThread(
                            "SAVE INCIDENT", Constants.SAVE_INCIDENT, inc);
                    dbManagerThread.start();
                    try {
                        dbManagerThread.join();
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
                MqttManager.publish(
                        Constants.CONNECTED_TOPIC + id + Constants.TOPIC_WARNING,
                        Constants.MESSAGE_WARNING);

            } else {
                Incident inc = new Incident(this.id, 1, this.lightVal, this.proxVal
                        , this.latitude, this.longitude, date, time);
                DataBaseManagerThread dbManagerThread = new DataBaseManagerThread(
                        "SAVE INCIDENT", Constants.SAVE_INCIDENT, inc);
                dbManagerThread.run();
                MqttManager.publish(
                        Constants.CONNECTED_TOPIC + id + Constants.TOPIC_DANGER,
                        Constants.MESSAGE_DANGER);
                try {
                    dbManagerThread.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dbManagerThread = new DataBaseManagerThread(
                        "SWITCH LoD", Constants.UPDATE_DANGER, this.otherId);
                dbManagerThread.start();
                MqttManager.publish(
                        Constants.CONNECTED_TOPIC + otherId + Constants.TOPIC_DANGER,
                        Constants.MESSAGE_DANGER);
            }
        } else if ((lightCheck.equals(Constants.MESSAGE_STOP_WARNING)
                || lightCheck.equals(Constants.MESSAGE_NO_WARNING))
                && (proxCheck.equals(Constants.MESSAGE_STOP_WARNING)
                || proxCheck.equals(Constants.MESSAGE_NO_WARNING))) {
            /*
             * If either check produces a stop warning signal and
             * the other has not produced a warning signal, stop
             * We pretty much always send a stop warning signal if the values
             * are OK and the android client checks if it's already playing a sound
             */
            DataBaseManager dbManager = new DataBaseManager();
            ClientAverage client = dbManager.getClientAverage(id);
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

        if (lightVal.equals("null")) {
            return Constants.MESSAGE_STOP_WARNING;
        }
        float lightVal = Float.valueOf(this.lightVal);

        DataBaseManager dbManager = new DataBaseManager();
        ClientAverage client = dbManager.getClientAverage(id);
        dbManager.closeConnection();

        if (client.getTimes() < Constants.AVERAGE_TIMES) {
            System.out.println("LESS TIMES");
            client.setTimes(client.getTimes() + 1);
            client.setLightSum(client.getLightSum() + lightVal);
            DataBaseManagerThread dbManagerThread = new DataBaseManagerThread(
                    "UPDATE CLIENT", Constants.UPDATE_CLIENT_AVERAGE, client);
            dbManagerThread.start();
            System.out.println("Retuning message no warning (not enough values");
            return Constants.MESSAGE_NO_WARNING;
        } else {
            float average;
            if (client.getLightAverage() == 0) {
                /*
                * On the 10th time a client has given us values
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
            float floor = average * lightThres / 100;
            if (lightVal > average + floor) {
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
            } else if (lightVal <= average - floor) {
                /*
                 * If, at any point, the light sensor value
                 * falls beneath average - floor, we send
                 * out the warning/danger signal
                 * (the run function checks whether it should send
                 * out a warning or danger signal)
                 */
                client.setIsRinging(true);
                DataBaseManagerThread dbManagerThread = new DataBaseManagerThread(
                        "UPDATE CLIENT", Constants.UPDATE_CLIENT_AVERAGE, client);
                dbManagerThread.start();
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
         * the client will send a message
         */
        client.setLightAverage(0f);
        client.setLightSum(0f);
        client.setTimes(0);
        client.setIsRinging(false);
        return client;
    }

    private boolean checkIncidentTime() {
        System.out.println("Checking incident time");
        /*
         * Get the last incident's time and check it against the current time
         * If both incidents occurred within the same hour, minute and second,
         * we send out a warning/danger signal (the run function checks this)
         */
        DataBaseManager dbManager = new DataBaseManager();

        /*
         * We get the current time as early as possible to avoid
         * getting
         */
        String time2 = HelpFunc.getTime();
        System.out.println("Current time is: " + time2);
        int[] currentTime = HelpFunc.timeToParts(time2);
        System.out.println("Current time is: " + currentTime[0] + ":" + currentTime[1]
                + ":" + currentTime[2]);
        /*
         * Returns the most recent incident FOR TODAY.
         * If there were no incidents today or an error occurred, it returns null
         */
        IncidentTime lastIncidentTime = dbManager.getLastIncidentTime();

        dbManager.closeConnection();

        if (lastIncidentTime == null) {
            return false;
        }

        if (this.id.equals(lastIncidentTime.getId())) {
            return false;
        }

        int[] time = lastIncidentTime.getTime();

        boolean sameHour = currentTime[0] == time[0];
        boolean sameMinute = currentTime[1] == time[1];

        /*
         * We allow a one second difference between the two incidents
         */
        boolean sameSecond = currentTime[2] == time[2] || currentTime[2] == time[2] + 1;

        /*
         * If the two incidents did happen at the same time
         * we save the current user with a level of danger of 1
         * and we update the other user's level of danger to 1
         */
        boolean sameTime = sameHour && sameMinute && sameSecond;
        if (sameTime) {
            DataBaseManagerThread dbManagerThread = new DataBaseManagerThread(
                    "UPDATE DANGER", Constants.UPDATE_DANGER, lastIncidentTime.getId());

            dbManagerThread.start();
            this.otherId = lastIncidentTime.getId();
            return true;
        } else {
            return false;
        }
    }
}