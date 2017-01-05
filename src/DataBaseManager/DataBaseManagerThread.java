package DataBaseManager;

import BundleClasses.*;

public class DataBaseManagerThread extends Thread {
    /*
     * This thread will manage only the fire and forget operations of the data base
     * Functions that use select statements could have also been included
     * but since we need to wait for a result, it would nullify the thread usefulness
     */

    private DataBaseManager dbManager;
    private String threadName;
    private String operation;
    private Incident inc;
    private String id;
    private int newId;
    private SettingsBundle bundle;
    private Profile prof;
    private ClientAverage client;

    public DataBaseManagerThread() {
    }

    public DataBaseManagerThread(String threadName, String op, Incident inc) {
        super(threadName);
        this.threadName = threadName;
        this.dbManager = new DataBaseManager();
        this.operation = op;
        this.inc = inc;
    }

    public DataBaseManagerThread(String threadName, String op, String id) {
        super(threadName);
        this.threadName = threadName;
        this.dbManager = new DataBaseManager();
        this.operation = op;
        this.id = id;
    }

    public DataBaseManagerThread(String threadName, String op, int newId) {
        super(threadName);
        this.threadName = threadName;
        this.dbManager = new DataBaseManager();
        this.operation = op;
        this.newId = newId;
    }

    public DataBaseManagerThread(String threadName, String op, SettingsBundle bundle) {
        super(threadName);
        this.threadName = threadName;
        this.dbManager = new DataBaseManager();
        this.operation = op;
        this.bundle = bundle;
    }

    public DataBaseManagerThread(String threadName, String op, Profile prof) {
        super(threadName);
        this.threadName = threadName;
        this.dbManager = new DataBaseManager();
        this.operation = op;
        this.prof = prof;
    }

    public DataBaseManagerThread(String threadName, String op, ClientAverage client) {
        super(threadName);
        this.threadName = threadName;
        this.dbManager = new DataBaseManager();
        this.operation = op;
        this.client = client;
    }

    @Override
    public void run() {
        switch (this.operation) {
            case Constants.SAVE_INCIDENT:
                saveIncident(this.inc);
                break;
            case Constants.UPDATE_DANGER:
                updateDanger(this.id);
                break;
            case Constants.SWITCH_PROFILE:
                switchProfile(this.newId);
                break;
            case Constants.UPDATE_PROFILE:
                updateProfile(this.bundle);
                break;
            case Constants.DELETE_PROFILE:
                deleteProfile(this.prof);
                break;
            case Constants.SAVE_NEW_PROFILE:
                saveNewProfile(this.bundle);
                break;
            case Constants.INSERT_CLIENT_AVERAGE:
                insertClientAverage(this.client);
                break;
            case Constants.UPDATE_CLIENT_AVERAGE:
                updateClientAverage(this.client);
                break;
            default:
                /*
                 * I shouldn't be in here
                 */
        }
        closeConnection();
    }

    private void saveIncident(Incident inc) {
        dbManager.saveIncident(inc);
    }

    private void updateDanger(String id) {
        dbManager.updateDanger(id);
    }

    private void switchProfile(int newId) {
        dbManager.switchProfile(newId);
    }

    private void updateProfile(SettingsBundle bundle) {
        dbManager.updateProfile(bundle);
    }

    private void deleteProfile(Profile prof) {
        dbManager.deleteProfile(prof);
    }

    private void saveNewProfile(SettingsBundle bundle) {
        dbManager.saveNewProfile(bundle);
    }

    private void insertClientAverage(ClientAverage client) {
        dbManager.insertClientAverage(client);
    }

    private void updateClientAverage(ClientAverage client) {
        dbManager.updateClientAverage(client);
    }

    private void closeConnection() {
        dbManager.closeConnection();
    }


}
