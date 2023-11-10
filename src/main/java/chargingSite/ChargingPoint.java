package chargingSite;

import java.util.ArrayList;

public class ChargingPoint {
    private String id; // Unique identifier for the EV
    private  String model;
    private ArrayList <PlugType> plugTypes = new ArrayList<PlugType>(); // Plug type provided to connect to this ChargingPoint
    private double maxPower;
    private boolean state;
    private double currentPower;
    private ElectricVehicle chargedCar;

    public ChargingPoint(double power) {
        this.plugTypes.add(PlugType.CCStype2);
        this.maxPower = power;
    }

    public void pluginCar(ElectricVehicle chargedCar) {
        this.chargedCar = chargedCar;
    }
    public ElectricVehicle getChargedCar() { return chargedCar; }
    public void unplugCar() {
        chargedCar = null;
    }

    public ArrayList <PlugType> getPlugTypes() {
        return plugTypes;
    }
    public double getMaxPower() {
        return this.maxPower;
    }
    public double getPower() {
        if (chargedCar != null) {
            return chargedCar.getChargingPower();
        }
        else {
            //System.out.println("Warning: <ChargingPoint>.getPower returned zero because no car was assigned!"); it's OK because ChargingPoint ca be idle at times
            return 0;
        }
    }

}