package sample;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class DataBaseManager {

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/project16";

    private static final String USER = "root";
    private static final String PASS = "project2016";

    Connection conn;
    Statement statement;

    DataBaseManager() {
        try {
            Class.forName(JDBC_DRIVER);

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
                for (int i = 0; i < time.length - 1; i++) {
                    if (incidentTime[i] != time[i]) {
                        return false;
                    }
                }

                /*
                 * If we've reached this point,
                 */
                return incidentTime[2] == time[2];
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
