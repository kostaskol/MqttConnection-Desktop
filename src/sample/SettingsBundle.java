package sample;


public class SettingsBundle {
    private String connUrl;
    private String clientName;
    private boolean cleanSess;
    private int lightThres;
    private int proxThres;
    private int profId;
    private String profName;

    SettingsBundle(String url, String name, boolean sess, int light, int prox, int id, String profName) {
        this.connUrl = url;
        this.clientName = name;
        this.cleanSess = sess;
        this.lightThres = light;
        this.proxThres = prox;
        this.profId = id;
        this.profName = profName;
    }

    public String getConnUrl() {
        return connUrl;
    }

    public String getClientName() {
        return clientName;
    }

    public boolean getCleanSess() {
        return cleanSess;
    }

    public int getLightThres() {
        return lightThres;
    }

    public int getProxThres() {
        return proxThres;
    }

    public int getProfId() {
        return profId;
    }

    public String getProfName() {
        return profName;
    }
}
