package chargingSite;


import distributions.Distribution;
import distributions.DistributionType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import queueingSystem.Queue;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;


public class SimulationParameters {

    private double MIN_ARRIVAL_RATE;
    private double MAX_ARRIVAL_RATE;
    private double ARRIVAL_RATE_STEP;
    private int SIM_STEPS;
    private int NUMBER_OF_CAR_TYPES;
    private int MAX_EVENTS;
    private int NUMBER_OF_SERVERS;

    public int MAX_SITE_POWER;  // Maximum Charging Site Power (50.000)
    public int MAX_POINT_POWER; // Maximum Charging Point Power (750)
    public int MAX_EV_POWER; // Maximum EV Charging Power (750)
    public int MAX_EV_POWER2;
    public int MAX_EV_POWER3;

    public double MEAN_CHARGING_DEMAND;
    public double MEAN_CHARGING_DEMAND2;
    public double MEAN_CHARGING_DEMAND3;

    private int QUEUE_SIZE;
    private Queue.QueueingType QUEUEING_TYPE;
    private double MEAN_SERVICE_TIME;
    private double MEAN_SERVICE_TIME2;
    private double MEAN_SERVICE_TIME3;
    public DistributionType SERVICE_TYPE;
    private double AVERAGE_SERVICE_TIME;
    private DistributionType ARRIVAL_TYPE;
    private DistributionType DEMAND_TYPE;
    private DistributionType DEMAND_TYPE2;
    private DistributionType DEMAND_TYPE3;
    private int confLevel;
    public double batteryCapacity;
    public double batteryCapacity2;
    public double batteryCapacity3;

    private double percentageOfCars = 1;
    private double percentageOfCars2;
    private double percentageOfCars3;

    SimulationParameters() {

    }

    public int getConfLevel() {
        return confLevel;
    }

    public void setConfLevel(int confLevel) {
        this.confLevel = confLevel;
    }

    public DistributionType getSERVICE_TYPE() {
        return SERVICE_TYPE;
    }

    public int getSIM_STEPS() {
        return SIM_STEPS;
    }


    public void setMIN_ARRIVAL_RATE(double MIN_ARRIVAL_RATE) {
        this.MIN_ARRIVAL_RATE = MIN_ARRIVAL_RATE;
    }

    public void setMAX_ARRIVAL_RATE(double MAX_ARRIVAL_RATE) {
        this.MAX_ARRIVAL_RATE = MAX_ARRIVAL_RATE;
    }

    public double getARRIVAL_RATE_STEP() {
        return ARRIVAL_RATE_STEP;
    }

    public void setARRIVAL_RATE_STEP(double ARRIVAL_RATE_STEP) {
        this.ARRIVAL_RATE_STEP = ARRIVAL_RATE_STEP;
    }


    public void setSIM_STEPS(int SIM_STEPS) {
        this.SIM_STEPS = SIM_STEPS;
    }

    public void setBatteryCapacity(double batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }

    public void setNUMBER_OF_CAR_TYPES(int NUMBER_OF_CAR_TYPES) {
        this.NUMBER_OF_CAR_TYPES = NUMBER_OF_CAR_TYPES;
    }

    public void setMAX_EVENTS(int MAX_EVENTS) {
        this.MAX_EVENTS = MAX_EVENTS;
    }

    public void setNUMBER_OF_SERVERS(int NUMBER_OF_SERVERS) {
        this.NUMBER_OF_SERVERS = NUMBER_OF_SERVERS;
    }

    public void setQUEUE_SIZE(int QUEUE_SIZE) {
        this.QUEUE_SIZE = QUEUE_SIZE;
    }

    public void setQUEUEING_TYPE(Queue.QueueingType QUEUEING_TYPE) {
        this.QUEUEING_TYPE = QUEUEING_TYPE;
    }

