package chargingSite;

import distributions.Distribution;
import distributions.DistributionType;
import distributions.UniformDistribution;
import exceptions.ChargingPowerUpdateException;
import exceptions.NegativeChargedEnergyException;
import exceptions.NegativeChargingPowerException;
import exceptions.NegativeSoCduration;
import queueingSystem.QueueingSystem;
import queueingSystem.Server;

import java.util.logging.Logger;

public class ElectricVehicle {

    private String id; // Unique identifier for the EV
    private String model;
    private double reservationTime; // How long it occupies the ChargingPoint (reservation based) h
    private double chargeDemand; // How much energy it wishes to charge (time is determined by charge demand) kWh
    private double meanServiceTime;
    private final Distribution demandDistribution; // How is the ChargingDemand distributed
    private double maxPower; // Power at which it can be charged (declines with increasing SoC) kW
    private PlugType plugType = PlugType.CCStype2; // Plug type required to connect to a ChargingPoint
    private String chargingCard; // Charging card used (for payment)
    private double stateOfCharge; //SoC %
    private double batteryCapacity;//amount Of energy the battery can store kWh
    private double energyCharged; //amount of energy kWh charged since last charging started [kWh]
    private double chargingPower; //current Charging power (from charging point) kW
    private double meanChargingDemand;
    private static double MAX_BATTERY_CAPACITY = 0;
    private ChargingPoint chargingPoint;
    private QueueingSystem siteModel;
    private Server myServer;

    private static final Logger logger = Logger.getLogger(ElectricVehicle.class.getName());

    public ElectricVehicle(String model, double maxPower, double batteryCapacity, DistributionType demandDistributionType) {
        this.id = model + "_" + (int) UniformDistribution.createSample(500); // + "_" + System.currentTimeMillis();
        // this.id = model + "_" + UUID.randomUUID();
        this.model = model;
        this.maxPower = maxPower;
        this.chargingPower = 0;
        this.batteryCapacity = batteryCapacity;
        if (batteryCapacity > ElectricVehicle.MAX_BATTERY_CAPACITY)
            ElectricVehicle.MAX_BATTERY_CAPACITY = batteryCapacity;
        this.demandDistribution = Distribution.create(demandDistributionType);
        double demand = demandDistribution.getSample(Simulation.MEAN_CHARGING_DEMAND);
        //bad patch !!! TO BE DONE better, i.e., BETA distribution sometimes returns values slightly above 1 for mean > 0.7 ...
        if (demand < 0) {
            logger.warning("Warning: negative demand  " + demand + " was converted to positive.");
            demand *= -1;
        }
        if (demand > 1) {
            logger.warning("Warning: demand " + demand + " > 1 was converted to 1/demand.");
            demand = 1 / demand;
        }
        this.chargeDemand = demand * batteryCapacity;
        this.stateOfCharge = 1 - chargeDemand / batteryCapacity;
        this.energyCharged = 0;
    }

    public void setMeanServiceTime(double meanServiceTime) {
        this.meanServiceTime = meanServiceTime;
    }

    public void setQueueingSystem(QueueingSystem siteModel) {
        this.siteModel = siteModel;
    }

    public void setChargingPoint(ChargingPoint chargingPoint) {
        this.chargingPoint = chargingPoint;
    }

    public void setMyServer(Server myServer) {
        this.myServer = myServer;
    }

    public QueueingSystem getSiteModel() {
        return this.siteModel;
    }

    public double getChargeDemand() {
        return chargeDemand;
    }

    public PlugType getPlugType() {
        return plugType;
    }

    public ChargingPoint getChargingPoint() {
        return chargingPoint;
    }

    public double getChargingPower() {
        return chargingPower;
    }

    public double getEnergyCharged() {
        return energyCharged;
    }


    public Server getMyServer() {
        return myServer;
    }

    public void setChargeDemand(double chargeDemand) {

        this.chargeDemand = chargeDemand;
        this.stateOfCharge = 1 - chargeDemand / batteryCapacity;
    }

