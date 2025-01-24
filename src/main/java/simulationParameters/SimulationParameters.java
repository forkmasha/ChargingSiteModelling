package simulationParameters;

import distributions.Distribution;
import distributions.DistributionType;
import queueingSystem.QueueingType;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;


public class SimulationParameters {

    private double minArrivalRate;
    private double maxArrivalRate;
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
    private QueueingType QUEUEING_TYPE;
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

    public SimulationParameters() {

    }

    public int getConfLevel() {
        return confLevel;
    }

    public void setConfLevel(int confLevel) {
        this.confLevel = confLevel;
    }

    public DistributionType getServiceType() {
        return SERVICE_TYPE;
    }

    public int getSteps() {
        return SIM_STEPS;
    }


    public void setMinimumArrivalRate(double MIN_ARRIVAL_RATE) {
        this.minArrivalRate = MIN_ARRIVAL_RATE;
    }

    public void setMaxArrivalRate(double MAX_ARRIVAL_RATE) {
        this.maxArrivalRate = MAX_ARRIVAL_RATE;
    }

    public double getArrivalRateStep() {
        return ARRIVAL_RATE_STEP;
    }

    public void setArrivalRateStep(double ARRIVAL_RATE_STEP) {
        this.ARRIVAL_RATE_STEP = ARRIVAL_RATE_STEP;
    }


    public void setSimulationSteps(int SIM_STEPS) {
        this.SIM_STEPS = SIM_STEPS;
    }

    public void setBatteryCapacity(double batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }

    public void setNumberOfCarTypes(int NUMBER_OF_CAR_TYPES) {
        this.NUMBER_OF_CAR_TYPES = NUMBER_OF_CAR_TYPES;
    }

    public void setMaxEvents(int MAX_EVENTS) {
        this.MAX_EVENTS = MAX_EVENTS;
    }

    public void setNumberOfServers(int NUMBER_OF_SERVERS) {
        this.NUMBER_OF_SERVERS = NUMBER_OF_SERVERS;
    }

    public void setQueueSize(int QUEUE_SIZE) {
        this.QUEUE_SIZE = QUEUE_SIZE;
    }

    public void setQueueingType(QueueingType QUEUEING_TYPE) {
        this.QUEUEING_TYPE = QUEUEING_TYPE;
    }

    public void setAverageServiceTime(double AVERAGE_SERVICE_TIME) {
        this.AVERAGE_SERVICE_TIME = AVERAGE_SERVICE_TIME;
    }

    public void setArrivalType(DistributionType ARRIVAL_TYPE) {
        this.ARRIVAL_TYPE = ARRIVAL_TYPE;
    }

    public void setDemandType(DistributionType DEMAND_TYPE) {
        this.DEMAND_TYPE = DEMAND_TYPE;
    }

