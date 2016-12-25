package sample;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Incident {
    private StringProperty id;
    private IntegerProperty levelOfDanger;
    private StringProperty lightVal;
    private StringProperty proxVal;
    private StringProperty latitude;
    private StringProperty longitude;
    private StringProperty date;
    private StringProperty time;

    Incident(String id, int danger, String light, String prox,
             String lat, String lng, String date, String time) {
        this.id = new SimpleStringProperty(id);
        this.levelOfDanger = new SimpleIntegerProperty(danger);
        this.lightVal = new SimpleStringProperty(light);
        this.proxVal = new SimpleStringProperty(prox);
        this.latitude = new SimpleStringProperty(lat);
        this.longitude = new SimpleStringProperty(lng);
        this.date = new SimpleStringProperty(date);
        this.time = new SimpleStringProperty(time);
    }

    String getId() {
        return this.id.get();
    }

    StringProperty getIdProperty() {
        return this.id;
    }

    int getLevelOfDanger() {
        return this.levelOfDanger.get();
    }

    IntegerProperty getLevelOfDangerProperty() {
        return this.levelOfDanger;
    }

    String getLightVal() {
        return this.lightVal.get();
    }

    StringProperty getLightValProperty() {
        return this.lightVal;
    }

    String getProxVal() {
        return this.proxVal.get();
    }

    StringProperty getProxValProperty() {
        return this.proxVal;
    }

    String getLatitude() {
        return this.latitude.get();
    }

    StringProperty getLatitudeProperty() {
        return this.latitude;
    }

    String getLongitude() {
        return this.longitude.get();
    }

    StringProperty getLongitudeProperty() {
        return this.longitude;
    }

    String getDate() {
        return this.date.get();
    }

    StringProperty getDateProperty() {
        return this.date;
    }

    String getTime() {
        return this.time.get();
    }

    StringProperty getTimeProperty() {
        return this.time;
    }

    void print() {
        System.out.println("Printing Result: ");
        System.out.println("ID: " + this.id +
                "\nLevel of danger: " + this.levelOfDanger +
                "\nLight val: " + this.lightVal +
                "\nProx val: " + this.proxVal +
                "\nDate: " + this.date +
                "\nTime: " + this.time);
    }
}

class IncidentTime {
    private int[] time;
    private int[] date;
    private String id;

    IncidentTime(int[] time, int[] date, String id) {
        this.time = time;
        this.date = date;
        this.id = id;
    }

    IncidentTime(int[] time, String id) {
        this.time = time;
        this.id = id;
        this.date = new int[]{0, 0, 0};
    }

    int[] getTime() {
        return this.time;
    }

    int[] getDate() {
        return this.date;
    }

    String getId() {
        return this.id;
    }
}
