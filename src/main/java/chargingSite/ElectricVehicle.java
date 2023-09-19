package chargingSite;

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
    private double chargingPower; //current Charging power (from charging point) kW

    private ChargingPoint chargingPoint;

    public ElectricVehicle(String model, double maxPower, double batteryCapacity) {
        this.model = model;
        this.maxPower = maxPower;
        this.batteryCapacity = batteryCapacity;
    }

    public void setReservationTime(double reservationTime) {
        this.reservationTime = reservationTime;
    }

    public void setChargeDemand(double chargeDemand) {
        this.chargeDemand = chargeDemand;
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
