package chargingSite;

import java.util.ArrayList;

public class ChargingPoint {
    private String id;
    private String model;
    private boolean state;
    private double currentPower;

    // Plug type provided to connect to this ChargingPoint
    private ArrayList<PlugType> plugTypes = new ArrayList<>();
    private ElectricVehicle chargedCar;
    private double maxPower;

    public ChargingPoint(double maxPower) {
        this.plugTypes.add(PlugType.CCStype2);
        this.maxPower = maxPower;
    }

    public void setCurrentPower(double power) {
        this.currentPower = power;
    }

    public void pluginCar(ElectricVehicle chargedCar) {
        chargedCar.setChargingPoint(this);
        this.chargedCar = chargedCar;
        chargedCar.updateChargingPower();
    }

    public void unplugCar() {
        this.currentPower = 0;
        chargedCar = null;
    }

    public ElectricVehicle getChargedCar() {
        return chargedCar;
    }

    public double checkPower(double power) {
        if (power > maxPower) {
            // Power reduced to maxPointPower
            return maxPower;
        }
        return power;
    }

    public double getPower() {
        if (chargedCar != null) {
            return chargedCar.getChargingPower();
        } else {
            // getPower returned zero because no car was assigned,
            // it's OK because ChargingPoint ca be idle at times
            return 0;
        }
    }
}