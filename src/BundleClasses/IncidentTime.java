package BundleClasses;


/**
 * Helpful bundle class that only holds time/date (as an int array)
 * and the involved ID of an incident.
 */
public class IncidentTime {
    private int[] time;
    private int[] date;
    private String id;

    public IncidentTime(int[] time, int[] date, String id) {
        this.time = time;
        this.date = date;
        this.id = id;
    }

    public IncidentTime(int[] time, String id) {
        this.time = time;
        this.id = id;
        this.date = new int[]{0, 0, 0};
    }

    public int[] getTime() {
        return this.time;
    }

    public int[] getDate() {
        return this.date;
    }

    public String getId() {
        return this.id;
    }
}
