package chargingSite;

public class TimePowerData {
    private double time;
    private double power;

    public TimePowerData(double time, double power) {
        this.time = time;
        this.power = power;
    }

    public double getTime() {
        return time;
    }

    public double getPower() {
        return power;
    }
}
