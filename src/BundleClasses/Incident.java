package BundleClasses;


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

    public Incident(String id, int danger, String light, String prox,
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

    public String getId() {
        return this.id.get();
    }

    public StringProperty getIdProperty() {
        return this.id;
    }

    public int getLevelOfDanger() {
        return this.levelOfDanger.get();
    }

    public IntegerProperty getLevelOfDangerProperty() {
        return this.levelOfDanger;
    }

    public String getLightVal() {
        return this.lightVal.get();
    }

    public StringProperty getLightValProperty() {
        return this.lightVal;
    }

    public String getProxVal() {
        return this.proxVal.get();
    }

    public StringProperty getProxValProperty() {
        return this.proxVal;
    }

    public String getLatitude() {
        return this.latitude.get();
    }

    public StringProperty getLatitudeProperty() {
        return this.latitude;
    }

    public String getLongitude() {
        return this.longitude.get();
    }

    public StringProperty getLongitudeProperty() {
        return this.longitude;
    }

    public String getDate() {
        return this.date.get();
    }

    public StringProperty getDateProperty() {
        return this.date;
    }

    public String getTime() {
        return this.time.get();
    }

    public StringProperty getTimeProperty() {
        return this.time;
    }

}

