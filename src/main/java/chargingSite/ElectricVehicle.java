package chargingSite;

import distributions.UniformDistribution;
import results.Statistics;

import java.util.ArrayList;
import java.util.List;

public class ElectricVehicle {
    private String id; // Unique identifier for the EV
    private  String model;
    private double reservationTime; // How long it occupies the ChargingPoint (reservation based) h
    private double chargeDemand; // How much energy it wishes to charge (time is determined by charge demand) kWh
    private double maxPower; // Power at which it can be charged (declines with increasing SoC) kW
    private String plugType; // Plug type required to connect to a ChargingPoint
    private String chargingCard; // Charging card used (for payment)
    private double stateOfCharge; //SoC %
    private  double batteryCapacity;//amount Of energy the battery can store kWh
    private double energyCharged; //amount of energy kWh charged since last charging started [kWh]
    private double chargingPower; //current Charging power (from charging point) kW

    private ChargingPoint chargingPoint;

    public ElectricVehicle(String model, double maxPower, double batteryCapacity) {
        this.id = model + "_" + UniformDistribution.createSample(500) + "_" + System.currentTimeMillis();
        this.model = model;
        this.maxPower = maxPower;
        this.chargingPower = maxPower;
        this.batteryCapacity = batteryCapacity;
        this.chargeDemand = batteryCapacity;
        this.stateOfCharge = 0;
        this.energyCharged = 0;
    }

    public void setReservationTime(double reservationTime) {
        this.reservationTime = reservationTime;
    }

    public void setChargeDemand(double chargeDemand) {

        this.chargeDemand = chargeDemand;
        this.stateOfCharge = 1 - chargeDemand/batteryCapacity;
    }

    public void addEnergyCharged(double duration) {
        if (stateOfCharge>=1) { return; }
        double chargedEnergy = duration * chargingPower;
        this.updateChargingPower();
        if (stateOfCharge + chargedEnergy/batteryCapacity > 1) {
            chargedEnergy = (1-stateOfCharge) * batteryCapacity;
        }
        this.stateOfCharge += chargedEnergy/batteryCapacity;
        this.energyCharged += chargedEnergy;
        System.out.println("duration = " + duration);
        System.out.println("chargingPower = " + chargingPower);
        System.out.println("chargedEnergy = " + chargedEnergy);
    }

    public void resetEnergyCharged() {this.energyCharged = 0.0; }
    
    public void updateChargingPower() {
        double newChargingPower = this.chargingPower;
        if (this.stateOfCharge>=1) {
            this.chargingPower = 0;
        } else if (this.stateOfCharge>0.5) {  // adjust charging power to current state of charge
            this.chargingPower *= 0.5;  // TO BE DONE - not the correct formula yet!
        }
        if (this.chargingPower>this.maxPower) {  // limit charging power to max possible
            this.chargingPower=this.maxPower;
            // TO BE DONE - limit also to max available for Charging Site
        }
    }

    public void setPlugType(String plugType) {
        this.plugType = plugType;
    }

    public void setChargingPoint(ChargingPoint chargingPoint) {
        this.chargingPoint = chargingPoint;
    }

    private ArrayList<Double> chargingPowerHistory = new ArrayList<>();

    public ArrayList<Double> getChargingPowerHistory() {
        return chargingPowerHistory;
    }

    public double getChargingPower() {
        return chargingPower;
    }
    public double getEnergyCharged() { return energyCharged; }

    public void setChargingPower(double chargingPower) {
        this.chargingPower = chargingPower;
    }


    public double getStateOfCharge() {
        return stateOfCharge;
    }

    public void setStateOfCharge(double stateOfCharge) {
        this.stateOfCharge = stateOfCharge;
    }

    public double getMaxPower() {
        return maxPower;
    }

    public double getBatteryCapacity() {
        return batteryCapacity;
    }
}