    public void setAVERAGE_SERVICE_TIME(double AVERAGE_SERVICE_TIME) {
        this.AVERAGE_SERVICE_TIME = AVERAGE_SERVICE_TIME;
    }

    public void setARRIVAL_TYPE(DistributionType ARRIVAL_TYPE) {
        this.ARRIVAL_TYPE = ARRIVAL_TYPE;
    }

    public void setDEMAND_TYPE(DistributionType DEMAND_TYPE) {
        this.DEMAND_TYPE = DEMAND_TYPE;
    }

    public void setSERVICE_TYPE(DistributionType type) {
        SERVICE_TYPE = type;
    }

    public void setMaxSitePower(int maxSitePower) {
        MAX_SITE_POWER = maxSitePower;
    }

    public void setMeanChargingDemand(double meanChargingDemand) {
        MEAN_CHARGING_DEMAND = meanChargingDemand;
    }

    public void setMaxPointPower(int maxPointPower) {
        MAX_POINT_POWER = maxPointPower;
    }

    public void setMaxEvPower(int maxEvPower) {
        MAX_EV_POWER = maxEvPower;
    }


    public void setMaxEvPower2(int maxEvPower2) {
        MAX_EV_POWER2 = maxEvPower2;
    }

    public void setMaxEvPower3(int maxEvPower3) {
        MAX_EV_POWER3 = maxEvPower3;
    }

    public void setMeanChargingDemand2(double meanChargingDemand2) {
        MEAN_CHARGING_DEMAND2 = meanChargingDemand2;
    }

    public void setMeanChargingDemand3(double meanChargingDemand3) {
        MEAN_CHARGING_DEMAND3 = meanChargingDemand3;
    }

    public void setDEMAND_TYPE2(DistributionType DEMAND_TYPE2) {
        this.DEMAND_TYPE2 = DEMAND_TYPE2;
    }

    public void setDEMAND_TYPE3(DistributionType DEMAND_TYPE3) {
        this.DEMAND_TYPE3 = DEMAND_TYPE3;
    }

    public void setBatteryCapacity2(double batteryCapacity2) {
        this.batteryCapacity2 = batteryCapacity2;
    }

    public void setBatteryCapacity3(double batteryCapacity3) {
        this.batteryCapacity3 = batteryCapacity3;
    }

    public void setPercentageOfCars(double percentageOfCars) {
        if (percentageOfCars > 1) percentageOfCars /= 100;
        if (percentageOfCars < 0)
            System.out.println("ERROR: percentage of type 1 cars " + percentageOfCars + "is smaller than 0!");
        this.percentageOfCars = percentageOfCars;
    }

    public void setPercentageOfCars2(double percentageOfCars) {
        if (percentageOfCars > 1) percentageOfCars /= 100;
        if (percentageOfCars < 0)
            System.out.println("ERROR: percentage of type 1 cars " + percentageOfCars + "is smaller than 0!");
        this.percentageOfCars2 = percentageOfCars;
    }

    public void setPercentageOfCars3(double percentageOfCars) {
        if (percentageOfCars > 1) percentageOfCars /= 100;
        if (percentageOfCars < 0)
            System.out.println("ERROR: percentage of type 1 cars " + percentageOfCars + "is smaller than 0!");
        this.percentageOfCars3 = percentageOfCars;
    }

    public void setMEAN_SERVICE_TIME(double time) {
        if (time == 0) System.out.println("Warning: charging till 80% is yet not implemented correctly!");
        this.MEAN_SERVICE_TIME = time;
    }

    public void setMEAN_SERVICE_TIME2(double time) {
        this.MEAN_SERVICE_TIME2 = time;
    }

    public void setMEAN_SERVICE_TIME3(double time) {
        this.MEAN_SERVICE_TIME3 = time;
    }

    public int getNUMBER_OF_CAR_TYPES() {
        return NUMBER_OF_CAR_TYPES;
    }

    public int getMaxEvPower() {
        return MAX_EV_POWER;
    }