    public void addEnergyCharged(double duration) {
        if (this.stateOfCharge >= 1) {
            this.chargingPower = 0;
            return;
        }
        //this.updateChargingPower(sitePower);
        double ChargingPowerAtBeginOfInterval = this.chargingPower;
        this.updateChargingPower();
        double chargedEnergy = duration * (ChargingPowerAtBeginOfInterval + this.chargingPower) / 2;
        if (chargedEnergy < 0) {
            logger.warning("Charged Energy is negative!");
            throw new NegativeChargedEnergyException("Charged Energy is negative!");
        }
        //if(chargingPower<0) {System.out.println("ERROR: Negative charging power!"); System.exit(1);}
        if (this.stateOfCharge + chargedEnergy / this.batteryCapacity > 1) {
            chargedEnergy = (1 - this.stateOfCharge) * this.batteryCapacity;
            this.chargingPower = 0;
        }
        this.stateOfCharge += chargedEnergy / this.batteryCapacity;
        this.energyCharged += chargedEnergy;

        if (this.meanServiceTime <= 0 && this.stateOfCharge >= 1) {
            // calculate charging performance (this.chargeDemand * this.batteryCapacity - this.chargedEnergy) and store it for statistical evaluation
            this.siteModel.instantDeparture(this.getMyServer().getClient());
            // System.out.println("Car finished service (charging) because the battery is full.");
        }

        // tracking down negative SoC...

        if (this.stateOfCharge < 0) {
            logger.severe("Negative SoC! duration = " + duration + ", chargingPower = " + chargingPower +
                    ", chargedEnergy = " + chargedEnergy + ", SoC = " + stateOfCharge);
            throw new NegativeSoCduration("Negative SoC duration!");
        }
        /* if(this.energyCharged<0) {
            System.out.println("duration = " + duration);
            System.out.println("chargingPower = " + chargingPower);
            System.out.println("chargedEnergy = " + chargedEnergy);
            System.out.println("ERROR: EnergyCharged became negative !");
            System.exit(1);
        } */
    }

    public void resetEnergyCharged() {
        this.energyCharged = 0.0;
    }

    public void updateChargingPower() {
        // double newChargingPower = this.chargingPower;
        if (this.chargingPower < 0) {
            throw new NegativeChargingPowerException("Negative charging power prior update!");
        }
        if (this.stateOfCharge < 0) {
            throw new NegativeChargingPowerException("Negative SoC prior charging power update!");
        }
        if (this.stateOfCharge >= 1) {
            this.chargingPower = 0;
        }
        else if (this.stateOfCharge > 0.8) {  // adjust charging power to current state of charge
            this.chargingPower = this.batteryCapacity * (1 - stateOfCharge) / 0.2;
            if (this.chargingPower < 0) {
                throw new NegativeChargingPowerException("Negative charging power for SoC > 0.8!");
            }
        }
        else if (this.stateOfCharge > 0.2) {  // adjust charging power to current state of charge
            this.chargingPower = this.batteryCapacity * (1 + (0.8 - stateOfCharge) / 0.3);
            if (this.chargingPower < 0) {
                throw new NegativeChargingPowerException("Negative charging power for SoC in 0.2 .. 0.8!");
            }
        }
        else { // adjust charging power to current state of charge
            this.chargingPower = this.batteryCapacity * (0.5 + 2.5 * stateOfCharge / 0.2);
            if (this.chargingPower < 0) {
                throw new NegativeChargingPowerException("Negative charging power for SoC < 0.2!");
            }
        }

        //System.out.println("batteryCapacity = " + this.batteryCapacity);
        //System.out.println("stateOfCharge = " + this.stateOfCharge);
        //System.out.println("chargingPower = " + this.chargingPower);

        // limit charging power to max possible per charging point
        this.chargingPower = this.getChargingPoint().checkPower(this.chargingPower);

        /*double maxChargingPointPower = this.getChargingPoint().getMaxPower();
        if (this.chargingPower>maxChargingPointPower) {  // limit charging power to max possible per charging point
            this.chargingPower = maxChargingPointPower;
            if(this.chargingPower<0) {System.out.println("ERROR: Negative charging power after PointLimiting!");}
        }*/

        // limit charging power to max possible for charging site
        // -> does it for all cars currently charged, adjusting the chargingPower if needed
        this.getSiteModel().getChargingSite().checkPower();

        //double maxChargingSitePower = this.getSiteModel().getChargingSite().getMaxSitePower();
        //if (sitePower>maxChargingSitePower) {  // limit charging power to max possible for charging site
        //    this.scaleChargingPower(maxChargingSitePower/sitePower);
        //    if(this.chargingPower<0) {System.out.println("ERROR: Negative charging power after SiteLimiting!");}
        //}

        // limit charging power to maximum specified for the ElectricVehicle
        if (this.chargingPower > this.maxPower) {
            this.chargingPower = this.maxPower;
        }

        // looking for negative charging power (error)
        if (this.chargingPower < 0 || this.stateOfCharge < 0) {
            String errorMessage = "updateChargingPower ERROR: " + this.id +
                    ": SoC = " + this.stateOfCharge +
                    " chargingPower = " + this.chargingPower;
            logger.severe(errorMessage);
            throw new ChargingPowerUpdateException(errorMessage);
        }

        //if(this.chargingPower>maxChargingSitePower/getSiteModel().getNumberOfServers()) {
        //    System.out.println("ERROR1: Charging power " + this.chargingPower + "is bigger than maximum currently available " + sitePower + " !");}

        this.getChargingPoint().setCurrentPower(this.chargingPower);
    }

    public double scaleChargingPower(double scale) {
        double newPower = scale * this.chargingPower;
        this.chargingPower = newPower;
        return newPower;
    }
}