package sample;


class Constants {
    /*
     * Mqtt subscription message offsets
     */
    static final int ID = 0;
    static final int LATITUDE = 1;
    static final int LONGITUDE = 2;
    static final int LIGHT = 3;
    static final int PROX = 4;

    /*
     * Level of danger constants
     */
    static final String DANGER_LOW = "Danger Low";
    static final String DANGER_HIGH = "Danger High";

    /*
     * Light value constants
     */
    static final String LIGHT_HIGH = "high lighting";
    static final String LIGHT_NORMAL = "normal lighting";
    static final String LIGHT_DIM = "dim lighting";

    /*
     * Proximity value constants
     */
    static final String PROX_CLOSE = "close proximity";

    /*
     * Search Filter Constants
     */
    static final String TIME_MIN_5 = "Last 5 minutes";
    static final String TIME_MIN_30 = "Last 30 minutes";
    static final String TIME_HOURS_1 = "Last 1 hour";
    static final String TIME_HOURS_12 = "Last 12 hours";
    static final String TIME_HOURS_24 = "Last 24 hours";

    static final String NONE = "-Choose one-";

    /*
     * Profile Constants
     */
    static final String NEW_PROFILE = "New Profile";
    static final String DEFAULT_PROFILE = "Default";

    /*
     * Search Results constants
     */
    static final int ROWS_PER_PAGE = 18;


    /*
     * Mqtt Constants
     */
    static final String LOG_TOPIC = "log";
    static final String CONNECTION_TOPIC = "connections";
    static final String NEW_CONNECTION_TOPIC = CONNECTION_TOPIC + "/newConnections";
    static final String NEW_CONNECTION_TOPIC_SIMPLE = "newConnections";
    static final String CONNECTED_TOPIC = CONNECTION_TOPIC + "/connected";
    static final String CONNECTED_TOPIC_SIMPLE = "connected";


    /*
     * Warning Control
     */
    static final String MESSAGE_WARNING = "warning";
    static final String MESSAGE_DANGER = "danger";
    static final String MESSAGE_NO_WARNING = "no warning";
    static final String MESSAGE_STOP_WARNING = "stop warning";

    static final int AVERAGE_TIMES = 10;


    /*
     * DataBaseManagerThread constants
     * (used to run a specific function)
     */
    static final String SAVE_INCIDENT = "save incident";
    static final String UPDATE_DANGER = "update danger";
    static final String SWITCH_PROFILE = "switch profile";
    static final String UPDATE_PROFILE = "update profile";
    static final String DELETE_PROFILE = "delete profile";
    static final String SAVE_NEW_PROFILE = "save new profile";
    static final String INSERT_CLIENT_AVERAGE = "insert client average";
    static final String UPDATE_CLIENT_AVERAGE = "update client average";


    /*
     * DataBaseManager constants
     */
    static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/project16";

    static final String USER = "root";
    static final String PASS = "Kostas6988854656";

}