    public int getMaxEvPower2() {
        return MAX_EV_POWER2;
    }

    public int getMaxEvPower3() {
        return MAX_EV_POWER3;
    }

    public double getMeanChargingDemand() {
        return MEAN_CHARGING_DEMAND;
    }

    public double getMeanChargingDemand2() {
        return MEAN_CHARGING_DEMAND2;
    }

    public double getMeanChargingDemand3() {
        return MEAN_CHARGING_DEMAND3;
    }

    public double getMEAN_SERVICE_TIME() {
        return MEAN_SERVICE_TIME;
    }

    public double getMEAN_SERVICE_TIME2() {
        return MEAN_SERVICE_TIME2;
    }

    public double getMEAN_SERVICE_TIME3() {
        return MEAN_SERVICE_TIME3;
    }

    public DistributionType getDEMAND_TYPE() {
        return DEMAND_TYPE;
    }

    public DistributionType getDEMAND_TYPE2() {
        return DEMAND_TYPE2;
    }

    public DistributionType getDEMAND_TYPE3() {
        return DEMAND_TYPE3;
    }

    public double getBatteryCapacity() {
        return batteryCapacity;
    }

    public double getBatteryCapacity2() {
        return batteryCapacity2;
    }

    public double getBatteryCapacity3() {
        return batteryCapacity3;
    }

    public double getPercentageOfCars() {
        return percentageOfCars;
    }

    public double getPercentageOfCars2() {
        return percentageOfCars2;
    }

    public double getPercentageOfCars3() {
        return percentageOfCars3;
    }

    public int getSimSteps() {
        return SIM_STEPS;
    }

    public int getMAX_EVENTS() {
        return MAX_EVENTS;
    }

    public double getMinArrivalRate() {
        return MIN_ARRIVAL_RATE;
    }

    public int getNUMBER_OF_SERVERS() {
        return NUMBER_OF_SERVERS;
    }

    public int getQUEUE_SIZE() {
        return QUEUE_SIZE;
    }

    public Queue.QueueingType getQUEUEING_TYPE() {
        return QUEUEING_TYPE;
    }

    public double getAVERAGE_SERVICE_TIME() {
        return AVERAGE_SERVICE_TIME;
    }

    public DistributionType getARRIVAL_TYPE() {
        return ARRIVAL_TYPE;
    }

    public double getMaxArrivalRate() {
        return MAX_ARRIVAL_RATE;
    }

    public int getMaxEvents() {
        return MAX_EVENTS;
    }

    public double getMIN_ARRIVAL_RATE() {
        return MIN_ARRIVAL_RATE;
    }

    public double getMAX_ARRIVAL_RATE() {
        return MAX_ARRIVAL_RATE;
    }

    public double getMaxSitePower() {
        return MAX_SITE_POWER;
    }

    public double getAvgServiceTime() {
        double p1, p2, p3;
        if (this.getNUMBER_OF_CAR_TYPES() < 2) this.setAVERAGE_SERVICE_TIME(this.getMEAN_SERVICE_TIME());
        else if (this.getNUMBER_OF_CAR_TYPES() < 3) {
            if (this.getPercentageOfCars2() > 1) p2 = this.getPercentageOfCars2() / 100;
            else p2 = this.getPercentageOfCars2();
            p1 = 1 - p2;
            this.setAVERAGE_SERVICE_TIME(
                    p1 * this.getMEAN_SERVICE_TIME()
                            + p2 * this.getMEAN_SERVICE_TIME2());
        } else {
            if (this.getPercentageOfCars2() > 1) p2 = this.getPercentageOfCars2() / 100;
            else p2 = this.getPercentageOfCars2();
            if (this.getPercentageOfCars3() > 1) p3 = this.getPercentageOfCars3() / 100;
            else p3 = this.getPercentageOfCars3();
            p1 = 1 - p2 - p3;
            this.setAVERAGE_SERVICE_TIME(
                    p1 * this.getMEAN_SERVICE_TIME()
                            + p2 * this.getMEAN_SERVICE_TIME2()
                            + p3 * this.getMEAN_SERVICE_TIME3());
        }
        return AVERAGE_SERVICE_TIME;
    }

