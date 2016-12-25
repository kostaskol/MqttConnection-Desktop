package sample;


public class SaveIncidentThread extends Thread {

    private Incident incident;
    private String threadName;

    public SaveIncidentThread() {
    }

    public SaveIncidentThread(String threadName, String id, int LoD, String light,
                              String prox, String lat, String lng) {
        super(threadName);
        this.threadName = threadName;

        String date = HelpFunc.getDate();
        String time = HelpFunc.getTime();
        this.incident = new Incident(id, LoD, light, prox, lat, lng, date, time);
    }

    @Override
    public void run() {
        DataBaseManager dbManager = new DataBaseManager();
        dbManager.saveIncident(incident);
    }

}