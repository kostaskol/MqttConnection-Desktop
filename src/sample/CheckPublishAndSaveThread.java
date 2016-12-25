package sample;

public class CheckPublishAndSaveThread extends Thread {
    private String threadName;
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
        this.threadName = threadName;
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
            /*
             * If the most recent incident did not happen within the same second
             * as this one
             */
            if (!checkIncidentTime()) {
                MqttManager.publish(
                        Constants.CONNECTED_TOPIC + "/" + id + "/warning",
                        Constants.MESSAGE_WARNING);

            } else {
                MqttManager.publish(
                        Constants.CONNECTED_TOPIC + "/" + id + "/danger",
                        Constants.MESSAGE_DANGER);
                MqttManager.publish(
                        Constants.CONNECTED_TOPIC + "/" + otherId + "/danger",
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
            MqttManager.publish(
                    Constants.CONNECTED_TOPIC + "/" + id + "/stop",
                    Constants.MESSAGE_STOP_WARNING);
        }
    }

    private String checkValueLight() {
        int lightThres = bundle.getLightThres();
        float lightVal = Float.valueOf(this.lightVal);

        DataBaseManager dbManager = new DataBaseManager();
        ClientAverage client = dbManager.getClientAverage(id);
        dbManager.closeConnection();

        if (client.getTimes() < Constants.AVERAGE_TIMES) {
            client.setTimes(client.getTimes() + 1);
            client.setLightSum(client.getLightSum() + lightVal);
            DataBaseManagerThread dbManagerThread = new DataBaseManagerThread(
                    "UPDATE CLIENT", Constants.UPDATE_CLIENT_AVERAGE, client);
            dbManagerThread.start();
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
                 * FIXME: ? There is a slight problem here
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
        int proxVal = Integer.parseInt(this.proxVal);

        if (proxVal < proxThres) {
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
        return client;
    }

    private boolean checkIncidentTime() {
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
        int[] currentTime = HelpFunc.timeToParts(HelpFunc.getTime());
        /*
         * Returns the most recent incident FOR TODAY.
         * If there were no incidents today or an error occurred, it returns null
         */
        IncidentTime lastIncidentTime = dbManager.getLastIncidentTime();
        dbManager.closeConnection();
        if (lastIncidentTime == null) {
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
            SaveIncidentThread saveIncident = new SaveIncidentThread(
                    "Save Incident", id, 1, lightVal,
                    proxVal, latitude, longitude);

            saveIncident.start();

            DataBaseManagerThread dbManagerThread = new DataBaseManagerThread(
                    "UPDATE DANGER", Constants.UPDATE_DANGER, lastIncidentTime.getId());

            dbManagerThread.start();
            this.otherId = lastIncidentTime.getId();
            return true;
        } else {
            SaveIncidentThread saveIncident = new SaveIncidentThread(
                    "Save Incident", id, 0, lightVal, proxVal, latitude, longitude);
            saveIncident.start();
            return false;
        }
    }
}
