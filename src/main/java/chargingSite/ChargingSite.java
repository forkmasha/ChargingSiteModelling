package chargingSite;

import queueingSystem.Server;

import java.util.ArrayList;

public class ChargingSite {
    private ArrayList<ChargingPoint> chargingPoints = new ArrayList<>();
    private int numberOfChargingPoints;
    private ArrayList<Double> chargingPowers = new ArrayList<Double>();
    private double maxSitePower;

    public ChargingSite(int numberOfChargingPoints, double maxSitePower) {
        this.numberOfChargingPoints = numberOfChargingPoints;
        this.maxSitePower = maxSitePower;
        for ( int i=0; i<numberOfChargingPoints; i++ )  {
            chargingPowers.add(i,(double) Simulation.MAX_POINT_POWER);
            chargingPoints.add(i, new ChargingPoint(Simulation.MAX_POINT_POWER));
        }
    }

    public ChargingPoint getChargingPoint(int index) {
        return this.chargingPoints.get(index);
    }
    public ChargingPoint getIdleChargingPoint(PlugType plugType) {
        for(ChargingPoint next : chargingPoints) {
            if (next.getChargedCar() == null) return next; // && next.getPlugTypes().contains(plugType)
        }
        return null;
    }

    public double getMaxSitePower() {
        return this.maxSitePower;
    }
    public void checkPower() { getSitePower(); }
    public double getSitePower() {
        double sitePower = 0;
        int i = 0;
        for (ChargingPoint next : chargingPoints) {
            sitePower += next.getPower();
            i++;
        }
        if (i > numberOfChargingPoints) {
            System.out.println("ERROR: <ChargingSite>.getSitePower summed over " + i + " chargingPoints > " + this.numberOfChargingPoints + " configured!");
        }
        if(sitePower > maxSitePower) {
            sitePower = scaleChargingPower(maxSitePower/sitePower);
            if (sitePower-maxSitePower>0.0001) {
                System.out.println("Warning in getSitePower: Site power " + sitePower + " is bigger than set maximum " + maxSitePower + " !");
            }
        }
        return sitePower;
    }
    public double scaleChargingPower(double scale) {
        double newSitePower = 0;
        for (ChargingPoint next : chargingPoints) {
            ElectricVehicle car = next.getChargedCar();
            if (car != null ) newSitePower += car.scaleChargingPower(scale);
        }
        return newSitePower;
    }
}
