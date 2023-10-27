package chargingSite;

import java.util.ArrayList;

public class ChargingSite {
    private ArrayList<ChargingPoint> chargingPoints = new ArrayList<>();
    private int numberOfChargingPoints;
    private ArrayList<Double> charginPowers = new ArrayList<Double>();
    private double maxSitePower;

    public ChargingSite(int numberOfChargingPoints, double maxSitePower) {
        this.numberOfChargingPoints = numberOfChargingPoints;
        for ( int i=0; i<numberOfChargingPoints; i++ )  {
            charginPowers.add(i,(double) Simulation.MAX_SITE_POWER);
            chargingPoints.add(i, new ChargingPoint(charginPowers.get(i)));
        }
        this.maxSitePower = maxSitePower;
    }

    public ChargingPoint getChargingPoint(int index) {
        return this.chargingPoints.get(index);
    }

    public double getMaxSitePower() {
        return this.maxSitePower;
    }

}
