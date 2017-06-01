package BundleClasses;

/**
 * Constants bundle
 */
public class Constants {
    /*
     * Managers.DataBaseManager constants
     * Please change these to successfully connect to your
     * local database
     */
    public static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    public static final String DB_URL = "jdbc:mysql://localhost/project16";

    public static final String USER = "root";
    public static final String PASS = "Kostas6988854656";

    /*
     * Mqtt subscription message offsets
     */
    public static final int ID = 0;
    public static final int LATITUDE = 1;
    public static final int LONGITUDE = 2;
    public static final int LIGHT = 3;
    public static final int PROX = 4;

    /*
     * Level of danger constants
     */
    public static final String DANGER_LOW = "Danger Low";
    public static final String DANGER_HIGH = "Danger High";

    /*
     * Light value constants
     */
    public static final String LIGHT_HIGH = "high lighting";
    public static final String LIGHT_NORMAL = "normal lighting";
    public static final String LIGHT_DIM = "dim lighting";

    /*
     * Proximity value constants
     */
    public static final String PROX_CLOSE = "close proximity";

    /*
     * Search Filter Constants
     */
    public static final String TIME_MIN_5 = "Last 5 minutes";
    public static final String TIME_MIN_30 = "Last 30 minutes";
    public static final String TIME_HOURS_1 = "Last 1 hour";
    public static final String TIME_HOURS_12 = "Last 12 hours";
    public static final String TIME_HOURS_24 = "Last 24 hours";

    public static final String NONE = "-Choose one-";

    /*
     * Profile Constants
     */
    public static final String NEW_PROFILE = "New Profile";
    public static final String DEFAULT_PROFILE = "Default";

    /*
     * Search Results constants
     */
    public static final int ROWS_PER_PAGE = 18;


    /*
     * Mqtt Constants
     */
    public static final String LOG_TOPIC = "log";
    public static final String CONNECTION_TOPIC = "connections";
    public static final String NEW_CONNECTION_TOPIC = CONNECTION_TOPIC + "/newConnections";
    public static final String NEW_CONNECTION_TOPIC_SIMPLE = "newConnections";
    public static final String CONNECTED_TOPIC = CONNECTION_TOPIC + "/connected/";
    public static final String REQUEST_ACKNOWLEDGEMENT_TOPIC = CONNECTION_TOPIC + "/requestAck";
    public static final String REQUEST_ACKNOWLEDGEMENT_TOPIC_SIMPLE = "requestAck";
    public static final String CONNECTED_TOPIC_SIMPLE = "connected";
    public static final String CONNECTED_ACKNOWLEDGE_TOPIC = "/acknowledged";
    public static final String TOPIC_WARNING = "/warning";
    public static final String TOPIC_DANGER = "/danger";
    public static final String TOPIC_STOP_WARNING = "/stopSounds";
    public static String LAST_WILL_TOPIC = "mainClient/disconnected";
    public static String MAIN_CLIENT_DISCONNECTING = "disconnecting";


    /*
     * Warning Control
     */
    public static final String MESSAGE_WARNING = "warning";
    public static final String MESSAGE_DANGER = "danger";
    public static final String MESSAGE_NO_WARNING = "no warning";
    public static final String MESSAGE_STOP_WARNING = "stop warning";

    public static final int AVERAGE_TIMES = 3;


    /*
     * DataBaseManagerThread constants
     * (used to run a specific function from DataBaseManagerThread)
     */
    public static final String SAVE_INCIDENT = "save incident";
    public static final String UPDATE_DANGER = "update danger";
    public static final String SWITCH_PROFILE = "switch profile";
    public static final String UPDATE_PROFILE = "update profile";
    public static final String DELETE_PROFILE = "delete profile";
    public static final String SAVE_NEW_PROFILE = "save new profile";
    public static final String INSERT_CLIENT_AVERAGE = "insert client average";
    public static final String UPDATE_CLIENT_AVERAGE = "update client average";
    public static final String SWITCH_LOD = "update danger";

}