    public String getKendallName() {
        return Distribution.getTitleAbbreviation(String.valueOf(ARRIVAL_TYPE)) + "/"
                + Distribution.getTitleAbbreviation(String.valueOf(SERVICE_TYPE)) + "/"
                + NUMBER_OF_SERVERS + "/" + (NUMBER_OF_SERVERS + QUEUE_SIZE);
    }


    public double getMMnNwaitingTime(double rho) {
        double meanWaitingTime;
        double arrivalRate = rho * this.NUMBER_OF_SERVERS / this.AVERAGE_SERVICE_TIME;
        double[] pdi = new double[1 + this.NUMBER_OF_SERVERS + this.QUEUE_SIZE];
        double meanQueueLength = 0;
        double sFac = Distribution.factorial(this.NUMBER_OF_SERVERS);
        rho *= this.NUMBER_OF_SERVERS;
        pdi[0] = 0;
        for (int i = 1; i <= this.QUEUE_SIZE; i++) {
            pdi[0] += Math.pow(rho / this.NUMBER_OF_SERVERS, i);
        }
        pdi[0] *= Math.pow(rho, this.NUMBER_OF_SERVERS) / sFac;
        for (int i = 1; i <= this.NUMBER_OF_SERVERS; i++) {
            pdi[0] += Math.pow(rho, i) / Distribution.factorial(i);
        }
        pdi[0] = Math.pow(1 + pdi[0], -1);

        for (int i = 1; i <= this.NUMBER_OF_SERVERS; i++) {
            pdi[i] = Math.pow(rho, i) / Distribution.factorial(i) * pdi[0];
        }
        for (int i = this.NUMBER_OF_SERVERS + 1; i <= this.NUMBER_OF_SERVERS + this.QUEUE_SIZE; i++) {
            pdi[i] = Math.pow(rho, i) / (sFac * Math.pow(this.NUMBER_OF_SERVERS, i - this.NUMBER_OF_SERVERS)) * pdi[0];
        }

        for (int i = 1; i <= this.QUEUE_SIZE; i++) {
            meanQueueLength += i * pdi[this.NUMBER_OF_SERVERS + i];
        }

        meanWaitingTime = meanQueueLength / (arrivalRate * (1 - pdi[this.NUMBER_OF_SERVERS + this.QUEUE_SIZE]));
        if (Math.abs(1 - Arrays.stream(pdi).sum()) > 0.000001) {
            System.out.println("ERROR: sum over all state-probabilities !=1");
            StringBuilder output = new StringBuilder("M/M/n/S state probabilities: ");
            for (int i = 0; i < pdi.length; i++) {
                output.append(pdi[i]).append(" / ");
            }
            System.out.println("M/M/n/S state probabilities: " + output);
        }
        System.out.println("M/M/n/S: " + arrivalRate + " " + meanQueueLength + " " + meanWaitingTime);
        return meanWaitingTime;
    }

