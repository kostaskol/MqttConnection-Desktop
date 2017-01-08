package BundleClasses;

/**
 * Bundle class to store query results for table settings
 */
public class SettingsBundle {
    private String connUrl;
    private String port;
    private String clientName;
    private boolean cleanSess;
    private int lightThres;
    private int proxThres;
    private int profId;
    private int checkFrequency;
    private String profName;

    public SettingsBundle(String url, String port, String name, boolean sess, int light, int prox, int id, String profName, int frequency) {
        this.connUrl = url;
        this.port = port;
        this.clientName = name;
        this.cleanSess = sess;
        this.lightThres = light;
        this.proxThres = prox;
        this.profId = id;
        this.profName = profName;
        this.checkFrequency = frequency;
    }

    public String getConnUrl() {
        return this.connUrl;
    }

    public String getPort() {
        return this.port;
    }

    public String getClientName() {
        return this.clientName;
    }

    public boolean getCleanSess() {
        return this.cleanSess;
    }

    public int getLightThres() {
        return this.lightThres;
    }

    public int getProxThres() {
        return this.proxThres;
    }

    public int getProfId() {
        return this.profId;
    }

    public String getProfName() {
        return this.profName;
    }

    public int getFrequency() {
        return this.checkFrequency;
    }
}
