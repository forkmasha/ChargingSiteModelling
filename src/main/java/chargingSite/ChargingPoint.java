package chargingSite;

import java.util.ArrayList;

public class ChargingPoint {
    private String id; // Unique identifier for the EV
    private  String model;
    private ArrayList <PlugType> plugTypes; // Plug type provided to connect to this ChargingPoint
    private double maxPower;
    private boolean state;
    private double currentPower;
    private ElectricVehicle chargedCar;


    public void setChargedCar(ElectricVehicle chargedCar) {
        this.chargedCar = chargedCar;
    }

    public ChargingPoint(double maxPower) {
        this.maxPower = maxPower;
    }
}
