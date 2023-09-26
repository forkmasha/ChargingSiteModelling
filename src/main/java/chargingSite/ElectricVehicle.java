package chargingSite;

import distributions.Distribution;
import distributions.DistributionType;
import distributions.UniformDistribution;
import queueingSystem.QueueingSystem;
import results.Statistics;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class ElectricVehicle {
    private String id; // Unique identifier for the EV
    private  String model;
    private double reservationTime; // How long it occupies the ChargingPoint (reservation based) h
    private double chargeDemand; // How much energy it wishes to charge (time is determined by charge demand) kWh
    private final Distribution demandDistribution; // How is the ChargingDemand distributed
    private double maxPower; // Power at which it can be charged (declines with increasing SoC) kW
    private String plugType; // Plug type required to connect to a ChargingPoint
    private String chargingCard; // Charging card used (for payment)
    private double stateOfCharge; //SoC %
    private  double batteryCapacity;//amount Of energy the battery can store kWh
    private double energyCharged; //amount of energy kWh charged since last charging started [kWh]
    private double chargingPower; //current Charging power (from charging point) kW
    private ChargingPoint chargingPoint;
    private QueueingSystem siteModel;

    public ElectricVehicle(String model, double maxPower, double batteryCapacity, DistributionType demandDistributionType) {
        this.id = model + "_" + UniformDistribution.createSample(500) + "_" + System.currentTimeMillis();
        this.model = model;
        this.maxPower = maxPower;
        this.chargingPower = maxPower;
        this.batteryCapacity = batteryCapacity;
        this.demandDistribution = Distribution.create(demandDistributionType);
        this.chargeDemand = batteryCapacity * demandDistribution.getSample(0.2);
        this.stateOfCharge = 1 - chargeDemand/batteryCapacity;
        this.energyCharged = 0;
    }

    public void setQueueingSystem(QueueingSystem siteModel) {
        this.siteModel = siteModel;
    }
    public QueueingSystem getSiteModel() {
        return this.siteModel;
    }
    public void setReservationTime(double reservationTime) {
        this.reservationTime = reservationTime;
    }

    public void setChargeDemand(double chargeDemand) {

        this.chargeDemand = chargeDemand;
        this.stateOfCharge = 1 - chargeDemand/batteryCapacity;
    }

    public void addEnergyCharged(double duration, double sitePower) {
        if (stateOfCharge>=1) { return; }
        double chargedEnergy = duration * chargingPower;
        this.updateChargingPower(sitePower);
        if (stateOfCharge + chargedEnergy/batteryCapacity > 1) {
            chargedEnergy = (1-stateOfCharge) * batteryCapacity;
        }
        this.stateOfCharge += chargedEnergy/batteryCapacity;
        this.energyCharged += chargedEnergy;
        //System.out.println("duration = " + duration);
        //System.out.println("chargingPower = " + chargingPower);
        //System.out.println("chargedEnergy = " + chargedEnergy);
    }

    public void resetEnergyCharged() {this.energyCharged = 0.0; }
    
    public void updateChargingPower(double sitePower) {
        double newChargingPower = this.chargingPower;
        double maxChargingPointPower = this.getChargingPoint().getMaxPower();
        double maxChargingSitePower = this.getSiteModel().getChargingSite().getMaxSitePower();
        if (this.stateOfCharge>=1) {
            this.chargingPower = 0;
        } else if (this.stateOfCharge>0.8) {  // adjust charging power to current state of charge
            this.chargingPower = this.batteryCapacity * (1 - stateOfCharge ) / 0.2 ;
        } else if (this.stateOfCharge>0.2) {  // adjust charging power to current state of charge
            this.chargingPower = this.batteryCapacity * 3.3 * (1.1 - stateOfCharge);
        } else { // adjust charging power to current state of charge
            this.chargingPower = this.batteryCapacity + 2 * this.batteryCapacity * stateOfCharge / 0.2;
        }

        if (this.chargingPower>maxChargingPointPower) {  // limit charging power to max possible
            this.chargingPower = maxChargingPointPower;
        }

        if (this.chargingPower>maxChargingSitePower) {  // limit charging power to max possible
            this.chargingPower *= maxChargingSitePower/sitePower;
        }

        System.out.println("chargingPower = " + this.chargingPower);
    }

    public void setPlugType(String plugType) {
        this.plugType = plugType;
    }

    public void setChargingPoint(ChargingPoint chargingPoint) {
        this.chargingPoint = chargingPoint;
    }

    public ChargingPoint getChargingPoint() {
        return chargingPoint;
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
