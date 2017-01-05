package BundleClasses;

public class ClientAverage {
    private String clientId;
    private float lightAverage;
    private float lightSum;
    private int times;
    private boolean isRinging;

    public ClientAverage(String id, float avg, float lightSum, int times, int ring) {
        this.clientId = id;
        this.lightAverage = avg;
        this.lightSum = lightSum;
        this.times = times;
        this.isRinging = ring == 1;
    }

    public String getClientId() {
        return this.clientId;
    }

    public float getLightAverage() {
        return this.lightAverage;
    }

    public void setLightAverage(float avg) {
        this.lightAverage = avg;
    }

    public float getLightSum() {
        return this.lightSum;
    }

    public void setLightSum(float sum) {
        this.lightSum = sum;
    }

    public int getTimes() {
        return this.times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public boolean isRinging() {
        return this.isRinging;
    }

    public void setIsRinging(boolean isRinging) {
        this.isRinging = isRinging;
    }
}
