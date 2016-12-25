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
    static final String PROX_FAR = "far proximity";

    /*
     * Time constants
     */
    static final String TIME_MIN_5 = "Last 5 minutes";
    static final String TIME_MIN_30 = "Last 30 minutes";
    static final String TIME_HOURS_1 = "Last 1 hour";
    static final String TIME_HOURS_12 = "Last 12 hours";
    static final String TIME_HOURS_24 = "Last 24 hours";

    static final String NONE = "-Choose one-";


    static final String NEW_PROFILE = "New Profile";
    static final String DEFAULT_PROFILE = "Default";

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
    static final String WARNING = "warning";
    static final String NO_WARNING = "no warning";
    static final String STOP_WARNING = "stop warning";

}
