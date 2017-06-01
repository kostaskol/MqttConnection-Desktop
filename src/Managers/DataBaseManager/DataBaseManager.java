package Managers.DataBaseManager;

import BundleClasses.*;
import Managers.MqttManager.MqttManager;
import Utilities.DateAndTimeUtility;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager class the manages *all* of the DataBase's operations
 */
public class DataBaseManager {


    private Connection conn;
    private Statement statement;

    public DataBaseManager() {
        try {
            try {
                Class.forName(Constants.JDBC_DRIVER);
            } catch (ClassNotFoundException cnfe) {
                MqttManager.publish(Constants.LOG_TOPIC, "ERROR: DBMANAGER | Class not found");
                cnfe.printStackTrace();
            }
            conn = DriverManager.getConnection(Constants.DB_URL, Constants.USER, Constants.PASS);
        } catch(SQLException se) {
            if (se.getErrorCode() != 1007) {
            }
            // This is normal. It means that the database has
            // already been created.
        }

    }

    /*
     * Save a new incident
     */
    void saveIncident(Incident inc) {
        String sql = "INSERT INTO log " +
                "VALUES (" +
                inc.getLevelOfDanger() + ", " +
                "\"" + inc.getLightVal() + "\", " +
                "\"" + inc.getProxVal() + "\", " +
                "\"" + inc.getDate() + "\", " +
                "\"" + inc.getTime() + "\", " +
                "\"" + inc.getLatitude() + "\", " +
                "\"" + inc.getLongitude() + "\", " +
                "\"" + inc.getId() + "\")";
        executeStatement(sql);

    }

