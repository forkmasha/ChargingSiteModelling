package chargingSite;

import distributions.DistributionType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import queueingSystem.Queue;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class SimulationGUI {
    private static final Color LIGHT_PINK = new Color(255, 182, 193);
    private static final Color LAVENDER = new Color(230, 230, 250);
    private static final Color PEACH = new Color(255, 218, 185);
    private static final Color BLUE = new Color(173, 216, 230);
    private static final Color LIGHT_BLUE = new Color(200, 200, 240);
    private static final Color DARK_BLUE = new Color(136, 186, 242);
    private static final Color ORANGE = new Color(255, 175, 128);
    private static final Color RED = new Color(255, 102, 102);
    private static final Color LIGHT_GREEN = new Color(200, 240, 200);
    private static final Color LIGHT_BLUE1 = new Color(180, 200, 240);
    private static final Color LIGHT_YELLOW = new Color(240, 210, 160);
    private static final Color LIGHT_PINK1 = new Color(240, 200, 220);
    private static final Color LIGHT_YELLOW1 = new Color(240, 240, 200);

    private static final Color LIGHT_KREM = new Color(240, 230, 210);
    private static final Color LAVANDA = new Color(220, 200, 240);

    public static int WIDTH_OF_PNG_PICTURE = 2400;
    public static int HEIGHT_OF_PNG_PICTURE = 1560;

    public static int WIDTH_OF_SVG_PICTURE = 1200;
    public static int HEIGHT_OF_SVG_PICTURE = 730;

    private static Simulation simulation;
    private static SimulationParameters parameters;

    static ImageIcon gifIcon = new ImageIcon("D:\\ChargingSiteModelling\\src\\main\\resources\\smallTransparentClock.gif");
    static JLabel gifLabel = new JLabel(gifIcon);

    private static boolean simulationRun = false;

    public static void runSimulationGUI() {
        JFrame frame = createSimulationFrame();
        addWindowCloseListener(frame);
        frame.setVisible(true);
    }

    private static JFrame createSimulationFrame() {
        JFrame frame = new JFrame("Charging Site Modeling");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GraphicsEnvironment ge1 = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs1 = ge1.getScreenDevices();

        for (GraphicsDevice gd : gs1) {
            Rectangle bounds = gd.getDefaultConfiguration().getBounds();

            int frameWidth = (int) (bounds.width * 0.20);
            int frameHeight = (int) (bounds.height * 0.9);


            frame.setPreferredSize(new Dimension(frameWidth, frameHeight));
            frame.setSize(frameWidth, frameHeight);

            int xPosition = bounds.x;
            int yPosition = bounds.y + (bounds.height - frameHeight) / 2;
            frame.setLocation(xPosition, yPosition);

        }

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();

        Rectangle largestBounds = null;
        long maxArea = 0;

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
        confLevel.setBackground(Color.WHITE);

        AutoResizeButton saveResults = new AutoResizeButton("Save Results");
        AutoResizeButton runSimulation = new AutoResizeButton("Run Simulation");
        AutoResizeButton loadResults = new AutoResizeButton("Load Parameters");
        AutoResizeButton closeWindows = new AutoResizeButton("Close Windows");
        loadResults.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadResults.setBackground(LIGHT_KREM);
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select XML file with simulation parameters");
                fileChooser.setAcceptAllFileFilterUsed(false);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("XML Files", "xml");
                fileChooser.addChoosableFileFilter(filter);

                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();

                    if (selectedFile.isFile() && selectedFile.getName().toLowerCase().endsWith(".xml")) {
                        try {
                            loadParametersFromXml(frame, selectedFile);

                            simulation.runSimulation();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame, "Failed to load parameters or start the simulation: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Please select a valid XML file.", "Invalid File", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });


        JPanel procPanel = new JPanel();

        JScrollPane jScrollPane = new JScrollPane(procPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setMaximumSize(new Dimension(430, 1000));

        runSimulation.addActionListener(e -> {
            if (ChargingSite.frame1 != null) {
                ChargingSite.frame1.setVisible(true);
            } else {
                ChargingSite.initializePowerOverTimeChart1();
            }
            simulationRun = true;
            runSimulation.setBackground(LIGHT_PINK);
            jScrollPane.getViewport().setViewPosition(new Point(0, 0));

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

            //simulation.runSimulation();
            saveResults.setVisible(true);

            gifLabel.setVisible(true);
            Dimension initial = new Dimension(100, 100);
            gifLabel.setPreferredSize(initial);

            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    simulation.runSimulation();
                    return null;
                }

                @Override
                protected void done() {
                    gifLabel.setVisible(false);
                }
            };

            worker.execute();

        });


        saveResults.addActionListener(e -> {
            saveResults.setBackground(LIGHT_YELLOW1);
            if (!simulationRun) {
                saveResults.setBackground(LIGHT_YELLOW1);
                JOptionPane.showMessageDialog(frame,
                        "In order to save the results, you must first run the simulation.",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
            } else {

                saveResults.setBackground(LIGHT_GREEN);
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

                JOptionPane.showMessageDialog(
                        frame,
                        new JScrollPane(optionList),
                        "Save Parameters",
                        JOptionPane.PLAIN_MESSAGE
                );

                int selectedIndex = optionList.getSelectedIndex();
                if (selectedIndex != -1) {
                    switch (selectedIndex) {
                        case 0:
                            String[] formats = {"csv", "svg", "png"};
                            String selectedFormat = (String) JOptionPane.showInputDialog(
                                    frame,
                                    "Choose the format to save the graph:",
                                    "Save Format",
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    formats,
                                    formats[0]
                            );

                            if (selectedFormat != null) {
                                JFileChooser fileChooser = new JFileChooser();
                                fileChooser.setDialogTitle("Specify a file to save");

                                if ("csv".equals(selectedFormat)) {
                                    fileChooser.setSelectedFile(new File("ChargingSiteQueueingCharacteristics.csv"));
                                } else if ("svg".equals(selectedFormat)) {
                                    fileChooser.setSelectedFile(new File("ChargingSiteQueueingCharacteristics.svg"));
                                } else if ("png".equals(selectedFormat)) {
                                    fileChooser.setSelectedFile(new File("ChargingSiteQueueingCharacteristics.png"));
                                }

                                int userSelection = fileChooser.showSaveDialog(frame);

                                if (userSelection == JFileChooser.APPROVE_OPTION) {
                                    File fileToSave = fileChooser.getSelectedFile();

                                    if ("csv".equals(selectedFormat)) {
                                        simulation.saveQueueingCharacteristicsToCSV(fileToSave.getAbsolutePath());
                                    } else if ("svg".equals(selectedFormat)) {
                                        try {
                                            simulation.saveQueueingCharacteristicsAsSVG(WIDTH_OF_SVG_PICTURE, HEIGHT_OF_SVG_PICTURE, fileToSave);
                                        } catch (IOException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    } else if ("png".equals(selectedFormat)) {
                                        simulation.saveQueueingCharacteristicsGraphToPNG(fileToSave.getAbsolutePath());
                                    }
                                }
                            }
                            break;

                        case 1:
                            String[] formats1 = {"csv", "svg", "png"};
                            String selectedFormat1 = (String) JOptionPane.showInputDialog(
                                    frame,
                                    "Choose the format to save the graph:",
                                    "Save Format",
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    formats1,
                                    formats1[0]
                            );

                            if (selectedFormat1 != null) {
                                JFileChooser fileChooser = new JFileChooser();
                                fileChooser.setDialogTitle("Specify a file to save");

                                if ("csv".equals(selectedFormat1)) {
                                    fileChooser.setSelectedFile(new File("ChargingSiteEnergyCharacteristics.csv"));
                                } else if ("svg".equals(selectedFormat1)) {
                                    fileChooser.setSelectedFile(new File("ChargingSiteEnergyCharacteristics.svg"));
                                } else if ("png".equals(selectedFormat1)) {
                                    fileChooser.setSelectedFile(new File("ChargingSiteEnergyCharacteristics.png"));
                                }

                                int userSelection = fileChooser.showSaveDialog(frame);

                                if (userSelection == JFileChooser.APPROVE_OPTION) {
                                    File fileToSave = fileChooser.getSelectedFile();

                                    if ("csv".equals(selectedFormat1)) {
                                        simulation.chargingMonitor.saveEnergyCharacteristicsGraphToCSV(fileToSave.getAbsolutePath());
                                    } else if ("svg".equals(selectedFormat1)) {
                                        try {
                                            simulation.chargingMonitor.saveEnergyCharacteristicsGraphAsSVG(WIDTH_OF_SVG_PICTURE, HEIGHT_OF_SVG_PICTURE, fileToSave);
                                        } catch (IOException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    } else if ("png".equals(selectedFormat1)) {
                                        simulation.chargingMonitor.saveEnergyCharacteristicsGraphToPNG(fileToSave.getAbsolutePath());
                                    }
                                }
                            }
                            break;

                        case 2:
                            String[] formats2 = {"csv", "svg", "png"};
                            String selectedFormat2 = (String) JOptionPane.showInputDialog(
                                    frame,
                                    "Choose the format to save the graph:",
                                    "Save Format",
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    formats2,
                                    formats2[0]
                            );

                            if (selectedFormat2 != null) {
                                JFileChooser fileChooser = new JFileChooser();
                                fileChooser.setDialogTitle("Specify a file to save");

                                if ("csv".equals(selectedFormat2)) {
                                    fileChooser.setSelectedFile(new File("PowerOverTimeChart.csv"));
                                } else if ("svg".equals(selectedFormat2)) {
                                    fileChooser.setSelectedFile(new File("PowerOverTimeChart.svg"));
                                } else if ("png".equals(selectedFormat2)) {
                                    fileChooser.setSelectedFile(new File("PowerOverTimeChart.png"));
                                }

                                int userSelection = fileChooser.showSaveDialog(frame);

                                if (userSelection == JFileChooser.APPROVE_OPTION) {
                                    File fileToSave = fileChooser.getSelectedFile();

                                    if ("csv".equals(selectedFormat2)) {
                                        simulation.site.savePowerOverTimeGraphToCSV(fileToSave.getAbsolutePath());
                                    } else if ("svg".equals(selectedFormat2)) {
                                        //    simulation.saveAsSVG(1200, 730, fileToSave);
                                        simulation.site.savePowerOverTimeToSVG(fileToSave.getAbsolutePath());

                                    } else if ("png".equals(selectedFormat2)) {
                                        //    simulation.saveAsSVG(1200, 730, fileToSave);
                                        simulation.site.savePowerOverTimeGraphToPNG(fileToSave.getAbsolutePath());
                                    }
                                }
                            }
                            break;
                        case 3:

                            String[] formats3 = {"csv", "svg", "png"};
                            String selectedFormat3 = (String) JOptionPane.showInputDialog(
                                    frame,
                                    "Choose the format to save the histogram:",
                                    "Save Format",
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    formats3,
                                    formats3[0]
                            );

                            if (selectedFormat3 != null) {
                                JFileChooser fileChooser = new JFileChooser();
                                fileChooser.setDialogTitle("Specify a file to save");

                                if ("csv".equals(selectedFormat3)) {
                                    fileChooser.setSelectedFile(new File("SitePowerDistributionHistogram.csv"));
                                } else if ("svg".equals(selectedFormat3)) {
                                    fileChooser.setSelectedFile(new File("SitePowerDistributionHistogram.svg"));
                                } else if ("png".equals(selectedFormat3)) {
                                    fileChooser.setSelectedFile(new File("SitePowerDistributionHistogram.png"));
                                }

                                int userSelection = fileChooser.showSaveDialog(frame);

                                if (userSelection == JFileChooser.APPROVE_OPTION) {
                                    File fileToSave = fileChooser.getSelectedFile();

                                    if ("csv".equals(selectedFormat3)) {
                                        try {
                                            simulation.site.saveHistogramDataToCSV(fileToSave.getAbsolutePath());
                                        } catch (IOException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    } else if ("svg".equals(selectedFormat3)) {
                                        //    simulation.saveAsSVG(1200, 730, fileToSave);
                                        simulation.site.saveHistogramToSVG(fileToSave.getAbsolutePath());
                                    } else if ("png".equals(selectedFormat3)) {
                                        simulation.site.saveHistogramToPNG(fileToSave.getAbsolutePath());
                                    }
                                }
                            }
                            break;
                        case 4:
                            String[] formats4 = {"txt", "xml"};
                            String selectedFormat4 = (String) JOptionPane.showInputDialog(
                                    frame, // ваш JFrame
                                    "Choose the format to save the result:", // текст запитання
                                    "Save Format", // заголовок вікна
                                    JOptionPane.QUESTION_MESSAGE,
                                    null, // без іконки
                                    formats4, // опції для вибору
                                    formats4[0] // вибір за замовчуванням
                            );

                            if (selectedFormat4 != null) {
                                JFileChooser fileChooser = new JFileChooser();
                                fileChooser.setDialogTitle("Specify a file to save");


                                if ("txt".equals(selectedFormat4)) {
                                    fileChooser.setSelectedFile(new File("SimulationParameters.txt"));
                                } else if ("xml".equals(selectedFormat4)) {
                                    fileChooser.setSelectedFile(new File("SimulationParameters.xml"));
                                }
                                int userSelection = fileChooser.showSaveDialog(frame);

                                if (userSelection == JFileChooser.APPROVE_OPTION) {
                                    File fileToSave = fileChooser.getSelectedFile();

                                    if ("txt".equals(selectedFormat4)) {
                                        parameters.writeParameters2txt(frame, fileToSave.getAbsolutePath());
                                    } else if ("xml".equals(selectedFormat4)) {
                                        parameters.writeParameters2xml(frame, fileToSave.getAbsolutePath());
                                    }

                                }
                            }
                            break;
                        case 5:
                            SimpleDateFormat dateFormat = new SimpleDateFormat("ddMM_HHmm");
                            String dateTime = dateFormat.format(new Date());
                            String kendallName = parameters. getKendallNameForFile();
                            String finalFileName = kendallName + "_" + dateTime + ".zip";

                            JFileChooser fileChooser = new JFileChooser();
                            fileChooser.setDialogTitle("Specify a file to save all results in a ZIP archive");
                            fileChooser.setSelectedFile(new File(finalFileName));

                            int userSelection = fileChooser.showSaveDialog(null);

                            if (userSelection == JFileChooser.APPROVE_OPTION) {
                                File zipFileToSave = fileChooser.getSelectedFile();


                                try (FileOutputStream fos = new FileOutputStream(zipFileToSave);
                                     ZipOutputStream zipOut = new ZipOutputStream(fos)) {

                                    // Масив назв і форматів файлів для збереження
                                    String[][] fileInfo = {
                                            {"ChargingSiteQueueingCharacteristics", "csv", "svg", "png"},
                                            {"ChargingSiteEnergyCharacteristics", "csv", "svg", "png"},
                                            {"PowerOverTimeChart", "csv", "svg", "png"},
                                            {"SitePowerDistributionHistogram", "csv", "svg", "png"},
                                            {"SimulationParameters", "txt", "xml"}
                                    };

                                    for (String[] info : fileInfo) {
                                        for (int formatIndex = 1; formatIndex < info.length; formatIndex++) {
                                            String format = info[formatIndex];
                                            String baseName = info[0];
                                            String fileName = baseName + "." + format;

                                            File fileToSave = new File(System.getProperty("java.io.tmpdir"), fileName);

                                            if ("csv".equals(format)) {
                                                if ("ChargingSiteQueueingCharacteristics".equals(baseName)) {
                                                    simulation.saveQueueingCharacteristicsToCSV(fileToSave.getAbsolutePath());
                                                } else if ("ChargingSiteEnergyCharacteristics".equals(baseName)) {
                                                    simulation.chargingMonitor.saveEnergyCharacteristicsGraphToCSV(fileToSave.getAbsolutePath());
                                                } else if ("PowerOverTimeChart".equals(baseName)) {
                                                    simulation.site.savePowerOverTimeGraphToCSV(fileToSave.getAbsolutePath());
                                                } else if ("SitePowerDistributionHistogram".equals(baseName)) {
                                                    simulation.site.saveHistogramDataToCSV(fileToSave.getAbsolutePath());
                                                }
                                            } else if ("svg".equals(format)) {
                                                if ("ChargingSiteQueueingCharacteristics".equals(baseName)) {
                                                    simulation.saveQueueingCharacteristicsAsSVG(1200, 730, fileToSave);
                                                } else if ("ChargingSiteEnergyCharacteristics".equals(baseName)) {
                                                    simulation.chargingMonitor.saveEnergyCharacteristicsGraphAsSVG(1200, 730, fileToSave);
                                                } else if ("PowerOverTimeChart".equals(baseName)) {
                                                    simulation.site.savePowerOverTimeToSVG(fileToSave.getAbsolutePath());
                                                } else if ("SitePowerDistributionHistogram".equals(baseName)) {
                                                    simulation.site.saveHistogramToSVG(fileToSave.getAbsolutePath());
                                                }
                                            } else if ("png".equals(format)) {
                                                if ("ChargingSiteQueueingCharacteristics".equals(baseName)) {
                                                    simulation.saveQueueingCharacteristicsGraphToPNG(fileToSave.getAbsolutePath());
                                                } else if ("ChargingSiteEnergyCharacteristics".equals(baseName)) {
                                                    simulation.chargingMonitor.saveEnergyCharacteristicsGraphToPNG(fileToSave.getAbsolutePath());
                                                } else if ("PowerOverTimeChart".equals(baseName)) {
                                                    simulation.site.savePowerOverTimeGraphToPNG(fileToSave.getAbsolutePath());
                                                } else if ("SitePowerDistributionHistogram".equals(baseName)) {
                                                    simulation.site.saveHistogramToPNG(fileToSave.getAbsolutePath());
                                                }
                                            } else if ("SimulationParameters".equals(baseName)) {
                                                if ("txt".equals(format)) {
                                                    parameters.writeParameters2txt(frame, fileToSave.getAbsolutePath()); // Припускаємо, що цей метод зберігає параметри у форматі TXT
                                                } else if ("xml".equals(format)) {
                                                    parameters.writeParameters2xml(frame, fileToSave.getAbsolutePath()); // Припускаємо, що цей метод зберігає параметри у форматі XML
                                                }
                                            }
                                            try (FileInputStream fis = new FileInputStream(fileToSave)) {
                                                ZipEntry zipEntry = new ZipEntry(fileToSave.getName());
                                                zipOut.putNextEntry(zipEntry);

                                                byte[] bytes = new byte[1024];
                                                int length;
                                                while ((length = fis.read(bytes)) >= 0) {
                                                    zipOut.write(bytes, 0, length);
                                                }
                                                zipOut.closeEntry();
                                            }

                                            fileToSave.delete();
                                        }
                                    }
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            break;
                    }
                } else {
                    JOptionPane.showMessageDialog(
                            frame,
                            "Please select an option to proceed with saving.",
                            "Selection Required",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            }
        });


        runSimulation.setBorder(BorderFactory.createEmptyBorder(13, 0, 13, 0));

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


        //  JPanel procPanel = new JPanel();
        procPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        GridBagConstraints gbc10 = new GridBagConstraints();
        gbc10.anchor = GridBagConstraints.CENTER;
        gbc10.insets = new Insets(1, 2, 25, 1);

        procPanel.add(gifLabel);
        gifLabel.setVisible(false);

        TitledBorder generalBorder = BorderFactory.createTitledBorder("General parameters");
        JPanel generalPanel = new JPanel(new GridBagLayout());
        generalBorder.setTitleColor(Color.BLUE);

        generalPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Dimension initialPanelSize = new Dimension(240, 45);
        generalPanel.setPreferredSize(initialPanelSize);
        generalPanel.setMinimumSize(initialPanelSize);
        generalPanel.setMaximumSize(initialPanelSize);


        JLabel label = new JLabel("Click to see General Parameters");
        label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        label.setForeground(Color.BLACK);
        generalPanel.add(label);


        generalPanel.addMouseListener(new MouseAdapter() {
            private boolean isParametersShown = false;

            public void mouseClicked(MouseEvent e) {
                if (!isParametersShown) {

                    generalPanel.removeAll();

                    addRowToPanel(generalPanel, gbc, "Number of Simulation Steps", numberOfSteps);
                    addRowToPanel(generalPanel, gbc, "Max Events per Step", maxEvents);
                    addRowToPanel(generalPanel, gbc, "Confidence Interval Level [%]", confLevel);
                    addRowToPanel(generalPanel, gbc, "Number of EV Types", numberOfClientTypes);


                    generalPanel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(10, 10, 10, 10), generalBorder));
                    generalPanel.setPreferredSize(new Dimension(240, 280));

                    isParametersShown = true;

                } else {

                    generalPanel.removeAll();
                    generalPanel.add(label);

                    generalPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    generalPanel.setPreferredSize(initialPanelSize);

                    isParametersShown = false;
                }
                generalPanel.revalidate();
                generalPanel.repaint();
            }

        });

        TitledBorder siteBorder = BorderFactory.createTitledBorder("Site Parameters");
        JPanel sitePanel = new JPanel(new GridBagLayout());
        sitePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), siteBorder));

        sitePanel.addMouseListener(new MouseAdapter() {
            private boolean isParametersShown = false;

            public void mouseClicked(MouseEvent e) {
                if (!isParametersShown) {
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
                } else {
                    sitePanel.removeAll();
                    JLabel label = new JLabel("Click to show site parameters");
                    label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                    label.setForeground(Color.BLACK);
                    sitePanel.add(label);
                    sitePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    isParametersShown = false;
                    sitePanel.setPreferredSize(new Dimension(240, 45));
                }
                sitePanel.revalidate();
                sitePanel.repaint();
            }
        });

        sitePanel.removeAll();
        JLabel initialLabel = new JLabel("Click to show site parameters");
        initialLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initialLabel.setForeground(Color.BLACK);
        sitePanel.add(initialLabel);
        sitePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sitePanel.setPreferredSize(new Dimension(240, 45));


        TitledBorder chargingParameters = BorderFactory.createTitledBorder("Charging Parameters");
        JPanel chargingParametersPanel = new JPanel(new GridBagLayout());

        Dimension chargingPanelSize = new Dimension(240, 210);
        chargingParametersPanel.setPreferredSize(new Dimension(240, 45));
        chargingParametersPanel.setMinimumSize(chargingPanelSize);
        chargingParametersPanel.setMaximumSize(chargingPanelSize);

        chargingParameters.setTitleColor(Color.BLUE);
        chargingParameters.setTitle("<html><body>&nbsp;<b><font color='blue'>Charging Parameters</font></b></body></html>");
        chargingParametersPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), chargingParameters));

        chargingParametersPanel.addMouseListener(new MouseAdapter() {
            private boolean isParametersShown = false;

            public void mouseClicked(MouseEvent e) {
                if (!isParametersShown) {
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
                } else {
                    chargingParametersPanel.removeAll();
                    JLabel label = new JLabel("Click to show charging parameters");
                    label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                    label.setForeground(Color.BLACK);
                    chargingParametersPanel.add(label);
                    chargingParametersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    isParametersShown = false;
                    chargingParametersPanel.setPreferredSize(new Dimension(240, 45));
                }
                chargingParametersPanel.revalidate();
                chargingParametersPanel.repaint();
            }
        });

        chargingParametersPanel.removeAll();
        JLabel initialLabel1 = new JLabel("Click to show charging parameters");
        initialLabel1.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initialLabel1.setForeground(Color.BLACK);
        chargingParametersPanel.add(initialLabel1);
        chargingParametersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        TitledBorder EVParameters = BorderFactory.createTitledBorder("EV Parameters");
        JPanel EVParametersPanel = new JPanel(new GridBagLayout());
        EVParametersPanel.setVisible(true);

        Dimension EVPanelSize = new Dimension(240, 350);
        EVParametersPanel.setPreferredSize(new Dimension(240, 45)); // Start with compact size
        EVParametersPanel.setMinimumSize(EVPanelSize);
        EVParametersPanel.setMaximumSize(EVPanelSize);

        EVParameters.setTitleColor(Color.BLUE);
        EVParameters.setTitle("<html><body>&nbsp;<b><font color='blue'>Charging Parameters</font></b></body></html>");
        EVParameters.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), chargingParameters));

        EVParametersPanel.addMouseListener(new MouseAdapter() {
            private boolean isParametersShown = false;

            public void mouseClicked(MouseEvent e) {
                if (!isParametersShown) {
                    EVParametersPanel.removeAll();

                    addRowToPanel(EVParametersPanel, gbc, "Battery Capacity [kWh]", batteryCapacity);
                    addRowToPanel(EVParametersPanel, gbc, "Mean Charging Time [h]", meanServiceTime);
                    addRowToPanel(EVParametersPanel, gbc, "Max EV Charging Power [kW]", maxEVPower);
                    addRowToPanel(EVParametersPanel, gbc, "Demand Distribution Type", demandType);
                    addRowToPanel(EVParametersPanel, gbc, "Mean Charging Demand [fraction of battery]", meanChargingDemand);

                    TitledBorder tempBorder = BorderFactory.createTitledBorder("EV Parameters");
                    tempBorder.setTitleColor(Color.BLUE);
                    EVParametersPanel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(10, 10, 10, 10), tempBorder));

                    isParametersShown = true;
                    EVParametersPanel.setPreferredSize(EVPanelSize);
                } else {
                    EVParametersPanel.removeAll();
                    JLabel label = new JLabel("Click to show ev parameters");
                    label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                    label.setForeground(Color.BLACK);
                    EVParametersPanel.add(label);
                    EVParametersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    isParametersShown = false;
                    EVParametersPanel.setPreferredSize(new Dimension(240, 45));
                }
                EVParametersPanel.revalidate();
                EVParametersPanel.repaint();
            }
        });

        EVParametersPanel.removeAll();
        JLabel initialLabel4 = new JLabel("Click to show ev parameters");
        initialLabel4.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initialLabel4.setForeground(Color.BLACK);
        EVParametersPanel.add(initialLabel4);
        EVParametersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        TitledBorder EVParameters2 = BorderFactory.createTitledBorder("EV Parameters for type 2");
        JPanel EVParametersPanel2 = new JPanel(new GridBagLayout());
        EVParametersPanel2.setVisible(false);

        Dimension EVPanelSize2 = new Dimension(240, 420);
        EVParametersPanel2.setPreferredSize(new Dimension(240, 45));
        EVParametersPanel2.setMinimumSize(EVPanelSize2);
        EVParametersPanel2.setMaximumSize(EVPanelSize2);

        EVParameters2.setTitleColor(Color.BLUE);
        EVParameters2.setTitle("<html><body>&nbsp;<b><font color='blue'>EV Parameters type 2</font></b></body></html>");
        EVParameters2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), chargingParameters));

        EVParametersPanel2.addMouseListener(new MouseAdapter() {
            private boolean isParametersShown = false;

            public void mouseClicked(MouseEvent e) {
                if (!isParametersShown) {
                    EVParametersPanel2.removeAll();

                    addRowToPanel(EVParametersPanel2, gbc, "Percentage of EV 2 [%]", percentageOfCars2);
                    addRowToPanel(EVParametersPanel2, gbc, "Battery Capacity [kWh]", batteryCapacity2);
                    addRowToPanel(EVParametersPanel2, gbc, "Mean Charging Time [h]", meanServiceTime2);
                    addRowToPanel(EVParametersPanel2, gbc, "Max EV Charging Power [kW]", maxEVPower2);
                    addRowToPanel(EVParametersPanel2, gbc, "Demand Distribution Type", demandType2);
                    addRowToPanel(EVParametersPanel2, gbc, "Mean Charging Demand [fraction of battery]", meanChargingDemand2);

                    TitledBorder tempBorder = BorderFactory.createTitledBorder("EV Parameters for type 2");
                    tempBorder.setTitleColor(Color.BLUE);
                    EVParametersPanel2.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(10, 10, 10, 10), tempBorder));

                    isParametersShown = true;
                    EVParametersPanel2.setPreferredSize(EVPanelSize2);
                } else {
                    EVParametersPanel2.removeAll();
                    JLabel label = new JLabel("Click to show ev parameters 2");
                    label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                    label.setForeground(Color.BLACK);
                    EVParametersPanel2.add(label);
                    EVParametersPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    isParametersShown = false;
                    EVParametersPanel2.setPreferredSize(new Dimension(240, 45));
                }
                EVParametersPanel2.revalidate();
                EVParametersPanel2.repaint();
            }
        });

        EVParametersPanel2.removeAll();
        JLabel initialLabel5 = new JLabel("Click to show parameters for ev 2");
        initialLabel5.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initialLabel5.setForeground(Color.BLACK);
        EVParametersPanel2.add(initialLabel5);
        EVParametersPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        TitledBorder EVParameters3 = BorderFactory.createTitledBorder("EV Parameters for type 3");
        JPanel EVParametersPanel3 = new JPanel(new GridBagLayout());
        EVParametersPanel3.setVisible(false);

        Dimension EVPanelSize3 = new Dimension(240, 420);
        EVParametersPanel3.setPreferredSize(new Dimension(240, 45));
        EVParametersPanel3.setMinimumSize(EVPanelSize3);
        EVParametersPanel3.setMaximumSize(EVPanelSize3);

        EVParameters3.setTitleColor(Color.BLUE);
        EVParameters3.setTitle("<html><body>&nbsp;<b><font color='blue'>EV Parameters type 3</font></b></body></html>");
        EVParameters3.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), chargingParameters));

        EVParametersPanel3.addMouseListener(new MouseAdapter() {
            private boolean isParametersShown = false;

            public void mouseClicked(MouseEvent e) {
                if (!isParametersShown) {
                    EVParametersPanel3.removeAll();

                    addRowToPanel(EVParametersPanel3, gbc, "Percentage of EV 3 [%]", percentageOfCars3);
                    addRowToPanel(EVParametersPanel3, gbc, "Battery Capacity [kWh]", batteryCapacity3);
                    addRowToPanel(EVParametersPanel3, gbc, "Mean Charging Time [h]", meanServiceTime3);
                    addRowToPanel(EVParametersPanel3, gbc, "Max EV Charging Power [kW]", maxEVPower3);
                    addRowToPanel(EVParametersPanel3, gbc, "Demand Distribution Type", demandType3);
                    addRowToPanel(EVParametersPanel3, gbc, "Mean Charging Demand [fraction of battery]", meanChargingDemand3);

                    TitledBorder tempBorder = BorderFactory.createTitledBorder("EV Parameters for type 3");
                    tempBorder.setTitleColor(Color.BLUE);
                    EVParametersPanel3.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(10, 10, 10, 10), tempBorder));

                    isParametersShown = true;
                    EVParametersPanel3.setPreferredSize(EVPanelSize3);
                } else {
                    EVParametersPanel3.removeAll();
                    JLabel label = new JLabel("Click to show ev parameters 3");
                    label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                    label.setForeground(Color.BLACK);
                    EVParametersPanel3.add(label);
                    EVParametersPanel3.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    isParametersShown = false;
                    EVParametersPanel3.setPreferredSize(new Dimension(240, 45));
                }
                EVParametersPanel3.revalidate();
                EVParametersPanel3.repaint();
            }
        });

        EVParametersPanel3.removeAll();
        JLabel initialLabel6 = new JLabel("Click to show parameters for ev 3");
        initialLabel6.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initialLabel6.setForeground(Color.BLACK);
        EVParametersPanel3.add(initialLabel6);
        EVParametersPanel3.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        numberOfClientTypes.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int selectedValue = (Integer) numberOfClientTypes.getValue();
                if (selectedValue == 1) {
                    EVParametersPanel.setVisible(true);
                    EVParametersPanel2.setVisible(false);
                    EVParametersPanel3.setVisible(false);
                    procPanel.revalidate();
                    procPanel.repaint();
                } else if (selectedValue == 2) {
                    EVParametersPanel.setVisible(true);
                    EVParametersPanel2.setVisible(true);
                    EVParametersPanel3.setVisible(false);
                    procPanel.revalidate();
                    procPanel.repaint();
                } else {
                    EVParametersPanel.setVisible(true);
                    EVParametersPanel2.setVisible(true);
                    EVParametersPanel3.setVisible(true);

                    procPanel.revalidate();
                    procPanel.repaint();
                }
            }
        });


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

        GridBagConstraints EVParametersGbc2 = new GridBagConstraints();
        EVParametersGbc2.anchor = GridBagConstraints.WEST;
        EVParametersGbc2.insets = new Insets(5, 5, 5, 5);
        EVParametersGbc2.gridx = 0;
        procPanel.add(EVParametersPanel2, EVParametersGbc2);

        GridBagConstraints EVParametersGbc3 = new GridBagConstraints();
        EVParametersGbc3.anchor = GridBagConstraints.WEST;
        EVParametersGbc3.insets = new Insets(5, 5, 5, 5);
        EVParametersGbc3.gridx = 0;
        procPanel.add(EVParametersPanel3, EVParametersGbc3);

        procPanel.setBackground(LIGHT_BLUE);
        verticalBox.add(jScrollPane);

        JTextArea consoleTextArea = new JTextArea(10, 40); // Визначте розміри текстової області
        consoleTextArea.setEditable(false); // Забороніть редагування тексту в консолі
        JScrollPane scrollPane = new JScrollPane(consoleTextArea); // Додайте прокрутку до JTextArea


        JPanel bottomPanel = new JPanel();
        runSimulation.setForeground(Color.BLACK);
        bottomPanel.setBackground(DARK_BLUE);
        bottomPanel.setLayout(new GridLayout(1, 3));

        bottomPanel.add(loadResults);
        bottomPanel.add(runSimulation);
        bottomPanel.add(saveResults);

        // bottomPanel.add(closeResults);

        /*  consoleTextArea.setEditable(false);
        JScrollPane consoleScrollPane = new JScrollPane(consoleTextArea);
        procPanel.add(consoleScrollPane, gbc10);
        consoleTextArea.append("Hello, World!\n");*/

        frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        frame.getContentPane().add(verticalBox, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        return frame;
    }

    public static void loadParametersFromXml(JFrame frame, File selectedFile1) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select the simulation parameters file to load");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(selectedFile);
                doc.getDocumentElement().normalize();

                NodeList generalParamsList = doc.getElementsByTagName("GeneralParameters");
                if (generalParamsList.getLength() > 0) {
                    Element generalParams = (Element) generalParamsList.item(0);
                    int numberOfSimulationSteps = Integer.parseInt(generalParams.getElementsByTagName("NumberOfSimulationSteps").item(0).getTextContent());
                    int maxEventsPerStep = Integer.parseInt(generalParams.getElementsByTagName("MaxEventsPerStep").item(0).getTextContent());
                    int confidenceIntervalLevel = Integer.parseInt(generalParams.getElementsByTagName("ConfidenceIntervalLevel").item(0).getTextContent());

                    simulation.getParameters().setSIM_STEPS(numberOfSimulationSteps);
                    simulation.getParameters().setMAX_EVENTS(maxEventsPerStep);
                    simulation.getParameters().setConfLevel(confidenceIntervalLevel);
                }

                NodeList siteParamsList = doc.getElementsByTagName("SiteParameters");
                if (siteParamsList.getLength() > 0) {
                    Element siteParams = (Element) siteParamsList.item(0);
                    String arrivalDistributionType = siteParams.getElementsByTagName("ArrivalDistributionType").item(0).getTextContent();
                    double maxMeanArrivalRate = Double.parseDouble(siteParams.getElementsByTagName("MaxMeanArrivalRate").item(0).getTextContent());
                    int parkingSpace = Integer.parseInt(siteParams.getElementsByTagName("ParkingSpace").item(0).getTextContent());
                    String queueingType = siteParams.getElementsByTagName("QueueingType").item(0).getTextContent();
                    int maxSitePower = Integer.parseInt(siteParams.getElementsByTagName("MaxSitePower").item(0).getTextContent());

                    simulation.getParameters().setARRIVAL_TYPE(DistributionType.valueOf(arrivalDistributionType));
                    simulation.getParameters().setMAX_ARRIVAL_RATE(maxMeanArrivalRate);
                    simulation.getParameters().setQUEUE_SIZE(parkingSpace);
                    simulation.getParameters().setQUEUEING_TYPE(Queue.QueueingType.valueOf(queueingType));
                    simulation.getParameters().setMaxSitePower(maxSitePower);
                }

                NodeList chargingParamsList = doc.getElementsByTagName("ChargingParameters");
                if (chargingParamsList.getLength() > 0) {
                    Element chargingParams = (Element) chargingParamsList.item(0);
                    int numberOfChargingPoints = Integer.parseInt(chargingParams.getElementsByTagName("NumberOfChargingPoints").item(0).getTextContent());
                    String serviceDistributionType = chargingParams.getElementsByTagName("ServiceDistributionType").item(0).getTextContent();
                    int maxPowerOfChargingPoint = Integer.parseInt(chargingParams.getElementsByTagName("MaxPowerOfChargingPoint").item(0).getTextContent());

                    simulation.getParameters().setNUMBER_OF_SERVERS(numberOfChargingPoints);
                    simulation.getParameters().setSERVICE_TYPE(DistributionType.valueOf(serviceDistributionType));
                    simulation.getParameters().setMaxPointPower(maxPowerOfChargingPoint);
                }

                NodeList evParamsList = doc.getElementsByTagName("EVParameters");
                for (int temp = 0; temp < evParamsList.getLength(); temp++) {
                    Node nNode = evParamsList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element evParams = (Element) nNode;
                        int numberOfEVTypes = Integer.parseInt(evParams.getElementsByTagName("NumberOfEVTypes").item(0).getTextContent());
                        double batteryCapacity = Double.parseDouble(evParams.getElementsByTagName("BatteryCapacity").item(0).getTextContent());
                        double meanChargingTime = Double.parseDouble(evParams.getElementsByTagName("MeanChargingTime").item(0).getTextContent());
                        int maxEVChargingPower = Integer.parseInt(evParams.getElementsByTagName("MaxEVChargingPower").item(0).getTextContent());
                        String demandDistributionType = evParams.getElementsByTagName("DemandDistributionType").item(0).getTextContent();
                        double meanChargingDemand = Double.parseDouble(evParams.getElementsByTagName("MeanChargingDemand").item(0).getTextContent());

                        simulation.getParameters().setNUMBER_OF_CAR_TYPES(numberOfEVTypes);
                        simulation.getParameters().setBatteryCapacity(batteryCapacity);
                        simulation.getParameters().setMEAN_SERVICE_TIME(meanChargingTime);
                        simulation.getParameters().setMaxEvPower(maxEVChargingPower);
                        simulation.getParameters().setDEMAND_TYPE(DistributionType.valueOf(demandDistributionType));
                        simulation.getParameters().setMeanChargingDemand(meanChargingDemand);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error loading parameters from file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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


    public static class AutoResizeButton extends JButton {


        public AutoResizeButton(String text) {
            super("<html>" + text + "</html>");
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            fitTextToButton();
        }

        private void fitTextToButton() {
            FontMetrics fm = getFontMetrics(getFont());
            String text = getText().replaceAll("<html>", "").replaceAll("</html>", "");
            int stringWidth = fm.stringWidth(text);
            int componentWidth = getWidth();

            if (stringWidth > componentWidth) {
                StringBuilder newText = new StringBuilder("<html>");
                String[] words = text.split(" ");
                String currentLine = "";
                for (String word : words) {
                    // Перевіряємо ширину поточного рядка з новим словом
                    int lineWidth = fm.stringWidth(currentLine + word);
                    if (lineWidth < componentWidth) {
                        currentLine += word + " ";
                    } else {
                        newText.append(currentLine.trim()).append("<br>");
                        currentLine = word + " ";
                    }
                }
                newText.append(currentLine).append("</html>");
                setText(newText.toString());
            }
        }
    }
}