    public void setServiceType(DistributionType type) {
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

    public void setDemandType3(DistributionType DEMAND_TYPE3) {
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

    public void setMeanServiceTime(double time) {
        if (time == 0) System.out.println("Warning: charging till 80% is yet not implemented correctly!");
        this.MEAN_SERVICE_TIME = time;
    }

    public void setMeanServiceTime2(double time) {
        this.MEAN_SERVICE_TIME2 = time;
    }

    public void setMeanServiceTime3(double time) {
        this.MEAN_SERVICE_TIME3 = time;
    }

    public int getNumberOfCarTypes() {
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

    public double getMeanServiceTime() {
        return MEAN_SERVICE_TIME;
    }

    public double getMeanServiceTime2() {
        return MEAN_SERVICE_TIME2;
    }

    public double getMeanServiceTime3() {
        return MEAN_SERVICE_TIME3;
    }

    public DistributionType getDemandType() {
        return DEMAND_TYPE;
    }

    public DistributionType getDemandType2() {
        return DEMAND_TYPE2;
    }

    public DistributionType getDemandType3() {
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

    public int getNUMBER_OF_SERVERS() {
        return NUMBER_OF_SERVERS;
    }

    public int getQUEUE_SIZE() {
        return QUEUE_SIZE;
    }

    public QueueingType getQUEUEING_TYPE() {
        return QUEUEING_TYPE;
    }

    public double getAVERAGE_SERVICE_TIME() {
        return AVERAGE_SERVICE_TIME;
    }

    public DistributionType getARRIVAL_TYPE() {
        return ARRIVAL_TYPE;
    }

    public int getMaxEvents() {
        return MAX_EVENTS;
    }

    public double getMinArrivalRate() {
        return minArrivalRate;
    }

    public double getMaxArrivalRate() {
        return maxArrivalRate;
    }

    public double getMaxSitePower() {
        return MAX_SITE_POWER;
    }

    public double getAvgServiceTime() {
        double p1, p2, p3;
        if (this.getNumberOfCarTypes() < 2) this.setAverageServiceTime(this.getMeanServiceTime());
        else if (this.getNumberOfCarTypes() < 3) {
            if (this.getPercentageOfCars2() > 1) p2 = this.getPercentageOfCars2() / 100;
            else p2 = this.getPercentageOfCars2();
            p1 = 1 - p2;
            this.setAverageServiceTime(
                    p1 * this.getMeanServiceTime()
                            + p2 * this.getMeanServiceTime2());
        } else {
            if (this.getPercentageOfCars2() > 1) p2 = this.getPercentageOfCars2() / 100;
            else p2 = this.getPercentageOfCars2();
            if (this.getPercentageOfCars3() > 1) p3 = this.getPercentageOfCars3() / 100;
            else p3 = this.getPercentageOfCars3();
            p1 = 1 - p2 - p3;
            this.setAverageServiceTime(
                    p1 * this.getMeanServiceTime()
                            + p2 * this.getMeanServiceTime2()
                            + p3 * this.getMeanServiceTime3());
        }
        return AVERAGE_SERVICE_TIME;
    }

    public String getKendallName() {
        return Distribution.getTitleAbbreviation(String.valueOf(ARRIVAL_TYPE)) + "/"
                + Distribution.getTitleAbbreviation(String.valueOf(SERVICE_TYPE)) + "/"
                + NUMBER_OF_SERVERS + "/" + (NUMBER_OF_SERVERS + QUEUE_SIZE);
    }
    public String getKendallNameForFile() {
        String kendallName = getKendallName();
        String kendallNameForFile = kendallName.replace("/", "-");
        return kendallNameForFile;
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

    public void saveParametersAsTxt(String filePath) {

        java.io.File fileToSave = new java.io.File(filePath);
        if (!fileToSave.getPath().toLowerCase().endsWith(".txt")) {
            fileToSave = new java.io.File(fileToSave.getPath() + ".txt");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd' T 'HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date());
            writer.write("Date and Time: " + currentDateTime + "\n");
            String title = "Charging Site Model Parameters for " + this.getKendallName() + " Queueing System \n";
            writer.write(title + "\n\n");

            writer.write("General parameters" + "\n");
            writer.write("Number of  simulation steps - " + SIM_STEPS + "\n");
            writer.write("Max Events per step - " + MAX_EVENTS + "\n");
            writer.write("Confidence interval level - " + confLevel + "\n");
            writer.write("Number of EV types - " + NUMBER_OF_CAR_TYPES + "\n");
            writer.newLine();

            writer.write("Site parameters" + "\n");
            writer.write("Arrival Distribution Type - " + ARRIVAL_TYPE.toString() + "\n");
            writer.write("Max Mean Arrival Rate - " + maxArrivalRate + "\n");
            writer.write("Parking space - " + QUEUE_SIZE + "\n");
            writer.write("Queueing Type - " + QUEUEING_TYPE.toString() + "\n");
            writer.write("Max Site Power - " + MAX_SITE_POWER + "\n");
            writer.newLine();

            writer.write("Charging parameters" + "\n");
            writer.write("Number of charging points " + NUMBER_OF_SERVERS + "\n");
            writer.write("Service Distribution Type - " + SERVICE_TYPE.toString() + "\n");
            writer.write("Max power of Charging Point" + MAX_POINT_POWER + "\n");
            writer.newLine();

            if (NUMBER_OF_CAR_TYPES > 1) {
                writer.write("Parameters for the first EV " + "\n");
                writer.write("Percentage of EV 1 - " + percentageOfCars * 100 + "\n");
            } else {
                writer.write("EV parameters" + "\n");
            }

            writer.write("Battery capacity - " + batteryCapacity + "\n");
            writer.write("Mean Charging Time - " + getMeanServiceTime() + "\n");
            writer.write("Max EV charging power - " + MAX_EV_POWER + "\n");
            writer.write("Demand distribution type - " + DEMAND_TYPE.toString() + "\n");
            writer.write("Mean charging demand - " + MEAN_CHARGING_DEMAND + "\n");
            writer.newLine();

            int selectedClientTypes = NUMBER_OF_CAR_TYPES;
            if (selectedClientTypes > 1) {
                writer.write("Parameters for the second EV" + "\n");
                writer.write("Percentage of EV 2 - " + percentageOfCars2 * 100 + "\n");
                writer.write("Battery capacity 2 - " + batteryCapacity2 + "\n");
                writer.write("Mean charging time 2 - " + MEAN_SERVICE_TIME2 + "\n");
                writer.write("Max EV charging power 2 - " + MAX_EV_POWER2 + "\n");
                writer.write("Demand distribution type 2 - " + DEMAND_TYPE2.toString() + "\n");
                writer.write("Mean charging demand 2 - " + MEAN_CHARGING_DEMAND2 + "\n");
                writer.newLine();
            }
            if (selectedClientTypes > 2) {
                writer.write("Parameters for the third EV" + "\n");
                writer.write("Percentage of EV 3 - " + percentageOfCars3 * 100 + "\n");
                writer.write("Battery capacity 3 - " + batteryCapacity3 + "\n");
                writer.write("Mean charging time 3 - " + MEAN_SERVICE_TIME3 + "\n");
                writer.write("Max EV charging power 3 - " + MAX_EV_POWER3 + "\n");
                writer.write("Demand distribution type 3 - " + DEMAND_TYPE3.toString() + "\n");
                writer.write("Mean charging demand 3 - " + MEAN_CHARGING_DEMAND3 + "\n");
                writer.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void saveParametersAsXml(String filePath) {
        File fileToSave = new java.io.File(filePath);
        if (!fileToSave.getPath().toLowerCase().endsWith(".xml")) {
            fileToSave = new java.io.File(fileToSave.getPath() + ".xml");
        }

        try (BufferedWriter w = new BufferedWriter(new FileWriter(fileToSave))) {
            XmlParametersBuilder xmlBuilder = new XmlParametersBuilder(w, "SimulationParameters");

            xmlBuilder.xmlStart()
                    .firstLevel("SimulationDateTime", new Date())
                    .firstLevel("SimulationTitle", "Charging Site Model Parameters for " + this.getKendallName() + " Queueing System")

                    .groupStart("GeneralParameters")
                    .childLevel("NumberOfSimulationSteps", SIM_STEPS)
                    .childLevel("MaxEventsPerStep", MAX_EVENTS)
                    .childLevel("ConfidenceIntervalLevel", confLevel)
                    .childLevel("NumberOfEVTypes", NUMBER_OF_CAR_TYPES)
                    .groupEnd()

                    .groupStart("SiteParameters")
                    .childLevel("ArrivalDistributionType", ARRIVAL_TYPE.toString())
                    .childLevel("MaxMeanArrivalRate", maxArrivalRate)
                    .childLevel("ParkingSpace", QUEUE_SIZE)
                    .childLevel("QueueingType", QUEUEING_TYPE.toString())
                    .childLevel("MaxSitePower", MAX_SITE_POWER)
                    .groupEnd()

                    .groupStart("ChargingParameters")
                    .childLevel("NumberOfChargingPoints", NUMBER_OF_SERVERS)
                    .childLevel("ServiceDistributionType", SERVICE_TYPE.toString())
                    .childLevel("MaxPowerOfChargingPoint", MAX_POINT_POWER)
                    .groupEnd()

                    .groupStart("EVParameters")
                    .childLevel("BatteryCapacity", batteryCapacity)
                    .childLevel("MeanChargingTime", MEAN_SERVICE_TIME)
                    .childLevel("MaxEVChargingPower", MAX_EV_POWER)
                    .childLevel("DemandDistributionType", DEMAND_TYPE.toString())
                    .childLevel("MeanChargingDemand", MEAN_CHARGING_DEMAND)
                    .groupEnd();

            if (getNumberOfCarTypes() >= 2) {
                xmlBuilder
                        .groupStart("EVParameters2")
                        .childLevel("PercentageOfTheSecondCar", percentageOfCars2 * 100)
                        .childLevel("BatteryCapacity2", batteryCapacity2)
                        .childLevel("MeanChargingTime2", MEAN_SERVICE_TIME2)
                        .childLevel("MaxEVChargingPower2", MAX_EV_POWER2)
                        .childLevel("DemandDistributionType2", DEMAND_TYPE2.toString())
                        .childLevel("MeanChargingDemand2", MEAN_CHARGING_DEMAND2)
                        .groupEnd();
            }

            if (getNumberOfCarTypes() == 3) {
                xmlBuilder
                        .groupStart("EVParameters3")
                        .childLevel("PercentageOfTheThirdCar", percentageOfCars3 * 100)
                        .childLevel("BatteryCapacity3", batteryCapacity3)
                        .childLevel("MeanChargingTime3", MEAN_SERVICE_TIME3)
                        .childLevel("MaxEVChargingPower3", MAX_EV_POWER3)
                        .childLevel("DemandDistributionType3", DEMAND_TYPE3.toString())
                        .childLevel("MeanChargingDemand3", MEAN_CHARGING_DEMAND3)
                        .groupEnd();
            }
            xmlBuilder.writeXmlEnd();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadParametersFromXml(String filePath){
        try {
            File file = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("SimulationParameters").item(0).getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    switch (element.getTagName()) {
                        case "GeneralParameters":
                            for(int j = 0; j < node.getChildNodes().getLength(); j++){
                                Element childElement = tryGetChildElem(node, j);
                                switch (childElement.getTagName()) {
                                    case "NumberOfSimulationSteps" -> setSimulationSteps(getInt(childElement));
                                    case "MaxEventsPerStep" -> setMaxEvents(getInt(childElement));
                                    case "ConfidenceIntervalLevel" -> setConfLevel(getInt(childElement));
                                    case "NumberOfEVTypes" -> setNumberOfCarTypes(getInt(childElement));
                                }
                            }
                            break;
                        case "SiteParameters":
                            for(int j = 0; j < node.getChildNodes().getLength(); j++){
                                Element childElement = tryGetChildElem(node, j);
                                switch (childElement.getTagName()) {
                                    case "ArrivalDistributionType" -> setArrivalType(getDistributionType(childElement));
                                    case "MaxMeanArrivalRate" -> setMaxArrivalRate(getDouble(childElement));
                                    case "ParkingSpace" -> setQueueSize(getInt(childElement));
                                    case "QueueingType" -> setQueueingType(QueueingType.valueOf(childElement.getTextContent()));
                                    case "MaxSitePower" -> setMaxSitePower(getInt(childElement));
                                }
                            }
                            break;
                        case "ChargingParameters":
                            for(int j = 0; j < node.getChildNodes().getLength(); j++){
                                Element childElement = tryGetChildElem(node, j);
                                switch (childElement.getTagName()) {
                                    case "NumberOfChargingPoints" -> setNumberOfServers(getInt(childElement));
                                    case "ServiceDistributionType" -> setServiceType(getDistributionType(childElement));
                                    case "MaxPowerOfChargingPoint" -> setMaxPointPower(getInt(childElement));
                                }
                            }
                            break;
                        case "EVParameters":
                            for(int j = 0; j < node.getChildNodes().getLength(); j++){
                                Element childElement = tryGetChildElem(node, j);
                                switch (childElement.getTagName()) {
                                    case "BatteryCapacity" -> setBatteryCapacity(getDouble(childElement));
                                    case "MeanChargingTime" -> setMeanServiceTime(getDouble(childElement));
                                    case "MaxEVChargingPower" -> setMaxEvPower(getInt(childElement));
                                    case "DemandDistributionType" -> setDemandType(getDistributionType(childElement));
                                    case "MeanChargingDemand" -> setMeanChargingDemand(getDouble(childElement));
                                }
                            }
                            break;
                        case "EVParameters2":
                            for(int j = 0; j < node.getChildNodes().getLength(); j++){
                                Element childElement = tryGetChildElem(node, j);
                                switch (childElement.getTagName()) {
                                    case "PercentageOfTheSecondCar" -> setPercentageOfCars2(getDouble(childElement) / 100);
                                    case "BatteryCapacity2" -> setBatteryCapacity2(getDouble(childElement));
                                    case "MeanChargingTime2" -> setMeanServiceTime2(getDouble(childElement));
                                    case "MaxEVChargingPower2" -> setMaxEvPower2(getInt(childElement));
                                    case "DemandDistributionType2" -> setDEMAND_TYPE2(getDistributionType(childElement));
                                    case "MeanChargingDemand2" -> setMeanChargingDemand2(getDouble(childElement));
                                }
                            }
                            break;
                        case "EVParameters3":
                            for(int j = 0; j < node.getChildNodes().getLength(); j++){
                                Element childElement = tryGetChildElem(node, j);
                                switch (childElement.getTagName()) {
                                    case "PercentageOfTheThirdCar" -> setPercentageOfCars3(getDouble(childElement) / 100);
                                    case "BatteryCapacity3" -> setBatteryCapacity3(getDouble(childElement));
                                    case "MeanChargingTime3" -> setMeanServiceTime3(getDouble(childElement));
                                    case "MaxEVChargingPower3" -> setMaxEvPower3(getInt(childElement));
                                    case "DemandDistributionType3" -> setDemandType3(getDistributionType(childElement));
                                    case "MeanChargingDemand3" -> setMeanChargingDemand3(getDouble(childElement));
                                }
                            }
                            break;
                    }
                }
            }

            ARRIVAL_RATE_STEP = (maxArrivalRate - minArrivalRate) / SIM_STEPS;
            minArrivalRate = ARRIVAL_RATE_STEP;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Element tryGetChildElem(Node node, int index) {
        Node childNode = node.getChildNodes().item(index);
        if (childNode.getNodeType() == Node.ELEMENT_NODE) {
            return (Element) childNode;
        }
        return (Element)node;
    }

    private DistributionType getDistributionType(Element childElement) {
        return DistributionType.valueOf(childElement.getTextContent());
    }

    private double getDouble(Element childElement) {
        return Double.parseDouble(childElement.getTextContent());
    }

    private int getInt(Element childElement) {
        return Integer.parseInt(childElement.getTextContent());
    }

    public enum Format {
        TXT, XML
    }

    public static void writeParameters(JFrame frame, Format format) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose location to save simulation parameters");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        String defaultFileName = "simulation_parameters" + (format == Format.XML ? ".xml" : ".txt");
        fileChooser.setSelectedFile(new java.io.File(defaultFileName));

        int userSelection = fileChooser.showSaveDialog(frame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getPath();

            if (!filePath.toLowerCase().endsWith(format == Format.XML ? ".xml" : ".txt")) {
                fileToSave = new java.io.File(filePath + (format == Format.XML ? ".xml" : ".txt"));
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                if (format == Format.TXT) {
                    writeTxtFormat(writer);
                } else if (format == Format.XML) {
                    writeXmlFormat(writer);
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