package Managers.DataBaseManager;

import BundleClasses.*;

/**
 * Manager class (thread) that manages *all* of the fire and forget
 * operations of the Managers.DataBaseManager class.
 * Functions that use select statements could have also been included
 * but since we need to wait for a result, it would nullify the usefulness of a thread
 */
public class DataBaseManagerThread extends Thread {

    private DataBaseManager dbManager;
    private String operation;
    private Incident inc;
    private int newId;
    private SettingsBundle bundle;
    private Profile prof;
    private ClientAverage client;
    private IncidentTime lastIncidentTime;

    public DataBaseManagerThread() {
    }

    /*
     * When creating an instance of this class,
     * an operation (available operations are defined
     * in BundleClasses$Constants) and the required parameter
     * of the said operation must be supplied
     *
     * i.e.
     * DataBaseManagerThread updateManager = new DataBaseManagerThread(
     *      "SAVE INCIDENT THREAD", Constants.SAVE_INCIDENT, incident);
     *
     * Note: The class doesn't support user error handling since it
     * will only be used by us
     */
    public DataBaseManagerThread(String threadName, String op, Incident inc) {
        super(threadName);
        this.dbManager = new DataBaseManager();
        this.operation = op;
        this.inc = inc;
    }

    public DataBaseManagerThread(String threadName, String op, IncidentTime lastIncidentTime) {
        super(threadName);
        this.dbManager = new DataBaseManager();
        this.operation = op;
        this.lastIncidentTime = lastIncidentTime;
    }

    public DataBaseManagerThread(String threadName, String op, int newId) {
        super(threadName);
        this.dbManager = new DataBaseManager();
        this.operation = op;
        this.newId = newId;
    }

    public DataBaseManagerThread(String threadName, String op, SettingsBundle bundle) {
        super(threadName);
        this.dbManager = new DataBaseManager();
        this.operation = op;
        this.bundle = bundle;
    }

    public DataBaseManagerThread(String threadName, String op, Profile prof) {
        super(threadName);
        this.dbManager = new DataBaseManager();
        this.operation = op;
        this.prof = prof;
    }

    public DataBaseManagerThread(String threadName, String op, ClientAverage client) {
        super(threadName);
        this.dbManager = new DataBaseManager();
        this.operation = op;
        this.client = client;
    }

    /*
     * Calls the necessary function according to the operation name
     */
    @Override
    public void run() {
        switch (this.operation) {
            case Constants.SAVE_INCIDENT:
                saveIncident(this.inc);
                break;
            case Constants.UPDATE_DANGER:
                updateDanger(this.lastIncidentTime);
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
                // I shouldn't be in here
        }
        closeConnection();
    }

    private void saveIncident(Incident inc) {
        dbManager.saveIncident(inc);
    }


    private void updateDanger(IncidentTime incTime) {
        dbManager.updateDanger(incTime);
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
