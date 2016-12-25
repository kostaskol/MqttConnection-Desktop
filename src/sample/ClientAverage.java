package sample;

class ClientAverage {
    private String clientId;
    private float lightAverage;
    private float lightSum;
    private int times;

    ClientAverage(String id, float avg, float lightSum, int times) {
        this.clientId = id;
        this.lightAverage = avg;
        this.lightSum = lightSum;
        this.times = times;
    }

    String getClientId() {
        return this.clientId;
    }

    void setClientId(String id) {
        this.clientId = id;
    }

    float getLightAverage() {
        return this.lightAverage;
    }

    void setLightAverage(float avg) {
        this.lightAverage = avg;
    }

    float getLightSum() {
        return this.lightSum;
    }

    void setLightSum(float sum) {
        this.lightSum = sum;
    }

    int getTimes() {
        return this.times;
    }

    void setTimes(int times) {
        this.times = times;
    }
}
