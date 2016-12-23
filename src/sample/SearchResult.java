package sample;


import java.sql.Date;
import java.sql.Time;

public class SearchResult {
    private String id;
    private int levelOfDanger;
    private String lightVal;
    private String proxVal;
    private Date date;
    private Time time;

    SearchResult(String id, int danger, String light, String prox, Date date, Time time) {
        this.id = id;
        this.levelOfDanger = danger;
        this.lightVal = light;
        this.proxVal = prox;
        this.date = date;
        this.time = time;
    }

    String getId() { return this.id; }

    int getLevelOfDanger() { return this.levelOfDanger; }

    String getLightVal() { return this.lightVal; }

    String getProxVal() { return this.proxVal; }

    Date getDate() { return this.date; }

    Time getTime() { return this.time; }

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
