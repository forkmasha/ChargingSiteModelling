package chargingSite;

import distributions.DistributionType;
import queueingSystem.Queue;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SimulationGUI {
    private static final Color LIGHT_PINK = new Color(255, 182, 193);
    private static final Color LAVENDER = new Color(230, 230, 250);
    private static final Color PEACH = new Color(255, 218, 185);
    private static final Color BLUE = new Color(173, 216, 230);
    private static final Color LIGHT_BLUE = new Color(200, 200, 240);
    private static final Color DARK_BLUE = new Color(136, 186, 242);
    private static final Color ORANGE = new Color(255, 175, 128);
    private static final Color RED = new Color(255, 102, 102);


    private static final Color GREY = new Color(0, 0, 128);
    private static final Color DARK_GREY = new Color(0, 0, 80);
    private static final Color BLUE1 = new Color(50, 50, 50);
    private static final Color BLUE2 = new Color(80, 80, 80);

    private static Simulation simulation;
    private static SimulationParameters parameters;


    public static void runSimulationGUI() {
        JFrame frame = createSimulationFrame();
        addWindowCloseListener(frame);
        frame.setVisible(true);
    }

    private static JFrame createSimulationFrame() {
        JFrame frame = new JFrame("Charging Site Modeling");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //  frame.getContentPane().setBackground(LIGHT_BLUE);
        frame.setPreferredSize(new Dimension(350, 800)); //450 775
//        frame.setMinimumSize(new Dimension(450, 840));


        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();

        // Початкові значення для визначення найбільшого монітора
        Rectangle largestBounds = null;
        long maxArea = 0;

        // Визначення відступу в пікселях. Припустимо, 1 см ≈ 38 пікселів. Для відступу в 2 см = 76 пікселів.
        int leftMarginInPixels = 30;

        for (GraphicsDevice gd : gs) {
            Rectangle bounds = gd.getDefaultConfiguration().getBounds();
            long area = bounds.width * bounds.height;
            if (area > maxArea) {
                maxArea = area;
                largestBounds = bounds;
            }
        }

        if (largestBounds != null) {
            // Розміщення вікна на найбільшому моніторі з відступом
            int xPosition = largestBounds.x + leftMarginInPixels;
            int yPosition = largestBounds.y + (largestBounds.height - frame.getPreferredSize().height) / 2; // Центрування по вертикалі
            frame.setLocation(xPosition, yPosition);
        }


        //JSpinner minArrivalRate = createSpinner(0.5, 0.0, Double.MAX_VALUE, 0.1);
        JSpinner numberOfSteps = createSpinner(50, 0, Integer.MAX_VALUE, 1);
        JSpinner maxArrivalRate = createSpinner(25.0, 0.0, Double.MAX_VALUE, 0.1);

        JSpinner numberOfClientTypes = createSpinner(1, 1, 3, 1);

        JSpinner maxEvents = createSpinner(2500, 1, Integer.MAX_VALUE, 1);
        JSpinner numberOfServers = createSpinner(5, 1, Integer.MAX_VALUE, 1);
        JSpinner queueSize = createSpinner(10, 1, Integer.MAX_VALUE, 1);
        JSpinner meanServiceTime = createSpinner(0.5, 0.0, Double.MAX_VALUE, 0.1);
        JSpinner meanChargingDemand = createSpinner(0.8, 0.0, 1.0, 0.1);
        JSpinner batteryCapacity = createSpinner(60.0, 0.0, 200.0, 5);
        JSpinner maxEVPower = createSpinner(150, 1.0, Double.MAX_VALUE, 1);
        JSpinner percentageOfCars = createSpinner(100, 1, 100, 1);

        JSpinner percentageOfCars2 = createSpinner(50, 1, 100, 10);
        JSpinner meanServiceTime2 = createSpinner(0.75, 0.0, Double.MAX_VALUE, 0.1);
        JSpinner maxEVPower2 = createSpinner(250, 1.0, Double.MAX_VALUE, 1);
        JSpinner meanChargingDemand2 = createSpinner(0.7, 0.0, 1.0, 0.1);
        JSpinner batteryCapacity2 = createSpinner(90.0, 0.0, 200.0, 5);


        JSpinner percentageOfCars3 = createSpinner(30, 1, 100, 10);
        JSpinner meanServiceTime3 = createSpinner(0.25, 0.0, Double.MAX_VALUE, 0.1);
        JSpinner maxEVPower3 = createSpinner(50, 1.0, Double.MAX_VALUE, 1);
        JSpinner meanChargingDemand3 = createSpinner(0.9, 0.0, 1.0, 0.1);
        JSpinner batteryCapacity3 = createSpinner(15.0, 0.0, 200.0, 5);


        JSpinner maxSitePower = createSpinner(300, 1.0, Double.MAX_VALUE, 1);
        JSpinner maxPointPower = createSpinner(100, 1.0, Double.MAX_VALUE, 1);


        String[] queueingTypes = {"FIFO", "LIFO", "RANDOM"};
        JComboBox<String> queueingType = new JComboBox<>(queueingTypes);

        String[] arrivalDistributionTypes = {"DETERMINISTIC", "EXPONENTIAL", "ERLANG", "UNIFORM", "LOMAX"};
        String[] serviceDistributionTypes = {"DETERMINISTIC", "EXPONENTIAL", "ERLANG", "ERLANGD", "UNIFORM", "LOMAX"};
        String[] demandDistributionTypes = {"DETERMINISTIC", "UNIFORM", "BETA"};
        JComboBox<String> arrivalType = new JComboBox<>(arrivalDistributionTypes);
        JComboBox<String> serviceType = new JComboBox<>(serviceDistributionTypes);
        JComboBox<String> demandType = new JComboBox<>(demandDistributionTypes);

        JComboBox<String> demandType2 = new JComboBox<>(demandDistributionTypes);
        JComboBox<String> demandType3 = new JComboBox<>(demandDistributionTypes);

        arrivalType.setSelectedItem("EXPONENTIAL");
        serviceType.setSelectedItem("ERLANG");
        demandType.setSelectedItem("BETA");

        demandType2.setSelectedItem("BETA");
        demandType3.setSelectedItem("BETA");

        String[] confidenceLevels = {"80", "90", "95", "98", "99"};
        JComboBox<String> confLevel = new JComboBox<>(confidenceLevels);
        confLevel.setSelectedItem("95");

        queueingType.setUI(new CustomComboBoxUI());
        arrivalType.setUI(new CustomComboBoxUI());
        serviceType.setUI(new CustomComboBoxUI());
        demandType.setUI(new CustomComboBoxUI());
        demandType2.setUI(new CustomComboBoxUI());
        //confLevel.setUI(new CustomComboBoxUI()); //

        arrivalType.setBackground(Color.WHITE);
        serviceType.setBackground(Color.white);
        queueingType.setBackground(Color.white);
        demandType.setBackground(Color.white);
        demandType2.setBackground(Color.white);
        confLevel.setBackground(Color.WHITE); //


        JButton runSimulation = new JButton("Run Simulation");
        configureButton(runSimulation);

        JButton saveResults = new JButton("Save Results");
        configureButton(saveResults);
        saveResults.setVisible(false);


        runSimulation.addActionListener(e -> {

            double maxSitePowerValue = getSpinnerValueAsDouble(maxSitePower);
            double maxPointPowerValue = getSpinnerValueAsDouble(maxPointPower);
            double maxEVPowerValue = getSpinnerValueAsDouble(maxEVPower);
            double meanChargingDemandValue = getSpinnerValueAsDouble(meanChargingDemand);

            simulation = new Simulation();
            parameters = simulation.getParameters();

            //simulation.setMIN_ARRIVAL_RATE(getSpinnerValueAsDouble(minArrivalRate));
            //simulation.setARRIVAL_RATE_STEP((getSpinnerValueAsDouble(maxArrivalRate)-getSpinnerValueAsDouble(minArrivalRate))/(getSpinnerValueAsInt(numberOfSteps)-1));
            simulation.getParameters().setARRIVAL_RATE_STEP((getSpinnerValueAsDouble(maxArrivalRate) / getSpinnerValueAsInt(numberOfSteps)));
            simulation.getParameters().setMIN_ARRIVAL_RATE(simulation.getParameters().getARRIVAL_RATE_STEP());
            simulation.getParameters().setMAX_ARRIVAL_RATE(getSpinnerValueAsDouble(maxArrivalRate));
            ;
            //simulation.setSIM_STEPS((int) Math.ceil((simulation.getMAX_ARRIVAL_RATE() - simulation.getMIN_ARRIVAL_RATE()) / simulation.getARRIVAL_RATE_STEP()));
            simulation.getParameters().setSIM_STEPS(getSpinnerValueAsInt(numberOfSteps));
            simulation.getParameters().setNUMBER_OF_CAR_TYPES(getSpinnerValueAsInt(numberOfClientTypes));
            simulation.getParameters().setMAX_EVENTS(getSpinnerValueAsInt(maxEvents));
            simulation.getParameters().setNUMBER_OF_SERVERS(getSpinnerValueAsInt(numberOfServers));
            simulation.getParameters().setQUEUE_SIZE(getSpinnerValueAsInt(queueSize));

            simulation.getParameters().setMaxSitePower((int) maxSitePowerValue);     //REDO
            simulation.getParameters().setMaxPointPower((int) maxPointPowerValue);
            simulation.getParameters().setMaxEvPower((int) maxEVPowerValue);
            simulation.getParameters().setMeanChargingDemand(meanChargingDemandValue);

            simulation.getParameters().setBatteryCapacity(getSpinnerValueAsDouble(batteryCapacity));

            double maxEVPowerValue2 = getSpinnerValueAsDouble(maxEVPower);
            double meanChargingDemandValue2 = getSpinnerValueAsDouble(meanChargingDemand2);

            double maxEVPowerValue3 = getSpinnerValueAsDouble(maxEVPower);
            double meanChargingDemandValue3 = getSpinnerValueAsDouble(meanChargingDemand3);

            simulation.getParameters().setPercentageOfCars2(getSpinnerValueAsInt(percentageOfCars2));
            simulation.getParameters().setMaxEvPower2((int) maxEVPowerValue2);
            simulation.getParameters().setMeanChargingDemand2(meanChargingDemandValue2);
            simulation.getParameters().setBatteryCapacity2(getSpinnerValueAsDouble(batteryCapacity2));


            simulation.getParameters().setPercentageOfCars3(getSpinnerValueAsInt(percentageOfCars3));
            simulation.getParameters().setMaxEvPower3((int) maxEVPowerValue3);
            simulation.getParameters().setMeanChargingDemand3(meanChargingDemandValue3);
            simulation.getParameters().setBatteryCapacity3(getSpinnerValueAsDouble(batteryCapacity3));


            String queueingTypeString = (String) queueingType.getSelectedItem();
            switch (queueingTypeString) {
                case "FIFO" -> simulation.getParameters().setQUEUEING_TYPE(Queue.QueueingType.FIFO);
                case "LIFO" -> simulation.getParameters().setQUEUEING_TYPE(Queue.QueueingType.LIFO);
                case "RANDOM" -> simulation.getParameters().setQUEUEING_TYPE(Queue.QueueingType.RAND);
            }

            simulation.getParameters().setMEAN_SERVICE_TIME(getSpinnerValueAsDouble(meanServiceTime));
            simulation.getParameters().setMEAN_SERVICE_TIME2(getSpinnerValueAsDouble(meanServiceTime2));
            simulation.getParameters().setMEAN_SERVICE_TIME3(getSpinnerValueAsDouble(meanServiceTime3));

            String arrivalTypeString = (String) arrivalType.getSelectedItem();

            switch (arrivalTypeString) {
                case "GEOMETRIC" -> simulation.getParameters().setARRIVAL_TYPE(DistributionType.GEOMETRIC);
                case "EXPONENTIAL" -> simulation.getParameters().setARRIVAL_TYPE(DistributionType.EXPONENTIAL);
                case "ERLANG" -> simulation.getParameters().setARRIVAL_TYPE(DistributionType.ERLANG);
                case "ERLANGD" -> simulation.getParameters().setARRIVAL_TYPE(DistributionType.ERLANGD);
                case "UNIFORM" -> simulation.getParameters().setARRIVAL_TYPE(DistributionType.UNIFORM);
                case "BETA" -> simulation.getParameters().setARRIVAL_TYPE(DistributionType.BETA);
                case "DETERMINISTIC" -> simulation.getParameters().setARRIVAL_TYPE(DistributionType.DETERMINISTIC);
                case "LOMAX" -> simulation.getParameters().setARRIVAL_TYPE(DistributionType.LOMAX);
            }

            String serviceTypeString = (String) serviceType.getSelectedItem();
            switch (serviceTypeString) {
                case "GEOMETRIC" -> simulation.getParameters().setSERVICE_TYPE(DistributionType.GEOMETRIC);
                case "EXPONENTIAL" -> simulation.getParameters().setSERVICE_TYPE(DistributionType.EXPONENTIAL);
                case "ERLANG" -> simulation.getParameters().setSERVICE_TYPE(DistributionType.ERLANG);
                case "ERLANGD" -> simulation.getParameters().setSERVICE_TYPE(DistributionType.ERLANGD);
                case "UNIFORM" -> simulation.getParameters().setSERVICE_TYPE(DistributionType.UNIFORM);
                case "BETA" -> simulation.getParameters().setSERVICE_TYPE(DistributionType.BETA);
                case "DETERMINISTIC" -> simulation.getParameters().setSERVICE_TYPE(DistributionType.DETERMINISTIC);
                case "LOMAX" -> simulation.getParameters().setSERVICE_TYPE(DistributionType.LOMAX);
            }

            String demandTypeString = (String) demandType.getSelectedItem();
            switch (demandTypeString) {
                case "DETERMINISTIC" -> simulation.getParameters().setDEMAND_TYPE(DistributionType.DETERMINISTIC);
                case "GEOMETRIC" -> simulation.getParameters().setDEMAND_TYPE(DistributionType.GEOMETRIC);
                case "EXPONENTIAL" -> simulation.getParameters().setDEMAND_TYPE(DistributionType.EXPONENTIAL);
                case "ERLANG" -> simulation.getParameters().setDEMAND_TYPE(DistributionType.ERLANG);
                case "ERLANGD" -> simulation.getParameters().setDEMAND_TYPE(DistributionType.ERLANGD);
                case "UNIFORM" -> simulation.getParameters().setDEMAND_TYPE(DistributionType.UNIFORM);
                case "BETA" -> simulation.getParameters().setDEMAND_TYPE(DistributionType.BETA);
                case "LOMAX" -> simulation.getParameters().setDEMAND_TYPE(DistributionType.LOMAX);
            }

            String demandTypeString2 = (String) demandType2.getSelectedItem();
            switch (demandTypeString2) {
                case "DETERMINISTIC" -> simulation.getParameters().setDEMAND_TYPE2(DistributionType.DETERMINISTIC);
                case "GEOMETRIC" -> simulation.getParameters().setDEMAND_TYPE2(DistributionType.GEOMETRIC);
                case "EXPONENTIAL" -> simulation.getParameters().setDEMAND_TYPE2(DistributionType.EXPONENTIAL);
                case "ERLANG" -> simulation.getParameters().setDEMAND_TYPE2(DistributionType.ERLANG);
                case "ERLANGD" -> simulation.getParameters().setDEMAND_TYPE2(DistributionType.ERLANGD);
                case "UNIFORM" -> simulation.getParameters().setDEMAND_TYPE2(DistributionType.UNIFORM);
                case "BETA" -> simulation.getParameters().setDEMAND_TYPE2(DistributionType.BETA);
                case "LOMAX" -> simulation.getParameters().setDEMAND_TYPE2(DistributionType.LOMAX);
            }

            String demandTypeString3 = (String) demandType3.getSelectedItem();
            switch (demandTypeString3) {
                case "DETERMINISTIC" -> simulation.getParameters().setDEMAND_TYPE3(DistributionType.DETERMINISTIC);
                case "GEOMETRIC" -> simulation.getParameters().setDEMAND_TYPE3(DistributionType.GEOMETRIC);
                case "EXPONENTIAL" -> simulation.getParameters().setDEMAND_TYPE3(DistributionType.EXPONENTIAL);
                case "ERLANG" -> simulation.getParameters().setDEMAND_TYPE3(DistributionType.ERLANG);
                case "ERLANGD" -> simulation.getParameters().setDEMAND_TYPE3(DistributionType.ERLANGD);
                case "UNIFORM" -> simulation.getParameters().setDEMAND_TYPE3(DistributionType.UNIFORM);
                case "BETA" -> simulation.getParameters().setDEMAND_TYPE3(DistributionType.BETA);
                case "LOMAX" -> simulation.getParameters().setDEMAND_TYPE3(DistributionType.LOMAX);
            }

            int selectedConfidenceLevel = Integer.parseInt((String) confLevel.getSelectedItem());
            simulation.setConfLevel(selectedConfidenceLevel);


            // frame.dispose(); //fix the bug=)

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Виберіть місце для збереження файлу параметрів симуляції");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fileChooser.setSelectedFile(new java.io.File("simulation_parameters.txt"));

            int userSelection = fileChooser.showSaveDialog(frame);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                if (!fileToSave.getPath().toLowerCase().endsWith(".txt")) {
                    fileToSave = new java.io.File(fileToSave.getPath() + ".txt");
                }

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {

                    writer.write("General parameters" + "\n");
                    writer.write("Number of  Simulation steps - " + getSpinnerValueAsDouble(numberOfSteps) + "\n");
                    writer.write("Max Events per step - " + getSpinnerValueAsDouble(maxEvents) + "\n");
                    writer.write("Confidence interval level - " + (String) confLevel.getSelectedItem() + "\n");


                    writer.write("Arrival Distribution Type - " + arrivalTypeString + "\n");
                    writer.write("Max Mean Arrival Rate - " + getSpinnerValueAsDouble(maxArrivalRate) + "\n");
                    writer.write("Number of servers  - " + getSpinnerValueAsDouble(numberOfServers) + "\n");
                    writer.write("Queue size - " + getSpinnerValueAsDouble(queueSize) + "\n");
                    writer.write("Queueing Type - " + queueingTypeString + "\n");
                    writer.write("Service Distribution Type - " + serviceTypeString + "\n");
                    writer.write("Number of client types - " + getSpinnerValueAsDouble(numberOfClientTypes) + "\n");
                    writer.write("Demand distribution type - " + demandTypeString + "\n");
                    writer.write("Mean charging demand - " + getSpinnerValueAsDouble(meanChargingDemand) + "\n");
                    writer.write("Battery capacity - " + getSpinnerValueAsDouble(batteryCapacity) + "\n");
                    writer.write("Max site power - " + getSpinnerValueAsDouble(maxSitePower) + "\n");
                    writer.write("Max point power - " + getSpinnerValueAsDouble(maxPointPower) + "\n");
                    writer.write("Max EV power - " + getSpinnerValueAsDouble(maxEVPower) + "\n");

                    int selectedClientTypes = getSpinnerValueAsInt(numberOfClientTypes);
                    if (selectedClientTypes == 2) {

                        writer.newLine();
                        writer.write("Parameters for second car" + "\n");

                        writer.write("Percentage of cars 2 - " + getSpinnerValueAsDouble(percentageOfCars2) + "\n");
                        writer.write("Mean service time 2 - " + getSpinnerValueAsDouble(meanServiceTime2) + "\n");
                        writer.write("Max EV power 2 - " + getSpinnerValueAsDouble(maxEVPower2) + "\n");
                        writer.write("Demand distribution type 2 - " + demandTypeString2 + "\n");
                        writer.write("Mean charging demand 2 - " + getSpinnerValueAsDouble(meanChargingDemand2) + "\n");
                        writer.write("Battery capacity 2 - " + getSpinnerValueAsDouble(batteryCapacity2) + "\n");
                    } else if (selectedClientTypes == 3) {
                        writer.newLine();
                        writer.write("Parameters for second car" + "\n");

                        writer.write("Percentage of cars 2 - " + getSpinnerValueAsDouble(percentageOfCars2) + "\n");
                        writer.write("Mean service time 2 - " + getSpinnerValueAsDouble(meanServiceTime2) + "\n");
                        writer.write("Max EV power 2 - " + getSpinnerValueAsDouble(maxEVPower2) + "\n");
                        writer.write("Demand distribution type 2 - " + demandTypeString2 + "\n");
                        writer.write("Mean charging demand 2 - " + getSpinnerValueAsDouble(meanChargingDemand2) + "\n");
                        writer.write("Battery capacity 2 - " + getSpinnerValueAsDouble(batteryCapacity2) + "\n");

                        writer.newLine();
                        writer.write("Parameters for third car" + "\n");

                        writer.write("Percentage of cars 3 - " + getSpinnerValueAsDouble(percentageOfCars3) + "\n");
                        writer.write("Mean service time 3 - " + getSpinnerValueAsDouble(meanServiceTime3) + "\n");
                        writer.write("Max EV power 3 - " + getSpinnerValueAsDouble(maxEVPower3) + "\n");
                        writer.write("Demand distribution type 3 - " + demandTypeString3 + "\n");
                        writer.write("Mean charging demand 3 - " + getSpinnerValueAsDouble(meanChargingDemand3) + "\n");
                        writer.write("Battery capacity 3 - " + getSpinnerValueAsDouble(batteryCapacity3) + "\n");
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            parameters.writeParameters2txt(frame);
            simulation.runSimulation();
            saveResults.setVisible(true);

        });


        saveResults.addActionListener(e -> {
            String[] options = {
                    "Save Charging Site Queueing Characteristics",
                    "Charging Site Energy Characteristics",
                    "Power vs Time Chart",
                    "Site Power Distribution Histogram",
                    "GUI Parameters",
                    "All Results"
            };
            JList<String> optionList = new JList<>(options);
            optionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            optionList.setLayoutOrientation(JList.VERTICAL);
            optionList.setVisibleRowCount(options.length);

            // Використання JOptionPane для відображення JList
            JOptionPane.showMessageDialog(
                    frame, // frame - ваш JFrame
                    new JScrollPane(optionList), // Обгорнути JList в JScrollPane
                    "Save Parameters", // Заголовок
                    JOptionPane.PLAIN_MESSAGE
            );

            // Отримання вибору користувача
            int selectedIndex = optionList.getSelectedIndex();
            if (selectedIndex != -1) {
                switch (selectedIndex) {
                    case 0:
                        String[] formats = {"csv", "svg"};
                        String selectedFormat = (String) JOptionPane.showInputDialog(
                                frame, // ваш JFrame
                                "Choose the format to save the graph:", // текст запитання
                                "Save Format", // заголовок вікна
                                JOptionPane.QUESTION_MESSAGE,
                                null, // без іконки
                                formats, // опції для вибору
                                formats[0] // вибір за замовчуванням
                        );

                        // Перевірка вибору користувача та виконання відповідної логіки
                        if (selectedFormat != null) {
                            if ("csv".equals(selectedFormat)) {
                                simulation.saveGraphDataToCSV("D:\\masha1.csv");

                                // Логіка збереження в форматі CSV

                            } else if ("svg".equals(selectedFormat)) {
                                // Логіка збереження в форматі SVG
                                File svgFile = new File("D:\\sim.svg");                        ///

                                try {
                                    simulation.saveAsSVG(200, 200, svgFile);
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        }
                        break;
                    case 1:
                        // Логіка для збереження Charging Site Energy Characteristics

                        break;
                    case 2:
                        // Логіка для збереження Power vs Time Chart
                        break;
                    case 3:
                        // Логіка для збереження Site Power Distribution Histogram
                        break;
                    case 4:
                        String[] formats1 = {"txt", "xml"};
                        String selectedFormat1 = (String) JOptionPane.showInputDialog(
                                frame, // ваш JFrame
                                "Choose the format to save the result:", // текст запитання
                                "Save Format", // заголовок вікна
                                JOptionPane.QUESTION_MESSAGE,
                                null, // без іконки
                                formats1, // опції для вибору
                                formats1[0] // вибір за замовчуванням
                        );

                        if (selectedFormat1 != null) {
                            if ("txt".equals(selectedFormat1)) {
                                //SimulationParameters parameters = new SimulationParameters();

                                parameters.writeParameters2txt(frame);

                            } else if ("xml".equals(selectedFormat1)) {
                                parameters.writeParameters2xml(frame);
                            }
                        }
                        break;
                }
            } else {
                // Користувач не вибрав опцію
            }
        });

        runSimulation.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        //setSpinnerModel(minArrivalRate);
        setSpinnerModel(numberOfSteps);
        setSpinnerModel(maxArrivalRate);
        setSpinnerModel(numberOfClientTypes);
        setSpinnerModel(maxEvents);
        setSpinnerModel(numberOfServers);
        setSpinnerModel(queueSize);
        setSpinnerModel(meanServiceTime);
        setSpinnerModel(maxSitePower);
        setSpinnerModel(maxPointPower);
        setSpinnerModel(maxEVPower);
        setSpinnerModel(meanChargingDemand);
        setSpinnerModel(batteryCapacity);
        setSpinnerModel(percentageOfCars);
        setSpinnerModel(percentageOfCars2);
        setSpinnerModel(meanServiceTime2);
        setSpinnerModel(maxEVPower2);
        setSpinnerModel(meanChargingDemand2);
        setSpinnerModel(batteryCapacity2);
        setSpinnerModel(percentageOfCars3);
        setSpinnerModel(meanServiceTime3);
        setSpinnerModel(maxEVPower3);
        setSpinnerModel(meanChargingDemand3);
        setSpinnerModel(batteryCapacity3);

        Box verticalBox = Box.createVerticalBox();
        verticalBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel procPanel = new JPanel();
        procPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        //  TitledBorder titledBorder = BorderFactory.createTitledBorder("General Parameters");
        //     procPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), titledBorder));
        JScrollPane jScrollPane = new JScrollPane(procPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setMaximumSize(new Dimension(430, 550));
        numberOfClientTypes.addChangeListener(new ChangeListener() {
            @Override
                public void stateChanged(ChangeEvent e) {
                int selectedClientTypes = getSpinnerValueAsInt(numberOfClientTypes);
                if (isThirdCarPanelAdded(procPanel)) {
                    removeThirdCarPanel(procPanel);
                }

                if (selectedClientTypes == 1) {
                    if (isSecondCarPanelAdded(procPanel)) {
                        removeSecondCarPanel(procPanel);
                    }
                } else if (selectedClientTypes == 2) {
                    if (isThirdCarPanelAdded(procPanel)) {
                        removeThirdCarPanel(procPanel);
                    }


                    JPanel secondCarPanel = createSecondCarPanel(percentageOfCars2, meanServiceTime2, maxEVPower2, meanChargingDemand2, demandType2, batteryCapacity2);
                    Dimension panelSize = new Dimension(240, 420);
                    secondCarPanel.setPreferredSize(panelSize);
                    secondCarPanel.setMinimumSize(panelSize);
                    secondCarPanel.setMaximumSize(panelSize);
                    GridBagConstraints secondCarGbc = new GridBagConstraints();
                    secondCarGbc.anchor = GridBagConstraints.WEST;
                    secondCarGbc.insets = new Insets(5, 5, 5, 5);
                    secondCarGbc.gridx = 0;
                    secondCarGbc.gridy = 28;
                    procPanel.add(secondCarPanel, secondCarGbc);
                    procPanel.revalidate();
                    procPanel.repaint();

                } else if (selectedClientTypes == 3) {

                    JPanel secondCarPanel = createSecondCarPanel(percentageOfCars2, meanServiceTime2, maxEVPower2, meanChargingDemand2, demandType2, batteryCapacity2);
                    Dimension panelSize = new Dimension(240, 420);
                    secondCarPanel.setPreferredSize(panelSize);
                    secondCarPanel.setMinimumSize(panelSize);
                    secondCarPanel.setMaximumSize(panelSize);

                    GridBagConstraints secondCarGbc = new GridBagConstraints();
                    secondCarGbc.anchor = GridBagConstraints.WEST;
                    secondCarGbc.insets = new Insets(5, 5, 5, 5);
                    secondCarGbc.gridx = 0;
                    secondCarGbc.gridy = 28;
                    procPanel.add(secondCarPanel, secondCarGbc);

                    JPanel thirdCarPanel = createThirdCarPanel(percentageOfCars3, meanServiceTime3, maxEVPower3, meanChargingDemand3, demandType3, batteryCapacity3);


                    GridBagConstraints thirdCarGbc = new GridBagConstraints();
                    thirdCarGbc.anchor = GridBagConstraints.WEST;
                    thirdCarGbc.insets = new Insets(5, 5, 5, 5);
                    thirdCarGbc.gridx = 0;
                    thirdCarGbc.gridy = 35;
                    procPanel.add(thirdCarPanel, thirdCarGbc);

                    procPanel.revalidate();
                    procPanel.repaint();
                }
            }
        });


        TitledBorder generalBorder = BorderFactory.createTitledBorder("General parameters");
        JPanel generalPanel = new JPanel(new GridBagLayout());
        generalPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), generalBorder));
        generalPanel.setBorder(generalBorder);
        generalBorder.setTitleColor(Color.BLUE);

        Dimension panelSize = new Dimension(240, 210);
        generalPanel.setPreferredSize(panelSize);
        generalPanel.setMinimumSize(panelSize);
        generalPanel.setMaximumSize(panelSize);
        generalPanel.addMouseListener(new MouseAdapter() {
            private boolean isParametersShown = true;

            public void mouseClicked(MouseEvent e) {
                if (isParametersShown) {
                    generalPanel.removeAll();
                    JLabel label = new JLabel("General Parameters");
                    label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                    label.setForeground(Color.BLACK);
                    generalPanel.add(label);

                    generalPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                    isParametersShown = false;
                    generalPanel.setPreferredSize(new Dimension(240, 45));
                    generalPanel.revalidate();
                    generalPanel.repaint();
                } else {
                    generalPanel.removeAll();

                    addRowToPanel(generalPanel, gbc, "Number of Simulation Steps", numberOfSteps);
                    addRowToPanel(generalPanel, gbc, "Max Events per Step", maxEvents);
                    addRowToPanel(generalPanel, gbc, "Confidence Interval Level [%]", confLevel);

                    TitledBorder generalBorder = BorderFactory.createTitledBorder("General parameters");
                    generalPanel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(10, 10, 10, 10),
                            generalBorder));
                    generalBorder.setTitleColor(Color.BLUE);

                    isParametersShown = true;
                    generalPanel.setPreferredSize(new Dimension(240, 210));
                    generalPanel.revalidate();
                    generalPanel.repaint();

                }
                generalPanel.revalidate();
                generalPanel.repaint();
            }
        });

        generalPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                generalBorder));

        generalBorder.setTitle("<html><body>&nbsp;<b><font color='blue'>General Parameters</font></b></body></html>");

        addRowToPanel(generalPanel, gbc, "Number of Simulation Steps", numberOfSteps);
        addRowToPanel(generalPanel, gbc, "Max Events per Step", maxEvents);
        addRowToPanel(generalPanel, gbc, "Confidence Interval Level [%]", confLevel);

        TitledBorder siteBorder = BorderFactory.createTitledBorder("Site Parameters");
        JPanel sitePanel = new JPanel(new GridBagLayout());
        sitePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), siteBorder));

        sitePanel.addMouseListener(new MouseAdapter() {
            private boolean isParametersShown = true;

            public void mouseClicked(MouseEvent e) {
                if (isParametersShown) {
                    sitePanel.removeAll();
                    JLabel label = new JLabel("Site Parameters");
                    label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                    label.setForeground(Color.BLACK);
                    sitePanel.add(label);
                    sitePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    isParametersShown = false;
                    sitePanel.setPreferredSize(new Dimension(240, 45));
                } else {
                    sitePanel.removeAll();

                    addRowToPanel(sitePanel, gbc, "Arrival Distribution Type", arrivalType);
                    addRowToPanel(sitePanel, gbc, "Max Mean Arrival Rate [EV/h]", maxArrivalRate);
                    addRowToPanel(sitePanel, gbc, "Parking Space [EV]", queueSize);
                    addRowToPanel(sitePanel, gbc, "Queueing Type", queueingType);
                    addRowToPanel(sitePanel, gbc, "Max Site Power [kW]", maxSitePower);

                    TitledBorder tempBorder = BorderFactory.createTitledBorder("Site Parameters");
                    sitePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), tempBorder));
                    tempBorder.setTitleColor(Color.BLUE);

                    isParametersShown = true;
                    sitePanel.setPreferredSize(new Dimension(240, 350));
                }
                sitePanel.revalidate();
                sitePanel.repaint();
            }
        });

        sitePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), siteBorder));
        siteBorder.setTitle("<html><body>&nbsp;<b><font color='blue'>Site Parameters</font></b></body></html>");

        addRowToPanel(sitePanel, gbc, "Arrival Distribution Type", arrivalType);
        addRowToPanel(sitePanel, gbc, "Max Mean Arrival Rate [EV/h]", maxArrivalRate);
        addRowToPanel(sitePanel, gbc, "Parking Space [EV]", queueSize);
        addRowToPanel(sitePanel, gbc, "Queueing Type", queueingType);
        addRowToPanel(sitePanel, gbc, "Max Site Power [kW]", maxSitePower);


        TitledBorder chargingParameters = BorderFactory.createTitledBorder("Charging Parameters");
        JPanel chargingParametersPanel = new JPanel(new GridBagLayout());

        Dimension chargingPanelSize = new Dimension(240, 210);
        chargingParametersPanel.setPreferredSize(chargingPanelSize);
        chargingParametersPanel.setMinimumSize(chargingPanelSize);
        chargingParametersPanel.setMaximumSize(chargingPanelSize);

        chargingParameters.setTitleColor(Color.BLUE);
        chargingParameters.setTitle("<html><body>&nbsp;<b><font color='blue'>Charging Parameters</font></b></body></html>");
        chargingParametersPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), chargingParameters));

        chargingParametersPanel.addMouseListener(new MouseAdapter() {
            private boolean isParametersShown = true;
            public void mouseClicked(MouseEvent e) {
                if (isParametersShown) {
                    chargingParametersPanel.removeAll();
                    JLabel label = new JLabel("Charging Parameters");
                    label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                    label.setForeground(Color.BLACK);
                    chargingParametersPanel.add(label);
                    chargingParametersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    isParametersShown = false;
                    chargingParametersPanel.setPreferredSize(new Dimension(240, 45));
                } else {
                    chargingParametersPanel.removeAll();

                    addRowToPanel(chargingParametersPanel, gbc, "Number of Charging Points", numberOfServers);
                    addRowToPanel(chargingParametersPanel, gbc, "Service Distribution Type", serviceType);
                    addRowToPanel(chargingParametersPanel, gbc, "Max Power of Charging Point [kW]", maxPointPower);

                    TitledBorder tempBorder = BorderFactory.createTitledBorder("Charging Parameters");
                    tempBorder.setTitleColor(Color.BLUE);
                    chargingParametersPanel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(10, 10, 10, 10), tempBorder));

                    isParametersShown = true;
                    chargingParametersPanel.setPreferredSize(chargingPanelSize);
                }
                chargingParametersPanel.revalidate();
                chargingParametersPanel.repaint();
            }
        });

        addRowToPanel(chargingParametersPanel, gbc, "Number of Charging Points", numberOfServers);
        addRowToPanel(chargingParametersPanel, gbc, "Service Distribution Type", serviceType);
        addRowToPanel(chargingParametersPanel, gbc, "Max Power of Charging Point [kW]", maxPointPower);


        TitledBorder EVParameters = BorderFactory.createTitledBorder("EV Parameters");
        JPanel EVParametersPanel = new JPanel(new GridBagLayout());

        Dimension evPanelSize = new Dimension(240, 420);
        EVParametersPanel.setPreferredSize(evPanelSize);
        EVParametersPanel.setMinimumSize(evPanelSize);
        EVParametersPanel.setMaximumSize(evPanelSize);

        EVParameters.setTitleColor(Color.BLUE);
        EVParameters.setTitle("<html><body>&nbsp;<b><font color='blue'>EV Parameters</font></b></body></html>");
        EVParametersPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), EVParameters));

        EVParametersPanel.addMouseListener(new MouseAdapter() {
            private boolean isParametersShown = true;

            public void mouseClicked(MouseEvent e) {
                if (isParametersShown) {
                    EVParametersPanel.removeAll();
                    JLabel label = new JLabel("EV Parameters");
                    label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                    label.setForeground(Color.BLACK);
                    EVParametersPanel.add(label);

                    EVParametersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                    isParametersShown = false;
                    EVParametersPanel.setPreferredSize(new Dimension(240, 45));
                } else {
                    EVParametersPanel.removeAll();

                    addRowToPanel(EVParametersPanel, gbc, "Number of EV Types", numberOfClientTypes);
                    addRowToPanel(EVParametersPanel, gbc, "Battery Capacity [kWh]", batteryCapacity);
                    addRowToPanel(EVParametersPanel, gbc, "Mean Charging Time [h]", meanServiceTime);
                    addRowToPanel(EVParametersPanel, gbc, "Max EV Charging Power [kW]", maxEVPower);
                    addRowToPanel(EVParametersPanel, gbc, "Demand Distribution Type", demandType);
                    addRowToPanel(EVParametersPanel, gbc, "Mean Charging Demand [% of battery]", meanChargingDemand);


                    TitledBorder tempBorder = BorderFactory.createTitledBorder("EV Parameters");
                    tempBorder.setTitleColor(Color.BLUE);
                    EVParametersPanel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(10, 10, 10, 10), tempBorder));

                    isParametersShown = true;
                    EVParametersPanel.setPreferredSize(evPanelSize);
                }
                EVParametersPanel.revalidate();
                EVParametersPanel.repaint();
            }
        });

        addRowToPanel(EVParametersPanel, gbc, "Number of EV Types", numberOfClientTypes);
        addRowToPanel(EVParametersPanel, gbc, "Battery Capacity [kWh]", batteryCapacity);
        addRowToPanel(EVParametersPanel, gbc, "Mean Charging Time [h]", meanServiceTime);
        addRowToPanel(EVParametersPanel, gbc, "Max EV Charging Power [kW]", maxEVPower);
        addRowToPanel(EVParametersPanel, gbc, "Demand Distribution Type", demandType);
        addRowToPanel(EVParametersPanel, gbc, "Mean Charging Demand [% of battery]", meanChargingDemand);

        GridBagConstraints generalGbc = new GridBagConstraints();
        generalGbc.anchor = GridBagConstraints.WEST;
        generalGbc.insets = new Insets(5, 5, 5, 5);
        generalGbc.gridx = 0;

        procPanel.add(generalPanel, generalGbc);

        GridBagConstraints sitePowerGbc = new GridBagConstraints();
        sitePowerGbc.anchor = GridBagConstraints.WEST;
        sitePowerGbc.insets = new Insets(5, 5, 5, 5);
        sitePowerGbc.gridx = 0;
        procPanel.add(sitePanel, sitePowerGbc);

        GridBagConstraints chargingParametersGbc = new GridBagConstraints();
        chargingParametersGbc.anchor = GridBagConstraints.WEST;
        chargingParametersGbc.insets = new Insets(5, 5, 5, 5);
        chargingParametersGbc.gridx = 0;
        procPanel.add(chargingParametersPanel, chargingParametersGbc);

        GridBagConstraints EVParametersGbc = new GridBagConstraints();
        EVParametersGbc.anchor = GridBagConstraints.WEST;
        EVParametersGbc.insets = new Insets(5, 5, 5, 5);
        EVParametersGbc.gridx = 0;
        procPanel.add(EVParametersPanel, EVParametersGbc);

        procPanel.setBackground(LIGHT_BLUE);
        verticalBox.add(jScrollPane);


        JPanel bottomPanel = new JPanel();
        runSimulation.setForeground(Color.BLACK);
        bottomPanel.setBackground(DARK_BLUE);
        bottomPanel.setLayout(new GridLayout(1, 2));
        bottomPanel.add(runSimulation);
        bottomPanel.add(saveResults);

        frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        frame.getContentPane().add(verticalBox, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(true);
        return frame;
    }


    private static boolean isSecondCarPanelAdded(JPanel panel) {
        Component[] components = panel.getComponents();
        for (Component component : components) {
            if (component instanceof JPanel && ((JPanel) component).getComponentCount() > 0) {
                return true;
            }
        }
        return false;
    }

    private static void removeSecondCarPanel(JPanel panel) {
        Component[] components = panel.getComponents();
        for (Component component : components) {
            if (component instanceof JPanel && ((JPanel) component).getComponentCount() > 0) {
                panel.remove(component);
                panel.revalidate();
                panel.repaint();
                return;
            }
        }
    }

    private static JPanel createThirdCarPanel(JSpinner percentageOfCars3, JSpinner meanServiceTime3, JSpinner
            maxEVPower3, JSpinner meanChargingDemand3, JComboBox<String> demandType3, JSpinner batteryCapacity3) {
        JPanel thirdCarPanel = new JPanel();
        thirdCarPanel.setLayout(new GridBagLayout());
        thirdCarPanel.setBackground(LIGHT_PINK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // TitledBorder titledBorder = BorderFactory.createTitledBorder("Parameters for third car type");
        // thirdCarPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), titledBorder));

        addRowToPanel(thirdCarPanel, gbc, "Percentage of Cars 3", percentageOfCars3);
        addRowToPanel(thirdCarPanel, gbc, "Mean Service Time 3", meanServiceTime3);
        addRowToPanel(thirdCarPanel, gbc, "Max EV Power 3", maxEVPower3);
        // addRowToPanel(thirdCarPanel, gbc, "Demand Distribution Type 3", createDemandTypeComboBox());
        addRowToPanel(thirdCarPanel, gbc, "Demand Distribution Type 3", demandType3);
        addRowToPanel(thirdCarPanel, gbc, "Mean Charging Demand 3", meanChargingDemand3);
        addRowToPanel(thirdCarPanel, gbc, "Battery Capacity 3", batteryCapacity3);

        return thirdCarPanel;
    }

    private static void addRowToPanel(JPanel panel, GridBagConstraints gbc, String labelText, JComponent component) {
        gbc.gridx = 0;
        gbc.gridy++;

        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 0, 5);

        JLabel label = new JLabel(labelText, SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(200, 20));
        panel.add(label, gbc);

        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.insets = new Insets(5, 5, 5, 5);

        component.setPreferredSize(new Dimension(200, 20));
        panel.add(component, gbc);
    }


    private static boolean isThirdCarPanelAdded(JPanel panel) {
        Component[] components = panel.getComponents();
        for (Component component : components) {
            if (component instanceof JPanel && ((JPanel) component).getComponentCount() > 0) {
                return true;
            }
        }
        return false;
    }

    private static void removeThirdCarPanel(JPanel panel) {
        Component[] components = panel.getComponents();
        for (Component component : components) {
            if (component instanceof JPanel && ((JPanel) component).getComponentCount() > 0) {
                panel.remove(component);
                panel.revalidate();
                panel.repaint();
                return;
            }
        }
    }

    private static JPanel createSecondCarPanel(JSpinner percentageOfCars2, JSpinner meanServiceTime2, JSpinner
            maxEVPower2, JSpinner meanChargingDemand2, JComboBox<String> demandType2, JSpinner batteryCapacity2) {
        JPanel secondCarPanel = new JPanel();
        secondCarPanel.setLayout(new GridBagLayout());
        secondCarPanel.setBackground(PEACH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        addRowToPanel(secondCarPanel, gbc, "Percentage of Cars 2", percentageOfCars2);
        addRowToPanel(secondCarPanel, gbc, "Mean Service Time 2", meanServiceTime2);
        addRowToPanel(secondCarPanel, gbc, "Max EV Power 2", maxEVPower2);
        addRowToPanel(secondCarPanel, gbc, "Demand Distribution Type 2", demandType2);
        addRowToPanel(secondCarPanel, gbc, "Mean Charging Demand 2", meanChargingDemand2);
        addRowToPanel(secondCarPanel, gbc, "Battery Capacity 2", batteryCapacity2);

        return secondCarPanel;
    }


    private static JComboBox<String> createDemandTypeComboBox() {
        String[] demandTypes = {"DETERMINISTIC", "GEOMETRIC", "EXPONENTIAL", "ERLANG", "ERLANGD", "UNIFORM", "BETA", "LOMAX"};
        return new JComboBox<>(demandTypes);
    }

    private static void addWindowCloseListener(JFrame frame) {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to close all simulation results?", "Exit all confirmation", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                } else {
                    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });
    }

    private static JSpinner createSpinner(double value, double min, double max, double step) {
        SpinnerModel model = new SpinnerNumberModel(value, min, max, step);
        JSpinner spinner = new JSpinner(model);
        setSpinnerModel(spinner);
        return spinner;
    }

    private static JSpinner createSpinner(int value, int min, int max, int step) {
        SpinnerModel model = new SpinnerNumberModel(value, min, max, step);
        JSpinner spinner = new JSpinner(model);
        setSpinnerModel(spinner);
        return spinner;
    }

    private static void setSpinnerModel(JSpinner spinner) {
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor defaultEditor = (JSpinner.DefaultEditor) editor;
            defaultEditor.getTextField().setHorizontalAlignment(JTextField.LEFT);
            defaultEditor.getTextField().setBorder(BorderFactory.createCompoundBorder(
                    defaultEditor.getTextField().getBorder(),
                    BorderFactory.createEmptyBorder(0, 3, 0, 0)
            ));
        }
    }

    private static double getSpinnerValueAsDouble(JSpinner spinner) {
        return Double.parseDouble(spinner.getValue().toString());
    }

    private static int getSpinnerValueAsInt(JSpinner spinner) {
        return Integer.parseInt(spinner.getValue().toString());
    }

    private static JPanel createSpinnerPanel(String label1, String label2, String label3, JSpinner
            spinner1, JSpinner spinner2, JSpinner spinner3) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 3));
        panel.add(new JLabel(label1, SwingConstants.CENTER));
        panel.add(new JLabel(label2, SwingConstants.CENTER));
        panel.add(new JLabel(label3, SwingConstants.CENTER));
        panel.add(spinner1);
        panel.add(spinner2);
        panel.add(spinner3);
        panel.setBackground(LIGHT_BLUE);
        return panel;
    }


    private static void configureButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(RED);
        button.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BLUE);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(RED);
            }
        });
    }


    static class CustomComboBoxUI extends BasicComboBoxUI {

        @Override
        protected ComboPopup createPopup() {
            BasicComboPopup popup = (BasicComboPopup) super.createPopup();
            popup.setBorder(BorderFactory.createEmptyBorder(2, 5, 0, 25));
            return popup;
        }

        @Override
        public ListCellRenderer<Object> createRenderer() {
            return new BasicComboBoxRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                    if (isSelected) {
                        comp.setBackground(BLUE);
                    } else {
                        comp.setBackground(Color.WHITE);
                    }

                    JLabel label = (JLabel) comp;
                    label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

                    return comp;
                }
            };
        }
    }
}