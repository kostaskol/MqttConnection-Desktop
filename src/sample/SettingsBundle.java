package sample;


public class SettingsBundle {
    private String connUrl;
    private String port;
    private String clientName;
    private boolean cleanSess;
    private int lightThres;
    private int proxThres;
    private int profId;
    private String profName;

    SettingsBundle(String url, String port, String name, boolean sess, int light, int prox, int id, String profName) {
        this.connUrl = url;
        this.port = port;
        this.clientName = name;
        this.cleanSess = sess;
        this.lightThres = light;
        this.proxThres = prox;
        this.profId = id;
        this.profName = profName;
    }

    String getConnUrl() {
        return connUrl;
    }

    String getPort() {
        return port;
    }

    String getClientName() {
        return clientName;
    }

    boolean getCleanSess() {
        return cleanSess;
    }

    int getLightThres() {
        return lightThres;
    }

    int getProxThres() {
        return proxThres;
    }

    int getProfId() {
        return profId;
    }

    String getProfName() {
        return profName;
    }

    void print() {
        System.out.println("BUNDLE:\n" +
                "conn url: " + connUrl +
                "\nport: " + port +
                "\nname: " + clientName
        );
    }
}