    public void writeParameters2txt(JFrame frame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a location to save the simulation parameters file");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setSelectedFile(new java.io.File("simulation_parameters.txt"));

        int userSelection = fileChooser.showSaveDialog(frame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();

            if (!fileToSave.getPath().toLowerCase().endsWith(".txt")) {
                fileToSave = new java.io.File(fileToSave.getPath() + ".txt");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd' T 'HH:mm:ss");
                String currentDateTime = dateFormat.format(new Date());
                writer.write("Simulation Date and Time: " + currentDateTime + "\n");

                String title = "Charging Site Queueing Characteristics \n"
                        + this.getKendallName() + " Queueing System"
                        + " (" + getMAX_EVENTS() + " samples per evaluation point)";
                writer.write("Simulation Title: " + title + "\n\n");

                writer.write("General parameters" + "\n");
                writer.write("Number of  simulation steps - " + SIM_STEPS + "\n");
                writer.write("Max Events per step - " + MAX_EVENTS + "\n");
                writer.write("Confidence interval level - " + confLevel + "\n");

                writer.newLine();

                writer.write("Site parameters" + "\n");
                writer.write("Arrival Distribution Type - " + ARRIVAL_TYPE.toString() + "\n");
                writer.write("Max Mean Arrival Rate - " + MAX_ARRIVAL_RATE + "\n");
                writer.write("Parking space - " + QUEUE_SIZE + "\n");
                writer.write("Queueing Type - " + QUEUEING_TYPE.toString() + "\n");
                writer.write("Max Site Power - " + MAX_SITE_POWER + "\n");
                writer.newLine();

                writer.write("Charging parameters" + "\n");
                writer.write("Number of charging points " + NUMBER_OF_SERVERS + "\n");
                writer.write("Service Distribution Type - " + SERVICE_TYPE.toString() + "\n");
                writer.write("Max power of Charging Point" + MAX_POINT_POWER + "\n");
                writer.newLine();

                writer.write("EV parameters" + "\n");
                writer.write("Number of EV types - " + NUMBER_OF_CAR_TYPES + "\n");
                writer.write("Battery capacity - " + batteryCapacity + "\n");
                writer.write("Mean Charging Time - " + getMEAN_SERVICE_TIME() + "\n");
                writer.write("Max EV charging power - " + MAX_EV_POWER + "\n");
                writer.write("Demand distribution type - " + DEMAND_TYPE.toString() + "\n");
                writer.write("Mean charging demand - " + +MEAN_CHARGING_DEMAND + "\n");
                writer.newLine();


                int selectedClientTypes = NUMBER_OF_CAR_TYPES;
                if (selectedClientTypes == 2) {

                    writer.newLine();
                    writer.write("Parameters for the second EV" + "\n");

                    writer.write("Percentage of EV 2" + percentageOfCars2 + "\n");
                    writer.write("Battery capacity 2 - " + batteryCapacity2 + "\n");
                    writer.write("Mean charging time 2 - " + MEAN_SERVICE_TIME2 + "\n");
                    writer.write("Max EV charging power 2 - " + MAX_EV_POWER2 + "\n");
                    writer.write("Demand distribution type 2 - " + DEMAND_TYPE2.toString() + "\n");
                    writer.write("Mean charging demand 2 - " + MEAN_CHARGING_DEMAND2 + "\n");

                } else if (selectedClientTypes == 3) {
                    writer.newLine();
                    writer.write("Parameters for the second EV" + "\n");

                    writer.write("Percentage of EV 2 - " + percentageOfCars2 + "\n");
                    writer.write("Battery capacity 2 - " + batteryCapacity2 + "\n");
                    writer.write("Mean charging time 2 - " + MEAN_SERVICE_TIME2 + "\n");
                    writer.write("Max EV charging power 2 - " + MAX_EV_POWER2 + "\n");
                    writer.write("Demand distribution type 2 - " + DEMAND_TYPE2.toString() + "\n");
                    writer.write("Mean charging demand 2 - " + MEAN_CHARGING_DEMAND2 + "\n");

                    writer.newLine();
                    writer.write("Parameters for the third EV" + "\n");

                    writer.write("Percentage of EV 3 -" + percentageOfCars3 + "\n");
                    writer.write("Battery capacity 3 - " + batteryCapacity3 + "\n");
                    writer.write("Mean charging time 3 - " + MEAN_SERVICE_TIME3 + "\n");
                    writer.write("Max EV charging power 3 - " + MAX_EV_POWER3 + "\n");
                    writer.write("Demand distribution type 3 - " + DEMAND_TYPE3.toString() + "\n");
                    writer.write("Mean charging demand 3 - " + MEAN_CHARGING_DEMAND3 + "\n");
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void writeParameters2xml(JFrame frame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a location to save the simulation parameters file");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setSelectedFile(new java.io.File("simulation_parameters.xml"));

        int userSelection = fileChooser.showSaveDialog(frame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();

            if (!fileToSave.getPath().toLowerCase().endsWith(".xml")) {
                fileToSave = new java.io.File(fileToSave.getPath() + ".xml");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                writer.write("<SimulationParameters>\n");

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                String currentDateTime = dateFormat.format(new Date());
                writer.write("    <SimulationDateTime>" + currentDateTime + "</SimulationDateTime>\n");

                String title = "Charging Site Queueing Characteristics \n"
                        + this.getKendallName() + " Queueing System"
                        + " (" + getMAX_EVENTS() + " samples per evaluation point)";
                writer.write("    <SimulationTitle>" + title + "</SimulationTitle>\n");

                writer.write("        <GeneralParameters>\n");
                writer.write("        <NumberOfSimulationSteps>" + SIM_STEPS + "</NumberOfSimulationSteps>\n");
                writer.write("        <MaxEventsPerStep>" + MAX_EVENTS + "</MaxEventsPerStep>\n");
                writer.write("        <ConfidenceIntervalLevel>" + confLevel + "</ConfidenceIntervalLevel>\n");
                writer.write("    </GeneralParameters>\n");

                writer.write("        <SiteParameters>\n");
                writer.write("        <ArrivalDistributionType>" + ARRIVAL_TYPE.toString() + "</ArrivalDistributionType>\n");
                writer.write("        <MaxMeanArrivalRate>" + MAX_ARRIVAL_RATE + "</MaxMeanArrivalRate>\n");
                writer.write("        <ParkingSpace>" + QUEUE_SIZE + "</ParkingSpace>\n");
                writer.write("        <QueueingType>" + QUEUEING_TYPE.toString() + "</QueueingType>\n");
                writer.write("        <MaxSitePower>" + MAX_SITE_POWER + "</MaxSitePower>\n");
                writer.write("    </SiteParameters>\n");

                writer.write("        <ChargingParameters>\n");
                writer.write("        <NumberOfChargingPoints>" + NUMBER_OF_SERVERS + "</NumberOfChargingPoints>\n");
                writer.write("        <ServiceDistributionType>" + SERVICE_TYPE.toString() + "</ServiceDistributionType>\n");
                writer.write("        <MaxPowerOfChargingPoint>" + MAX_POINT_POWER + "</MaxPowerOfChargingPoint>\n");
                writer.write("        </ChargingParameters>\n");

                writer.write("        <EVParameters>\n");
                writer.write("        <NumberOfEVTypes>" + NUMBER_OF_CAR_TYPES + "</NumberOfEVTypes>\n");
                writer.write("        <BatteryCapacity>" + batteryCapacity + "</BatteryCapacity>\n");
                writer.write("         <MeanChargingTime>" + MEAN_SERVICE_TIME + "</MeanChargingTime>\n");
                writer.write("        <MaxEVChargingPower>" + MAX_EV_POWER + "</MaxEVChargingPower>\n");
                writer.write("        <DemandDistributionType>" + DEMAND_TYPE.toString() + "</DemandDistributionType>\n");
                writer.write("        <MeanChargingDemand>" + MEAN_CHARGING_DEMAND + "</MeanChargingDemand>\n");
                writer.write("        </EVParameters>\n");

                if (getNUMBER_OF_CAR_TYPES() == 2) {
                    writer.write("        <EVParameters2>\n");
                    writer.write("        <PercentageOfTheSecondCar>" + percentageOfCars2 + "</PercentageOfTheSecondCar>\n");
                    writer.write("        <BatteryCapacity2>" + batteryCapacity2 + "</BatteryCapacity2>\n");
                    writer.write("        <MeanChargingTime2>" + MEAN_SERVICE_TIME2 + "</MeanChargingTime2>\n");
                    writer.write("        <MaxEVChargingPower2>" + MAX_EV_POWER2 + "</MaxEVChargingPower2>\n");
                    writer.write("        <DemandDistributionType2>" + DEMAND_TYPE2.toString() + "</DemandDistributionType2>\n");
                    writer.write("        <MeanChargingDemand2>" + MEAN_CHARGING_DEMAND2 + "</MeanChargingDemand2>\n");
                    writer.write("        </EVParameters2>\n");
                } else if (getNUMBER_OF_CAR_TYPES() == 3) {
                    writer.write("        <EVParameters2>\n");
                    writer.write("        <PercentageOfTheSecondCar>" + percentageOfCars2 + "</PercentageOfTheSecondCar>\n");
                    writer.write("        <BatteryCapacity2>" + batteryCapacity2 + "</BatteryCapacity2>\n");
                    writer.write("        <MeanChargingTime2>" + MEAN_SERVICE_TIME2 + "</MeanChargingTime2>\n");
                    writer.write("        <MaxEVChargingPower2>" + MAX_EV_POWER2 + "</MaxEVChargingPower2>\n");
                    writer.write("        <DemandDistributionType2>" + DEMAND_TYPE2.toString() + "</DemandDistributionType2>\n");
                    writer.write("        <MeanChargingDemand2>" + MEAN_CHARGING_DEMAND2 + "</MeanChargingDemand2>\n");
                    writer.write("        </EVParameters2>\n");

                    writer.write("        <EVParameters3>\n");
                    writer.write("        <PercentageOfTheThirdCar>" + percentageOfCars3 + "</PercentageOfTheThirdCar>\n");
                    writer.write("        <BatteryCapacity3>" + batteryCapacity3 + "</BatteryCapacity3>\n");
                    writer.write("        <MeanChargingTime3>" + MEAN_SERVICE_TIME3 + "</MeanChargingTime3>\n");
                    writer.write("        <MaxEVChargingPower3>" + MAX_EV_POWER3 + "</MaxEVChargingPower3>\n");
                    writer.write("        <DemandDistributionType3>" + DEMAND_TYPE3.toString() + "</DemandDistributionType3>\n");
                    writer.write("        <MeanChargingDemand3>" + MEAN_CHARGING_DEMAND3 + "</MeanChargingDemand3>\n");
                    writer.write("        </EVParameters3>\n");

                }

                writer.write("</SimulationParameters>\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public enum Format {
        TXT, XML
    }

    // Method to save parameters in either TXT or XML format
    public static void writeParameters(JFrame frame, Format format) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose location to save simulation parameters");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        // Set default file name based on format
        String defaultFileName = "simulation_parameters" + (format == Format.XML ? ".xml" : ".txt");
        fileChooser.setSelectedFile(new java.io.File(defaultFileName));

        int userSelection = fileChooser.showSaveDialog(frame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getPath();

            // Ensure the file has the correct extension
            if (!filePath.toLowerCase().endsWith(format == Format.XML ? ".xml" : ".txt")) {
                fileToSave = new java.io.File(filePath + (format == Format.XML ? ".xml" : ".txt"));
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                if (format == Format.TXT) {
                    writeTxtFormat(writer); // Implement this method to write in TXT format
                } else if (format == Format.XML) {
                    writeXmlFormat(writer); // Implement this method to write in XML format
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    private static void writeTxtFormat(BufferedWriter writer) throws IOException {

    }

    private static void writeXmlFormat(BufferedWriter writer) throws IOException {
        // Example of writing in XML format
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        writer.write("<simulationParameters>\n");
        // More writing code here...
        writer.write("</simulationParameters>\n");
    }
}
