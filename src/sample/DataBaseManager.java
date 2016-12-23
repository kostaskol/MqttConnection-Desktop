package sample;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DataBaseManager {

    private static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/project16";

    private static final String USER = "root";
    private static final String PASS = "Kostas6988854656";

    Connection conn;
    Statement statement;

    DataBaseManager() {
        try {
            try {
                Class.forName(JDBC_DRIVER);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Connecting to database");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch(SQLException se) {
            se.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    void saveSettings(String url, String name, boolean cleanSess, int lightThres, int proxThres) {
        try {
            statement = conn.createStatement();
            String sql = "UPDATE settings SET connUrl = \"" + url + "\", clientName=\"" +
                    name + "\", cleanSession=\"" + String.valueOf(cleanSess) + "\", lightThreshold=\"" + lightThres +
                    "\"proxThreshold=\"" + proxThres + "\" WHERE id=1;";
            statement.execute(sql);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    double[] getThresholds() {
        try {
            statement = conn.createStatement();
            String sql = "SELECT lightThreshold, proxThreshold FROM settings WHERE id=1";
            ResultSet rs = statement.executeQuery(sql);
            //We don't really need that since we
            //only have one row returned
            double light = 0;
            double prox = 0;
            while (rs.next()) {
                light = Double.valueOf(rs.getString("lightThreshold"));
                prox = Double.valueOf(rs.getString("proxThreshold"));
            }
            rs.close();
            statement.close();
            return new double[] {light, prox};
        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    void insertEntry(String id, String danger, String lightVal, String proxVal) {
        String date = HelpFunc.getDate();
        String time = HelpFunc.getTime();
        try {
            statement = conn.createStatement();
            String sql = "INSERT INTO log VALUES (" +
                    id + ", " + danger + ", " + lightVal + ", " + proxVal + ", " + date + ", " + time;
            statement.execute(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    boolean checkIncidentTimes(String id) {
        int[] time = HelpFunc.timeToParts(HelpFunc.getTime());
        int[] date = HelpFunc.dateToParts(HelpFunc.getDate());

        try {
            statement = conn.createStatement();
            /*
             * ORDER BY date DESC will give us the most recent date first
             */
            String sql = "SELECT date, time FROM log WHERE userID=" + id + " ORDER BY date DESC;";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                int[] incidentTime = HelpFunc.timeToParts(rs.getString("time"));
                int[] incidentDate = HelpFunc.dateToParts(rs.getString("date"));

                /*
                 * Check each of the incident's and current date's values
                 * If they are all the same, the incident happened on the same day
                 * If not, we return false
                 * TODO: Double check this to see if it makes sense
                 */
                for (int i = 0; i < date.length; i++) {
                    if (incidentDate[i] != date[i]) {
                        return false;
                    }
                }

                /*
                 * If both happened on the same day, however,
                 * we check if they happened within the same minute
                 */
                boolean sameTime = true;
                for (int i = 0; i < time.length - 1; i++) {
                    if (incidentTime[i] != time[i]) {
                        sameTime = false;
                    }
                }

                if (!sameTime) {
                    continue;
                }

                /*
                 * If we've reached this point,
                 */
                if (incidentTime[2] == time[2]) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Exception on outer select");
            e.printStackTrace();
            return false;
        }
        return false;
    }

    void updateDanger(String id) {
        try {
            statement = conn.createStatement();
            String sql = "UPDATE log SET levelOfDanger=1 WHERE userID=" + id + ";";
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Exception on inner update");
            e.printStackTrace();

        }
    }

    String[] getSettings() {
        try {
            statement = conn.createStatement();
            String sql = "SELECT * FROM settings;";
            ResultSet rs = statement.executeQuery(sql);
            String url = null;
            String name = null;
            String cleanSess = null;
            String lightThres = null;
            String proxThres = null;
            while (rs.next()) {
                url = rs.getString("connUrl");
                name = rs.getString("clientName");
                cleanSess = String.valueOf(rs.getBoolean("cleanSession"));
                lightThres = String.valueOf(rs.getInt("lightThreshold"));
                proxThres = String.valueOf(rs.getInt("proxThreshold"));
            }
            return new String[] {url, name, cleanSess, lightThres, proxThres};
        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    List<SearchResult> searchDb(String id, String danger, String light, String prox, String date, String time) {
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
                if (light.equals(Constants.LIGHT_HIGH)) {
                    threshold = 2000;
                } else if (light.equals(Constants.LIGHT_NORMAL)) {
                    threshold = 1000;
                } else if (light.equals(Constants.LIGHT_DIM)) {
                    threshold = 500;
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
            System.out.println("Date != null");
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
                if (time.equals(Constants.TIME_MIN_5)) {
                    interval = 5;
                } else if (time.equals(Constants.TIME_MIN_30)) {
                    interval = 30;
                } else if (time.equals(Constants.TIME_HOURS_1)) {
                    interval = 60;
                } else if (time.equals(Constants.TIME_HOURS_12)) {
                    interval = 720;
                } else if (time.equals(Constants.TIME_HOURS_24)) {
                    interval = 1440;
                } else {
                    System.out.println("Bad value for time");
                    interval = 0;
                }
            /*
             * Get all values more recent than interval minutes
             */
                sql += " time > date_sub(now(), interval " + interval + " minute)";
            }
        }

        try {

            statement = conn.createStatement();
            System.out.println("Executing statement: " + sql);
            ResultSet rs = statement.executeQuery(sql);

            List<SearchResult> results = new ArrayList<>();

            while(rs.next()) {

                String userId = rs.getString("userID");
                int levelOfDanger = rs.getInt("levelOfDanger");
                String lightVal = rs.getString("lightValue");
                String proxVal = rs.getString("proxValue");
                java.sql.Date dt = rs.getDate("date");
                Time tm = rs.getTime("time");

                SearchResult tmpResult = new SearchResult(userId, levelOfDanger,
                        lightVal, proxVal, dt, tm);

                results.add(tmpResult);
            }

            return results;
        } catch(SQLException e) {
            e.printStackTrace();
            return null;
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
