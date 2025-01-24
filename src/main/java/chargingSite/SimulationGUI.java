package chargingSite;

import distributions.DistributionType;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import queueingSystem.QueueingType;
import results.Monitor;
import simulationParameters.SimulationParameters;

import javax.swing.*;
import javax.swing.border.Border;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class SimulationGUI {

    private static Simulation simulation;
    private static SimulationParameters simulationParameters;
    private static boolean simulationRun = false;
    static boolean isSimulationStarted = false;

    private JFrame frame;
    private JComboBox<String> queueingType, arrivalType, serviceType, confLevel, demandType, demandType2, demandType3;
    private AutoResizeButton saveResultsButton, runSimulationButton, loadResultsButton, closeWindowsButton;
    private JSpinner numberOfSteps, maxArrivalRate, numberOfClientTypes, maxEvents, numberOfServers, queueSize,
            meanServiceTime, meanChargingDemand, batteryCapacity, maxEVPower, percentageOfCars,
            percentageOfCars2, meanServiceTime2, maxEVPower2, meanChargingDemand2, batteryCapacity2,
            percentageOfCars3, meanServiceTime3, maxEVPower3, meanChargingDemand3, batteryCapacity3,
            maxSitePower, maxPointPower;

    private JPanel mainGuiPanel, generalPanel, bottomPanel, sitePanel, chargingParametersPanel, EVParametersPanel,
            EVParametersPanel2, EVParametersPanel3;
    private JScrollPane jScrollPane;

    private ImageIcon gifIcon = new ImageIcon("src\\main\\resources\\smallTransparentClock.gif");
    private JLabel gifLabel = new JLabel(gifIcon);

    public SimulationGUI() {
        initializeMainFrame();
        initializeComboBoxes();
        initializeButtons();
        initializeSpinners();
        initializePanels();
    }

    public void runSimulationGUI() {

        gifLabel.setVisible(false);
        mainGuiPanel.add(gifLabel);

        GridBagConstraints gbc = createGridBagConstraints();
        mainGuiPanel.add(generalPanel, gbc);
        mainGuiPanel.add(sitePanel, gbc);
        mainGuiPanel.add(chargingParametersPanel, gbc);
        mainGuiPanel.add(EVParametersPanel, gbc);
        mainGuiPanel.add(EVParametersPanel2, gbc);
        mainGuiPanel.add(EVParametersPanel3, gbc);

        bottomPanel.add(loadResultsButton);
        bottomPanel.add(runSimulationButton);
        bottomPanel.add(saveResultsButton);
        bottomPanel.add(closeWindowsButton);

        jScrollPane = createMainScrollPane();
        Box verticalBox = createVerticalBox();
        JScrollPane scrollPane = createTextScrollPane();

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        frame.getContentPane().add(verticalBox, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        addWindowCloseListener();
    }

    private Box createVerticalBox() {
        Box verticalBox = Box.createVerticalBox();
        verticalBox.setBorder(createEmptyBorder(10));
        verticalBox.add(jScrollPane);
        return verticalBox;
    }

    // TODO: How to initialize parameters of fraime in one step - without duplication of code
    private void initializeMainFrame() {
        frame = new JFrame("Charging Site Modeling");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GraphicsEnvironment ge1 = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs1 = ge1.getScreenDevices();

        Rectangle largestBounds = null;
        long maxArea = 0;

        for (GraphicsDevice gd1 : gs1) {
            Rectangle bounds = gd1.getDefaultConfiguration().getBounds();
            long area = bounds.width * bounds.height;
            if (area > maxArea) {
                maxArea = area;
                largestBounds = bounds;
            }
        }

        if (largestBounds != null) {

            int frameHeight = (int) (largestBounds.height * 0.85);
            int frameWidth = (int) (largestBounds.width * 0.20);

            frame.setPreferredSize(new Dimension(frameWidth, frameHeight));
            frame.setSize(frameWidth, frameHeight);

            int yPosition = (int) (largestBounds.y + (largestBounds.height * 0.02));
            int xPosition = largestBounds.x + (largestBounds.width - frameWidth) / 2;

            frame.setLocation(xPosition, yPosition);
        }

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();

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
            // Центрування по вертикалі
            int yPosition = largestBounds.y + (largestBounds.height - frame.getPreferredSize().height) / 2;
            frame.setLocation(xPosition, yPosition);
        }
    }

    private void initializeComboBoxes() {
        queueingType = createComboBox(GuiParameters.queueingTypes, GuiParameters.defaultQueueingType);
        arrivalType = createComboBox(GuiParameters.arrivalDistributionTypes, GuiParameters.defaultArrivalType);
        serviceType = createComboBox(GuiParameters.serviceDistributionTypes, GuiParameters.defaultServiceType);
        confLevel = createComboBox(GuiParameters.confidenceLevels, GuiParameters.defaultConfidenceLevel);

        demandType = createComboBox(GuiParameters.demandDistributionTypes, GuiParameters.defaultDemandType);
        demandType2 = createComboBox(GuiParameters.demandDistributionTypes, GuiParameters.defaultDemandType);
        demandType3 = createComboBox(GuiParameters.demandDistributionTypes, GuiParameters.defaultDemandType);
    }

    private void initializeButtons() {
        saveResultsButton = new AutoResizeButton("Save Results");
        saveResultsButton.addActionListener(new SaveResultsActionListener());

        runSimulationButton = new AutoResizeButton("Run Simulation");
        runSimulationButton.addActionListener(new RunSimulationActionListener());
        runSimulationButton.setBorder(BorderFactory.createEmptyBorder(13, 0, 13, 0));
        runSimulationButton.setForeground(Color.BLACK);

        loadResultsButton = new AutoResizeButton("Load Parameters");
        loadResultsButton.addActionListener(new LoadResultsActionListener());

        closeWindowsButton = new AutoResizeButton("Close Windows");
        closeWindowsButton.addActionListener(new CloseWindowsActionListener());
    }

    private void initializeSpinners() {
        // JSpinner minArrivalRate = createSpinner(0.5, 0.0, Double.MAX_VALUE, 0.1);
        numberOfSteps = createSpinner(50, 0, Integer.MAX_VALUE, 1);
        maxArrivalRate = createSpinner(25.0, 0.0, Double.MAX_VALUE, 5);
        numberOfClientTypes = createSpinner(1, 1, 3, 1);
        numberOfClientTypes.addChangeListener(new NumberOfClientsChangeListener());

        maxEvents = createSpinner(2500, 100, Integer.MAX_VALUE, 100);
        numberOfServers = createSpinner(5, 1, Integer.MAX_VALUE, 1);
        queueSize = createSpinner(10, 1, Integer.MAX_VALUE, 1);

        meanServiceTime = createSpinner(0.5, 0.0, Double.MAX_VALUE, 0.25);
        meanChargingDemand = createSpinner(0.8, 0.0, 1.0, 0.1);
        batteryCapacity = createSpinner(60.0, 0.0, 200.0, 5);
        maxEVPower = createSpinner(150, 1.5, Double.MAX_VALUE, 10);
        percentageOfCars = createSpinner(100, 1, 100, 1);

        percentageOfCars2 = createSpinner(20, 1, 100, 10);
        meanServiceTime2 = createSpinner(0.75, 0.0, Double.MAX_VALUE, 0.25);
        maxEVPower2 = createSpinner(250, 1.5, Double.MAX_VALUE, 10);
        meanChargingDemand2 = createSpinner(0.7, 0.0, 1.0, 0.1);
        batteryCapacity2 = createSpinner(90.0, 0.0, 200.0, 5);

        percentageOfCars3 = createSpinner(30, 1, 100, 10);
        meanServiceTime3 = createSpinner(0.25, 0.0, Double.MAX_VALUE, 0.25);
        maxEVPower3 = createSpinner(50, 1.5, Double.MAX_VALUE, 10);
        meanChargingDemand3 = createSpinner(0.9, 0.0, 1.0, 0.1);
        batteryCapacity3 = createSpinner(15.0, 0.0, 200.0, 5);

        maxSitePower = createSpinner(300, 1.0, Double.MAX_VALUE, 50);
        maxPointPower = createSpinner(100, 1.0, Double.MAX_VALUE, 10);
    }

    private void initializePanels() {

        mainGuiPanel = new JPanel();
        mainGuiPanel.setLayout(new GridBagLayout());
        mainGuiPanel.setBackground(Colors.LIGHT_BLUE);

        bottomPanel = new JPanel();
        bottomPanel.setBackground(Colors.DARK_BLUE);
        bottomPanel.setLayout(new GridLayout(2, 2));

        Dimension minPanelSize = new Dimension(240, 45);
        generalPanel = new JPanel(new GridBagLayout());

        Dimension maxGeneraPanelSize = new Dimension(240, 280);
        LinkedHashMap<String, JComponent> generalParameters = new LinkedHashMap<>();
        generalParameters.put("Number of Simulation Steps", numberOfSteps);
        generalParameters.put("Max Events per Step", maxEvents);
        generalParameters.put("Confidence Interval Level [%]", confLevel);
        generalParameters.put("Number of EV Types", numberOfClientTypes);
        String minTextGeneral = "Click to show general parameters", maxTextGeneral = "General Parameters";

        generalPanel.addMouseListener(new MyMouseListener(generalPanel, generalParameters, minTextGeneral, minPanelSize,
                maxTextGeneral, maxGeneraPanelSize));
        initializeMinPanel(generalPanel, minTextGeneral, minPanelSize);

        sitePanel = new JPanel(new GridBagLayout());
        Dimension maxSitePanelSize = new Dimension(240, 350);
        LinkedHashMap<String, JComponent> siteParameters = new LinkedHashMap<>();
        siteParameters.put("Arrival Distribution Type", arrivalType);
        siteParameters.put("Max Mean Arrival Rate [EV/h]", maxArrivalRate);
        siteParameters.put("Parking Space [EV]", queueSize);
        siteParameters.put("Queueing Type", queueingType);
        siteParameters.put("Max Site Power [kW]", maxSitePower);
        String minTextSite = "Click to show site parameters", maxTextSite = "Site Parameters";

        sitePanel.addMouseListener(new MyMouseListener(sitePanel, siteParameters, minTextSite, minPanelSize,
                maxTextSite, maxSitePanelSize));
        initializeMinPanel(sitePanel, minTextSite, minPanelSize);

        chargingParametersPanel = new JPanel(new GridBagLayout());
        Dimension maxChargingParamsPanelSize = new Dimension(240, 210);
        LinkedHashMap<String, JComponent> chargingParameters = new LinkedHashMap<>();
        chargingParameters.put("Number of Charging Points", numberOfServers);
        chargingParameters.put("Service Distribution Type", serviceType);
        chargingParameters.put("Max Power of Charging Point [kW]", maxPointPower);
        String minTextChargingParams = "Click to show charging parameters",
                maxTextChargingParams = "Charging Parameters";

        chargingParametersPanel.addMouseListener(new MyMouseListener(chargingParametersPanel, chargingParameters,
                minTextChargingParams, minPanelSize, maxTextChargingParams, maxChargingParamsPanelSize));
        initializeMinPanel(chargingParametersPanel, minTextChargingParams, minPanelSize);

        Dimension maxPanelSize = new Dimension(240, 360);
        EVParametersPanel = new JPanel(new GridBagLayout());
        LinkedHashMap<String, JComponent> evParameters = new LinkedHashMap<>();
        evParameters.put("Battery Capacity [kWh]", batteryCapacity);
        evParameters.put("Mean Charging Time [h]", meanServiceTime);
        evParameters.put("Max EV Charging Power [kW]", maxEVPower);
        evParameters.put("Demand Distribution Type", demandType);
        evParameters.put("Mean Charging Demand [fraction of battery]", meanChargingDemand);

        String minText = "Click to show ev parameters", maxText = "EV Parameters";
        EVParametersPanel.setVisible(true);
        EVParametersPanel.addMouseListener(
                new MyMouseListener(EVParametersPanel, evParameters, minText, minPanelSize, maxText, maxPanelSize));
        initializeMinPanel(EVParametersPanel, minText, minPanelSize);

        Dimension maxPanelSize2 = new Dimension(240, 420);
        EVParametersPanel2 = new JPanel(new GridBagLayout());
        LinkedHashMap<String, JComponent> evParameters2 = new LinkedHashMap<>();
        evParameters2.put("Percentage of EV 2 [%]", percentageOfCars2);
        evParameters2.put("Battery Capacity [kWh]", batteryCapacity2);
        evParameters2.put("Mean Charging Time [h]", meanServiceTime2);
        evParameters2.put("Max EV Charging Power [kW]", maxEVPower2);
        evParameters2.put("Demand Distribution Type", demandType2);
        evParameters2.put("Mean Charging Demand [fraction of battery]", meanChargingDemand2);

        String minText2 = "Click to show ev parameters 2", maxText2 = "EV Parameters for type 2";
        EVParametersPanel2.setVisible(false);
        EVParametersPanel2.addMouseListener(
                new MyMouseListener(EVParametersPanel2, evParameters2, minText2, minPanelSize, maxText2, maxPanelSize2));
        initializeMinPanel(EVParametersPanel2, minText2, minPanelSize);

        EVParametersPanel3 = new JPanel(new GridBagLayout());
        LinkedHashMap<String, JComponent> evParameters3 = new LinkedHashMap<>();
        evParameters3.put("Percentage of EV 3 [%]", percentageOfCars3);
        evParameters3.put("Battery Capacity [kWh]", batteryCapacity3);
        evParameters3.put("Mean Charging Time [h]", meanServiceTime3);
        evParameters3.put("Max EV Charging Power [kW]", maxEVPower3);
        evParameters3.put("Demand Distribution Type", demandType3);
        evParameters3.put("Mean Charging Demand [fraction of battery]", meanChargingDemand3);

        String minText3 = "Click to show ev parameters 3", maxText3 = "EV Parameters for type 3";
        EVParametersPanel3.setVisible(false);
        EVParametersPanel3.addMouseListener(
                new MyMouseListener(EVParametersPanel3, evParameters3, minText3, minPanelSize, maxText3, maxPanelSize2));
        initializeMinPanel(EVParametersPanel3, minText3, minPanelSize);
    }

    private void addWindowCloseListener() {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to close all simulation results?",
                        "Exit all confirmation",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                } else {
                    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });
    }

    private JScrollPane createMainScrollPane() {
        JScrollPane jScrollPane = new JScrollPane(mainGuiPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setMaximumSize(new Dimension(430, 1000));
        return jScrollPane;
    }

    private JScrollPane createTextScrollPane() {
        JTextArea textArea = new JTextArea(10, 30); // 10 рядків, 30 символів в ширину
        textArea.setEditable(false); // Зробити текстове поле нередагованим
        JScrollPane scrollPane = new JScrollPane(textArea); // Додати прокрутку для текстового поля
        return scrollPane;
    }

    private void initializeMinPanel(JPanel panel, String minText, Dimension minSize) {
        panel.setPreferredSize(minSize);
        panel.add(createLabel(minText, 20, Color.BLACK));
        panel.setBorder(createEmptyBorder(10));
    }

    private static JComboBox<String> createComboBox(String[] items, String defaultItem) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setSelectedItem(defaultItem);
        comboBox.setUI(new CustomComboBoxUI());
        comboBox.setBackground(Color.WHITE);
        return comboBox;
    }

    private static JLabel createLabel(String text, int borderSize, Color color) {
        JLabel label = new JLabel(text);
        label.setBorder(createEmptyBorder(borderSize));
        label.setForeground(color);
        return label;
    }

    private static Border createEmptyBorder(int size) {
        return BorderFactory.createEmptyBorder(size, size, size, size);
    }

    private static GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        return gbc;
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
                    BorderFactory.createEmptyBorder(0, 3, 0, 0)));
        }
    }

    private class CloseWindowsActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            Object[] options = {"Close results of the last simulation", "Close whole results"};
            int choice = JOptionPane.showOptionDialog(null,
                    "Choose what to do:",
                    "Close windows",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (choice == JOptionPane.YES_OPTION) {
                ChargingSite.resetFrames();
                simulation.disposeQueueingCharacteristicsFrame();
                Monitor.disposeEnergyCharacteristicsFrame();
            } else if (choice == JOptionPane.NO_OPTION) {
                Monitor.closeAllWindows();
                simulation.closeAllWindows();
                ChargingSite.disposeFrames();
            }

            closeWindowsButton.setBackground(Colors.BLUE);
        }
    }

    private class RunSimulationActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            simulationRun = true;

            ChargingSite.initializePowerOverTimeChart(true);
            ChargingSite.initializeHistogram(true);

            runSimulationButton.setBackground(Colors.LIGHT_PINK);
            jScrollPane.getViewport().setViewPosition(new Point(0, 0));


            simulationParameters = getSimParametersFromGui();
            simulation = new Simulation(simulationParameters);

            int selectedConfidenceLevel = Integer.parseInt((String) confLevel.getSelectedItem());
            simulation.setConfLevel(selectedConfidenceLevel);

            saveResultsButton.setVisible(true);

            gifLabel.setVisible(true);
            Dimension initial = new Dimension(100, 100);
            gifLabel.setPreferredSize(initial);

            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    simulation.runSimulation(true);
                    return null;
                }

                @Override
                protected void done() {
                    gifLabel.setVisible(false);
                }
            };

            worker.execute();
        }

        private SimulationParameters getSimParametersFromGui() {
            SimulationParameters parameters = new SimulationParameters();
            int numberOfStepsValue = getSpinValAsInt(numberOfSteps);
            double maxArrivalRateValue = getSpinnerValueAsDouble(maxArrivalRate);

            parameters.setArrivalRateStep((maxArrivalRateValue / numberOfStepsValue));
            parameters.setMinimumArrivalRate(parameters.getArrivalRateStep());
            parameters.setMaxArrivalRate(maxArrivalRateValue);

            parameters.setSimulationSteps(numberOfStepsValue);
            parameters.setNumberOfCarTypes(getSpinValAsInt(numberOfClientTypes));
            parameters.setMaxEvents(getSpinValAsInt(maxEvents));
            parameters.setNumberOfServers(getSpinValAsInt(numberOfServers));
            parameters.setQueueSize(getSpinValAsInt(queueSize));

            double maxSitePowerValue = getSpinnerValueAsDouble(maxSitePower);
            double maxPointPowerValue = getSpinnerValueAsDouble(maxPointPower);
            double maxEVPowerValue = getSpinnerValueAsDouble(maxEVPower);
            double meanChargingDemandValue = getSpinnerValueAsDouble(meanChargingDemand);

            parameters.setMaxSitePower((int) maxSitePowerValue); // REDO
            parameters.setMaxPointPower((int) maxPointPowerValue);
            parameters.setMaxEvPower((int) maxEVPowerValue);
            parameters.setMeanChargingDemand(meanChargingDemandValue);

            parameters.setBatteryCapacity(getSpinnerValueAsDouble(batteryCapacity));

            double maxEVPowerValue2 = getSpinnerValueAsDouble(maxEVPower2);
            double meanChargingDemandValue2 = getSpinnerValueAsDouble(meanChargingDemand2);

            double maxEVPowerValue3 = getSpinnerValueAsDouble(maxEVPower3);
            double meanChargingDemandValue3 = getSpinnerValueAsDouble(meanChargingDemand3);

            parameters.setPercentageOfCars2(getSpinValAsInt(percentageOfCars2));
            parameters.setMaxEvPower2((int) maxEVPowerValue2);
            parameters.setMeanChargingDemand2(meanChargingDemandValue2);
            parameters.setBatteryCapacity2(getSpinnerValueAsDouble(batteryCapacity2));

            parameters.setPercentageOfCars3(getSpinValAsInt(percentageOfCars3));
            parameters.setMaxEvPower3((int) maxEVPowerValue3);
            parameters.setMeanChargingDemand3(meanChargingDemandValue3);
            parameters.setBatteryCapacity3(getSpinnerValueAsDouble(batteryCapacity3));

            parameters.setQueueingType(QueueingType.fromString(queueingType.getSelectedItem()));

            parameters.setMeanServiceTime(getSpinnerValueAsDouble(meanServiceTime));
            parameters.setMeanServiceTime2(getSpinnerValueAsDouble(meanServiceTime2));
            parameters.setMeanServiceTime3(getSpinnerValueAsDouble(meanServiceTime3));

            parameters.setArrivalType(DistributionType.fromString(arrivalType.getSelectedItem()));
            parameters.setServiceType(DistributionType.fromString(serviceType.getSelectedItem()));
            parameters.setDemandType(DistributionType.fromString(demandType.getSelectedItem()));
            parameters.setDEMAND_TYPE2(DistributionType.fromString(demandType2.getSelectedItem()));
            parameters.setDemandType3(DistributionType.fromString(demandType3.getSelectedItem()));

            return parameters;
        }

        private double getSpinnerValueAsDouble(JSpinner spinner) {
            return Double.parseDouble(spinner.getValue().toString());
        }

        private int getSpinValAsInt(JSpinner spinner) {
            return Integer.parseInt(spinner.getValue().toString());
        }
    }

    private class LoadResultsActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            loadResultsButton.setBackground(Colors.LIGHT_KREM);
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select XML file with simulation parameters");
            fileChooser.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("XML Files", "xml");
            fileChooser.addChoosableFileFilter(filter);

            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (isXmlFileValid(selectedFile)) {
                    try {
                        loadParametersFromXmltoGui(selectedFile);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame,
                                "Failed to load parameters or start the simulation: " + ex.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a valid XML file.", "Invalid File",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        public void loadParametersFromXmltoGui(File selectedFile) {
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(selectedFile);
                doc.getDocumentElement().normalize();

                NodeList generalParamsList = doc.getElementsByTagName("GeneralParameters");
                if (generalParamsList.getLength() > 0 && generalParamsList.item(0) != null) {
                    Element generalParams = (Element) generalParamsList.item(0);

                    numberOfSteps.setValue((int) Math.round(getDoubleFromElement(generalParams, "NumberOfSimulationSteps")));
                    maxEvents.setValue(getIntFromElement(generalParams, "MaxEventsPerStep"));
                    confLevel.setSelectedItem(getStringFromElement(generalParams, "ConfidenceIntervalLevel"));
                    numberOfClientTypes.setValue(getIntFromElement(generalParams, "NumberOfEVTypes"));
                }

                NodeList siteParamsList = doc.getElementsByTagName("SiteParameters");
                if (siteParamsList.getLength() > 0) {
                    Element siteParams = (Element) siteParamsList.item(0);

                    arrivalType.setSelectedItem(getStringFromElement(siteParams, "ArrivalDistributionType"));
                    maxArrivalRate.setValue(getDoubleFromElement(siteParams, "MaxMeanArrivalRate"));
                    queueSize.setValue(getIntFromElement(siteParams, "ParkingSpace"));
                    maxSitePower.setValue(getIntFromElement(siteParams, "MaxSitePower"));
                    queueingType.setSelectedItem(getStringFromElement(siteParams, "QueueingType"));
                }

                NodeList chargingParamsList = doc.getElementsByTagName("ChargingParameters");
                if (chargingParamsList.getLength() > 0) {
                    Element chargingParams = (Element) chargingParamsList.item(0);

                    numberOfServers.setValue(getIntFromElement(chargingParams, "NumberOfChargingPoints"));
                    serviceType.setSelectedItem(getStringFromElement(chargingParams, "ServiceDistributionType"));
                    maxPointPower.setValue(getIntFromElement(chargingParams, "MaxPowerOfChargingPoint"));
                }

                NodeList EVParamsList = doc.getElementsByTagName("EVParameters");
                if (EVParamsList.getLength() > 0) {
                    Element EVParams = (Element) EVParamsList.item(0);

                    batteryCapacity.setValue(getDoubleFromElement(EVParams, "BatteryCapacity"));
                    meanServiceTime.setValue(getDoubleFromElement(EVParams, "MeanChargingTime"));
                    maxEVPower.setValue(getDoubleFromElement(EVParams, "MaxEVChargingPower"));
                    demandType.setSelectedItem(getStringFromElement(EVParams, "DemandDistributionType"));
                    meanChargingDemand.setValue(getDoubleFromElement(EVParams, "MeanChargingDemand"));
                }

                NodeList EVParamsList2 = doc.getElementsByTagName("EVParameters2");
                if (EVParamsList2.getLength() > 0) {
                    Element EVParams2 = (Element) EVParamsList2.item(0);

                    percentageOfCars2.setValue(
                            (int) Math.round(getDoubleFromElement(EVParams2, "PercentageOfTheSecondCar") * 100));
                    batteryCapacity2.setValue(getDoubleFromElement(EVParams2, "BatteryCapacity2"));
                    meanServiceTime2.setValue(getDoubleFromElement(EVParams2, "MeanChargingTime2"));
                    maxEVPower2.setValue(getDoubleFromElement(EVParams2, "MaxEVChargingPower2"));
                    demandType2.setSelectedItem(getStringFromElement(EVParams2, "DemandDistributionType2"));
                    meanChargingDemand2.setValue(getDoubleFromElement(EVParams2, "MeanChargingDemand2"));
                }

                NodeList EVParamsList3 = doc.getElementsByTagName("EVParameters3");
                if (EVParamsList3.getLength() > 0) {
                    Element EVParams3 = (Element) EVParamsList3.item(0);

                    percentageOfCars3.setValue(
                            (int) Math.round(getDoubleFromElement(EVParams3, "PercentageOfTheThirdCar") * 100));
                    batteryCapacity3.setValue(getDoubleFromElement(EVParams3, "BatteryCapacity3"));
                    meanServiceTime3.setValue(getDoubleFromElement(EVParams3, "MeanChargingTime3"));
                    maxEVPower3.setValue(getDoubleFromElement(EVParams3, "MaxEVChargingPower3"));
                    demandType3.setSelectedItem(getStringFromElement(EVParams3, "DemandDistributionType3"));
                    meanChargingDemand3.setValue(getDoubleFromElement(EVParams3, "MeanChargingDemand3"));
                }

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error loading parameters from file: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        private String getStringFromElement(Element element, String tagName) {
            return element.getElementsByTagName(tagName).item(0).getTextContent();
        }

        private int getIntFromElement(Element element, String tagName) {
            return Integer.parseInt(getStringFromElement(element, tagName));
        }

        private double getDoubleFromElement(Element element, String tagName) {
            return Double.parseDouble(getStringFromElement(element, tagName));
        }

        private boolean isXmlFileValid(File selectedFile) {
            return selectedFile.isFile() && selectedFile.getName().toLowerCase().endsWith(".xml");
        }
    }

    public interface SaveFunction {
        void save(String absoluteFilePath);
    }

    private class SaveResultsActionListener implements ActionListener {

        private int DEFAULT_SVG_WIDTH = 800;
        private int DEFAULT_SVG_HEIGTH = 520;

        private int svgWidth = 1200, svgHeight = 730;
        private Map<String, Map<String, SaveFunction>> allSaveOptions = Map.of(
                "ChargeSiteQueueingCharacteristics", Map.of(
                        "csv", (path) -> simulation.saveQueueingCharacteristicsAsCSV(path),
                        "svg", (path) -> simulation.saveQueueingCharacteristicsAsSVG(svgWidth, svgHeight, new File(path)),
                        "png", (path) -> simulation.saveQueueingCharacteristicsAsPNG(path)),

                "ChargingSiteEnergyCharacteristics", Map.of(
                        "csv", (path) -> simulation.chargingMonitor.saveEnergyCharacteristicsGraphAsCSV(path),
                        "svg", (path) -> simulation.chargingMonitor.saveEnergyCharacteristicsGraphAsSVG(
                                svgWidth, svgHeight, new File(path)),
                        "png", (path) -> simulation.chargingMonitor.saveEnergyCharacteristicsGraphAsPNG(path)),

                "PowerOverTimeChart", Map.of(
                        "csv", (path) -> simulation.site.savePowerOverTimeGraphAsCSV(path),
                        "png", (path) -> simulation.site.savePowerOverTimeGraphAsPNG(path)),

                "SitePowerDistributionHistogram", Map.of(
                        "csv", (path) -> simulation.site.saveHistogramAsCSV(path),
                        "svg", (path) -> simulation.site.saveHistogramAsSVG(path),
                        "png", (path) -> simulation.site.saveHistogramAsPNG(path)),

                "SitePowerDistribution3DHistogram", Map.of(
                        "csv", (path) -> simulation.site.saveHistogram3DAsCSV(path),
                        "svg", (path) -> simulation.site.saveHistogram3DAsSVG(path),
                        "png", (path) -> simulation.site.saveHistogram3DAsPNG(path)),

                "SimulationParameters", Map.of(
                        "txt", (path) -> simulationParameters.saveParametersAsTxt(path),
                        "xml", (path) -> simulationParameters.saveParametersAsXml(path)));

        @Override
        public void actionPerformed(ActionEvent e) {
            saveResultsButton.setBackground(Colors.LIGHT_YELLOW1);
            if (!simulationRun) {
                saveResultsButton.setBackground(Colors.LIGHT_YELLOW1);
                JOptionPane.showMessageDialog(frame,
                        "In order to save the results, you must first run the simulation.",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                saveResultsButton.setBackground(Colors.LIGHT_GREEN);

                String[] options = {
                        "Charging Site Queueing Characteristics",
                        "Charging Site Energy Characteristics",
                        "Power vs Time Chart",
                        "Site Power Distribution Histogram",
                        "GUI Parameters",
                        "All Results",
                        "Site Power Distribution 3D Histogram"
                };

                JList<String> optionList = new JList<>(options);
                optionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                optionList.setLayoutOrientation(JList.VERTICAL);
                optionList.setVisibleRowCount(options.length);

                JOptionPane.showMessageDialog(frame, new JScrollPane(optionList), "Save Parameters",
                        JOptionPane.PLAIN_MESSAGE);

                int selectedIndex = optionList.getSelectedIndex();
                switch (selectedIndex) {
                    case 0 -> saveChargingSiteQueueData();
                    case 1 -> saveChargingSiteEnergyData();
                    case 2 -> savePowerAndTimeChart();
                    case 3 -> saveSitePowerDistributionHistogram();
                    case 4 -> saveSimulationParameters();
                    case 5 -> saveAllData();
                    case 6 -> saveSitePowerDistribution3DHistogram();
                    default -> JOptionPane.showMessageDialog(
                            frame,
                            "Please select an option to proceed with saving.",
                            "Selection Required",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        }

        private void saveSitePowerDistribution3DHistogram() {
            String message = "Choose the format to save the 3d histogram:";
            String fileName = "SitePowerDistribution3DHistogram";
            Map<String, SaveFunction> formatFunctions = allSaveOptions.get(fileName);

            showSaveDialogAndExecuteFunction(message, fileName, formatFunctions);
        }

        private void saveSimulationParameters() {
            String message = "Choose the format to save the result:";
            String fileName = "SimulationParameters";
            Map<String, SaveFunction> formatFunctions = allSaveOptions.get(fileName);

            showSaveDialogAndExecuteFunction(message, fileName, formatFunctions);
        }

        private void saveSitePowerDistributionHistogram() {
            String message = "Choose the format to save the graph:";
            String fileName = "SitePowerDistributionHistogram";
            Map<String, SaveFunction> formatFunctions = allSaveOptions.get(fileName);

            showSaveDialogAndExecuteFunction(message, fileName, formatFunctions);
        }

        private void savePowerAndTimeChart() {
            String message = "Choose the format to save the graph:";
            String fileName = "PowerOverTimeChart";
            Map<String, SaveFunction> formatFunctions = allSaveOptions.get(fileName);

            showSaveDialogAndExecuteFunction(message, fileName, formatFunctions);
        }

        private void saveChargingSiteEnergyData() {
            String message = "Choose the format to save the graph:";
            String fileName = "ChargingSiteEnergyCharacteristics";
            Map<String, SaveFunction> formatFunctions = Map.of(
                    "csv",
                    (filePath) -> simulation.chargingMonitor.saveEnergyCharacteristicsGraphAsCSV(filePath),
                    "svg",
                    (filePath) -> simulation.chargingMonitor.saveEnergyCharacteristicsGraphAsSVG(DEFAULT_SVG_WIDTH,
                            DEFAULT_SVG_HEIGTH, new File(filePath)),
                    "png", (filePath) -> simulation.chargingMonitor
                            .saveEnergyCharacteristicsGraphAsPNG(filePath));

            showSaveDialogAndExecuteFunction(message, fileName, formatFunctions);
        }

        private void saveChargingSiteQueueData() {
            String message = "Choose the format to save the graph:";
            String fileName = "ChargingSiteQueueingCharacteristics";
            Map<String, SaveFunction> formatFunctions = Map.of(
                    "csv", (filePath) -> simulation.saveQueueingCharacteristicsAsCSV(filePath),
                    "svg", (filePath) -> simulation.saveQueueingCharacteristicsAsSVG(DEFAULT_SVG_WIDTH,
                            DEFAULT_SVG_HEIGTH, new File(filePath)),
                    "png", (filePath) -> simulation.saveQueueingCharacteristicsAsPNG(filePath));

            showSaveDialogAndExecuteFunction(message, fileName, formatFunctions);
        }

        private void showSaveDialogAndExecuteFunction(String message, String fileName,
                                                      Map<String, SaveFunction> formatFunctions) {

            String selectedFormat = getSelectedFormat(message, formatFunctions.keySet().toArray(new String[0]));
            if (selectedFormat != null) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Specify a file to save");
                fileChooser.setSelectedFile(new File(fileName + "." + selectedFormat));

                int userSelection = fileChooser.showSaveDialog(frame);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    String absoluteFilePath = fileChooser.getSelectedFile().getAbsolutePath();

                    formatFunctions.get(selectedFormat).save(absoluteFilePath);
                }
            }
        }

        public void saveAllData(String zipFilePath) {
            File zipFileToSave = new File(zipFilePath + getZipFileName());
            saveDataToZip(zipFileToSave);
        }

        public void saveAllData() {

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Specify a file to save all results in a ZIP archive");
            fileChooser.setSelectedFile(new File(getZipFileName()));

            int userSelection = fileChooser.showSaveDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File zipFileToSave = fileChooser.getSelectedFile();
                saveDataToZip(zipFileToSave);
            }
        }

        private void saveDataToZip(File zipFileToSave) {
            try (FileOutputStream fos = new FileOutputStream(zipFileToSave);
                 ZipOutputStream zipOut = new ZipOutputStream(fos)) {

                for (String saveOption : allSaveOptions.keySet()) {
                    Map<String, SaveFunction> saveOptions = allSaveOptions.get(saveOption);

                    for (String format : saveOptions.keySet()) {
                        String fileName = saveOption + "." + format;
                        File fileToSave = new File(System.getProperty("java.io.tmpdir"), fileName);
                        String absoluteFilePath = fileToSave.getAbsolutePath();

                        saveOptions.get(format).save(absoluteFilePath);
                        writeFileToZip(zipOut, fileToSave);
                        fileToSave.delete();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        private void writeFileToZip(ZipOutputStream zipOut, File fileToSave) throws IOException, FileNotFoundException {
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
        }

        private String getZipFileName() {
            var dateFormat = new SimpleDateFormat("ddMM_HHmm");
            String kendallName = simulationParameters.getKendallNameForFile();
            return kendallName + "_" + dateFormat.format(new Date()) + ".zip";
        }

        private String getSelectedFormat(String message, String[] formats) {
            return (String) JOptionPane.showInputDialog(frame, message, "Save Format", JOptionPane.QUESTION_MESSAGE,
                    null, formats, formats[0]);
        }
    }

    private class MyMouseListener implements MouseListener {

        private boolean isPanelMaximazed = true;
        private JPanel panel;
        private Map<String, JComponent> components;
        private String minText, maxText;
        private Dimension minSize, maxSize;
        private GridBagConstraints gbc;

        public MyMouseListener(JPanel panel, Map<String, JComponent> components, String minText, Dimension minSize,
                               String maxText, Dimension maxSize) {
            this.panel = panel;
            this.components = components;
            this.minText = minText;
            this.minSize = minSize;

            this.maxText = maxText;
            this.maxSize = maxSize;

            gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(5, 5, 5, 5);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            panel.removeAll();

            if (isPanelMaximazed) {
                for (Map.Entry<String, JComponent> entry : components.entrySet()) {
                    addRowToPanel(panel, gbc, entry.getKey(), entry.getValue());
                }
                TitledBorder border = BorderFactory.createTitledBorder(maxText);
                border.setTitleColor(Color.BLUE);
                panel.setBorder(BorderFactory.createCompoundBorder(createEmptyBorder(10), border));
                panel.setPreferredSize(maxSize);

                isPanelMaximazed = false;
            } else {
                initializeMinPanel(panel, minText, minSize);
                isPanelMaximazed = true;
            }

            panel.revalidate();
            panel.repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }

    private class NumberOfClientsChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            int numberOfClientTypesValue = (int) numberOfClientTypes.getValue();
            EVParametersPanel2.setVisible(numberOfClientTypesValue >= 2);
            EVParametersPanel3.setVisible(numberOfClientTypesValue >= 3);

            mainGuiPanel.revalidate();
            mainGuiPanel.repaint();
        }
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
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                              boolean isSelected, boolean cellHasFocus) {
                    Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                    if (isSelected) {
                        comp.setBackground(Colors.BLUE);
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

        private static String htmlOpen = "<html>", htmlClose = "</html>";

        public AutoResizeButton(String text) {
            super(htmlOpen + text + htmlClose);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            fitTextToButton();
        }

        private void fitTextToButton() {
            FontMetrics fm = getFontMetrics(getFont());

            String text = getText().replaceAll(htmlOpen, "").replaceAll(htmlClose, "");
            int stringWidth = fm.stringWidth(text);
            int componentWidth = getWidth();

            if (stringWidth > componentWidth) {
                StringBuilder newText = new StringBuilder(htmlOpen);
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
                newText.append(currentLine).append(htmlClose);
                setText(newText.toString());
            }
        }
    }

    public void runSimulationConsole(String parametersPath, String saveResultPath) {

        ChargingSite.createPowerOverTimeChart(false);
        ChargingSite.createHistogramFrame(false);

        simulationParameters = new SimulationParameters();

        String defaultPath = "SimulationParameters.xml";
        simulationParameters.loadParametersFromXml(parametersPath.isEmpty() ? defaultPath : parametersPath);

        simulation = new Simulation(simulationParameters);
        simulation.setConfLevel(95);//?
        simulation.runSimulation(false);

        new SaveResultsActionListener().saveAllData(saveResultPath);

        System.out.println("Simulation finished");

        Monitor.closeAllWindows();
        Simulation.closeAllWindows();
        ChargingSite.disposeFrames();
    }
}