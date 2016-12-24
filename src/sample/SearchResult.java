package sample;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SearchResult {
    private StringProperty id;
    private IntegerProperty levelOfDanger;
    private StringProperty lightVal;
    private StringProperty proxVal;
    private StringProperty date;
    private StringProperty time;

    SearchResult(String id, int danger, String light, String prox, String date, String time) {
        this.id = new SimpleStringProperty(id);
        this.levelOfDanger = new SimpleIntegerProperty(danger);
        this.lightVal = new SimpleStringProperty(light);
        this.proxVal = new SimpleStringProperty(prox);
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