    /*
     * This returns the last incident's time FOR TODAY
     * (the array's last element is the ID of the last incident.
     * This is useful for updating to danger later on)
     */
    public IncidentTime getLastIncidentTime() {
        String today = DateAndTimeUtility.getDate();
        int[] todayParts = DateAndTimeUtility.dateToParts(today);
        /*
         * This will give us the most recent incident
         */
        String sql = "SELECT id, date, time FROM log ORDER BY date DESC, time DESC LIMIT 1";

        ResultSet rs = executeQuery(sql);

        assert rs != null;

        try {
            IncidentTime tmp = null;
            if (rs.next()) {
                /*
                 * If the most recent incident did not happen today, we return null
                 */
                String date = rs.getString("date");

                int[] dateParts = DateAndTimeUtility.dateToParts(date);
                for (int i = 0; i < 3; i++) {
                    if (dateParts[i] != todayParts[i]) {
                        System.out.println(dateParts[i] + " not " + todayParts[i]);
                        return null;
                    }
                }
                /*
                 * We return the most recent incident and the involved user's id
                 */
                tmp = new IncidentTime(DateAndTimeUtility.timeToParts(rs.getString("time")),
                        rs.getString("id"));
            }

            rs.close();
            return tmp;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    /*
     * Change the danger level of the specific ID to 1 (DANGER_HIGH)
     */
    void updateDanger(IncidentTime inc) {
        int incDate[] = inc.getDate();
        String date = incDate[0] + "-" + incDate[1] + "-" + incDate[2];
        int incTime[] = inc.getTime();
        String time = incTime[0] + ":" + incTime[1] + ":" + incTime[2];
        String sql = "UPDATE log SET levelOfDanger=1 WHERE id=\"" + inc.getId() + "\"" +
                "AND time=\"" + date + "\"" +
                "AND date=\"" + time + "\"";
        executeStatement(sql);
    }

    /*
     * Checks the provided filters and
     * returns the query's results
     */
    public List<Incident> searchDb(String id, String danger, String light, String prox, String date, String time) {

        /*
         * We check each of the variables and add them to the
         * query if the user has supplied a value
         */
        String sql = "SELECT * FROM log ";

        int numOfVars = 0;
        if (id != null) {
            if (!id.equals("")) {
                sql += " WHERE ";
                sql += " id LIKE \"%" + id + "%\"";
                numOfVars++;
            }
        }

        if (danger != null) {
            if (!danger.equals(Constants.NONE)) {
                if (numOfVars != 0) {
                    sql += " AND ";
                } else {
                    sql += " WHERE ";
                }

                if (danger.equals(Constants.DANGER_LOW)) {
                    sql += " levelOfDanger=0 ";
                } else if (danger.equals(Constants.DANGER_HIGH)) {
                    sql += " levelOfDanger=1 ";
                }
                numOfVars++;
            }
        }

        if (light != null) {
            if (!light.equals(Constants.NONE)) {
                if (numOfVars != 0) {
                    sql += " AND ";
                } else {
                    sql += " WHERE ";
                }
                int threshold = 0;
                switch (light) {
                    case Constants.LIGHT_HIGH:
                        threshold = 2000;
                        break;
                    case Constants.LIGHT_NORMAL:
                        threshold = 1000;
                        break;
                    case Constants.LIGHT_DIM:
                        threshold = 500;
                        break;
                }
                sql += " lightValue > " + threshold;
                numOfVars++;
            }
        }

        if (prox != null) {
            if (!prox.equals(Constants.NONE)) {
                if (numOfVars != 0) {
                    sql += " AND ";
                } else {
                    sql += " WHERE ";
                }
                int threshold = 0;
                if (!prox.equals(Constants.PROX_CLOSE)) {
                    threshold = 5;
                }
                sql += " proxValue >= " + threshold;
                numOfVars++;
            }
        }

        if (date != null) {
            if (numOfVars != 0) {
                sql += " AND ";
            } else {
                sql += " WHERE ";
            }
            sql += " date = \"" + date + "\"";
            numOfVars++;
        }

        if (time != null) {
            if (!time.equals(Constants.NONE)) {
                if (numOfVars != 0) {
                    sql += " AND ";
                } else {
                    sql += " WHERE ";
                }

                int interval;
                switch (time) {
                    case Constants.TIME_MIN_5:
                        interval = 5;
                        break;
                    case Constants.TIME_MIN_30:
                        interval = 30;
                        break;
                    case Constants.TIME_HOURS_1:
                        interval = 60;
                        break;
                    case Constants.TIME_HOURS_12:
                        interval = 720;
                        break;
                    case Constants.TIME_HOURS_24:
                        interval = 1440;
                        break;
                    default:
                        MqttManager.publish(Constants.LOG_TOPIC,
                                "WARNING: DBMANAGER | QUERY | Bad value for time");
                        interval = 0;
                        break;
                }

                /*
                 * Get all values more recent than interval minutes (for today)
                 */
                String currDate = DateAndTimeUtility.getDate();
                sql += " time > date_sub(now(), interval " + interval + " minute) AND date=\"" + currDate + "\"";
            }
        }
        ResultSet rs = executeQuery(sql);

        if (rs == null) {
            return null;
        }

        List<Incident> results = new ArrayList<>();
        try {
            while(rs.next()) {

                String userId = rs.getString("id");
                int levelOfDanger = rs.getInt("levelOfDanger");
                String lightVal = rs.getString("lightValue");
                String proxVal = rs.getString("proxValue");
                String dt = rs.getString("date");
                String tm = rs.getString("time");
                String lat = rs.getString("latitude");
                String lng = rs.getString("longitude");

                Incident tmpResult = new Incident(userId, levelOfDanger,
                        lightVal, proxVal, lat, lng, dt, tm);

                results.add(tmpResult);
            }
            rs.close();
            return results;
        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * Returns the id of the currently selected profile
     */
    public int getSelectedProfile() {
        String sql = "SELECT currId FROM settingsProfile";

        ResultSet rs = executeQuery(sql);

        assert rs != null;
        /*
         * We only get one row so this is redundant
         */
        int currId = -1;
        try {
            while (rs.next()) {
                currId = rs.getInt("currId");
            }
            rs.close();
            return currId;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int getMaxProfileId() {
        String sql = "SELECT profileID FROM settings ORDER BY profileID DESC";
        ResultSet rs = executeQuery(sql);

        assert rs != null;

        try {
            int maxId = -1;
            if (rs.next()) {
                maxId = rs.getInt("profileID");
            }
            return maxId;
        } catch (SQLException oe) {
            oe.printStackTrace();
            try {
                rs.close();
            } catch (SQLException ie) {
                ie.printStackTrace();
            }
            return -1;
        }
    }

    /*
     * Used when we want to change the selected profile to another one
     */
    public void switchProfile(int newId) {
        String sql = "UPDATE settingsProfile SET currId=" + newId;
        executeStatement(sql);
    }

    /*
     * Update profile
     */
    void updateProfile(SettingsBundle bundle) {
        String sql = "UPDATE settings SET " +
                "connUrl=\"" + bundle.getConnUrl() +
                "\", clientName=\"" + bundle.getClientName() +
                "\", cleanSession=" + bundle.getCleanSess() +
                ", lightThreshold=" + bundle.getLightThres() +
                ", proxThreshold=" + bundle.getProxThres() +
                ", profileName=\"" + bundle.getProfName() +
                "\", port=\"" + bundle.getPort() +
                "\", frequency=" + bundle.getFrequency() +
                " WHERE profileID=" + bundle.getProfId();
        System.out.println("Executing query " + sql);
        executeStatement(sql);
        switchProfile(bundle.getProfId());
    }

    public void deleteProfile(Profile prof) {
        String sql = "DELETE FROM settings WHERE profileID=" + prof.getProfileId();
        executeStatement(sql);
        switchProfile(0);
    }

    /*
     * Create new profile
     */
    void saveNewProfile(SettingsBundle bundle) {
        String sql = "INSERT INTO settings VALUES (" +
                "\"" + bundle.getConnUrl() + "\", " +
                "\"" + bundle.getClientName() + "\", " +
                bundle.getCleanSess() + ", " +
                bundle.getLightThres() + ", " +
                bundle.getProxThres() + ", " +
                "\"" + bundle.getProfName() + "\", " +
                "\"" + bundle.getPort() + "\", " +
                bundle.getProfId() + ", " +
                bundle.getFrequency() + ")";
        executeStatement(sql);
        switchProfile(bundle.getProfId());
    }

    public SettingsBundle getProfile(int selectedProfile) {
        String sql = "SELECT * FROM settings WHERE profileID=" + selectedProfile;
        ResultSet rs = executeQuery(sql);
        assert rs != null;
        try {
            SettingsBundle settings = null;
            while (rs.next()) {
                String connUrl = rs.getString("connUrl");
                String port = rs.getString("port");
                String clientName = rs.getString("clientName");
                boolean cleanSess = rs.getBoolean("cleanSession");
                int lightThres = rs.getInt("lightThreshold");
                int proxThres = rs.getInt("proxThreshold");
                String profileName = rs.getString("profileName");
                int freq = rs.getInt("frequency");
                settings = new SettingsBundle(
                        connUrl, port, clientName, cleanSess, lightThres, proxThres, selectedProfile, profileName, freq);
            }
            rs.close();
            return settings;
        } catch (SQLException oe) {
            oe.printStackTrace();
            try {
                rs.close();
            } catch (SQLException ie) {
                ie.printStackTrace();
            }
            return null;
        }
    }

    public List<SettingsBundle> getAllProfiles() {
        String sql = "SELECT * FROM settings";
        try {
            statement = conn.createStatement();
            ResultSet rs = executeQuery(sql);

            assert rs != null;

            List<SettingsBundle> bundle = new ArrayList<>();

            while (rs.next()) {
                String connUrl = rs.getString("connUrl");
                String port = rs.getString("port");
                String clientName = rs.getString("clientName");
                boolean cleanSess = rs.getInt("cleanSession") == 1;
                int lightThres = rs.getInt("lightThreshold");
                int proxThres = rs.getInt("proxThreshold");
                int id = rs.getInt("profileID");
                String profileName = rs.getString("profileName");
                int freq = rs.getInt("frequency");

                SettingsBundle tmpBundle = new SettingsBundle(
                        connUrl, port, clientName, cleanSess, lightThres, proxThres, id, profileName, freq);

                bundle.add(tmpBundle);
            }
            rs.close();
            return bundle;
        } catch (SQLException oe) {
            oe.printStackTrace();
            try {
                statement.close();
            } catch (SQLException ie) {
                ie.printStackTrace();
            }
            return null;
        }
    }

    /*
     * Returns a client's average light value
     * or creates a new one if none exists
     */
    public ClientAverage getClientAverage(String clientId) {
        /*
         * Get client's average lighting
         */
        String sql = "SELECT * FROM clientAverages WHERE id=\"" + clientId + "\"";
        ResultSet rs = executeQuery(sql);
        if (rs == null) {
            return null;
        }
        try {
            ClientAverage client = null;
            if (rs.next()) {
                String id = rs.getString("id");
                float lightAvg = rs.getFloat("lightAvg");
                float lightSum = rs.getFloat("lightSum");
                int times = rs.getInt("times");
                int ringing = rs.getInt("currentlyRinging");
                client = new ClientAverage(id, lightAvg, lightSum, times, ringing);
            }

            if (client != null) {
                /*
                 * If client already exists in table
                 */
                return client;
            } else {
                /*
                 * New client (no records exist)
                 */
                client = new ClientAverage(clientId, 0f, 0f, 0, 0);
                insertClientAverage(client);
                return client;
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    void insertClientAverage(ClientAverage client) {
        String id = client.getClientId();
        float avg = client.getLightAverage();
        float sum = client.getLightSum();
        int times = client.getTimes();
        int ring = client.isRinging() ? 1 : 0;
        String sql = "INSERT INTO clientAverages " +
                "VALUES (" +
                "\"" + id + "\", " +
                avg + ", " +
                times + ", " +
                sum + ", " +
                ring + ")";
        executeStatement(sql);
    }

    void updateClientAverage(ClientAverage client) {
        String id = client.getClientId();
        float avg = client.getLightAverage();
        float sum = client.getLightSum();
        int times = client.getTimes();
        boolean isRinging = client.isRinging();
        int ringing = isRinging ? 1 : 0;
        String sql = "UPDATE clientAverages " +
                "SET " +
                "lightAvg=" + avg + ", " +
                "lightSum=" + sum + ", " +
                "times=" + times + ", " +
                "currentlyRinging=" + ringing +
                " WHERE id=\"" + id + "\"";
        executeStatement(sql);
    }

    public void clearClients() {
        String sql = "DELETE FROM clientAverages";
        executeStatement(sql);
    }

    /*
     * Help function to execute a statement
     */
    private void executeStatement(String sql) {
        try {
            statement = conn.createStatement();
            statement.execute(sql);

            statement.close();
        } catch (SQLException oe) {
            try {
                statement.close();
            } catch (SQLException ie) {
                ie.printStackTrace();
            }
        }
    }

    /*
     * Help function to execute a query
     */
    private ResultSet executeQuery(String sql) {
        try {
            if (conn == null) {
                return null;
            }
            statement = conn.createStatement();
            return statement.executeQuery(sql);

        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            try {
                statement.close();
            } catch (SQLException ei) {
                ei.printStackTrace();
            }
            return null;
        }
    }

    public void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
            if (statement != null) {
                statement.close();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
