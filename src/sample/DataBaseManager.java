package sample;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class DataBaseManager {


    private Connection conn;
    private Statement statement;

    DataBaseManager() {
        try {
            try {
                Class.forName(Constants.JDBC_DRIVER);
            } catch (Exception e) {
                e.printStackTrace();
            }
            conn = DriverManager.getConnection(Constants.DB_URL, Constants.USER, Constants.PASS);
        } catch(SQLException se) {
            se.printStackTrace();
        }
    }

    /*
     * Save a new incident
     */
    void saveIncident(Incident inc) {
        String sql = "INSERT INTO log " +
                "VALUES " +
                "(\"" +
                inc.getId() + "\", " +
                inc.getLevelOfDanger() + ", \"" +
                inc.getLightVal() + "\", \"" +
                inc.getProxVal() + "\", \"" +
                inc.getDate() + "\", \"" +
                inc.getTime() + "\",\"" +
                inc.getLatitude() + "\", \"" +
                inc.getLongitude() + "\")";

        executeStatement(sql);

    }

    /*
     * This returns the last incident's time FOR TODAY
     * (the array's last element is the ID of the last incident.
     * This is useful for updating to danger later on)
     */
    IncidentTime getLastIncidentTime() {
        String today = HelpFunc.getDate();
        /*
         * This will give us the most recent incident
         */
        String sql = "SELECT userID, date, time FROM log ORDER BY date DESC, time DESC LIMIT 1";

        ResultSet rs = executeQuery(sql);

        assert rs != null;

        try {
            IncidentTime tmp = null;
            if (rs.next()) {
                /*
                 * If the most recent incident did not happen today, we return null
                 */
                if (!today.equals(rs.getString("date"))) {
                    return null;
                }
                /*
                 * We return the most recent incident and the involved user's id
                 */
                tmp = new IncidentTime(HelpFunc.timeToParts(rs.getString("time")),
                        rs.getString("userID"));
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
    void updateDanger(String id) {
        String sql = "UPDATE log SET levelOfDanger=1 WHERE userID=\"" + id + "\";";
        executeStatement(sql);
    }

    /*
     * Checks the provided filters and
     * returns the query's results
     */
    List<Incident> searchDb(String id, String danger, String light, String prox, String date, String time) {
        /*
         * We check each of the variables and add them to the
         * query if necessary
         */
        String sql = "SELECT * FROM log ";

        int numOfVars = 0;
        if (id != null) {
            if (!id.equals("")) {
                sql += " WHERE ";
                sql += " userID=" + id;
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
                sql += " lightThreshold > " + threshold;
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
                sql += " proxThreshold > " + threshold;
                numOfVars++;
            }
        }

        if (date != null) {
            if (numOfVars != 0) {
                sql += " AND ";
            } else {
                sql += " WHERE ";
            }
            sql += " date = " + date;
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
                        System.out.println("Bad value for time");
                        interval = 0;
                        break;
                }

                /*
                 * Get all values more recent than interval minutes (for today)
                 */
                String currDate = HelpFunc.getDate();
                sql += " time > date_sub(now(), interval " + interval + " minute) AND date=\"" + currDate + "\"";
            }
        }
        ResultSet rs = executeQuery(sql);

        assert rs != null;

        List<Incident> results = new ArrayList<>();
        try {
            while(rs.next()) {

                String userId = rs.getString("userID");
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
    int getSelectedProfile() {
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

    /*
     * Used when we want to change the current profile to another specific one
     */
    private void changeProfile(int newId, int oldId) {
        String sql = "UPDATE settingsProfile SET currId=" + newId + " WHERE currId=" + oldId;
        executeStatement(sql);
    }

    /*
     * Used when we want to change the selected profile to another one
     */
    void switchProfile(int newId) {
        int oldId = getSelectedProfile();
        String sql = "UPDATE settingsProfile SET currId=" + newId + " WHERE currId=" + oldId;
        executeStatement(sql);
    }

    /*
     * Update profile
     */
    void updateProfile(SettingsBundle bundle) {
        String sql = "UPDATE settings SET connUrl=\"" + bundle.getConnUrl() +
                "\", clientName=\"" + bundle.getClientName() +
                "\", cleanSession=" + bundle.getCleanSess() +
                ", lightThreshold=" + bundle.getLightThres() +
                ", proxThreshold=" + bundle.getProxThres() +
                ", profileName=\"" + bundle.getProfName() +
                "\", port=\"" + bundle.getPort() +
                "\" WHERE id=" + bundle.getProfId();
        System.out.println("Executing statement: " + sql);
        executeStatement(sql);
        switchProfile(bundle.getProfId());
    }

    void deleteProfile(SettingsProfile prof) {
        String sql = "DELETE FROM settings WHERE id=" + prof.getId();
        executeStatement(sql);
        changeProfile(0, prof.getId());
    }

    /*
     * Create new profile
     */
    void saveNewProfile(SettingsBundle bundle) {
        String sql = "INSERT INTO settings VALUES (\"" +
                bundle.getConnUrl() + "\", \"" +
                bundle.getClientName() + "\", " +
                bundle.getCleanSess() + ", " +
                bundle.getLightThres() + ", " +
                bundle.getProxThres() + ", " +
                bundle.getProfId() + ", \"" +
                bundle.getProfName() + "\", \"" +
                bundle.getPort() + "\")";
        executeStatement(sql);
        changeProfile(bundle.getProfId(), getSelectedProfile());
    }

    SettingsBundle getProfile(int selectedProfile) {
        String sql = "SELECT * FROM settings WHERE id=" + selectedProfile;
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
                settings = new SettingsBundle(
                        connUrl, port, clientName, cleanSess, lightThres, proxThres, selectedProfile, profileName
                );
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

    List<SettingsBundle> getAllProfiles() {
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
                int id = rs.getInt("id");
                String profileName = rs.getString("profileName");

                SettingsBundle tmpBundle = new SettingsBundle(
                        connUrl, port, clientName, cleanSess, lightThres, proxThres, id, profileName
                );

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
    ClientAverage getClientAverage(String clientId) {
        /*
         * Get client's average lighting
         */
        String sql = "SELECT * FROM clientAverages WHERE id=\"" + clientId + "\"";
        ResultSet rs = executeQuery(sql);
        assert rs != null;
        try {
            ClientAverage client = null;
            if (rs.next()) {
                String id = rs.getString("id");
                float lightAvg = rs.getFloat("lightAvg");
                float lightSum = rs.getFloat("lightSum");
                int times = rs.getInt("times");
                client = new ClientAverage(id, lightAvg, lightSum, times);
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
                client = new ClientAverage(clientId, 0f, 0f, 0);
                insertClientAverage(client);
                return client;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    void insertClientAverage(ClientAverage client) {
        String id = client.getClientId();
        float avg = client.getLightAverage();
        float sum = client.getLightSum();
        int times = client.getTimes();
        String sql = "INSERT INTO clientAverages " +
                "VALUES (\"" +
                id + "\", " +
                avg + ", " +
                sum + ", " +
                times + ");";

        executeStatement(sql);
    }

    void updateClientAverage(ClientAverage client) {
        String id = client.getClientId();
        float avg = client.getLightAverage();
        float sum = client.getLightSum();
        int times = client.getTimes();
        String sql = "UPDATE clientAverages " +
                "SET " +
                "lightAvg=" + avg + ", " +
                "lightSum=" + sum + ", " +
                "times=" + times +
                " WHERE id=\"" + id + "\"";
        System.out.println("Executing query: " + sql);
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
            oe.printStackTrace();
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
            statement = conn.createStatement();
            return statement.executeQuery(sql);

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                statement.close();
            } catch (SQLException ei) {
                ei.printStackTrace();
            }
            return null;
        }
    }

    int getMaxProfileId() {
        String sql = "SELECT id FROM settings ORDER BY id DESC";
        ResultSet rs = executeQuery(sql);

        assert rs != null;

        try {
            int maxId = -1;
            if (rs.next()) {
                maxId = rs.getInt("id");
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

    void closeConnection() {
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
