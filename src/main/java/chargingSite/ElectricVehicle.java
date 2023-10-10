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

    private double meanChargingDemand;

    public ElectricVehicle(String model, double maxPower, double batteryCapacity, DistributionType demandDistributionType) {
        this.id = model + "_" + (int) UniformDistribution.createSample(500); // + "_" + System.currentTimeMillis();
        this.model = model;
        this.maxPower = maxPower;
        this.chargingPower = maxPower;
        this.batteryCapacity = batteryCapacity;
        this.demandDistribution = Distribution.create(demandDistributionType);
        double demand = demandDistribution.getSample(Simulation.MEAN_CHARGING_DEMAND);  // TO BO DONE ???  via GUI (0.1, 0.2, ... 0.9) meanChargingDemand
        //bad patch !!! TO BE DONE better, i.e., BETA distribution sometimes returns values slightly above 1 for mean > 0.7 ...
        if(demand < 0) {System.out.println("Warning: negative demand  " + demand + " was converted to positive."); demand *= -1; }
        if(demand > 1) {System.out.println("Warning: demand " + demand + " > 1 was converted to 1/demand."); demand = 1/demand; }
        this.chargeDemand = demand * batteryCapacity;
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

    public void setMeanChargingDemand(double meanChargingDemand) {
        this.meanChargingDemand = meanChargingDemand;
    }

    public void setChargeDemand(double chargeDemand) {

        this.chargeDemand = chargeDemand;
        this.stateOfCharge = 1 - chargeDemand/batteryCapacity;
    }

    public void addEnergyCharged(double duration, double sitePower) {
        if (this.stateOfCharge>=1) { return; }
        this.updateChargingPower(sitePower);
        double chargedEnergy = duration * this.chargingPower;
        if(chargedEnergy<0) {System.out.println("ERROR: Charged Energy is negative!"); System.exit(1);}
        //if(chargingPower<0) {System.out.println("ERROR: Negative charging power!"); System.exit(1);}
        if (this.stateOfCharge + chargedEnergy/this.batteryCapacity > 1) {
            chargedEnergy = (1-this.stateOfCharge) * this.batteryCapacity;
        }
        this.stateOfCharge += chargedEnergy/this.batteryCapacity;
        this.energyCharged += chargedEnergy;

        // tracking down negative SoC...
        if(this.stateOfCharge<0) {
            System.out.println("duration = " + duration);
            System.out.println("chargingPower = " + chargingPower);
            System.out.println("chargedEnergy = " + chargedEnergy);
            System.out.println("SoC = " + stateOfCharge);
            System.out.println("ERROR: Negative SoC!"); System.exit(1);
        }
        /* if(this.energyCharged<0) {
            System.out.println("duration = " + duration);
            System.out.println("chargingPower = " + chargingPower);
            System.out.println("chargedEnergy = " + chargedEnergy);
            System.out.println("ERROR: EnergyCharged became negative !");
            System.exit(1);
        } */
    }

    public void resetEnergyCharged() {this.energyCharged = 0.0; }
    
    public void updateChargingPower(double sitePower) {
        // double newChargingPower = this.chargingPower;
        if(this.chargingPower<0) {System.out.println("ERROR: Negative charging power prior update!");}
        if(this.stateOfCharge<0) {System.out.println("ERROR: Negative SoC prior charging power update!");}
        double maxChargingPointPower = this.getChargingPoint().getMaxPower();
        double maxChargingSitePower = this.getSiteModel().getChargingSite().getMaxSitePower();
        if (this.stateOfCharge>=1) {
            this.chargingPower = 0;
        } else if (this.stateOfCharge>0.8) {  // adjust charging power to current state of charge
            this.chargingPower = this.batteryCapacity * (1 - stateOfCharge ) / 0.2 ;
            if(this.chargingPower<0) {System.out.println("ERROR: Negative charging power for SoC >0.8!");}
        } else if (this.stateOfCharge>0.2) {  // adjust charging power to current state of charge
            this.chargingPower = this.batteryCapacity * 10 * (1.1 - stateOfCharge);
            if(this.chargingPower<0) {System.out.println("ERROR: Negative charging power for SoC in 0.2 .. 0.8!");}
        } else { // adjust charging power to current state of charge
            this.chargingPower = this.batteryCapacity + 2 * this.batteryCapacity * this.stateOfCharge / 0.2;
            if(this.chargingPower<0) {System.out.println("ERROR: Negative charging power for SoC <0.2!");}
        }

        //System.out.println("batteryCapacity = " + this.batteryCapacity);
        //System.out.println("stateOfCharge = " + this.stateOfCharge);
        //System.out.println("chargingPower = " + this.chargingPower);

        if (this.chargingPower>maxChargingPointPower) {  // limit charging power to max possible
            this.chargingPower = maxChargingPointPower;
            if(this.chargingPower<0) {System.out.println("ERROR: Negative charging power after PointLimiting!");}
        }

        if (sitePower>maxChargingSitePower) {  // limit charging power to max possible
            this.chargingPower *= maxChargingSitePower/sitePower;
            if(this.chargingPower<0) {System.out.println("ERROR: Negative charging power after SiteLimiting!");}
        }
        // looking for negative charging power (error)
        if(this.chargingPower<0 || this.stateOfCharge<0) {
            System.out.println("ERROR: " + this.id + ": SoC = " + this.stateOfCharge + " chargingPower = " + this.chargingPower);
            System.out.println("Battery Capacity: " + this.batteryCapacity + " Energy charged:" + this.getEnergyCharged());
            System.exit(1);
        }
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
